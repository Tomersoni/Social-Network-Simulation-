package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.Processor;
import bgu.spl.net.srv.Server;

import java.util.Arrays;

public class ReactorMain {

    public static void main(String[] args)
    {
        int port= Integer.parseInt(args[0]);
        int num= Integer.parseInt(args[1]);
        String[] fWords= {"fuck","asshole"};;
        System.out.println("Forbidden words are: " + Arrays.toString(fWords));
        ConnectionsImpl connections = new ConnectionsImpl();
        Processor processor= new Processor(connections,fWords);

//        try(Server<String> server1 = Server.threadPerClient(7777,connections,()->new BidiMessagingProtocolImpl<>(processor),
//                ()->new MessageEncoderDecoderImpl());)
//        {
//            server1.serve();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        try(Server<String> server1 = Server.reactor(num,port,()->new BidiMessagingProtocolImpl<>(processor),
                ()->new MessageEncoderDecoderImpl(),connections);)
        {
            server1.serve();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
