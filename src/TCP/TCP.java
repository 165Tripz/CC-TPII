package TCP;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            try (ServerSocket serverSocket = new ServerSocket(80)) {
                while (true) {
                    try (Socket socket = serverSocket.accept()) {
                        DataOutputStream s = (DataOutputStream) socket.getOutputStream();

                    StringBuilder send = new StringBuilder();
                    send.append("Is connected: ").append(c).append("\n");
                    send.append("Is in Sync: ").append(i).append("\n");
                    send.append("List of files in folder:\n");
                    for (File file : files) {
                        send.append(file).append("\n");
                    }

                    byte[] f = send.toString().getBytes(StandardCharsets.UTF_8);

                    s.write(f);
                    s.close();
                    socket.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
