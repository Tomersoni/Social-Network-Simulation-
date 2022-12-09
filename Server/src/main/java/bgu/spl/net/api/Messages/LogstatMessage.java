package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class LogstatMessage extends Message
{
    private int opCode=7;

    public LogstatMessage() {

    }

    public int getOpCode() {
        return opCode;
    }
}


