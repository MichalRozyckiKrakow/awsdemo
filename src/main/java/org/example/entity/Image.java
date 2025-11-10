package org.example.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "image_size")
    private Long size;

    @Column(name = "file_extension", length = 40)
    private String fileExtension;

    @Column(name = "last_update",
        columnDefinition = "TIMESTAMP",
        insertable = false,
        updatable = false)
    private LocalDateTime lastUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        return Objects.equals(id, image.id) && Objects.equals(name, image.name)
            && Objects.equals(size, image.size) && Objects.equals(fileExtension, image.fileExtension)
            && Objects.equals(lastUpdate, image.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, size, fileExtension, lastUpdate);
    }

    @Override
    public String toString() {
        return "Image{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", size=" + size +
            ", fileExtension='" + fileExtension + '\'' +
            ", lastUpdate=" + lastUpdate +
            '}';
    }
}