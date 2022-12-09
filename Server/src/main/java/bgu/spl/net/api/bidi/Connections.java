package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;

public interface Connections<T> {


    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

    public ConcurrentHashMap<Integer, ConnectionHandler<T>> getClients();

    public int addConnection(ConnectionHandler<T> handler);



}
