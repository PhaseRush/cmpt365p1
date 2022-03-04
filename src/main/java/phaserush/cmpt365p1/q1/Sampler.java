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

import static phaserush.cmpt365p1.q2.Imager.joinBufferedImage;

public class Sampler {
    private final AudioInputStream audioInputStream;
    private final List<Line2D.Double> linesLeft;
    private final List<Line2D.Double> linesRight;
    final int WIDTH = 1500;
    final int HEIGHT = 750;

    public Sampler(AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
        this.linesLeft = new ArrayList<>();
        this.linesRight = new ArrayList<>();
    }

    public void createWaveForm() throws IOException {
        final AudioFormat format = audioInputStream.getFormat();
        final byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
        audioInputStream.read(audioBytes);

        final int[] audioData;
        final int lengthInSamples = audioBytes.length / 2;
        audioData = new int[lengthInSamples / 2];
        for (int i = 0; i < lengthInSamples; i++) {
            int lowerBit = audioBytes[2 * i];
            int higherBit = audioBytes[2 * i + 1];
            audioData[i / 2] = higherBit << 8 | (255 & lowerBit);
        }


        int pixelFrameWidth = audioBytes.length / format.getFrameSize() / WIDTH/2;
        byte currByte;
        double y_last = 0;
        boolean left = true;
        for (double x = 0; x < WIDTH; x++) {
            int idx = (int) (pixelFrameWidth * 2 * x); // # channels guaranteed to be 2
            currByte = (byte) (128 * audioData[idx] / (2 << 14));
            double y_new = HEIGHT * (128 - currByte) / 256.0; // scale height
            if (left) {
                linesLeft.add(new Line2D.Double(x, y_last, x, y_new));  // connect lines
                left = false;
            } else {
                linesRight.add(new Line2D.Double(x, y_last, x, y_new));  // connect lines
                left = true;
            }
            y_last = y_new;
        }
    }


    public void render() {
        final BufferedImage leftImage = new BufferedImage(WIDTH/2, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final BufferedImage rightImage = new BufferedImage(WIDTH/2, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2Left = leftImage.createGraphics();
        final Graphics2D g2Right = rightImage.createGraphics();

        g2Left.setBackground(Color.WHITE);
        g2Left.fillRect(0, 0, WIDTH, HEIGHT);
        g2Left.setColor(Color.PINK);
        linesLeft.forEach(g2Left::draw); // draw every line
        g2Left.dispose();

        g2Right.setBackground(Color.WHITE);
        g2Right.fillRect(0, 0, WIDTH, HEIGHT);
        g2Right.setColor(Color.GREEN);
        linesRight.forEach(g2Right::draw); // draw every line
        g2Right.dispose();


        final BufferedImage combined = joinBufferedImage(leftImage, rightImage);
        final JFrame frame = new JFrame();
        final ImageIcon image = new ImageIcon(combined);
        final JLabel label = new JLabel(image);

        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

