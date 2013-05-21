package geogebra.web.gui.util;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.html5.awt.GBasicStrokeW;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianStaticW;
import geogebra.html5.openjdk.awt.geom.Ellipse2D;
import geogebra.html5.openjdk.awt.geom.GeneralPath;
import geogebra.html5.openjdk.awt.geom.Line2D;

import com.google.gwt.canvas.client.Canvas;

public class PointStyleImage {
	
	private int pointStyle = -1;

	// for drawing
	private int pointSize = 4;
	private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Line2D.Double line1, line2, line3, line4;
	private GeneralPath gp = null;
	private GBasicStrokeW borderStroke = (GBasicStrokeW) EuclidianStaticW.getDefaultStroke();
	private GBasicStrokeW[] crossStrokes = new GBasicStrokeW[10];
	private int h,w;
	Canvas c = null;

	public PointStyleImage(GDimensionW d, int pointStyle, int pointSize,
            GColor fgColor, GColor bgColor) {
		this.h = d.getHeight();
		this.w = d.getWidth();
		this.pointStyle = pointStyle;
		this.pointSize = pointSize;
		c = Canvas.createIfSupported();
		c.setCoordinateSpaceHeight(h);
		c.setCoordinateSpaceWidth(h);
		c.setWidth(w+"px");
		c.setHeight(h+"px");
		
		drawPointStyle(fgColor, bgColor);
    }
	
	public void drawPointStyle(GColor fgColor, GColor bgColor) {
		
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING,
				GRenderingHints.VALUE_ANTIALIAS_ON);

		// set background
		if (bgColor != null) 
			g2.setBackground(bgColor);

		// draw point using routine from euclidian.DrawPoint
		g2.setPaint(fgColor);
		getPath();

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_PLUS:
		case EuclidianStyleConstants.POINT_STYLE_CROSS:
			// draw cross like: X or +
			g2.setStroke(crossStrokes[pointSize]);
			g2.draw(line1);
			g2.draw(line2);
			break;

		case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
			// draw diamond
			g2.setStroke(crossStrokes[pointSize]);
			g2.draw(line1);
			g2.draw(line2);
			g2.draw(line3);
			g2.draw(line4);
			break;

		case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
			// draw diamond
			g2.setStroke(crossStrokes[pointSize]);
			// drawWithValueStrokePure(gp, g2);
			g2.draw(gp);
			g2.fill(gp);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			// draw a circle
			g2.setStroke(crossStrokes[pointSize]);
			g2.draw(circle);
			break;

			// case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
		default:
			// draw a dot
			g2.fill(circle);
			g2.setStroke(borderStroke);
			g2.draw(circle);
		}
	}

	public void getPath() {
		// clear old path
		if (gp != null)
			gp.reset();

		// set point size
		//pointSize = 4;
		int diameter = 2 * pointSize;

		// set coords = center of cell
		double[] coords = new double[2];
		coords[0] = w / 2.0;
		coords[1] = h / 2.0;

		// get draw path using routine from euclidian.DrawPoint
		double xUL = coords[0] - pointSize;
		double yUL = coords[1] - pointSize;
		double root3over2 = Math.sqrt(3.0) / 2.0;

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:

			double xR = coords[0] + pointSize;
			double yB = coords[1] + pointSize;

			if (gp == null) {
				gp = new GeneralPath();
			}
			gp.moveTo((float) (xUL + xR) / 2, (float) yUL);
			gp.lineTo((float) xUL, (float) (yB + yUL) / 2);
			gp.lineTo((float) (xUL + xR) / 2, (float) yB);
			gp.lineTo((float) xR, (float) (yB + yUL) / 2);
			gp.closePath();

			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:

			double direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH)
				direction = -1.0;

			if (gp == null) {
				gp = new GeneralPath();
			}
			gp.moveTo((float) coords[0], (float) (coords[1] + direction
					* pointSize));
			gp.lineTo((float) (coords[0] + pointSize * root3over2),
					(float) (coords[1] - direction * pointSize / 2));
			gp.lineTo((float) (coords[0] - pointSize * root3over2),
					(float) (coords[1] - direction * pointSize / 2));
			gp.lineTo((float) coords[0], (float) (coords[1] + direction
					* pointSize));
			gp.closePath();

			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:

			direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST)
				direction = -1.0;

			if (gp == null) {
				gp = new GeneralPath();
			}
			gp.moveTo((float) (coords[0] + direction * pointSize),
					(float) coords[1]);
			gp.lineTo((float) (coords[0] - direction * pointSize / 2),
					(float) (coords[1] + pointSize * root3over2));
			gp.lineTo((float) (coords[0] - direction * pointSize / 2),
					(float) (coords[1] - pointSize * root3over2));
			gp.lineTo((float) (coords[0] + direction * pointSize),
					(float) coords[1]);
			gp.closePath();

			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;

		case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = new Line2D.Double();
				line2 = new Line2D.Double();
			}
			if (line3 == null) {
				line3 = new Line2D.Double();
				line4 = new Line2D.Double();
			}
			line1.setLine((xUL + xR) / 2, yUL, xUL, (yB + yUL) / 2);
			line2.setLine(xUL, (yB + yUL) / 2, (xUL + xR) / 2, yB);
			line3.setLine((xUL + xR) / 2, yB, xR, (yB + yUL) / 2);
			line4.setLine(xR, (yB + yUL) / 2, (xUL + xR) / 2, yUL);

			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;

		case EuclidianStyleConstants.POINT_STYLE_PLUS:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = new Line2D.Double();
				line2 = new Line2D.Double();
			}
			line1.setLine((xUL + xR) / 2, yUL, (xUL + xR) / 2, yB);
			line2.setLine(xUL, (yB + yUL) / 2, xR, (yB + yUL) / 2);

			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CROSS:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = new Line2D.Double();
				line2 = new Line2D.Double();
			}
			line1.setLine(xUL, yUL, xR, yB);
			line2.setLine(xUL, yB, xR, yUL);

			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			if (crossStrokes[pointSize] == null)
				crossStrokes[pointSize] = new GBasicStrokeW(pointSize / 2f);
			break;
		}
		// for circle points
		circle.setFrame(xUL, yUL, diameter, diameter);
	}

	public Canvas getCanvas() {
	    return c;
    }

}
