package phaserush.cmpt365p1;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class Waver {
    final File file;
    AudioInputStream audioInputStream;
    Sampler samplingGraph;

    public Waver() {
        file = new File(Objects.requireNonNull(this.getClass().getResource("/sample.wav")).getFile());
    }

    public void createAudioInputStream() throws UnsupportedAudioFileException, IOException {
        audioInputStream = AudioSystem.getAudioInputStream(file);
        samplingGraph = new Sampler(audioInputStream);
        samplingGraph.createWaveForm();
        samplingGraph.render();
    }

    public static void main(String[] args) throws Exception {
        Waver awc = new Waver();
        awc.createAudioInputStream();
    }
}
   