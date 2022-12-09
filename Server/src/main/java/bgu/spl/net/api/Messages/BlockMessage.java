package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class BlockMessage extends Message {

    private int opCode=12;
    String username;

    public BlockMessage(String msg) {

        username=msg.substring(0,msg.length()-1);

    }

    public int getOpCode() {
        return opCode;
    }

    public String getUsername() {
        return username;
    }
}
