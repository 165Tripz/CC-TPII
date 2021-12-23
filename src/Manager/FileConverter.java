package Manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class FileConverter implements Runnable{
    File file;
    Queue<byte[]> messages = new ArrayDeque<>();
    Queue<String> strings = null;

    public void setStrings(Queue<String> strings) {
        this.strings = strings;
    }

    public FileConverter(File file) {
        this.file = file;
    }

    public Queue<byte[]> bytify() {
        try (FileReader fileReader = new FileReader(file)) {
            char[] buffer = new char[8096];
            int flag = 1;
            while (flag > 0) {
                flag = fileReader.read(buffer);
                CharBuffer charBuffer = CharBuffer.wrap(buffer);
                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
                byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                        byteBuffer.position(), byteBuffer.limit());
                Arrays.fill(byteBuffer.array(), (byte) 0);
                messages.add(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public void run() {
        debitify();
    }

    public void debitify() {
        file.delete();
        file.mkdirs();

        try (FileWriter fileWriter = new FileWriter(file,true)) {
                while (strings.size() != 0) {
                    String s = strings.poll();
                    fileWriter.write(s);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
