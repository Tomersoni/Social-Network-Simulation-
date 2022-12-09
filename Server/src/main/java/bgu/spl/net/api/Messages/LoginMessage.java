package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class LoginMessage extends Message {

    private int opCode=2;
    private String username;
    private String password;
    boolean captcha=true;

    public LoginMessage(String msg) {

        String[] message = msg.split(String.valueOf('\0'),3);
        username=message[0];
        password=message[1];
        captcha=message[2].charAt(0)=='1';
    }

    public int getOpCode() {
        return opCode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCaptcha() {
        return captcha;
    }
}
