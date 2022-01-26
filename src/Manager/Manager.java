package Manager;

import FT_Rapid.UDPSender;
import FolderManager.FolderWatcher;
import FT_Rapid.Buffer;
import FT_Rapid.UDPReceiver;
import FolderManager.SystemChanges;

import java.io.File;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Manager {

    private boolean inSync = false;
    private boolean connected = false;

    String path;
    InetAddress ip;

    public void start() throws SocketException {

        Scanner in = new Scanner(System.in);
        Path p;

        while (true) {
            System.out.print("Enter directory:");
            this.path = in.nextLine();
            p = Paths.get(path);

            try {
                File file = new File(path);
                char a = path.charAt(0);

                if (a == '/' || a == '~' || a == '.')
                    throw new InvalidPathException(path, "Invalid Directory");

                if (!file.exists() || !file.isDirectory()) {
                    throw new NotDirectoryException("Not a diretory.");
                }

                System.out.println("Destino da pasta : " + p.toAbsolutePath());

                System.out.print("Enter destination:");
                this.ip = InetAddress.getByName(in.nextLine());

                throw new InterruptedException();

            } catch (InterruptedException ignored) {
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        DatagramSocket socket = new DatagramSocket(80);

        Buffer bufferSender = new Buffer();
        Buffer bufferReceiver = new Buffer();
        Set<Integer> response = new HashSet<>();
        HashMap<Integer,Package> resend = new HashMap<Integer, Package>();
        HashMap<String, Queue<Package>> files = new HashMap<>();

        UDPSender udpSender = new UDPSender(socket,bufferSender);
        UDPReceiver UDPReceiver = new UDPReceiver(bufferReceiver,socket,bufferSender,response,resend);
        FolderWatcher watcher = new FolderWatcher(p,bufferSender,bufferReceiver,ip);
        SystemChanges rule = new SystemChanges(p,bufferReceiver,watcher,bufferSender,files,ip);

        Queue<File> list = watcher.takeFiles(p);
        bufferSender.add(this.sendInitial(list,ip));

        Thread t1 = new Thread(udpSender);
        Thread t2 = new Thread(UDPReceiver);
        Thread t3 = new Thread(watcher);
        Thread t4 = new Thread(rule);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public DatagramPacket sendInitial (Queue<File> files,InetAddress ip) {
        StringBuilder text = new StringBuilder();
        for (File file : files)
            text.append(file.lastModified()).append("&").append(file).append("\n");

        Package a = new Package();
        a.setType(2);
        a.setMessage("Beggining");
        a.setData(text.toString().getBytes(StandardCharsets.UTF_16));

        byte[] s = a.toBytes();

        DatagramPacket packet = new DatagramPacket(s,s.length, ip, 80);

        return packet;
    }

}
