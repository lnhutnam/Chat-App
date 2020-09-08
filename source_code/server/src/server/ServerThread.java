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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Le Nhut Nam
 */
public class ServerThread implements Runnable {
    
    ServerSocket server;
    int backlog = 500;
    ServerMainForm serverMainForm;
    boolean keepGoing = true;
    
    public ServerThread(int port, ServerMainForm serverMainForm){
        serverMainForm.appendMessage("[Server]: Starting server in port " + port);
        try {
            this.serverMainForm = serverMainForm;
            server = new ServerSocket(port, this.backlog, InetAddress.getByName("127.0.0.1"));
            serverMainForm.appendMessage("[Server]: Host:" 
                    + InetAddress.getLocalHost()
                    + ",IP:" + server.getInetAddress()
                    + ",Port:" + port 
                    + ",Backlog:" + backlog);
            serverMainForm.appendMessage("[Server]: Server started.!");
        } 
        catch (IOException iOException) { 
            serverMainForm.appendMessage("[IOException]: " + iOException.getMessage()); 
        }
    }
    
    public ServerThread(int port, int backlog, String ipAddress, ServerMainForm serverMainForm){
        serverMainForm.appendMessage("[Server]: Starting server in port " + port);
        try {
            this.serverMainForm = serverMainForm;
            server = new ServerSocket(port, backlog, InetAddress.getByName(ipAddress));
            serverMainForm.appendMessage("[Server]: IP:" 
                    + server.getInetAddress().toString() 
                    + ",Port:" + port 
                    + ",Backlog:" + backlog);
            serverMainForm.appendMessage("[Server]: Server started.!");
        } 
        catch (IOException iOException) { 
            serverMainForm.appendMessage("[IOException]: " + iOException.getMessage()); 
        }
    }
     
     
    public ServerThread(int port, int backlog, ServerMainForm serverMainForm){
        serverMainForm.appendMessage("[Server]: Starting server in port " + port);
        try {
            this.serverMainForm = serverMainForm;
            server = new ServerSocket(port, backlog, InetAddress.getByName("127.0.0.1"));
            serverMainForm.appendMessage("[Server]: IP:" 
                    + server.getInetAddress().toString() 
                    + ",Port:" + port +",Backlog:" 
                    + backlog);
            serverMainForm.appendMessage("[Server]: Server started.!");
        } 
        catch (IOException iOException) { 
            serverMainForm.appendMessage("[IOException]: " + iOException.getMessage()); 
        }
    }
     
     

    @Override
    public void run() {
        try {
            while(keepGoing){
                Socket socket = server.accept();
                serverMainForm.appendMessage("[Socket]: " + socket);
                /** SOcket thread **/
                new Thread(new SocketThread(socket, serverMainForm)).start();
            }
        } catch (IOException iOException) {
            serverMainForm.appendMessage("[ServerThreadIOException]: " + iOException.getMessage());
        }
    }
    
    
    public void stop(){
        try {
            server.close();
            keepGoing = false;
            System.out.println("Server is now closed..!");
        } catch (IOException iOException) {
            System.out.println(iOException.getMessage());
        }
    }
    
}
