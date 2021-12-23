package TCP;

import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class TCP implements Runnable {

    private  boolean i;
    private boolean c;
    private Queue<File> files;

    public TCP(boolean inSync, boolean connected, Queue<File> files) {
        i = inSync;
        c = connected;
        this.files = files;
    }

    @Override
    public void run() {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(80)) {
                try (Socket socket = serverSocket.accept()) {
                    OutputStream s = socket.getOutputStream();

                    //s.

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
