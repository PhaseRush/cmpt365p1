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
import java.util.Arrays;

public class Imager {
    static File file;
    static JFrame parent;
    static int WIDTH;
    static int HEIGHT;
    static int currentImage = 0;

    static int[][] ditherMatrix = {
            {1, 9, 3, 11},
            {13, 5, 15, 7},
            {4, 12, 2, 10},
            {16, 8, 14, 6}};

    private static void nextImage() {
        System.out.println(currentImage);
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
    static int[] bitmap;
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
        bitmap = originalImage.getRGB(0, 0, WIDTH, HEIGHT, null, 0, WIDTH);

        displayImage(originalImage);
        generateGrayscale();
//        displayImage(joinBufferedImage(originalImage, grayscaleImage));
        generateDither();
//        displayImage(joinBufferedImage(originalImage, ditheredImage));
        generateAutolevel();
    }

    static void generateGrayscale() {
        BufferedImage grayscale = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int r, g, b;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int pix = originalImage.getRGB(x, y);
                b = pix & 0xff;
                g = (pix & 0xff00) >> 8;
                r = (pix & 0xff0000) >> 16;
                //https://en.wikipedia.org/wiki/Grayscale#Converting_colour_to_greyscale
                int weightedSum = (int) (0.2162 * r + 0.7152 * g + 0.0722 * b) / 3;
                int gray = (weightedSum << 16) + (weightedSum << 8) + weightedSum;

                grayscale.setRGB(x, y, gray);
            }
        }
        // part of the stdlib but way too cheaty
//        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//        ColorConvertOp op = new ColorConvertOp(cs, null);
//        grayscaleImage = op.filter(originalImage, null);
        grayscaleImage = grayscale;
    }

    static void generateDither() {
        BufferedImage dither = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int c, gray;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int pix = originalImage.getRGB(x, y);
                c = (pix & 0xff0000) >> 16;
                gray = c;
                gray += gray * ditherMatrix[x % 3][y % 3] / 17;

                dither.setRGB(x, y, gray < 192 ? 0 : 0xff_ff_ff_ff);
            }
        }
        ditheredImage = dither;
    }

    static BufferedImage generateAutolevel() {
        int[] rHist = new int[256];
        int[] gHist = new int[256];
        int[] bHist = new int[256];

        int r, g, b, a;
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int pix = originalImage.getRGB(x, y);
                b = pix & 0xff;
                g = (pix & 0xff00) >> 8;
                r = (pix & 0xff0000) >> 16;

                rHist[r]++;
                gHist[g]++;
                bHist[b]++;
            }
        }
        System.out.println(Arrays.toString(rHist));
        System.out.println(Arrays.toString(gHist));
        System.out.println(Arrays.toString(bHist));

        double scale = 255.0 / (originalImage.getWidth() * originalImage.getHeight());
        System.out.println("scale\t" + scale);

        int[] rScaledHist = new int[256];
        int[] gScaledHist = new int[256];
        int[] bScaledHist = new int[256];

        long rTot = 0;
        long gTot = 0;
        long bTot = 0;

        for (int i = 0; i < 256; i++) {
            rTot += rHist[i];
            r = (int) (rTot * scale);
            rScaledHist[i] = Math.min(255, r);

            gTot += gHist[i];
            g = (int) (gTot * scale);
            gScaledHist[i] = Math.min(255, g);

            bTot += bHist[i];
            b = (int) (bTot * scale);
            bScaledHist[i] = Math.min(255, b);
        }

        BufferedImage leveled = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());

        Color pix;
        int rgbaLeveled;
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                pix = new Color(originalImage.getRGB(x, y));
                r = rScaledHist[pix.getRed()];
                g = gScaledHist[pix.getGreen()];
                b = bScaledHist[pix.getBlue()];
                a = pix.getAlpha();

//                System.out.println(r + "\t" + g + "\t" + b);
                rgbaLeveled = ((a & 0xFF) << 24) |
                        ((r & 0xFF) << 16) |
                        ((g & 0xFF) << 8) |
                        ((b & 0xFF));
//                System.out.println(rgbaLeveled);
                leveled.setRGB(x, y, rgbaLeveled);
            }
        }
        autoleveledImage = leveled;
        return leveled;
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


