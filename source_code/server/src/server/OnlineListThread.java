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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Le Nhut Nam
 */
public class OnlineListThread implements Runnable {
    
    ServerMainForm serverMainForm;
    
    public OnlineListThread(ServerMainForm serverMainForm){
        this.serverMainForm = serverMainForm;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
                String msg = "";
                for(int i = 0; i < serverMainForm.clientList.size(); i++){
                    msg = msg + " " + serverMainForm.clientList.elementAt(i);
                }
                
                for(int i = 0; i < serverMainForm.socketList.size(); i++){
                    Socket tsoc = (Socket) serverMainForm.socketList.elementAt(i);
                    DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                    /** CMD_ONLINE [user1] [user2] [user3] **/
                    if(msg.length() > 0){
                        dos.writeUTF("CMD_ONLINE " + msg);
                    }
                }
                Thread.sleep(1900);
            }
        } catch(InterruptedException interruptedException){
            System.out.println(interruptedException.getMessage());
            serverMainForm.appendMessage("[InterruptedException]: " + interruptedException.getMessage());
        } catch (IOException iOException) {
            System.out.println(iOException.getMessage());
            serverMainForm.appendMessage("[IOException]: " + iOException.getMessage());
        }
    }
    
    
}
