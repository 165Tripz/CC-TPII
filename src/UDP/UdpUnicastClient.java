package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpUnicastClient implements Runnable{
    private final int port;
    private Buffer buffer;

    public UdpUnicastClient(int port) {
        this.port = port;
    }

    public byte[] limit(byte[] aux) {
        int i;
        for (i = aux.length-1; aux[i] == 0;i--);
        i++;
        byte[] aux2 = new byte[i];
        System.arraycopy(aux, 0, aux2, 0, i);

        return aux2;
    }

    @Override
    public void run() {
        try (DatagramSocket clientSocket = new DatagramSocket(port)){
            byte[] buffer = new byte[65507];
            clientSocket.setSoTimeout(3000);
            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer,0, buffer.length);
                clientSocket.receive(datagramPacket);

                String receivedMessage = new String(limit(datagramPacket.getData()));

                System.out.println(receivedMessage);

                Codex c = new Codex();

                byte[] aux = receivedMessage.getBytes();
                System.out.println("C: " + aux);
                byte[] aux2 = c.decryptMessage(aux,"FixePataniscas42", aux.length);

                String receivedMessage2 = new String(aux2, 0, datagramPacket.getLength());

                System.out.println(receivedMessage2);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Client is closing.");
        }
    }
}
