package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Draw line for quadrics
 * 
 * @author mathieu
 *
 */
public class DrawLine3DForQuadrics extends DrawLine3D {

	private GeoQuadric3D quadric;
	private DrawQuadric3D quadricDrawable;

	/**
	 * @param a_view3D
	 *            view
	 * @param line
	 *            line
	 * @param quadric
	 *            quadric
	 * @param quadricDrawable TODO
	 */
	public DrawLine3DForQuadrics(EuclidianView3D a_view3D, GeoLine3D line,
			GeoQuadric3D quadric, DrawQuadric3D quadricDrawable) {
		super(a_view3D, line, quadric);
		this.quadricDrawable = quadricDrawable;
	}

	@Override
	protected void init(GeoElement line, GeoElement geo2) {

		super.init(line);
		this.quadric = (GeoQuadric3D) geo2;

	}

	@Override
	public GeoElement getGeoElement() {
		return quadric;
	}

	@Override
	protected GeoLine3D getLine() {
		return (GeoLine3D) super.getGeoElement();
	}

	@Override
	public boolean shouldBePacked() {
		return quadricDrawable.shouldBePacked();
	}
}
