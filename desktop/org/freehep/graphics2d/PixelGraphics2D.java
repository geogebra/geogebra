// Copyright 2000-2006, FreeHEP.
package org.freehep.graphics2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.print.PrinterGraphics;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import org.freehep.graphics2d.font.FontEncoder;

/**
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: PixelGraphics2D.java,v 1.6 2009-08-17 21:44:44 murkle Exp $
 */
public class PixelGraphics2D extends AbstractVectorGraphics {

    public final static RenderingHints.Key KEY_SYMBOL_BLIT = new SymbolBlitKey();

    public final static Object VALUE_SYMBOL_BLIT_ON = Boolean.TRUE;

    public final static Object VALUE_SYMBOL_BLIT_OFF = Boolean.FALSE;

    static class SymbolBlitKey extends RenderingHints.Key {
        public SymbolBlitKey() {
            super(94025);
        }

        public boolean isCompatibleValue(Object o) {
            if (o.equals(VALUE_SYMBOL_BLIT_ON))
                return true;
            if (o.equals(VALUE_SYMBOL_BLIT_OFF))
                return true;
            return false;
        }

        public String toString() {
            return "Symbol Blitting enable key";
        }
    }

    // The host graphics context.
    protected Graphics2D hostGraphics;

    protected double lineWidth;

    protected int resolution;

    // tag handler
    protected GenericTagHandler tagHandler;

    // fast symbol handling, FIXME: does not work for fillAndDraw
    private static final int MAX_BLIT_SIZE = 32;

    private static Map /* <color, Image[fill][symbol][size]> */symbols;

    private WebColor webColor;

    // graphics environment stuff
    private static boolean displayX11;

    private static boolean displayLocal;

    static {
        symbols = new HashMap();

        displayX11 = false;
        displayLocal = false;
        try {
            Class clazz = Class.forName("sun.awt.X11GraphicsEnvironment");
            displayX11 = true;
            Method method = clazz.getMethod("isDisplayLocal", null);
            Boolean result = (Boolean) method.invoke(null,null);
            displayLocal = result.booleanValue();
        } catch (ClassNotFoundException e) {
            // Windows case...
            displayLocal = true;
        } catch (IllegalAccessException e) {
            // ignored
        } catch (NoSuchMethodException e) {
            // ignored
        } catch (InvocationTargetException e) {
            // ignored
        } catch (ClassCastException e) {
            // ignored
        } catch (SecurityException e) {
            // ignored
        } catch (NullPointerException e){
        	// method.invoke throws ExceptionInInitializerError on Ubuntu 12.10.
        	// Seems to be a bug in the initialization of a static variable inside
        	// the method isDisplayLocal. See
        	// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/awt/X11GraphicsEnvironment.java#X11GraphicsEnvironment.isDisplayLocal() 
        	// Also oracle jdk 1.7 
        }
    }

    public PixelGraphics2D(Graphics graphics) {
        this();
        setHostGraphics(graphics);
    }

    /**
     */
    protected PixelGraphics2D(PixelGraphics2D graphics) {
        super(graphics);
        setHostGraphics(graphics.hostGraphics.create());
    }

    /**
     * Constructor for the case that you only know the hostgraphics later on.
     * NOTE: make sure you call setHostGraphics afterwards.
     */
    protected PixelGraphics2D() {
        super();
    }

    protected void setHostGraphics(Graphics graphics) {
        hostGraphics = (Graphics2D) graphics;
        resolution = (graphics instanceof PrinterGraphics) ? 0 : 1;
        tagHandler = new GenericTagHandler(hostGraphics);

        super.setBackground(hostGraphics.getBackground());
        super.setColor(hostGraphics.getColor());
        super.setPaint(hostGraphics.getPaint());
        super.setFont(hostGraphics.getFont());

        Stroke s = hostGraphics.getStroke();
        if (s instanceof BasicStroke) {
            lineWidth = ((BasicStroke) s).getLineWidth();
        }
        webColor = WebColor.create(getColor());
        setRenderingHint(KEY_SYMBOL_BLIT, VALUE_SYMBOL_BLIT_ON);
    }

    public void startExport() {
    }

    public void endExport() {
    }

    public void printComment(String comment) {
    }

    public Graphics create(double x, double y, double width, double height) {
        PixelGraphics2D graphics = new PixelGraphics2D(this);
        graphics.translate(x, y);
        graphics.clipRect(0, 0, width, height);
        return graphics;
    }

    // //
    // The methods which follow are those necessary for the Graphics
    // and Graphics2D contexts. These simply call the appropriate
    // method on the underlying graphics context.
    // //

    public void clearRect(int x, int y, int width, int height) {
        hostGraphics.clearRect(x, y, width, height);
    }

    public void clipRect(int x, int y, int width, int height) {
        hostGraphics.clipRect(x, y, width, height);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        hostGraphics.copyArea(x, y, width, height, dx, dy);
    }

    public Graphics create() {
        return new PixelGraphics2D(this);
    }

    public void dispose() {
        hostGraphics.dispose();
    }

    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        hostGraphics.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor,
            ImageObserver observer) {
        return hostGraphics.drawImage(img, x, y, getPrintColor(bgcolor),
                observer);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return hostGraphics.drawImage(img, x, y, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        return hostGraphics.drawImage(img, x, y, width, height,
                getPrintColor(bgcolor), observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer) {
        return hostGraphics.drawImage(img, x, y, width, height, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        return hostGraphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2,
                sy2, getPrintColor(bgcolor), observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return hostGraphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2,
                sy2, observer);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        hostGraphics.drawLine(x1, y1, x2, y2);
    }

    public void drawOval(int x, int y, int width, int height) {
        hostGraphics.drawOval(x, y, width, height);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        hostGraphics.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void drawPolygon(Polygon p) {
        hostGraphics.drawPolygon(p);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        hostGraphics.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawRect(int x, int y, int width, int height) {
        hostGraphics.drawRect(x, y, width, height);
    }

    public void drawString(String str, int x, int y) {
        str = FontEncoder.getEncodedString(str, getFont().getName());
        hostGraphics.drawString(str, x, y);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        hostGraphics.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillOval(int x, int y, int width, int height) {
        hostGraphics.fillOval(x, y, width, height);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        hostGraphics.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void fillPolygon(Polygon p) {
        hostGraphics.fillPolygon(p);
    }

    public void fillRect(int x, int y, int width, int height) {
        hostGraphics.fillRect(x, y, width, height);
    }

    public void drawSymbol(double x, double y, double size, int symbol) {
        if (size <= 0)
            return;

        int intSize = (int) Math.ceil(size);
        if ((intSize > MAX_BLIT_SIZE)
                || (lineWidth != 1.0)
                || !isDisplayLocal()
                || (getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON)
                || (getRenderingHint(KEY_SYMBOL_BLIT) == VALUE_SYMBOL_BLIT_OFF)) {
            super.drawSymbol(x, y, size, symbol);
            return;
        }

        blitSymbol(x, y, intSize, symbol, false);
    }

    public void fillSymbol(double x, double y, double size, int symbol) {
        if (size <= 0)
            return;

        int intSize = (int) Math.ceil(size);
        if ((intSize > MAX_BLIT_SIZE)
                || (lineWidth != 1.0)
                || !isDisplayLocal()
                || (getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON)
                || (getRenderingHint(KEY_SYMBOL_BLIT) == VALUE_SYMBOL_BLIT_OFF)) {
            super.fillSymbol(x, y, size, symbol);
            return;
        }

        blitSymbol(x, y, intSize, symbol, true);
    }

    // FIXME: overridden to avoid blitting
    public void fillAndDrawSymbol(double x, double y, double size, int symbol,
            Color fillColor) {
        Color color = getColor();
        setColor(fillColor);
        super.fillSymbol(x, y, size, symbol);
        setColor(color);
        super.drawSymbol(x, y, size, symbol);
    }

    private void blitSymbol(double x, double y, int size, int symbol,
            boolean fill) {
        Image[][][] images = (Image[][][]) symbols.get(webColor);
        if (images == null) {
            images = new Image[2][NUMBER_OF_SYMBOLS][MAX_BLIT_SIZE];
            symbols.put(webColor, images);
        }

        Image image = images[fill ? 1 : 0][symbol][size - 1];
        int imageSize = size + 1;
        double imageSize2 = imageSize / 2.0;
        if (image == null) {
            image = getDeviceConfiguration().createCompatibleImage(
                    imageSize + 1, imageSize + 1, Transparency.BITMASK);
            VectorGraphics imageGraphics = VectorGraphics.create(image
                    .getGraphics());
            Composite composite = imageGraphics.getComposite();
            imageGraphics.setComposite(AlphaComposite.Clear);
            imageGraphics.fillRect(0, 0, size, size);
            imageGraphics.setComposite(composite);
            imageGraphics.setColor(getColor());
            if (fill) {
                fillSymbol(imageGraphics, imageSize2, imageSize2, size, symbol);
            } else {
                drawSymbol(imageGraphics, imageSize2, imageSize2, size, symbol);
            }
            images[fill ? 1 : 0][symbol][size - 1] = image;
        }
        drawImage(image, (int) (x - imageSize2), (int) (y - imageSize2), null);
    }

    public void setLineWidth(double width) {
        super.setLineWidth(width);
        lineWidth = width;
    }

    public Shape getClip() {
        return hostGraphics.getClip();
    }

    public Rectangle getClipBounds() {
        return hostGraphics.getClipBounds();
    }

    public Rectangle getClipBounds(Rectangle r) {
        return hostGraphics.getClipBounds(r);
    }

    public FontMetrics getFontMetrics(Font f) {
        return hostGraphics.getFontMetrics(f);
    }

    public void setClip(int x, int y, int width, int height) {
        hostGraphics.setClip(x, y, width, height);
    }

    public void setClip(Shape clip) {
        hostGraphics.setClip(clip);
    }

    public void setFont(Font font) {
    	if (font == null) return;
    	
        super.setFont(font);
        if (font.getName().equals("Symbol")
                || font.getName().equals("ZapfDingbats")) {
            Font newFont = new Font("Serif", font.getSize(), font.getStyle());
            font = newFont.deriveFont(font.getSize2D());
        }
        hostGraphics.setFont(font);
    }

    public void setColor(Color color) {
    	if (color == null) return;
    	
        if (color.equals(getColor()))
            return;

        super.setColor(color);
        hostGraphics.setColor(getPrintColor(color));
        webColor = WebColor.create(color);
    }

    public void setPaint(Paint paint) {
    	if (paint == null) return; 
    	
        if (paint.equals(getPaint()))
            return;

        if (paint instanceof Color) {
            setColor((Color) paint);
        } else {
            super.setPaint(paint);
            hostGraphics.setPaint(paint);
        }
    }

    public void setPaintMode() {
        hostGraphics.setPaintMode();
    }

    public void setXORMode(Color c1) {
        hostGraphics.setXORMode(getPrintColor(c1));
    }

    public void translate(int x, int y) {
        hostGraphics.translate(x, y);
    }

    // //
    // The ones from the Graphic2D context. This overrides some
    // methods defined in VectorGraphics2D by simply calling the
    // underlying host graphics context.
    // //

    public void addRenderingHints(Map hints) {
        hostGraphics.addRenderingHints(hints);
    }

    public void clip(Shape clip) {
        hostGraphics.clip(clip);
    }

    public void draw(Shape s) {
        hostGraphics.draw(s);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        hostGraphics.drawGlyphVector(g, x, y);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        hostGraphics.drawImage(img, op, x, y);
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return hostGraphics.drawImage(img, xform, obs);
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        hostGraphics.drawRenderableImage(img, xform);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        hostGraphics.drawRenderedImage(img, xform);
    }

    public void drawString(AttributedCharacterIterator iterator, float x,
            float y) {
        hostGraphics.drawString(iterator, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        hostGraphics.drawString(iterator, x, y);
    }

    public void drawString(String str, float x, float y) {
        str = FontEncoder.getEncodedString(str, getFont().getName());
        hostGraphics.drawString(str, x, y);
    }

    public void fill(Shape s) {
        hostGraphics.fill(s);
    }

    public Composite getComposite() {
        return hostGraphics.getComposite();
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return hostGraphics.getDeviceConfiguration();
    }

    public FontRenderContext getFontRenderContext() {
        return hostGraphics.getFontRenderContext();
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return hostGraphics.getRenderingHint(hintKey);
    }

    public RenderingHints getRenderingHints() {
        return hostGraphics.getRenderingHints();
    }

    public Stroke getStroke() {
        return hostGraphics.getStroke();
    }

    public AffineTransform getTransform() {
        return hostGraphics.getTransform();
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return hostGraphics.hit(rect, s, onStroke);
    }

    public void rotate(double theta) {
        hostGraphics.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        hostGraphics.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        hostGraphics.scale(sx, sy);
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        hostGraphics.setBackground(getPrintColor(color));
    }

    public void setComposite(Composite comp) {
        hostGraphics.setComposite(comp);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        hostGraphics.setRenderingHint(hintKey, hintValue);
    }

    public void setRenderingHints(Map hints) {
        hostGraphics.setRenderingHints(hints);
    }

    public void setStroke(Stroke s) {
        hostGraphics.setStroke(s);
    }

    public void setTransform(AffineTransform Tx) {
        hostGraphics.setTransform(Tx);
    }

    public void shear(double shx, double shy) {
        hostGraphics.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        hostGraphics.transform(Tx);
    }

    public void translate(double tx, double ty) {
        hostGraphics.translate(tx, ty);
    }

    // //
    // The following methods are those specific to the VectorGraphics
    // interfaces, but which are simple extensions of integer method
    // calls to doubles.
    // //

    public void clearRect(double x, double y, double width, double height) {
        clearRect((int) x, (int) y, (int) width, (int) height);
    }

    public void clipRect(double x, double y, double width, double height) {
        clipRect((int) x, (int) y, (int) width, (int) height);
    }

    public void drawString(String str, double x, double y) {
        drawString(str, (int) Math.round(x), (int) Math.round(y));
    }

    public void setClip(double x, double y, double width, double height) {
        setClip(new Rectangle2D.Double(x, y, width, height));
    }

    public String toString() {
        return "PixelGraphics2D[" + hostGraphics.toString() + "]";
    }

    public static boolean isDisplayX11() {
        return displayX11;
    }

    public static boolean isDisplayLocal() {
        return displayLocal;
    }

    /**
     * Implementation of createShape makes sure that the points are different by
     * at least one pixel.
     */
    protected Shape createShape(double[] xPoints, double[] yPoints,
            int nPoints, boolean close) {
        return new ArrayPath(xPoints, yPoints, nPoints, close, resolution);
    }
    /*
     * protected GeneralPath createShape(double[] xPoints, double[] yPoints, int
     * nPoints, boolean close) { GeneralPath path = new
     * GeneralPath(GeneralPath.WIND_EVEN_ODD); if (nPoints > 0) {
     * path.moveTo((float)xPoints[0], (float)yPoints[0]); double lastX =
     * xPoints[0]; double lastY = yPoints[0]; if (close &&
     * (Math.abs(xPoints[nPoints-1] - lastX) < resolution) &&
     * (Math.abs(yPoints[nPoints-1] - lastY) < resolution)) { nPoints--; } for
     * (int i = 1; i < nPoints; i++) { if ((Math.abs(xPoints[i] - lastX) >
     * resolution) || (Math.abs(yPoints[i] - lastY) > resolution)) {
     * path.lineTo((float)xPoints[i], (float)yPoints[i]); lastX = xPoints[i];
     * lastY = yPoints[i]; } } if (close) path.closePath(); } return path; }
     */
}
