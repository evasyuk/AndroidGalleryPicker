package ua.pb.gallery.models;

import java.io.File;
import java.util.Comparator;

/**
 * Created by user on 29.08.15.
 */
public class FileItemModel implements Comparator<FileItemModel> {

    String fileItemPath;
    File file;

    public FileItemModel(String fileItemPath, File file) {
        this.fileItemPath = fileItemPath;
        this.file = file;
    }

    public String getFileFullPath() {
        return fileItemPath;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int compare(FileItemModel lhs, FileItemModel rhs) {
        return lhs.getFile().getName().compareTo(rhs.getFile().getName());
    }
}
