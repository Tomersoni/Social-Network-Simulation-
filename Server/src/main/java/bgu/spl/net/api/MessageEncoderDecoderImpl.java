package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

        private byte[] bytes = new byte[1 << 10]; //start with 1k
        private int len = 0;

        @Override
        public Message decodeNextByte(byte nextByte) {
            //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
            //this allow us to do the following comparison
            if (nextByte == ';') {
                return popMessage();
            }
            pushByte(nextByte);
            return null; //not a line yet
        }

        @Override
        public byte[] encode(Message message) {
            return (message.toString()+"\n").getBytes(); //uses utf8 by default
        }

        private void pushByte(byte nextByte) {
            if (len >= bytes.length) {
                bytes = Arrays.copyOf(bytes, len * 2);
            }

            bytes[len++] = nextByte;
        }

        private Message popMessage() {
            //notice that we explicitly requesting that the string will be decoded from UTF-8
            //this is not actually required as it is the default encoding in java.

            byte[] opcode= Arrays.copyOfRange(bytes,0,2);
            short opCode=bytesToShort(opcode);
            Message msg=null;

            if(opCode==4){
                byte[] followcode= Arrays.copyOfRange(bytes,2,4);
                short followCode=bytesToShort(followcode);
                String username=new String(bytes,4,len-4,StandardCharsets.UTF_8);
                msg=new FollowMessage(followCode,username);
            }

            String result = new String(bytes, 2, len-2, StandardCharsets.UTF_8);
            System.out.println(result);


            switch(opCode)
            {
                case (short)1 :
                    msg=new RegisterMessage(result);
                    break;
                case (short)2:
                    msg=new LoginMessage(result);
                    break;
                case (short)3:
                    msg=new LogoutMessage();
                    break;
                case (short)4:
                    break;
                case (short)5:
                    msg=new PostMessage(result);
                    break;
                case (short)6:
                    msg=new PMMessage(result);
                    break;
                case (short)7:
                    msg=new LogstatMessage();
                    break;
                case (short)8:
                    msg=new StatMessage(result);
                    break;

                case (short)12:
                    msg=new BlockMessage(result);
                    break;

            }


            len = 0;
            return msg;
        }

        public short bytesToShort(byte[] byteArr)

        {

            short result = (short)((byteArr[0] & 0xff) << 8);

            result += (short)(byteArr[1] & 0xff);

            return result;

        }
    }


