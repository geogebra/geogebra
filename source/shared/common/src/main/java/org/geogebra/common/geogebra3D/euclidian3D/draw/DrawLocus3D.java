/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.plot.CurvePlotterUtils;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * @author mathieu
 * 
 *         Drawable for locus
 * 
 */
public class DrawLocus3D extends Drawable3DCurves {

	private GeoLocusND<? extends MyPoint> locus;
	private CoordSys transformCoordSys;

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param locus
	 *            the locus to draw
	 * @param geo
	 *            locus itself or parent geo
	 * @param transformSys
	 *            transformation coord sys for implicit curves
	 */
	public DrawLocus3D(EuclidianView3D a_view3d, GeoLocusND<? extends MyPoint> locus,
			GeoElement geo, CoordSys transformSys) {
		super(a_view3d, geo);
		this.locus = locus;
		this.transformCoordSys = transformSys;
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	@Override
	protected boolean updateForItSelf() {

		EuclidianView3D view = getView3D();

		Renderer renderer = view.getRenderer();

		setPackCurve(true);
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		brush.start(getReusableGeometryIndex());
		brush.setThickness(getGeoElement().getLineThickness(),
				(float) view.getScale());
		brush.setAffineTexture(0f, 0f);
		brush.setLength(1f);

		try {
			CurvePlotterUtils.draw(brush, getLocus().getPoints(), transformCoordSys);
			setGeometryIndex(brush.end());
		} catch (Exception e) {
			setGeometryIndex(-1);
		}

		endPacking();

		return true;
	}

	protected GeoLocusND<? extends MyPoint> getLocus() {
		return locus;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			setWaitForUpdate();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_PATH;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
	}

	@Override
	protected boolean isLabelVisible() {
		return false;
	}

	@Override
	protected void updateGeometriesColor() {
		updateGeometriesColor(false);
	}

	@Override
	protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityNoSurface(visible);
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible() && getGeoElement().getLineThickness() > 0) {
			exportToPrinter3D.exportCurve(this, ExportToPrinter3D.Export3DType.CURVE);
		}
	}

}
