package org.geogebra.desktop.gui.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.factories.AwtFactoryD;

/**
 * adapted from PointStyleListRenderer
 * 
 */

@SuppressWarnings("rawtypes")
public class AxesStyleListRenderer extends JPanel implements ListCellRenderer<Integer> {
	private static final long serialVersionUID = 1L;
	/**
	 * Number of values
	 */
	public static final int MAX_ROW_COUNT = 5;
	private int style = -1;

	private static final int IMG_WIDTH = 32;
	private static final int IMG_HEIGHT = 24;
	private int arrowSize = 5;
	private int filledArrowLength = 10;

	// for drawing
	private Line2D.Double tempLine = new Line2D.Double();
	private GeneralPath gp = new GeneralPath();
	private static BasicStroke borderStroke = AwtFactoryD.getDefaultStrokeAwt();

	/**
	 * Axis arrows renderer
	 */
	public AxesStyleListRenderer() {
		setOpaque(true);
		setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Integer value,
			int index, boolean isSelected, boolean cellHasFocus) {

		// get the selected point style
		style = value == null ? EuclidianStyleConstants.AXES_LINE_TYPE_FULL
				: value;

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

		g2.setPaint(Color.BLACK);

		g2.setStroke(borderStroke);

		// line (represents the axis)
		tempLine.setLine(0, IMG_HEIGHT / 2.0, IMG_WIDTH, IMG_HEIGHT / 2.0);
		g2.draw(tempLine);

		switch (style) {
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS:

			leftArrow(g2);
			rightArrow(g2);
			break;
		default:
		case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW:
			rightArrow(g2);
			break;

		case EuclidianStyleConstants.AXES_LINE_TYPE_FULL:
			// just a line
			// do nothing
			break;

		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED:
			filledLeftArrow(g2);
			filledRightArrow(g2);
			break;
		case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED:

			filledRightArrow(g2);

		}
	}

	private void filledLeftArrow(Graphics2D g2) {

		gp.reset();
		gp.moveTo(0, IMG_HEIGHT / 2.0);
		gp.lineTo(0 + filledArrowLength, IMG_HEIGHT / 2.0 + arrowSize);
		gp.lineTo(0 + filledArrowLength, IMG_HEIGHT / 2.0 - arrowSize);

		g2.fill(gp);
		
	}

	private void filledRightArrow(Graphics2D g2) {

		gp.reset();
		gp.moveTo(IMG_WIDTH, IMG_HEIGHT / 2.0);
		gp.lineTo(IMG_WIDTH - filledArrowLength, IMG_HEIGHT / 2.0 + arrowSize);
		gp.lineTo(IMG_WIDTH - filledArrowLength, IMG_HEIGHT / 2.0 - arrowSize);

		g2.fill(gp);

	}

	private void rightArrow(Graphics2D g2) {

		tempLine.setLine(IMG_WIDTH, IMG_HEIGHT / 2.0, IMG_WIDTH - arrowSize,
				IMG_HEIGHT / 2.0 + arrowSize);
		g2.draw(tempLine);

		tempLine.setLine(IMG_WIDTH, IMG_HEIGHT / 2.0, IMG_WIDTH - arrowSize,
				IMG_HEIGHT / 2.0 - arrowSize);
		g2.draw(tempLine);

	}

	private void leftArrow(Graphics2D g2) {

		tempLine.setLine(0, IMG_HEIGHT / 2.0, 0 + arrowSize,
				IMG_HEIGHT / 2.0 + arrowSize);
		g2.draw(tempLine);

		tempLine.setLine(0, IMG_HEIGHT / 2.0, 0 + arrowSize,
				IMG_HEIGHT / 2.0 - arrowSize);
		g2.draw(tempLine);
		
	}

}
