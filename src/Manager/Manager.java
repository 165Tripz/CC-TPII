package Manager;

import FT_Rapid.UDPSender;
import FolderManager.FolderWatcher;
import FT_Rapid.Buffer;
import FT_Rapid.UDPReceiver;
import FolderManager.SystemChanges;

import java.io.File;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Manager {

    private boolean inSync = false;
    private boolean connected = false;

    String path;
    String ip;

    public Manager(String[] args) {
        path = args[0];
        ip = args[1];
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
        FolderWatcher watcher = new FolderWatcher(p,bufferSender);
        SystemChanges rule = new SystemChanges(p,bufferReceiver);


    }

}
