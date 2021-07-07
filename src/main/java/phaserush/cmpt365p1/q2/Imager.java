package phaserush.cmpt365p1.q2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Imager {
    static File file;
    static JFrame parent;
    static int WIDTH;
    static int HEIGHT;
    static int currentImage = 0;

    private static void nextImage() {
        parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
        reset();
        switch (currentImage % 4) {
            case 0 -> displayImage(joinBufferedImage(originalImage, grayscaleImage));
            case 1 -> displayImage(joinBufferedImage(grayscaleImage, ditheredImage));
            case 2 -> displayImage(joinBufferedImage(originalImage, autoleveledImage));
            case 3 -> displayImage(originalImage);
        }
        currentImage++;
    }

    static BufferedImage originalImage;
    static BufferedImage grayscaleImage;
    static BufferedImage ditheredImage;
    static BufferedImage autoleveledImage;

    static JFrame reset() {
        parent = new JFrame();
        parent.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                nextImage();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        return parent;
    }

    public static void main(String[] args) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        parent = reset();
        fileChooser.showOpenDialog(parent);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        file = fileChooser.getSelectedFile();
        originalImage = ImageIO.read(file);
        WIDTH = originalImage.getWidth();
        HEIGHT = originalImage.getHeight();

        displayImage(originalImage);
        grayscaleImage = new Grayscale().filter(originalImage);
        ditheredImage = new Dither().filter(originalImage);
        autoleveledImage = new Autolevel().filter(originalImage);
    }

    /*
    joins two buffered images into one, with first image on the left.
     */
    public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {
        System.out.println();
        final int WIDTH = img1.getWidth() + img2.getWidth();
        final int HEIGHT = Math.max(img1.getHeight(), img2.getHeight());

        BufferedImage joinedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = joinedImage.createGraphics();
        Color colour = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setColor(colour);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth(), 0);
        g2.dispose();
        return joinedImage;
    }

    static void displayImage(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);

        JLabel label = new JLabel(icon);
        label.setSize(image.getWidth(), image.getHeight());

        parent.add(label);
        parent.pack();
        parent.setVisible(true);
    }
}


