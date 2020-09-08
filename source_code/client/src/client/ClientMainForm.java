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
package client;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import videoClient.ClientVideoChatForm;
import voiceClient.ClientVoiceChatBroadCastForm;
import voiceClient.ClientVoiceChatForm;

/**
 *
 * @author Le Nhut Nam
 */
public class ClientMainForm extends javax.swing.JFrame {
    
    String username;
    String host;
    int port;
    Socket socket;
    DataOutputStream dos;
    public boolean attachmentOpen = false;
    private boolean isConnected = false;
    private String mydownloadfolder = "D:\\";
    
    /**
     * Creates new form MainForm
     */
    public ClientMainForm() {
        initComponents();
        jLayeredPaneEmoji.setVisible(false);
        jLayeredPaneKaomoji.setVisible(false);
    }
    
    public void initFrame(String username, String host, int port){
        this.username = username;
        this.host = host;
        this.port = port;
        setTitle("You are logged in as: " + username);
        /** Connect **/
        connect();
    }
    
    public void connect(){
        appendMessage(" Connecting...", "Status", Color.BLUE, Color.RED);
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            /** Send our username **/
            dos.writeUTF("CMD_JOIN " + username);
            appendMessage(" Connected", "Status", Color.BLUE, Color.RED);
            appendMessage(" Type your message now.!", "Status", Color.BLUE, Color.RED);
            
            /** Start Client Thread **/
            new Thread(new ClientThread(socket, this)).start();
            jButtonSendMessage.setEnabled(true);
            // were now connected
            isConnected = true;
            
        }
        catch(IOException iOException) {
            isConnected = false;
            JOptionPane.showMessageDialog(this, "Unable to Connect to Server, please try again later.!","Connection Failed",JOptionPane.ERROR_MESSAGE);
            appendMessage("[IOException]: " + iOException.getMessage(), "Error", Color.RED, Color.RED);
        }
    }
 
    /*
        is Connected
    */
    public boolean isConnected(){
        return this.isConnected;
    }
    
    /*
        System Message
    */
    /**
     * 
     * @param msg
     * @param header
     * @param headerColor
     * @param contentColor 
     */
    public void appendMessage(String msg, String header, Color headerColor, Color contentColor){
        ChatField.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        ChatField.setEditable(false);
    }
    
    /*
        My Message
    */
    /**
     * 
     * @param msg
     * @param header 
     */
    public void appendMyMessage(String msg, String header){
        ChatField.setEditable(true);
        getMsgHeader(header, Color.BLUE);
        getMsgContent(msg, Color.BLACK);
        ChatField.setEditable(false);
    }
    
    /*
        Message Header
    */
    /**
     * 
     * @param header
     * @param color 
     */
    public void getMsgHeader(String header, Color color){
        int len = ChatField.getDocument().getLength();
        ChatField.setCaretPosition(len);
        ChatField.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Arial", 13), false);
        ChatField.replaceSelection(header + ":");
    }
    /*
        Message Content
    */
    /**
     * 
     * @param msg
     * @param color 
     */
    public void getMsgContent(String msg, Color color){
        int len = ChatField.getDocument().getLength();
        ChatField.setCaretPosition(len);
        ChatField.setCharacterAttributes(MessageStyle.styleMessageContent(color, "Arial", 13), false);
        ChatField.replaceSelection(msg + "\n\n");
    }
    
    /**
     * 
     * @param list 
     */
    public void appendOnlineList(Vector list){
         // showOnLineList(list);  // -  Original Method()
        sampleOnlineList(list);  // - Sample Method()
    }
    
    /*
        Append online list
    */
    /**
     * 
     * @param list 
     */
    public void showOnLineList(Vector list){
        try {
            jTextPaneOnlineList.setEditable(true);
            jTextPaneOnlineList.setContentType("text/html");
            StringBuilder sb = new StringBuilder();
            Iterator it = list.iterator();
            sb.append("<html><table>");
            while(it.hasNext()){
                Object e = it.next();
                URL url = getImageFile();
                Icon icon = new ImageIcon(this.getClass().getResource("/icons/icons8-active-state-24.png"));
                //sb.append("<tr><td><img src='").append(url).append("'></td><td>").append(e).append("</td></tr>");
                sb.append("<tr><td><b>></b></td><td>").append(e).append("</td></tr>");
                System.out.println("Online: " + e);
            }
            sb.append("</table></body></html>");
            jTextPaneOnlineList.removeAll();
            jTextPaneOnlineList.setText(sb.toString());
            jTextPaneOnlineList.setEditable(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
        
    /**
     * 
     * @param list 
     */
    private void sampleOnlineList(Vector list){
        jTextPaneOnlineList.setEditable(true);
        jTextPaneOnlineList.removeAll();
        jTextPaneOnlineList.setText("");
        Iterator i = list.iterator();
        while(i.hasNext()){
            Object e = i.next();
            /*  Show Online Username   */
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.white);
            
            Icon icon = new ImageIcon(this.getClass().getResource("/icons/icons8-active-state-24.png"));
            JLabel label = new JLabel(icon);
            label.setText(" " + e);
            panel.add(label);
            int len = jTextPaneOnlineList.getDocument().getLength();
            jTextPaneOnlineList.setCaretPosition(len);
            jTextPaneOnlineList.insertComponent(panel);
            /*  Append Next Line   */
            sampleAppend();
        }
        jTextPaneOnlineList.setEditable(false);
    }
    
    /**
     * 
     */
    private void sampleAppend(){
        int len = jTextPaneOnlineList.getDocument().getLength();
        jTextPaneOnlineList.setCaretPosition(len);
        jTextPaneOnlineList.replaceSelection("\n");
    }
    
    /**
     * 
     * @return 
     */
    public URL getImageFile(){
        URL url = this.getClass().getResource("/icons/icons8-active-state-24.png");
        return url;
    }
    
    
    /**
     * 
     * @param s 
     */
    public void setMyTitle(String s){
        setTitle(s);
    }
    
    /**
     * 
     * @return 
     */
    public String getMyDownloadFolder(){
        return this.mydownloadfolder;
    }
    
    /**
     * 
     * @return 
     */
    public String getMyHost(){
        return this.host;
    }
    
    /**
     * 
     * @return 
     */
    public int getMyPort(){
        return this.port;
    }
    
    /**
     * 
     * @return 
     */
    public String getMyUsername(){
        return this.username;
    }
    
    /**
     * 
     * @param b 
     */
    public void updateAttachment(boolean b){
        this.attachmentOpen = b;
    }
    

    /**
     * 
     */
    public void openFolder(){
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = chooser.showDialog(this, "Browse Folder");
        if(open == JFileChooser.APPROVE_OPTION){
            mydownloadfolder = chooser.getSelectedFile().toString() + "\\";
        } else {
            mydownloadfolder = "D:\\";
        }
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooser = new javax.swing.JFileChooser();
        jTextFieldMessage = new javax.swing.JTextField();
        jButtonSendMessage = new javax.swing.JButton();
        jLayeredPaneKaomoji = new javax.swing.JLayeredPane();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        type01 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jButton56 = new javax.swing.JButton();
        jButton57 = new javax.swing.JButton();
        jButton58 = new javax.swing.JButton();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        jButton61 = new javax.swing.JButton();
        jButton62 = new javax.swing.JButton();
        jButton63 = new javax.swing.JButton();
        jButton64 = new javax.swing.JButton();
        jButton65 = new javax.swing.JButton();
        jButton66 = new javax.swing.JButton();
        jButton67 = new javax.swing.JButton();
        jButton68 = new javax.swing.JButton();
        jButton69 = new javax.swing.JButton();
        jButton70 = new javax.swing.JButton();
        jButton71 = new javax.swing.JButton();
        jButton72 = new javax.swing.JButton();
        jButton73 = new javax.swing.JButton();
        jButton74 = new javax.swing.JButton();
        jButton75 = new javax.swing.JButton();
        jButton76 = new javax.swing.JButton();
        jButton77 = new javax.swing.JButton();
        jButton78 = new javax.swing.JButton();
        jButton79 = new javax.swing.JButton();
        jButton80 = new javax.swing.JButton();
        jButton81 = new javax.swing.JButton();
        jButton82 = new javax.swing.JButton();
        jButton83 = new javax.swing.JButton();
        jButton84 = new javax.swing.JButton();
        jButton85 = new javax.swing.JButton();
        type02 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jButton86 = new javax.swing.JButton();
        jButton87 = new javax.swing.JButton();
        jButton88 = new javax.swing.JButton();
        jButton89 = new javax.swing.JButton();
        jButton90 = new javax.swing.JButton();
        jButton91 = new javax.swing.JButton();
        jButton92 = new javax.swing.JButton();
        jButton93 = new javax.swing.JButton();
        jButton94 = new javax.swing.JButton();
        jButton95 = new javax.swing.JButton();
        jButton96 = new javax.swing.JButton();
        jButton97 = new javax.swing.JButton();
        jButton98 = new javax.swing.JButton();
        jButton99 = new javax.swing.JButton();
        jButton100 = new javax.swing.JButton();
        jButton101 = new javax.swing.JButton();
        jButton102 = new javax.swing.JButton();
        jButton103 = new javax.swing.JButton();
        jButton104 = new javax.swing.JButton();
        jButton105 = new javax.swing.JButton();
        jButton106 = new javax.swing.JButton();
        jButton107 = new javax.swing.JButton();
        jButton108 = new javax.swing.JButton();
        jButton109 = new javax.swing.JButton();
        jButton110 = new javax.swing.JButton();
        jButton111 = new javax.swing.JButton();
        jButton112 = new javax.swing.JButton();
        jButton113 = new javax.swing.JButton();
        jButton114 = new javax.swing.JButton();
        jButton115 = new javax.swing.JButton();
        jButton116 = new javax.swing.JButton();
        jButton117 = new javax.swing.JButton();
        jButton118 = new javax.swing.JButton();
        jButton119 = new javax.swing.JButton();
        jButton120 = new javax.swing.JButton();
        jButton121 = new javax.swing.JButton();
        jButton122 = new javax.swing.JButton();
        jButton123 = new javax.swing.JButton();
        jButton124 = new javax.swing.JButton();
        jButton125 = new javax.swing.JButton();
        jButton126 = new javax.swing.JButton();
        jButton127 = new javax.swing.JButton();
        jButton128 = new javax.swing.JButton();
        jButton129 = new javax.swing.JButton();
        jButton130 = new javax.swing.JButton();
        jButton131 = new javax.swing.JButton();
        jButton132 = new javax.swing.JButton();
        jButton133 = new javax.swing.JButton();
        jButton134 = new javax.swing.JButton();
        jButton135 = new javax.swing.JButton();
        jButton136 = new javax.swing.JButton();
        jButton137 = new javax.swing.JButton();
        jButton138 = new javax.swing.JButton();
        jButton139 = new javax.swing.JButton();
        type03 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jButton140 = new javax.swing.JButton();
        jButton141 = new javax.swing.JButton();
        jButton142 = new javax.swing.JButton();
        jButton143 = new javax.swing.JButton();
        jButton144 = new javax.swing.JButton();
        jButton145 = new javax.swing.JButton();
        jButton146 = new javax.swing.JButton();
        jButton147 = new javax.swing.JButton();
        jButton148 = new javax.swing.JButton();
        jButton149 = new javax.swing.JButton();
        jButton150 = new javax.swing.JButton();
        jButton151 = new javax.swing.JButton();
        jButton152 = new javax.swing.JButton();
        jButton153 = new javax.swing.JButton();
        jButton154 = new javax.swing.JButton();
        jButton155 = new javax.swing.JButton();
        jButton156 = new javax.swing.JButton();
        jButton157 = new javax.swing.JButton();
        jButton158 = new javax.swing.JButton();
        jButton159 = new javax.swing.JButton();
        jButton160 = new javax.swing.JButton();
        jButton161 = new javax.swing.JButton();
        jButton162 = new javax.swing.JButton();
        jButton163 = new javax.swing.JButton();
        jButton164 = new javax.swing.JButton();
        jButton165 = new javax.swing.JButton();
        jButton166 = new javax.swing.JButton();
        jButton167 = new javax.swing.JButton();
        jButton168 = new javax.swing.JButton();
        jButton169 = new javax.swing.JButton();
        jButton170 = new javax.swing.JButton();
        jButton171 = new javax.swing.JButton();
        jButton172 = new javax.swing.JButton();
        jButton173 = new javax.swing.JButton();
        jButton174 = new javax.swing.JButton();
        jButton175 = new javax.swing.JButton();
        jButton176 = new javax.swing.JButton();
        jButton177 = new javax.swing.JButton();
        jButton178 = new javax.swing.JButton();
        jButton179 = new javax.swing.JButton();
        jButton180 = new javax.swing.JButton();
        jButton181 = new javax.swing.JButton();
        jButton182 = new javax.swing.JButton();
        jButton183 = new javax.swing.JButton();
        jButton184 = new javax.swing.JButton();
        jButton185 = new javax.swing.JButton();
        jButton186 = new javax.swing.JButton();
        jButton187 = new javax.swing.JButton();
        jButton188 = new javax.swing.JButton();
        jButton189 = new javax.swing.JButton();
        jButton190 = new javax.swing.JButton();
        jButton191 = new javax.swing.JButton();
        jButton192 = new javax.swing.JButton();
        jButton193 = new javax.swing.JButton();
        jButton194 = new javax.swing.JButton();
        jButton195 = new javax.swing.JButton();
        jButton196 = new javax.swing.JButton();
        jButton197 = new javax.swing.JButton();
        jButton198 = new javax.swing.JButton();
        jButton199 = new javax.swing.JButton();
        jButton200 = new javax.swing.JButton();
        type04 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        jButton201 = new javax.swing.JButton();
        jButton202 = new javax.swing.JButton();
        jButton203 = new javax.swing.JButton();
        jButton204 = new javax.swing.JButton();
        jButton205 = new javax.swing.JButton();
        jButton206 = new javax.swing.JButton();
        jButton207 = new javax.swing.JButton();
        jButton208 = new javax.swing.JButton();
        jButton209 = new javax.swing.JButton();
        jButton210 = new javax.swing.JButton();
        jButton211 = new javax.swing.JButton();
        jButton212 = new javax.swing.JButton();
        jButton213 = new javax.swing.JButton();
        jButton214 = new javax.swing.JButton();
        jButton215 = new javax.swing.JButton();
        jButton216 = new javax.swing.JButton();
        jButton217 = new javax.swing.JButton();
        jButton218 = new javax.swing.JButton();
        jButton219 = new javax.swing.JButton();
        jButton220 = new javax.swing.JButton();
        jButton221 = new javax.swing.JButton();
        jButton222 = new javax.swing.JButton();
        jButton223 = new javax.swing.JButton();
        jButton224 = new javax.swing.JButton();
        jButton225 = new javax.swing.JButton();
        jButton226 = new javax.swing.JButton();
        jButton227 = new javax.swing.JButton();
        jButton228 = new javax.swing.JButton();
        jButton229 = new javax.swing.JButton();
        jButton230 = new javax.swing.JButton();
        jButton231 = new javax.swing.JButton();
        jButton232 = new javax.swing.JButton();
        jButton233 = new javax.swing.JButton();
        jButton234 = new javax.swing.JButton();
        jButton235 = new javax.swing.JButton();
        jButton236 = new javax.swing.JButton();
        jButton237 = new javax.swing.JButton();
        jButton238 = new javax.swing.JButton();
        jButton239 = new javax.swing.JButton();
        jButton240 = new javax.swing.JButton();
        jButton241 = new javax.swing.JButton();
        jButton242 = new javax.swing.JButton();
        jButton243 = new javax.swing.JButton();
        jButton244 = new javax.swing.JButton();
        jButton245 = new javax.swing.JButton();
        jButton246 = new javax.swing.JButton();
        jButton247 = new javax.swing.JButton();
        jButton248 = new javax.swing.JButton();
        jButton249 = new javax.swing.JButton();
        jButton250 = new javax.swing.JButton();
        jButton251 = new javax.swing.JButton();
        jButton252 = new javax.swing.JButton();
        jButton253 = new javax.swing.JButton();
        jButton254 = new javax.swing.JButton();
        jButton255 = new javax.swing.JButton();
        jButton256 = new javax.swing.JButton();
        jButton257 = new javax.swing.JButton();
        jButton258 = new javax.swing.JButton();
        jButton259 = new javax.swing.JButton();
        jButton260 = new javax.swing.JButton();
        jButton261 = new javax.swing.JButton();
        jButton262 = new javax.swing.JButton();
        jButton263 = new javax.swing.JButton();
        jButton264 = new javax.swing.JButton();
        jButton265 = new javax.swing.JButton();
        jButton266 = new javax.swing.JButton();
        jButton267 = new javax.swing.JButton();
        jButton268 = new javax.swing.JButton();
        jButton269 = new javax.swing.JButton();
        jButton270 = new javax.swing.JButton();
        jButton271 = new javax.swing.JButton();
        jButton272 = new javax.swing.JButton();
        jButton273 = new javax.swing.JButton();
        jButton274 = new javax.swing.JButton();
        jButton275 = new javax.swing.JButton();
        jButton276 = new javax.swing.JButton();
        jButton277 = new javax.swing.JButton();
        jButton278 = new javax.swing.JButton();
        jButton279 = new javax.swing.JButton();
        jButton280 = new javax.swing.JButton();
        jButton281 = new javax.swing.JButton();
        jButton282 = new javax.swing.JButton();
        jButton283 = new javax.swing.JButton();
        jButton284 = new javax.swing.JButton();
        jButton285 = new javax.swing.JButton();
        jButton286 = new javax.swing.JButton();
        jButton287 = new javax.swing.JButton();
        jButton288 = new javax.swing.JButton();
        jButton289 = new javax.swing.JButton();
        jButton290 = new javax.swing.JButton();
        jButton291 = new javax.swing.JButton();
        jButton292 = new javax.swing.JButton();
        type05 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jButton293 = new javax.swing.JButton();
        jButton294 = new javax.swing.JButton();
        jButton295 = new javax.swing.JButton();
        jButton296 = new javax.swing.JButton();
        jButton297 = new javax.swing.JButton();
        jButton298 = new javax.swing.JButton();
        jButton299 = new javax.swing.JButton();
        jButton300 = new javax.swing.JButton();
        jButton301 = new javax.swing.JButton();
        jButton302 = new javax.swing.JButton();
        jButton303 = new javax.swing.JButton();
        jButton304 = new javax.swing.JButton();
        jButton305 = new javax.swing.JButton();
        jButton306 = new javax.swing.JButton();
        jButton307 = new javax.swing.JButton();
        jButton308 = new javax.swing.JButton();
        jButton309 = new javax.swing.JButton();
        jButton310 = new javax.swing.JButton();
        jButton311 = new javax.swing.JButton();
        jButton312 = new javax.swing.JButton();
        jButton313 = new javax.swing.JButton();
        jButton314 = new javax.swing.JButton();
        jButton315 = new javax.swing.JButton();
        jButton316 = new javax.swing.JButton();
        jButton317 = new javax.swing.JButton();
        jButton318 = new javax.swing.JButton();
        jButton319 = new javax.swing.JButton();
        jButton320 = new javax.swing.JButton();
        jButton321 = new javax.swing.JButton();
        jButton322 = new javax.swing.JButton();
        jButton323 = new javax.swing.JButton();
        jButton324 = new javax.swing.JButton();
        jButton389 = new javax.swing.JButton();
        jButton390 = new javax.swing.JButton();
        jButton391 = new javax.swing.JButton();
        jButton392 = new javax.swing.JButton();
        jButton393 = new javax.swing.JButton();
        jButton394 = new javax.swing.JButton();
        jButton395 = new javax.swing.JButton();
        jButton396 = new javax.swing.JButton();
        jButton397 = new javax.swing.JButton();
        jButton398 = new javax.swing.JButton();
        jButton399 = new javax.swing.JButton();
        jButton400 = new javax.swing.JButton();
        jButton401 = new javax.swing.JButton();
        jButton402 = new javax.swing.JButton();
        jButton403 = new javax.swing.JButton();
        jButton404 = new javax.swing.JButton();
        jButton405 = new javax.swing.JButton();
        jButton406 = new javax.swing.JButton();
        jButton407 = new javax.swing.JButton();
        jButton408 = new javax.swing.JButton();
        jButton409 = new javax.swing.JButton();
        jButton410 = new javax.swing.JButton();
        jButton411 = new javax.swing.JButton();
        jButton412 = new javax.swing.JButton();
        type06 = new javax.swing.JScrollPane();
        jPanel8 = new javax.swing.JPanel();
        jButton325 = new javax.swing.JButton();
        jButton326 = new javax.swing.JButton();
        jButton327 = new javax.swing.JButton();
        jButton328 = new javax.swing.JButton();
        jButton329 = new javax.swing.JButton();
        jButton330 = new javax.swing.JButton();
        jButton331 = new javax.swing.JButton();
        jButton332 = new javax.swing.JButton();
        jButton333 = new javax.swing.JButton();
        jButton334 = new javax.swing.JButton();
        jButton335 = new javax.swing.JButton();
        jButton336 = new javax.swing.JButton();
        jButton337 = new javax.swing.JButton();
        jButton338 = new javax.swing.JButton();
        jButton339 = new javax.swing.JButton();
        jButton340 = new javax.swing.JButton();
        jButton341 = new javax.swing.JButton();
        jButton342 = new javax.swing.JButton();
        jButton343 = new javax.swing.JButton();
        jButton344 = new javax.swing.JButton();
        jButton345 = new javax.swing.JButton();
        jButton346 = new javax.swing.JButton();
        jButton347 = new javax.swing.JButton();
        jButton348 = new javax.swing.JButton();
        jButton349 = new javax.swing.JButton();
        jButton350 = new javax.swing.JButton();
        jButton351 = new javax.swing.JButton();
        jButton352 = new javax.swing.JButton();
        jButton353 = new javax.swing.JButton();
        jButton354 = new javax.swing.JButton();
        jButton355 = new javax.swing.JButton();
        jButton356 = new javax.swing.JButton();
        type07 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jButton357 = new javax.swing.JButton();
        jButton358 = new javax.swing.JButton();
        jButton359 = new javax.swing.JButton();
        jButton360 = new javax.swing.JButton();
        jButton361 = new javax.swing.JButton();
        jButton362 = new javax.swing.JButton();
        jButton363 = new javax.swing.JButton();
        jButton364 = new javax.swing.JButton();
        jButton365 = new javax.swing.JButton();
        jButton366 = new javax.swing.JButton();
        jButton367 = new javax.swing.JButton();
        jButton368 = new javax.swing.JButton();
        jButton369 = new javax.swing.JButton();
        jButton370 = new javax.swing.JButton();
        jButton371 = new javax.swing.JButton();
        jButton372 = new javax.swing.JButton();
        jButton373 = new javax.swing.JButton();
        jButton374 = new javax.swing.JButton();
        jButton375 = new javax.swing.JButton();
        jButton376 = new javax.swing.JButton();
        jButton377 = new javax.swing.JButton();
        jButton378 = new javax.swing.JButton();
        jButton379 = new javax.swing.JButton();
        jButton380 = new javax.swing.JButton();
        jButton381 = new javax.swing.JButton();
        jButton382 = new javax.swing.JButton();
        jButton383 = new javax.swing.JButton();
        jButton384 = new javax.swing.JButton();
        jButton385 = new javax.swing.JButton();
        jButton386 = new javax.swing.JButton();
        jButton387 = new javax.swing.JButton();
        jButton388 = new javax.swing.JButton();
        jButton413 = new javax.swing.JButton();
        jButton414 = new javax.swing.JButton();
        jButton415 = new javax.swing.JButton();
        jButton416 = new javax.swing.JButton();
        jButton417 = new javax.swing.JButton();
        jButton418 = new javax.swing.JButton();
        jButton419 = new javax.swing.JButton();
        jButton420 = new javax.swing.JButton();
        jButton421 = new javax.swing.JButton();
        jButton422 = new javax.swing.JButton();
        jButton423 = new javax.swing.JButton();
        jButton424 = new javax.swing.JButton();
        jButton425 = new javax.swing.JButton();
        jButton426 = new javax.swing.JButton();
        jButton427 = new javax.swing.JButton();
        jButton428 = new javax.swing.JButton();
        jButton429 = new javax.swing.JButton();
        jButton430 = new javax.swing.JButton();
        jButton431 = new javax.swing.JButton();
        jButton432 = new javax.swing.JButton();
        jButton433 = new javax.swing.JButton();
        jButton434 = new javax.swing.JButton();
        jButton435 = new javax.swing.JButton();
        jButton436 = new javax.swing.JButton();
        jButton437 = new javax.swing.JButton();
        jButton438 = new javax.swing.JButton();
        jButton439 = new javax.swing.JButton();
        jButton440 = new javax.swing.JButton();
        jButton441 = new javax.swing.JButton();
        jButton442 = new javax.swing.JButton();
        jButton443 = new javax.swing.JButton();
        jButton444 = new javax.swing.JButton();
        jButton445 = new javax.swing.JButton();
        jButton446 = new javax.swing.JButton();
        jButton447 = new javax.swing.JButton();
        jButton448 = new javax.swing.JButton();
        jButton449 = new javax.swing.JButton();
        jButton450 = new javax.swing.JButton();
        jButton451 = new javax.swing.JButton();
        jButton452 = new javax.swing.JButton();
        jButton453 = new javax.swing.JButton();
        jButton454 = new javax.swing.JButton();
        jButton455 = new javax.swing.JButton();
        jButton456 = new javax.swing.JButton();
        jButton457 = new javax.swing.JButton();
        jButton458 = new javax.swing.JButton();
        jButton459 = new javax.swing.JButton();
        jButton460 = new javax.swing.JButton();
        jButton461 = new javax.swing.JButton();
        jButton462 = new javax.swing.JButton();
        jButton463 = new javax.swing.JButton();
        jButton464 = new javax.swing.JButton();
        jButton465 = new javax.swing.JButton();
        jButton466 = new javax.swing.JButton();
        jLayeredPaneEmoji = new javax.swing.JLayeredPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        emoji00 = new javax.swing.JLabel();
        emoji01 = new javax.swing.JLabel();
        emoji02 = new javax.swing.JLabel();
        emoji03 = new javax.swing.JLabel();
        emoji04 = new javax.swing.JLabel();
        emoji05 = new javax.swing.JLabel();
        emoji06 = new javax.swing.JLabel();
        emoji08 = new javax.swing.JLabel();
        emoji07 = new javax.swing.JLabel();
        emoji09 = new javax.swing.JLabel();
        emoji10 = new javax.swing.JLabel();
        emoji11 = new javax.swing.JLabel();
        emoji12 = new javax.swing.JLabel();
        emoji13 = new javax.swing.JLabel();
        emoji14 = new javax.swing.JLabel();
        emoji15 = new javax.swing.JLabel();
        emoji16 = new javax.swing.JLabel();
        emoji17 = new javax.swing.JLabel();
        emoji18 = new javax.swing.JLabel();
        emoji19 = new javax.swing.JLabel();
        emoji20 = new javax.swing.JLabel();
        emoji21 = new javax.swing.JLabel();
        emoji22 = new javax.swing.JLabel();
        emoji23 = new javax.swing.JLabel();
        emoji24 = new javax.swing.JLabel();
        emoji25 = new javax.swing.JLabel();
        emoji26 = new javax.swing.JLabel();
        emoji27 = new javax.swing.JLabel();
        emoji28 = new javax.swing.JLabel();
        emoji29 = new javax.swing.JLabel();
        emoji30 = new javax.swing.JLabel();
        emoji31 = new javax.swing.JLabel();
        emoji32 = new javax.swing.JLabel();
        emoji33 = new javax.swing.JLabel();
        emoji34 = new javax.swing.JLabel();
        emoji35 = new javax.swing.JLabel();
        emoji36 = new javax.swing.JLabel();
        emoji37 = new javax.swing.JLabel();
        emoji38 = new javax.swing.JLabel();
        emoji39 = new javax.swing.JLabel();
        emoji40 = new javax.swing.JLabel();
        emoji41 = new javax.swing.JLabel();
        emoji42 = new javax.swing.JLabel();
        emoji43 = new javax.swing.JLabel();
        emoji44 = new javax.swing.JLabel();
        emoji45 = new javax.swing.JLabel();
        emoji46 = new javax.swing.JLabel();
        emoji47 = new javax.swing.JLabel();
        emoji48 = new javax.swing.JLabel();
        emoji49 = new javax.swing.JLabel();
        emoji50 = new javax.swing.JLabel();
        emoji51 = new javax.swing.JLabel();
        emoji52 = new javax.swing.JLabel();
        emoji53 = new javax.swing.JLabel();
        emoji54 = new javax.swing.JLabel();
        emoji55 = new javax.swing.JLabel();
        emoji56 = new javax.swing.JLabel();
        emoji57 = new javax.swing.JLabel();
        emoji58 = new javax.swing.JLabel();
        emoji59 = new javax.swing.JLabel();
        emoji60 = new javax.swing.JLabel();
        emoji61 = new javax.swing.JLabel();
        emoji62 = new javax.swing.JLabel();
        emoji63 = new javax.swing.JLabel();
        emoji64 = new javax.swing.JLabel();
        emoji65 = new javax.swing.JLabel();
        emoji66 = new javax.swing.JLabel();
        emoji67 = new javax.swing.JLabel();
        emoji68 = new javax.swing.JLabel();
        emoji69 = new javax.swing.JLabel();
        emoji70 = new javax.swing.JLabel();
        emoji71 = new javax.swing.JLabel();
        emoji72 = new javax.swing.JLabel();
        emoji73 = new javax.swing.JLabel();
        emoji74 = new javax.swing.JLabel();
        emoji75 = new javax.swing.JLabel();
        emoji76 = new javax.swing.JLabel();
        emoji77 = new javax.swing.JLabel();
        emoji78 = new javax.swing.JLabel();
        emoji79 = new javax.swing.JLabel();
        emoji80 = new javax.swing.JLabel();
        emoji81 = new javax.swing.JLabel();
        emoji82 = new javax.swing.JLabel();
        emoji83 = new javax.swing.JLabel();
        emoji84 = new javax.swing.JLabel();
        emoji85 = new javax.swing.JLabel();
        emoji86 = new javax.swing.JLabel();
        emoji87 = new javax.swing.JLabel();
        emoji88 = new javax.swing.JLabel();
        emoji89 = new javax.swing.JLabel();
        emoji90 = new javax.swing.JLabel();
        emoji91 = new javax.swing.JLabel();
        emoji92 = new javax.swing.JLabel();
        emoji93 = new javax.swing.JLabel();
        emoji94 = new javax.swing.JLabel();
        emoji95 = new javax.swing.JLabel();
        emoji96 = new javax.swing.JLabel();
        emoji97 = new javax.swing.JLabel();
        emoji98 = new javax.swing.JLabel();
        emoji99 = new javax.swing.JLabel();
        emoji100 = new javax.swing.JLabel();
        emoji101 = new javax.swing.JLabel();
        emoji102 = new javax.swing.JLabel();
        emoji103 = new javax.swing.JLabel();
        emoji104 = new javax.swing.JLabel();
        emoji105 = new javax.swing.JLabel();
        emoji106 = new javax.swing.JLabel();
        emoji107 = new javax.swing.JLabel();
        emoji108 = new javax.swing.JLabel();
        emoji109 = new javax.swing.JLabel();
        emoji110 = new javax.swing.JLabel();
        emoji111 = new javax.swing.JLabel();
        emoji112 = new javax.swing.JLabel();
        emoji113 = new javax.swing.JLabel();
        emoji114 = new javax.swing.JLabel();
        emoji115 = new javax.swing.JLabel();
        emoji116 = new javax.swing.JLabel();
        emoji117 = new javax.swing.JLabel();
        emoji118 = new javax.swing.JLabel();
        emoji119 = new javax.swing.JLabel();
        emoji120 = new javax.swing.JLabel();
        emoji121 = new javax.swing.JLabel();
        emoji122 = new javax.swing.JLabel();
        emoji123 = new javax.swing.JLabel();
        emoji124 = new javax.swing.JLabel();
        emoji125 = new javax.swing.JLabel();
        emoji126 = new javax.swing.JLabel();
        emoji127 = new javax.swing.JLabel();
        emoji128 = new javax.swing.JLabel();
        emoji129 = new javax.swing.JLabel();
        emoji130 = new javax.swing.JLabel();
        emoji131 = new javax.swing.JLabel();
        emoji132 = new javax.swing.JLabel();
        emoji133 = new javax.swing.JLabel();
        emoji134 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPaneOnlineList = new javax.swing.JTextPane();
        emojiButton = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ChatField = new javax.swing.JTextPane();
        jMenuBarClientChatForm = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        sendFileMenu = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        LogoutMenu = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jGotoUnicastVoiceCall = new javax.swing.JMenuItem();
        jGotoBroadcastVoiceCall = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(145, 53, 53));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jTextFieldMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 450, 33));

        jButtonSendMessage.setBackground(new java.awt.Color(0, 102, 255));
        jButtonSendMessage.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSendMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-send-letter-24.png"))); // NOI18N
        jButtonSendMessage.setText("Send Message");
        jButtonSendMessage.setEnabled(false);
        jButtonSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSendMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 300, 170, -1));

        jLayeredPaneKaomoji.setPreferredSize(new java.awt.Dimension(420, 500));
        jLayeredPaneKaomoji.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(528, 30));

        jButton1.setBackground(new java.awt.Color(0, 102, 204));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText(":-)");
        jButton1.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 102, 204));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("=)");
        jButton2.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 102, 204));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText(":D");
        jButton3.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 102, 204));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText(";P");
        jButton4.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(0, 102, 204));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText(":-(");
        jButton5.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(0, 102, 204));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText(">:(");
        jButton6.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 102, 204));
        jButton7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText(":-0");
        jButton7.setPreferredSize(new java.awt.Dimension(55, 30));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLayeredPaneKaomoji.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 30));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jButton8.setText(";)");
        jButton8.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("^_~");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText(";-)");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText(":)");
        jButton11.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("^_^");
        jButton12.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("^_____^");
        jButton13.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText(":-)");
        jButton14.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText(":D");
        jButton15.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("^0^");
        jButton16.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText(":-D");
        jButton17.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText(":P");
        jButton18.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText(":-P");
        jButton19.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText(";P");
        jButton20.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText(":(");
        jButton21.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setText(":-(");
        jButton22.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setText("U_U");
        jButton23.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton24.setText(":[");
        jButton24.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton25.setText(">:(");
        jButton25.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setText(">\"<");
        jButton26.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton27.setText("):");
        jButton27.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton28.setText(":-O");
        jButton28.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setText("O.O");
        jButton29.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setText("OwO");
        jButton30.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setText(":-()");
        jButton31.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setText("~_~");
        jButton32.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton33.setText("^o^");
        jButton33.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton34.setText(":-S");
        jButton34.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setText("<3");
        jButton35.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton36.setText("^3^");
        jButton36.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setText(":-x");
        jButton37.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton38.setText(":/");
        jButton38.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton39.setText("X_X");
        jButton39.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jButton40.setText("=/");
        jButton40.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setText(";(");
        jButton41.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton42.setText("T_T");
        jButton42.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setText(";[");
        jButton43.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton44.setText("+_+");
        jButton44.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton45.setText("O_O");
        jButton45.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jButton46.setText(":O");
        jButton46.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        jButton47.setText("¬_¬");
        jButton47.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton48.setText(";_;");
        jButton48.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        jButton49.setText("=.=");
        jButton49.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        jButton50.setText(";]");
        jButton50.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jButton51.setText("^_+");
        jButton51.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        jButton52.setText(";O)");
        jButton52.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52ActionPerformed(evt);
            }
        });

        jButton53.setText(":-]");
        jButton53.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton53ActionPerformed(evt);
            }
        });

        jButton54.setText("^.^");
        jButton54.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton54ActionPerformed(evt);
            }
        });

        jButton55.setText("=)");
        jButton55.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton55ActionPerformed(evt);
            }
        });

        jButton56.setText(":O)");
        jButton56.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton56.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton56ActionPerformed(evt);
            }
        });

        jButton57.setText(":-3");
        jButton57.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton57ActionPerformed(evt);
            }
        });

        jButton58.setText("=D");
        jButton58.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton58ActionPerformed(evt);
            }
        });

        jButton59.setText(":|");
        jButton59.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton59.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton59ActionPerformed(evt);
            }
        });

        jButton60.setText("-.-");
        jButton60.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton60.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton60ActionPerformed(evt);
            }
        });

        jButton61.setText(">_<");
        jButton61.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton61.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton61ActionPerformed(evt);
            }
        });

        jButton62.setText(":O(");
        jButton62.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton62.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton62ActionPerformed(evt);
            }
        });

        jButton63.setText("*_*");
        jButton63.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton63.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton63ActionPerformed(evt);
            }
        });

        jButton64.setText("=[");
        jButton64.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton64.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton64ActionPerformed(evt);
            }
        });

        jButton65.setText("8-)");
        jButton65.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton65.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton65ActionPerformed(evt);
            }
        });

        jButton66.setText("^^;");
        jButton66.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton66.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton66ActionPerformed(evt);
            }
        });

        jButton67.setText(":-*");
        jButton67.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton67.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton67ActionPerformed(evt);
            }
        });

        jButton68.setText("B-)");
        jButton68.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton68.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton68ActionPerformed(evt);
            }
        });

        jButton69.setText("=_=");
        jButton69.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton69.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton69ActionPerformed(evt);
            }
        });

        jButton70.setText("-0-");
        jButton70.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton70.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton70ActionPerformed(evt);
            }
        });

        jButton71.setText(":S");
        jButton71.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton71.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton71ActionPerformed(evt);
            }
        });

        jButton72.setText("$_$");
        jButton72.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton72.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton72ActionPerformed(evt);
            }
        });

        jButton73.setText(":-$");
        jButton73.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton73.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton73ActionPerformed(evt);
            }
        });

        jButton74.setText(">.<");
        jButton74.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton74.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton74ActionPerformed(evt);
            }
        });

        jButton75.setText("-_-");
        jButton75.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton75.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton75ActionPerformed(evt);
            }
        });

        jButton76.setText("> <");
        jButton76.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton76.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton76ActionPerformed(evt);
            }
        });

        jButton77.setText(":]");
        jButton77.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton77.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton77ActionPerformed(evt);
            }
        });

        jButton78.setText("^^");
        jButton78.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton78.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton78ActionPerformed(evt);
            }
        });

        jButton79.setText("=]");
        jButton79.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton79.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton79ActionPerformed(evt);
            }
        });

        jButton80.setText(";D");
        jButton80.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton80.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton80ActionPerformed(evt);
            }
        });

        jButton81.setText("^_-");
        jButton81.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton81.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton81ActionPerformed(evt);
            }
        });

        jButton82.setText(":'(");
        jButton82.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton82.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton82ActionPerformed(evt);
            }
        });

        jButton83.setText("Y.Y");
        jButton83.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton83.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton83ActionPerformed(evt);
            }
        });

        jButton84.setText("=P");
        jButton84.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton84.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton84ActionPerformed(evt);
            }
        });

        jButton85.setText("=(");
        jButton85.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton85.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton85ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton55, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton60, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton61, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton62, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton63, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton64, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton65, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton66, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton67, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton68, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton69, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton70, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton71, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton72, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton73, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton74, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton75, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton76, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton77, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton78, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton79, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton82, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton83, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton80, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton81, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton84, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton85, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton55, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton60, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton61, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton62, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton63, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton64, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton65, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton66, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton67, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton68, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton69, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton70, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton71, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton72, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton73, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton74, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton75, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton76, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton77, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton78, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton79, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton80, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton81, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton84, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton83, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton82, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton85, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        type01.setViewportView(jPanel2);

        jLayeredPaneKaomoji.add(type01, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jButton86.setText("ヾ(≧▽≦*)o");
        jButton86.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton86.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton86ActionPerformed(evt);
            }
        });

        jButton87.setText("φ(*￣0￣)");
        jButton87.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton87.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton87ActionPerformed(evt);
            }
        });

        jButton88.setText("q(≧▽≦q)");
        jButton88.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton88.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton88ActionPerformed(evt);
            }
        });

        jButton89.setText("ψ(｀∇´)ψ\t");
        jButton89.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton89.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton89ActionPerformed(evt);
            }
        });

        jButton90.setText("（￣︶￣）↗");
        jButton90.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton90.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton90ActionPerformed(evt);
            }
        });

        jButton91.setText("*^____^*");
        jButton91.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton91.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton91ActionPerformed(evt);
            }
        });

        jButton92.setText("(～￣▽￣)～");
        jButton92.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton92.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton92ActionPerformed(evt);
            }
        });

        jButton93.setText("( •̀ ω •́ )");
        jButton93.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton93.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton93ActionPerformed(evt);
            }
        });

        jButton94.setText("✧[]~(￣▽￣)~*");
        jButton94.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton94.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton94ActionPerformed(evt);
            }
        });

        jButton95.setText("φ(゜▽゜*)♪\t");
        jButton95.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton95.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton95ActionPerformed(evt);
            }
        });

        jButton96.setText("o(*^＠^*)o");
        jButton96.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton96.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton96ActionPerformed(evt);
            }
        });

        jButton97.setText("O(∩_∩)O");
        jButton97.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton97.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton97ActionPerformed(evt);
            }
        });

        jButton98.setText("(✿◡‿◡)");
        jButton98.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton98.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton98ActionPerformed(evt);
            }
        });

        jButton99.setText("`(*>﹏<*)′");
        jButton99.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton99.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton99ActionPerformed(evt);
            }
        });

        jButton100.setText("(*^▽^*)");
        jButton100.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton100ActionPerformed(evt);
            }
        });

        jButton101.setText("（*＾-＾*）");
        jButton101.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton101.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton101ActionPerformed(evt);
            }
        });

        jButton102.setText("(*^_^*)");
        jButton102.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton102.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton102ActionPerformed(evt);
            }
        });

        jButton103.setText("(❁´◡`❁)");
        jButton103.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton103.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton103ActionPerformed(evt);
            }
        });

        jButton104.setText("(≧∇≦)ﾉ");
        jButton104.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton104.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton104ActionPerformed(evt);
            }
        });

        jButton105.setText("(´▽`ʃ♡ƪ)");
        jButton105.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton105.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton105ActionPerformed(evt);
            }
        });

        jButton106.setText("(●ˇ∀ˇ●)");
        jButton106.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton106.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton106ActionPerformed(evt);
            }
        });

        jButton107.setText("○( ＾皿＾)っ Hehehe…");
        jButton107.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton107.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton107ActionPerformed(evt);
            }
        });

        jButton108.setText("(￣y▽￣)╭ Ohohoho.....");
        jButton108.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton108.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton108ActionPerformed(evt);
            }
        });

        jButton109.setText("\\^o^/");
        jButton109.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton109.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton109ActionPerformed(evt);
            }
        });

        jButton110.setText("(‾◡◝)");
        jButton110.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton110.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton110ActionPerformed(evt);
            }
        });

        jButton111.setText("╰(*°▽°*)╯");
        jButton111.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton111.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton111ActionPerformed(evt);
            }
        });

        jButton112.setText("(〃￣︶￣)人(￣︶￣〃)");
        jButton112.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton112.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton112ActionPerformed(evt);
            }
        });

        jButton113.setText("o(*^▽^*)┛");
        jButton113.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton113.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton113ActionPerformed(evt);
            }
        });

        jButton114.setText("o(*￣▽￣*)ブ");
        jButton114.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton114.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton114ActionPerformed(evt);
            }
        });

        jButton115.setText("(^_-)db(-_^)");
        jButton115.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton115.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton115ActionPerformed(evt);
            }
        });

        jButton116.setText("o(*￣▽￣*)ブ");
        jButton116.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton116.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton116ActionPerformed(evt);
            }
        });

        jButton117.setText("♪(^∇^*)");
        jButton117.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton117.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton117ActionPerformed(evt);
            }
        });

        jButton118.setText("(≧∀≦)ゞ");
        jButton118.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton118.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton118ActionPerformed(evt);
            }
        });

        jButton119.setText("o(*￣︶￣*)o");
        jButton119.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton119.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton119ActionPerformed(evt);
            }
        });

        jButton120.setText("--<-<-<@");
        jButton120.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton120.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton120ActionPerformed(evt);
            }
        });

        jButton121.setText("(oﾟvﾟ)ノ");
        jButton121.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton121.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton121ActionPerformed(evt);
            }
        });

        jButton122.setText("o(*≧▽≦)ツ┏━┓");
        jButton122.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton122.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton122ActionPerformed(evt);
            }
        });

        jButton123.setText("(/≧▽≦)/");
        jButton123.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton123.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton123ActionPerformed(evt);
            }
        });

        jButton124.setText("($ _ $)");
        jButton124.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton124.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton124ActionPerformed(evt);
            }
        });

        jButton125.setText("(☆▽☆)");
        jButton125.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton125.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton125ActionPerformed(evt);
            }
        });

        jButton126.setText("ヾ(＠⌒ー⌒＠)ノ");
        jButton126.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton126.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton126ActionPerformed(evt);
            }
        });

        jButton127.setText("ㄟ(≧◇≦)ㄏ");
        jButton127.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton127.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton127ActionPerformed(evt);
            }
        });

        jButton128.setText("o((>ω<))o");
        jButton128.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton128.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton128ActionPerformed(evt);
            }
        });

        jButton129.setText("(*︾▽︾)");
        jButton129.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton129.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton129ActionPerformed(evt);
            }
        });

        jButton130.setText("ヾ(≧ ▽ ≦)ゝ");
        jButton130.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton130.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton130ActionPerformed(evt);
            }
        });

        jButton131.setText("☆*: .｡. o(≧▽≦)o .｡.:*☆");
        jButton131.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton131.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton131ActionPerformed(evt);
            }
        });

        jButton132.setText("(((o(*ﾟ▽ﾟ*)o)))");
        jButton132.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton132.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton132ActionPerformed(evt);
            }
        });

        jButton133.setText("＼(((￣(￣(￣▽￣)￣)￣)))／");
        jButton133.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton133.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton133ActionPerformed(evt);
            }
        });

        jButton134.setText("(^///^)");
        jButton134.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton134.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton134ActionPerformed(evt);
            }
        });

        jButton135.setText("(p≧w≦q)");
        jButton135.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton135.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton135ActionPerformed(evt);
            }
        });

        jButton136.setText("o(*￣▽￣*)o");
        jButton136.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton136.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton136ActionPerformed(evt);
            }
        });

        jButton137.setText("( •̀ ω •́ )y");
        jButton137.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton137.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton137ActionPerformed(evt);
            }
        });

        jButton138.setText("(o゜▽゜)o☆");
        jButton138.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton138.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton138ActionPerformed(evt);
            }
        });

        jButton139.setText("ƪ(˘⌣˘)ʃ");
        jButton139.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton139.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton139ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton86, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton87, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton88, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton89, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton90, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton91, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton137, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton138, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton139, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton134, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton135, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton136, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton131, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton132, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton133, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton128, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton129, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton130, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton122, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton123, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton124, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton119, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton120, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton121, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton116, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton117, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton118, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton113, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton114, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton115, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton110, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton111, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton112, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton107, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton108, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton109, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton104, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton105, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton106, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton101, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton102, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton103, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton98, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton99, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton100, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton95, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton96, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton97, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton92, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton93, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton94, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton125, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton126, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton127, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton86, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton87, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton88, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton89, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton90, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton91, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton92, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton93, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton94, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton95, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton96, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton97, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton98, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton99, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton100, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton101, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton102, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton103, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton104, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton105, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton106, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton107, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton108, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton109, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton110, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton111, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton112, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton113, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton114, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton115, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton116, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton117, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton118, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton119, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton120, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton121, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton122, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton123, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton124, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton125, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton126, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton127, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton128, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton129, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton130, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton131, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton132, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton133, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton134, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton135, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton136, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton137, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton138, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton139, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        type02.setViewportView(jPanel4);

        jLayeredPaneKaomoji.add(type02, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jButton140.setText("ヾ(•ω•`)o");
        jButton140.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton140.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton140ActionPerformed(evt);
            }
        });

        jButton141.setText("\\(^︶^*\\))");
        jButton141.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton141.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton141ActionPerformed(evt);
            }
        });

        jButton142.setText("(* ^3)(ε^ *)");
        jButton142.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton142.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton142ActionPerformed(evt);
            }
        });

        jButton143.setText("－O－");
        jButton143.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton143.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton143ActionPerformed(evt);
            }
        });

        jButton144.setText("(*-3-)╭");
        jButton144.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton144.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton144ActionPerformed(evt);
            }
        });

        jButton145.setText("( ´･･)ﾉ(._.`)");
        jButton145.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton145.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton145ActionPerformed(evt);
            }
        });

        jButton146.setText("(｡･∀･)ﾉﾞ");
        jButton146.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton146.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton146ActionPerformed(evt);
            }
        });

        jButton147.setText("o(*￣▽￣*)ブ");
        jButton147.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton147.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton147ActionPerformed(evt);
            }
        });

        jButton148.setText("(_　_)。゜zｚＺ");
        jButton148.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton148.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton148ActionPerformed(evt);
            }
        });

        jButton149.setText("(ToT)/~~~");
        jButton149.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton149.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton149ActionPerformed(evt);
            }
        });

        jButton150.setText("(∪.∪ )...zzz");
        jButton150.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton150.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton150ActionPerformed(evt);
            }
        });

        jButton151.setText("!(*^(^*)");
        jButton151.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton151.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton151ActionPerformed(evt);
            }
        });

        jButton152.setText("(_o_) . z Z");
        jButton152.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton152.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton152ActionPerformed(evt);
            }
        });

        jButton153.setText("(づ^3^)づ");
        jButton153.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton153.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton153ActionPerformed(evt);
            }
        });

        jButton154.setText("（＾∀＾●）ﾉｼ");
        jButton154.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton154.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton154ActionPerformed(evt);
            }
        });

        jButton155.setText("（づ￣3￣）づ╭❤～");
        jButton155.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton155.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton155ActionPerformed(evt);
            }
        });

        jButton156.setText("\\(@^0^@)/");
        jButton156.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton156.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton156ActionPerformed(evt);
            }
        });

        jButton157.setText("ヾ(^▽^*)))");
        jButton157.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton157.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton157ActionPerformed(evt);
            }
        });

        jButton158.setText("(～﹃～)~zZ");
        jButton158.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton158.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton158ActionPerformed(evt);
            }
        });

        jButton159.setText("☆⌒(*＾-゜)v");
        jButton159.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton159.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton159ActionPerformed(evt);
            }
        });

        jButton160.setText("(￣o￣) . z Z");
        jButton160.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton160.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton160ActionPerformed(evt);
            }
        });

        jButton161.setText("(*￣;(￣ *)");
        jButton161.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton161.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton161ActionPerformed(evt);
            }
        });

        jButton162.setText("||ヽ(*￣▽￣*)ノミ|Ю");
        jButton162.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton162.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton162ActionPerformed(evt);
            }
        });

        jButton163.setText("☆⌒(*＾-゜)v");
        jButton163.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton163.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton163ActionPerformed(evt);
            }
        });

        jButton164.setText("(＾Ｕ＾)ノ~ＹＯ");
        jButton164.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton164.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton164ActionPerformed(evt);
            }
        });

        jButton165.setText("o(*°▽°*)oヾ");
        jButton165.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton165.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton165ActionPerformed(evt);
            }
        });

        jButton166.setText("(-▽-) Bye~Bye~");
        jButton166.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton166ActionPerformed(evt);
            }
        });

        jButton167.setText("( ﾟдﾟ)つ Bye");
        jButton167.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton167.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton167ActionPerformed(evt);
            }
        });

        jButton168.setText("(๑•̀ㅂ•́)و✧\t");
        jButton168.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton168.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton168ActionPerformed(evt);
            }
        });

        jButton169.setText("(o゜▽゜)o☆");
        jButton169.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton169.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton169ActionPerformed(evt);
            }
        });

        jButton170.setText("(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
        jButton170.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton170.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton170ActionPerformed(evt);
            }
        });

        jButton171.setText("(∩^o^)⊃━☆");
        jButton171.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton171.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton171ActionPerformed(evt);
            }
        });

        jButton172.setText("✪ ω ✪");
        jButton172.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton172.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton172ActionPerformed(evt);
            }
        });

        jButton173.setText("d=====(￣▽￣*)b");
        jButton173.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton173.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton173ActionPerformed(evt);
            }
        });

        jButton174.setText("＜（＾－＾）＞");
        jButton174.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton174.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton174ActionPerformed(evt);
            }
        });

        jButton175.setText("o(*￣▽￣*)o");
        jButton175.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton175.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton175ActionPerformed(evt);
            }
        });

        jButton176.setText("o(￣▽￣)ｄ");
        jButton176.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton176.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton176ActionPerformed(evt);
            }
        });

        jButton177.setText("(╹ڡ╹ )");
        jButton177.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton177.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton177ActionPerformed(evt);
            }
        });

        jButton178.setText("(u‿u✿)");
        jButton178.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton178.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton178ActionPerformed(evt);
            }
        });

        jButton179.setText("♪(´▽｀)");
        jButton179.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton179.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton179ActionPerformed(evt);
            }
        });

        jButton180.setText("(╯▽╰ )");
        jButton180.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton180ActionPerformed(evt);
            }
        });

        jButton181.setText("ヽ(✿ﾟ▽ﾟ)ノ");
        jButton181.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton181.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton181ActionPerformed(evt);
            }
        });

        jButton182.setText("( •̀ .̫ •́ )✧");
        jButton182.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton182.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton182ActionPerformed(evt);
            }
        });

        jButton183.setText("(^^ゞ");
        jButton183.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton183.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton183ActionPerformed(evt);
            }
        });

        jButton184.setText("(＠＾０＾)");
        jButton184.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton184.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton184ActionPerformed(evt);
            }
        });

        jButton185.setText("（。＾▽＾）");
        jButton185.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton185.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton185ActionPerformed(evt);
            }
        });

        jButton186.setText("Ψ(￣∀￣)Ψ");
        jButton186.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton186.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton186ActionPerformed(evt);
            }
        });

        jButton187.setText("*★,°*:.☆(￣▽￣)/$:*.°★* 。");
        jButton187.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton187.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton187ActionPerformed(evt);
            }
        });

        jButton188.setText("b(￣▽￣)d");
        jButton188.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton188.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton188ActionPerformed(evt);
            }
        });

        jButton189.setText("o(^▽^)o");
        jButton189.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton189.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton189ActionPerformed(evt);
            }
        });

        jButton190.setText("(☞ﾟヮﾟ)☞");
        jButton190.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton190.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton190ActionPerformed(evt);
            }
        });

        jButton191.setText("☜(ﾟヮﾟ☜)");
        jButton191.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton191.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton191ActionPerformed(evt);
            }
        });

        jButton192.setText("☜(⌒▽⌒)☞");
        jButton192.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton192.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton192ActionPerformed(evt);
            }
        });

        jButton193.setText("(¬‿¬)");
        jButton193.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton193.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton193ActionPerformed(evt);
            }
        });

        jButton194.setText("(•_•)");
        jButton194.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton194.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton194ActionPerformed(evt);
            }
        });

        jButton195.setText("(•_•)>⌐■-■");
        jButton195.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton195.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton195ActionPerformed(evt);
            }
        });

        jButton196.setText("(⌐■_■)");
        jButton196.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton196.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton196ActionPerformed(evt);
            }
        });

        jButton197.setText("ヾ(⌐■_■)ノ♪");
        jButton197.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton197.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton197ActionPerformed(evt);
            }
        });

        jButton198.setText("(▀̿Ĺ̯▀̿ ̿)");
        jButton198.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton198.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton198ActionPerformed(evt);
            }
        });

        jButton199.setText("＼(ﾟｰﾟ＼)");
        jButton199.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton199.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton199ActionPerformed(evt);
            }
        });

        jButton200.setText("( ﾉ ﾟｰﾟ)ﾉ");
        jButton200.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton200.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton200ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton197, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton198, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton199, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton191, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton192, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton193, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton188, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton189, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton190, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton185, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton186, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton187, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton182, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton183, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton184, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton176, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton177, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton178, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton173, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton174, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton175, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton170, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton171, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton172, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton167, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton168, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton169, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton164, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton165, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton166, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton161, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton162, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton163, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton158, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton159, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton160, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton155, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton156, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton157, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton152, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton153, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton154, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton149, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton150, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton151, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton146, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton147, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton148, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton143, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton144, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton145, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton140, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton141, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton142, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton179, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton180, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton181, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton194, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton195, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton196, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jButton200, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton140, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton141, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton142, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton143, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton144, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton145, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton146, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton147, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton148, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton149, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton150, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton151, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton152, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton153, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton154, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton155, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton156, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton157, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton158, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton159, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton160, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton161, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton162, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton163, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton164, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton165, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton166, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton167, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton168, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton169, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton170, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton171, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton172, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton173, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton174, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton175, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton176, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton177, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton178, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton179, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton180, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton181, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton182, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton183, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton184, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton185, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton186, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton187, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton188, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton189, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton190, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton191, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton192, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton193, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton194, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton195, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton196, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton197, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton198, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton199, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton200, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        type03.setViewportView(jPanel5);

        jLayeredPaneKaomoji.add(type03, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jButton201.setText("(￣y▽,￣)╭");
        jButton201.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton201.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton201ActionPerformed(evt);
            }
        });

        jButton202.setText("(o|o)");
        jButton202.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton202.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton202ActionPerformed(evt);
            }
        });

        jButton203.setText("(^人^)");
        jButton203.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton203.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton203ActionPerformed(evt);
            }
        });

        jButton204.setText("§(*￣▽￣*)§");
        jButton204.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton204.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton204ActionPerformed(evt);
            }
        });

        jButton205.setText("ψ(._. )>");
        jButton205.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton205.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton205ActionPerformed(evt);
            }
        });

        jButton206.setText("(/▽＼)");
        jButton206.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton206.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton206ActionPerformed(evt);
            }
        });

        jButton207.setText("(o′┏▽┓｀o)");
        jButton207.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton207.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton207ActionPerformed(evt);
            }
        });

        jButton208.setText("(*≧︶≦))(￣▽￣* )ゞ");
        jButton208.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton208.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton208ActionPerformed(evt);
            }
        });

        jButton209.setText("(　o=^•ェ•)o　┏━┓");
        jButton209.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton209.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton209ActionPerformed(evt);
            }
        });

        jButton210.setText("◑﹏◐");
        jButton210.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton210.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton210ActionPerformed(evt);
            }
        });

        jButton211.setText("(○｀ 3′○)");
        jButton211.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton211.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton211ActionPerformed(evt);
            }
        });

        jButton212.setText("(ಥ _ ಥ)");
        jButton212.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton212.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton212ActionPerformed(evt);
            }
        });

        jButton213.setText("(⓿_⓿)");
        jButton213.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton213.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton213ActionPerformed(evt);
            }
        });

        jButton214.setText("(❤´艸｀❤)");
        jButton214.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton214.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton214ActionPerformed(evt);
            }
        });

        jButton215.setText("(ง •_•)ง");
        jButton215.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton215.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton215ActionPerformed(evt);
            }
        });

        jButton216.setText("ผ(•̀_•́ผ)");
        jButton216.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton216.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton216ActionPerformed(evt);
            }
        });

        jButton217.setText("（〃｀ 3′〃");
        jButton217.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton217.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton217ActionPerformed(evt);
            }
        });

        jButton218.setText("(●'◡'●)");
        jButton218.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton218.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton218ActionPerformed(evt);
            }
        });

        jButton219.setText("(. ❛ ᴗ ❛.)");
        jButton219.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton219.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton219ActionPerformed(evt);
            }
        });

        jButton220.setText("ლ(╹◡╹ლ)");
        jButton220.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton220.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton220ActionPerformed(evt);
            }
        });

        jButton221.setText("o(〃＾▽＾〃)o");
        jButton221.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton221.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton221ActionPerformed(evt);
            }
        });

        jButton222.setText("(。・ω・。)");
        jButton222.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton222.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton222ActionPerformed(evt);
            }
        });

        jButton223.setText("(>'-'<)");
        jButton223.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton223.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton223ActionPerformed(evt);
            }
        });

        jButton224.setText("(✿◠‿◠)");
        jButton224.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton224.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton224ActionPerformed(evt);
            }
        });

        jButton225.setText("(ﾉ*ФωФ)ﾉ");
        jButton225.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton225.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton225ActionPerformed(evt);
            }
        });

        jButton226.setText("ˋ( ° ▽、° )");
        jButton226.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton226.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton226ActionPerformed(evt);
            }
        });

        jButton227.setText("(*/ω＼*)");
        jButton227.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton227.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton227ActionPerformed(evt);
            }
        });

        jButton228.setText("=￣ω￣=");
        jButton228.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton228.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton228ActionPerformed(evt);
            }
        });

        jButton229.setText("ο(=•ω＜=)ρ⌒☆");
        jButton229.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton229.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton229ActionPerformed(evt);
            }
        });

        jButton230.setText("(✿◕‿◕✿)");
        jButton230.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton230.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton230ActionPerformed(evt);
            }
        });

        jButton231.setText("✍(◔◡◔)");
        jButton231.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton231.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton231ActionPerformed(evt);
            }
        });

        jButton232.setText("(★‿★)");
        jButton232.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton232.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton232ActionPerformed(evt);
            }
        });

        jButton233.setText("╰(￣ω￣ｏ)");
        jButton233.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton233.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton233ActionPerformed(evt);
            }
        });

        jButton234.setText("~(￣▽￣)~*");
        jButton234.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton234.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton234ActionPerformed(evt);
            }
        });

        jButton235.setText("〜(￣▽￣〜)");
        jButton235.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton235.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton235ActionPerformed(evt);
            }
        });

        jButton236.setText("(〜￣▽￣)〜"); // NOI18N
        jButton236.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton236.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton236ActionPerformed(evt);
            }
        });

        jButton237.setText("(～o￣3￣)～");
        jButton237.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton237.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton237ActionPerformed(evt);
            }
        });

        jButton238.setText("(っ´Ι`)っ");
        jButton238.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton238.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton238ActionPerformed(evt);
            }
        });

        jButton239.setText("ԅ(¯﹃¯ԅ)");
        jButton239.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton239.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton239ActionPerformed(evt);
            }
        });

        jButton240.setText("(￣﹃￣)");
        jButton240.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton240.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton240ActionPerformed(evt);
            }
        });

        jButton241.setText("༼ つ ◕_◕ ༽つ");
        jButton241.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton241.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton241ActionPerformed(evt);
            }
        });

        jButton242.setText("o(*////▽////*)q");
        jButton242.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton242.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton242ActionPerformed(evt);
            }
        });

        jButton243.setText("(^///^)");
        jButton243.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton243.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton243ActionPerformed(evt);
            }
        });

        jButton244.setText("(/ω＼*)………");
        jButton244.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton244.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton244ActionPerformed(evt);
            }
        });

        jButton245.setText("(/ω•＼*)(o゜▽゜)o☆");
        jButton245.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton245.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton245ActionPerformed(evt);
            }
        });

        jButton246.setText("（。＾▽＾）");
        jButton246.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton246.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton246ActionPerformed(evt);
            }
        });

        jButton247.setText("Ψ(￣∀￣)Ψ");
        jButton247.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton247.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton247ActionPerformed(evt);
            }
        });

        jButton248.setText("*★,°*:.☆(￣▽￣)/$:*.°★* 。");
        jButton248.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton248.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton248ActionPerformed(evt);
            }
        });

        jButton249.setText("( ‵▽′)ψ");
        jButton249.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton249.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton249ActionPerformed(evt);
            }
        });

        jButton250.setText("ヽ(￣ω￣(￣ω￣〃)ゝ");
        jButton250.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton250.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton250ActionPerformed(evt);
            }
        });

        jButton251.setText("*(੭*ˊᵕˋ)੭*ଘ");
        jButton251.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton251.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton251ActionPerformed(evt);
            }
        });

        jButton252.setText("┏ (゜ω゜)=☞");
        jButton252.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton252.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton252ActionPerformed(evt);
            }
        });

        jButton253.setText("U•ェ•*U");
        jButton253.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton253.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton253ActionPerformed(evt);
            }
        });

        jButton254.setText("（￣。。￣）");
        jButton254.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton254.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton254ActionPerformed(evt);
            }
        });

        jButton255.setText("(°°)～");
        jButton255.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton255.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton255ActionPerformed(evt);
            }
        });

        jButton256.setText("m( =∩王∩= )m");
        jButton256.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton256.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton256ActionPerformed(evt);
            }
        });

        jButton257.setText("o(=•ェ•=)m");
        jButton257.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton257.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton257ActionPerformed(evt);
            }
        });

        jButton258.setText("(‧‧)nnn");
        jButton258.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton258.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton258ActionPerformed(evt);
            }
        });

        jButton259.setText("\\(0^◇^0)/");
        jButton259.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton259.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton259ActionPerformed(evt);
            }
        });

        jButton260.setText("~o( =∩ω∩= )m");
        jButton260.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton260.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton260ActionPerformed(evt);
            }
        });

        jButton261.setText("--\\(˙<>˙)/--");
        jButton261.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton261.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton261ActionPerformed(evt);
            }
        });

        jButton262.setText("( ఠൠఠ )ﾉ");
        jButton262.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton262.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton262ActionPerformed(evt);
            }
        });

        jButton263.setText("≡[。。]≡");
        jButton263.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton263.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton263ActionPerformed(evt);
            }
        });

        jButton264.setText("(:≡");
        jButton264.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton264.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton264ActionPerformed(evt);
            }
        });

        jButton265.setText(".<{=．．．．");
        jButton265.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton265.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton265ActionPerformed(evt);
            }
        });

        jButton266.setText("ฅʕ•̫͡•ʔฅ");
        jButton266.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton266.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton266ActionPerformed(evt);
            }
        });

        jButton267.setText("( ◍•㉦•◍ )");
        jButton267.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton267.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton267ActionPerformed(evt);
            }
        });

        jButton268.setText("(◉Θ◉)");
        jButton268.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton268.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton268ActionPerformed(evt);
            }
        });

        jButton269.setText("ϵ( ‘Θ’ )϶");
        jButton269.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton269.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton269ActionPerformed(evt);
            }
        });

        jButton270.setText("( ⓛ ω ⓛ *)");
        jButton270.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton270.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton270ActionPerformed(evt);
            }
        });

        jButton271.setText("(*✧×✧*)");
        jButton271.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton271.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton271ActionPerformed(evt);
            }
        });

        jButton272.setText("(^◕.◕^)");
        jButton272.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton272.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton272ActionPerformed(evt);
            }
        });

        jButton273.setText("ᓚᘏᗢ");
        jButton273.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton273.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton273ActionPerformed(evt);
            }
        });

        jButton274.setText("~(=^‥^)ノ");
        jButton274.setPreferredSize(new java.awt.Dimension(40, 30));
        jButton274.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton274ActionPerformed(evt);
            }
        });

        jButton275.setText("/ᐠ｡ꞈ｡ᐟ\\");
            jButton275.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton275.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton275ActionPerformed(evt);
                }
            });

            jButton276.setText("( ¯(∞)¯ )");
            jButton276.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton276.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton276ActionPerformed(evt);
                }
            });

            jButton277.setText("(￣(工)￣)");
            jButton277.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton277.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton277ActionPerformed(evt);
                }
            });

            jButton278.setText("<。)#)))≦");
            jButton278.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton278.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton278ActionPerformed(evt);
                }
            });

            jButton279.setText("(:◎)≡");
            jButton279.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton279.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton279ActionPerformed(evt);
                }
            });

            jButton280.setText("^(*￣(oo)￣)^");
            jButton280.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton280.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton280ActionPerformed(evt);
                }
            });

            jButton281.setText("(ʘ ʖ̯ ʘ)");
            jButton281.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton281.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton281ActionPerformed(evt);
                }
            });

            jButton282.setText("(ʘ ͟ʖ ʘ)");
            jButton282.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton282.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton282ActionPerformed(evt);
                }
            });

            jButton283.setText("(ʘ ͜ʖ ʘ)");
            jButton283.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton283.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton283ActionPerformed(evt);
                }
            });

            jButton284.setText("( ͡• ͜ʖ ͡• )");
            jButton284.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton284.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton284ActionPerformed(evt);
                }
            });

            jButton285.setText("( ͠° ͟ʖ ͡°)");
            jButton285.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton285.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton285ActionPerformed(evt);
                }
            });

            jButton286.setText("( ͡° ͜ʖ ͡°)");
            jButton286.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton286.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton286ActionPerformed(evt);
                }
            });

            jButton287.setText("( ͡~ ͜ʖ ͡°)");
            jButton287.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton287.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton287ActionPerformed(evt);
                }
            });

            jButton288.setText("( ͡ಠ ʖ̯ ͡ಠ)");
            jButton288.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton288.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton288ActionPerformed(evt);
                }
            });

            jButton289.setText("( ఠ ͟ʖ ఠ)");
            jButton289.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton289.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton289ActionPerformed(evt);
                }
            });

            jButton290.setText("( ͡°( ͡° ͜ʖ( ͡° ͜ʖ ͡°)ʖ ͡°) ͡°)");
            jButton290.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton290.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton290ActionPerformed(evt);
                }
            });

            jButton291.setText("¯\\_( ͡° ͜ʖ ͡°)_/¯");
            jButton291.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton291.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton291ActionPerformed(evt);
                }
            });

            jButton292.setText("¯\\_(ツ)_/¯");
            jButton292.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton292.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton292ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton204, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton205, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton206, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton207, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton208, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton209, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton291, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton292, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton201, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton202, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton203, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton255, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton256, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton257, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton273, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton274, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton275, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton288, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton289, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton290, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton285, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton286, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton287, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton282, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton283, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton284, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton279, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton280, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton281, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton276, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton277, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton278, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton270, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton271, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton272, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton267, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton268, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton269, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton264, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton265, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton266, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton261, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton262, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton263, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton258, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton259, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton260, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton252, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton253, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton254, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton249, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton250, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton251, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton246, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton247, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton248, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton243, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton244, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton245, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton240, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton241, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton242, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton237, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton238, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton239, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton234, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton235, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton236, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton231, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton232, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton233, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton228, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton229, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton230, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton225, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton226, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton227, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton222, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton223, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton224, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton219, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton220, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton221, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton216, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton217, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton218, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton210, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton211, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton212, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jButton213, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton214, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton215, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE))
            );
            jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton201, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton202, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton203, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton204, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton205, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton206, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton207, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton208, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton209, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton210, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton211, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton212, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton213, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton214, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton215, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton216, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton217, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton218, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton219, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton220, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton221, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton222, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton223, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton224, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton225, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton226, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton227, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton228, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton229, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton230, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton231, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton232, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton233, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton234, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton235, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton236, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton237, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton238, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton239, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton240, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton241, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton242, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton243, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton244, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton245, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton246, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton247, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton248, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton249, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton250, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton251, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton252, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton253, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton254, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton255, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton256, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton257, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton258, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton259, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton260, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton261, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton262, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton263, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton264, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton265, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton266, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton267, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton268, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton269, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton270, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton271, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton272, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton273, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton274, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton275, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton276, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton277, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton278, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton279, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton280, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton281, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton282, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton283, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton284, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton285, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton286, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton287, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton288, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton289, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton290, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton291, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton292, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
            );

            type04.setViewportView(jPanel6);

            jLayeredPaneKaomoji.add(type04, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

            jPanel7.setBackground(new java.awt.Color(255, 255, 255));

            jButton293.setText("o((>ω< ))o");
            jButton293.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton293.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton293ActionPerformed(evt);
                }
            });

            jButton294.setText("╰（‵□′）╯");
            jButton294.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton294.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton294ActionPerformed(evt);
                }
            });

            jButton295.setText("(～￣(OO)￣)ブ");
            jButton295.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton295.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton295ActionPerformed(evt);
                }
            });

            jButton296.setText("o(≧口≦)o\t");
            jButton296.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton296.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton296ActionPerformed(evt);
                }
            });

            jButton297.setText("(╬▔皿▔)╯");
            jButton297.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton297.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton297ActionPerformed(evt);
                }
            });

            jButton298.setText("(⊙x⊙;)");
            jButton298.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton298.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton298ActionPerformed(evt);
                }
            });

            jButton299.setText("-へ-");
            jButton299.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton299.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton299ActionPerformed(evt);
                }
            });

            jButton300.setText("（︶^︶）");
            jButton300.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton300.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton300ActionPerformed(evt);
                }
            });

            jButton301.setText("( ˘︹˘ )");
            jButton301.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton301.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton301ActionPerformed(evt);
                }
            });

            jButton302.setText("(* -︿-)");
            jButton302.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton302.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton302ActionPerformed(evt);
                }
            });

            jButton303.setText("ヽ（≧□≦）ノ");
            jButton303.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton303.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton303ActionPerformed(evt);
                }
            });

            jButton304.setText("╚(•⌂•)╝");
            jButton304.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton304.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton304ActionPerformed(evt);
                }
            });

            jButton305.setText("╰(艹皿艹 )");
            jButton305.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton305.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton305ActionPerformed(evt);
                }
            });

            jButton306.setText("___*( --皿--)/#____");
            jButton306.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton306.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton306ActionPerformed(evt);
                }
            });

            jButton307.setText("(￣ε(#￣)☆╰╮o(￣皿￣///)");
            jButton307.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton307.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton307ActionPerformed(evt);
                }
            });

            jButton308.setText("<( ￣^￣)(θ(θ☆( >_<");
            jButton308.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton308.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton308ActionPerformed(evt);
                }
            });

            jButton309.setText("(￣ε(#￣)");
            jButton309.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton309.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton309ActionPerformed(evt);
                }
            });

            jButton310.setText("○|￣|_ =3");
            jButton310.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton310.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton310ActionPerformed(evt);
                }
            });

            jButton311.setText("┗|｀O′|┛");
            jButton311.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton311.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton311ActionPerformed(evt);
                }
            });

            jButton312.setText("(′д｀σ)σ");
            jButton312.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton312.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton312ActionPerformed(evt);
                }
            });

            jButton313.setText("(ノ｀Д)ノ");
            jButton313.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton313.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton313ActionPerformed(evt);
                }
            });

            jButton314.setText("(￢︿̫̿￢☆)");
            jButton314.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton314.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton314ActionPerformed(evt);
                }
            });

            jButton315.setText("～(　TロT)σ");
            jButton315.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton315.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton315ActionPerformed(evt);
                }
            });

            jButton316.setText("(〃＞目＜)");
            jButton316.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton316.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton316ActionPerformed(evt);
                }
            });

            jButton317.setText("(///￣皿￣)○～");
            jButton317.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton317.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton317ActionPerformed(evt);
                }
            });

            jButton318.setText("<( ‵□′)>───Ｃε(┬﹏┬)3");
            jButton318.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton318.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton318ActionPerformed(evt);
                }
            });

            jButton319.setText("<( ‵□′)───C＜─___-)||");
            jButton319.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton319.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton319ActionPerformed(evt);
                }
            });

            jButton320.setText("ε=( o｀ω′)ノ");
            jButton320.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton320.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton320ActionPerformed(evt);
                }
            });

            jButton321.setText("(ｏ ‵-′)ノ”(ノ﹏<。)");
            jButton321.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton321.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton321ActionPerformed(evt);
                }
            });

            jButton322.setText("ヾ(≧へ≦)〃");
            jButton322.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton322.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton322ActionPerformed(evt);
                }
            });

            jButton323.setText("(╯▔皿▔)╯");
            jButton323.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton323.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton323ActionPerformed(evt);
                }
            });

            jButton324.setText("щ(゜ロ゜щ)");
            jButton324.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton324.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton324ActionPerformed(evt);
                }
            });

            jButton389.setText("(σ｀д′)σ");
            jButton389.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton389.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton389ActionPerformed(evt);
                }
            });

            jButton390.setText("(►__◄)");
            jButton390.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton390.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton390ActionPerformed(evt);
                }
            });

            jButton391.setText("ᕦ(ò_óˇ)ᕤ");
            jButton391.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton391.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton391ActionPerformed(evt);
                }
            });

            jButton392.setText("ヽ(゜▽゜　)－C<(/;◇;)/~");
            jButton392.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton392.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton392ActionPerformed(evt);
                }
            });

            jButton393.setText("(╯‵□′)╯︵┻━┻");
            jButton393.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton393.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton393ActionPerformed(evt);
                }
            });

            jButton394.setText("┳━┳ ノ( ゜-゜ノ)");
            jButton394.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton394.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton394ActionPerformed(evt);
                }
            });

            jButton395.setText("(╯°□°）╯︵ ┻━┻");
            jButton395.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton395.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton395ActionPerformed(evt);
                }
            });

            jButton396.setText("(ヘ･_･)ヘ┳━┳");
            jButton396.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton396.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton396ActionPerformed(evt);
                }
            });

            jButton397.setText("┻━┻ ︵ ＼( °□° )／ ︵ ┻━┻");
            jButton397.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton397.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton397ActionPerformed(evt);
                }
            });

            jButton398.setText("┻━┻ ︵ヽ(`Д´)ﾉ︵ ┻━┻");
            jButton398.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton398.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton398ActionPerformed(evt);
                }
            });

            jButton399.setText("o(一︿一+)o");
            jButton399.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton399.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton399ActionPerformed(evt);
                }
            });

            jButton400.setText("(一﹏一；)\t");
            jButton400.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton400.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton400ActionPerformed(evt);
                }
            });

            jButton401.setText("(ㆆ_ㆆ)");
            jButton401.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton401.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton401ActionPerformed(evt);
                }
            });

            jButton402.setText("ಠ_ಠ");
            jButton402.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton402.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton402ActionPerformed(evt);
                }
            });

            jButton403.setText("┌( ಠ_ಠ)┘");
            jButton403.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton403.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton403ActionPerformed(evt);
                }
            });

            jButton404.setText("ಠಿ_ಠ");
            jButton404.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton404.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton404ActionPerformed(evt);
                }
            });

            jButton405.setText("ಠ﹏ಠ");
            jButton405.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton405.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton405ActionPerformed(evt);
                }
            });

            jButton406.setText("ಠ╭╮ಠ");
            jButton406.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton406.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton406ActionPerformed(evt);
                }
            });

            jButton407.setText("ಠ_ರೃ");
            jButton407.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton407.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton407ActionPerformed(evt);
                }
            });

            jButton408.setText("ಠ▃ಠ");
            jButton408.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton408.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton408ActionPerformed(evt);
                }
            });

            jButton409.setText("(>ლ)");
            jButton409.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton409.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton409ActionPerformed(evt);
                }
            });

            jButton410.setText("눈_눈");
            jButton410.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton410.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton410ActionPerformed(evt);
                }
            });

            jButton411.setText("(¬_¬ )");
            jButton411.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton411.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton411ActionPerformed(evt);
                }
            });

            jButton412.setText("(¬_¬\")");
            jButton412.setPreferredSize(new java.awt.Dimension(40, 30));
            jButton412.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton412ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
            jPanel7.setLayout(jPanel7Layout);
            jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton408, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton409, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton410, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton405, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton406, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton407, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton402, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton403, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton404, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton399, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton400, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton401, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton396, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton397, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton398, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton393, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton394, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton395, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton390, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton391, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton392, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton323, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton324, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton389, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton320, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton321, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton322, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton317, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton318, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton319, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton314, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton315, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton316, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton311, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton312, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton313, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton308, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton309, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton310, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton305, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton306, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton307, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton302, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton303, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton304, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton299, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton300, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton301, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton296, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton297, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton298, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton293, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton294, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton295, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jButton411, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton412, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton293, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton294, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton295, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton296, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton297, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton298, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton299, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton300, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton301, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton302, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton303, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton304, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton305, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton306, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton307, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton308, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton309, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton310, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton311, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton312, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton313, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton314, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton315, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton316, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton317, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton318, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton319, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton320, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton321, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton322, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton323, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton324, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton389, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton390, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton391, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton392, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton393, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton394, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton395, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton396, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton397, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton398, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton399, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton400, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton401, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton402, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton403, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton404, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton405, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton406, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton407, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton408, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton409, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton410, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton411, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton412, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
            );

            type05.setViewportView(jPanel7);

            jLayeredPaneKaomoji.add(type05, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

            jPanel8.setBackground(new java.awt.Color(255, 255, 255));

            jButton325.setText("/_ \\");
                jButton325.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton325.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton325ActionPerformed(evt);
                    }
                });

                jButton326.setText("（；´д｀）ゞ");
                jButton326.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton326.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton326ActionPerformed(evt);
                    }
                });

                jButton327.setText("＞﹏＜");
                jButton327.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton327.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton327ActionPerformed(evt);
                    }
                });

                jButton328.setText("(っ °Д °;)っ");
                jButton328.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton328.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton328ActionPerformed(evt);
                    }
                });

                jButton329.setText("(￣ ‘i ￣;)");
                jButton329.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton329.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton329ActionPerformed(evt);
                    }
                });

                jButton330.setText("( *^-^)ρ(*╯^╰)");
                jButton330.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton330.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton330ActionPerformed(evt);
                    }
                });

                jButton331.setText("＞︿＜");
                jButton331.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton331.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton331ActionPerformed(evt);
                    }
                });

                jButton332.setText("o(￣┰￣*)ゞ");
                jButton332.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton332.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton332ActionPerformed(evt);
                    }
                });

                jButton333.setText("(ノへ￣、)");
                jButton333.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton333.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton333ActionPerformed(evt);
                    }
                });

                jButton334.setText("<(＿　＿)>");
                jButton334.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton334.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton334ActionPerformed(evt);
                    }
                });

                jButton335.setText("(#｀-_ゝ-)");
                jButton335.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton335.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton335ActionPerformed(evt);
                    }
                });

                jButton336.setText("（＞人＜；）");
                jButton336.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton336.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton336ActionPerformed(evt);
                    }
                });

                jButton337.setText("{{{(>_<)}}}");
                jButton337.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton337.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton337ActionPerformed(evt);
                    }
                });

                jButton338.setText("~(>_<。)＼");
                jButton338.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton338.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton338ActionPerformed(evt);
                    }
                });

                jButton339.setText("≡(▔﹏▔)≡");
                jButton339.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton339.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton339ActionPerformed(evt);
                    }
                });

                jButton340.setText(".·´¯`(>▂<)´¯`·.");
                jButton340.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton340.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton340ActionPerformed(evt);
                    }
                });

                jButton341.setText("╯︿╰");
                jButton341.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton341.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton341ActionPerformed(evt);
                    }
                });

                jButton342.setText("இ௰இ");
                jButton342.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton342.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton342ActionPerformed(evt);
                    }
                });

                jButton343.setText("(┬┬﹏┬┬)");
                jButton343.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton343.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton343ActionPerformed(evt);
                    }
                });

                jButton344.setText("(´。＿。｀)");
                jButton344.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton344.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton344ActionPerformed(evt);
                    }
                });

                jButton345.setText("(˘･_･˘)");
                jButton345.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton345.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton345ActionPerformed(evt);
                    }
                });

                jButton346.setText("(；′⌒`)");
                jButton346.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton346.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton346ActionPerformed(evt);
                    }
                });

                jButton347.setText("≧ ﹏ ≦");
                jButton347.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton347.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton347ActionPerformed(evt);
                    }
                });

                jButton348.setText("〒▽〒");
                jButton348.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton348.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton348ActionPerformed(evt);
                    }
                });

                jButton349.setText("━((*′д｀)爻(′д｀*))━!!!!");
                jButton349.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton349.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton349ActionPerformed(evt);
                    }
                });

                jButton350.setText("T_T)");
                jButton350.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton350.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton350ActionPerformed(evt);
                    }
                });

                jButton351.setText("(≧﹏ ≦)");
                jButton351.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton351.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton351ActionPerformed(evt);
                    }
                });

                jButton352.setText("(′д｀ )…彡…彡");
                jButton352.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton352.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton352ActionPerformed(evt);
                    }
                });

                jButton353.setText("┗( T﹏T )┛");
                jButton353.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton353.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton353ActionPerformed(evt);
                    }
                });

                jButton354.setText("(。﹏。*)");
                jButton354.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton354.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton354ActionPerformed(evt);
                    }
                });

                jButton355.setText("X﹏X");
                jButton355.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton355.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton355ActionPerformed(evt);
                    }
                });

                jButton356.setText("ಥ_ಥ");
                jButton356.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton356.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton356ActionPerformed(evt);
                    }
                });

                javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
                jPanel8.setLayout(jPanel8Layout);
                jPanel8Layout.setHorizontalGroup(
                    jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton352, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton353, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton354, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton349, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton350, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton351, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton346, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton347, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton348, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton343, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton344, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton345, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton340, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton341, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton342, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton337, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton338, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton339, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton334, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton335, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton336, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton331, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton332, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton333, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton328, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton329, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton330, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton355, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton356, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton325, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton326, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton327, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                );
                jPanel8Layout.setVerticalGroup(
                    jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton325, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton326, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton327, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton328, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton329, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton330, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton331, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton332, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton333, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton334, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton335, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton336, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton337, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton338, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton339, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton340, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton341, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton342, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton343, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton344, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton345, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton346, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton347, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton348, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton349, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton350, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton351, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton352, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton353, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton354, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton355, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton356, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                );

                type06.setViewportView(jPanel8);

                jLayeredPaneKaomoji.add(type06, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

                jPanel9.setBackground(new java.awt.Color(255, 255, 255));

                jButton357.setText("w(ﾟДﾟ)w");
                jButton357.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton357.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton357ActionPerformed(evt);
                    }
                });

                jButton358.setText("┗|｀O′|┛");
                jButton358.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton358.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton358ActionPerformed(evt);
                    }
                });

                jButton359.setText("（⊙ｏ⊙）");
                jButton359.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton359.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton359ActionPerformed(evt);
                    }
                });

                jButton360.setText("(＃°Д°)");
                jButton360.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton360.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton360ActionPerformed(evt);
                    }
                });

                jButton361.setText("（*゜ー゜*）");
                jButton361.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton361.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton361ActionPerformed(evt);
                    }
                });

                jButton362.setText("(。_。)");
                jButton362.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton362.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton362ActionPerformed(evt);
                    }
                });

                jButton363.setText("...(*￣０￣)ノ");
                jButton363.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton363.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton363ActionPerformed(evt);
                    }
                });

                jButton364.setText("＼（〇_ｏ）／");
                jButton364.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton364.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton364ActionPerformed(evt);
                    }
                });

                jButton365.setText("o((⊙﹏⊙))o.");
                jButton365.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton365.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton365ActionPerformed(evt);
                    }
                });

                jButton366.setText("(⊙ˍ⊙)");
                jButton366.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton366.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton366ActionPerformed(evt);
                    }
                });

                jButton367.setText("(⊙_⊙)？");
                jButton367.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton367.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton367ActionPerformed(evt);
                    }
                });

                jButton368.setText("(⊙_⊙;)");
                jButton368.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton368.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton368ActionPerformed(evt);
                    }
                });

                jButton369.setText("(⊙_(⊙_⊙)_⊙)");
                jButton369.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton369.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton369ActionPerformed(evt);
                    }
                });

                jButton370.setText("(⊙o⊙)");
                jButton370.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton370.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton370ActionPerformed(evt);
                    }
                });

                jButton371.setText("⊙.☉");
                jButton371.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton371.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton371ActionPerformed(evt);
                    }
                });

                jButton372.setText("¯\\(°_o)/¯");
                jButton372.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton372.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton372ActionPerformed(evt);
                    }
                });

                jButton373.setText("(´･ω･`)?");
                jButton373.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton373.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton373ActionPerformed(evt);
                    }
                });

                jButton374.setText("(￣┰￣*)");
                jButton374.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton374.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton374ActionPerformed(evt);
                    }
                });

                jButton375.setText("o(><；)ooΣ");
                jButton375.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton375.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton375ActionPerformed(evt);
                    }
                });

                jButton376.setText("(っ °Д °;)っ");
                jButton376.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton376.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton376ActionPerformed(evt);
                    }
                });

                jButton377.setText("∑( 口 ||");
                jButton377.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton377.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton377ActionPerformed(evt);
                    }
                });

                jButton378.setText("┌(。Д。)┐");
                jButton378.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton378.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton378ActionPerformed(evt);
                    }
                });

                jButton379.setText("(°ー°〃)");
                jButton379.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton379.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton379ActionPerformed(evt);
                    }
                });

                jButton380.setText("ε=ε=ε=(~￣▽￣)~");
                jButton380.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton380.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton380ActionPerformed(evt);
                    }
                });

                jButton381.setText("(￣m￣）");
                jButton381.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton381.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton381ActionPerformed(evt);
                    }
                });

                jButton382.setText("(ノω<。)ノ))☆.。");
                jButton382.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton382.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton382ActionPerformed(evt);
                    }
                });

                jButton383.setText("(ﾉ*･ω･)ﾉ");
                jButton383.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton383.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton383ActionPerformed(evt);
                    }
                });

                jButton384.setText("(#`O′)");
                jButton384.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton384.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton384ActionPerformed(evt);
                    }
                });

                jButton385.setText("щ(ʘ╻ʘ)щ");
                jButton385.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton385.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton385ActionPerformed(evt);
                    }
                });

                jButton386.setText("（o´・ェ・｀o）");
                jButton386.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton386.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton386ActionPerformed(evt);
                    }
                });

                jButton387.setText("(*Φ皿Φ*)");
                jButton387.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton387.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton387ActionPerformed(evt);
                    }
                });

                jButton388.setText("(・∀・(・∀・(・∀・*)");
                jButton388.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton388.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton388ActionPerformed(evt);
                    }
                });

                jButton413.setText("(○´･д･)ﾉ");
                jButton413.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton413.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton413ActionPerformed(evt);
                    }
                });

                jButton414.setText("┬┴┬┴┤(･_├┬┴┬┴");
                jButton414.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton414.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton414ActionPerformed(evt);
                    }
                });

                jButton415.setText("(o_ _)ﾉ");
                jButton415.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton415.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton415ActionPerformed(evt);
                    }
                });

                jButton416.setText("(＠_＠;)");
                jButton416.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton416.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton416ActionPerformed(evt);
                    }
                });

                jButton417.setText("ㄟ( ▔, ▔ )ㄏ");
                jButton417.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton417.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton417ActionPerformed(evt);
                    }
                });

                jButton418.setText("(￣_,￣ )");
                jButton418.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton418.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton418ActionPerformed(evt);
                    }
                });

                jButton419.setText("(+_+)?");
                jButton419.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton419.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton419ActionPerformed(evt);
                    }
                });

                jButton420.setText("(。>︿<)_θ<");
                jButton420.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton420.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton420ActionPerformed(evt);
                    }
                });

                jButton421.setText("(￣ c￣)y▂");
                jButton421.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton421.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton421ActionPerformed(evt);
                    }
                });

                jButton422.setText("ξ(๐॔˃̶ᗜ˂̶๐॓)");
                jButton422.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton422.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton422ActionPerformed(evt);
                    }
                });

                jButton423.setText("o_o");
                jButton423.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton423.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton423ActionPerformed(evt);
                    }
                });

                jButton424.setText(".______.");
                jButton424.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton424.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton424ActionPerformed(evt);
                    }
                });

                jButton425.setText("━┳━　━┳━");
                jButton425.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton425.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton425ActionPerformed(evt);
                    }
                });

                jButton426.setText("━━(￣ー￣*|||━━……]");
                jButton426.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton426.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton426ActionPerformed(evt);
                    }
                });

                jButton427.setText("((o_ _)'");
                jButton427.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton427.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton427ActionPerformed(evt);
                    }
                });

                jButton428.setText("彡☆(。﹏。)");
                jButton428.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton428.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton428ActionPerformed(evt);
                    }
                });

                jButton429.setText("(⊙﹏⊙)...");
                jButton429.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton429.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton429ActionPerformed(evt);
                    }
                });

                jButton430.setText("( ＿ ＿)ノ");
                jButton430.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton430.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton430ActionPerformed(evt);
                    }
                });

                jButton431.setText(",,ԾㅂԾ,,");
                jButton431.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton431.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton431ActionPerformed(evt);
                    }
                });

                jButton432.setText("m( _ _ )m");
                jButton432.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton432.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton432ActionPerformed(evt);
                    }
                });

                jButton433.setText("(lll￢ω￢)");
                jButton433.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton433.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton433ActionPerformed(evt);
                    }
                });

                jButton434.setText("╮(╯-╰)╭");
                jButton434.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton434.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton434ActionPerformed(evt);
                    }
                });

                jButton435.setText("(￣▽￣)\"");
                jButton435.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton435.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton435ActionPerformed(evt);
                    }
                });

                jButton436.setText("(￣_￣|||)");
                jButton436.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton436.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton436ActionPerformed(evt);
                    }
                });

                jButton437.setText("(x_x)");
                jButton437.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton437.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton437ActionPerformed(evt);
                    }
                });

                jButton438.setText("_〆(´Д｀ )");
                jButton438.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton438.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton438ActionPerformed(evt);
                    }
                });

                jButton439.setText("( ╯□╰ )");
                jButton439.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton439.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton439ActionPerformed(evt);
                    }
                });

                jButton440.setText("⊙﹏⊙∥");
                jButton440.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton440.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton440ActionPerformed(evt);
                    }
                });

                jButton441.setText("┌( ´_ゝ` )┐");
                jButton441.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton441.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton441ActionPerformed(evt);
                    }
                });

                jButton442.setText("－_－b");
                jButton442.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton442.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton442ActionPerformed(evt);
                    }
                });

                jButton443.setText("(ˉ﹃ˉ)");
                jButton443.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton443.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton443ActionPerformed(evt);
                    }
                });

                jButton444.setText("╮（╯＿╰）╭");
                jButton444.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton444.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton444ActionPerformed(evt);
                    }
                });

                jButton445.setText("(￣_,￣ )");
                jButton445.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton445.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton445ActionPerformed(evt);
                    }
                });

                jButton446.setText("○|--|_");
                jButton446.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton446.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton446ActionPerformed(evt);
                    }
                });

                jButton447.setText("(ˉ▽￣～)");
                jButton447.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton447.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton447ActionPerformed(evt);
                    }
                });

                jButton448.setText("(´ｰ∀ｰ`)");
                jButton448.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton448.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton448ActionPerformed(evt);
                    }
                });

                jButton449.setText("(。・・)ノ");
                jButton449.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton449.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton449ActionPerformed(evt);
                    }
                });

                jButton450.setText("_(:з)∠)_┑");
                jButton450.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton450.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton450ActionPerformed(evt);
                    }
                });

                jButton451.setText("(￣Д ￣)┍");
                jButton451.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton451.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton451ActionPerformed(evt);
                    }
                });

                jButton452.setText("ε=ε=ε=┏(゜ロ゜;)┛");
                jButton452.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton452.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton452ActionPerformed(evt);
                    }
                });

                jButton453.setText("(*￣rǒ￣)");
                jButton453.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton453.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton453ActionPerformed(evt);
                    }
                });

                jButton454.setText("つ﹏⊂");
                jButton454.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton454.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton454ActionPerformed(evt);
                    }
                });

                jButton455.setText("(￣、￣)");
                jButton455.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton455.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton455ActionPerformed(evt);
                    }
                });

                jButton456.setText("╮(╯▽╰)╭");
                jButton456.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton456.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton456ActionPerformed(evt);
                    }
                });

                jButton457.setText("(☆-ｖ-)");
                jButton457.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton457.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton457ActionPerformed(evt);
                    }
                });

                jButton458.setText("(ˉ▽ˉ；)...");
                jButton458.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton458.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton458ActionPerformed(evt);
                    }
                });

                jButton459.setText("→_→");
                jButton459.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton459.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton459ActionPerformed(evt);
                    }
                });

                jButton460.setText("←_←");
                jButton460.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton460.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton460ActionPerformed(evt);
                    }
                });

                jButton461.setText("♨_♨");
                jButton461.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton461.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton461ActionPerformed(evt);
                    }
                });

                jButton462.setText("◉_◉");
                jButton462.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton462.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton462ActionPerformed(evt);
                    }
                });

                jButton463.setText("(●__●)");
                jButton463.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton463.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton463ActionPerformed(evt);
                    }
                });

                jButton464.setText("(⊙_◎)");
                jButton464.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton464.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton464ActionPerformed(evt);
                    }
                });

                jButton465.setText("⚆_⚆");
                jButton465.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton465.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton465ActionPerformed(evt);
                    }
                });

                jButton466.setText("(•ˋ _ ˊ•)");
                jButton466.setPreferredSize(new java.awt.Dimension(40, 30));
                jButton466.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton466ActionPerformed(evt);
                    }
                });

                javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
                jPanel9.setLayout(jPanel9Layout);
                jPanel9Layout.setHorizontalGroup(
                    jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton462, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton463, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton464, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton459, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton460, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton461, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton450, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton451, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton452, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton447, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton448, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton449, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton444, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton445, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton446, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton441, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton442, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton443, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton437, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton439, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton440, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton435, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton436, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton438, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton429, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton430, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton431, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton426, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton427, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton428, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton423, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton424, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton425, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton420, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton421, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton422, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton417, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton418, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton419, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton414, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton415, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton416, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton384, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton385, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton386, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton381, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton382, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton383, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton378, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton379, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton380, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton375, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton376, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton377, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton372, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton373, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton374, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton369, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton370, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton371, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton366, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton367, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton368, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton363, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton364, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton365, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton360, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton361, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton362, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton465, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton466, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton456, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton457, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton458, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton387, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton388, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton413, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton357, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton358, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton359, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton432, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton433, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton434, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton453, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton454, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton455, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                );
                jPanel9Layout.setVerticalGroup(
                    jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton357, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton358, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton359, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton360, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton361, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton362, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton363, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton364, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton365, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton366, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton367, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton368, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton369, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton370, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton371, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton372, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton373, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton374, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton375, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton376, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton377, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton378, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton379, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton380, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton381, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton382, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton383, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton384, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton385, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton386, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton387, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton388, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton413, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton414, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton415, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton416, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton417, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton418, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton419, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton420, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton421, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton422, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton423, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton424, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton425, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton426, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton427, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton428, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton429, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton430, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton431, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton432, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton433, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton434, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton435, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton436, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton438, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton437, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton439, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton440, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton441, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton442, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton443, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton444, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton445, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton446, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton447, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton448, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton449, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton450, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton451, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton452, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton453, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton454, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton455, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton456, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton457, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton458, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton459, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton460, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton461, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton462, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton463, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton464, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton465, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton466, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                );

                type07.setViewportView(jPanel9);

                jLayeredPaneKaomoji.add(type07, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 420, 150));

                getContentPane().add(jLayeredPaneKaomoji, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 100, 420, 190));

                jLayeredPaneEmoji.setBackground(new java.awt.Color(255, 255, 255));

                jPanel1.setBackground(new java.awt.Color(255, 255, 255));

                emoji00.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/0.gif"))); // NOI18N
                emoji00.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji00MousePressed(evt);
                    }
                });

                emoji01.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/1.gif"))); // NOI18N
                emoji01.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji01MousePressed(evt);
                    }
                });

                emoji02.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/2.gif"))); // NOI18N
                emoji02.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji02MousePressed(evt);
                    }
                });

                emoji03.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/3.gif"))); // NOI18N
                emoji03.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji03MousePressed(evt);
                    }
                });

                emoji04.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/4.gif"))); // NOI18N
                emoji04.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji04MousePressed(evt);
                    }
                });

                emoji05.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/5.gif"))); // NOI18N
                emoji05.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji05MousePressed(evt);
                    }
                });

                emoji06.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/6.gif"))); // NOI18N
                emoji06.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji06MousePressed(evt);
                    }
                });

                emoji08.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/8.gif"))); // NOI18N
                emoji08.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji08MousePressed(evt);
                    }
                });

                emoji07.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/7.gif"))); // NOI18N
                emoji07.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji07MousePressed(evt);
                    }
                });

                emoji09.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/9.gif"))); // NOI18N
                emoji09.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji09MousePressed(evt);
                    }
                });

                emoji10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/10.gif"))); // NOI18N
                emoji10.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji10MousePressed(evt);
                    }
                });

                emoji11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/11.gif"))); // NOI18N
                emoji11.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji11MousePressed(evt);
                    }
                });

                emoji12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/12.gif"))); // NOI18N
                emoji12.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji12MousePressed(evt);
                    }
                });

                emoji13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/13.gif"))); // NOI18N
                emoji13.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji13MousePressed(evt);
                    }
                });

                emoji14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/14.gif"))); // NOI18N
                emoji14.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji14MousePressed(evt);
                    }
                });

                emoji15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/15.gif"))); // NOI18N
                emoji15.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji15MousePressed(evt);
                    }
                });

                emoji16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/16.gif"))); // NOI18N
                emoji16.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji16MousePressed(evt);
                    }
                });

                emoji17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/17.gif"))); // NOI18N
                emoji17.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji17MousePressed(evt);
                    }
                });

                emoji18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/18.gif"))); // NOI18N
                emoji18.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji18MousePressed(evt);
                    }
                });

                emoji19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/19.gif"))); // NOI18N
                emoji19.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji19MousePressed(evt);
                    }
                });

                emoji20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/20.gif"))); // NOI18N
                emoji20.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji20MousePressed(evt);
                    }
                });

                emoji21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/21.gif"))); // NOI18N
                emoji21.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji21MousePressed(evt);
                    }
                });

                emoji22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/22.gif"))); // NOI18N
                emoji22.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji22MousePressed(evt);
                    }
                });

                emoji23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/23.gif"))); // NOI18N
                emoji23.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji23MousePressed(evt);
                    }
                });

                emoji24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/24.gif"))); // NOI18N
                emoji24.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji24MousePressed(evt);
                    }
                });

                emoji25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/25.gif"))); // NOI18N
                emoji25.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji25MousePressed(evt);
                    }
                });

                emoji26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/26.gif"))); // NOI18N
                emoji26.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji26MousePressed(evt);
                    }
                });

                emoji27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/27.gif"))); // NOI18N
                emoji27.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji27MousePressed(evt);
                    }
                });

                emoji28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/28.gif"))); // NOI18N
                emoji28.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji28MousePressed(evt);
                    }
                });

                emoji29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/29.gif"))); // NOI18N
                emoji29.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji29MousePressed(evt);
                    }
                });

                emoji30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/30.gif"))); // NOI18N
                emoji30.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji30MousePressed(evt);
                    }
                });

                emoji31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/31.gif"))); // NOI18N
                emoji31.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji31MousePressed(evt);
                    }
                });

                emoji32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/32.gif"))); // NOI18N
                emoji32.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji32MousePressed(evt);
                    }
                });

                emoji33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/33.gif"))); // NOI18N
                emoji33.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji33MousePressed(evt);
                    }
                });

                emoji34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/34.gif"))); // NOI18N
                emoji34.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji34MousePressed(evt);
                    }
                });

                emoji35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/35.gif"))); // NOI18N
                emoji35.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji35MousePressed(evt);
                    }
                });

                emoji36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/36.gif"))); // NOI18N
                emoji36.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji36MousePressed(evt);
                    }
                });

                emoji37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/37.gif"))); // NOI18N
                emoji37.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji37MousePressed(evt);
                    }
                });

                emoji38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/38.gif"))); // NOI18N
                emoji38.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji38MousePressed(evt);
                    }
                });

                emoji39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/39.gif"))); // NOI18N
                emoji39.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji39MousePressed(evt);
                    }
                });

                emoji40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/40.gif"))); // NOI18N
                emoji40.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji40MousePressed(evt);
                    }
                });

                emoji41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/41.gif"))); // NOI18N
                emoji41.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji41MousePressed(evt);
                    }
                });

                emoji42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/42.gif"))); // NOI18N
                emoji42.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji42MousePressed(evt);
                    }
                });

                emoji43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/43.gif"))); // NOI18N
                emoji43.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji43MousePressed(evt);
                    }
                });

                emoji44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/44.gif"))); // NOI18N
                emoji44.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji44MousePressed(evt);
                    }
                });

                emoji45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/45.gif"))); // NOI18N
                emoji45.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji45MousePressed(evt);
                    }
                });

                emoji46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/46.gif"))); // NOI18N
                emoji46.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji46MousePressed(evt);
                    }
                });

                emoji47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/47.gif"))); // NOI18N
                emoji47.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji47MousePressed(evt);
                    }
                });

                emoji48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/48.gif"))); // NOI18N
                emoji48.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji48MousePressed(evt);
                    }
                });

                emoji49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/49.gif"))); // NOI18N
                emoji49.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji49MousePressed(evt);
                    }
                });

                emoji50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/50.gif"))); // NOI18N
                emoji50.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji50MousePressed(evt);
                    }
                });

                emoji51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/51.gif"))); // NOI18N
                emoji51.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji51MousePressed(evt);
                    }
                });

                emoji52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/52.gif"))); // NOI18N
                emoji52.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji52MousePressed(evt);
                    }
                });

                emoji53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/53.gif"))); // NOI18N
                emoji53.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji53MousePressed(evt);
                    }
                });

                emoji54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/54.gif"))); // NOI18N
                emoji54.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji54MousePressed(evt);
                    }
                });

                emoji55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/55.gif"))); // NOI18N
                emoji55.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji55MousePressed(evt);
                    }
                });

                emoji56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/56.gif"))); // NOI18N
                emoji56.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji56MousePressed(evt);
                    }
                });

                emoji57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/57.gif"))); // NOI18N
                emoji57.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji57MousePressed(evt);
                    }
                });

                emoji58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/58.gif"))); // NOI18N
                emoji58.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji58MousePressed(evt);
                    }
                });

                emoji59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/59.gif"))); // NOI18N
                emoji59.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji59MousePressed(evt);
                    }
                });

                emoji60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/60.gif"))); // NOI18N
                emoji60.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji60MousePressed(evt);
                    }
                });

                emoji61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/61.gif"))); // NOI18N
                emoji61.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji61MousePressed(evt);
                    }
                });

                emoji62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/62.gif"))); // NOI18N
                emoji62.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji62MousePressed(evt);
                    }
                });

                emoji63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/63.gif"))); // NOI18N
                emoji63.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji63MousePressed(evt);
                    }
                });

                emoji64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/64.gif"))); // NOI18N
                emoji64.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji64MousePressed(evt);
                    }
                });

                emoji65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/65.gif"))); // NOI18N
                emoji65.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji65MousePressed(evt);
                    }
                });

                emoji66.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/66.gif"))); // NOI18N
                emoji66.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji66MousePressed(evt);
                    }
                });

                emoji67.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/67.gif"))); // NOI18N
                emoji67.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji67MousePressed(evt);
                    }
                });

                emoji68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/68.gif"))); // NOI18N
                emoji68.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji68MousePressed(evt);
                    }
                });

                emoji69.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/69.gif"))); // NOI18N
                emoji69.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji69MousePressed(evt);
                    }
                });

                emoji70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/70.gif"))); // NOI18N
                emoji70.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji70MousePressed(evt);
                    }
                });

                emoji71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/71.gif"))); // NOI18N
                emoji71.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji71MousePressed(evt);
                    }
                });

                emoji72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/72.gif"))); // NOI18N
                emoji72.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji72MousePressed(evt);
                    }
                });

                emoji73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/73.gif"))); // NOI18N
                emoji73.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji73MousePressed(evt);
                    }
                });

                emoji74.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/74.gif"))); // NOI18N
                emoji74.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji74MousePressed(evt);
                    }
                });

                emoji75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/75.gif"))); // NOI18N
                emoji75.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji75MousePressed(evt);
                    }
                });

                emoji76.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/76.gif"))); // NOI18N
                emoji76.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji76MousePressed(evt);
                    }
                });

                emoji77.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/77.gif"))); // NOI18N
                emoji77.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji77MousePressed(evt);
                    }
                });

                emoji78.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/78.gif"))); // NOI18N
                emoji78.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji78MousePressed(evt);
                    }
                });

                emoji79.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/79.gif"))); // NOI18N
                emoji79.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji79MousePressed(evt);
                    }
                });

                emoji80.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/80.gif"))); // NOI18N
                emoji80.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji80MousePressed(evt);
                    }
                });

                emoji81.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/81.gif"))); // NOI18N
                emoji81.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji81MousePressed(evt);
                    }
                });

                emoji82.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/82.gif"))); // NOI18N
                emoji82.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji82MousePressed(evt);
                    }
                });

                emoji83.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/83.gif"))); // NOI18N
                emoji83.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji83MousePressed(evt);
                    }
                });

                emoji84.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/84.gif"))); // NOI18N
                emoji84.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji84MousePressed(evt);
                    }
                });

                emoji85.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/85.gif"))); // NOI18N
                emoji85.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji85MousePressed(evt);
                    }
                });

                emoji86.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/86.gif"))); // NOI18N
                emoji86.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji86MousePressed(evt);
                    }
                });

                emoji87.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/87.gif"))); // NOI18N
                emoji87.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji87MousePressed(evt);
                    }
                });

                emoji88.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/88.gif"))); // NOI18N
                emoji88.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji88MousePressed(evt);
                    }
                });

                emoji89.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/89.gif"))); // NOI18N
                emoji89.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji89MousePressed(evt);
                    }
                });

                emoji90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/90.gif"))); // NOI18N
                emoji90.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji90MousePressed(evt);
                    }
                });

                emoji91.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/91.gif"))); // NOI18N
                emoji91.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji91MousePressed(evt);
                    }
                });

                emoji92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/92.gif"))); // NOI18N
                emoji92.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji92MousePressed(evt);
                    }
                });

                emoji93.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/93.gif"))); // NOI18N
                emoji93.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji93MousePressed(evt);
                    }
                });

                emoji94.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/94.gif"))); // NOI18N
                emoji94.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji94MousePressed(evt);
                    }
                });

                emoji95.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/95.gif"))); // NOI18N
                emoji95.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji95MousePressed(evt);
                    }
                });

                emoji96.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/96.gif"))); // NOI18N
                emoji96.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji96MousePressed(evt);
                    }
                });

                emoji97.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/97.gif"))); // NOI18N
                emoji97.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji97MousePressed(evt);
                    }
                });

                emoji98.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/98.gif"))); // NOI18N
                emoji98.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji98MousePressed(evt);
                    }
                });

                emoji99.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/99.gif"))); // NOI18N
                emoji99.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji99MousePressed(evt);
                    }
                });

                emoji100.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/100.gif"))); // NOI18N
                emoji100.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji100MousePressed(evt);
                    }
                });

                emoji101.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/101.gif"))); // NOI18N
                emoji101.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji101MousePressed(evt);
                    }
                });

                emoji102.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/102.gif"))); // NOI18N
                emoji102.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji102MousePressed(evt);
                    }
                });

                emoji103.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/103.gif"))); // NOI18N
                emoji103.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji103MousePressed(evt);
                    }
                });

                emoji104.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/104.gif"))); // NOI18N
                emoji104.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji104MousePressed(evt);
                    }
                });

                emoji105.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/105.gif"))); // NOI18N
                emoji105.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji105MousePressed(evt);
                    }
                });

                emoji106.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/106.gif"))); // NOI18N
                emoji106.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji106MousePressed(evt);
                    }
                });

                emoji107.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/107.gif"))); // NOI18N
                emoji107.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji107MousePressed(evt);
                    }
                });

                emoji108.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/108.gif"))); // NOI18N
                emoji108.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji108MousePressed(evt);
                    }
                });

                emoji109.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/109.gif"))); // NOI18N
                emoji109.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji109MousePressed(evt);
                    }
                });

                emoji110.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/110.gif"))); // NOI18N
                emoji110.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji110MousePressed(evt);
                    }
                });

                emoji111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/111.gif"))); // NOI18N
                emoji111.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji111MousePressed(evt);
                    }
                });

                emoji112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/112.gif"))); // NOI18N
                emoji112.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji112MousePressed(evt);
                    }
                });

                emoji113.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/113.gif"))); // NOI18N
                emoji113.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji113MousePressed(evt);
                    }
                });

                emoji114.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/114.gif"))); // NOI18N
                emoji114.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji114MousePressed(evt);
                    }
                });

                emoji115.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/115.gif"))); // NOI18N
                emoji115.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji115MousePressed(evt);
                    }
                });

                emoji116.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/116.gif"))); // NOI18N
                emoji116.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji116MousePressed(evt);
                    }
                });

                emoji117.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/117.gif"))); // NOI18N
                emoji117.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji117MousePressed(evt);
                    }
                });

                emoji118.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/118.gif"))); // NOI18N
                emoji118.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji118MousePressed(evt);
                    }
                });

                emoji119.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/119.gif"))); // NOI18N
                emoji119.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji119MousePressed(evt);
                    }
                });

                emoji120.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/120.gif"))); // NOI18N
                emoji120.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji120MousePressed(evt);
                    }
                });

                emoji121.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/121.gif"))); // NOI18N
                emoji121.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji121MousePressed(evt);
                    }
                });

                emoji122.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/122.gif"))); // NOI18N
                emoji122.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji122MousePressed(evt);
                    }
                });

                emoji123.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/123.gif"))); // NOI18N
                emoji123.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji123MousePressed(evt);
                    }
                });

                emoji124.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/124.gif"))); // NOI18N
                emoji124.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji124MousePressed(evt);
                    }
                });

                emoji125.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/125.gif"))); // NOI18N
                emoji125.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji125MousePressed(evt);
                    }
                });

                emoji126.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/126.gif"))); // NOI18N
                emoji126.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji126MousePressed(evt);
                    }
                });

                emoji127.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/127.gif"))); // NOI18N
                emoji127.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji127MousePressed(evt);
                    }
                });

                emoji128.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/128.gif"))); // NOI18N
                emoji128.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji128MousePressed(evt);
                    }
                });

                emoji129.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/129.gif"))); // NOI18N
                emoji129.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji129MousePressed(evt);
                    }
                });

                emoji130.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/130.gif"))); // NOI18N
                emoji130.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji130MousePressed(evt);
                    }
                });

                emoji131.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/131.gif"))); // NOI18N
                emoji131.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji131MousePressed(evt);
                    }
                });

                emoji132.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/132.gif"))); // NOI18N
                emoji132.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji132MousePressed(evt);
                    }
                });

                emoji133.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/133.gif"))); // NOI18N
                emoji133.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji133MousePressed(evt);
                    }
                });

                emoji134.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emojis/134.gif"))); // NOI18N
                emoji134.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emoji134MousePressed(evt);
                    }
                });

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji21))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji00)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji01, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji02)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji03)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji04)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji05)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji06)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji07)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji08)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji09)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji10))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji32))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji43))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji53)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji54))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji59)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji65))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji66)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji67)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji68)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji69)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji70)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji71)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji72)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji73)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji74)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji75)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji76))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji77)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji78)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji79)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji80)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji81)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji82)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji83)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji84)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji85)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji86)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji87))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji88)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji89)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji90)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji91)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji92)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji93)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji94)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji95)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji96)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji97)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji98))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji99)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji100)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji101)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji102)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji103)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji104)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji105)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji106)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji107)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji108)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji109))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji110)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji111)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji112)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji113)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji114)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji115)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji116)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji117)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji118)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji119)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji120))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji121)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji122)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji123)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji124)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji125)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji126)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji127)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji128)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji129)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji130)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji131))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(emoji132)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji133)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emoji134)))
                        .addContainerGap(34, Short.MAX_VALUE))
                );
                jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji10)
                            .addComponent(emoji09)
                            .addComponent(emoji04)
                            .addComponent(emoji08)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(emoji05, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emoji02)
                                    .addComponent(emoji03)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(emoji00)
                                        .addComponent(emoji01))))
                            .addComponent(emoji06)
                            .addComponent(emoji07))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji11)
                            .addComponent(emoji12)
                            .addComponent(emoji13)
                            .addComponent(emoji14)
                            .addComponent(emoji15)
                            .addComponent(emoji16)
                            .addComponent(emoji17)
                            .addComponent(emoji18)
                            .addComponent(emoji19)
                            .addComponent(emoji20)
                            .addComponent(emoji21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji22)
                            .addComponent(emoji23)
                            .addComponent(emoji24)
                            .addComponent(emoji25)
                            .addComponent(emoji26)
                            .addComponent(emoji27)
                            .addComponent(emoji28)
                            .addComponent(emoji29)
                            .addComponent(emoji30)
                            .addComponent(emoji31)
                            .addComponent(emoji32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji33)
                            .addComponent(emoji34)
                            .addComponent(emoji35)
                            .addComponent(emoji36)
                            .addComponent(emoji37)
                            .addComponent(emoji38)
                            .addComponent(emoji39)
                            .addComponent(emoji40)
                            .addComponent(emoji41)
                            .addComponent(emoji42)
                            .addComponent(emoji43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji44)
                            .addComponent(emoji45)
                            .addComponent(emoji46)
                            .addComponent(emoji47)
                            .addComponent(emoji48)
                            .addComponent(emoji49)
                            .addComponent(emoji50)
                            .addComponent(emoji51)
                            .addComponent(emoji52)
                            .addComponent(emoji53)
                            .addComponent(emoji54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji55)
                            .addComponent(emoji56)
                            .addComponent(emoji57)
                            .addComponent(emoji58)
                            .addComponent(emoji59)
                            .addComponent(emoji60)
                            .addComponent(emoji61)
                            .addComponent(emoji62)
                            .addComponent(emoji63)
                            .addComponent(emoji64)
                            .addComponent(emoji65))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji66)
                            .addComponent(emoji67)
                            .addComponent(emoji68)
                            .addComponent(emoji69)
                            .addComponent(emoji70)
                            .addComponent(emoji71)
                            .addComponent(emoji72)
                            .addComponent(emoji73)
                            .addComponent(emoji74)
                            .addComponent(emoji75)
                            .addComponent(emoji76))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji77)
                            .addComponent(emoji78)
                            .addComponent(emoji79)
                            .addComponent(emoji80)
                            .addComponent(emoji81)
                            .addComponent(emoji82)
                            .addComponent(emoji83)
                            .addComponent(emoji84)
                            .addComponent(emoji85)
                            .addComponent(emoji86)
                            .addComponent(emoji87))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji88)
                            .addComponent(emoji89)
                            .addComponent(emoji90)
                            .addComponent(emoji91)
                            .addComponent(emoji92)
                            .addComponent(emoji93)
                            .addComponent(emoji94)
                            .addComponent(emoji95)
                            .addComponent(emoji96)
                            .addComponent(emoji97)
                            .addComponent(emoji98))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji99)
                            .addComponent(emoji100)
                            .addComponent(emoji101)
                            .addComponent(emoji102)
                            .addComponent(emoji103)
                            .addComponent(emoji104)
                            .addComponent(emoji105)
                            .addComponent(emoji106)
                            .addComponent(emoji107)
                            .addComponent(emoji108)
                            .addComponent(emoji109))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji110)
                            .addComponent(emoji111)
                            .addComponent(emoji112)
                            .addComponent(emoji113)
                            .addComponent(emoji114)
                            .addComponent(emoji115)
                            .addComponent(emoji116)
                            .addComponent(emoji117)
                            .addComponent(emoji118)
                            .addComponent(emoji119)
                            .addComponent(emoji120))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji121)
                            .addComponent(emoji122)
                            .addComponent(emoji123)
                            .addComponent(emoji124)
                            .addComponent(emoji125)
                            .addComponent(emoji126)
                            .addComponent(emoji127)
                            .addComponent(emoji128)
                            .addComponent(emoji129)
                            .addComponent(emoji130)
                            .addComponent(emoji131))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emoji132)
                            .addComponent(emoji133)
                            .addComponent(emoji134))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jScrollPane2.setViewportView(jPanel1);

                jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                jLabel1.setText("Use Kaomoji");
                jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        jLabel1MousePressed(evt);
                    }
                });

                jLayeredPaneEmoji.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
                jLayeredPaneEmoji.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

                javax.swing.GroupLayout jLayeredPaneEmojiLayout = new javax.swing.GroupLayout(jLayeredPaneEmoji);
                jLayeredPaneEmoji.setLayout(jLayeredPaneEmojiLayout);
                jLayeredPaneEmojiLayout.setHorizontalGroup(
                    jLayeredPaneEmojiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPaneEmojiLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jLayeredPaneEmojiLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                );
                jLayeredPaneEmojiLayout.setVerticalGroup(
                    jLayeredPaneEmojiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneEmojiLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                );

                getContentPane().add(jLayeredPaneEmoji, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 100, 370, 200));

                jTextPaneOnlineList.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
                jTextPaneOnlineList.setForeground(new java.awt.Color(120, 14, 3));
                jTextPaneOnlineList.setAutoscrolls(false);
                jTextPaneOnlineList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                jScrollPane3.setViewportView(jTextPaneOnlineList);

                getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 170, 330));

                emojiButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-slightly-smiling-face-24.png"))); // NOI18N
                emojiButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        emojiButtonMousePressed(evt);
                    }
                });
                getContentPane().add(emojiButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 300, -1, 33));

                ChatField.setContentType("text/html");
                jScrollPane1.setViewportView(ChatField);

                getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 12, 670, 280));

                jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-sharing-24.png"))); // NOI18N
                jMenu3.setText("File Sharing");
                jMenu3.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jMenu3ActionPerformed(evt);
                    }
                });

                sendFileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-send-file-24.png"))); // NOI18N
                sendFileMenu.setText("Send File");
                sendFileMenu.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        sendFileMenuActionPerformed(evt);
                    }
                });
                jMenu3.add(sendFileMenu);

                jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-download-24.png"))); // NOI18N
                jMenuItem3.setText("Downloads");
                jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jMenuItem3ActionPerformed(evt);
                    }
                });
                jMenu3.add(jMenuItem3);

                jMenuBarClientChatForm.add(jMenu3);

                jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-male-user-24.png"))); // NOI18N
                jMenu2.setText("Account");

                LogoutMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-exit-24.png"))); // NOI18N
                LogoutMenu.setText("Logout");
                LogoutMenu.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        LogoutMenuActionPerformed(evt);
                    }
                });
                jMenu2.add(LogoutMenu);

                jMenuBarClientChatForm.add(jMenu2);

                jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-video-message-24.png"))); // NOI18N
                jMenu1.setText("Video Call");
                jMenu1.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jMenu1ActionPerformed(evt);
                    }
                });

                jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-video-message-24.png"))); // NOI18N
                jMenuItem4.setText("Connect to User");
                jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jMenuItem4ActionPerformed(evt);
                    }
                });
                jMenu1.add(jMenuItem4);

                jMenuBarClientChatForm.add(jMenu1);

                jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-call-24.png"))); // NOI18N
                jMenu6.setText("Voice Call");

                jGotoUnicastVoiceCall.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-call-transfer-24.png"))); // NOI18N
                jGotoUnicastVoiceCall.setText("Connect to username");
                jGotoUnicastVoiceCall.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jGotoUnicastVoiceCallActionPerformed(evt);
                    }
                });
                jMenu6.add(jGotoUnicastVoiceCall);

                jGotoBroadcastVoiceCall.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-broadcasting-24.png"))); // NOI18N
                jGotoBroadcastVoiceCall.setText("Connect to group");
                jGotoBroadcastVoiceCall.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jGotoBroadcastVoiceCallActionPerformed(evt);
                    }
                });
                jMenu6.add(jGotoBroadcastVoiceCall);

                jMenuBarClientChatForm.add(jMenu6);

                jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-administrative-tools-24.png"))); // NOI18N
                jMenu4.setText("Tools");
                jMenuBarClientChatForm.add(jMenu4);

                jMenu5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-contact-us-24.png"))); // NOI18N
                jMenu5.setText("Contacts");

                jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-help-24.png"))); // NOI18N
                jMenuItem1.setText("Helps");
                jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jMenuItem1ActionPerformed(evt);
                    }
                });
                jMenu5.add(jMenuItem1);

                jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-about-24.png"))); // NOI18N
                jMenuItem2.setText("About this app");
                jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jMenuItem2ActionPerformed(evt);
                    }
                });
                jMenu5.add(jMenuItem2);

                jMenuBarClientChatForm.add(jMenu5);

                setJMenuBar(jMenuBarClientChatForm);

                pack();
                setLocationRelativeTo(null);
            }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageActionPerformed
        // TODO add your handling code here:
        if(jTextFieldMessage.getText().charAt(0) == '/'){
            String sendToUser = jTextFieldMessage.getText().substring(1, jTextFieldMessage.getText().indexOf(" "));
            System.out.println(sendToUser);
            String msg = jTextFieldMessage.getText().substring(jTextFieldMessage.getText().indexOf(" "));
            System.out.println(msg);
            try {
                String content = username + " " + sendToUser + " " + msg;
                dos.writeUTF("CMD_CHAT " + content);
                appendMyMessage(" " + jTextFieldMessage.getText(), username);
                jTextFieldMessage.setText("");
            } catch (IOException iOException) {
                System.out.println(iOException.getMessage());
                appendMessage(" Unable to Send Message now, Server is not available at this time please try again later or Restart this Application.!", "Error", Color.RED, Color.RED);
            }
        }
        else {
            try {
                String content = username + " " + jTextFieldMessage.getText();
                dos.writeUTF("CMD_CHATALL " + content);
                appendMyMessage(" " + jTextFieldMessage.getText(), username);
                jTextFieldMessage.setText("");
            } catch (IOException iOException) {
                System.out.println(iOException.getMessage());
                appendMessage(" Unable to Send Message now, Server is not available at this time please try again later or Restart this Application.!", "Error", Color.RED, Color.RED);
            }            
        }
    }//GEN-LAST:event_jButtonSendMessageActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void sendFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileMenuActionPerformed
        // TODO add your handling code here:
        if(!attachmentOpen){
            SendFile s = new SendFile();
            if(s.prepare(username, host, port, this)){
                s.setLocationRelativeTo(null);
                s.setVisible(true);
                attachmentOpen = true;
            } else {
                JOptionPane.showMessageDialog(this, "Unable to stablish File Sharing at this moment, please try again later.!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_sendFileMenuActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(this, "Close this Application.?");
        if(confirm == 0){
            try {
                socket.close();
            } catch (IOException iOException) {
                System.out.println(iOException.getMessage());
            }
            this.dispose();
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        try {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int browse = chooser.showOpenDialog(this);
            if(browse == JFileChooser.APPROVE_OPTION){
                this.mydownloadfolder = chooser.getSelectedFile().toString() +"\\";
            }
        } catch (HeadlessException e) {
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void LogoutMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutMenuActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Logout your Account.?");
        if(confirm == 0){
            try {
                socket.close();
                setVisible(false);
                /** Login Form **/
                new LoginForm().setVisible(true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_LogoutMenuActionPerformed

    private void emojiButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emojiButtonMousePressed
        // TODO add your handling code here:
        if(!jLayeredPaneEmoji.isVisible()){
            jLayeredPaneEmoji.setVisible(true);
            jLayeredPaneKaomoji.setVisible(false);
        } else {
            jLayeredPaneEmoji.setVisible(false);
            jLayeredPaneKaomoji.setVisible(false);
        }
    }//GEN-LAST:event_emojiButtonMousePressed

    private void emoji00MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji00MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji00] ");
    }//GEN-LAST:event_emoji00MousePressed

    private void emoji01MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji01MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji01] ");        
    }//GEN-LAST:event_emoji01MousePressed

    private void emoji02MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji02MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji02] ");  
    }//GEN-LAST:event_emoji02MousePressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                try {
                    Desktop.getDesktop().browse(new URI("https://lenhutnam298.github.io/chat-app/index.html"));
                } catch (IOException ex) {
                    Logger.getLogger(ClientMainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (URISyntaxException ex) {
                Logger.getLogger(ClientMainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    @SuppressWarnings("empty-statement")
    private void jGotoUnicastVoiceCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGotoUnicastVoiceCallActionPerformed
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(() -> {
            ClientVoiceChatForm clientVoiceChatForm =  new ClientVoiceChatForm();
            ImageIcon img = new ImageIcon("src/icons/icons8-outgoing-call-100.png");
            clientVoiceChatForm.setIconImage(img.getImage());
            clientVoiceChatForm.setLocationRelativeTo(null);;
            clientVoiceChatForm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            clientVoiceChatForm.setVisible(true);
        });
    }//GEN-LAST:event_jGotoUnicastVoiceCallActionPerformed

    @SuppressWarnings("empty-statement")
    private void jGotoBroadcastVoiceCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGotoBroadcastVoiceCallActionPerformed
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(() -> {
            ClientVoiceChatBroadCastForm clientVoiceChatBroadCastForm =  new ClientVoiceChatBroadCastForm();
            ImageIcon img = new ImageIcon("src/icons/icons8-outgoing-call-100.png");
            clientVoiceChatBroadCastForm.setIconImage(img.getImage());
            clientVoiceChatBroadCastForm.setLocationRelativeTo(null);;
            clientVoiceChatBroadCastForm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            clientVoiceChatBroadCastForm.setVisible(true);
        });       
    }//GEN-LAST:event_jGotoBroadcastVoiceCallActionPerformed

    private void emoji03MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji03MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji03] ");
    }//GEN-LAST:event_emoji03MousePressed

    private void emoji04MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji04MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji04] ");        
    }//GEN-LAST:event_emoji04MousePressed

    private void emoji05MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji05MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji05] ");
    }//GEN-LAST:event_emoji05MousePressed

    private void emoji06MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji06MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji06] ");
    }//GEN-LAST:event_emoji06MousePressed

    private void emoji07MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji07MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji07] ");        
    }//GEN-LAST:event_emoji07MousePressed

    private void emoji08MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji08MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji08] ");        
    }//GEN-LAST:event_emoji08MousePressed

    private void emoji09MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji09MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji09] ");       
    }//GEN-LAST:event_emoji09MousePressed

    private void emoji10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji10MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji10] ");
    }//GEN-LAST:event_emoji10MousePressed

    private void emoji11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji11MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji11] ");
    }//GEN-LAST:event_emoji11MousePressed

    private void emoji12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji12MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji12] ");
    }//GEN-LAST:event_emoji12MousePressed

    private void emoji13MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji13MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji13] ");
    }//GEN-LAST:event_emoji13MousePressed

    private void emoji14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji14MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji14] ");
    }//GEN-LAST:event_emoji14MousePressed

    private void emoji15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji15MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji15] ");
    }//GEN-LAST:event_emoji15MousePressed

    private void emoji16MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji16MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji16] ");
    }//GEN-LAST:event_emoji16MousePressed

    private void emoji17MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji17MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji17] ");
    }//GEN-LAST:event_emoji17MousePressed

    private void emoji18MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji18MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji18] ");
    }//GEN-LAST:event_emoji18MousePressed

    private void emoji19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji19MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji19] ");
    }//GEN-LAST:event_emoji19MousePressed

    private void emoji20MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji20MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji20] ");
    }//GEN-LAST:event_emoji20MousePressed

    private void emoji21MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji21MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji21] ");
    }//GEN-LAST:event_emoji21MousePressed

    private void emoji22MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji22MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji22] ");
    }//GEN-LAST:event_emoji22MousePressed

    private void emoji23MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji23MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji23] ");
    }//GEN-LAST:event_emoji23MousePressed

    private void emoji24MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji24MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji24] ");
    }//GEN-LAST:event_emoji24MousePressed

    private void emoji25MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji25MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji25] ");
    }//GEN-LAST:event_emoji25MousePressed

    private void emoji26MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji26MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji26] ");
    }//GEN-LAST:event_emoji26MousePressed

    private void emoji27MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji27MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji27] ");
    }//GEN-LAST:event_emoji27MousePressed

    private void emoji28MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji28MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji28] ");
    }//GEN-LAST:event_emoji28MousePressed

    private void emoji29MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji29MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji29] ");
    }//GEN-LAST:event_emoji29MousePressed

    private void emoji30MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji30MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji30] ");
    }//GEN-LAST:event_emoji30MousePressed

    private void emoji31MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji31MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji31] ");
    }//GEN-LAST:event_emoji31MousePressed

    private void emoji32MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji32MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji32] ");
    }//GEN-LAST:event_emoji32MousePressed

    private void emoji33MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji33MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji33] ");
    }//GEN-LAST:event_emoji33MousePressed

    private void emoji34MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji34MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji34] ");
    }//GEN-LAST:event_emoji34MousePressed

    private void emoji35MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji35MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji35] ");
    }//GEN-LAST:event_emoji35MousePressed

    private void emoji36MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji36MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji36] ");
    }//GEN-LAST:event_emoji36MousePressed

    private void emoji37MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji37MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji37] ");
    }//GEN-LAST:event_emoji37MousePressed

    private void emoji38MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji38MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji38] ");
    }//GEN-LAST:event_emoji38MousePressed

    private void emoji39MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji39MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji39] ");
    }//GEN-LAST:event_emoji39MousePressed

    private void emoji40MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji40MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji40] ");
    }//GEN-LAST:event_emoji40MousePressed

    private void emoji41MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji41MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji41] ");
    }//GEN-LAST:event_emoji41MousePressed

    private void emoji42MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji42MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji42] ");
    }//GEN-LAST:event_emoji42MousePressed

    private void emoji43MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji43MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji43] ");
    }//GEN-LAST:event_emoji43MousePressed

    private void emoji44MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji44MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji44] ");
    }//GEN-LAST:event_emoji44MousePressed

    private void emoji45MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji45MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji45] ");
    }//GEN-LAST:event_emoji45MousePressed

    private void emoji46MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji46MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji46] ");
    }//GEN-LAST:event_emoji46MousePressed

    private void emoji47MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji47MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji47] ");
    }//GEN-LAST:event_emoji47MousePressed

    private void emoji48MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji48MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji48] ");
    }//GEN-LAST:event_emoji48MousePressed

    private void emoji49MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji49MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji49] ");
    }//GEN-LAST:event_emoji49MousePressed

    private void emoji50MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji50MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji50] ");
    }//GEN-LAST:event_emoji50MousePressed

    private void emoji51MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji51MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji51] ");
    }//GEN-LAST:event_emoji51MousePressed

    private void emoji52MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji52MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji52] ");
    }//GEN-LAST:event_emoji52MousePressed

    private void emoji53MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji53MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji53] ");
    }//GEN-LAST:event_emoji53MousePressed

    private void emoji54MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji54MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji54] ");
    }//GEN-LAST:event_emoji54MousePressed

    private void emoji55MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji55MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji55] ");
    }//GEN-LAST:event_emoji55MousePressed

    private void emoji56MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji56MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji56] ");
    }//GEN-LAST:event_emoji56MousePressed

    private void emoji57MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji57MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji57] ");
    }//GEN-LAST:event_emoji57MousePressed

    private void emoji58MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji58MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji58] ");
    }//GEN-LAST:event_emoji58MousePressed

    private void emoji59MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji59MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji59] ");
    }//GEN-LAST:event_emoji59MousePressed

    private void emoji60MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji60MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji60] ");
    }//GEN-LAST:event_emoji60MousePressed

    private void emoji61MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji61MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji61] ");        
    }//GEN-LAST:event_emoji61MousePressed

    private void emoji62MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji62MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji62] ");        
    }//GEN-LAST:event_emoji62MousePressed

    private void emoji63MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji63MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji63] ");
    }//GEN-LAST:event_emoji63MousePressed

    private void emoji64MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji64MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji64] ");
    }//GEN-LAST:event_emoji64MousePressed

    private void emoji65MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji65MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji65] ");
    }//GEN-LAST:event_emoji65MousePressed

    private void emoji66MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji66MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji66] ");
    }//GEN-LAST:event_emoji66MousePressed

    private void emoji67MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji67MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji67] ");
    }//GEN-LAST:event_emoji67MousePressed

    private void emoji68MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji68MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji68] ");
    }//GEN-LAST:event_emoji68MousePressed

    private void emoji69MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji69MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji69] ");
    }//GEN-LAST:event_emoji69MousePressed

    private void emoji70MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji70MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji70] ");
    }//GEN-LAST:event_emoji70MousePressed

    private void emoji71MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji71MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji71] ");
    }//GEN-LAST:event_emoji71MousePressed

    private void emoji72MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji72MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji72] ");
    }//GEN-LAST:event_emoji72MousePressed

    private void emoji73MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji73MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji73] ");
    }//GEN-LAST:event_emoji73MousePressed

    private void emoji74MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji74MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji74] ");
    }//GEN-LAST:event_emoji74MousePressed

    private void emoji75MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji75MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji75] ");
    }//GEN-LAST:event_emoji75MousePressed

    private void emoji76MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji76MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji76] ");
    }//GEN-LAST:event_emoji76MousePressed

    private void emoji77MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji77MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji77] ");
    }//GEN-LAST:event_emoji77MousePressed

    private void emoji78MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji78MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji78] ");
    }//GEN-LAST:event_emoji78MousePressed

    private void emoji79MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji79MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji79] ");
    }//GEN-LAST:event_emoji79MousePressed

    private void emoji80MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji80MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji80] ");
    }//GEN-LAST:event_emoji80MousePressed

    private void emoji81MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji81MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji81] ");
    }//GEN-LAST:event_emoji81MousePressed

    private void emoji82MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji82MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji82] ");
    }//GEN-LAST:event_emoji82MousePressed

    private void emoji83MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji83MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji83] ");
    }//GEN-LAST:event_emoji83MousePressed

    private void emoji84MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji84MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji84] ");
    }//GEN-LAST:event_emoji84MousePressed

    private void emoji85MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji85MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji85] ");
    }//GEN-LAST:event_emoji85MousePressed

    private void emoji86MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji86MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji86] ");
    }//GEN-LAST:event_emoji86MousePressed

    private void emoji87MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji87MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji87] ");
    }//GEN-LAST:event_emoji87MousePressed

    private void emoji88MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji88MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji88] ");
    }//GEN-LAST:event_emoji88MousePressed

    private void emoji89MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji89MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji89] ");
    }//GEN-LAST:event_emoji89MousePressed

    private void emoji90MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji90MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji90] ");
    }//GEN-LAST:event_emoji90MousePressed

    private void emoji91MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji91MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji91] ");
    }//GEN-LAST:event_emoji91MousePressed

    private void emoji92MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji92MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji92] ");
    }//GEN-LAST:event_emoji92MousePressed

    private void emoji93MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji93MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji93] ");
    }//GEN-LAST:event_emoji93MousePressed

    private void emoji94MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji94MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji94] ");
    }//GEN-LAST:event_emoji94MousePressed

    private void emoji95MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji95MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji95] ");
    }//GEN-LAST:event_emoji95MousePressed

    private void emoji96MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji96MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji96] ");
    }//GEN-LAST:event_emoji96MousePressed

    private void emoji97MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji97MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji97] ");
    }//GEN-LAST:event_emoji97MousePressed

    private void emoji98MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji98MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji98] ");
    }//GEN-LAST:event_emoji98MousePressed

    private void emoji99MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji99MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji99] ");
    }//GEN-LAST:event_emoji99MousePressed

    private void emoji100MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji100MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji100] ");
    }//GEN-LAST:event_emoji100MousePressed

    private void emoji101MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji101MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji101] ");
    }//GEN-LAST:event_emoji101MousePressed

    private void emoji102MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji102MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji102] ");
    }//GEN-LAST:event_emoji102MousePressed

    private void emoji103MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji103MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji103] ");
    }//GEN-LAST:event_emoji103MousePressed

    private void emoji104MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji104MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji104] ");
    }//GEN-LAST:event_emoji104MousePressed

    private void emoji105MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji105MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji105] ");
    }//GEN-LAST:event_emoji105MousePressed

    private void emoji106MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji106MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji106] ");
    }//GEN-LAST:event_emoji106MousePressed

    private void emoji107MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji107MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji107] ");
    }//GEN-LAST:event_emoji107MousePressed

    private void emoji108MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji108MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji108] ");
    }//GEN-LAST:event_emoji108MousePressed

    private void emoji109MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji109MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji109] ");
    }//GEN-LAST:event_emoji109MousePressed

    private void emoji110MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji110MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji110] ");
    }//GEN-LAST:event_emoji110MousePressed

    private void emoji111MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji111MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji111] ");
    }//GEN-LAST:event_emoji111MousePressed

    private void emoji112MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji112MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji112] ");
    }//GEN-LAST:event_emoji112MousePressed

    private void emoji113MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji113MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji113] ");
    }//GEN-LAST:event_emoji113MousePressed

    private void emoji114MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji114MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji114] ");
    }//GEN-LAST:event_emoji114MousePressed

    private void emoji115MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji115MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji115] ");
    }//GEN-LAST:event_emoji115MousePressed

    private void emoji116MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji116MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji116] ");
    }//GEN-LAST:event_emoji116MousePressed

    private void emoji117MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji117MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji117] ");
    }//GEN-LAST:event_emoji117MousePressed

    private void emoji118MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji118MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji118] ");
    }//GEN-LAST:event_emoji118MousePressed

    private void emoji119MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji119MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji119] ");
    }//GEN-LAST:event_emoji119MousePressed

    private void emoji120MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji120MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji120] ");
    }//GEN-LAST:event_emoji120MousePressed

    private void emoji121MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji121MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji121] ");
    }//GEN-LAST:event_emoji121MousePressed

    private void emoji122MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji122MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji122] ");
    }//GEN-LAST:event_emoji122MousePressed

    private void emoji123MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji123MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji123] ");
    }//GEN-LAST:event_emoji123MousePressed

    private void emoji124MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji124MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji124] ");
    }//GEN-LAST:event_emoji124MousePressed

    private void emoji125MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji125MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji125] ");
    }//GEN-LAST:event_emoji125MousePressed

    private void emoji126MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji126MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji126] ");
    }//GEN-LAST:event_emoji126MousePressed

    private void emoji127MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji127MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji127] ");
    }//GEN-LAST:event_emoji127MousePressed

    private void emoji128MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji128MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji128] ");
    }//GEN-LAST:event_emoji128MousePressed

    private void emoji129MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji129MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji129] ");
    }//GEN-LAST:event_emoji129MousePressed

    private void emoji130MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji130MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji130] ");
    }//GEN-LAST:event_emoji130MousePressed

    private void emoji131MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji131MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji131] ");
    }//GEN-LAST:event_emoji131MousePressed

    private void emoji132MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji132MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji132] ");
    }//GEN-LAST:event_emoji132MousePressed

    private void emoji133MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji133MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji133] ");
    }//GEN-LAST:event_emoji133MousePressed

    private void emoji134MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emoji134MousePressed
        // TODO add your handling code here:
        String messageCurrent = jTextFieldMessage.getText();
        jTextFieldMessage.setText(messageCurrent + " [emoji134] ");
    }//GEN-LAST:event_emoji134MousePressed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        String information = "Coder: Le Nhut Nam\nChat App Project (c) July 2020\nMIT License\nOpen Source";
        JOptionPane.showMessageDialog(this, information, "About", JOptionPane.DEFAULT_OPTION);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        // TODO add your handling code here:
        if(!jLayeredPaneKaomoji.isVisible()){
            jLayeredPaneEmoji.setVisible(false);
            resetVisibleKaomojiComponents();
            type01.setVisible(true);
            jLayeredPaneKaomoji.setVisible(true);
        }else {
            jLayeredPaneEmoji.setVisible(true);
            resetVisibleKaomojiComponents();
            jLayeredPaneKaomoji.setVisible(false);
        }
    }//GEN-LAST:event_jLabel1MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type01.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type02.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type03.setVisible(true);        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type04.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type05.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type06.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        resetVisibleKaomojiComponents();
        type07.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(() -> {
            new ClientVideoChatForm().setVisible(true);
        });          
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(() -> {
            ClientVideoChatForm clientVideoChatForm =  new ClientVideoChatForm();
            ImageIcon img = new ImageIcon("src/icons/icons8-video-call-96.png");
            clientVideoChatForm.setIconImage(img.getImage());
            clientVideoChatForm.setLocationRelativeTo(null);;
            clientVideoChatForm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            clientVideoChatForm.setVisible(true);
        });   
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    // Start set up for Kaomoji Type 01
    
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton8.getText() + " ");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton9.getText() + " ");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton10.getText() + " ");
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton11.getText() + " ");
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton12.getText() + " ");
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton13.getText() + " ");
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton14.getText() + " ");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton15.getText() + " ");
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton16.getText() + " ");
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton17.getText() + " ");
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton18.getText() + " ");
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton19.getText() + " ");
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton20.getText() + " ");
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton21.getText() + " ");
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton22.getText() + " ");
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton23.getText() + " ");
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton24.getText() + " ");
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton25.getText() + " ");
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton26.getText() + " ");
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton27.getText() + " ");
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton28.getText() + " ");
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton29.getText() + " ");
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton30.getText() + " ");
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton31.getText() + " ");
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton32.getText() + " ");
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton33.getText() + " ");
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton34.getText() + " ");
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton35.getText() + " ");
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton36.getText() + " ");
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton37.getText() + " ");
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton38.getText() + " ");
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton39.getText() + " ");
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton40.getText() + " ");
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton41.getText() + " ");
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton42.getText() + " ");
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton43.getText() + " ");
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton44.getText() + " ");
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton45.getText() + " ");
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton46.getText() + " ");
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton47.getText() + " ");
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton48.getText() + " ");
    }//GEN-LAST:event_jButton48ActionPerformed

    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton49.getText() + " ");
    }//GEN-LAST:event_jButton49ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton50.getText() + " ");
    }//GEN-LAST:event_jButton50ActionPerformed

    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton51.getText() + " ");
    }//GEN-LAST:event_jButton51ActionPerformed

    private void jButton52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton52.getText() + " ");
    }//GEN-LAST:event_jButton52ActionPerformed

    private void jButton53ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton53ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton53.getText() + " ");
    }//GEN-LAST:event_jButton53ActionPerformed

    private void jButton54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton54ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton54.getText() + " ");
    }//GEN-LAST:event_jButton54ActionPerformed

    private void jButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton55ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton55.getText() + " ");
    }//GEN-LAST:event_jButton55ActionPerformed

    private void jButton56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton56ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton56.getText() + " ");
    }//GEN-LAST:event_jButton56ActionPerformed

    private void jButton57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton57ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton57.getText() + " ");
    }//GEN-LAST:event_jButton57ActionPerformed

    private void jButton58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton58ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton58.getText() + " ");
    }//GEN-LAST:event_jButton58ActionPerformed

    private void jButton59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton59ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton59.getText() + " ");
    }//GEN-LAST:event_jButton59ActionPerformed

    private void jButton60ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton60ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton60.getText() + " ");
    }//GEN-LAST:event_jButton60ActionPerformed

    private void jButton61ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton61ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton61.getText() + " ");
    }//GEN-LAST:event_jButton61ActionPerformed

    private void jButton62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton62ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton62.getText() + " ");
    }//GEN-LAST:event_jButton62ActionPerformed

    private void jButton63ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton63ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton63.getText() + " ");
    }//GEN-LAST:event_jButton63ActionPerformed

    private void jButton64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton64ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton64.getText() + " ");
    }//GEN-LAST:event_jButton64ActionPerformed

    private void jButton65ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton65ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton65.getText() + " ");
    }//GEN-LAST:event_jButton65ActionPerformed

    private void jButton66ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton66ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton66.getText() + " ");
    }//GEN-LAST:event_jButton66ActionPerformed

    private void jButton67ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton67ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton67.getText() + " ");
    }//GEN-LAST:event_jButton67ActionPerformed

    private void jButton68ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton68ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton68.getText() + " ");
    }//GEN-LAST:event_jButton68ActionPerformed

    private void jButton69ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton69ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton69.getText() + " ");
    }//GEN-LAST:event_jButton69ActionPerformed

    private void jButton70ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton70ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton70.getText() + " ");
    }//GEN-LAST:event_jButton70ActionPerformed

    private void jButton71ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton71ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton71.getText() + " ");
    }//GEN-LAST:event_jButton71ActionPerformed

    private void jButton72ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton72ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton72.getText() + " ");
    }//GEN-LAST:event_jButton72ActionPerformed

    private void jButton73ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton73ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton73.getText() + " ");
    }//GEN-LAST:event_jButton73ActionPerformed

    private void jButton74ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton74ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton74.getText() + " ");
    }//GEN-LAST:event_jButton74ActionPerformed

    private void jButton75ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton75ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton75.getText() + " ");
    }//GEN-LAST:event_jButton75ActionPerformed

    private void jButton76ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton76ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton76.getText() + " ");
    }//GEN-LAST:event_jButton76ActionPerformed

    private void jButton77ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton77ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton77.getText() + " ");
    }//GEN-LAST:event_jButton77ActionPerformed

    private void jButton78ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton78ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton78.getText() + " ");
    }//GEN-LAST:event_jButton78ActionPerformed

    private void jButton79ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton79ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton79.getText() + " ");
    }//GEN-LAST:event_jButton79ActionPerformed

    private void jButton80ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton80ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton80.getText() + " ");
    }//GEN-LAST:event_jButton80ActionPerformed

    private void jButton81ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton81ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton81.getText() + " ");
    }//GEN-LAST:event_jButton81ActionPerformed

    private void jButton84ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton84ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton84.getText() + " ");
    }//GEN-LAST:event_jButton84ActionPerformed

    private void jButton82ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton82ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton82.getText() + " ");
    }//GEN-LAST:event_jButton82ActionPerformed

    private void jButton83ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton83ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton83.getText() + " ");
    }//GEN-LAST:event_jButton83ActionPerformed

    private void jButton85ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton85ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton85.getText() + " ");
    }//GEN-LAST:event_jButton85ActionPerformed
    // End set up for Kaomoji Type 01
    
    // Start set up for Kaomoji Type 02
    private void jButton86ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton86ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton86.getText() + " ");
    }//GEN-LAST:event_jButton86ActionPerformed

    private void jButton87ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton87ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton87.getText() + " ");
    }//GEN-LAST:event_jButton87ActionPerformed

    private void jButton88ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton88ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton88.getText() + " ");
    }//GEN-LAST:event_jButton88ActionPerformed

    private void jButton89ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton89ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton89.getText() + " ");
    }//GEN-LAST:event_jButton89ActionPerformed

    private void jButton90ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton90ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton90.getText() + " ");
    }//GEN-LAST:event_jButton90ActionPerformed

    private void jButton91ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton91ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton91.getText() + " ");
    }//GEN-LAST:event_jButton91ActionPerformed

    private void jButton92ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton92ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton92.getText() + " ");
    }//GEN-LAST:event_jButton92ActionPerformed

    private void jButton93ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton93ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton93.getText() + " ");
    }//GEN-LAST:event_jButton93ActionPerformed

    private void jButton94ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton94ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton94.getText() + " ");
    }//GEN-LAST:event_jButton94ActionPerformed

    private void jButton95ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton95ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton95.getText() + " ");
    }//GEN-LAST:event_jButton95ActionPerformed

    private void jButton96ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton96ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton96.getText() + " ");
    }//GEN-LAST:event_jButton96ActionPerformed

    private void jButton97ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton97ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton97.getText() + " ");
    }//GEN-LAST:event_jButton97ActionPerformed

    private void jButton98ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton98ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton98.getText() + " ");
    }//GEN-LAST:event_jButton98ActionPerformed

    private void jButton99ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton99ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton99.getText() + " ");
    }//GEN-LAST:event_jButton99ActionPerformed

    private void jButton100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton100ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton100.getText() + " ");
    }//GEN-LAST:event_jButton100ActionPerformed

    private void jButton101ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton101ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton101.getText() + " ");
    }//GEN-LAST:event_jButton101ActionPerformed

    private void jButton102ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton102ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton102.getText() + " ");
    }//GEN-LAST:event_jButton102ActionPerformed

    private void jButton103ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton103ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton103.getText() + " ");
    }//GEN-LAST:event_jButton103ActionPerformed

    private void jButton104ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton104ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton104.getText() + " ");
    }//GEN-LAST:event_jButton104ActionPerformed

    private void jButton105ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton105ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton105.getText() + " ");
    }//GEN-LAST:event_jButton105ActionPerformed

    private void jButton106ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton106ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton106.getText() + " ");
    }//GEN-LAST:event_jButton106ActionPerformed

    private void jButton107ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton107ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton107.getText() + " ");
    }//GEN-LAST:event_jButton107ActionPerformed

    private void jButton108ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton108ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton108.getText() + " ");
    }//GEN-LAST:event_jButton108ActionPerformed

    private void jButton109ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton109ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton109.getText() + " ");
    }//GEN-LAST:event_jButton109ActionPerformed

    private void jButton110ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton110ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton110.getText() + " ");
    }//GEN-LAST:event_jButton110ActionPerformed

    private void jButton111ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton111ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton111.getText() + " ");
    }//GEN-LAST:event_jButton111ActionPerformed

    private void jButton112ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton112ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton112.getText() + " ");
    }//GEN-LAST:event_jButton112ActionPerformed

    private void jButton113ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton113ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton113.getText() + " ");
    }//GEN-LAST:event_jButton113ActionPerformed

    private void jButton114ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton114ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton114.getText() + " ");
    }//GEN-LAST:event_jButton114ActionPerformed

    private void jButton115ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton115ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton115.getText() + " ");
    }//GEN-LAST:event_jButton115ActionPerformed

    private void jButton116ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton116ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton116.getText() + " ");
    }//GEN-LAST:event_jButton116ActionPerformed

    private void jButton117ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton117ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton117.getText() + " ");
    }//GEN-LAST:event_jButton117ActionPerformed

    private void jButton118ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton118ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton118.getText() + " ");
    }//GEN-LAST:event_jButton118ActionPerformed

    private void jButton119ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton119ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton119.getText() + " ");
    }//GEN-LAST:event_jButton119ActionPerformed

    private void jButton120ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton120ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton120.getText() + " ");
    }//GEN-LAST:event_jButton120ActionPerformed

    private void jButton121ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton121ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton121.getText() + " ");
    }//GEN-LAST:event_jButton121ActionPerformed

    private void jButton122ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton122ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton122.getText() + " ");
    }//GEN-LAST:event_jButton122ActionPerformed

    private void jButton123ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton123ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton123.getText() + " ");
    }//GEN-LAST:event_jButton123ActionPerformed

    private void jButton124ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton124ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton124.getText() + " ");
    }//GEN-LAST:event_jButton124ActionPerformed

    private void jButton125ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton125ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton125.getText() + " ");
    }//GEN-LAST:event_jButton125ActionPerformed

    private void jButton126ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton126ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton126.getText() + " ");
    }//GEN-LAST:event_jButton126ActionPerformed

    private void jButton127ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton127ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton127.getText() + " ");
    }//GEN-LAST:event_jButton127ActionPerformed

    private void jButton128ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton128ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton128.getText() + " ");
    }//GEN-LAST:event_jButton128ActionPerformed

    private void jButton129ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton129ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton129.getText() + " ");
    }//GEN-LAST:event_jButton129ActionPerformed

    private void jButton130ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton130ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton130.getText() + " ");
    }//GEN-LAST:event_jButton130ActionPerformed

    private void jButton131ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton131ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton131.getText() + " ");
    }//GEN-LAST:event_jButton131ActionPerformed

    private void jButton132ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton132ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton132.getText() + " ");
    }//GEN-LAST:event_jButton132ActionPerformed

    private void jButton133ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton133ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton133.getText() + " ");
    }//GEN-LAST:event_jButton133ActionPerformed

    private void jButton134ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton134ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton134.getText() + " ");
    }//GEN-LAST:event_jButton134ActionPerformed

    private void jButton135ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton135ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton135.getText() + " ");
    }//GEN-LAST:event_jButton135ActionPerformed

    private void jButton136ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton136ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton136.getText() + " ");
    }//GEN-LAST:event_jButton136ActionPerformed

    private void jButton137ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton137ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton137.getText() + " ");
    }//GEN-LAST:event_jButton137ActionPerformed

    private void jButton138ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton138ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton138.getText() + " ");
    }//GEN-LAST:event_jButton138ActionPerformed

    private void jButton139ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton139ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton138.getText() + " ");
    }//GEN-LAST:event_jButton139ActionPerformed
    // End set up for Kaomoji Type 02
    
    // Start set up for Kaomoji Type 03
    private void jButton140ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton140ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton140.getText() + " ");
    }//GEN-LAST:event_jButton140ActionPerformed

    private void jButton141ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton141ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton141.getText() + " ");
    }//GEN-LAST:event_jButton141ActionPerformed

    private void jButton142ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton142ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton142.getText() + " ");
    }//GEN-LAST:event_jButton142ActionPerformed

    private void jButton143ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton143ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton143.getText() + " ");
    }//GEN-LAST:event_jButton143ActionPerformed

    private void jButton144ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton144ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton144.getText() + " ");
    }//GEN-LAST:event_jButton144ActionPerformed

    private void jButton145ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton145ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton145.getText() + " ");
    }//GEN-LAST:event_jButton145ActionPerformed

    private void jButton146ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton146ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton146.getText() + " ");
    }//GEN-LAST:event_jButton146ActionPerformed

    private void jButton147ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton147ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton147.getText() + " ");
    }//GEN-LAST:event_jButton147ActionPerformed

    private void jButton148ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton148ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton148.getText() + " ");
    }//GEN-LAST:event_jButton148ActionPerformed

    private void jButton149ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton149ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton149.getText() + " ");
    }//GEN-LAST:event_jButton149ActionPerformed

    private void jButton150ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton150ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton150.getText() + " ");
    }//GEN-LAST:event_jButton150ActionPerformed

    private void jButton151ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton151ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton151.getText() + " ");
    }//GEN-LAST:event_jButton151ActionPerformed

    private void jButton152ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton152ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton152.getText() + " ");
    }//GEN-LAST:event_jButton152ActionPerformed

    private void jButton153ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton153ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton153.getText() + " ");
    }//GEN-LAST:event_jButton153ActionPerformed

    private void jButton154ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton154ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton154.getText() + " ");
    }//GEN-LAST:event_jButton154ActionPerformed

    private void jButton155ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton155ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton155.getText() + " ");
    }//GEN-LAST:event_jButton155ActionPerformed

    private void jButton156ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton156ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton156.getText() + " ");
    }//GEN-LAST:event_jButton156ActionPerformed

    private void jButton157ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton157ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton157.getText() + " ");
    }//GEN-LAST:event_jButton157ActionPerformed

    private void jButton158ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton158ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton158.getText() + " ");
    }//GEN-LAST:event_jButton158ActionPerformed

    private void jButton159ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton159ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton159.getText() + " ");
    }//GEN-LAST:event_jButton159ActionPerformed

    private void jButton160ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton160ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton160.getText() + " ");
    }//GEN-LAST:event_jButton160ActionPerformed

    private void jButton161ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton161ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton161.getText() + " ");
    }//GEN-LAST:event_jButton161ActionPerformed

    private void jButton162ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton162ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton162.getText() + " ");
    }//GEN-LAST:event_jButton162ActionPerformed

    private void jButton163ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton163ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton163.getText() + " ");
    }//GEN-LAST:event_jButton163ActionPerformed

    private void jButton164ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton164ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton164.getText() + " ");
    }//GEN-LAST:event_jButton164ActionPerformed

    private void jButton165ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton165ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton165.getText() + " ");
    }//GEN-LAST:event_jButton165ActionPerformed

    private void jButton166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton166ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton166.getText() + " ");
    }//GEN-LAST:event_jButton166ActionPerformed

    private void jButton167ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton167ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton167.getText() + " ");
    }//GEN-LAST:event_jButton167ActionPerformed

    private void jButton168ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton168ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton168.getText() + " ");
    }//GEN-LAST:event_jButton168ActionPerformed

    private void jButton169ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton169ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton169.getText() + " ");
    }//GEN-LAST:event_jButton169ActionPerformed

    private void jButton170ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton170ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton170.getText() + " ");
    }//GEN-LAST:event_jButton170ActionPerformed

    private void jButton171ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton171ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton171.getText() + " ");
    }//GEN-LAST:event_jButton171ActionPerformed

    private void jButton172ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton172ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton172.getText() + " ");
    }//GEN-LAST:event_jButton172ActionPerformed

    private void jButton173ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton173ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton173.getText() + " ");
    }//GEN-LAST:event_jButton173ActionPerformed

    private void jButton174ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton174ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton174.getText() + " ");
    }//GEN-LAST:event_jButton174ActionPerformed

    private void jButton175ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton175ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton175.getText() + " ");
    }//GEN-LAST:event_jButton175ActionPerformed

    private void jButton176ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton176ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton176.getText() + " ");
    }//GEN-LAST:event_jButton176ActionPerformed

    private void jButton177ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton177ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton177.getText() + " ");
    }//GEN-LAST:event_jButton177ActionPerformed

    private void jButton178ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton178ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton178.getText() + " ");
    }//GEN-LAST:event_jButton178ActionPerformed

    private void jButton179ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton179ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton179.getText() + " ");
    }//GEN-LAST:event_jButton179ActionPerformed

    private void jButton180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton180ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton180.getText() + " ");
    }//GEN-LAST:event_jButton180ActionPerformed

    private void jButton181ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton181ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton181.getText() + " ");
    }//GEN-LAST:event_jButton181ActionPerformed

    private void jButton182ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton182ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton182.getText() + " ");
    }//GEN-LAST:event_jButton182ActionPerformed

    private void jButton183ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton183ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton183.getText() + " ");
    }//GEN-LAST:event_jButton183ActionPerformed

    private void jButton184ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton184ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton184.getText() + " ");
    }//GEN-LAST:event_jButton184ActionPerformed

    private void jButton185ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton185ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton185.getText() + " ");
    }//GEN-LAST:event_jButton185ActionPerformed

    private void jButton186ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton186ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton186.getText() + " ");
    }//GEN-LAST:event_jButton186ActionPerformed

    private void jButton187ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton187ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton187.getText() + " ");
    }//GEN-LAST:event_jButton187ActionPerformed

    private void jButton188ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton188ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton188.getText() + " ");
    }//GEN-LAST:event_jButton188ActionPerformed

    private void jButton189ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton189ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton189.getText() + " ");
    }//GEN-LAST:event_jButton189ActionPerformed

    private void jButton190ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton190ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton190.getText() + " ");
    }//GEN-LAST:event_jButton190ActionPerformed

    private void jButton191ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton191ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton191.getText() + " ");
    }//GEN-LAST:event_jButton191ActionPerformed

    private void jButton192ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton192ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton192.getText() + " ");
    }//GEN-LAST:event_jButton192ActionPerformed

    private void jButton193ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton193ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton193.getText() + " ");
    }//GEN-LAST:event_jButton193ActionPerformed

    private void jButton194ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton194ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton194.getText() + " ");
    }//GEN-LAST:event_jButton194ActionPerformed

    private void jButton195ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton195ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton195.getText() + " ");
    }//GEN-LAST:event_jButton195ActionPerformed

    private void jButton196ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton196ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton196.getText() + " ");
    }//GEN-LAST:event_jButton196ActionPerformed

    private void jButton197ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton197ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton197.getText() + " ");
    }//GEN-LAST:event_jButton197ActionPerformed

    private void jButton198ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton198ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton198.getText() + " ");
    }//GEN-LAST:event_jButton198ActionPerformed

    private void jButton199ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton199ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton199.getText() + " ");
    }//GEN-LAST:event_jButton199ActionPerformed

    private void jButton200ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton200ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton200.getText() + " ");
    }//GEN-LAST:event_jButton200ActionPerformed
    // End set up for Kaomoji Type 03
    
    // Start set up for Kaomoji Type 04
    private void jButton201ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton201ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton201.getText() + " ");
    }//GEN-LAST:event_jButton201ActionPerformed

    private void jButton202ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton202ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton202.getText() + " ");
    }//GEN-LAST:event_jButton202ActionPerformed

    private void jButton203ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton203ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton203.getText() + " ");
    }//GEN-LAST:event_jButton203ActionPerformed

    private void jButton204ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton204ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton204.getText() + " ");
    }//GEN-LAST:event_jButton204ActionPerformed

    private void jButton205ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton205ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton205.getText() + " ");
    }//GEN-LAST:event_jButton205ActionPerformed

    private void jButton206ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton206ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton206.getText() + " ");
    }//GEN-LAST:event_jButton206ActionPerformed

    private void jButton207ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton207ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton207.getText() + " ");
    }//GEN-LAST:event_jButton207ActionPerformed

    private void jButton208ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton208ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton208.getText() + " ");
    }//GEN-LAST:event_jButton208ActionPerformed

    private void jButton209ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton209ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton209.getText() + " ");
    }//GEN-LAST:event_jButton209ActionPerformed

    private void jButton210ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton210ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton210.getText() + " ");
    }//GEN-LAST:event_jButton210ActionPerformed

    private void jButton211ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton211ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton211.getText() + " ");
    }//GEN-LAST:event_jButton211ActionPerformed

    private void jButton212ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton212ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton212.getText() + " ");
    }//GEN-LAST:event_jButton212ActionPerformed

    private void jButton213ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton213ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton213.getText() + " ");
    }//GEN-LAST:event_jButton213ActionPerformed

    private void jButton214ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton214ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton214.getText() + " ");
    }//GEN-LAST:event_jButton214ActionPerformed

    private void jButton215ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton215ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton215.getText() + " ");
    }//GEN-LAST:event_jButton215ActionPerformed

    private void jButton216ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton216ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton216.getText() + " ");
    }//GEN-LAST:event_jButton216ActionPerformed

    private void jButton217ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton217ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton217.getText() + " ");
    }//GEN-LAST:event_jButton217ActionPerformed

    private void jButton218ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton218ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton218.getText() + " ");
    }//GEN-LAST:event_jButton218ActionPerformed

    private void jButton219ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton219ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton219.getText() + " ");
    }//GEN-LAST:event_jButton219ActionPerformed

    private void jButton220ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton220ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton220.getText() + " ");
    }//GEN-LAST:event_jButton220ActionPerformed

    private void jButton221ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton221ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton221.getText() + " ");
    }//GEN-LAST:event_jButton221ActionPerformed

    private void jButton222ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton222ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton222.getText() + " ");
    }//GEN-LAST:event_jButton222ActionPerformed

    private void jButton223ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton223ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton223.getText() + " ");
    }//GEN-LAST:event_jButton223ActionPerformed

    private void jButton224ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton224ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton224.getText() + " ");
    }//GEN-LAST:event_jButton224ActionPerformed

    private void jButton225ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton225ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton225.getText() + " ");
    }//GEN-LAST:event_jButton225ActionPerformed

    private void jButton226ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton226ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton226.getText() + " ");
    }//GEN-LAST:event_jButton226ActionPerformed

    private void jButton227ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton227ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton227.getText() + " ");
    }//GEN-LAST:event_jButton227ActionPerformed

    private void jButton228ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton228ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton228.getText() + " ");
    }//GEN-LAST:event_jButton228ActionPerformed

    private void jButton229ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton229ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton229.getText() + " ");
    }//GEN-LAST:event_jButton229ActionPerformed

    private void jButton230ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton230ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton230.getText() + " ");
    }//GEN-LAST:event_jButton230ActionPerformed

    private void jButton231ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton231ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton231.getText() + " ");
    }//GEN-LAST:event_jButton231ActionPerformed

    private void jButton232ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton232ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton232.getText() + " ");
    }//GEN-LAST:event_jButton232ActionPerformed

    private void jButton233ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton233ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton233.getText() + " ");
    }//GEN-LAST:event_jButton233ActionPerformed

    private void jButton234ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton234ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton234.getText() + " ");
    }//GEN-LAST:event_jButton234ActionPerformed

    private void jButton235ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton235ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton235.getText() + " ");
    }//GEN-LAST:event_jButton235ActionPerformed

    private void jButton236ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton236ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton236.getText() + " ");
    }//GEN-LAST:event_jButton236ActionPerformed

    private void jButton237ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton237ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton237.getText() + " ");
    }//GEN-LAST:event_jButton237ActionPerformed

    private void jButton238ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton238ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton238.getText() + " ");
    }//GEN-LAST:event_jButton238ActionPerformed

    private void jButton239ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton239ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton239.getText() + " ");
    }//GEN-LAST:event_jButton239ActionPerformed

    private void jButton240ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton240ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton240.getText() + " ");
    }//GEN-LAST:event_jButton240ActionPerformed

    private void jButton241ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton241ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton241.getText() + " ");
    }//GEN-LAST:event_jButton241ActionPerformed

    private void jButton242ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton242ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton242.getText() + " ");
    }//GEN-LAST:event_jButton242ActionPerformed

    private void jButton243ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton243ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton243.getText() + " ");
    }//GEN-LAST:event_jButton243ActionPerformed

    private void jButton244ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton244ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton244.getText() + " ");
    }//GEN-LAST:event_jButton244ActionPerformed

    private void jButton245ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton245ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton245.getText() + " ");
    }//GEN-LAST:event_jButton245ActionPerformed

    private void jButton246ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton246ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton246.getText() + " ");
    }//GEN-LAST:event_jButton246ActionPerformed

    private void jButton247ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton247ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton247.getText() + " ");
    }//GEN-LAST:event_jButton247ActionPerformed

    private void jButton248ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton248ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton248.getText() + " ");
    }//GEN-LAST:event_jButton248ActionPerformed

    private void jButton249ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton249ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton249.getText() + " ");
    }//GEN-LAST:event_jButton249ActionPerformed

    private void jButton250ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton250ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton250.getText() + " ");
    }//GEN-LAST:event_jButton250ActionPerformed

    private void jButton251ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton251ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton251.getText() + " ");
    }//GEN-LAST:event_jButton251ActionPerformed

    private void jButton252ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton252ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton252.getText() + " ");
    }//GEN-LAST:event_jButton252ActionPerformed

    private void jButton253ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton253ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton253.getText() + " ");
    }//GEN-LAST:event_jButton253ActionPerformed

    private void jButton254ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton254ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton254.getText() + " ");
    }//GEN-LAST:event_jButton254ActionPerformed

    private void jButton255ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton255ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton255.getText() + " ");
    }//GEN-LAST:event_jButton255ActionPerformed

    private void jButton256ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton256ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton256.getText() + " ");
    }//GEN-LAST:event_jButton256ActionPerformed

    private void jButton257ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton257ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton257.getText() + " ");
    }//GEN-LAST:event_jButton257ActionPerformed

    private void jButton258ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton258ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton258.getText() + " ");
    }//GEN-LAST:event_jButton258ActionPerformed

    private void jButton259ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton259ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton259.getText() + " ");
    }//GEN-LAST:event_jButton259ActionPerformed

    private void jButton260ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton260ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton260.getText() + " ");
    }//GEN-LAST:event_jButton260ActionPerformed

    private void jButton261ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton261ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton261.getText() + " ");
    }//GEN-LAST:event_jButton261ActionPerformed

    private void jButton262ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton262ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton262.getText() + " ");
    }//GEN-LAST:event_jButton262ActionPerformed

    private void jButton263ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton263ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton263.getText() + " ");
    }//GEN-LAST:event_jButton263ActionPerformed

    private void jButton264ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton264ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton264.getText() + " ");
    }//GEN-LAST:event_jButton264ActionPerformed

    private void jButton265ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton265ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton265.getText() + " ");
    }//GEN-LAST:event_jButton265ActionPerformed

    private void jButton266ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton266ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton266.getText() + " ");
    }//GEN-LAST:event_jButton266ActionPerformed

    private void jButton267ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton267ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton267.getText() + " ");
    }//GEN-LAST:event_jButton267ActionPerformed

    private void jButton268ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton268ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton268.getText() + " ");
    }//GEN-LAST:event_jButton268ActionPerformed

    private void jButton269ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton269ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton269.getText() + " ");
    }//GEN-LAST:event_jButton269ActionPerformed

    private void jButton270ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton270ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton270.getText() + " ");
    }//GEN-LAST:event_jButton270ActionPerformed

    private void jButton271ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton271ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton271.getText() + " ");
    }//GEN-LAST:event_jButton271ActionPerformed

    private void jButton272ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton272ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton272.getText() + " ");
    }//GEN-LAST:event_jButton272ActionPerformed

    private void jButton273ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton273ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton273.getText() + " ");
    }//GEN-LAST:event_jButton273ActionPerformed

    private void jButton274ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton274ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton274.getText() + " ");
    }//GEN-LAST:event_jButton274ActionPerformed

    private void jButton275ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton275ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton275.getText() + " ");
    }//GEN-LAST:event_jButton275ActionPerformed

    private void jButton276ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton276ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton276.getText() + " ");
    }//GEN-LAST:event_jButton276ActionPerformed

    private void jButton277ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton277ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton277.getText() + " ");
    }//GEN-LAST:event_jButton277ActionPerformed

    private void jButton278ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton278ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton278.getText() + " ");
    }//GEN-LAST:event_jButton278ActionPerformed

    private void jButton279ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton279ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton279.getText() + " ");
    }//GEN-LAST:event_jButton279ActionPerformed

    private void jButton280ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton280ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton280.getText() + " ");
    }//GEN-LAST:event_jButton280ActionPerformed

    private void jButton281ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton281ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton281.getText() + " ");
    }//GEN-LAST:event_jButton281ActionPerformed

    private void jButton282ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton282ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton282.getText() + " ");
    }//GEN-LAST:event_jButton282ActionPerformed

    private void jButton283ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton283ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton283.getText() + " ");
    }//GEN-LAST:event_jButton283ActionPerformed

    private void jButton284ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton284ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton284.getText() + " ");
    }//GEN-LAST:event_jButton284ActionPerformed

    private void jButton285ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton285ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton285.getText() + " ");
    }//GEN-LAST:event_jButton285ActionPerformed

    private void jButton286ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton286ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton286.getText() + " ");
    }//GEN-LAST:event_jButton286ActionPerformed

    private void jButton287ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton287ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton287.getText() + " ");
    }//GEN-LAST:event_jButton287ActionPerformed

    private void jButton288ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton288ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton288.getText() + " ");
    }//GEN-LAST:event_jButton288ActionPerformed

    private void jButton289ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton289ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton289.getText() + " ");
    }//GEN-LAST:event_jButton289ActionPerformed

    private void jButton290ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton290ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton290.getText() + " ");
    }//GEN-LAST:event_jButton290ActionPerformed

    private void jButton291ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton291ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton291.getText() + " ");
    }//GEN-LAST:event_jButton291ActionPerformed

    private void jButton292ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton292ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton292.getText() + " ");
    }//GEN-LAST:event_jButton292ActionPerformed
    // End set up for Kaomoji Type 4
    
    // Start set up for Kaomoji Type 5
    private void jButton293ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton293ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton293.getText() + " ");
    }//GEN-LAST:event_jButton293ActionPerformed

    private void jButton294ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton294ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton294.getText() + " ");
    }//GEN-LAST:event_jButton294ActionPerformed

    private void jButton295ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton295ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton295.getText() + " ");
    }//GEN-LAST:event_jButton295ActionPerformed

    private void jButton296ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton296ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton296.getText() + " ");
    }//GEN-LAST:event_jButton296ActionPerformed

    private void jButton297ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton297ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton297.getText() + " ");
    }//GEN-LAST:event_jButton297ActionPerformed

    private void jButton298ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton298ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton298.getText() + " ");
    }//GEN-LAST:event_jButton298ActionPerformed

    private void jButton299ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton299ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton299.getText() + " ");
    }//GEN-LAST:event_jButton299ActionPerformed

    private void jButton300ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton300ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton300.getText() + " ");
    }//GEN-LAST:event_jButton300ActionPerformed

    private void jButton301ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton301ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton301.getText() + " ");
    }//GEN-LAST:event_jButton301ActionPerformed

    private void jButton302ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton302ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton302.getText() + " ");
    }//GEN-LAST:event_jButton302ActionPerformed

    private void jButton303ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton303ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton303.getText() + " ");
    }//GEN-LAST:event_jButton303ActionPerformed

    private void jButton304ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton304ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton304.getText() + " ");
    }//GEN-LAST:event_jButton304ActionPerformed

    private void jButton305ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton305ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton305.getText() + " ");
    }//GEN-LAST:event_jButton305ActionPerformed

    private void jButton306ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton306ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton306.getText() + " ");
    }//GEN-LAST:event_jButton306ActionPerformed

    private void jButton307ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton307ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton307.getText() + " ");
    }//GEN-LAST:event_jButton307ActionPerformed

    private void jButton308ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton308ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton308.getText() + " ");
    }//GEN-LAST:event_jButton308ActionPerformed

    private void jButton309ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton309ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton309.getText() + " ");
    }//GEN-LAST:event_jButton309ActionPerformed

    private void jButton310ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton310ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton310.getText() + " ");
    }//GEN-LAST:event_jButton310ActionPerformed

    private void jButton311ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton311ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton311.getText() + " ");
    }//GEN-LAST:event_jButton311ActionPerformed

    private void jButton312ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton312ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton312.getText() + " ");
    }//GEN-LAST:event_jButton312ActionPerformed

    private void jButton313ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton313ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton313.getText() + " ");
    }//GEN-LAST:event_jButton313ActionPerformed

    private void jButton314ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton314ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton314.getText() + " ");
    }//GEN-LAST:event_jButton314ActionPerformed

    private void jButton315ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton315ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton315.getText() + " ");
    }//GEN-LAST:event_jButton315ActionPerformed

    private void jButton316ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton316ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton316.getText() + " ");
    }//GEN-LAST:event_jButton316ActionPerformed

    private void jButton317ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton317ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton317.getText() + " ");
    }//GEN-LAST:event_jButton317ActionPerformed

    private void jButton318ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton318ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton318.getText() + " ");
    }//GEN-LAST:event_jButton318ActionPerformed

    private void jButton319ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton319ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton319.getText() + " ");
    }//GEN-LAST:event_jButton319ActionPerformed

    private void jButton320ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton320ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton320.getText() + " ");
    }//GEN-LAST:event_jButton320ActionPerformed

    private void jButton321ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton321ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton321.getText() + " ");
    }//GEN-LAST:event_jButton321ActionPerformed

    private void jButton322ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton322ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton322.getText() + " ");
    }//GEN-LAST:event_jButton322ActionPerformed

    private void jButton323ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton323ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton323.getText() + " ");
    }//GEN-LAST:event_jButton323ActionPerformed

    private void jButton324ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton324ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton324.getText() + " ");
    }//GEN-LAST:event_jButton324ActionPerformed

    private void jButton389ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton389ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton389.getText() + " ");
    }//GEN-LAST:event_jButton389ActionPerformed

    private void jButton390ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton390ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton390.getText() + " ");
    }//GEN-LAST:event_jButton390ActionPerformed

    private void jButton391ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton391ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton391.getText() + " ");
    }//GEN-LAST:event_jButton391ActionPerformed

    private void jButton392ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton392ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton392.getText() + " ");
    }//GEN-LAST:event_jButton392ActionPerformed

    private void jButton393ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton393ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton393.getText() + " ");
    }//GEN-LAST:event_jButton393ActionPerformed

    private void jButton394ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton394ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton394.getText() + " ");
    }//GEN-LAST:event_jButton394ActionPerformed

    private void jButton395ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton395ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton395.getText() + " ");
    }//GEN-LAST:event_jButton395ActionPerformed

    private void jButton396ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton396ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton396.getText() + " ");
    }//GEN-LAST:event_jButton396ActionPerformed

    private void jButton397ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton397ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton397.getText() + " ");
    }//GEN-LAST:event_jButton397ActionPerformed

    private void jButton398ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton398ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton398.getText() + " ");
    }//GEN-LAST:event_jButton398ActionPerformed

    private void jButton399ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton399ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton399.getText() + " ");
    }//GEN-LAST:event_jButton399ActionPerformed

    private void jButton400ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton400ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton400.getText() + " ");
    }//GEN-LAST:event_jButton400ActionPerformed

    private void jButton401ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton401ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton401.getText() + " ");
    }//GEN-LAST:event_jButton401ActionPerformed

    private void jButton402ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton402ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton402.getText() + " ");
    }//GEN-LAST:event_jButton402ActionPerformed

    private void jButton403ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton403ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton403.getText() + " ");
    }//GEN-LAST:event_jButton403ActionPerformed

    private void jButton404ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton404ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton404.getText() + " ");
    }//GEN-LAST:event_jButton404ActionPerformed

    private void jButton405ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton405ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton405.getText() + " ");
    }//GEN-LAST:event_jButton405ActionPerformed

    private void jButton406ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton406ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton406.getText() + " ");
    }//GEN-LAST:event_jButton406ActionPerformed

    private void jButton407ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton407ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton407.getText() + " ");
    }//GEN-LAST:event_jButton407ActionPerformed

    private void jButton408ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton408ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton408.getText() + " ");
    }//GEN-LAST:event_jButton408ActionPerformed

    private void jButton409ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton409ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton409.getText() + " ");
    }//GEN-LAST:event_jButton409ActionPerformed

    private void jButton410ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton410ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton410.getText() + " ");
    }//GEN-LAST:event_jButton410ActionPerformed

    private void jButton411ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton411ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton411.getText() + " ");
    }//GEN-LAST:event_jButton411ActionPerformed

    private void jButton412ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton412ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton412.getText() + " ");
    }//GEN-LAST:event_jButton412ActionPerformed
    // End set up for Kaomoji Type 05
    
    // Start set up for Kaomoji Type 06
    private void jButton325ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton325ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton325.getText() + " ");
    }//GEN-LAST:event_jButton325ActionPerformed

    private void jButton326ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton326ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton326.getText() + " ");
    }//GEN-LAST:event_jButton326ActionPerformed

    private void jButton327ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton327ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton327.getText() + " ");
    }//GEN-LAST:event_jButton327ActionPerformed

    private void jButton328ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton328ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton328.getText() + " ");
    }//GEN-LAST:event_jButton328ActionPerformed

    private void jButton329ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton329ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton329.getText() + " ");
    }//GEN-LAST:event_jButton329ActionPerformed

    private void jButton330ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton330ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton330.getText() + " ");
    }//GEN-LAST:event_jButton330ActionPerformed

    private void jButton331ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton331ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton331.getText() + " ");
    }//GEN-LAST:event_jButton331ActionPerformed

    private void jButton332ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton332ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton332.getText() + " ");
    }//GEN-LAST:event_jButton332ActionPerformed

    private void jButton333ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton333ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton333.getText() + " ");
    }//GEN-LAST:event_jButton333ActionPerformed

    private void jButton334ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton334ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton334.getText() + " ");
    }//GEN-LAST:event_jButton334ActionPerformed

    private void jButton335ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton335ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton335.getText() + " ");
    }//GEN-LAST:event_jButton335ActionPerformed

    private void jButton336ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton336ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton336.getText() + " ");
    }//GEN-LAST:event_jButton336ActionPerformed

    private void jButton337ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton337ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton337.getText() + " ");
    }//GEN-LAST:event_jButton337ActionPerformed

    private void jButton338ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton338ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton338.getText() + " ");
    }//GEN-LAST:event_jButton338ActionPerformed

    private void jButton339ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton339ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton339.getText() + " ");
    }//GEN-LAST:event_jButton339ActionPerformed

    private void jButton340ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton340ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton340.getText() + " ");
    }//GEN-LAST:event_jButton340ActionPerformed

    private void jButton341ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton341ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton341.getText() + " ");
    }//GEN-LAST:event_jButton341ActionPerformed

    private void jButton342ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton342ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton342.getText() + " ");
    }//GEN-LAST:event_jButton342ActionPerformed

    private void jButton343ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton343ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton343.getText() + " ");
    }//GEN-LAST:event_jButton343ActionPerformed

    private void jButton344ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton344ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton344.getText() + " ");
    }//GEN-LAST:event_jButton344ActionPerformed

    private void jButton345ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton345ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton345.getText() + " ");
    }//GEN-LAST:event_jButton345ActionPerformed

    private void jButton346ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton346ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton346.getText() + " ");
    }//GEN-LAST:event_jButton346ActionPerformed

    private void jButton347ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton347ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton347.getText() + " ");
    }//GEN-LAST:event_jButton347ActionPerformed

    private void jButton348ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton348ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton348.getText() + " ");
    }//GEN-LAST:event_jButton348ActionPerformed

    private void jButton349ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton349ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton349.getText() + " ");
    }//GEN-LAST:event_jButton349ActionPerformed

    private void jButton350ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton350ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton350.getText() + " ");
    }//GEN-LAST:event_jButton350ActionPerformed

    private void jButton351ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton351ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton351.getText() + " ");
    }//GEN-LAST:event_jButton351ActionPerformed

    private void jButton352ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton352ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton352.getText() + " ");
    }//GEN-LAST:event_jButton352ActionPerformed

    private void jButton353ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton353ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton353.getText() + " ");
    }//GEN-LAST:event_jButton353ActionPerformed

    private void jButton354ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton354ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton354.getText() + " ");
    }//GEN-LAST:event_jButton354ActionPerformed

    private void jButton355ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton355ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton355.getText() + " ");
    }//GEN-LAST:event_jButton355ActionPerformed

    private void jButton356ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton356ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton356.getText() + " ");
    }//GEN-LAST:event_jButton356ActionPerformed
    // End set up for Kaomoji Type 06
    
    // Start set up for Kaomoji Type 07
    private void jButton357ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton357ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton357.getText() + " ");
    }//GEN-LAST:event_jButton357ActionPerformed

    private void jButton358ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton358ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton358.getText() + " ");
    }//GEN-LAST:event_jButton358ActionPerformed

    private void jButton359ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton359ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton359.getText() + " ");
    }//GEN-LAST:event_jButton359ActionPerformed

    private void jButton360ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton360ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton360.getText() + " ");
    }//GEN-LAST:event_jButton360ActionPerformed

    private void jButton361ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton361ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton361.getText() + " ");
    }//GEN-LAST:event_jButton361ActionPerformed

    private void jButton362ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton362ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton362.getText() + " ");
    }//GEN-LAST:event_jButton362ActionPerformed

    private void jButton363ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton363ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton363.getText() + " ");
    }//GEN-LAST:event_jButton363ActionPerformed

    private void jButton364ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton364ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton364.getText() + " ");
    }//GEN-LAST:event_jButton364ActionPerformed

    private void jButton365ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton365ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton365.getText() + " ");
    }//GEN-LAST:event_jButton365ActionPerformed

    private void jButton366ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton366ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton366.getText() + " ");
    }//GEN-LAST:event_jButton366ActionPerformed

    private void jButton367ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton367ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton367.getText() + " ");
    }//GEN-LAST:event_jButton367ActionPerformed

    private void jButton368ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton368ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton368.getText() + " ");
    }//GEN-LAST:event_jButton368ActionPerformed

    private void jButton369ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton369ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton369.getText() + " ");
    }//GEN-LAST:event_jButton369ActionPerformed

    private void jButton370ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton370ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton370.getText() + " ");
    }//GEN-LAST:event_jButton370ActionPerformed

    private void jButton371ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton371ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton371.getText() + " ");
    }//GEN-LAST:event_jButton371ActionPerformed

    private void jButton372ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton372ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton372.getText() + " ");
    }//GEN-LAST:event_jButton372ActionPerformed

    private void jButton373ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton373ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton373.getText() + " ");
    }//GEN-LAST:event_jButton373ActionPerformed

    private void jButton374ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton374ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton374.getText() + " ");
    }//GEN-LAST:event_jButton374ActionPerformed

    private void jButton375ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton375ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton375.getText() + " ");
    }//GEN-LAST:event_jButton375ActionPerformed

    private void jButton376ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton376ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton376.getText() + " ");
    }//GEN-LAST:event_jButton376ActionPerformed

    private void jButton377ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton377ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton377.getText() + " ");
    }//GEN-LAST:event_jButton377ActionPerformed

    private void jButton378ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton378ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton378.getText() + " ");
    }//GEN-LAST:event_jButton378ActionPerformed

    private void jButton379ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton379ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton379.getText() + " ");
    }//GEN-LAST:event_jButton379ActionPerformed

    private void jButton380ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton380ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton380.getText() + " ");
    }//GEN-LAST:event_jButton380ActionPerformed

    private void jButton381ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton381ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton381.getText() + " ");
    }//GEN-LAST:event_jButton381ActionPerformed

    private void jButton382ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton382ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton382.getText() + " ");
    }//GEN-LAST:event_jButton382ActionPerformed

    private void jButton383ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton383ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton383.getText() + " ");
    }//GEN-LAST:event_jButton383ActionPerformed

    private void jButton384ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton384ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton384.getText() + " ");
    }//GEN-LAST:event_jButton384ActionPerformed

    private void jButton385ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton385ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton385.getText() + " ");
    }//GEN-LAST:event_jButton385ActionPerformed

    private void jButton386ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton386ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton386.getText() + " ");
    }//GEN-LAST:event_jButton386ActionPerformed

    private void jButton387ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton387ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton387.getText() + " ");
    }//GEN-LAST:event_jButton387ActionPerformed

    private void jButton388ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton388ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton388.getText() + " ");
    }//GEN-LAST:event_jButton388ActionPerformed

    private void jButton413ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton413ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton413.getText() + " ");
    }//GEN-LAST:event_jButton413ActionPerformed

    private void jButton414ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton414ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton414.getText() + " ");
    }//GEN-LAST:event_jButton414ActionPerformed

    private void jButton415ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton415ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton415.getText() + " ");
    }//GEN-LAST:event_jButton415ActionPerformed

    private void jButton416ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton416ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton416.getText() + " ");
    }//GEN-LAST:event_jButton416ActionPerformed

    private void jButton417ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton417ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton417.getText() + " ");
    }//GEN-LAST:event_jButton417ActionPerformed

    private void jButton418ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton418ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton418.getText() + " ");
    }//GEN-LAST:event_jButton418ActionPerformed

    private void jButton419ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton419ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton419.getText() + " ");
    }//GEN-LAST:event_jButton419ActionPerformed

    private void jButton420ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton420ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton420.getText() + " ");
    }//GEN-LAST:event_jButton420ActionPerformed

    private void jButton421ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton421ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton421.getText() + " ");
    }//GEN-LAST:event_jButton421ActionPerformed

    private void jButton422ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton422ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton422.getText() + " ");
    }//GEN-LAST:event_jButton422ActionPerformed

    private void jButton423ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton423ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton423.getText() + " ");
    }//GEN-LAST:event_jButton423ActionPerformed

    private void jButton424ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton424ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton424.getText() + " ");
    }//GEN-LAST:event_jButton424ActionPerformed

    private void jButton425ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton425ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton425.getText() + " ");
    }//GEN-LAST:event_jButton425ActionPerformed

    private void jButton426ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton426ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton426.getText() + " ");
    }//GEN-LAST:event_jButton426ActionPerformed

    private void jButton427ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton427ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton427.getText() + " ");
    }//GEN-LAST:event_jButton427ActionPerformed

    private void jButton428ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton428ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton428.getText() + " ");
    }//GEN-LAST:event_jButton428ActionPerformed

    private void jButton429ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton429ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton429.getText() + " ");
    }//GEN-LAST:event_jButton429ActionPerformed

    private void jButton430ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton430ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton430.getText() + " ");
    }//GEN-LAST:event_jButton430ActionPerformed

    private void jButton431ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton431ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton431.getText() + " ");
    }//GEN-LAST:event_jButton431ActionPerformed

    private void jButton432ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton432ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton432.getText() + " ");
    }//GEN-LAST:event_jButton432ActionPerformed

    private void jButton433ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton433ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton433.getText() + " ");
    }//GEN-LAST:event_jButton433ActionPerformed

    private void jButton434ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton434ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton434.getText() + " ");
    }//GEN-LAST:event_jButton434ActionPerformed

    private void jButton435ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton435ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton435.getText() + " ");
    }//GEN-LAST:event_jButton435ActionPerformed

    private void jButton436ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton436ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton436.getText() + " ");
    }//GEN-LAST:event_jButton436ActionPerformed

    private void jButton438ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton438ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton438.getText() + " ");
    }//GEN-LAST:event_jButton438ActionPerformed

    private void jButton437ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton437ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton437.getText() + " ");
    }//GEN-LAST:event_jButton437ActionPerformed

    private void jButton439ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton439ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton439.getText() + " ");
    }//GEN-LAST:event_jButton439ActionPerformed

    private void jButton440ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton440ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton440.getText() + " ");
    }//GEN-LAST:event_jButton440ActionPerformed

    private void jButton441ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton441ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton441.getText() + " ");
    }//GEN-LAST:event_jButton441ActionPerformed

    private void jButton442ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton442ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton442.getText() + " ");
    }//GEN-LAST:event_jButton442ActionPerformed

    private void jButton443ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton443ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton443.getText() + " ");
    }//GEN-LAST:event_jButton443ActionPerformed

    private void jButton444ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton444ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton444.getText() + " ");
    }//GEN-LAST:event_jButton444ActionPerformed

    private void jButton445ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton445ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton445.getText() + " ");
    }//GEN-LAST:event_jButton445ActionPerformed

    private void jButton446ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton446ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton446.getText() + " ");
    }//GEN-LAST:event_jButton446ActionPerformed

    private void jButton447ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton447ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton447.getText() + " ");
    }//GEN-LAST:event_jButton447ActionPerformed

    private void jButton448ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton448ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton448.getText() + " ");
    }//GEN-LAST:event_jButton448ActionPerformed

    private void jButton449ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton449ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton449.getText() + " ");
    }//GEN-LAST:event_jButton449ActionPerformed

    private void jButton450ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton450ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton450.getText() + " ");
    }//GEN-LAST:event_jButton450ActionPerformed

    private void jButton451ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton451ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton451.getText() + " ");
    }//GEN-LAST:event_jButton451ActionPerformed

    private void jButton452ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton452ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton452.getText() + " ");
    }//GEN-LAST:event_jButton452ActionPerformed

    private void jButton453ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton453ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton453.getText() + " ");
    }//GEN-LAST:event_jButton453ActionPerformed

    private void jButton454ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton454ActionPerformed
        // TODO add your handling code here0
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton454.getText() + " ");
    }//GEN-LAST:event_jButton454ActionPerformed

    private void jButton455ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton455ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton455.getText() + " ");
    }//GEN-LAST:event_jButton455ActionPerformed

    private void jButton456ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton456ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton456.getText() + " ");
    }//GEN-LAST:event_jButton456ActionPerformed

    private void jButton457ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton457ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton457.getText() + " ");
    }//GEN-LAST:event_jButton457ActionPerformed

    private void jButton458ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton458ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton458.getText() + " ");
    }//GEN-LAST:event_jButton458ActionPerformed

    private void jButton459ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton459ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton459.getText() + " ");
    }//GEN-LAST:event_jButton459ActionPerformed

    private void jButton460ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton460ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton460.getText() + " ");
    }//GEN-LAST:event_jButton460ActionPerformed

    private void jButton461ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton461ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton461.getText() + " ");
    }//GEN-LAST:event_jButton461ActionPerformed

    private void jButton462ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton462ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton462.getText() + " ");
    }//GEN-LAST:event_jButton462ActionPerformed

    private void jButton463ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton463ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton463.getText() + " ");
    }//GEN-LAST:event_jButton463ActionPerformed

    private void jButton464ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton464ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton464.getText() + " ");
    }//GEN-LAST:event_jButton464ActionPerformed

    private void jButton465ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton465ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton465.getText() + " ");
    }//GEN-LAST:event_jButton465ActionPerformed

    private void jButton466ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton466ActionPerformed
        // TODO add your handling code here:
        String currentText = jTextFieldMessage.getText();
        jTextFieldMessage.setText(currentText + " " + jButton466.getText() + " ");
    }//GEN-LAST:event_jButton466ActionPerformed
    // End set up for Kaomoji Type 07
   
    private void resetVisibleKaomojiComponents(){
        type01.setVisible(false);
        type02.setVisible(false);
        type03.setVisible(false);
        type04.setVisible(false);
        type05.setVisible(false);
        type06.setVisible(false);
        type07.setVisible(false);
    }
  
  
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            ClientMainForm clientMainForm = new ClientMainForm();
            ImageIcon img = new ImageIcon("src/icons/icons8-chat-96.png");
            clientMainForm.setIconImage(img.getImage());
            clientMainForm.setLocationRelativeTo(null);
            clientMainForm.setVisible(true);
         //</editor-fold>
        //</editor-fold>
        
       });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane ChatField;
    private javax.swing.JMenuItem LogoutMenu;
    private javax.swing.JFileChooser chooser;
    private javax.swing.JLabel emoji00;
    private javax.swing.JLabel emoji01;
    private javax.swing.JLabel emoji02;
    private javax.swing.JLabel emoji03;
    private javax.swing.JLabel emoji04;
    private javax.swing.JLabel emoji05;
    private javax.swing.JLabel emoji06;
    private javax.swing.JLabel emoji07;
    private javax.swing.JLabel emoji08;
    private javax.swing.JLabel emoji09;
    private javax.swing.JLabel emoji10;
    private javax.swing.JLabel emoji100;
    private javax.swing.JLabel emoji101;
    private javax.swing.JLabel emoji102;
    private javax.swing.JLabel emoji103;
    private javax.swing.JLabel emoji104;
    private javax.swing.JLabel emoji105;
    private javax.swing.JLabel emoji106;
    private javax.swing.JLabel emoji107;
    private javax.swing.JLabel emoji108;
    private javax.swing.JLabel emoji109;
    private javax.swing.JLabel emoji11;
    private javax.swing.JLabel emoji110;
    private javax.swing.JLabel emoji111;
    private javax.swing.JLabel emoji112;
    private javax.swing.JLabel emoji113;
    private javax.swing.JLabel emoji114;
    private javax.swing.JLabel emoji115;
    private javax.swing.JLabel emoji116;
    private javax.swing.JLabel emoji117;
    private javax.swing.JLabel emoji118;
    private javax.swing.JLabel emoji119;
    private javax.swing.JLabel emoji12;
    private javax.swing.JLabel emoji120;
    private javax.swing.JLabel emoji121;
    private javax.swing.JLabel emoji122;
    private javax.swing.JLabel emoji123;
    private javax.swing.JLabel emoji124;
    private javax.swing.JLabel emoji125;
    private javax.swing.JLabel emoji126;
    private javax.swing.JLabel emoji127;
    private javax.swing.JLabel emoji128;
    private javax.swing.JLabel emoji129;
    private javax.swing.JLabel emoji13;
    private javax.swing.JLabel emoji130;
    private javax.swing.JLabel emoji131;
    private javax.swing.JLabel emoji132;
    private javax.swing.JLabel emoji133;
    private javax.swing.JLabel emoji134;
    private javax.swing.JLabel emoji14;
    private javax.swing.JLabel emoji15;
    private javax.swing.JLabel emoji16;
    private javax.swing.JLabel emoji17;
    private javax.swing.JLabel emoji18;
    private javax.swing.JLabel emoji19;
    private javax.swing.JLabel emoji20;
    private javax.swing.JLabel emoji21;
    private javax.swing.JLabel emoji22;
    private javax.swing.JLabel emoji23;
    private javax.swing.JLabel emoji24;
    private javax.swing.JLabel emoji25;
    private javax.swing.JLabel emoji26;
    private javax.swing.JLabel emoji27;
    private javax.swing.JLabel emoji28;
    private javax.swing.JLabel emoji29;
    private javax.swing.JLabel emoji30;
    private javax.swing.JLabel emoji31;
    private javax.swing.JLabel emoji32;
    private javax.swing.JLabel emoji33;
    private javax.swing.JLabel emoji34;
    private javax.swing.JLabel emoji35;
    private javax.swing.JLabel emoji36;
    private javax.swing.JLabel emoji37;
    private javax.swing.JLabel emoji38;
    private javax.swing.JLabel emoji39;
    private javax.swing.JLabel emoji40;
    private javax.swing.JLabel emoji41;
    private javax.swing.JLabel emoji42;
    private javax.swing.JLabel emoji43;
    private javax.swing.JLabel emoji44;
    private javax.swing.JLabel emoji45;
    private javax.swing.JLabel emoji46;
    private javax.swing.JLabel emoji47;
    private javax.swing.JLabel emoji48;
    private javax.swing.JLabel emoji49;
    private javax.swing.JLabel emoji50;
    private javax.swing.JLabel emoji51;
    private javax.swing.JLabel emoji52;
    private javax.swing.JLabel emoji53;
    private javax.swing.JLabel emoji54;
    private javax.swing.JLabel emoji55;
    private javax.swing.JLabel emoji56;
    private javax.swing.JLabel emoji57;
    private javax.swing.JLabel emoji58;
    private javax.swing.JLabel emoji59;
    private javax.swing.JLabel emoji60;
    private javax.swing.JLabel emoji61;
    private javax.swing.JLabel emoji62;
    private javax.swing.JLabel emoji63;
    private javax.swing.JLabel emoji64;
    private javax.swing.JLabel emoji65;
    private javax.swing.JLabel emoji66;
    private javax.swing.JLabel emoji67;
    private javax.swing.JLabel emoji68;
    private javax.swing.JLabel emoji69;
    private javax.swing.JLabel emoji70;
    private javax.swing.JLabel emoji71;
    private javax.swing.JLabel emoji72;
    private javax.swing.JLabel emoji73;
    private javax.swing.JLabel emoji74;
    private javax.swing.JLabel emoji75;
    private javax.swing.JLabel emoji76;
    private javax.swing.JLabel emoji77;
    private javax.swing.JLabel emoji78;
    private javax.swing.JLabel emoji79;
    private javax.swing.JLabel emoji80;
    private javax.swing.JLabel emoji81;
    private javax.swing.JLabel emoji82;
    private javax.swing.JLabel emoji83;
    private javax.swing.JLabel emoji84;
    private javax.swing.JLabel emoji85;
    private javax.swing.JLabel emoji86;
    private javax.swing.JLabel emoji87;
    private javax.swing.JLabel emoji88;
    private javax.swing.JLabel emoji89;
    private javax.swing.JLabel emoji90;
    private javax.swing.JLabel emoji91;
    private javax.swing.JLabel emoji92;
    private javax.swing.JLabel emoji93;
    private javax.swing.JLabel emoji94;
    private javax.swing.JLabel emoji95;
    private javax.swing.JLabel emoji96;
    private javax.swing.JLabel emoji97;
    private javax.swing.JLabel emoji98;
    private javax.swing.JLabel emoji99;
    private javax.swing.JLabel emojiButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton100;
    private javax.swing.JButton jButton101;
    private javax.swing.JButton jButton102;
    private javax.swing.JButton jButton103;
    private javax.swing.JButton jButton104;
    private javax.swing.JButton jButton105;
    private javax.swing.JButton jButton106;
    private javax.swing.JButton jButton107;
    private javax.swing.JButton jButton108;
    private javax.swing.JButton jButton109;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton110;
    private javax.swing.JButton jButton111;
    private javax.swing.JButton jButton112;
    private javax.swing.JButton jButton113;
    private javax.swing.JButton jButton114;
    private javax.swing.JButton jButton115;
    private javax.swing.JButton jButton116;
    private javax.swing.JButton jButton117;
    private javax.swing.JButton jButton118;
    private javax.swing.JButton jButton119;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton120;
    private javax.swing.JButton jButton121;
    private javax.swing.JButton jButton122;
    private javax.swing.JButton jButton123;
    private javax.swing.JButton jButton124;
    private javax.swing.JButton jButton125;
    private javax.swing.JButton jButton126;
    private javax.swing.JButton jButton127;
    private javax.swing.JButton jButton128;
    private javax.swing.JButton jButton129;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton130;
    private javax.swing.JButton jButton131;
    private javax.swing.JButton jButton132;
    private javax.swing.JButton jButton133;
    private javax.swing.JButton jButton134;
    private javax.swing.JButton jButton135;
    private javax.swing.JButton jButton136;
    private javax.swing.JButton jButton137;
    private javax.swing.JButton jButton138;
    private javax.swing.JButton jButton139;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton140;
    private javax.swing.JButton jButton141;
    private javax.swing.JButton jButton142;
    private javax.swing.JButton jButton143;
    private javax.swing.JButton jButton144;
    private javax.swing.JButton jButton145;
    private javax.swing.JButton jButton146;
    private javax.swing.JButton jButton147;
    private javax.swing.JButton jButton148;
    private javax.swing.JButton jButton149;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton150;
    private javax.swing.JButton jButton151;
    private javax.swing.JButton jButton152;
    private javax.swing.JButton jButton153;
    private javax.swing.JButton jButton154;
    private javax.swing.JButton jButton155;
    private javax.swing.JButton jButton156;
    private javax.swing.JButton jButton157;
    private javax.swing.JButton jButton158;
    private javax.swing.JButton jButton159;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton160;
    private javax.swing.JButton jButton161;
    private javax.swing.JButton jButton162;
    private javax.swing.JButton jButton163;
    private javax.swing.JButton jButton164;
    private javax.swing.JButton jButton165;
    private javax.swing.JButton jButton166;
    private javax.swing.JButton jButton167;
    private javax.swing.JButton jButton168;
    private javax.swing.JButton jButton169;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton170;
    private javax.swing.JButton jButton171;
    private javax.swing.JButton jButton172;
    private javax.swing.JButton jButton173;
    private javax.swing.JButton jButton174;
    private javax.swing.JButton jButton175;
    private javax.swing.JButton jButton176;
    private javax.swing.JButton jButton177;
    private javax.swing.JButton jButton178;
    private javax.swing.JButton jButton179;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton180;
    private javax.swing.JButton jButton181;
    private javax.swing.JButton jButton182;
    private javax.swing.JButton jButton183;
    private javax.swing.JButton jButton184;
    private javax.swing.JButton jButton185;
    private javax.swing.JButton jButton186;
    private javax.swing.JButton jButton187;
    private javax.swing.JButton jButton188;
    private javax.swing.JButton jButton189;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton190;
    private javax.swing.JButton jButton191;
    private javax.swing.JButton jButton192;
    private javax.swing.JButton jButton193;
    private javax.swing.JButton jButton194;
    private javax.swing.JButton jButton195;
    private javax.swing.JButton jButton196;
    private javax.swing.JButton jButton197;
    private javax.swing.JButton jButton198;
    private javax.swing.JButton jButton199;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton200;
    private javax.swing.JButton jButton201;
    private javax.swing.JButton jButton202;
    private javax.swing.JButton jButton203;
    private javax.swing.JButton jButton204;
    private javax.swing.JButton jButton205;
    private javax.swing.JButton jButton206;
    private javax.swing.JButton jButton207;
    private javax.swing.JButton jButton208;
    private javax.swing.JButton jButton209;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton210;
    private javax.swing.JButton jButton211;
    private javax.swing.JButton jButton212;
    private javax.swing.JButton jButton213;
    private javax.swing.JButton jButton214;
    private javax.swing.JButton jButton215;
    private javax.swing.JButton jButton216;
    private javax.swing.JButton jButton217;
    private javax.swing.JButton jButton218;
    private javax.swing.JButton jButton219;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton220;
    private javax.swing.JButton jButton221;
    private javax.swing.JButton jButton222;
    private javax.swing.JButton jButton223;
    private javax.swing.JButton jButton224;
    private javax.swing.JButton jButton225;
    private javax.swing.JButton jButton226;
    private javax.swing.JButton jButton227;
    private javax.swing.JButton jButton228;
    private javax.swing.JButton jButton229;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton230;
    private javax.swing.JButton jButton231;
    private javax.swing.JButton jButton232;
    private javax.swing.JButton jButton233;
    private javax.swing.JButton jButton234;
    private javax.swing.JButton jButton235;
    private javax.swing.JButton jButton236;
    private javax.swing.JButton jButton237;
    private javax.swing.JButton jButton238;
    private javax.swing.JButton jButton239;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton240;
    private javax.swing.JButton jButton241;
    private javax.swing.JButton jButton242;
    private javax.swing.JButton jButton243;
    private javax.swing.JButton jButton244;
    private javax.swing.JButton jButton245;
    private javax.swing.JButton jButton246;
    private javax.swing.JButton jButton247;
    private javax.swing.JButton jButton248;
    private javax.swing.JButton jButton249;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton250;
    private javax.swing.JButton jButton251;
    private javax.swing.JButton jButton252;
    private javax.swing.JButton jButton253;
    private javax.swing.JButton jButton254;
    private javax.swing.JButton jButton255;
    private javax.swing.JButton jButton256;
    private javax.swing.JButton jButton257;
    private javax.swing.JButton jButton258;
    private javax.swing.JButton jButton259;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton260;
    private javax.swing.JButton jButton261;
    private javax.swing.JButton jButton262;
    private javax.swing.JButton jButton263;
    private javax.swing.JButton jButton264;
    private javax.swing.JButton jButton265;
    private javax.swing.JButton jButton266;
    private javax.swing.JButton jButton267;
    private javax.swing.JButton jButton268;
    private javax.swing.JButton jButton269;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton270;
    private javax.swing.JButton jButton271;
    private javax.swing.JButton jButton272;
    private javax.swing.JButton jButton273;
    private javax.swing.JButton jButton274;
    private javax.swing.JButton jButton275;
    private javax.swing.JButton jButton276;
    private javax.swing.JButton jButton277;
    private javax.swing.JButton jButton278;
    private javax.swing.JButton jButton279;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton280;
    private javax.swing.JButton jButton281;
    private javax.swing.JButton jButton282;
    private javax.swing.JButton jButton283;
    private javax.swing.JButton jButton284;
    private javax.swing.JButton jButton285;
    private javax.swing.JButton jButton286;
    private javax.swing.JButton jButton287;
    private javax.swing.JButton jButton288;
    private javax.swing.JButton jButton289;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton290;
    private javax.swing.JButton jButton291;
    private javax.swing.JButton jButton292;
    private javax.swing.JButton jButton293;
    private javax.swing.JButton jButton294;
    private javax.swing.JButton jButton295;
    private javax.swing.JButton jButton296;
    private javax.swing.JButton jButton297;
    private javax.swing.JButton jButton298;
    private javax.swing.JButton jButton299;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton300;
    private javax.swing.JButton jButton301;
    private javax.swing.JButton jButton302;
    private javax.swing.JButton jButton303;
    private javax.swing.JButton jButton304;
    private javax.swing.JButton jButton305;
    private javax.swing.JButton jButton306;
    private javax.swing.JButton jButton307;
    private javax.swing.JButton jButton308;
    private javax.swing.JButton jButton309;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton310;
    private javax.swing.JButton jButton311;
    private javax.swing.JButton jButton312;
    private javax.swing.JButton jButton313;
    private javax.swing.JButton jButton314;
    private javax.swing.JButton jButton315;
    private javax.swing.JButton jButton316;
    private javax.swing.JButton jButton317;
    private javax.swing.JButton jButton318;
    private javax.swing.JButton jButton319;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton320;
    private javax.swing.JButton jButton321;
    private javax.swing.JButton jButton322;
    private javax.swing.JButton jButton323;
    private javax.swing.JButton jButton324;
    private javax.swing.JButton jButton325;
    private javax.swing.JButton jButton326;
    private javax.swing.JButton jButton327;
    private javax.swing.JButton jButton328;
    private javax.swing.JButton jButton329;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton330;
    private javax.swing.JButton jButton331;
    private javax.swing.JButton jButton332;
    private javax.swing.JButton jButton333;
    private javax.swing.JButton jButton334;
    private javax.swing.JButton jButton335;
    private javax.swing.JButton jButton336;
    private javax.swing.JButton jButton337;
    private javax.swing.JButton jButton338;
    private javax.swing.JButton jButton339;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton340;
    private javax.swing.JButton jButton341;
    private javax.swing.JButton jButton342;
    private javax.swing.JButton jButton343;
    private javax.swing.JButton jButton344;
    private javax.swing.JButton jButton345;
    private javax.swing.JButton jButton346;
    private javax.swing.JButton jButton347;
    private javax.swing.JButton jButton348;
    private javax.swing.JButton jButton349;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton350;
    private javax.swing.JButton jButton351;
    private javax.swing.JButton jButton352;
    private javax.swing.JButton jButton353;
    private javax.swing.JButton jButton354;
    private javax.swing.JButton jButton355;
    private javax.swing.JButton jButton356;
    private javax.swing.JButton jButton357;
    private javax.swing.JButton jButton358;
    private javax.swing.JButton jButton359;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton360;
    private javax.swing.JButton jButton361;
    private javax.swing.JButton jButton362;
    private javax.swing.JButton jButton363;
    private javax.swing.JButton jButton364;
    private javax.swing.JButton jButton365;
    private javax.swing.JButton jButton366;
    private javax.swing.JButton jButton367;
    private javax.swing.JButton jButton368;
    private javax.swing.JButton jButton369;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton370;
    private javax.swing.JButton jButton371;
    private javax.swing.JButton jButton372;
    private javax.swing.JButton jButton373;
    private javax.swing.JButton jButton374;
    private javax.swing.JButton jButton375;
    private javax.swing.JButton jButton376;
    private javax.swing.JButton jButton377;
    private javax.swing.JButton jButton378;
    private javax.swing.JButton jButton379;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton380;
    private javax.swing.JButton jButton381;
    private javax.swing.JButton jButton382;
    private javax.swing.JButton jButton383;
    private javax.swing.JButton jButton384;
    private javax.swing.JButton jButton385;
    private javax.swing.JButton jButton386;
    private javax.swing.JButton jButton387;
    private javax.swing.JButton jButton388;
    private javax.swing.JButton jButton389;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton390;
    private javax.swing.JButton jButton391;
    private javax.swing.JButton jButton392;
    private javax.swing.JButton jButton393;
    private javax.swing.JButton jButton394;
    private javax.swing.JButton jButton395;
    private javax.swing.JButton jButton396;
    private javax.swing.JButton jButton397;
    private javax.swing.JButton jButton398;
    private javax.swing.JButton jButton399;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton400;
    private javax.swing.JButton jButton401;
    private javax.swing.JButton jButton402;
    private javax.swing.JButton jButton403;
    private javax.swing.JButton jButton404;
    private javax.swing.JButton jButton405;
    private javax.swing.JButton jButton406;
    private javax.swing.JButton jButton407;
    private javax.swing.JButton jButton408;
    private javax.swing.JButton jButton409;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton410;
    private javax.swing.JButton jButton411;
    private javax.swing.JButton jButton412;
    private javax.swing.JButton jButton413;
    private javax.swing.JButton jButton414;
    private javax.swing.JButton jButton415;
    private javax.swing.JButton jButton416;
    private javax.swing.JButton jButton417;
    private javax.swing.JButton jButton418;
    private javax.swing.JButton jButton419;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton420;
    private javax.swing.JButton jButton421;
    private javax.swing.JButton jButton422;
    private javax.swing.JButton jButton423;
    private javax.swing.JButton jButton424;
    private javax.swing.JButton jButton425;
    private javax.swing.JButton jButton426;
    private javax.swing.JButton jButton427;
    private javax.swing.JButton jButton428;
    private javax.swing.JButton jButton429;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton430;
    private javax.swing.JButton jButton431;
    private javax.swing.JButton jButton432;
    private javax.swing.JButton jButton433;
    private javax.swing.JButton jButton434;
    private javax.swing.JButton jButton435;
    private javax.swing.JButton jButton436;
    private javax.swing.JButton jButton437;
    private javax.swing.JButton jButton438;
    private javax.swing.JButton jButton439;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton440;
    private javax.swing.JButton jButton441;
    private javax.swing.JButton jButton442;
    private javax.swing.JButton jButton443;
    private javax.swing.JButton jButton444;
    private javax.swing.JButton jButton445;
    private javax.swing.JButton jButton446;
    private javax.swing.JButton jButton447;
    private javax.swing.JButton jButton448;
    private javax.swing.JButton jButton449;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton450;
    private javax.swing.JButton jButton451;
    private javax.swing.JButton jButton452;
    private javax.swing.JButton jButton453;
    private javax.swing.JButton jButton454;
    private javax.swing.JButton jButton455;
    private javax.swing.JButton jButton456;
    private javax.swing.JButton jButton457;
    private javax.swing.JButton jButton458;
    private javax.swing.JButton jButton459;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton460;
    private javax.swing.JButton jButton461;
    private javax.swing.JButton jButton462;
    private javax.swing.JButton jButton463;
    private javax.swing.JButton jButton464;
    private javax.swing.JButton jButton465;
    private javax.swing.JButton jButton466;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton65;
    private javax.swing.JButton jButton66;
    private javax.swing.JButton jButton67;
    private javax.swing.JButton jButton68;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton70;
    private javax.swing.JButton jButton71;
    private javax.swing.JButton jButton72;
    private javax.swing.JButton jButton73;
    private javax.swing.JButton jButton74;
    private javax.swing.JButton jButton75;
    private javax.swing.JButton jButton76;
    private javax.swing.JButton jButton77;
    private javax.swing.JButton jButton78;
    private javax.swing.JButton jButton79;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton80;
    private javax.swing.JButton jButton81;
    private javax.swing.JButton jButton82;
    private javax.swing.JButton jButton83;
    private javax.swing.JButton jButton84;
    private javax.swing.JButton jButton85;
    private javax.swing.JButton jButton86;
    private javax.swing.JButton jButton87;
    private javax.swing.JButton jButton88;
    private javax.swing.JButton jButton89;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButton90;
    private javax.swing.JButton jButton91;
    private javax.swing.JButton jButton92;
    private javax.swing.JButton jButton93;
    private javax.swing.JButton jButton94;
    private javax.swing.JButton jButton95;
    private javax.swing.JButton jButton96;
    private javax.swing.JButton jButton97;
    private javax.swing.JButton jButton98;
    private javax.swing.JButton jButton99;
    private javax.swing.JButton jButtonSendMessage;
    private javax.swing.JMenuItem jGotoBroadcastVoiceCall;
    private javax.swing.JMenuItem jGotoUnicastVoiceCall;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPaneEmoji;
    private javax.swing.JLayeredPane jLayeredPaneKaomoji;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBarClientChatForm;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextFieldMessage;
    private javax.swing.JTextPane jTextPaneOnlineList;
    private javax.swing.JMenuItem sendFileMenu;
    private javax.swing.JScrollPane type01;
    private javax.swing.JScrollPane type02;
    private javax.swing.JScrollPane type03;
    private javax.swing.JScrollPane type04;
    private javax.swing.JScrollPane type05;
    private javax.swing.JScrollPane type06;
    private javax.swing.JScrollPane type07;
    // End of variables declaration//GEN-END:variables
}
