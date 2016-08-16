package com.himamis.retex.renderer.share;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class TableBox extends Box {

	private Box content;
	private ArrayList<Rectangle2D> rectangles;
	private ArrayList<Color> colors;

	public TableBox(Box content, ArrayList<Rectangle2D> arr,
			ArrayList<Color> colors) {
		this.content = content;
		this.rectangles = arr;
		this.colors = colors;
		this.children.add(content);
		this.width = content.width;
		this.height = content.height;
		this.depth = content.depth;
	}
	@Override
	public void draw(Graphics2DInterface g2, float x, float y) {
		if (rectangles != null) {
			Color old = g2.getColor();

			for (int i = 0; i < rectangles.size(); i++) {
				if (colors.get(i) == null) {
					continue;
				}
				g2.setColor(colors.get(i));
				Rectangle2D rect = FactoryProvider.INSTANCE.getGeomFactory()
						.createRectangle2D(
								rectangles.get(i).getX() + x,
								rectangles.get(i).getY() + y - (height + depth)
										/ 2, rectangles.get(i).getWidth(),
								rectangles.get(i).getHeight());
				g2.fill(rect);
			}
			g2.setColor(old);
		}
		content.draw(g2, x, y);

	}

	@Override
	public int getLastFontId() {
		return content.getLastFontId();
	}

}
