package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class CursorBox extends Box {

	public static double startX, startY;
	public static boolean blink = true;
	private Box content;

	public CursorBox(Box content, float coeff, Color color) {
		super(color, null);
		this.content = content;
		this.children.add(content);
		this.width = 0;
		this.height = content.height * coeff;
		this.depth = content.depth * coeff;
	}
	@Override
	public void draw(Graphics2DInterface g2, float x, float y) {
		if (CursorBox.blink) {
			Color old = g2.getColor();
			g2.setColor(foreground);
			content.draw(g2, (float) (x - content.width * 0.5), y);
			g2.setColor(old);
		}

		CursorBox.startX = g2.getTransform().getScaleX() * x
				+ g2.getTransform().getShearX() * y
				+ g2.getTransform().getTranslateX();
		CursorBox.startY = g2.getTransform().getScaleY() * y
				+ g2.getTransform().getShearY() * x
				+ g2.getTransform().getTranslateY();
	}

	@Override
	public int getLastFontId() {
		return content.getLastFontId();
	}
}
