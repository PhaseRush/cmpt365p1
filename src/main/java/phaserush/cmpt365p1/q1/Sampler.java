package phaserush.cmpt365p1.q1;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sampler {
    private final AudioInputStream audioInputStream;
    private final List<Line2D.Double> lines;
    final int WIDTH = 1500;
    final int HEIGHT = 750;

    public Sampler(AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
        this.lines = new ArrayList<>();
    }

    public void createWaveForm() throws IOException {
        final AudioFormat format = audioInputStream.getFormat();
        final byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
        audioInputStream.read(audioBytes);

        final int[] audioData;
        final int lengthInSamples = audioBytes.length / 2;
        audioData = new int[lengthInSamples];
        for (int i = 0; i < lengthInSamples; i++) {
            int lowerBit = audioBytes[2 * i];
            int higherBit = audioBytes[2 * i + 1];
            audioData[i] = higherBit << 8 | (255 & lowerBit);
        }


        int pixelFrameWidth = audioBytes.length / format.getFrameSize() / WIDTH;
        byte currByte;
        double y_last = 0;
        for (double x = 0; x < WIDTH; x++) {
            int idx = (int) (pixelFrameWidth * 2 * x); // # channels guaranteed to be 2
            currByte = (byte) (128 * audioData[idx] / (2 << 14));
            double y_new = HEIGHT * (128 - currByte) / 256.0; // scale height
            lines.add(new Line2D.Double(x, y_last, x, y_new));  // connect lines
            y_last = y_new;
        }
    }


    public void render() {
        final BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2 = bufferedImage.createGraphics();

        g2.setBackground(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setColor(Color.PINK);
        lines.forEach(g2::draw); // draw every line
        g2.dispose();

        final JFrame frame = new JFrame();
        final ImageIcon image = new ImageIcon(bufferedImage);
        final JLabel label = new JLabel(image);

        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

