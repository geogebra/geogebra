package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.main.App;

/**
 * draws a quadric part
 * 
 * @author mathieu
 *
 */
public class DrawQuadric3DPart extends DrawQuadric3D {

	public DrawQuadric3DPart(EuclidianView3D view, GeoQuadric3DPart quadric) {
		super(view, quadric);
	}

	@Override
	protected double[] getMinMax() {

		GeoQuadric3DPart quadric = (GeoQuadric3DPart) getGeoElement();

		return new double[] { quadric.getMinParameter(1),
				quadric.getMaxParameter(1) };
	}

	@Override
	protected void setSurfaceV(float min, float max, PlotterSurface surface) {
		surface.setV(min, max);
		surface.setNbV(3);
	}

	@Override
	protected void updateForView() {
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();

		switch (quadric.getType()) {
		// case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			if (getView3D().viewChangedByZoom()) {
				int l = getView3D().getRenderer().getGeometryManager()
						.getLongitude(quadric.getHalfAxis(0), getView3D().getScale());
				if (longitude != l){
					updateForItSelf();
				}
			}
			break;
		}
	}

	@Override
	public boolean doHighlighting() {

		// if it depends on a limited quadric, look at the meta' highlighting

		if (getGeoElement().getMetasLength() > 0) {
			for (GeoElement meta : ((FromMeta) getGeoElement()).getMetas()) {
				if (meta != null && meta.doHighlighting())
					return true;
			}
		}

		return super.doHighlighting();
	}
}
