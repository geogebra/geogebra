package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class TableBox extends Box {

	private Box content;

	public TableBox(Box content) {
		this.content = content;
		this.children.add(content);
		this.width = content.width;
		this.height = content.height;
		this.depth = content.depth;
	}
	@Override
	public void draw(Graphics2DInterface g2, float x, float y) {
		content.draw(g2, x, y);

	}

	@Override
	public int getLastFontId() {
		return content.getLastFontId();
	}

}
