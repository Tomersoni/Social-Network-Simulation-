#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <thread>
#include <iostream>
#include <cstring>

//ConnectionHandler &connectionHandler1(host, port);

void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
void sendToServer(ConnectionHandler* connectionHandler, bool terminate) {

    while (terminate) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cout<<"Enter command: "<<std::endl;
        std::cin.getline(buf, bufsize);
        std::string input(buf);
        int len=input.length();

        int i = input.find(" ");
        std::string opCode;
        if(i==-1){
            opCode=input;
        }
        else {
            opCode = input.substr(0, i);
            input = input.substr(opCode.size() + 1, input.size());
            std::cout << input.length();
        }
        char bytesArr[100];

        if (opCode == "REGISTER") {
            shortToBytes(1, bytesArr);
            for (int i = 0; i < input.length(); i++) {
                if (input[i] == ' ')
                    bytesArr[2 + i] = '\0';
                else
                    bytesArr[2 + i] = input[i];
            }
            bytesArr[2 + input.length()] = '\0';
            std::cout<<input.length();
            len=input.length()+3;
        }

        else if (opCode == "LOGIN") {
            shortToBytes(2, bytesArr);
            for (int i = 0; i < input.length(); i++) {
                if (input[i] == ' ')
                    bytesArr[2 + i] = '\0';
                else
                    bytesArr[2 + i] = input[i];
            }
            bytesArr[2 + input.length()] = '\0';
            //bytesArr[2 + input.length() + 1] = '1';
            len=3+input.length();
        }

        else if (opCode == "LOGOUT") {
            shortToBytes(3, bytesArr);
            len=2;
        }

        else if (opCode == "FOLLOW") {
            shortToBytes(4, bytesArr);
            char followArr[2];
            if (input[0] == '1')
                shortToBytes(1, followArr);
            else
                shortToBytes(0, followArr);
            bytesArr[2] = followArr[0];
            bytesArr[3] = followArr[1];
            for (int i = 2; i < input.length(); i++) {
                bytesArr[i+2] = input[i];
            }
            len=2+input.length();
        }
        else if (opCode == "POST") {
            shortToBytes(5, bytesArr);
            for (int i =0; i < input.length(); i++) {
                bytesArr[2 + i] = input[i];
            }
            bytesArr[2 + input.length()] = '\0';
            len=3+input.length();
        }

        else if (opCode == "PM") {
            shortToBytes(6, bytesArr);
            bool found=false;
            for (int i = 0; i < input.length(); i++) {
                if (input[i] == ' ' && !found){
                    bytesArr[2 + i] = '\0';
                    found=true;
                }
                else
                    bytesArr[2 + i] = input[i];
            }
            bytesArr[2 + input.length()] = '\0';
            len=3+input.length();
        }

        else if (opCode == "LOGSTAT") {
            shortToBytes(7, bytesArr);
            len=2;
        }
        else if (opCode == "STAT") {
            shortToBytes(8, bytesArr);
            for (int i = 0; i < input.length(); i++) {
                if(input[i]==' '){
                    bytesArr[2+i]='|';
                }
                else {
                    bytesArr[2 + i] = input[i];
                }
            }
            bytesArr[2 + input.length()] = '\0';
            len=3+input.length();
        }
        else if (opCode == "BLOCK") {
            shortToBytes(12, bytesArr);
            for (int i = 0; i < input.length(); i++) {
                bytesArr[2 + i] = input[i];
            }
            bytesArr[2 + input.length()] = '\0';
            len=3+input.length();
        }

        bytesArr[len]=';';
        len=len+1;

//        while(bytesArr[i]!=';')
//        {
//            std::cout<<bytesArr[i]<<std::endl;
//            i++;
//        }

        if (!connectionHandler->sendBytes(bytesArr,len)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
        std::cout << "Sent " << len+1 << " bytes to server" << std::endl;

    }

}

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    int len;
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    bool terminate=true;
    std::thread work(sendToServer, &connectionHandler,&terminate);
    work.detach();


	//From here we will see the rest of the ehco client implementation:
    while (1) {
        // We can use one of three options to read data from the server:
        // 1. Read a fixed number of characters
        // 2. Read a line (up to the newline character using the getline() buffered reader
        // 3. Read up to the null character
        std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }


		len=answer.length();
		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        answer.resize(len-1);
        std::cout << "Reply: " << answer << " " << len << " bytes " << std::endl << std::endl;
        if (answer == "ACK 3") {
            terminate=false;
            std::cout << "Exiting...\n" << std::endl;
            break;
        }
    }


    return 0;
}
