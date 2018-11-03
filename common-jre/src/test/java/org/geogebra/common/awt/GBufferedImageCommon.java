package org.geogebra.common.awt;

/**
 * Buffered image used for testing.
 */
public class GBufferedImageCommon implements GBufferedImage {

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public GGraphics2D createGraphics() {
        return new GGraphicsCommon();
    }

    @Override
    public GBufferedImage getSubimage(int x, int y, int w, int h) {
        return null;
    }

    @Override
    public void flush() {
        // ignore
    }

    @Override
    public String getBase64() {
        return null;
    }
}
