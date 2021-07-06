package phaserush.cmpt365p1.q2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

public class Imager {
    static File file;
    static JFrame parent;
    static int WIDTH;
    static int HEIGHT;

    static int[][] ditherMatrix = {
            {3, 7, 4},
            {6, 1, 9},
            {2, 8, 5}
    };

    static BufferedImage originalImage;
    static int[] bitmap;
    static BufferedImage grayscaleImage;
    static BufferedImage ditheredImage;
    static BufferedImage autoleveledImage;

    public static void main(String[] args) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        parent = new JFrame();
        fileChooser.showOpenDialog(parent);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        file = fileChooser.getSelectedFile();
        originalImage = ImageIO.read(file);
        WIDTH = originalImage.getWidth();
        HEIGHT = originalImage.getHeight();
        bitmap = originalImage.getRGB(0,
                0,
                WIDTH,
                HEIGHT,
                null,
                0,
                WIDTH);


//        displayImage(originalImage);
        generateGrayscale();
//        displayImage(joinBufferedImage(originalImage, grayscaleImage));
        generateDither();
        displayImage(joinBufferedImage(originalImage, ditheredImage));
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
                gray += gray * ditherMatrix[x % 3][y % 3] / 10;


                dither.setRGB(x, y, gray < 192 ? 0 : 0xff_ff_ff_ff);
            }
        }
        ditheredImage = dither;
    }

    /*
    joins two buffered images into one, with first image on the left.
     */
    public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {
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


