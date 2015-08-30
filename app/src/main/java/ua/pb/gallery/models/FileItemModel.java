package ua.pb.gallery.models;

/**

 Copyright 2015 evasyuk  < vasyuk.eugene@gmail.com >

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

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
