package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpUnicastServer implements Runnable {

    private final int clientPort;

    public UdpUnicastServer(int port) {
        clientPort = port;
    }

    @Override
    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket(50000)){
            for (int i=0; i < 3; i++) {
                String message = "Message Number: " + i;
                Codex c = new Codex();
                byte[] messageBytes = c.encryptMessage(message.getBytes(StandardCharsets.UTF_8),"FixePataniscas42",message.length());

                DatagramPacket datagramPacket = new DatagramPacket(messageBytes,messageBytes.length, InetAddress.getLocalHost(),clientPort);

                System.out.println(new String(messageBytes));

                serverSocket.send(datagramPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
