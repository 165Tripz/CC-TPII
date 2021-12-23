package Manager;

import java.io.Serializable;

public class Package implements Serializable {

    private static int id = 0;
    private int memID;
    private String type; // "Ack" | "Syn" | "Req" | "Trf" | "Del" | "Mis" | "RTT" | "Beg"
    private byte[] data;
    private String message;
    private boolean isFinal;

    Package(String type,byte[]data,String message,boolean isFinal){
        this.memID = id;
        id++;
        this.type = type;
        this.data = data;
        this.isFinal = isFinal;
        this.message = message;
    }

    Package() {
        memID = id;
        id++;
        type = null;
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

    public void setType(String type) {
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

    public String getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }

    byte[] toBytes() {
        return null;
    }

}
