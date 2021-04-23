package org.geogebra.common.awt;

import org.geogebra.common.gui.font.GFontCommon;

/**
 * Class used for testing.
 */
public class GGraphicsCommon implements GGraphics2D {

    @Override
    public void draw(GShape s) {
        if (s == null) {
            throw new IllegalArgumentException("Shape cannot be null");
        }
    }

    @Override
    public void drawImage(GBufferedImage img, int x, int y) {
        // ignore empty method
    }

    @Override
    public void drawImage(MyImage img, int x, int y) {
        // ignore empty method
    }

    @Override
    public void drawString(String str, int x, int y) {
        // ignore empty method
    }

    @Override
    public void drawString(String str, double x, double y) {
        // ignore empty method
    }

    @Override
    public void fill(GShape s) {
        if (s == null) {
            throw new IllegalArgumentException("Shape cannot be null");
        }
    }

    @Override
    public void setComposite(GComposite comp) {
        // ignore empty method
    }

    @Override
    public void setPaint(GPaint paint) {
        // ignore empty method
    }

    @Override
    public void setStroke(GBasicStroke s) {
        // ignore empty method
    }

    @Override
    public void setRenderingHint(int hintKey, int hintValue) {
        // ignore empty method
    }

    @Override
    public void translate(double tx, double ty) {
        // ignore empty method
    }

    @Override
    public void scale(double sx, double sy) {
        // ignore empty method
    }

    @Override
    public void transform(GAffineTransform Tx) {
        // ignore empty method
    }

    @Override
    public GComposite getComposite() {
        return null;
    }

    @Override
    public GColor getBackground() {
        return null;
    }

    @Override
    public GBasicStroke getStroke() {
        return null;
    }

    @Override
    public GFontRenderContext getFontRenderContext() {
        return null;
    }

    @Override
    public GColor getColor() {
        return null;
    }

    @Override
    public GFont getFont() {
		return new GFontCommon(12);
    }

    @Override
    public void setFont(GFont font) {
        // ignore empty method
    }

    @Override
    public void setColor(GColor selColor) {
        // ignore empty method
    }

    @Override
    public void fillRect(int x, int y, int w, int h) {
        // ignore empty method
    }

    @Override
    public void clearRect(int x, int y, int w, int h) {
        // ignore empty method
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        // ignore empty method
    }

    @Override
    public void setClip(GShape shape) {
        // ignore empty method
    }

    @Override
    public void setClip(GShape shape, boolean saveContext) {
        // ignore empty method
    }

    @Override
    public void resetClip() {
        // ignore empty method
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        // ignore empty method
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        // ignore empty method
    }

    @Override
    public void setClip(int x, int y, int width, int height, boolean saveContext) {
        // ignore empty method
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // ignore empty method
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // ignore empty method
    }

    @Override
    public void setAntialiasing() {
        // ignore empty method
    }

    @Override
    public void setTransparent() {
        // ignore empty method
    }

    @Override
    public Object setInterpolationHint(boolean needsInterpolationRenderingHint) {
        return null;
    }

    @Override
    public void resetInterpolationHint(Object oldInterpolationHint) {
        // ignore empty method
    }

    @Override
    public void updateCanvasColor() {
        // ignore empty method
    }

    @Override
    public void drawStraightLine(double xCrossPix, double d, double xCrossPix2, double i) {
        // ignore empty method
    }

    @Override
    public void saveTransform() {
        // ignore empty method
    }

    @Override
    public void restoreTransform() {
        // ignore empty method
    }

    @Override
    public void startGeneralPath() {
        // ignore empty method
    }

    @Override
    public void addStraightLineToGeneralPath(double x1, double y1, double x2, double y2) {
        // ignore empty method
    }

    @Override
    public void endAndDrawGeneralPath() {
        // ignore empty method
    }

    @Override
    public void drawImage(MyImage img, int sx, int sy, int sw, int sh,
            int dx, int dy, int dw, int dh) {
        // ignore empty method
    }

    @Override
    public void drawImage(MyImage img, int dx, int dy, int dw, int dh) {
        // ignore empty method
    }
}
