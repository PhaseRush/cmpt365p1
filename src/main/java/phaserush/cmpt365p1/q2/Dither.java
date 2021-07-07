package phaserush.cmpt365p1.q2;

import java.awt.image.BufferedImage;

public class Dither implements Filter {
    static int[][] ditherMatrix = {
            {1, 9, 3, 11},
            {13, 5, 15, 7},
            {4, 12, 2, 10},
            {16, 8, 14, 6}};

    @Override
    public BufferedImage filter(BufferedImage in) {
        final int WIDTH = in.getWidth();
        final int HEIGHT = in.getHeight();
        final BufferedImage dither = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int c, gray;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int pix = in.getRGB(x, y);
                c = (pix & 0xff0000) >> 16;
                gray = c;
                gray += gray * ditherMatrix[x % 3][y % 3] / 17;

                dither.setRGB(x, y, gray < 192 ? 0 : 0xff_ff_ff_ff);
            }
        }
        return dither;
    }
}
