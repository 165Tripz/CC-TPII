package Manager;

import FT_Rapid.UDPSender;
import FolderManager.FolderWatcher;
import FT_Rapid.Buffer;
import FT_Rapid.UDPReceiver;
import FolderManager.SystemChanges;

import java.io.File;
import java.net.*;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Manager {

    private boolean inSync = false;
    private boolean connected = false;

    String path;
    InetAddress ip;

    public Manager(String[] args) throws UnknownHostException {
        path = args[0];
        ip = InetAddress.getByName(args[1]);

    }

    public void start() throws SocketException {
        Path p = Paths.get(path);


        try {
            File file = new File(path);
            char a = path.charAt(0);

            if (a == '/' || a == '~' || a == '.')
                throw new InvalidPathException(path, "Invalid Directory");

            if (!file.exists() || !file.isDirectory()) {
                throw new NotDirectoryException("Not a diretory.");
            }

            System.out.println("Destino da pasta : " + p.toAbsolutePath());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        DatagramSocket socket = new DatagramSocket(80);

        Buffer bufferSender = new Buffer();
        Buffer bufferReceiver = new Buffer();

        UDPSender udpSender = new UDPSender(socket,bufferSender);
        UDPReceiver UDPReceiver = new UDPReceiver(bufferReceiver,socket);
        FolderWatcher watcher = new FolderWatcher(p,bufferSender,ip);
        SystemChanges rule = new SystemChanges(p,bufferReceiver,watcher);

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

}
