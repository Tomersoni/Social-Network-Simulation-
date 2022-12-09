package bgu.spl.net.api.Messages;
import bgu.spl.net.api.Message;

public class RegisterMessage extends Message {

    private int opCode=1;

    private String userName;
    private String password;
    private String birthday;

    public RegisterMessage(String msg) {

        String [] message = msg.split(String.valueOf('\0') ,3);
        userName=message[0];
        password=message[1];
        birthday=message[2].substring(0,message[2].length()-1);
    }

    public int getOpCode() {
        return opCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }
}
