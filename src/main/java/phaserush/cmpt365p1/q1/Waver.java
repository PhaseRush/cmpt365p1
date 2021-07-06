package phaserush.cmpt365p1.q1;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;


public class Waver {
    final File file;
    AudioInputStream audioInputStream;
    Sampler samplingGraph;

    public Waver() {
        JFileChooser fileChooser = new JFileChooser();
        JFrame frame = new JFrame();
        fileChooser.showOpenDialog(frame);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        file = fileChooser.getSelectedFile();
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
   