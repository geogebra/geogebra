package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoStadium;

public final class AlgoStadium extends AlgoElement {
	private final GeoPoint p;
	private final GeoPoint q;
	private final GeoNumeric height;
	private final GeoStadium stadium;
	private final ArrayList<MyPoint> points = new ArrayList<>();

	/**
	 *
	 * @param c {@link Construction}
	 * @param p centre of the first semicircle
	 * @param q centre of the second semicircle
	 * @param height of the stadium (2x radius of a semicircle)
	 */
	public AlgoStadium(Construction c, GeoPoint p, GeoPoint q, GeoNumeric height) {
		super(c);
		this.p = p;
		this.q = q;
		this.height = height;
		stadium = new GeoStadium(cons, p, q, height);
		stadium.setDefined(true);
		compute();
		setInputOutput();
	}

	@Override
	public void compute() {
		updatePoints();
		stadium.setPoints(points);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{p, q, height};
		setOnlyOutput(stadium);
		setDependencies();
	}

	private void updatePoints() {
		int steps = 50;
		double angleStep = Math.PI / steps;
		double radius = height.getValue() / 2.0;
		points.clear();
		double dx = q.getX() - p.getX();
		double dy = q.getY() - p.getY();
		double baseAngle = Math.atan2(dy, dx);  // rotation angle of segment pq
		double pi2 = -Math.PI / 2;
		addAroundPoint(p, -radius, baseAngle + pi2, SegmentType.MOVE_TO);
		lineAroundPoint(q, radius, baseAngle - pi2);

		for (int i = steps; i > 0; i--) {
			double angle = pi2 + i * angleStep;
			lineAroundPoint(q, radius, baseAngle + angle);
		}

		lineAroundPoint(q, radius, baseAngle + pi2 + angleStep);
		for (int i = steps; i >= 0; i--) {
			double angle = pi2 + i * angleStep;
			lineAroundPoint(p, -radius, baseAngle + angle);

		}
		lineAroundPoint(p, -radius, baseAngle + pi2);
	}

	private void lineAroundPoint(GeoPoint p, double radius, double angle) {
		addAroundPoint(p, radius, angle, SegmentType.LINE_TO);
	}

	private void addAroundPoint(GeoPoint p, double radius, double angle, SegmentType segmentType) {
		double x = p.getX() + radius * Math.cos(angle);
		double y = p.getY() + radius * Math.sin(angle);
		addPoint(x, y, segmentType);

	}

	private void addPoint(double x, double y, SegmentType segmentType) {
		points.add(new MyPoint(x, y, segmentType));
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Stadium;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SHAPE_STADIUM;
	}

	/**
	 * Updates stadium parameters to compute with.
	 *
	 * @param p centre of the first semicircle
	 * @param q centre of the second semicircle
	 * @param height of the stadium (2x radius of a semicircle)
	 */
	public void update(GeoPoint p, GeoPoint q, GeoNumeric height) {
		this.p.set(p);
		this.q.set(q);
		this.height.set(height);
	}

	public ArrayList<MyPoint> getPoints() {
		return points;
	}

	@Override
	public boolean hasOnlyFreeInputPoints(EuclidianViewInterfaceSlim view) {
		return view.getFreeInputPoints(this).size() == 2;
	}
}
