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

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author Le Nhut Nam
 */
public class SendingFileThread implements Runnable {
    
    // Properties
    protected Socket socket;
    private DataOutputStream dos;
    protected SendFile form;
    protected String file;
    protected String receiver;
    protected String sender;
    protected DecimalFormat df = new DecimalFormat("##,#00");
    private final int BUFFER_SIZE = 100;
    
    /**
     * 
     * @param socket
     * @param file
     * @param receiver
     * @param sender
     * @param frm 
     */
    public SendingFileThread(Socket socket, String file, String receiver, String sender, SendFile frm){
        this.socket = socket;
        this.file = file;
        this.receiver = receiver;
        this.sender = sender;
        this.form = frm;
    }

    
    /**
     * 
     */
    @Override
    public void run() {
        try {
            form.disableGUI(true);
            System.out.println("Sending File..!");
            dos = new DataOutputStream(socket.getOutputStream());
            /** Write filename, recipient, username  **/
            //  Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Consignee]
            File filename = new File(file);
            int len = (int) filename.length();
            int filesize = (int)Math.ceil(len / BUFFER_SIZE); // get the file size
            String clean_filename = filename.getName();
            dos.writeUTF("CMD_SENDFILE "
                    + clean_filename.replace(" ", "_") 
                    + " " + filesize 
                    + " " + receiver 
                    + " " + sender);
            System.out.println("From: " + sender);
            System.out.println("To: " + receiver);
            /** Create an stream **/
            InputStream input = new FileInputStream(filename);
            /*  Monitor progress   */
            //ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(form, "Sending file please wait...", input);
            /** Read file ***/
            try (OutputStream output = socket.getOutputStream()) {
                /*  Monitor progress   */
                //ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(form, "Sending file please wait...", input);
                /** Read file ***/
                BufferedInputStream bis = new BufferedInputStream(input);
                /** Create a temporary file storage **/
                byte[] buffer = new byte[BUFFER_SIZE];
                int count, percent = 0;
                while((count = bis.read(buffer)) > 0){
                    percent = percent + count;
                    int p = (percent / filesize);
                    //form.setMyTitle(p +"% Sending File...");
                    form.updateProgress(p);
                    output.write(buffer, 0, count);
                }   /* Update AttachmentForm GUI */
                form.setMyTitle("File was sent.!");
                form.updateAttachment(false); //  Update Attachment 
                JOptionPane.showMessageDialog(form, "File successfully sent.!", "Sucess", JOptionPane.INFORMATION_MESSAGE);
                form.closeThis();
                /* Close Streams */
                output.flush();
            }
            System.out.println("File was sent..!");
        } catch (IOException iOException) {
            form.updateAttachment(false); //  Update Attachment
            System.out.println("[SendFile]: " + iOException.getMessage());
        }
    }
}
