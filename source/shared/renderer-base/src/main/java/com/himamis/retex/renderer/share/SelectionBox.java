package com.himamis.retex.renderer.share;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GLine2D;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class SelectionBox extends Box {
	private static final int DIAMETER = 10;
	public static double startX, startY;
	public static double endX, endY;
	private Box content;
	private static boolean touchSelection = true;

	public SelectionBox(Box content) {
		this.content = content;
		// XXX
		// this.children.add(content);
		this.width = content.width;
		this.height = content.height;
		this.depth = content.depth;
	}

	public static boolean isTouchSelection() {
		return touchSelection;
	}

	public static void setTouchSelection(boolean touchSelection) {
		SelectionBox.touchSelection = touchSelection;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		content.draw(g2, x, y);
		GBasicStroke old = g2.getStroke();

		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(1, 0, 0, 1, null));

		GAffineTransform transform = g2.getTransform();
		SelectionBox.startX = transform.getScaleX() * x
				+ transform.getShearX() * y
				+ transform.getTranslateX();
		SelectionBox.startY = transform.getScaleY() * y
				+ transform.getShearY() * x
				+ transform.getTranslateY();

		SelectionBox.endX = transform.getScaleX() * (x + content.width)
				+ transform.getShearX() * (y + content.depth)
				+ transform.getTranslateX();
		SelectionBox.endY = transform.getScaleY() * (y + content.depth)
				+ transform.getShearY() * (x + content.width)
				+ transform.getTranslateY();
		if (isTouchSelection()) {
			g2.saveTransform();

			g2.scale(1.0 / DIAMETER, 1.0 / DIAMETER);
			GLine2D line = AwtFactory.getPrototype().newLine2D();
			line.setLine(
					DIAMETER * x, DIAMETER * y - DIAMETER * content.height,
					DIAMETER * x, DIAMETER * y + DIAMETER * content.depth);
			g2.draw(line);
			line.setLine(
					DIAMETER * (x + content.width),
					DIAMETER * y - DIAMETER * content.height,
					DIAMETER * (x + content.width),
					DIAMETER * (y + content.depth));
			g2.draw(line);
			g2.drawArc((int) (DIAMETER * x - 5),
					(int) (DIAMETER * y + DIAMETER * content.depth), DIAMETER,
					DIAMETER, 0, 360);
			g2.drawArc((int) (DIAMETER * x + DIAMETER * content.width - 5),
					(int) (DIAMETER * y + DIAMETER * content.depth), DIAMETER,
					DIAMETER, 0, 360);

			g2.restoreTransform();
		}
		g2.setStroke(old);

	}

	@Override
	public FontInfo getLastFont() {
		return content.getLastFont();
	}
}
