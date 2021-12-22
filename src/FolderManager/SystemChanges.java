package FolderManager;

import FT_Rapid.Buffer;

import java.nio.file.Path;

public class SystemChanges {
    private Path folder;
    private Buffer receiver;

    public SystemChanges(Path path,Buffer buffer) {
         folder = path;
         receiver = buffer;
    }



}
