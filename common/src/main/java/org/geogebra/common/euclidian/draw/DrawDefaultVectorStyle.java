package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.clipping.ClipLine;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.MyMath;

public class DrawDefaultVectorStyle implements DrawVectorStyle {

	private GLine2D line;
	private double[] coordsA;
	private double[] coordsB;
	private double[] coordsV;
	private GGeneralPath gpArrow; // for arrow

	private final DrawVector drawVector;
	private final EuclidianView view;
	private final VectorVisibility visibility;
	private final GPoint2D[] tmpClipPoints = {new GPoint2D(), new GPoint2D()};
	private GArea area;

	public DrawDefaultVectorStyle(DrawVector drawVector, EuclidianView view) {
		this.drawVector = drawVector;
		this.view = view;
		this.visibility = drawVector;
	}

	public void update(double[] coordsA, double[] coordsB, double[] coordsV, double lineThickness) {
// screen coords of start and end point of vector
		this.coordsA = coordsA;
		this.coordsB = coordsB;
		this.coordsV = coordsV;
		final boolean onscreenA = view.toScreenCoords(this.coordsA);
		final boolean onscreenB = view.toScreenCoords(this.coordsB);
		normalizeVector();

		// calculate endpoint F at base of arrow

		double factor = DrawVector.getFactor(lineThickness);

		double length = MyMath.length(this.coordsV[0], this.coordsV[1]);

		// decrease arrowhead size if it's longer than the vector
		if (length < factor) {
			factor = length;
		}

		if (length > 0.0) {
			this.coordsV[0] = (this.coordsV[0] * factor) / length;
			this.coordsV[1] = (this.coordsV[1] * factor) / length;
		}
		double[] coordsF = new double[2];
		coordsF[0] = this.coordsB[0] - this.coordsV[0];
		coordsF[1] = this.coordsB[1] - this.coordsV[1];

		// set clipped line
		if (line == null) {
			line = AwtFactory.getPrototype().newLine2D();
		}
		visibility.setLineVisible(true);

		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(this.coordsA[0], this.coordsA[1], coordsF[0], coordsF[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			// check if any of vector is on-screen
			GPoint2D[] clippedPoints = ClipLine.getClipped(this.coordsA[0],
					this.coordsA[1], this.coordsB[0], this.coordsB[1],
					view.getMinXScreen() - EuclidianStatic.CLIP_DISTANCE,
					view.getMaxXScreen() + EuclidianStatic.CLIP_DISTANCE,
					view.getMinYScreen() - EuclidianStatic.CLIP_DISTANCE,
					view.getMaxYScreen() + EuclidianStatic.CLIP_DISTANCE,
					tmpClipPoints);
			if (clippedPoints == null) {
				visibility.setVisible(false);
			} else {

				// now re-clip at A and F
				clippedPoints = ClipLine.getClipped(this.coordsA[0], this.coordsA[1],
						coordsF[0], coordsF[1], -EuclidianStatic.CLIP_DISTANCE,
						view.getWidth() + EuclidianStatic.CLIP_DISTANCE,
						-EuclidianStatic.CLIP_DISTANCE,
						view.getHeight() + EuclidianStatic.CLIP_DISTANCE,
						tmpClipPoints);
				if (clippedPoints != null) {
					line.setLine(clippedPoints[0].getX(),
							clippedPoints[0].getY(), clippedPoints[1].getX(),
							clippedPoints[1].getY());
				} else {
					visibility.setHeadVisible(false);
				}
			}
		}

		// add triangle if visible
		if (gpArrow == null) {
			gpArrow = AwtFactory.getPrototype().newGeneralPath();
		} else {
			gpArrow.reset();
		}
		if (visibility.isVisible()) {
			area = AwtFactory.getPrototype().newArea();
			GShape strokedLine = drawVector.getObjStroke().createStrokedShape(line, 255);
			area.add(GCompositeShape.toArea(strokedLine));

			if (length > 0) {
				drawHead(coordsF);
				area.add(GCompositeShape.toArea(gpArrow));
			}

			visibility.setHeadVisible(onscreenB || view.intersects(gpArrow));
		}
	}

	private void normalizeVector() {
		coordsV[0] = coordsB[0] - coordsA[0];
		coordsV[1] = coordsB[1] - coordsA[1];
	}
	private void drawHead(double[] coordsF) {
		coordsV[0] /= 4.0;
		coordsV[1] /= 4.0;

		gpArrow.moveTo(coordsB[0], coordsB[1]); // end point
		gpArrow.lineTo(coordsF[0] - coordsV[1], coordsF[1] + coordsV[0]);
		gpArrow.lineTo(coordsF[0] + coordsV[1], coordsF[1] - coordsV[0]);
		gpArrow.closePath();
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
