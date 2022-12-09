package bgu.spl.net.impl.echo;//package bgu.spl.net.impl.echo;
//
//import bgu.spl.net.api.MessagingProtocol;
//import bgu.spl.net.api.bidi.BidiMessagingProtocol;
//import bgu.spl.net.api.bidi.Connections;
//import bgu.spl.net.api.bidi.Processor;
//
//import java.time.LocalDateTime;
//
//public class EchoProtocol implements BidiMessagingProtocol<String> {
//
//    private boolean shouldTerminate = false;
//    private Processor processor;
//    private int connectionId;
//
//    public EchoProtocol(Processor processor) {
//        this.processor=processor;
//    }
//
//    @Override
//    public void start(int connectionId, Connections<String> connections) {
//        connections.getClients().get(connectionId).register();
//        this.connectionId=connectionId;
//    }
//
//    @Override
//    public void process(String msg) {
//        shouldTerminate = "bye".equals(msg);
//        System.out.println("[" + LocalDateTime.now() + "]: " + msg);
//        processor.createEcho(msg,connectionId);
//    }
//
//    private void createEcho(String message) {
//        String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
//        return message + " .. " + echoPart + " .. " + echoPart + " ..";
//    }
//
//    @Override
//    public boolean shouldTerminate() {
//        return shouldTerminate;
//    }
//}
