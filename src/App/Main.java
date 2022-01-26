package App;

import Manager.Manager;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        try {
            Manager manager = new Manager();
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
