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

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Le Nhut Nam
 */
public enum SoundEffect {
    
    //  Ringtone for Chat message receive
    MessageReceive("/audio/receive_message.wav", false), 
    //   Ringtone for income file
    FileSharing("/audio/alarm.wav", false); 
    
    // Properties
    private Clip clip;
    private boolean loop;
    
    /**
     * Constructor SoundEffect
     * @param filename
     * @param loop 
     */
    SoundEffect(String filename, boolean loop){
        try {
            this.loop = loop;
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIS = AudioSystem.getAudioInputStream(url);
            
            clip = AudioSystem.getClip();
            clip.open(audioIS);
        } catch (IOException iOException) {
            System.out.println("[SoundEffect]" + iOException.getMessage());
        } catch (LineUnavailableException lineUnavailableException) {
            System.out.println("[SoundEffect]" + lineUnavailableException.getMessage());
        } catch (UnsupportedAudioFileException unsupportedAudioFileException){
            System.out.println("[SoundEffect]" + unsupportedAudioFileException.getMessage());
        }
    }
    
    /**
     * 
     */
    public void play(){
        if(clip.isRunning()){
            clip.stop(); //  Stop Audio
        }
        //  Reset Audio from the beginning
        clip.setFramePosition(0);
        clip.start();
        //  Check if audio play contineously
        if(loop){
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * 
     */
    public void stop(){
        if(clip.isRunning()){
            clip.stop(); //   Stop Audio
        }
    }
}
