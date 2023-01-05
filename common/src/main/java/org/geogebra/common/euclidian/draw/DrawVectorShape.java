package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.clipping.ClipLine;
import org.geogebra.common.factories.AwtFactory;

public class DrawVectorShape implements DrawVectorStyle {

	private GLine2D line;
	private final GGeneralPath arrow;

	private final EuclidianView view;
	private final VectorVisibility visibility;
	private final GPoint2D[] tmpClipPoints = {new GPoint2D(), new GPoint2D()};
	private GArea area;
	private boolean headVisible = true;
	private DrawVectorProperties properties;

	public DrawVectorShape(VectorVisibility visibility, EuclidianView view) {
		this.view = view;
		this.visibility = visibility;
		arrow = AwtFactory.getPrototype().newGeneralPath();
	}

	public void update(VectorShape vectorShape) {
		this.properties = vectorShape.properties();
		final boolean onscreenA = view.toScreenCoords(properties.getStartCoords());
		final boolean onscreenB = view.toScreenCoords(properties.getEndCoords());
		properties.normalize();

		// calculate endpoint F at base of arrow

		double factor = DrawVector.getFactor(properties.getLineThickness());

		double length = properties.length();

		// decrease arrowhead size if it's longer than the vector
		if (length < factor) {
			factor = length;
		}

		if (length > 0.0) {
			properties.scaleNormalVector(factor);
		}

		if (onscreenA && onscreenB) {
			line = vectorShape.body();
		} else {
			line = vectorShape.clippedBody();
		}

		// add triangle if visible
		if (visibility.isVisible()) {
			createVectorShape(properties.getStroke(), length, vectorShape);
			headVisible = onscreenB || view.intersects(arrow);
		}
	}

	private void clipVector(DrawVectorProperties properties, double arrowBaseX, double arrowBaseY) {
		// A or B off screen
		// clip at screen, that's important for huge coordinates
		// check if any of vector is on-screen
		GPoint2D[] clippedPoints = ClipLine.getClipped(properties.getStartX(),
				properties.getStartY(), properties.getEndX(), properties.getEndY(),
				view.getMinXScreen() - EuclidianStatic.CLIP_DISTANCE,
				view.getMaxXScreen() + EuclidianStatic.CLIP_DISTANCE,
				view.getMinYScreen() - EuclidianStatic.CLIP_DISTANCE,
				view.getMaxYScreen() + EuclidianStatic.CLIP_DISTANCE,
				tmpClipPoints);
		if (clippedPoints == null) {
			visibility.setVisible(false);
		} else {

			// n ow re-clip at A and F
			clippedPoints = ClipLine.getClipped(properties.getStartX(),
					properties.getStartY(),
					arrowBaseX, arrowBaseY, -EuclidianStatic.CLIP_DISTANCE,
					view.getWidth() + EuclidianStatic.CLIP_DISTANCE,
					-EuclidianStatic.CLIP_DISTANCE,
					view.getHeight() + EuclidianStatic.CLIP_DISTANCE,
					tmpClipPoints);
			if (clippedPoints != null) {
				line.setLine(clippedPoints[0].getX(),
						clippedPoints[0].getY(), clippedPoints[1].getX(),
						clippedPoints[1].getY());
			} else {
				headVisible = false;
			}
		}
	}

	private void createVectorShape(GBasicStroke stroke, double length, VectorShape vectorShape) {
		area = AwtFactory.getPrototype().newArea();
		GShape strokedLine = stroke.createStrokedShape(line, 255);
		area.add(GCompositeShape.toArea(strokedLine));

		if (length > 0 && headVisible) {
			area.add(GCompositeShape.toArea(vectorShape.head()));
		}
	}


	@Override
	public void draw(GGraphics2D g2) {
		if (visibility.isVisible() && area != null) {
			g2.fill(area);
		}
	}

	@Override
	public GShape getShape() {
		return area;
	}
}
