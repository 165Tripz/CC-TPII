package FT_Rapid;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * FT-Rapid Sender
 */

public class UDPSender implements Runnable {

    private final DatagramSocket mainSocket;
    private final Buffer sendPackages;

    /**
     * Constructor parameters
     * @param socket Socket de porta 80
     * @param buffer Pacotes que vao ser enviados
     */

    public UDPSender(DatagramSocket socket, Buffer buffer) {
        sendPackages = buffer;
        mainSocket = socket;
    }

    /**
     * Iniciação da thread
     *
     * Retira o pacote do buffer
     * Cria codex e codifica a mensagem
     * Envia a mensagem
     *
     */

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket datagramPacket = sendPackages.take();

                Codex x = new Codex();

                datagramPacket.setData(x.encrypt(datagramPacket.getData(), "FixePataniscas42", datagramPacket.getLength()));

                mainSocket.send(datagramPacket);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
