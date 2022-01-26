package FT_Rapid;

import Manager.Package;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FT-Rapid UDPReceiver
 */

public class UDPReceiver implements Runnable{
    private ReentrantLock lock;
    private final Buffer storagePackage;
    private final Buffer sender;
    private final DatagramSocket mainSocket;
    private final Set<Integer> response;
    private final HashMap<Integer, Package> resend;

    /**
     * Construtor que inicializa o Buffer 'storagePackages' e realiza a atribuição do Socket 'mainSocket'.
     * @param socket Socket de porta 80.
     * @param buffer Pacotes que vão ser enviados.
     */
    public UDPReceiver(Buffer buffer, DatagramSocket socket, Buffer sender, Set<Integer> response, HashMap<Integer, Package> resend) {
        storagePackage = buffer;
        this.sender = sender;
        mainSocket = socket;
        this.response = response;
        this.resend = resend;
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
                    Package e = new Package(desencriptado);
                    Package res = new Package(e);

                    if (response.size() > 3 && e.getMemID() > response.stream().findFirst().get()) {
                        var eee = resend.get(response.stream().findFirst().get()).toBytes();

                        DatagramPacket responsePacket = new DatagramPacket(eee,eee.length);

                        sender.add(responsePacket);
                    }

                    lock.lock();
                    if (e.getType() == 1) {
                        resend.remove(e.getMemID());
                        response.remove(e.getMemID());
                    } else {
                        DatagramPacket responsePacket = new DatagramPacket(res.toBytes(),res.toBytes().length);
                        sender.add(responsePacket);
                    }
                    lock.unlock();


                    storagePackage.add(datagramPacket);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}