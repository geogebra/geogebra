package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.matrix.CoordSys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DrawPenStroke extends Drawable {

	private static final int BITMAP_PADDING = 10;

	private GeoLocusStroke stroke;

	private GeneralPathClippedForCurvePlotter gp;
	private GeneralPathClippedForCurvePlotter gpMask;

	private GRectangle partialHitClip;

	private GBufferedImage bitmap;
	private int bitmapShiftX;
	private int bitmapShiftY;

	GArea maskArea;

	public DrawPenStroke(EuclidianView view, GeoLocusStroke stroke) {
		super(view, stroke);
		this.stroke = stroke;
	}

	@Override
	public void update() {
		bitmap = null;
		buildGeneralPath();
		cleanupStroke();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (geo.isPenStroke() && !geo.getKernel().getApplication().isExporting()) {
			if (bitmap == null) {
				this.bitmap = makeImage(g2);
				GGraphics2D g2bmp = bitmap.createGraphics();
				g2bmp.setAntialiasing();
				bitmapShiftX = (int) getBounds().getMinX() - BITMAP_PADDING;
				bitmapShiftY = (int) getBounds().getMinY() - BITMAP_PADDING;
				g2bmp.translate(-bitmapShiftX, -bitmapShiftY);
				drawPath(g2bmp);
			}
			g2.drawImage(bitmap, bitmapShiftX, bitmapShiftY);
		} else {
			drawPath(g2);
		}
	}

	private void buildGeneralPath() {
		updateStrokes(stroke);
		if (gp == null) {
			gp = new GeneralPathClippedForCurvePlotter(view);
			gpMask = new GeneralPathClippedForCurvePlotter(view);
		} else {
			gp.reset();
		}

		CurvePlotter.draw(gp, stroke.getPoints(), CoordSys.XOY);
		setShape(AwtFactory.getPrototype().newArea(objStroke
				.createStrokedShape(gp, 2000)));

		maskArea = AwtFactory.getPrototype().newArea();

		for (Map.Entry<Double, ArrayList<MyPoint>> entry : stroke.mask.entrySet()) {
			gpMask.reset();
			CurvePlotter.draw(gpMask, entry.getValue(), CoordSys.XOY);
			GBasicStroke stroke = AwtFactory.getPrototype().newBasicStroke(entry.getKey() * view.getXscale(),
					GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND);
			GArea area = AwtFactory.getPrototype().newArea(stroke.createStrokedShape(gpMask, 2000));
			maskArea.add(area);
		}

		getShape().subtract(maskArea);
	}

	private void drawPath(GGraphics2D g2) {
		if (getShape() == null) {
			buildGeneralPath();
		}

		g2.setPaint(getObjectColor());
		g2.fill(getShape());
		g2.setPaint(GColor.CYAN);
		g2.fill(maskArea);
	}

	private GBufferedImage makeImage(GGraphics2D g2p) {
		return AwtFactory.getPrototype().newBufferedImage(
				(int) this.getBounds().getWidth() + 2 * BITMAP_PADDING,
				(int) this.getBounds().getHeight() + 2 * BITMAP_PADDING, g2p);
	}

	public void cleanupStroke() {
		switch (stroke.getSimplificationState()) {
		case 0:
			collapseMasks();
			break;
		case 1:
			removeInvisibleSegments();
			break;
		case 2:
			removeNonCoveringMasks();
			break;
		}

		stroke.increaseSimplificationState();
	}

	private void collapseMasks() {
		// first step: collapse masks that have a very similar width
		Map.Entry<Double, ArrayList<MyPoint>> previous = null;
		Iterator<Map.Entry<Double, ArrayList<MyPoint>>> it = stroke.mask.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, ArrayList<MyPoint>> current = it.next();
			if (previous != null) {
				if (current.getKey() - previous.getKey() < 1) {
					previous.getValue().addAll(current.getValue());
					it.remove();
					continue;
				}
			}
			previous = current;
		}
	}

	private void removeInvisibleSegments() {
		// second step: remove segments that are at least two steps from any visible point
		GeneralPathClippedForCurvePlotter piecePlotter = new GeneralPathClippedForCurvePlotter(view);
		List<Boolean> visible = new ArrayList<>();
		List<MyPoint> points = stroke.getPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			if (!points.get(i).isDefined() || !points.get(i + 1).isDefined()) {
				visible.add(false);
				continue;
			}

			piecePlotter.reset();
			piecePlotter.moveTo(new double[] {points.get(i).x, points.get(i).y});
			if (points.get(i + 1).getSegmentType() == SegmentType.CONTROL) {
				MyPoint p1 = points.get(i + 1);
				MyPoint p2 = points.get(i + 2);
				MyPoint p3 = points.get(i + 3);
				piecePlotter.drawTo(new double[] {p1.x, p1.y}, SegmentType.CONTROL);
				piecePlotter.drawTo(new double[] {p2.x, p2.y}, SegmentType.CONTROL);
				piecePlotter.drawTo(new double[] {p3.x, p3.y}, SegmentType.CURVE_TO);
				i += 2;
			} else {
				piecePlotter.lineTo(new double[] {points.get(i + 1).x, points.get(i + 1).y});
			}

			GArea pieceShape = AwtFactory.getPrototype().newArea(objStroke
					.createStrokedShape(piecePlotter, 10));
			pieceShape.add(maskArea);
			visible.add(!(pieceShape).equals(maskArea));
		}

		int pointIndex = 0;
		List<MyPoint> newPoints = new ArrayList<>();
		for (int i = 0; i < visible.size(); i++) {
			if (!points.get(pointIndex).isDefined()) {
				ensureTrailingNaN(newPoints);
				pointIndex++;
				continue;
			}

			if ((i < 3 || !visible.get(i - 2) && !visible.get(i - 1))
					&& !visible.get(i)
					&& (i > visible.size() - 3 || !visible.get(i + 1) && !visible.get(i + 2))) {
				if (points.get(pointIndex + 1).getSegmentType() == SegmentType.CONTROL) {
					pointIndex += 3;
				} else {
					pointIndex++;
				}
				ensureTrailingNaN(newPoints);
			} else {
				if (points.get(pointIndex + 1).getSegmentType() == SegmentType.CONTROL) {
					addPoint(newPoints, points.get(pointIndex));
					newPoints.add(points.get(pointIndex + 1));
					newPoints.add(points.get(pointIndex + 2));
					newPoints.add(points.get(pointIndex + 3));
					pointIndex += 3;
				} else {
					addPoint(newPoints, points.get(pointIndex));
					newPoints.add(points.get(pointIndex + 1));
					pointIndex++;
				}
			}
		}

		points.clear();
		points.addAll(newPoints);
		stroke.resetXMLPointBuilder();
	}

	private void removeNonCoveringMasks() {
		GArea strokeArea = AwtFactory.getPrototype().newArea(objStroke.createStrokedShape(gp, 2000));
		GeneralPathClippedForCurvePlotter maskPiece = new GeneralPathClippedForCurvePlotter(view);
		for (Map.Entry<Double, ArrayList<MyPoint>> entry : stroke.mask.entrySet()) {
			GBasicStroke stroke = AwtFactory.getPrototype().newBasicStroke(
					entry.getKey() * view.getXscale(),
					GBasicStroke.CAP_ROUND, GBasicStroke.JOIN_ROUND);

			ArrayList<MyPoint> current = entry.getValue();
			ArrayList<MyPoint> result = new ArrayList<>();
			for (int i = 0; i < current.size() - 1; i++) {
				if (!current.get(i).isDefined() || !current.get(i + 1).isDefined()) {
					ensureTrailingNaN(result);
					continue;
				}

				maskPiece.reset();
				maskPiece.moveTo(new double[] {current.get(i).x, current.get(i).y});
				maskPiece.lineTo(new double[] {current.get(i + 1).x, current.get(i + 1).y});

				GArea pieceShape = AwtFactory.getPrototype().newArea(stroke
						.createStrokedShape(maskPiece, 10));
				pieceShape.intersect(strokeArea);

				if (pieceShape.isEmpty()) {
					ensureTrailingNaN(result);
				} else {
					addPoint(result, current.get(i));
					result.add(current.get(i + 1));
				}
			}

			current.clear();
			current.addAll(result);
		}
	}

	private void ensureTrailingNaN(List<MyPoint> points) {
		if (points.size() > 0 && points.get(points.size() - 1).isDefined()) {
			points.add(new MyPoint(Double.NaN, Double.NaN));
		}
	}

	private void addPoint(List<MyPoint> points, MyPoint newPoint) {
		if (points.size() > 0 && points.get(points.size() - 1).isDefined()) {
			if (!points.get(points.size() - 1).equals(newPoint)) {
				points.add(newPoint);
			}
		} else {
			points.add(newPoint.withType(SegmentType.MOVE_TO));
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return getShape() != null &&
				getShape().intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public GeoLocusStroke getGeoElement() {
		return stroke;
	}

	@Override
	public GRectangle getBounds() {
		if (getShape() == null) {
			buildGeneralPath();
		}

		return getShape().getBounds();
	}

	@Override
	public GRectangle getBoundsClipped() {
		if (this.partialHitClip != null) {
			return getBounds().createIntersection(partialHitClip).getBounds();
		}
		return getBounds();
	}

	@Override
	public GRectangle getPartialHitClip() {
		return partialHitClip;
	}

	@Override
	public GRectangle getBoundsForStylebarPosition() {
		return getBoundsClipped();
	}

	@Override
	public void setPartialHitClip(GRectangle rect) {
		this.partialHitClip = rect;
	}

	@Override
	public boolean resetPartialHitClip(int x, int y) {
		if (partialHitClip != null && !partialHitClip.contains(x, y)) {
			partialHitClip = null;
			return geo.isSelected();
		}
		return false;
	}

	@Override
	public ArrayList<GPoint2D> toPoints() {
		ArrayList<GPoint2D> points = new ArrayList<>();
		for (MyPoint pt : stroke.getTransformPoints()) {
			points.add(
					new MyPoint(view.toScreenCoordXd(pt.getX()), view.toScreenCoordYd(pt.getY())));
		}
		return points;
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		int i = 0;
		for (MyPoint pt : stroke.getTransformPoints()) {
			pt.setLocation(view.toRealWorldCoordX(points.get(i).getX()),
					view.toRealWorldCoordY(points.get(i).getY()));
			i++;
		}
		stroke.resetXMLPointBuilder();
	}
}
