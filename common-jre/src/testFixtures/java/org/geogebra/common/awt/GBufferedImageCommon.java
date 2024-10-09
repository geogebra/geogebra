package org.geogebra.common.awt;

import java.util.stream.Stream;

/**
 * Buffered image used for testing.
 */
public class GBufferedImageCommon implements GBufferedImage {

    private final int width;
    private final int height;

    /**
     * @param pixelWidth width
     * @param pixelHeight height
     */
    public GBufferedImageCommon(int pixelWidth, int pixelHeight) {
        this.width = pixelWidth;
        this.height = pixelHeight;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public GGraphics2D createGraphics() {
        return new GGraphicsCommon();
    }

    @Override
    public GBufferedImage getSubimage(int x, int y, int w, int h) {
        if (w + x > width || h + y > height || Stream.of(x, y, w, h).anyMatch(v -> v < 0)) {
            throw new IllegalArgumentException("Out of image bounds");
        }
        return new GBufferedImageCommon(w, h);
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
