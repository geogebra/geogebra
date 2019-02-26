package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class CursorBox extends Box {

	public static double startX, startY;
	private static boolean blink = true;
	private Box content;

	public CursorBox(Box content, double coeff, Color color) {
		super(color, null);
		this.content = content;
		// XXX
		// this.children.add(content);
		this.width = 0;
		this.height = content.height * coeff;
		this.depth = 0;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		if (CursorBox.blink) {
			Color old = g2.getColor();
			g2.setColor(foreground);
			content.draw(g2, (x - content.width * 0.5), y);
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
	public FontInfo getLastFont() {
		return content.getLastFont();
	}

	/**
	 * Toggle temporary cursor visibility for blinking effect
	 */
	public static void toggleBlink() {
		blink = !blink;
	}

	/**
	 * Force cursor to be visible / invisible (gets switched periodically by
	 * toggleBlink in web/desktop)
	 * 
	 * @param blink
	 *            blink state
	 */
	public static void setBlink(boolean blink) {
		CursorBox.blink = blink;
	}
}
