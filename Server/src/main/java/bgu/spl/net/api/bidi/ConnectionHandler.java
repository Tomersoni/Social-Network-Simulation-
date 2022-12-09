package bgu.spl.net.api.bidi;

import java.io.Closeable;

public interface ConnectionHandler<T> extends Closeable{

    void send(T msg) ;

    public void register();

    public void unregister();

    public boolean isRegistered();

}
