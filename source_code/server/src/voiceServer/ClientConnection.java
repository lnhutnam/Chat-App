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


package voiceServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Le Nhut Nam
 */
public class ClientConnection extends Thread{

    private VoiceServer serv; //instance of server, needed to put messages in the server's broadcast queue
    private Socket s; //connection to client 
    private ObjectInputStream in; //object streams to/from client
    private ObjectOutputStream out;
    private long chId; //unique id of this client, generated in the costructor
    private ArrayList<VoiceMessage> toSend = new ArrayList<VoiceMessage>(); //queue of messages to be sent to the client

    public InetAddress getInetAddress() { //returns this client's ip address
        return s.getInetAddress();
    }

    public int getPort() { //returns this client's tcp port
        return s.getPort();
    }

    public long getChId() { //return this client's unique id
        return chId;
    }

    public ClientConnection(VoiceServer serv, Socket s) {
        this.serv = serv;
        this.s = s;
        byte[] addr = s.getInetAddress().getAddress();
        chId = (addr[0] << 48 | addr[1] << 32 | addr[2] << 24 | addr[3] << 16) + s.getPort(); //generate unique chId from client's IP and port
    }

    public void addToQueue(VoiceMessage m) { //add a message to send to the client
        try {
            toSend.add(m);
        } catch (Throwable t) {
            //mutex error, ignore because the server must be as fast as possible
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(s.getOutputStream()); //create object streams to/from client
            in = new ObjectInputStream(s.getInputStream());
        } catch (IOException ex) { //connection error, close connection
            try {
                s.close();
                VoiceServerLog.add("ERROR " + getInetAddress() + ":" + getPort() + " " + ex);
            } catch (IOException ex1) {
            }
            stop();
        }
        for (;;) {
            try {
                if (s.getInputStream().available() > 0) { //we got something from the client
                    VoiceMessage toBroadcast = (VoiceMessage) in.readObject(); //read data from client
                    if (toBroadcast.getChId() == -1) { //set its chId and timestamp and pass it to the server
                        toBroadcast.setChId(chId);
                        toBroadcast.setTimestamp(System.nanoTime() / 1000000L);
                        serv.addToBroadcastQueue(toBroadcast);
                    } else {
                        continue; //invalid message
                    }
                }
                try {
                    if (!toSend.isEmpty()) {
                        VoiceMessage toClient = toSend.get(0); //we got something to send to the client
                        if (!(toClient.getData() instanceof SoundPacket) || toClient.getTimestamp() + toClient.getTtl() < System.nanoTime() / 1000000L) { //is the message too old or of an unknown type?
                            VoiceServerLog.add("dropping packet from " + toClient.getChId() + " to " + chId);
                            continue;
                        }
                        out.writeObject(toClient); //send the message
                        toSend.remove(toClient); //and remove it from the queue
                    } else {
                        VoiceServerUtil.sleep(10); //avoid busy wait
                    }
                } catch (Throwable t) {
                    if (t instanceof IOException) {//connection closed or connection error
                        throw (Exception) t;
                    } else {//mutex error, try again
                        System.out.println("cc fixmutex");
                        continue;
                    }
                }
            } catch (Exception ex) { //connection closed or connection error, kill thread
                try {
                    s.close();
                } catch (IOException ex1) {
                }
                stop();
            }
        }

    }    
}
