package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;

public class DrawStyledVector {

	private GLine2D line;
	private final EuclidianView view;
	private final DrawableVisibility visibility;
	private GArea area;

	DrawStyledVector(DrawableVisibility visibility, EuclidianView view) {
		this.view = view;
		this.visibility = visibility;
	}

	void update(VectorShape vectorShape) {
		DrawVectorModel model = vectorShape.model();
		model.isStartOnScreen(view);
		model.isEndOnScreen(view);
		model.update();
		line = vectorShape.body(); // do we need clipping?

		if (visibility.isVisible()) {
			createVectorShape(model.getStroke(), model.length(), vectorShape);
		}
	}

	private void createVectorShape(GBasicStroke stroke, double length, VectorShape vectorShape) {
		area = AwtFactory.getPrototype().newArea();
		GShape strokedLine = stroke.createStrokedShape(line, 255);
		area.add(GCompositeShape.toArea(strokedLine));

		if (length > 0) {
			area.add(GCompositeShape.toArea(vectorShape.head()));
		}
	}

	void draw(GGraphics2D g2) {
		if (visibility.isVisible() && area != null) {
			g2.fill(area);
		}
	}

	public GRectangle getBounds() {
		return area.getBounds();
	}

	public boolean intersects(int x1, int y1, int w, int h) {
		return area.intersects(x1, y1, w, h);
	}

	public void fill(GGraphics2D g2) {
		g2.fill(area);
	}
}
