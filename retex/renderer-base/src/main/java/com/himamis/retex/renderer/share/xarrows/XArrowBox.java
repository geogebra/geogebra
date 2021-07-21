package com.himamis.retex.renderer.share.xarrows;

import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.FontInfo;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public abstract class XArrowBox extends Box {

	protected String commands;
	protected double[] data;

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		draw(g2, x, y, commands, data);
	}

	protected void draw(Graphics2DInterface g2, double x, double y,
			String commands, double[] data) {
		startDraw(g2, x, y);
		g2.translate(x, y);

		g2.startDrawing();
		int j = 0;
		for (char c : commands.toCharArray()) {
			switch (c) {
			case 'M':
				g2.moveTo(data[j], data[j + 1]);
				j += 2;
				break;
			case 'L':
				g2.lineTo(data[j], data[j + 1]);
				j += 2;
				break;
			case 'Q':
				g2.quadraticCurveTo(data[j + 2], data[j + 3], data[j],
						data[j + 1]);
				j += 4;
				break;
			case 'C':
				g2.bezierCurveTo(data[j], data[j + 1], data[j + 2], data[j + 3],
						data[j + 4], data[j + 5]);
				j += 6;
				break;
			}
		}
		g2.finishDrawing();

		g2.translate(-x, -y);
		endDraw(g2);
	}

	@Override
	public FontInfo getLastFont() {
		return null;
	}
}
