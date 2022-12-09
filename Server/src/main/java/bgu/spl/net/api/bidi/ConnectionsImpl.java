package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {


    private ConcurrentHashMap<Integer,ConnectionHandler<T>> clients;
    public  AtomicInteger idCounter;

    public ConnectionsImpl() {
        this.clients = new ConcurrentHashMap<>();
        idCounter=new AtomicInteger(0);
    }

    @Override
    public ConcurrentHashMap<Integer, ConnectionHandler<T>> getClients() {
        return clients;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(clients.get(connectionId)==null) {
            return false;
        }
        clients.get(connectionId).send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> curr: clients.values())
        {
            curr.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        clients.remove(connectionId);
    }

    public synchronized int addConnection(ConnectionHandler<T> handler) {
        int id = idCounter.getAndIncrement();
        clients.put(id,handler);
        return id;
    }

}
