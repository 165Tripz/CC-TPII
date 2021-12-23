package FT_Rapid;

import java.net.DatagramPacket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FT-Rapid Buffer
 */

public class Buffer {
    Queue<DatagramPacket> buffer;
    Lock l;
    Condition cond;

    /**
     * Construtor que inicializa o Buffer, o Lock e a Condição.
     */
    public Buffer(){
        buffer = new ArrayDeque<>();
        l = new ReentrantLock();
        cond = l.newCondition();
    }

    /**
     * Função 'hasElements' que verifica se o Buffer contem algum elemento.
     * Retorna "true" caso o Buffer possua elementos e retorna "false" caso esteja vazio.
     */
    public synchronized boolean hasElements(){
        return buffer.size() > 0;
    }

    /**
     * Função 'takeAll' que acede ao Buffer e tira todos os pacotes que este possua.
     * Devolve uma lista desses mesmos pacotes.
     */
    public List<DatagramPacket> takeAll() throws InterruptedException{
        List<DatagramPacket> aux = new ArrayList<>();
        try{
            l.lock();
            while(buffer.size() == 0)
                cond.await();
            while(buffer.size() > 0){
                aux.add(buffer.poll());
            }
        }
        finally{
            l.unlock();
        }
        return aux;
    }

    /**
     * Função 'take' que acede ao Buffer e tira apenas um pacote que este possua.
     * Devolve esse mesmo pacote.
     */
    public DatagramPacket take() throws InterruptedException{
        DatagramPacket d;
        try{
            l.lock();
            while(buffer.size() == 0)
                cond.await();
            d = buffer.poll();
        }
        finally{
            l.unlock();
        }
        return d;
    }

    /**
     * Função 'add' que acede ao Buffer e adiciona-lhe um pacote.
     * @param s DatagramPacket : É o pacote a ser adicionado ao Buffer.
     */
    public void add(DatagramPacket s){
        try{
            l.lock();
            buffer.add(s);
            cond.signalAll();
        }
        finally{
            l.unlock();
        }
    }

    /**
     * Função 'addAll' que acede ao Buffer e adiciona-lhe uma lista de pacotes.
     * @param s List<DatagramPacket> : É a lista de pacotes a ser adicionados ao Buffer.
     */
    public void addAll(List<DatagramPacket> s){
        try{
            l.lock();
            buffer.addAll(s);
            cond.signalAll();
        }
        finally{
            l.unlock();
        }
    }
}