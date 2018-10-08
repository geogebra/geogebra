package com.himamis.retex.renderer.share;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;

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

		SelectionBox.startX = g2.getTransform().getScaleX() * x
				+ g2.getTransform().getShearX() * y
				+ g2.getTransform().getTranslateX();
		SelectionBox.startY = g2.getTransform().getScaleY() * y
				+ g2.getTransform().getShearY() * x
				+ g2.getTransform().getTranslateY();

		SelectionBox.endX = g2.getTransform().getScaleX() * (x + content.width)
				+ g2.getTransform().getShearX() * (y + content.depth)
				+ g2.getTransform().getTranslateX();
		SelectionBox.endY = g2.getTransform().getScaleY() * (y + content.depth)
				+ g2.getTransform().getShearY() * (x + content.width)
				+ g2.getTransform().getTranslateY();
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

	@Override
	public void getPath(double x, double y, ArrayList<Integer> list) {
		super.getPath(x, y, list);

	}
}
