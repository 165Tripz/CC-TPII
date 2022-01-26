package FolderManager;

import FT_Rapid.Buffer;
import Manager.FileConverter;
import Manager.Package;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class SystemChanges implements Runnable {
    private Path folder;
    private Buffer receiver;
    private Buffer sender;
    private FolderWatcher v;
    private InetAddress ip;
    ReentrantLock lock = new ReentrantLock();
    HashMap<String, Queue<Package>> files;

    public SystemChanges(Path path,Buffer buffer,FolderWatcher r,Buffer sender,HashMap<String, Queue<Package>> files,InetAddress ip) {
         folder = path;
         receiver = buffer;
         this.sender = sender;
         this.ip = ip;
         this.files = files;
         v = r;
    }

    @Override
    public void run() {
        try {
            DatagramPacket p = receiver.take();

            Package a = new Package(p.getData());
            if (a.getType() == 2) {
                var b = new String(a.getData(), StandardCharsets.UTF_16);
                var b1 = b.split("\n");

                for (String b2 : b1) {
                    if(b2.length() == 0) break;
                    var b3 = b2.split("&");

                    File f1 = new File(b3[1]);
                    if (f1.exists()) {
                        if (f1.lastModified() > Long.parseLong(b3[0])) {
                            var b4 = new FileConverter(f1).bytify();

                            var sample = b4.size();
                            while (b4.size() > 0) {
                                var ar = new Package();
                                ar.setMany(sample);
                                ar.setData(b4.poll());
                                ar.setMessage(f1.getName());
                                ar.setFinal(b4.size() == 0);
                                ar.setType(9);

                                byte[] r = ar.toBytes();
                                sender.add(new DatagramPacket(r,r.length,ip,80));

                            }

                        }
                    } else {
                        var pack = new Package();
                        pack.setType(3);
                        pack.setMany(1);
                        pack.setFinal(true);
                        pack.setMessage(b1[1]);

                        var r = pack.toBytes();

                        sender.add(new DatagramPacket(r,r.length,ip,80));
                    }

                }

            } else if (a.getType() == 3) {
                File f1 = new File(a.getMessage());
                if (f1.exists()) {

                    var b4 = new FileConverter(f1).bytify();
                    var sample = b4.size();
                    while (b4.size() > 0) {
                        var ar = new Package();
                        ar.setMany(sample);
                        ar.setData(b4.poll());
                        ar.setMessage(f1.getName());
                        ar.setFinal(b4.size() == 0);
                        ar.setType(9);

                        byte[] r = ar.toBytes();
                        sender.add(new DatagramPacket(r,r.length,ip,80));

                    }
                }
            }
            else if (a.getType() == 5) {
                File file = new File(a.getMessage());
                v.setFlag(false);
                if (file.delete()) {
                    v.setFlag(true);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    PrintWriter writer = new PrintWriter(new FileWriter("folder.log", true));
                    writer.print(localDateTime + " : " + folder.getFileName() + ": " + "Deleted" + ": " + file.getName() + '\n');
                }
            } else if (a.getType() == 9) {
                lock.lock();
                if (!files.containsKey(a.getMessage())) {
                    files.put(a.getMessage(),new PriorityQueue<>(a.getMany(), (o1, o2) -> {
                        if (o1.getMemID() > o2.getMemID())
                            return 1;
                        else return 0;
                    }));
                }

                files.get(a.getMessage()).add(a);

                Queue<Package> aa = files.get(a.getMessage());
                lock.unlock();

                if (aa.size() == a.getMany()) {
                    Queue<String> tt = new ArrayDeque<>();

                    while (!aa.isEmpty()) {
                        tt.add(new String(aa.poll().getData(),StandardCharsets.UTF_16));
                    }

                    var ttt = new FileConverter(new File(a.getMessage()));
                    ttt.setStrings(tt);

                    var t = new Thread(ttt);
                    t.start();

                    v.setFlag(true);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    PrintWriter writer = new PrintWriter(new FileWriter("folder.log", true));
                    writer.print(localDateTime + " : " + folder.getFileName() + ": " + "Created" + ": " + a.getMessage() + '\n');

                }


            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
