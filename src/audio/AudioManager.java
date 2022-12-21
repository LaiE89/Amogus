package audio;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class AudioManager {

    private double volume = 0.5;
    private HashMap<String, Clip> sounds = new HashMap<>();
    public static AudioManager instance;

    //private constructor
    private AudioManager(){
        loadSounds();
        changeVolumes();
    }

    public static AudioManager getInstance(){
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void changeVolumes(){
        for (String s : sounds.keySet()) {
            FloatControl gainControl = (FloatControl) sounds.get(s).getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));
        }
    }

    public double getVolume() {
        return this.volume;
    }

    public void setVolume(double newVolume) {
        this.volume = newVolume;
    }

    public void playSound(String name) {
        if (sounds.containsKey(name)) {
            Clip curClip = sounds.get(name);
            if(curClip.isActive() || curClip.isRunning()) {
                curClip.stop();
                curClip.flush();
            }
            curClip.setMicrosecondPosition(0);
            curClip.start();
        }else {
            System.out.println(name + " is not in the sounds list.");
        }
    }

    private void loadSounds(){
        try{
            getResources();
        } catch(Exception e){
            System.out.println("Load sound failure: " + e.getMessage());
        }
    }

    public void getResources() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        String[] allFiles = new String[]{"clearline.wav", "drop.wav", "garbage.wav", "hold.wav", "menu.wav", "rotate.wav"};
        for (String line : allFiles) {
            if (line.endsWith("wav")) {
                InputStream is2 = getClass().getClassLoader().getResourceAsStream(line);
                InputStream bufferedIn = new BufferedInputStream(is2);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                sounds.put(line, clip);
                bufferedIn.close();
            }
        }
    }
}
