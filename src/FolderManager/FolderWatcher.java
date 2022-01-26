package FolderManager;

import Manager.FileConverter;
import Manager.Package;
import FT_Rapid.Buffer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class FolderWatcher implements Runnable{
    Path path;
    Buffer sender;
    Buffer receiver;
    InetAddress ip;
    Boolean flag = false;

    public FolderWatcher(Path path,Buffer buffer, Buffer receiver,InetAddress ip) {
        this.path = path;
        sender = buffer;
        this.ip = ip;
        this.receiver = receiver;

        DatagramPacket packet = null;
        try {
            packet = receiver.take();
            Package a = new Package(packet.getData());
            if (a.getType() == 2);
                //this.compareFiles(new String(a.getData(), StandardCharsets.UTF_8), this.takeFiles(path), sender);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
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
                    if (!evPath.equals("folder.log") && flag) {
                        try (PrintWriter writer = new PrintWriter(new FileWriter("folder.log", true))) {
                            writer.print(localDateTime + " : " + eventDir + ": " + kind + ": " + eventPath + '\n');

                            if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                Package a = new Package();

                                a.setType(5);
                                a.setMessage(String.valueOf(eventPath));
                                a.setData(new byte[8096]);

                                byte[] pa = a.toBytes();

                                DatagramPacket packet = new DatagramPacket(pa, pa.length, ip, 80);

                                sender.add(packet);
                            }
                            else {
                                File file = new File(String.valueOf(eventPath));
                                FileConverter r = new FileConverter(file);
                                Queue<byte[]> c = r.bytify();
                                int amount = c.size();

                                for (int i = 0; !c.isEmpty() ; i++) {
                                    Package a = new Package();

                                    a.setMany(amount);
                                    a.setData(c.poll());
                                    a.setFinal(c.size() == 0);
                                    a.setType(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)?9:10);

                                    a.setMessage(file.getName());

                                    byte[] pa = a.toBytes();

                                    DatagramPacket packet = new DatagramPacket(pa, pa.length, ip, 80);

                                    sender.add(packet);
                                }
                            }
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

    public Queue<File> takeFiles(Path path) {
        Queue<File> s = new ArrayDeque<>();

        File[] filelist = new File(String.valueOf(path)).listFiles();

        assert filelist != null;
        for (File file : filelist) {
            if (file.isDirectory()) {
                s.addAll(this.takeFiles(file.toPath()));
            } else {
                s.add(file);
            }
        }

        return s;
    }

}
