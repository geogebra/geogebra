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
