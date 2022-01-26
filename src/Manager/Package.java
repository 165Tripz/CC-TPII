package Manager;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Package implements Serializable {

    private static int id = 0;
    private final int memID;
    private int type; // 1 "Ack" | 2 "Syn" | 3 "Req" | 5 "Del" | 6 "Mis" | 7 "RTT"  | 9 "Snd"
    private int many;
    private byte[] data;
    private String message;
    private boolean isFinal;

    public Package(int type,byte[]data,String message,boolean isFinal){
        this.memID = id;
        id++;
        this.type = type;
        this.data = data;
        this.isFinal = isFinal;
        this.message = message; //
    }

    public Package() {
        memID = id;
        id++;
        type = 0;
        data = null;
        message = null;
        isFinal = true;
    }

    public Package(Package e) {
        this.memID = e.memID;
        this.type = 1;
        data = null;
        message = null;
        isFinal = true;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public int getMemID() {
        return memID;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setMany(int many) {
        this.many = many;
    }

    public int getMany() {
        return many;
    }

    public byte[] toBytes() {
        byte[] aux = ByteBuffer.allocate(4).putInt(memID).array(); //4
        byte[] aux2 = ByteBuffer.allocate(4).putInt(type).array(); //4
        byte[] aux5 = ByteBuffer.allocate(4).putInt(many).array(); //4
        byte[] aux3 = message.getBytes(StandardCharsets.UTF_8);

        byte[] n = new byte[1 + 12 + 16192 + aux3.length];
        n[0] = (byte) (isFinal?1:0);

        int i = 1;
        for (int o = 0; o < aux.length ; o++, i++) {
            n[i] = aux[o];
            n[i+4] = aux2[o];
            n[i+8] = aux5[o];
        } i += 8;
        for (int o = 0; o < data.length; o++, i++) {
            n[i] = data[o];
        }
        for (int o = 0; o < aux3.length ; o++, i++) {
            n[i] = aux3[o];
        }

        return n;
    }


    public Package(byte[] n) {
        byte[] aux = new byte[4]; //4
        byte[] aux2 = new byte[4]; //4
        byte[] aux5 = new byte[4];
        data = new byte[16192];
        byte[] aux4 = new byte[500];

        int i = 1;
        for (int o = 0; o < 4 ; o++, i++) {
            aux[o] = n[i];
            aux2[o] = n[i+4];
            aux5[o] = n[i+8];
        } i += 8;
        for (int o = 0; o < 16192 ; o++, i++) {
            data[o] = n[i];
        }
        for (int o = 0; o < aux4.length ; o++, i++) {
            aux4[o] = n[i];
        }

        isFinal = (n[0]==1);
        memID = ByteBuffer.wrap(aux).getInt();
        type = ByteBuffer.wrap(aux2).getInt();
        many = ByteBuffer.wrap(aux5).getInt();
        message = new String(aux4,StandardCharsets.UTF_8);

    }

}
