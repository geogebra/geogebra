/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
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
