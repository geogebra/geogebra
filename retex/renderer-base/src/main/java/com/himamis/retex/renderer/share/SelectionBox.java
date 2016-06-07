package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;

public class SelectionBox extends Box {
	private Box content;

	public SelectionBox(Box content) {
		this.content = content;
		this.children.add(content);
		this.width = content.width;
		this.height = content.height;
		this.depth = content.depth;
	}
	@Override
	public void draw(Graphics2DInterface g2, float x, float y) {
		content.draw(g2, x, y);
		Stroke old = g2.getStroke();

		g2.setStroke(FactoryProvider.INSTANCE.getGraphicsFactory()
				.createBasicStroke((float) (1 / 10.0), 0, 0, 1));
		g2.draw(FactoryProvider.INSTANCE.getGeomFactory().createLine2D(x, y, x,
				y + content.height));
		g2.draw(FactoryProvider.INSTANCE.getGeomFactory().createLine2D(
				x + content.width, y, x + content.width, y + content.height));
		g2.setStroke(old);

	}

	@Override
	public int getLastFontId() {
		// TODO Auto-generated method stub
		return content.getLastFontId();
	}

}
