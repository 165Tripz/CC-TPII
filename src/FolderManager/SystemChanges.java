package FolderManager;

import FT_Rapid.Buffer;
import Manager.FileConverter;
import Manager.Package;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class SystemChanges implements Runnable {
    private Path folder;
    private Buffer receiver;
    private FolderWatcher v;

    public SystemChanges(Path path,Buffer buffer,FolderWatcher r) {
         folder = path;
         receiver = buffer;
         v = r;
    }

    @Override
    public void run() {
        try {
            DatagramPacket p = receiver.take();

            Package a = new Package(p.getData());
            if (a.getType() == 5) {
                File file = new File(a.getMessage());
                v.setFlag(false);
                if (file.delete()) {
                    v.setFlag(true);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    PrintWriter writer = new PrintWriter(new FileWriter("folder.log", true));
                    writer.print(localDateTime + " : " + folder.getFileName() + ": " + "Deleted" + ": " + file.getName() + '\n');
                }
            } else if (a.getType() == 9 && a.isFinal()) {
                File file = new File(a.getMessage());
                if (a.getType() == 9) {
                }
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
