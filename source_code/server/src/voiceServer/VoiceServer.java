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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;
import server.ServerMainForm;

/**
 *
 * @author Le Nhut Nam
 */
public class VoiceServer {
   
    private ArrayList<VoiceMessage> broadCastQueue = new ArrayList<VoiceMessage>();   
    private ArrayList<VoiceMessage> uniCastQueue = new ArrayList<VoiceMessage>();
    private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>();
    private ArrayList<ClientConnection> uniCastClients = new ArrayList<ClientConnection>();

    private int port;
    
    // when upnp is enabled, this points to the upnp service
    private UpnpService u; 
    
    ServerMainForm serverMainForm;
    
    /**
     * 
     * @param m 
     */
    public void addToBroadcastQueue(VoiceMessage m) { //add a message to the broadcast queue. this method is used by all ClientConnection instances
        try {
            broadCastQueue.add(m);
        } catch (Throwable t) {
            //mutex error, try again
            VoiceServerUtil.sleep(1);
            addToBroadcastQueue(m);
        }
    }
    
    /**
     * 
     * @param voiceMessage 
     */
    public void addToUnicastQueue(VoiceMessage voiceMessage){
         try {
            broadCastQueue.add(voiceMessage);
        } catch (Throwable t) {
            //mutex error, try again
            VoiceServerUtil.sleep(1);
            addToBroadcastQueue(voiceMessage);
        }
    }
    
    
    private ServerSocket s;
    
    /**
     * 
     * @param port
     * @param upnp
     * @throws Exception 
     */
    public VoiceServer(int port, boolean upnp) throws Exception{
        this.port = port;
        if(upnp){
            VoiceServerLog.add("Strating...");
            //first we need the address of this machine on the local network
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException ex) {
                VoiceServerLog.add("Network error");
                throw new Exception("Network error");
            }
            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            try {
                net = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                VoiceServerLog.add("Not connected to any network");
                throw new Exception("Network error");
            }

            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {
                        if (ip.isSiteLocalAddress()) {
                            ipAddress = ip.getHostAddress();
                            break;
                        }
                    }
                }
                if (ipAddress != null) {
                    break;
                }
            }
            if (ipAddress == null) {
                VoiceServerLog.add("Not connected to any IPv4 network");
                throw new Exception("Network error");
            }
            u = new UpnpServiceImpl(new PortMappingListener(new PortMapping(port, ipAddress, PortMapping.Protocol.TCP)));
            u.getControlPoint().search();
        }
        try {
            s = new ServerSocket(port); //listen on specified port
            port = s.getLocalPort();
	    VoiceServerLog.add("Boom.. Voice Server started.\nIP : " + InetAddress.getLocalHost().toString() + ", Port : " + s.getLocalPort());
            VoiceServerLog.add("Waiting for friends...");
        } catch (IOException ex) {
            VoiceServerLog.add("Server error " + ex + "(port " + port + ")");
            throw new Exception("Error " + ex);
        }
        // create a BroadcastThread and start it
        new BroadcastThread().start(); 
        // create a UnicastThread and start it
        new UnicastThread().start();;
        //accept all incoming connection
        for (;;) { 
            try {
                Socket c = s.accept();
                ClientConnection cc = new ClientConnection(this, c); //create a ClientConnection thread
                cc.start();
                addToClients(cc);
                VoiceServerLog.add("new client " + c.getInetAddress() + ":" + c.getPort() + " on port " + port);
            } catch (IOException ex) {
            }
        }
    }
    
    public VoiceServer(String ipAddress, int port, boolean upnp){
        
    }

    /**
     * 
     * @param cc 
     */
    private void addToClients(ClientConnection cc) {
        try {
            clients.add(cc); //add the new connection to the list of connections
        } catch (Throwable t) {
            //mutex error, try again
            VoiceServerUtil.sleep(1);
            addToClients(cc);
        }
    }
    
    /**
     * 
     * @param clientConnection 
     */
    private void addToUnicastClients(ClientConnection clientConnection){
        if(this.uniCastClients.size() < 3){
            try {
                uniCastClients.add(clientConnection);
            }catch (Throwable t) {
                //mutex error, try again
                VoiceServerUtil.sleep(1);
                addToUnicastClients(clientConnection);
            }
        }
    }

    /**
     * broadcasts messages to each ClientConnection, and removes dead ones
     */
    private class BroadcastThread extends Thread {
        
        /**
         * 
         */
        public BroadcastThread() {
        }
        
        /**
         * 
         */
        @Override
        public void run() {
            for (;;) {
                try {
                    // Create a list of dead connections
                    ArrayList<ClientConnection> toRemove = new ArrayList<ClientConnection>(); 
                    for (ClientConnection cc : clients) {
                        // Connection is dead, need to be removed
                        if (!cc.isAlive()) { 
                            VoiceServerLog.add("dead connection closed: " + cc.getInetAddress() + ":" + cc.getPort() + " on port " + port);
                            toRemove.add(cc);
                        }
                    }
                    // Delete all dead connections
                    clients.removeAll(toRemove); 
                     // Nothing to send
                    if (broadCastQueue.isEmpty()) {
                        // Avoid busy wait
                        VoiceServerUtil.sleep(10); 
                        continue;
                        
                    } 
                    // We got something to broadcast
                    else { 
                        VoiceMessage m = broadCastQueue.get(0);
                        // Broadcast the message
                        for (ClientConnection cc : clients) { 
                            if (cc.getChId() != m.getChId()) {
                                cc.addToQueue(m);
                            }
                        }
                        // Remove it from the broadcast queue
                        broadCastQueue.remove(m); 
                    }
                } catch (Throwable t) {
                    // Mutex error, try again
                }
            }
        }
    }
    
    /**
     * 
     */
    private class UnicastThread extends Thread {
        /**
         * 
         */
        public UnicastThread(){
            
        }
        
        /**
         * 
         */
        @Override
        public void run(){
            for (;;) {
                try {
                    // Create a list of dead connections
                    ArrayList<ClientConnection> toRemove = new ArrayList<ClientConnection>(); 
                    for (ClientConnection clientConnection : uniCastClients) {
                        // Connection is dead, need to be removed
                        if (!clientConnection.isAlive()) { 
                            VoiceServerLog.add("Dead connection closed: " + clientConnection.getInetAddress() + ":" + clientConnection.getPort() + " on port " + port);
                            toRemove.add(clientConnection);
                        }
                    }
                    // Delete all dead connections
                    uniCastClients.removeAll(toRemove); 
                    // Nothing to send
                    if (uniCastQueue.isEmpty()) { 
                        // Avoid busy wait
                        VoiceServerUtil.sleep(10); 
                        continue;
                    } else { // We got something to broadcast
                        VoiceMessage m = uniCastQueue.get(0);
                        // Unicast the message
                        for (ClientConnection clientConnection : uniCastClients) { 
                            if (clientConnection.getChId() != m.getChId()) {
                                clientConnection.addToQueue(m);
                            }
                        }
                        // Remove it from the unicast queue
                        uniCastQueue.remove(m); 
                    }
                } catch (Throwable t) {
                    // Mutex error, try again
                }
            }
        }
    }
}
