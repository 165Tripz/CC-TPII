package App;

import FolderManager.FolderWatcher;
import UDP.UdpUnicastClient;
import UDP.UdpUnicastServer;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        Path p = null;

        while (p == null) {
            try {
                System.out.print("Pasta : ");

                String path = in.nextLine();
                File file = new File(path);
                char a = path.charAt(0);

                if (a == '/' || a == '~' || a == '.')
                    throw new InvalidPathException(path,"Invalid Directory");

                if (!file.exists() || !file.isDirectory()) {
                    throw new NotDirectoryException("Not a diretory.");
                }

                p = Paths.get(path);


                System.out.println("Destino da pasta : " + p.toAbsolutePath());

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        int port = 50001;
        UdpUnicastServer server = new UdpUnicastServer(port);
        UdpUnicastClient client = new UdpUnicastClient(port);
        FolderWatcher watcher = new FolderWatcher(p,client);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        //executorService.submit(server);
        //executorService.submit(client);
        executorService.submit(watcher);
        executorService.shutdown();
    }

}
