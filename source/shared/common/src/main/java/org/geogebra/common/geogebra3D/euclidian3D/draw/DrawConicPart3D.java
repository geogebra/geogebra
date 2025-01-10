package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.matrix.Coords;

public class DrawConicPart3D extends DrawConic3D {
	private Coords tmpCoords1;
	private Coords tmpCoords2;
	private Coords tmpCoords3;

	public DrawConicPart3D(EuclidianView3D view3d, GeoConicPartND conic) {
		super(view3d, (GeoConicND) conic);
	}

	@Override
	protected double getEllipseSurfaceStart() {
		// return 0;
		return ((GeoConicPartND) getGeoElement()).getParameterStart();
	}

	@Override
	protected double getEllipseSurfaceExtent() {
		return ((GeoConicPartND) getGeoElement()).getParameterExtent();
	}

	@Override
	protected void updateCircle(PlotterBrush brush) {

		double start = getEllipseSurfaceStart();
		double extent = getEllipseSurfaceExtent();
		brush.arc(m, ev1, ev2, e1, start, extent, longitude);

		updateSectorSegments(brush, start, start + extent);
	}

	@Override
	protected void updateEllipse(PlotterBrush brush) {

		double start = getEllipseSurfaceStart();
		double extent = getEllipseSurfaceExtent();
		brush.arcEllipse(m, ev1, ev2, e1, e2, start, extent);

		updateSectorSegments(brush, start, start + extent);
	}

	private void createTmpCoordsIfNeeded() {
		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
			tmpCoords2 = new Coords(3);
			tmpCoords3 = new Coords(3);
		}
	}

	private void updateSectorSegments(PlotterBrush brush, double start,
			double end) {

		// if sector draws segments
		if (((GeoConicPartND) getGeoElement())
				.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
			brush.setAffineTexture(0.5f, 0.25f);
			createTmpCoordsIfNeeded();
			brush.segment(m, tmpCoords1.setAdd3(
					m, tmpCoords1.setAdd3(
							tmpCoords2.setMul3(ev1, e1 * Math.cos(start)),
							tmpCoords3.setMul3(ev2, e2 * Math.sin(start)))));
			brush.segment(m, tmpCoords1.setAdd3(
					m, tmpCoords1.setAdd3(
							tmpCoords2.setMul3(ev1, e1 * Math.cos(end)),
							tmpCoords3.setMul3(ev2, e2 * Math.sin(end)))));
		}
	}

	@Override
	protected void updateLines(PlotterBrush brush) {

		if (((GeoConicPartND) conic).positiveOrientation()) {
			brush.segment(conic.getOrigin3D(0),
					((GeoConicPartND) conic).getSegmentEnd3D());
		} else {
			m = conic.getOrigin3D(0);
			createTmpCoordsIfNeeded();
			d = tmpCoords2.setSub(((GeoConicPartND) conic).getSegmentEnd3D(), m);
			minmax = getLineMinMax(0); // get min/max with current (m,d)
			
			brush.segment(m, tmpCoords1.setAdd3(m, tmpCoords1.setMul3(d, minmax[0])));
			brush.segment(((GeoConicPartND) conic).getSegmentEnd3D(),
					tmpCoords1.setAdd3(m, tmpCoords1.setMul3(d, minmax[1])));
		}
	}

	@Override
	protected void updateParallelLines(PlotterSurface surface) {
		// no surface here
	}

	@Override
	protected void updateSinglePoint(PlotterSurface surface) {
		// not visible
		setGeometryIndex(-1);
	}

	@Override
	protected boolean isSector() {
		return ((GeoConicPartND) conic)
				.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR;
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			if (exportSurface) {
				exportToPrinter3D.exportSurface(this, true, true);
			} else if (getGeoElement().getLineThickness() > 0) {
				exportToPrinter3D.exportCurve(this, Type.CURVE);
			}
		}
	}

}
