package ua.pb.gallery.models;

import java.io.File;

/**
 * Created by user on 16.08.15.
 */
public class PhotoEntity {
    private String fileFullPath;
    private File photoFile;

    public PhotoEntity(String fileFullPath, File photoFile) {
        this.fileFullPath = fileFullPath;
        this.photoFile = photoFile;
    }

    public String getPhotoFileFullPath() {
        return fileFullPath;
    }

    public File getPhotoFile() {
        return photoFile;
    }
}
