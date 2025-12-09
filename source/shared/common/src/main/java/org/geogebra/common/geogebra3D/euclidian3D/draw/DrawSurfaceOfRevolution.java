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

import java.util.ArrayList;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.RotationConverter;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoAxisND;

/**
 * Preview for surface of revolution tool.
 * 
 * @author Zbynek
 *
 */
public class DrawSurfaceOfRevolution extends Drawable3DSurfaces
		implements Previewable {

	private ArrayList<Path> selectedFunctions;
	private GeoElement previewSurface;
	private GeoAngle angle;

	/**
	 * @param a_view3d
	 *            view
	 * @param selectedFunctions
	 *            selected functions
	 */
	public DrawSurfaceOfRevolution(EuclidianView3D a_view3d,
			ArrayList<Path> selectedFunctions) {
		super(a_view3d);
		this.selectedFunctions = selectedFunctions;
	}

	@Override
	public void updatePreview() {
		if (previewSurface == null && selectedFunctions.size() == 1
				&& !(selectedFunctions.get(0) instanceof GeoAxisND)) {
			Kernel3D kernel = getView3D().getKernel();
			angle = createAngle(0);
			boolean oldSilent = kernel.isSilentMode();
			kernel.setSilentMode(true);
			previewSurface = kernel.getManager3D()
					.surfaceOfRevolution(selectedFunctions.get(0), angle, null);
			kernel.setSilentMode(oldSilent);
			previewSurface.setAlgebraVisible(false);
			previewSurface.setLabel(null);
			((EuclidianController3D) getView3D().getEuclidianController())
					.setHandledGeo(previewSurface,
							selectedFunctions.get(0).toGeoElement());
		}
	}

	private GeoAngle createAngle(double value) {
		Kernel kernel = getView3D().getKernel();
		return new GeoAngle(kernel.getConstruction(), value);
	}

	@Override
	public void updateMousePos(double x, double y) {
		// no drawing
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		// no drawing
	}

	@Override
	public void disposePreview() {
		super.disposePreview();
		cleanUp();
	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		// no drawing
	}

	@Override
	protected boolean updateForItSelf() {
		// no drawing
		return false;
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// no drawing
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// no drawing
	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no drawing
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	/**
	 * Create actual angle after preview is done.
	 */
	public void createAngle() {
		Hits hits = new Hits();
		double angleValue = 2 * Math.PI;
		if (angle != null
				&& angle.getValue() > RotationConverter.SNAP_PRECISION) {
			angleValue = angle.getValue();
		}
		hits.add(createAngle(angleValue));
		getView3D().getEuclidianController().addSelectedNumberValue(hits, 1,
				false, false);
		cleanUp();
	}

	private void cleanUp() {
		((EuclidianController3D) getView3D().getEuclidianController())
				.disposeHandledGeo();
		if (previewSurface != null) {
			previewSurface.remove();
		}
	}

}
