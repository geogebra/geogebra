package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;

/**
 * Helper for drawing planes, for quadrics
 * 
 * @author Proprietaire
 *
 */
public class DrawPlane3DForQuadrics extends DrawPlane3D {

	private GeoQuadric3D quadric;
	private DrawQuadric3D quadricDrawable;

	public DrawPlane3DForQuadrics(EuclidianView3D a_view3D,
			GeoPlane3D a_plane3D, GeoQuadric3D quadric,
			DrawQuadric3D quadricDrawable) {
		super(a_view3D, a_plane3D, quadric);
		this.quadricDrawable = quadricDrawable;
	}

	@Override
	protected void init(GeoElement a_plane3D, GeoElement geo2) {

		super.init(a_plane3D);
		this.quadric = (GeoQuadric3D) geo2;

	}

	@Override
	public GeoElement getGeoElement() {
		return quadric;
	}

	@Override
	protected GeoPlane3D getPlane() {
		return (GeoPlane3D) super.getGeoElement();
	}

	@Override
	public boolean shouldBePacked() {
		return quadricDrawable.shouldBePacked();
	}

	@Override
	protected int getGridThickness() {
		if (getView3D().getApplication().has(Feature.MOB_PACK_QUADRICS)) {
			return 0; // no grid used for planes for quadrics
		}
		return super.getGridThickness();
	}

}
