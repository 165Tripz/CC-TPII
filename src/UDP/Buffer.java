package UDP;

import java.net.DatagramPacket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    Queue<DatagramPacket> buffer;
    Lock l;
    Condition cond;

    public Buffer(){
        buffer = new ArrayDeque<DatagramPacket>();
        l = new ReentrantLock();
        cond = l.newCondition();
    }

    public synchronized boolean hasElements(){
        return buffer.size() > 0;
    }

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

