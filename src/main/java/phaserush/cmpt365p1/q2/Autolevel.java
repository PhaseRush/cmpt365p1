package phaserush.cmpt365p1.q2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Autolevel implements Filter{
    @Override
    public BufferedImage filter(BufferedImage in) {
        final int[] rHist = new int[256];
        final int[] gHist = new int[256];
        final int[] bHist = new int[256];

        int r, g, b, a;
        for (int x = 0; x < in.getWidth(); x++) {
            for (int y = 0; y < in.getHeight(); y++) {
                int pix = in.getRGB(x, y);
                b = pix & 0xff;
                g = (pix & 0xff00) >> 8;
                r = (pix & 0xff0000) >> 16;

                rHist[r]++;
                gHist[g]++;
                bHist[b]++;
            }
        }
        final double scale = 255.0 / (in.getWidth() * in.getHeight());
        final int[] rScaledHist = new int[256];
        final int[] gScaledHist = new int[256];
        final int[] bScaledHist = new int[256];

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

        final BufferedImage leveled = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());

        Color pix;
        int rgbaLeveled;
        for (int x = 0; x < in.getWidth(); x++) {
            for (int y = 0; y < in.getHeight(); y++) {
                pix = new Color(in.getRGB(x, y));
                r = rScaledHist[pix.getRed()];
                g = gScaledHist[pix.getGreen()];
                b = bScaledHist[pix.getBlue()];
                a = pix.getAlpha();

                rgbaLeveled = ((a & 0xFF) << 24) |
                        ((r & 0xFF) << 16) |
                        ((g & 0xFF) << 8) |
                        ((b & 0xFF));
                leveled.setRGB(x, y, rgbaLeveled);
            }
        }
        return leveled;
    }
}
