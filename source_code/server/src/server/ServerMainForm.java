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

import java.awt.Desktop;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Le Nhut Nam
 */
public class ServerMainForm extends javax.swing.JFrame {
 
    // Properties
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
    Thread thread;
    ServerThread serverThread;
    /** Chat List  **/
    public Vector socketList = new Vector();
    public Vector clientList = new Vector();
    /** File Sharing List **/
    public Vector clientFileSharingUsername = new Vector();
    public Vector clientFileSharingSocket = new Vector();
    /** Server **/
    ServerSocket server;
    
    /**
     * Creates new form MainForm
     */
    public ServerMainForm() {
        initComponents();
    }
    
    /**
     * 
     * @param msg 
     */
    public void appendMessage(String msg){
        Date date = new Date();
        jTextAreaChatServerConsole.append(simpleDateFormat.format(date) + ": " + msg + "\n");
        jTextAreaChatServerConsole.setCaretPosition(jTextAreaChatServerConsole.getText().length() - 1);
    }
    
    /**
     * 
     * @param msg 
     */
    public void appendMessageVoiceChatConsole(String msg){
        Date date = new Date();
        jTextAreaVoiceChatConsole.append(simpleDateFormat.format(date) + ": " + msg + "\n");
        jTextAreaVoiceChatConsole.setCaretPosition(jTextAreaVoiceChatConsole.getText().length() - 1);
    }
    
    /**
     * 
     * @param msg 
     */
    public void appendMessageVideoChatConsole(String msg){
        Date date = new Date();
        jTextAreaVideoChatConsole.append(simpleDateFormat.format(date) + ": " + msg + "\n");
        jTextAreaVideoChatConsole.setCaretPosition(jTextAreaVideoChatConsole.getText().length() - 1);       
    }
    
    /** Setters **/
    /**
     * 
     * @param socket 
     */
    public void setSocketList(Socket socket){
        try {
            socketList.add(socket);
            appendMessage("[setSocketList]: Added");
        } catch (Exception exception) { 
            appendMessage("[setSocketList]: " + exception.getMessage()); 
        }
    }
    
    /**
     * 
     * @param client 
     */
    public void setClientList(String client){
        try {
            clientList.add(client);
            appendMessage("[setClientList]: Added");
        } catch (Exception exception) { 
            appendMessage("[setClientList]: " + exception.getMessage()); 
        }
    }
    
    /**
     * 
     * @param user 
     */
    public void setClientFileSharingUsername(String user){
        try {
            clientFileSharingUsername.add(user);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
    
    /**
     * 
     * @param soc 
     */
    public void setClientFileSharingSocket(Socket soc){
        try {
            clientFileSharingSocket.add(soc);
        } catch (Exception exception) { 
            System.out.println(exception.getMessage());
        }
    }
    
    /** Getters
     * 
     * @param client
     * @return  **/
    public Socket getClientList(String client){
        Socket tsoc = null;
        for(int i = 0; i < clientList.size(); i++){
            if(clientList.get(i).equals(client)){
                tsoc = (Socket) socketList.get(i);
                break;
            }
        }
        return tsoc;
    }
    
    
    /**
     * 
     * @param client 
     */
    public void removeFromTheList(String client){
        try {
            for(int i = 0; i < clientList.size(); i++){
                if(clientList.elementAt(i).equals(client)){
                    clientList.removeElementAt(i);
                    socketList.removeElementAt(i);
                    appendMessage("[Removed]: " + client);
                    break;
                }
            }
        } catch (Exception exception) {
            appendMessage("[RemovedException]: " +  exception.getMessage());
        }
    }
    
    /**
     * 
     * @param username
     * @return 
     */
    public Socket getClientFileSharingSocket(String username){
        Socket tsoc = null;
        for(int i = 0; i < clientFileSharingUsername.size(); i++){
            if(clientFileSharingUsername.elementAt(i).equals(username)){
                tsoc = (Socket) clientFileSharingSocket.elementAt(i);
                break;
            }
        }
        return tsoc;
    }
    
    
    /*
    Remove Client File Sharing List
    */
    /**
     * 
     * @param username 
     */
    public void removeClientFileSharing(String username){
        for(int i = 0; i < clientFileSharingUsername.size(); i++){
            if(clientFileSharingUsername.elementAt(i).equals(username)){
                try {
                    Socket rSock = getClientFileSharingSocket(username);
                    if(rSock != null){
                        rSock.close();
                    }
                    clientFileSharingUsername.removeElementAt(i);
                    clientFileSharingSocket.removeElementAt(i);
                    appendMessage("[FileSharing]: Removed "+ username);
                } catch (IOException iOException) {
                    appendMessage("[FileSharing]: " + iOException.getMessage());
                    appendMessage("[FileSharing]: Unable to Remove "+ username);
                }
                break;
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    String getIPAddress(){
        return jSpinnerFirstByte.getValue().toString() 
                + "." + jSpinnerSecondByte.getValue().toString() 
                + "." +jSpinnerThirdByte.getValue().toString() 
                + "." + jSpinnerFourthByte.getValue().toString();
    }

    /**
     * 
     * @return 
     */
    private Integer getPort(){
        return (Integer) jSpinnerPortNumber.getValue();
    }    
    
    /**
     * 
     * @return 
     */
    String getIPAddressVoiceChatServerControl(){
        return jSpinnerFirstByteIpVoiceChat.getValue().toString() 
                + "." + jSpinnerSecondByteIpVoiceChat.getValue().toString() 
                + "." +jSpinnerThirdByteIpVoiceChat.getValue().toString() 
                + "." + jSpinnerFourthByteIpVoiceChat.getValue().toString();
    }

    /**
     * 
     * @return 
     */
    private Integer getPortVoiceChatServerControl(){
        return (Integer) jSpinnerPortNumberVoiceChat.getValue();
    } 
    
    
    /**
     * 
     * @return 
     */
    String getIPAddressVideoChatServerControl(){
        return jSpinnerFirstByteIpVideoChat.getValue().toString() 
                + "." + jSpinnerSecondByteIpVideoChat.getValue().toString() 
                + "." +jSpinnerThirdByteIpVideoChat.getValue().toString() 
                + "." + jSpinnerFourthByteIpVideoChat.getValue().toString();
    }

    /**
     * 
     * @return 
     */
    private Integer getPortVideoChatServerControl(){
        return (Integer) jSpinnerPortNumberVideoChat.getValue();
    } 
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonStartChatServer = new javax.swing.JButton();
        jButtonStopChatServer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaChatServerConsole = new javax.swing.JTextArea();
        jButtonTestChatServer = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSpinnerFirstByte = new javax.swing.JSpinner();
        jSpinnerSecondByte = new javax.swing.JSpinner();
        jSpinnerThirdByte = new javax.swing.JSpinner();
        jSpinnerFourthByte = new javax.swing.JSpinner();
        jSpinnerPortNumber = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSpinnerFirstByteIpVoiceChat = new javax.swing.JSpinner();
        jSpinnerSecondByteIpVoiceChat = new javax.swing.JSpinner();
        jSpinnerThirdByteIpVoiceChat = new javax.swing.JSpinner();
        jSpinnerFourthByteIpVoiceChat = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jSpinnerPortNumberVoiceChat = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaVoiceChatConsole = new javax.swing.JTextArea();
        jButtonStartVoiceChatServer = new javax.swing.JButton();
        jButtonTestVoiceChatServer = new javax.swing.JButton();
        jButtonStopVoiceChatServer = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSpinnerFirstByteIpVideoChat = new javax.swing.JSpinner();
        jSpinnerSecondByteIpVideoChat = new javax.swing.JSpinner();
        jSpinnerThirdByteIpVideoChat = new javax.swing.JSpinner();
        jSpinnerFourthByteIpVideoChat = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jSpinnerPortNumberVideoChat = new javax.swing.JSpinner();
        jButtonVideoChatStartServer = new javax.swing.JButton();
        jButtonTestVideoChatServer = new javax.swing.JButton();
        jButtonStopVideoServer = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaVideoChatConsole = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuAboutServer = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server Control Panel");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButtonStartChatServer.setBackground(new java.awt.Color(0, 102, 255));
        jButtonStartChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonStartChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-start-24.png"))); // NOI18N
        jButtonStartChatServer.setText("Start Server");
        jButtonStartChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartChatServerActionPerformed(evt);
            }
        });

        jButtonStopChatServer.setBackground(new java.awt.Color(255, 51, 51));
        jButtonStopChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonStopChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-stop-sign-24.png"))); // NOI18N
        jButtonStopChatServer.setText("Stop Server");
        jButtonStopChatServer.setEnabled(false);
        jButtonStopChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopChatServerActionPerformed(evt);
            }
        });

        jTextAreaChatServerConsole.setEditable(false);
        jTextAreaChatServerConsole.setBackground(new java.awt.Color(0, 0, 0));
        jTextAreaChatServerConsole.setColumns(20);
        jTextAreaChatServerConsole.setFont(new java.awt.Font("Consolas", 0, 13)); // NOI18N
        jTextAreaChatServerConsole.setForeground(new java.awt.Color(51, 255, 0));
        jTextAreaChatServerConsole.setRows(5);
        jTextAreaChatServerConsole.setPreferredSize(null);
        jScrollPane1.setViewportView(jTextAreaChatServerConsole);

        jButtonTestChatServer.setBackground(new java.awt.Color(204, 204, 0));
        jButtonTestChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonTestChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-test-program-24.png"))); // NOI18N
        jButtonTestChatServer.setText("Test Server");
        jButtonTestChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestChatServerActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel1.setText("IP Address");

        jLabel2.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel2.setText("Port Number");

        jSpinnerFirstByte.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerFirstByte.setModel(new javax.swing.SpinnerNumberModel(127, 0, 255, 1));

        jSpinnerSecondByte.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerSecondByte.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jSpinnerThirdByte.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerThirdByte.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jSpinnerFourthByte.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerFourthByte.setModel(new javax.swing.SpinnerNumberModel(1, 0, 255, 1));

        jSpinnerPortNumber.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerPortNumber.setModel(new javax.swing.SpinnerNumberModel(4000, 1, 65536, 1));

        jLabel3.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 18)); // NOI18N
        jLabel3.setText("Voice Chat control area");

        jLabel4.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 18)); // NOI18N
        jLabel4.setText("Chat control area");

        jLabel5.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel5.setText("IP Address");

        jSpinnerFirstByteIpVoiceChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerFirstByteIpVoiceChat.setModel(new javax.swing.SpinnerNumberModel(127, 0, 255, 1));

        jSpinnerSecondByteIpVoiceChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerSecondByteIpVoiceChat.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jSpinnerThirdByteIpVoiceChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerThirdByteIpVoiceChat.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jSpinnerFourthByteIpVoiceChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerFourthByteIpVoiceChat.setModel(new javax.swing.SpinnerNumberModel(1, 0, 255, 1));

        jLabel6.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel6.setText("Port Number");

        jSpinnerPortNumberVoiceChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerPortNumberVoiceChat.setModel(new javax.swing.SpinnerNumberModel(4006, 1, 65536, 1));

        jTextAreaVoiceChatConsole.setEditable(false);
        jTextAreaVoiceChatConsole.setBackground(new java.awt.Color(0, 0, 0));
        jTextAreaVoiceChatConsole.setColumns(20);
        jTextAreaVoiceChatConsole.setFont(new java.awt.Font("Consolas", 0, 13)); // NOI18N
        jTextAreaVoiceChatConsole.setForeground(new java.awt.Color(51, 255, 0));
        jTextAreaVoiceChatConsole.setRows(5);
        jScrollPane2.setViewportView(jTextAreaVoiceChatConsole);

        jButtonStartVoiceChatServer.setBackground(new java.awt.Color(0, 102, 255));
        jButtonStartVoiceChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonStartVoiceChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-start-24.png"))); // NOI18N
        jButtonStartVoiceChatServer.setText("Start Server");
        jButtonStartVoiceChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartVoiceChatServerActionPerformed(evt);
            }
        });

        jButtonTestVoiceChatServer.setBackground(new java.awt.Color(204, 204, 0));
        jButtonTestVoiceChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonTestVoiceChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-test-program-24.png"))); // NOI18N
        jButtonTestVoiceChatServer.setText("Test Server");
        jButtonTestVoiceChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestVoiceChatServerActionPerformed(evt);
            }
        });

        jButtonStopVoiceChatServer.setBackground(new java.awt.Color(255, 51, 51));
        jButtonStopVoiceChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonStopVoiceChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-stop-sign-24.png"))); // NOI18N
        jButtonStopVoiceChatServer.setText("Stop Server");
        jButtonStopVoiceChatServer.setEnabled(false);
        jButtonStopVoiceChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopVoiceChatServerActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 18)); // NOI18N
        jLabel7.setText("Video Chat control area");

        jLabel8.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel8.setText("IP Address");

        jSpinnerFirstByteIpVideoChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerFirstByteIpVideoChat.setModel(new javax.swing.SpinnerNumberModel(127, 0, 255, 1));

        jSpinnerSecondByteIpVideoChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerSecondByteIpVideoChat.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jSpinnerThirdByteIpVideoChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerThirdByteIpVideoChat.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        jSpinnerFourthByteIpVideoChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerFourthByteIpVideoChat.setModel(new javax.swing.SpinnerNumberModel(1, 0, 255, 1));

        jLabel9.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel9.setText("Port Number");

        jSpinnerPortNumberVideoChat.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 13)); // NOI18N
        jSpinnerPortNumberVideoChat.setModel(new javax.swing.SpinnerNumberModel(4007, 1, 65536, 1));

        jButtonVideoChatStartServer.setBackground(new java.awt.Color(0, 102, 255));
        jButtonVideoChatStartServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonVideoChatStartServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-start-24.png"))); // NOI18N
        jButtonVideoChatStartServer.setText("Start Server");
        jButtonVideoChatStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVideoChatStartServerActionPerformed(evt);
            }
        });

        jButtonTestVideoChatServer.setBackground(new java.awt.Color(204, 204, 0));
        jButtonTestVideoChatServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonTestVideoChatServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-test-program-24.png"))); // NOI18N
        jButtonTestVideoChatServer.setText("Test Server");
        jButtonTestVideoChatServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestVideoChatServerActionPerformed(evt);
            }
        });

        jButtonStopVideoServer.setBackground(new java.awt.Color(255, 51, 51));
        jButtonStopVideoServer.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jButtonStopVideoServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-stop-sign-24.png"))); // NOI18N
        jButtonStopVideoServer.setText("Stop Server");
        jButtonStopVideoServer.setEnabled(false);
        jButtonStopVideoServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopVideoServerActionPerformed(evt);
            }
        });

        jTextAreaVideoChatConsole.setEditable(false);
        jTextAreaVideoChatConsole.setBackground(new java.awt.Color(0, 0, 0));
        jTextAreaVideoChatConsole.setColumns(20);
        jTextAreaVideoChatConsole.setFont(new java.awt.Font("Consolas", 0, 13)); // NOI18N
        jTextAreaVideoChatConsole.setForeground(new java.awt.Color(51, 255, 0));
        jTextAreaVideoChatConsole.setRows(5);
        jScrollPane3.setViewportView(jTextAreaVideoChatConsole);

        jLabel10.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 18)); // NOI18N
        jLabel10.setText("Development Information");

        jLabel24.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel24.setText("Java 8 - Apache Netbeans 12 LTS");

        jLabel25.setFont(new java.awt.Font("DejaVu Sans Condensed", 0, 14)); // NOI18N
        jLabel25.setText("2020 (c) Open Source");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/fit-hcmus-150.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerPortNumberVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonStartVoiceChatServer)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonTestVoiceChatServer)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButtonStopVoiceChatServer))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerFirstByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jSpinnerSecondByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(jSpinnerThirdByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(jSpinnerFourthByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerPortNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonStartChatServer)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonTestChatServer)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButtonStopChatServer))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(jSpinnerFirstByte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(jSpinnerSecondByte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(jSpinnerThirdByte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerFourthByte, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jSpinnerPortNumberVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(jButtonVideoChatStartServer)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonTestVideoChatServer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonStopVideoServer, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerFirstByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerSecondByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerThirdByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jSpinnerFourthByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinnerFirstByte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerSecondByte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerThirdByte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerFourthByte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jSpinnerPortNumber)
                                .addComponent(jButtonStartChatServer)
                                .addComponent(jButtonTestChatServer)
                                .addComponent(jButtonStopChatServer)
                                .addComponent(jButtonVideoChatStartServer))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(33, 33, 33)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSpinnerFirstByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSpinnerSecondByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSpinnerThirdByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jSpinnerFourthByteIpVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonTestVideoChatServer)
                                .addComponent(jButtonStopVideoServer)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerPortNumberVideoChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinnerFirstByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerSecondByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerThirdByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinnerFourthByteIpVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSpinnerPortNumberVoiceChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonStartVoiceChatServer)
                            .addComponent(jButtonTestVoiceChatServer)
                            .addComponent(jButtonStopVoiceChatServer)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-administrative-tools-24.png"))); // NOI18N
        jMenu1.setText("Tools");
        jMenuBar1.add(jMenu1);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-contact-us-24.png"))); // NOI18N
        jMenu2.setText("Contacts");

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-documents-24.png"))); // NOI18N
        jMenuItem3.setText("Online docs");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-help-24.png"))); // NOI18N
        jMenuItem1.setText("Help & Support");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuAboutServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-about-24.png"))); // NOI18N
        jMenuAboutServer.setText("About");
        jMenuAboutServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutServerActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuAboutServer);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStartChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartChatServerActionPerformed
        // TODO add your handling code here:
        int port = this.getPort();
        serverThread = new ServerThread(port, this);
        thread = new Thread(serverThread);
        thread.start();
        
        new Thread(new OnlineListThread(this)).start();
        
        jButtonStartChatServer.setEnabled(false);
        jButtonTestChatServer.setEnabled(false);
        jButtonStopChatServer.setEnabled(true);
    }//GEN-LAST:event_jButtonStartChatServerActionPerformed

    private void jButtonStopChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopChatServerActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Close Server.?");
        if(confirm == 0){
            serverThread.stop();
        }
        jButtonStartChatServer.setEnabled(true);
        jButtonTestChatServer.setEnabled(true);
        jButtonStopChatServer.setEnabled(false);
    }//GEN-LAST:event_jButtonStopChatServerActionPerformed

    private void jButtonTestChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestChatServerActionPerformed
        // TODO add your handling code here:
        int port = this.getPort();
        String ip = this.getIPAddress();
        appendMessage("[Server]: Prepare running at " + ip + " in port " + port);
    }//GEN-LAST:event_jButtonTestChatServerActionPerformed

    private void jButtonStartVoiceChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartVoiceChatServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonStartVoiceChatServerActionPerformed

    private void jButtonTestVoiceChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestVoiceChatServerActionPerformed
        // TODO add your handling code here:
        int port = this.getPortVoiceChatServerControl();
        String ip = this.getIPAddressVoiceChatServerControl();
        appendMessageVoiceChatConsole("[Voice Server]: Prepare running at " + ip + " in port " + port);
    }//GEN-LAST:event_jButtonTestVoiceChatServerActionPerformed

    private void jButtonStopVoiceChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopVoiceChatServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonStopVoiceChatServerActionPerformed

    private void jButtonVideoChatStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVideoChatStartServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonVideoChatStartServerActionPerformed

    private void jButtonTestVideoChatServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestVideoChatServerActionPerformed
        // TODO add your handling code here:
        int port = this.getPortVideoChatServerControl();
        String ip = this.getIPAddressVideoChatServerControl();
        appendMessageVideoChatConsole("[Video Server]: Prepare running at " + ip + " in port " + port);
    }//GEN-LAST:event_jButtonTestVideoChatServerActionPerformed

    private void jButtonStopVideoServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopVideoServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonStopVideoServerActionPerformed

    private void jMenuAboutServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutServerActionPerformed
        // TODO add your handling code here:
        String information = "Coder: Le Nhut Nam\nChat App Project (c) July 2020\nMIT License\nOpen Source";
        JOptionPane.showMessageDialog(this, information, "About", JOptionPane.DEFAULT_OPTION);      
    }//GEN-LAST:event_jMenuAboutServerActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                try {
                    Desktop.getDesktop().browse(new URI("https://lenhutnam298.github.io/chat-app/index.html"));
                } catch (IOException ex) {
                    Logger.getLogger(ServerMainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (URISyntaxException ex) {
                Logger.getLogger(ServerMainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException classNotFoundException) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, classNotFoundException);
        } catch (InstantiationException instantiationException) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, instantiationException);
        } catch (IllegalAccessException illegalAccessException) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, illegalAccessException);
        } catch (javax.swing.UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, unsupportedLookAndFeelException);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            ServerMainForm serverMainForm =  new ServerMainForm();
            ImageIcon img = new ImageIcon("src/icons/icons8-server-96.png");
            serverMainForm.setIconImage(img.getImage());
            serverMainForm.setLocationRelativeTo(null);;
            serverMainForm.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonStartChatServer;
    private javax.swing.JButton jButtonStartVoiceChatServer;
    private javax.swing.JButton jButtonStopChatServer;
    private javax.swing.JButton jButtonStopVideoServer;
    private javax.swing.JButton jButtonStopVoiceChatServer;
    private javax.swing.JButton jButtonTestChatServer;
    private javax.swing.JButton jButtonTestVideoChatServer;
    private javax.swing.JButton jButtonTestVoiceChatServer;
    private javax.swing.JButton jButtonVideoChatStartServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuAboutServer;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSpinner jSpinnerFirstByte;
    private javax.swing.JSpinner jSpinnerFirstByteIpVideoChat;
    private javax.swing.JSpinner jSpinnerFirstByteIpVoiceChat;
    private javax.swing.JSpinner jSpinnerFourthByte;
    private javax.swing.JSpinner jSpinnerFourthByteIpVideoChat;
    private javax.swing.JSpinner jSpinnerFourthByteIpVoiceChat;
    private javax.swing.JSpinner jSpinnerPortNumber;
    private javax.swing.JSpinner jSpinnerPortNumberVideoChat;
    private javax.swing.JSpinner jSpinnerPortNumberVoiceChat;
    private javax.swing.JSpinner jSpinnerSecondByte;
    private javax.swing.JSpinner jSpinnerSecondByteIpVideoChat;
    private javax.swing.JSpinner jSpinnerSecondByteIpVoiceChat;
    private javax.swing.JSpinner jSpinnerThirdByte;
    private javax.swing.JSpinner jSpinnerThirdByteIpVideoChat;
    private javax.swing.JSpinner jSpinnerThirdByteIpVoiceChat;
    private javax.swing.JTextArea jTextAreaChatServerConsole;
    private javax.swing.JTextArea jTextAreaVideoChatConsole;
    private javax.swing.JTextArea jTextAreaVoiceChatConsole;
    // End of variables declaration//GEN-END:variables


}
