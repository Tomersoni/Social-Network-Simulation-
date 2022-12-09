package bgu.spl.net.api.bidi;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class InstagramClient {

    static String clientInput = " ";
    static BufferedReader sIn;
    static BufferedWriter sOut;

    public static void main(String[] args) throws IOException {

        LineMessageEncoderDecoder encodeDecode = new LineMessageEncoderDecoder();

        try (Socket sock = new Socket(args[0], 7777);
             BufferedInputStream in = new BufferedInputStream(new BufferedInputStream(sock.getInputStream()));
             BufferedOutputStream out = new BufferedOutputStream(new BufferedOutputStream(sock.getOutputStream()))) {
//            String ex1= "LOGIN shir a12;";
//            String ex1="";
            byte[] opcode = shortToBytes((short) 1);
            System.out.println(Arrays.toString(opcode));
            System.out.println(bytesToShort(opcode));
            System.out.println(opcode);
            int read;

            String login = "Shir asd123 02-04-1999;";
            String loginMsg = "";

            for (int i = 0; i < login.length(); i++) {
                if (login.charAt(i) == ' ')
                    loginMsg += "\0";
                else
                    loginMsg += login.charAt(i);
            }

            System.out.println(loginMsg);

            byte[] msg = loginMsg.getBytes(StandardCharsets.UTF_8);
            byte[] msgToSend = new byte[msg.length + 2];

            for (int i = 2; i < msgToSend.length; i++) {
                msgToSend[i] = msg[i - 2];
            }

            for (int i = 0; i < 2; i++) {
                msgToSend[i] = shortToBytes((short) 1)[i];
            }

            System.out.println(Arrays.toString(msgToSend));

            System.out.println(new String(msgToSend, 0, msgToSend.length, StandardCharsets.UTF_8));

            System.out.println(bytesToShort(Arrays.copyOfRange(msgToSend, 0, 2)));

            out.write(msgToSend);
            out.flush();

//           System.out.println(in.readLine());

            while ((read = in.read()) >= 0) {
                String nextMessage = encodeDecode.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println(nextMessage);
                }
            }
        }
//
//        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
//        try (Socket sock = new Socket(args[0], 7777);
//
//             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));){
//
//            sIn=in;
//            sOut=out;
//
//            Scanner keyboard = new Scanner(System.in);
//            String serverInput=" ";
//
//            String finalServerInput = serverInput;
//            Thread reader= new Thread(new Runnable() {
//                @Override
//                public void run()  {
//                    while(!finalServerInput.equals("ACK 3")) {
//                        System.out.println("Awaiting message: ");
//                        clientInput = keyboard.nextLine();
//                        try {
//                            sOut.write(clientInput);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            sOut.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            reader.start();
//
//            while(serverInput.equals("ACK 3"))
//            {
//                serverInput= in.readLine();
//                if(serverInput!=null)
//                System.out.println(serverInput);
//            }
//
//        }
//    }



    }

    public static byte[] shortToBytes(short num)

    {

        byte[] bytesArr = new byte[2];

        bytesArr[0] = (byte)((num >> 8) & 0xFF);

        bytesArr[1] = (byte)(num & 0xFF);

        return bytesArr;

    }

    public static short bytesToShort(byte[] byteArr)

    {

        short result = (short)((byteArr[0] & 0xff) << 8);

        result += (short)(byteArr[1] & 0xff);

        return result;

    }


}
