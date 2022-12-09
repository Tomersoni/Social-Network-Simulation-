package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionHandler;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private ConnectionsImpl<T> connections;
    private boolean isRegistered=false;
    private T response;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol,
                                     ConnectionsImpl<T> connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connections=connections;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            int id= connections.addConnection(this);

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println("New Message Received");
                    protocol.process(nextMessage,id);
                }
//                if (response != null) {
//                    out.write(encdec.encode(response));
//                    out.flush();
//                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            out.write(encdec.encode(msg));
            out.flush();
            System.out.println(msg);
            System.out.println("Message sent to client");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void register()
    {
        isRegistered=true;
    }

    public void unregister()
    {
        isRegistered=false;
    }

    @Override
    public boolean isRegistered() {
        return isRegistered;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
