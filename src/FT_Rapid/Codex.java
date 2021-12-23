package FT_Rapid;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * FT-Rapid Codex
 */

public class Codex {

    /**
     * Função 'concatenateByteArrays' que concatena dois arrays de bytes.
     * @param a byte[] : É um array de bytes a ser concatenado.
     * @param b byte[] : É um array de bytes a ser concatenado.
     */
    byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Função 'getdata' que transforma um checksum no array de bytes.
     * Retorna o array de bytes resultante da transformação do checksum.
     * @param x long : É o checksum a ser transformado.
     */
    public byte[] getdata(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * Função 'enumerate' que transforma um array de bytes num inteiro (long).
     * Retorna o inteiro (long) resultante da transformação do array de bytes.
     * @param bytes byte[] : É o array a ser transformado.
     */
    public long enumerate(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * Função 'encrypt' que realiza a encriptação de um array de bytes.
     * Retorna o array de bytes encriptado.
     * @param messageToEncript byte[] : É o array de bytes a ser encriptado.
     * @param keyBytes String : É a chave de encriptação.
     * @param length int : É o tamanho do array a ser encriptado.
     */
    public byte[] encrypt(byte[] messageToEncript , String keyBytes, int length){
        try {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encriptado =  cipher.doFinal(messageToEncript,0,length);
            Checksum checksum = new CRC32();
            checksum.update(encriptado, 0,encriptado.length);

            long checksumValue = checksum.getValue();
            return concatenateByteArrays(encriptado,getdata(checksumValue));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Função 'decrypt' que realiza a desencriptação de um array de bytes.
     * Retorna o array de bytes desencriptado.
     * @param encryptedMessage byte[] : É o array de bytes a ser desencriptado.
     * @param keyBytes String : É a chave de desencriptação.
     * @param length int : É o tamanho do array a ser desencriptado.
     */
    public byte[] decrypt(byte[] encryptedMessage, String keyBytes, int length) {
        try {
            int index = length - 8;
            byte[] tail = new byte[8];
            for(int i = index,j = 0; i < length ; i++, j++){
                tail[j] = encryptedMessage[i];
            }

            byte[] filtered = new byte[index];
            System.arraycopy(encryptedMessage, 0, filtered, 0, index);


            Checksum checksum_compare = new CRC32();
            checksum_compare.update(filtered, 0, filtered.length);
            long checksum_compare_long = checksum_compare.getValue();

            if( enumerate(tail) != checksum_compare_long) {
                System.out.println("CRC doesnt check out");
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(filtered);

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}