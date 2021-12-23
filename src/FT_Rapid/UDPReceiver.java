package FT_Rapid;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * FT-Rapid UDPReceiver
 */

public class UDPReceiver implements Runnable{
    private final Buffer storagePackage;
    private final DatagramSocket mainSocket;

    /**
     * Construtor que inicializa o Buffer 'storagePackages' e realiza a atribuição do Socket 'mainSocket'.
     * @param socket Socket de porta 80.
     * @param buffer Pacotes que vão ser enviados.
     */
    public UDPReceiver(Buffer buffer, DatagramSocket socket) {
        storagePackage = buffer;
        mainSocket = socket;
    }

    /**
    public byte[] limit(byte[] aux) {
        int i;
        for (i = aux.length-1; aux[i] == 0;i--);
        i++;
        byte[] aux2 = new byte[i];
        System.arraycopy(aux, 0, aux2, 0, i);

        return aux2;
    }
     */

    /**
     * Iniciação da Thread.
     *
     * Adiciona os Pacotes ao Buffer.
     * Descodifica a Mensagem através do Codex.
     * Recebe a Mensagem.
     */
    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[10440];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                mainSocket.receive(datagramPacket);

                Codex x = new Codex();

                byte[] recevido = datagramPacket.getData();
                byte[] desencriptado = x.decrypt(recevido,"FixePataniscas42", datagramPacket.getLength());
                if (desencriptado != null) {
                    datagramPacket.setData(desencriptado);
                    storagePackage.add(datagramPacket);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}