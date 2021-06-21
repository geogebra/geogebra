package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

public class SelectionBox extends Box {
	private static final int DIAMETER = 10;
	public static double startX, startY;
	public static double endX, endY;
	private Box content;
	public static boolean touchSelection = true;

	public SelectionBox(Box content) {
		this.content = content;
		// XXX
		// this.children.add(content);
		this.width = content.width;
		this.height = content.height;
		this.depth = content.depth;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		content.draw(g2, x, y);
		Stroke old = g2.getStroke();

		g2.setStroke(FactoryProvider.getInstance().getGraphicsFactory()
				.createBasicStroke(1, 0, 0, 1));

		Transform transform = g2.getTransform();
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
		if (touchSelection) {
			g2.saveTransformation();

			g2.scale(1.0 / DIAMETER, 1.0 / DIAMETER);
			g2.draw(FactoryProvider.getInstance().getGeomFactory().createLine2D(
					DIAMETER * x, DIAMETER * y - DIAMETER * content.height,
					DIAMETER * x, DIAMETER * y + DIAMETER * content.depth));
			g2.draw(FactoryProvider.getInstance().getGeomFactory().createLine2D(
					DIAMETER * (x + content.width),
					DIAMETER * y - DIAMETER * content.height,
					DIAMETER * (x + content.width),
					DIAMETER * (y + content.depth)));
			g2.drawArc((int) (DIAMETER * x - 5),
					(int) (DIAMETER * y + DIAMETER * content.depth), DIAMETER,
					DIAMETER, 0, 360);
			g2.drawArc((int) (DIAMETER * x + DIAMETER * content.width - 5),
					(int) (DIAMETER * y + DIAMETER * content.depth), DIAMETER,
					DIAMETER, 0, 360);

			g2.restoreTransformation();
		}
		g2.setStroke(old);

	}

	@Override
	public FontInfo getLastFont() {
		return content.getLastFont();
	}
}
