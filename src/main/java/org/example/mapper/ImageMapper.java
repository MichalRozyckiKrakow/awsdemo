package org.example.mapper;

import org.example.S3Service;
import org.example.dto.ImageDto;
import org.example.dto.ImageMetadataDto;
import org.example.dto.ImageUploadDto;
import org.example.entity.Image;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "file", ignore = true)
    ImageDto toDto(Image entityModel, @Context S3Service s3Service);

    ImageMetadataDto toMetadataDto(Image entityModel);

    Image toEntity(ImageUploadDto clientModel);

    @AfterMapping
    default void setMetadata(@MappingTarget ImageDto target, Image source) {
        target.setMetadata(toMetadataDto(source));
    }

    @AfterMapping
    default void setBitmapToClient(@MappingTarget ImageDto target, Image source, @Context S3Service s3Service) {
        target.setFile(s3Service.downloadObject(source.getName()));
    }
}
