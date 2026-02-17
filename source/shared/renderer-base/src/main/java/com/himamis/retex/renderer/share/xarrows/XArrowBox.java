package com.himamis.retex.renderer.share.xarrows;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GGeneralPath;

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
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		int j = 0;
		for (char c : commands.toCharArray()) {
			switch (c) {
			case 'M':
				gp.moveTo(data[j], data[j + 1]);
				j += 2;
				break;
			case 'L':
				gp.lineTo(data[j], data[j + 1]);
				j += 2;
				break;
			case 'Q':
				gp.quadTo(data[j + 2], data[j + 3], data[j],
						data[j + 1]);
				j += 4;
				break;
			case 'C':
				gp.curveTo(data[j], data[j + 1], data[j + 2], data[j + 3],
						data[j + 4], data[j + 5]);
				j += 6;
				break;
			}
		}
		g2.fill(gp);

		g2.translate(-x, -y);
		endDraw(g2);
	}

	@Override
	public FontInfo getLastFont() {
		return null;
	}
}
