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
 
package org.geogebra.common.gui.font;

import org.geogebra.common.awt.GFont;

/**
 * Font used for testing.
 */
public class GFontCommon extends GFont {

    private int size;

    /**
     * Construct a new font object.
     *
     * @param size size
     */
    public GFontCommon(int size) {
        this.size = size;
    }

    @Override
    public int getStyle() {
        return 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isItalic() {
        return false;
    }

    @Override
    public boolean isBold() {
        return false;
    }

    @Override
    public int canDisplayUpTo(String str) {
        return 0;
    }

    @Override
    public GFont deriveFont(int style, int fontSize) {
		return new GFontCommon(fontSize);
    }

    @Override
    public GFont deriveFont(int style, double fontSize) {
		return this;
    }

    @Override
    public GFont deriveFont(int style) {
		return this;
    }

    @Override
    public String getFontName() {
        return null;
    }
}
