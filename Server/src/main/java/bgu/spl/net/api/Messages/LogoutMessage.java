package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class LogoutMessage extends Message {

    private int opCode=3;

    public LogoutMessage() {

    }

    public int getOpCode() {
        return opCode;
    }
}
