package FT_Rapid;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * FT-Rapid UDPSender
 */

public class UDPSender implements Runnable {

    private final DatagramSocket mainSocket;
    private final Buffer sendPackages;

    /**
     * Construtor que inicializa o Buffer 'sendPackages' e realiza a atribuição do Socket 'mainSocket'.
     * @param socket Socket de porta 80.
     * @param buffer Pacotes que vão ser enviados.
     */
    public UDPSender(DatagramSocket socket, Buffer buffer) {
        sendPackages = buffer;
        mainSocket = socket;
    }

    /**
     * Iniciação da Thread
     *
     * Retira os Pacotes do Buffer.
     * Cria Codex e codifica a Mensagem.
     * Envia a Mensagem.
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