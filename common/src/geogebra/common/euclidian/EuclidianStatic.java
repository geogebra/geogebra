package geogebra.common.euclidian;

import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.Font;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;


/**
 * @author gabor@gegeobra.org
 *
 *
 *Abstract class for EuclidianStatic
 */
public abstract class EuclidianStatic {
	// need to clip just outside the viewing area when drawing eg vectors
		// as a near-horizontal thick vector isn't drawn correctly otherwise
		public static final int CLIP_DISTANCE = 5;
		
	public static EuclidianStatic prototype;
	protected static BasicStroke standardStroke = 
			geogebra.common.factories.AwtFactory.prototype.newMyBasicStroke(1.0f);

	protected static BasicStroke selStroke = 
			geogebra.common.factories.AwtFactory.prototype.newMyBasicStroke(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	static public BasicStroke getDefaultStroke() {
		return standardStroke;
	}
	static public BasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}
	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * 
	 * @param width
	 * @param type
	 * @return stroke
	 */
	public static geogebra.common.awt.BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianStyleConstants.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke
				.getEndCap();

		return geogebra.common.factories.AwtFactory.prototype.newBasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}
/*
	public abstract float textWidth(String str, Font font, FontRenderContext frc);
	*/
	
	/**
	 * Draw a multiline LaTeX label.
	 * 
	 * TODO: Improve performance (caching, etc.) Florian Sonner
	 * 
	 * @param g2
	 * @param font
	 * @param fgColor
	 * @param bgColor
	 */
	public static final geogebra.common.awt.Rectangle drawMultilineLaTeX(AbstractApplication app,
			geogebra.common.awt.Graphics2D tempGraphics, GeoElement geo, geogebra.common.awt.Graphics2D g2, geogebra.common.awt.Font font,
			geogebra.common.awt.Color fgColor, geogebra.common.awt.Color bgColor, String labelDesc, int xLabel,
			int yLabel, boolean serif) {
		return prototype.doDrawMultilineLaTeX(app, tempGraphics, geo, g2, font, fgColor, bgColor, labelDesc, xLabel, yLabel, serif);
	}
	protected abstract geogebra.common.awt.Rectangle doDrawMultilineLaTeX(AbstractApplication app,
			geogebra.common.awt.Graphics2D tempGraphics, GeoElement geo, geogebra.common.awt.Graphics2D g2, geogebra.common.awt.Font font,
			geogebra.common.awt.Color fgColor, geogebra.common.awt.Color bgColor, String labelDesc, int xLabel,
			int yLabel, boolean serif);
	
	/**
	 * Draws a string str with possible indices to g2 at position x, y. The
	 * indices are drawn using the given indexFont. Examples for strings with
	 * indices: "a_1" or "s_{ab}"
	 * 
	 * @param g2
	 * @param str
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	public static geogebra.common.awt.Point drawIndexedString(AbstractApplication app, geogebra.common.awt.Graphics2D g3,
			String str, float xPos, float yPos, boolean serif) {
		return prototype.doDrawIndexedString(app, g3, str, xPos, yPos, serif);
	}
	
	protected abstract geogebra.common.awt.Point doDrawIndexedString(AbstractApplication app, geogebra.common.awt.Graphics2D g3,
			String str, float xPos, float yPos, boolean serif);
	protected abstract  void doFillWithValueStrokePure(geogebra.common.awt.Shape shape, geogebra.common.awt.Graphics2D g3);
	public static void fillWithValueStrokePure(geogebra.common.awt.Shape shape, geogebra.common.awt.Graphics2D g3){
		prototype.doFillWithValueStrokePure(shape, g3);
		
	}
	public final static geogebra.common.awt.Rectangle drawMultiLineText(AbstractApplication app,
			String labelDesc, int xLabel, int yLabel, geogebra.common.awt.Graphics2D g2,
			boolean serif) {
		return prototype.doDrawMultiLineText(app, labelDesc, xLabel, yLabel, g2, serif);
	}
	protected abstract  geogebra.common.awt.Rectangle doDrawMultiLineText(AbstractApplication app,
			String labelDesc, int xLabel, int yLabel, geogebra.common.awt.Graphics2D g2,
			boolean serif); 
}
