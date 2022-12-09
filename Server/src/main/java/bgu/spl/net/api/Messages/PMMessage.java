package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

import java.time.LocalDate;

public class PMMessage extends Message {
    private int opCode=6;
    private String username;
    private String content;
    private String dateAndTime;


    public PMMessage(String msg) {

        String [] message = msg.split(String.valueOf('\0'), 2);
        username= message[0];
        LocalDate LdateAndTime= LocalDate.now();
        dateAndTime= LdateAndTime.getDayOfMonth()+"-"+LdateAndTime.getMonthValue()+"-"+LdateAndTime.getYear();
        content=message[1].substring(0,message[1].length()-1)+" "+dateAndTime;
    }

    public int getOpCode() {
        return opCode;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
