package org.example;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.io.InputStream;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.dto.ImageMetadataDto;
import org.example.dto.ImageUploadDto;
import org.example.entity.Image;
import org.example.exception.NotFoundException;
import org.example.mapper.ImageMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import static org.apache.commons.lang3.StringUtils.joinWith;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String IMAGE_UPLOAD_ACTION = "Image was uploaded";

    private final ImageRepository imageJpaRepository;
    private final ImageMapper imageMapper;
    private final S3Service s3Service;
    private final FileService fileService;
    private final NotificationService notificationService;

    public ResponseEntity<?> findImageMetadataByName(String name) {
        Image image = imageJpaRepository.findByName(name).stream()
            .findFirst()
            .orElseThrow(NotFoundException::new);
        return new ResponseEntity<>(imageMapper.toMetadataDto(image), OK);
    }

    public ResponseEntity<?> findRandomImageMetadata() {
        Image image = imageJpaRepository.findRandom().stream()
            .findFirst()
            .orElseThrow(NotFoundException::new);
        return new ResponseEntity<>(imageMapper.toMetadataDto(image), OK);
    }

    @Transactional
    public ResponseEntity<?> findImage(String name) {
        Image image = imageJpaRepository.findByName(name).stream()
            .findFirst()
            .orElseThrow(NotFoundException::new);
        return new ResponseEntity<>(s3Service.downloadObject(image.getName()), OK);
    }

    @Transactional
    public ResponseEntity<?> upload(ImageUploadDto downloadClientModel) {
        MultipartFile file = downloadClientModel.getFile();
        InputStream inputStream = fileService.getInputStream(file);

        s3Service.uploadObject(inputStream, file.getOriginalFilename(), downloadClientModel.getName());

        Image image = imageMapper.toEntity(downloadClientModel);
        image.setSize(file.getSize());
        image.setFileExtension(fileService.getFileExtension(file.getOriginalFilename()));

        Image save = imageJpaRepository.save(image);
        ImageMetadataDto imageMetadataDto = imageMapper.toMetadataDto(save);

        notificationService.sendMessageToQueue(createMessage(imageMetadataDto));

        return new ResponseEntity<>(imageMetadataDto, CREATED);
    }

    private String createMessage(ImageMetadataDto imageMetadataDto) {
        var downloadLink = linkTo(methodOn(CloudInfoController.class).getImage(imageMetadataDto.getName()));
        return joinWith(":::", IMAGE_UPLOAD_ACTION, imageMetadataDto.toString(),
            downloadLink);
    }

    @Transactional
    public ResponseEntity<?> delete(String name) {
        List<Image> imageList = imageJpaRepository.findByName(name);
        imageList.stream()
            .map(Image::getName)
            .forEach(s3Service::deleteObject);
        imageList.forEach(imageJpaRepository::delete);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
