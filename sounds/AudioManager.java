package sounds;

import javafx.scene.media.AudioClip;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.nio.file.Path;

public class AudioManager {

    private static AudioManager audio;
    private static int volume = 50;
    private static AudioInputStream audioInputStream;
    private static Clip clip;
    public static Boolean running;

    //private constructor
    private AudioManager(){
        try{
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./resources/Tetris.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            running = true;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static AudioManager getInstance(){
        if(audio==null){
            audio = new AudioManager();
        }
        return audio;
    }
    public static void toggleAudio(){
        if(running){
            clip.stop();
            running = false;
        }
        else{
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            running = true;
        }
    }
    public static void setVolume(int newVolume){
        volume = newVolume;
    }
}
