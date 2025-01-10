package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.Rotatable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;

/**
 * @author mathieu
 *
 *         Interface for surfaces in any dimension
 */
public interface GeoImplicitSurfaceND extends Translateable, Dilateable,
		Rotatable, MirrorableAtPlane, Transformable {

	/**
	 * @return surface defining function (LHS-RHS)
	 */
	FunctionNVar getExpression();

}
