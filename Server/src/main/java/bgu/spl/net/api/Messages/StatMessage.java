package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.util.LinkedList;
import java.util.List;

public class StatMessage extends Message {
    private int opCode= 8;
    List<String> usernames;

    public StatMessage(String msg) {
        msg= msg.substring(0,msg.length()-1);
        usernames= new LinkedList<>();
        if(msg!=null) {
            while (msg.contains("|")) {
                usernames.add(msg.substring(0, msg.indexOf("|")));
                msg = msg.substring(msg.indexOf("|") + 1, msg.length());
            }
            usernames.add(msg);
        }
    }

    public int getOpCode() {
        return opCode;
    }

    public List<String> getUsernames() {
        return usernames;
    }
}
