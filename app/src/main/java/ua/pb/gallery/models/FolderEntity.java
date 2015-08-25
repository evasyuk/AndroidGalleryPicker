package ua.pb.gallery.models;

import java.io.File;
import java.util.Comparator;

/**
 * Created by user on 15.08.15.
 */
public class FolderEntity implements Comparator<FolderEntity>{
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

    @Override
    public int compare(FolderEntity lhs, FolderEntity rhs) {
        return lhs.getFolder().getName().compareTo(rhs.getFolder().getName());
    }
}
