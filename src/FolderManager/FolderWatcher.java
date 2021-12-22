package FolderManager;

import FT_Rapid.Buffer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FolderWatcher implements Runnable{
    Path path;
    Buffer sender;

    public FolderWatcher(Path path,Buffer buffer) {
        this.path = path;
        sender = buffer;
    }

    public void run() {

         try (WatchService service = FileSystems.getDefault().newWatchService()) {
             Map<WatchKey, Path> keyMap = new HashMap<>();
             Path path = this.path;
             keyMap.put(path.register(service,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY),path);

             WatchKey watchKey;

             do {
                watchKey = service.take();
                Path eventDir = keyMap.get(watchKey);

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();
                    String evPath = eventPath.toString();
                    if (!evPath.equals("folder.log")) {
                        try (PrintWriter writer = new PrintWriter(new FileWriter("folder.log", true))) {
                            writer.print(localDateTime + " : " + eventDir + ": " + kind + ": " + eventPath + '\n');

                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }

             } while (watchKey.reset());

         } catch (Exception e) {
             e.printStackTrace();
         }
    }

}
