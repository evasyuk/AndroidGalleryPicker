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
