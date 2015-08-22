package ua.pb.gallery.models;

import java.io.File;

/**
 * Created by user on 15.08.15.
 */
public class FolderEntity {
    private String folderFullPath;
    private File folder;

    public FolderEntity(String folderFullPath, File folder) {
        this.folderFullPath = folderFullPath;
        this.folder = folder;
    }

    public String getFolderFullPath() {
        return folderFullPath;
    }

    public File getFolder() {
        return folder;
    }
}
