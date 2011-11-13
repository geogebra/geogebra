// Copyright 2000-2006, FreeHEP
package org.freehep.graphics2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.Properties;

import org.freehep.util.UserProperties;

/**
 * This class implements all conversions from integer to double as well as a few
 * other convenience functions. It also handles the different drawSymbol and
 * fillSymbol methods and print colors. The drawing of framed strings is
 * broken down to lower level methods.
 * 
 * @author Simon Fischer
 * @author Mark Donszelmann
 * @author Steffen Greiffenberg
 * @version $Id: AbstractVectorGraphics.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public abstract class AbstractVectorGraphics extends VectorGraphics {

    private UserProperties properties;

    private String creator;

    private boolean isDeviceIndependent;

    private SymbolShape cachedShape;

    private int colorMode;

    private Color backgroundColor;

    private Color currentColor;

    private Paint currentPaint;

    private Font currentFont;

    public AbstractVectorGraphics() {
        properties = new UserProperties();
        creator = "FreeHEP Graphics2D Driver";
        isDeviceIndependent = false;
        cachedShape = new SymbolShape();
        colorMode = PrintColor.COLOR;

        // all of these have to be set in the subclasses
        currentFont = null;
        backgroundColor = null;
        currentColor = null;
        currentPaint = null;
    }

    protected AbstractVectorGraphics(AbstractVectorGraphics graphics) {
        super();
        properties = graphics.properties;
        creator = graphics.creator;
        isDeviceIndependent = graphics.isDeviceIndependent;
        cachedShape = graphics.cachedShape;

        backgroundColor = graphics.backgroundColor;
        currentColor = graphics.currentColor;
        currentPaint = graphics.currentPaint;
        colorMode = graphics.colorMode;
        currentFont = graphics.currentFont;
    }

    public void setProperties(Properties newProperties) {
        if (newProperties == null)
            return;
        properties.setProperties(newProperties);
    }

    protected void initProperties(Properties defaults) {
        properties = new UserProperties();
        properties.setProperties(defaults);
    }

    protected Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Color getPropertyColor(String key) {
        return properties.getPropertyColor(key);
    }

    public Rectangle getPropertyRectangle(String key) {
        return properties.getPropertyRectangle(key);
    }

    public Insets getPropertyInsets(String key) {
        return properties.getPropertyInsets(key);
    }

    public Dimension getPropertyDimension(String key) {
        return properties.getPropertyDimension(key);
    }

    public int getPropertyInt(String key) {
        return properties.getPropertyInt(key);
    }

    public double getPropertyDouble(String key) {
        return properties.getPropertyDouble(key);
    }

    public boolean isProperty(String key) {
        return properties.isProperty(key);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        if (creator != null) {
            this.creator = creator;
        }
    }

    public boolean isDeviceIndependent() {
        return isDeviceIndependent;
    }

    public void setDeviceIndependent(boolean isDeviceIndependent) {
        this.isDeviceIndependent = isDeviceIndependent;
    }

    /**
     * Gets the current font.
     * 
     * @return current font
     */
    public Font getFont() {
        return currentFont;
    }

    /**
     * Sets the current font.
     * 
     * @param font to be set
     */
    public void setFont(Font font) {
    	if (font == null) return;
    	
        // FIXME: maybe add delayed setting
        currentFont = font;
    }

    public void drawSymbol(int x, int y, int size, int symbol) {
	drawSymbol((double) x, (double) y, (double) size, symbol);
    }

    public void fillSymbol(int x, int y, int size, int symbol) {
        fillSymbol((double) x, (double) y, (double) size, symbol);
    }

    public void fillAndDrawSymbol(int x, int y, int size, int symbol,
            Color fillColor) {
        fillAndDrawSymbol((double) x, (double) y, (double) size, symbol,
                fillColor);
    }

    public void drawSymbol(double x, double y, double size, int symbol) {
        if (size <= 0)
            return;
        drawSymbol(this, x, y, size, symbol);
    }

    protected void drawSymbol(VectorGraphics g, double x, double y,
            double size, int symbol) {
        switch (symbol) {
        case SYMBOL_VLINE:
        case SYMBOL_STAR:
        case SYMBOL_HLINE:
        case SYMBOL_PLUS:
        case SYMBOL_CROSS:
        case SYMBOL_BOX:
        case SYMBOL_UP_TRIANGLE:
        case SYMBOL_DN_TRIANGLE:
        case SYMBOL_DIAMOND:
            cachedShape.create(symbol, x, y, size);
            g.draw(cachedShape);
            break;

        case SYMBOL_CIRCLE: {
            double diameter = Math.max(1, size);
            diameter += (diameter % 2);
            g.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
            break;
        }
        }
    }

    public void fillSymbol(double x, double y, double size, int symbol) {
        if (size <= 0)
            return;
        fillSymbol(this, x, y, size, symbol);
    }

    protected void fillSymbol(VectorGraphics g, double x, double y,
            double size, int symbol) {
        switch (symbol) {
        case SYMBOL_VLINE:
        case SYMBOL_STAR:
        case SYMBOL_HLINE:
        case SYMBOL_PLUS:
        case SYMBOL_CROSS:
            cachedShape.create(symbol, x, y, size);
            g.draw(cachedShape);
            break;

        case SYMBOL_BOX:
        case SYMBOL_UP_TRIANGLE:
        case SYMBOL_DN_TRIANGLE:
        case SYMBOL_DIAMOND:
            cachedShape.create(symbol, x, y, size);
            g.fill(cachedShape);
            break;

        case SYMBOL_CIRCLE: {
            double diameter = Math.max(1, size);
            diameter += (diameter % 2);
            g.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
            break;
        }
        }
    }

    public void fillAndDrawSymbol(double x, double y, double size, int symbol,
            Color fillColor) {
        Color color = getColor();
        setColor(fillColor);
        fillSymbol(x, y, size, symbol);
        setColor(color);
        drawSymbol(x, y, size, symbol);
    }

    public void fillAndDraw(Shape s, Color fillColor) {
        Color color = getColor();
        setColor(fillColor);
        fill(s);
        setColor(color);
        draw(s);
    }

    // ---------------------------------------------------------
    // -------------------- WRAPPER METHODS --------------------
    // -------------------- int -> double --------------------
    // needs a bias in some cases
    // ---------------------------------------------------------

    private static final double bias = 0.5;
    
    public void clearRect(int x, int y, int width, int height) {
        clearRect((double) x + bias, (double) y + bias, (double) width, (double) height);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        drawLine((double) x1 + bias, (double) y1 + bias, (double) x2 + bias, (double) y2 + bias);
    }

    public void drawRect(int x, int y, int width, int height) {
        drawRect((double) x + bias, (double) y + bias, (double) width, (double) height);
    }

    public void fillRect(int x, int y, int width, int height) {
        fillRect((double) x, (double) y, (double) width, (double) height);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        drawArc((double) x + bias, (double) y + bias, (double) width, (double) height,
                (double) startAngle, (double) arcAngle);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        fillArc((double) x, (double) y, (double) width, (double) height,
                (double) startAngle, (double) arcAngle);
    }

    public void drawOval(int x, int y, int width, int height) {
        drawOval((double) x + bias, (double) y + bias, (double) width, (double) height);
    }

    public void fillOval(int x, int y, int width, int height) {
        fillOval((double) x, (double) y, (double) width, (double) height);
    }

    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        drawRoundRect((double)x + bias, (double)y + bias, (double) width, (double) height, (double) arcWidth,
                (double) arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        fillRoundRect((double) x, (double) y, (double) width, (double) height,
                (double) arcWidth, (double) arcHeight);
    }

    public void translate(int x, int y) {
        translate((double) x, (double) y);
    }

    /*--------------------------------------------------------------------------------
     | 8.1. stroke/linewidth
     *--------------------------------------------------------------------------------*/
    public void setLineWidth(int width) {
        setLineWidth((double) width);
    }

    public void setLineWidth(double width) {
        Stroke stroke = getStroke();
        if (stroke instanceof BasicStroke) {
            BasicStroke cs = (BasicStroke) stroke;
            if (cs.getLineWidth() != width) {
                stroke = new BasicStroke((float) width, cs.getEndCap(), cs
                        .getLineJoin(), cs.getMiterLimit(), cs.getDashArray(),
                        cs.getDashPhase());
                setStroke(stroke);
            }
        } else {
            stroke = new BasicStroke((float) width);
            setStroke(stroke);
        }
    }

    public void drawString(String str, int x, int y) {
        drawString(str, (double) x, (double) y);
    }

    public void drawString(String s, float x, float y) {
        drawString(s, (double) x, (double) y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y);
    }

    /**
     * Draws frame and banner for a TextLayout, which is used for
     * calculation auf ajustment
     *
     * @param tl TextLayout for frame calculation
     * @param x coordinate to draw string
     * @param y coordinate to draw string
     * @param horizontal alignment of the text
     * @param vertical alignment of the text
     * @param framed true if text is surrounded by a frame
     * @param frameColor color of the frame
     * @param frameWidth witdh of the frame
     * @param banner true if the frame is filled by a banner
     * @param bannerColor color of the banner
     * @return Offset for the string inside the frame
     */
    private Point2D drawFrameAndBanner(TextLayout tl, double x, double y, int horizontal,
            int vertical, boolean framed, Color frameColor, double frameWidth,
            boolean banner, Color bannerColor) {

        // calculate string bounds for alignment
        Rectangle2D bounds = tl.getBounds();

        // calculate real bounds
        bounds.setRect(
            bounds.getX(),
            bounds.getY(),
            // care for Italic fonts too
            Math.max(tl.getAdvance(), bounds.getWidth()),
            bounds.getHeight());

        // add x and y
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);

        // horizontal alignment
        if (horizontal == TEXT_RIGHT) {
            at.translate(- bounds.getWidth(), 0);
        } else if (horizontal == TEXT_CENTER) {
            at.translate(- bounds.getWidth() / 2, 0);
        }

        // vertical alignment
        if (vertical == TEXT_BASELINE) {
            // no translation needed
        } else if (vertical == TEXT_TOP) {
            at.translate(0, - bounds.getY());
        } else if (vertical == TEXT_CENTER) {
            // the following adds supersript ascent too,
            // so it does not work
            // at.translate(0, tl.getAscent() / 2);
            // this is nearly the same
            at.translate(0, tl.getDescent());
        } else if (vertical == TEXT_BOTTOM) {
            at.translate(0, - bounds.getHeight() - bounds.getY());
        }

        // transform the bounds
        bounds = at.createTransformedShape(bounds).getBounds2D();
        // create the result with the same transformation
        Point2D result = at.transform(new Point2D.Double(0, 0), new Point2D.Double());

        // space between string and border
        double adjustment = (getFont().getSize2D() * 2) / 10;

        // add the adjustment
        bounds.setRect(
            bounds.getX() - adjustment,
            bounds.getY() - adjustment,
            bounds.getWidth() + 2 * adjustment,
            bounds.getHeight() + 2 * adjustment);

        if (banner) {
            Paint paint = getPaint();
            setColor(bannerColor);
            fill(bounds);
            setPaint(paint);
        }
        if (framed) {
            Paint paint = getPaint();
            Stroke stroke = getStroke();
            setColor(frameColor);
            setLineWidth(frameWidth);
            draw(bounds);
            setPaint(paint);
            setStroke(stroke);
        }

        return result;
    }

    /**
     * Draws frame, banner and aligned text inside
     *
     * @param str text to be drawn
     * @param x coordinate to draw string
     * @param y coordinate to draw string
     * @param horizontal alignment of the text
     * @param vertical alignment of the text
     * @param framed true if text is surrounded by a frame
     * @param frameColor color of the frame
     * @param frameWidth witdh of the frame
     * @param banner true if the frame is filled by a banner
     * @param bannerColor color of the banner
     */
    public void drawString(String str, double x, double y, int horizontal,
            int vertical, boolean framed, Color frameColor, double frameWidth,
            boolean banner, Color bannerColor) {

        // change the x offset for the next drawing
        // FIXME: change y offset for vertical text
        TextLayout tl = new TextLayout(
            str,
            getFont().getAttributes(),
            getFontRenderContext());

        // draw the frame
        Point2D offset = drawFrameAndBanner(
            tl, x, y, horizontal, vertical,
            framed, frameColor, frameWidth, banner, bannerColor);

        // draw the string
        drawString(str, offset.getX(), offset.getY());
    }

    /**
     * Draws the tagged string parsed by a {@link TagHandler} and adds a
     * border specified by the parameters
     *
     * @param str Tagged text to be drawn
     * @param x coordinate to draw string
     * @param y coordinate to draw string
     * @param horizontal alignment of the text
     * @param vertical alignment of the text
     * @param framed true if text is surrounded by a frame
     * @param frameColor color of the frame
     * @param frameWidth witdh of the frame
     * @param banner true if the frame is filled by a banner
     * @param bannerColor color of the banner
     */
    public void drawString(TagString str, double x, double y, int horizontal,
            int vertical, boolean framed, Color frameColor, double frameWidth,
            boolean banner, Color bannerColor) {

		GenericTagHandler tagHandler = new GenericTagHandler(this);
		TextLayout tl = tagHandler.createTextLayout(str, getFont().getSize2D() / 7.5);

        // draw the frame
        Point2D offset = drawFrameAndBanner(
            tl, x, y, horizontal, vertical,
            framed, frameColor, frameWidth, banner, bannerColor);

        // FIXME: not quite clear why correction is needed
        // see {@link GenericTagHandler#superscriptCorrection
        tagHandler.print(str, offset.getX(), offset.getY(), getFont().getSize2D() / 7.5);
    }

    // ------------------ other wrapper methods ----------------

    public void drawString(String str, double x, double y, int horizontal,
            int vertical) {
        drawString(str, x, y, horizontal, vertical, false, null, 0, false, null);
    }

    public void drawString(TagString str, double x, double y) {
        drawString(str, x, y, TEXT_LEFT, TEXT_BASELINE);
    }

    public void drawString(TagString str, double x, double y, int horizontal,
            int vertical) {
        drawString(str, x, y, horizontal, vertical, false, null, 0, false, null);
    }

    /* 8.2. paint/color */
    public int getColorMode() {
        return colorMode;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    /**
     * Gets the background color.
     * 
     * @return background color
     */
    public Color getBackground() {
        return backgroundColor;
    }

    /**
     * Sets the background color.
     * 
     * @param color background color to be set
     */
    public void setBackground(Color color) {
        backgroundColor = color;
    }

    /**
     * Sets the current color and the current paint. Calls writePaint(Color).
     * 
     * @param color to be set
     */
    public void setColor(Color color) {
    	if (color == null) return;
    	
        currentColor = color;
        currentPaint = color;
    }

    /**
     * Gets the current color.
     * 
     * @return the current color
     */
    public Color getColor() {
        return currentColor;
    }

    /**
     * Sets the current paint.
     * 
     * @param paint to be set
     */
    public void setPaint(Paint paint) {
    	if (paint == null) return;
    	
        if (!(paint instanceof Color))
            currentColor = null;
        currentPaint = paint;
    }

    /**
     * Gets the current paint.
     * 
     * @return paint current paint
     */
    public Paint getPaint() {
        return currentPaint;
    }

    /**
     * Returns a printColor created from the original printColor, based on the
     * ColorMode. If you run getPrintColor on it again you still get the same
     * color, so that it does not matter if you convert it more than once.
     */
    protected Color getPrintColor(Color color) {
        // shortcut if mode is COLOR for speed
        if (colorMode == PrintColor.COLOR)
            return color;

        // otherwise...
        PrintColor printColor = PrintColor.createPrintColor(color);
        return printColor.getColor(colorMode);
    }

    public void rotate(double theta, double x, double y) {
        translate(x, y);
        rotate(theta);
        translate(-x, -y);
    }

    public void drawArc(double x, double y, double width, double height,
            double startAngle, double arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle,
                Arc2D.OPEN));
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    public void drawOval(double x, double y, double width, double height) {
        draw(new Ellipse2D.Double(x, y, width, height));
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        draw(createShape(xPoints, yPoints, nPoints, false, true));
    }

    public void drawPolyline(double[] xPoints, double[] yPoints, int nPoints) {
        draw(createShape(xPoints, yPoints, nPoints, false));
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        draw(createShape(xPoints, yPoints, nPoints, true, true));
    }

    public void drawPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        draw(createShape(xPoints, yPoints, nPoints, true));
    }

    public void drawRect(double x, double y, double width, double height) {
        draw(new Rectangle2D.Double(x, y, width, height));
    }

    public void drawRoundRect(double x, double y, double width, double height,
            double arcWidth, double arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth,
                arcHeight));
    }

    public void fillArc(double x, double y, double width, double height,
            double startAngle, double arcAngle) {
        fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle,
                Arc2D.PIE));
    }

    public void fillOval(double x, double y, double width, double height) {
        fill(new Ellipse2D.Double(x, y, width, height));
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        fill(createShape(xPoints, yPoints, nPoints, true, false));
    }

    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        fill(createShape(xPoints, yPoints, nPoints, true));
    }

    public void fillRect(double x, double y, double width, double height) {
        fill(new Rectangle2D.Double(x, y, width, height));
    }

    public void fillRoundRect(double x, double y, double width, double height,
            double arcWidth, double arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth,
                arcHeight));
    }

    /**
     * Creates a polyline/polygon shape from a set of points. Needs to be
     * defined in subclass because its implementations could be device specific
     * 
     * @param xPoints X coordinates of the polyline.
     * @param yPoints Y coordinates of the polyline.
     * @param nPoints number of points of the polyline.
     * @param close is shape closed
     */
    protected abstract Shape createShape(double[] xPoints, double[] yPoints,
            int nPoints, boolean close);

    /**
     * Creates a polyline/polygon shape from a set of points.
     * Needs a bias!
     * 
     * @param xPoints X coordinates of the polyline.
     * @param yPoints Y coordinates of the polyline.
     * @param nPoints number of points of the polyline.
     * @param close is shape closed
     */
    protected Shape createShape(int[] xPoints, int[] yPoints, int nPoints,
            boolean close, boolean biased) {
        
        float offset = biased ? (float)bias : 0.0f;
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        if (nPoints > 0) {
            path.moveTo(xPoints[0] + offset, yPoints[0] + offset);
            int lastX = xPoints[0];
            int lastY = yPoints[0];
            if (close && (Math.abs(xPoints[nPoints - 1] - lastX) < 1)
                    && (Math.abs(yPoints[nPoints - 1] - lastY) < 1)) {
                nPoints--;
            }
            for (int i = 1; i < nPoints; i++) {
                if ((Math.abs(xPoints[i] - lastX) > 1)
                        || (Math.abs(yPoints[i] - lastY) > 1)) {
                    path.lineTo(xPoints[i] + offset, yPoints[i] + offset);
                    lastX = xPoints[i];
                    lastY = yPoints[i];
                }
            }
            if (close)
                path.closePath();
        }
        return path;
    }    
}
