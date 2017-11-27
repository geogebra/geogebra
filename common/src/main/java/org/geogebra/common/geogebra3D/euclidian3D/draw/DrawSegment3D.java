package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.LinkedList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.Feature;

/**
 * Class for drawing segments
 * 
 * @author matthieu
 *
 */
public class DrawSegment3D extends DrawCoordSys1D {
	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);
	private LinkedList<Integer> traces;

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param segment
	 *            segment
	 */
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegmentND segment) {

		super(a_view3D, (GeoElement) segment);

		setDrawMinMax(0, 1);
	}

	@Override
	public boolean doHighlighting() {

		if (!shouldBePacked()) {
			// if the segments depends on a polygon (or polyhedron), look at the
			// poly' highlighting
			GeoElement meta = ((GeoSegmentND) getGeoElement()).getMetas()[0];
			if (meta != null && meta.doHighlighting()) {
				return true;
			}
		}

		return super.doHighlighting();
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()) {
			updateForItSelf();
		}
	}

	@Override
	protected void updateForViewNotVisible() {
		if (shouldBePacked()) {
			if (getView3D().viewChangedByZoom()) {
				// will be updated if visible again
				setWaitForUpdate();
			}
		}
	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * Constructor for previewable
	 * 
	 * @param a_view3D
	 *            view
	 * @param selectedPoints
	 *            endpoints
	 */
	public DrawSegment3D(EuclidianView3D a_view3D,
			ArrayList<GeoPointND> selectedPoints) {

		super(a_view3D, selectedPoints,
				new GeoSegment3D(a_view3D.getKernel().getConstruction()));

		setDrawMinMax(0, 1);

	}

	@Override
	protected Coords[] calcPoints() {
		GeoSegmentND seg = (GeoSegmentND) getGeoElement();
		return new Coords[] { seg.getStartInhomCoords(),
				seg.getEndInhomCoords() };
	}

	@Override
	protected void setStartEndPoints(Coords p1, Coords p2) {
		super.setStartEndPoints(p1, p2);

		double radius = getLineThickness() * PlotterBrush.LINE3D_THICKNESS
				/ getView3D().getScale();

		for (int i = 1; i <= 3; i++) {
			if (p1.get(i) < p2.get(i)) {
				boundsMin.set(i, p1.get(i) - radius);
				boundsMax.set(i, p2.get(i) + radius);
			} else {
				boundsMin.set(i, p2.get(i) - radius);
				boundsMax.set(i, p1.get(i) + radius);
			}
		}
	}

	@Override
	public void enlargeBounds(Coords min, Coords max) {
		enlargeBounds(min, max, boundsMin, boundsMax);
	}

	@Override
	protected void updateForItSelf(Coords p1, Coords p2) {
		if (shouldBePacked()) {
			getView3D().getRenderer().getGeometryManager().setPackCurve(getColor(), getGeoElement().getLineType(),
					getGeoElement().getLineTypeHidden());
		}
		super.updateForItSelf(p1, p2);
		if (shouldBePacked()) {
			getView3D().getRenderer().getGeometryManager().endPacking();
		}
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {
		if (shouldBePacked()) {
			if (prop == GProperty.COLOR) {
				updateColors();
				getView3D().getRenderer().getGeometryManager().updateColor(getColor(), getGeometryIndex());
				if (!isVisible()) {
					getView3D().getRenderer().getGeometryManager().updateVisibility(false, getGeometryIndex());
				}
			} else if (prop == GProperty.VISIBLE) {
				getView3D().getRenderer().getGeometryManager().updateVisibility(isVisible(), getGeometryIndex());
			} else {
				super.setWaitForUpdateVisualStyle(prop);
			}
		} else {
			super.setWaitForUpdateVisualStyle(prop);
		}
	}

	@Override
	public void disposePreview() {
		if (shouldBePacked()) {
			removeFromGL();
		}
		super.disposePreview();
	}

	@Override
	protected int getReusableGeometryIndex() {
		if (managerPackBuffers() && shouldBePacked()) {
			int index = getGeometryIndex();
			if (hasTrace()) {
				if (index != NOT_REUSABLE_INDEX) {
					if (traces == null) {
						traces = new LinkedList<Integer>();
					}
					traces.add(index);
				}
				return NOT_REUSABLE_INDEX;
			}
			return index;
		}
		return super.getReusableGeometryIndex();
	}

	@Override
	protected void recordTrace() {
		if (!(managerPackBuffers() && shouldBePacked())) {
			super.recordTrace();
		}
	}

	@Override
	protected void clearTraceForViewChangedByZoomOrTranslate() {
		if (managerPackBuffers() && shouldBePacked()) {
			if (traces != null) {
				while (!traces.isEmpty()) {
					doRemoveGeometryIndex(traces.pop());
				}
			}
		} else {
			super.clearTraceForViewChangedByZoomOrTranslate();
		}
	}

	@Override
	public boolean shouldBePacked() {
		return getView3D().getApplication().has(Feature.MOB_PACK_BUFFERS_3D) && !createdByDrawList();
	}

	private boolean managerPackBuffers() {
		return getView3D().getRenderer().getGeometryManager().packBuffers();
	}
}
