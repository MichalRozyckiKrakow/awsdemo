package org.example.dto;

import lombok.Data;

@Data
public class ImageDto {
    private ImageMetadataDto metadata;
    private byte[] file;
}
