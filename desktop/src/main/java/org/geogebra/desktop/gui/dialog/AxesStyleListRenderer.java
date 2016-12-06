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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * adapted from PointStyleListRenderer
 * 
 */

public class AxesStyleListRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	public static final int MAX_ROW_COUNT = 5;
	private int style = -1;

	private static int WIDTH = 32;
	private static int HEIGHT = 24;
	private int arrowSize = 5;
	private int filledArrowLength = 10;

	// for drawing
	private int pointSize = 4;
	private Line2D.Double tempLine = new Line2D.Double();
	private GeneralPath gp = new GeneralPath();
	private static BasicStroke borderStroke = AwtFactoryD.getDefaultStrokeAwt();

	public AxesStyleListRenderer() {
		setOpaque(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		// get the selected point style
		style = value == null ? EuclidianStyleConstants.AXES_LINE_TYPE_FULL
				: ((Integer) value).intValue();

		if (isSelected) {
			setBackground(Color.LIGHT_GRAY);
		} else {
			setBackground(Color.WHITE);
		}
		return this;
	}

	@Override
	@SuppressFBWarnings({ "SF_SWITCH_FALLTHROUGH",
			"missing break is deliberate" })
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
		switch (style) {
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS:

			// left arrow
			tempLine.setLine(0, HEIGHT / 2.0, WIDTH, HEIGHT / 2.0);
			g2.draw(tempLine);

			tempLine.setLine(0, HEIGHT / 2.0, 0 + arrowSize,
					HEIGHT / 2.0 + arrowSize);
			g2.draw(tempLine);

			tempLine.setLine(0, HEIGHT / 2.0, 0 + arrowSize,
					HEIGHT / 2.0 - arrowSize);
			g2.draw(tempLine);

			// fall through
		case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW:
		default:

			// right-arrow

			tempLine.setLine(WIDTH, HEIGHT / 2.0, WIDTH - arrowSize,
					HEIGHT / 2.0 + arrowSize);
			g2.draw(tempLine);

			tempLine.setLine(WIDTH, HEIGHT / 2.0, WIDTH - arrowSize,
					HEIGHT / 2.0 - arrowSize);
			g2.draw(tempLine);
			// fall through
		case EuclidianStyleConstants.AXES_LINE_TYPE_FULL:
			// just a line
			tempLine.setLine(0, HEIGHT / 2.0, WIDTH, HEIGHT / 2.0);
			g2.draw(tempLine);
			break;
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED:

			// left arrow (filled)
			tempLine.setLine(0, HEIGHT / 2.0, WIDTH, HEIGHT / 2.0);
			g2.draw(tempLine);

			gp.reset();
			gp.moveTo(0, HEIGHT / 2.0);
			gp.lineTo(0 + filledArrowLength, HEIGHT / 2.0 + arrowSize);
			gp.lineTo(0 + filledArrowLength, HEIGHT / 2.0 - arrowSize);

			g2.fill(gp);

			// fall through
		case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED:

			// right-arrow (filled)
			tempLine.setLine(0, HEIGHT / 2.0, WIDTH, HEIGHT / 2.0);
			g2.draw(tempLine);

			gp.reset();
			gp.moveTo(WIDTH, HEIGHT / 2.0);
			gp.lineTo(WIDTH - filledArrowLength, HEIGHT / 2.0 + arrowSize);
			gp.lineTo(WIDTH - filledArrowLength, HEIGHT / 2.0 - arrowSize);

			g2.fill(gp);

		}
	}

}
