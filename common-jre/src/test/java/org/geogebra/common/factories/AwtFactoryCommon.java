package org.geogebra.common.factories;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GBufferedImageCommon;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GDimensionCommon;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.gui.font.GFontCommon;

/**
 * Class used for testing.
 */
public class AwtFactoryCommon extends AwtFactory {

    @Override
    public GAffineTransform newAffineTransform() {
        return null;
    }

    @Override
    public GRectangle2D newRectangle2D() {
        return null;
    }

    @Override
    public GRectangle newRectangle(int x, int y, int w, int h) {
        return null;
    }

    @Override
    public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight, double pixelRatio) {
		return new GBufferedImageCommon();
    }

    @Override
    public GBufferedImage createBufferedImage(int width, int height, boolean transparency) {
        return new GBufferedImageCommon();
    }

    @Override
    public MyImage newMyImage(int pixelWidth, int pixelHeight, int typeIntArgb) {
        return null;
    }

    @Override
    public GDimension newDimension(int width, int height) {
		return new GDimensionCommon();
    }

    @Override
    public GPoint2D newPoint2D() {
        return null;
    }

    @Override
    public GRectangle newRectangle(int w, int h) {
        return null;
    }

    @Override
    public GRectangle newRectangle() {
        return null;
    }

    @Override
    public GPoint2D newPoint2D(double x, double y) {
        return null;
    }

    @Override
    public GGeneralPath newGeneralPath() {
        return null;
    }

    @Override
    public GBasicStroke newMyBasicStroke(double f) {
		return new GStrokeCommon();
    }

    @Override
    public GBasicStroke newBasicStroke(double f, int cap, int join) {
		return new GStrokeCommon();
    }

    @Override
    public GBasicStroke newBasicStroke(double width, int endCap, int lineJoin, double miterLimit, double[] dash) {
		return new GStrokeCommon();
    }

    @Override
    public GBasicStroke newBasicStroke(double f) {
        return null;
    }

    @Override
    public GLine2D newLine2D() {
        return null;
    }

    @Override
    public GRectangle newRectangle(GRectangle bb) {
        return null;
    }

    @Override
    public GEllipse2DDouble newEllipse2DDouble() {
        return null;
    }

    @Override
    public GEllipse2DDouble newEllipse2DDouble(double x, double y, double w, double h) {
        return null;
    }

    @Override
    public GArc2D newArc2D() {
        return null;
    }

    @Override
    public GQuadCurve2D newQuadCurve2D() {
        return null;
    }

    @Override
    public GArea newArea() {
        return null;
    }

    @Override
    public GArea newArea(GShape shape) {
        return null;
    }

    @Override
    public GGeneralPath newGeneralPath(int rule) {
        return null;
    }

    @Override
    public GTextLayout newTextLayout(String string, GFont fontLine, GFontRenderContext frc) {
        return null;
    }

    @Override
    public GAlphaComposite newAlphaComposite(double alpha) {
        return null;
    }

    @Override
    public GBasicStroke newBasicStrokeJoinMitre(double f) {
        return null;
    }

    @Override
    public GGradientPaint newGradientPaint(double x, double y, GColor bg2, double x2, double i, GColor bg) {
        return null;
    }

    @Override
    public FocusListener newFocusListener(Object listener) {
        return null;
    }

    @Override
    public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
        return null;
    }

    @Override
    public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
        return null;
    }

    @Override
    public GFont newFont(String name, int style, int size) {
        return new GFontCommon(size);
    }
}
