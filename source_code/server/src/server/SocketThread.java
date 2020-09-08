/*
MIT License

Copyright (c) 2020 Who Write Code

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author Le Nhut Nam
 */
public class SocketThread implements Runnable{
    
    // Properties
    Socket socket;
    ServerMainForm serverMainForm;
    DataInputStream dataInputStream;
    StringTokenizer stringTokenizer;
    String client, filesharing_username;
    
    // Constant value
    private final int BUFFER_SIZE = 100;
    
    // Constructor
    /**
     * 
     * @param socket
     * @param serverMainForm 
     */
    public SocketThread(Socket socket, ServerMainForm serverMainForm){
        this.serverMainForm = serverMainForm;
        this.socket = socket;
        
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException iOException) {
            serverMainForm.appendMessage("[SocketThreadIOException]: " + iOException.getMessage());
        }
    }
    
    /*   This method will get the client socket in client socket list then stablish a connection    */
    /**
     * 
     * @param receiver
     * @param sender
     * @param filename 
     */
    private void createConnection(String receiver, String sender, String filename){
        try {
            serverMainForm.appendMessage("[createConnection]: creating file sharing connection.");
            Socket s = serverMainForm.getClientList(receiver);
            if(s != null){ 
                // Client was exists
                serverMainForm.appendMessage("[createConnection]: Socket OK");
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                serverMainForm.appendMessage("[createConnection]: DataOutputStream OK");
                // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                String format = "CMD_FILE_XD " + sender + " " +receiver + " " +filename;
                dosS.writeUTF(format);
                serverMainForm.appendMessage("[createConnection]: "+ format);
            }else{
                // Client was not exist, send back to sender that receiver was not found.
                serverMainForm.appendMessage("[createConnection]: Client was not found '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + receiver + "' was not found in the list, make sure it is on the online list.!");
            }
        } catch (IOException iOException) {
            serverMainForm.appendMessage("[createConnection]: " + iOException.getLocalizedMessage());
        }
    }
    
    /**
     * 
     */
    @Override
    public void run() {
        try {
            while(true){
                /** Get Client Data **/
                String data = dataInputStream.readUTF();
                stringTokenizer = new StringTokenizer(data);
                String CMD = stringTokenizer.nextToken();
                /** Check CMD **/
                switch(CMD){
                    case "CMD_JOIN":
                        /** CMD_JOIN [clientUsername] **/
                        System.out.println(data);
                        String clientUsername = stringTokenizer.nextToken();
                        client = clientUsername;
                        serverMainForm.setClientList(clientUsername);
                        serverMainForm.setSocketList(socket);
                        serverMainForm.appendMessage("[Client]: " + clientUsername + " joins the chatroom.!");
                        serverMainForm.appendMessage("[Client]: " + clientUsername + " at " + socket.getInetAddress().toString() + " on port " + socket.getPort());
                        break;
                        
                    case "CMD_CHAT":
                        /** CMD_CHAT [from] [sendTo] [message] **/
                        String from = stringTokenizer.nextToken();
                        String sendTo = stringTokenizer.nextToken();
                        String msg = "";
                        while(stringTokenizer.hasMoreTokens()){
                            msg = msg + " " + stringTokenizer.nextToken();
                        }
                        Socket tsoc = serverMainForm.getClientList(sendTo);
                        try {
                            DataOutputStream dataOutputStream = new DataOutputStream(tsoc.getOutputStream());
                            /** CMD_MESSAGE **/
                            String content = from + ": " + msg;
                            dataOutputStream.writeUTF("CMD_MESSAGE " + content);
                            serverMainForm.appendMessage("[Message]: From " + from + " To " + sendTo + " : " + msg);
                        } catch (IOException iOException) {  
                            System.out.println(iOException.getMessage());
                            serverMainForm.appendMessage("[IOException]: Unable to send message to " + sendTo); 
                        }
                        break;
                    
                    case "CMD_CHATALL":
                        /** CMD_CHATALL [from] [message] **/
                        String chatall_from = stringTokenizer.nextToken();
                        String chatall_msg = "";
                        while(stringTokenizer.hasMoreTokens()){
                            chatall_msg = chatall_msg + " " + stringTokenizer.nextToken();
                        }
                        String chatall_content = chatall_from + " " + chatall_msg;
                        for(int i = 0; i < serverMainForm.clientList.size(); i++){
                            if(!serverMainForm.clientList.elementAt(i).equals(chatall_from)){
                                try {
                                    Socket tsoc2 = (Socket) serverMainForm.socketList.elementAt(i);
                                    DataOutputStream dataOutputStream2 = new DataOutputStream(tsoc2.getOutputStream());
                                    dataOutputStream2.writeUTF("CMD_MESSAGE " + chatall_content);
                                } catch (IOException iOException) {
                                    serverMainForm.appendMessage("[CMD_CHATALL]: " + iOException.getMessage());
                                }
                            }
                        }
                        serverMainForm.appendMessage("[CMD_CHATALL]: "+ chatall_content);
                        break;
                    
                    case "CMD_SHARINGSOCKET":
                        serverMainForm.appendMessage("CMD_SHARINGSOCKET : Client stablish a socket connection for file sharing...");
                        String file_sharing_username = stringTokenizer.nextToken();
                        filesharing_username = file_sharing_username;
                        serverMainForm.setClientFileSharingUsername(file_sharing_username);
                        serverMainForm.setClientFileSharingSocket(socket);
                        serverMainForm.appendMessage("CMD_SHARINGSOCKET : Username: " + file_sharing_username);
                        serverMainForm.appendMessage("CMD_SHARINGSOCKET : File sharing is now open");
                        break;
                    
                    case "CMD_SENDFILE":
                        serverMainForm.appendMessage("CMD_SENDFILE : Client sending a file...");
                        /*
                        Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Consignee]  from: Sender Format
                        Format: CMD_SENDFILE [Filename] [Size] [Consignee] to Receiver Format
                        */
                        String file_name = stringTokenizer.nextToken();
                        String filesize = stringTokenizer.nextToken();
                        String sendto = stringTokenizer.nextToken();
                        String consignee = stringTokenizer.nextToken();
                        serverMainForm.appendMessage("CMD_SENDFILE : From: "+ consignee);
                        serverMainForm.appendMessage("CMD_SENDFILE : To: "+ sendto);
                        /**  Get the client Socket **/
                        serverMainForm.appendMessage("CMD_SENDFILE : preparing connections..");
                        Socket cSock = serverMainForm.getClientFileSharingSocket(sendto); /* Consignee Socket  */
                        /*   Now Check if the consignee socket was exists.   */
                        if(cSock != null){ /* Exists   */
                            try {
                                serverMainForm.appendMessage("CMD_SENDFILE : Connected..!");
                                /** First Write the filename..  **/
                                serverMainForm.appendMessage("CMD_SENDFILE : Sending file to client...");
                                DataOutputStream cDataOutputStream = new DataOutputStream(cSock.getOutputStream());
                                cDataOutputStream.writeUTF("CMD_SENDFILE " + file_name + " " + filesize + " " + consignee);
                                /** Second send now the file content  **/
                                InputStream input = socket.getInputStream();
                                try (OutputStream sendFile = cSock.getOutputStream()) {
                                    byte[] buffer = new byte[BUFFER_SIZE];
                                    int cnt;
                                    while((cnt = input.read(buffer)) > 0){
                                        sendFile.write(buffer, 0, cnt);
                                    }
                                    sendFile.flush();
                                }
                                /** Remove client list **/
                                serverMainForm.removeClientFileSharing(sendto);
                                serverMainForm.removeClientFileSharing(consignee);
                                serverMainForm.appendMessage("CMD_SENDFILE : File was send to client...");
                            } catch (IOException iOException) {
                                serverMainForm.appendMessage("[CMD_SENDFILE]: " + iOException.getMessage());
                            }
                        }else{ /*   Not exists, return error  */
                            /*   FORMAT: CMD_SENDFILEERROR  */
                            serverMainForm.removeClientFileSharing(consignee);
                            serverMainForm.appendMessage("CMD_SENDFILE : Client '" + sendto + "' was not found.!");
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            dataOutputStream.writeUTF("CMD_SENDFILEERROR " + "Client '" + sendto + "' was not found, File Sharing will exit.");
                        }                        
                        break;
                        
                        
                    case "CMD_SENDFILERESPONSE":
                        /*
                        Format: CMD_SENDFILERESPONSE [username] [Message]
                        */
                        String receiver = stringTokenizer.nextToken(); // get the receiver username
                        String rMsg = ""; // get the error message
                        serverMainForm.appendMessage("[CMD_SENDFILERESPONSE]: username: "+ receiver);
                        while(stringTokenizer.hasMoreTokens()){
                            rMsg = rMsg + " " +stringTokenizer.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) serverMainForm.getClientFileSharingSocket(receiver);
                            DataOutputStream rDataOutputStream = new DataOutputStream(rSock.getOutputStream());
                            rDataOutputStream.writeUTF("CMD_SENDFILERESPONSE" + " " + receiver + " " + rMsg);
                        } catch (IOException iOException) {
                            serverMainForm.appendMessage("[CMD_SENDFILERESPONSE]: " + iOException.getMessage());
                        }
                        break;
                        
                      // Format: CMD_SEND_FILE_XD [sender] [receiver]     
                    case "CMD_SEND_FILE_XD":                       
                        try {
                            String send_sender = stringTokenizer.nextToken();
                            String send_receiver = stringTokenizer.nextToken();
                            String send_filename = stringTokenizer.nextToken();
                            serverMainForm.appendMessage("[CMD_SEND_FILE_XD]: Host: " + send_sender);
                            this.createConnection(send_receiver, send_sender, send_filename);
                        } catch (Exception exception) {
                            serverMainForm.appendMessage("[CMD_SEND_FILE_XD]: " + exception.getLocalizedMessage());
                        }
                        break;
                        
                    // Format:  CMD_SEND_FILE_ERROR [receiver] [Message]    
                    case "CMD_SEND_FILE_ERROR":  
                        String eReceiver = stringTokenizer.nextToken();
                        String eMsg = "";
                        while(stringTokenizer.hasMoreTokens()){
                            eMsg = eMsg + " " + stringTokenizer.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket eSock = serverMainForm.getClientFileSharingSocket(eReceiver); // get the file sharing host socket for connection
                            DataOutputStream eDataOutputStream = new DataOutputStream(eSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ERROR [Message]
                            eDataOutputStream.writeUTF("CMD_RECEIVE_FILE_ERROR " + eMsg);
                        } catch (IOException iOException) {
                            serverMainForm.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + iOException.getMessage());
                        }
                        break;
                        
                    // Format:  CMD_SEND_FILE_ACCEPT [receiver] [Message]
                    case "CMD_SEND_FILE_ACCEPT": 
                        String aReceiver = stringTokenizer.nextToken();
                        String aMsg = "";
                        while(stringTokenizer.hasMoreTokens()){
                            aMsg = aMsg + " " + stringTokenizer.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket aSock = serverMainForm.getClientFileSharingSocket(aReceiver); // get the file sharing host socket for connection
                            DataOutputStream aDataOutputStream = new DataOutputStream(aSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ACCEPT [Message]
                            aDataOutputStream.writeUTF("CMD_RECEIVE_FILE_ACCEPT " + aMsg);
                        } catch (IOException iOException) {
                            serverMainForm.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + iOException.getMessage());
                        }
                        break;
                    
                    case "CMD_VOICE_CHAT":
                        break;
                        
                    case "CMD_VIDEO_CHAT":
                        break;
                        
                    default: 
                        serverMainForm.appendMessage("[CMDException]: Unknown Command " + CMD);
                    break;
                }
            }
        } catch (IOException iOException) {
            /*   this is for chatting client, remove if it is exists..   */
            System.out.println(client);
            System.out.println("File Sharing: " + filesharing_username);
            System.out.println(iOException.getMessage());
            serverMainForm.removeFromTheList(client);
            if(filesharing_username != null){
                serverMainForm.removeClientFileSharing(filesharing_username);
            }
            serverMainForm.appendMessage("[SocketThread]: Client connection closed..!");
        }
    }
    
}
