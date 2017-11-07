package org.geogebra.desktop.gui.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.factories.AwtFactoryD;

/**
 * @author George Sturr 2009-9-19 This class defines the ComboBox renderer where
 *         the user chooses the point style for GeoPoint
 * 
 */

public class PointStyleListRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	private int pointStyle = -1;

	// for drawing
	private int pointSize = 4;
	private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Line2D.Double line1, line2, line3, line4;
	private GeneralPath gp = null;
	private static BasicStroke borderStroke = AwtFactoryD.getDefaultStrokeAwt();
	private static BasicStroke[] crossStrokes = new BasicStroke[10];

	public PointStyleListRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		// get the selected point style
		pointStyle = value == null ? EuclidianStyleConstants.POINT_STYLE_DOT
				: ((Integer) value).intValue();

		if (isSelected) {
			setBackground(Color.LIGHT_GRAY);
		} else {
			setBackground(Color.WHITE);
		}
		return this;
	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		GGraphics2DD.setAntialiasing(g2);

		// paint cell background
		if (getBackground() == Color.LIGHT_GRAY) {
			g2.setPaint(Color.LIGHT_GRAY);
		} else {
			g2.setPaint(Color.WHITE);
		}
		g2.fillRect(0, 0, getWidth(), getHeight());

		// draw point using routine from euclidian.DrawPoint
		g2.setPaint(Color.BLACK);
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
			g2.draw(gp);
			g2.fill(gp);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			// draw a circle
			g2.setStroke(crossStrokes[pointSize]);
			g2.draw(circle);
			break;

		case EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE:
			// filled circle
			g2.fill(circle);
			g2.setStroke(borderStroke);
			g2.draw(circle);
			break;

		default:
			// circle with gray middle
			g2.setPaint(Color.LIGHT_GRAY);
			g2.fill(circle);
			g2.setPaint(Color.BLACK);
			g2.setStroke(borderStroke);
			g2.draw(circle);
		}
	}

	public void getPath() {
		// clear old path
		if (gp != null) {
			gp.reset();
		}

		// set point size
		pointSize = 4;
		int diameter = 2 * pointSize;

		// set coords = center of cell
		double[] coords = new double[2];
		coords[0] = getWidth() / 2.0;
		coords[1] = getHeight() / 2.0;

		// get draw path using routine from euclidian.DrawPoint
		double xUL = coords[0] - pointSize;
		double yUL = coords[1] - pointSize;
		double root3over2 = Math.sqrt(3.0) / 2.0;

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_DOT:
		default:
			// do nothing
			break;
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

			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:

			double direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH) {
				direction = -1.0;
			}

			if (gp == null) {
				gp = new GeneralPath();
			}
			gp.moveTo((float) coords[0],
					(float) (coords[1] + direction * pointSize));
			gp.lineTo((float) (coords[0] + pointSize * root3over2),
					(float) (coords[1] - direction * pointSize / 2));
			gp.lineTo((float) (coords[0] - pointSize * root3over2),
					(float) (coords[1] - direction * pointSize / 2));
			gp.lineTo((float) coords[0],
					(float) (coords[1] + direction * pointSize));
			gp.closePath();

			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:

			direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST) {
				direction = -1.0;
			}

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

			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
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

			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
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

			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
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

			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
			break;

		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			if (crossStrokes[pointSize] == null) {
				crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
			}
			break;
		}
		// for circle points
		circle.setFrame(xUL, yUL, diameter, diameter);
	}

}
