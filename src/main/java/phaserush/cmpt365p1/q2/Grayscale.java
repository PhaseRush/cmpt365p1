package phaserush.cmpt365p1.q2;

import java.awt.image.BufferedImage;

public class Grayscale implements Filter {
    @Override
    public BufferedImage filter(BufferedImage in) {
        final int WIDTH = in.getWidth();
        final int HEIGHT = in.getHeight();

        final BufferedImage grayscale = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int r, g, b;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int pix = in.getRGB(x, y);
                b = pix & 0xff;
                g = (pix & 0xff00) >> 8;
                r = (pix & 0xff0000) >> 16;
                //https://en.wikipedia.org/wiki/Grayscale#Converting_colour_to_greyscale
                int weightedSum = (int) (0.2162 * r + 0.7152 * g + 0.0722 * b) / 3;
                int gray = (weightedSum << 16) + (weightedSum << 8) + weightedSum;

                grayscale.setRGB(x, y, gray);
            }
        }
        return grayscale;
    }
}
