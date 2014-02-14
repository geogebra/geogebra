/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoMirror;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;

/**
 * @author Zoltan Kovacs
 * Restriction coming from an {@link AlgoMirror}.
 */
public class EquationMirrorRestriction extends EquationGenericMidpointRestriction {

	/**
	 * General constructor.
	 * @param geo a point.
	 * @param algo an {@link AlgoElement}
	 * @param scope an {@link EquationScope}
	 */
	public EquationMirrorRestriction(final GeoElement geo, final AlgoMirror algo, final EquationScope scope) {
		super(geo, algo, scope);
		
		GeoElement p = algo.getInput()[0];
		GeoElement m = algo.getInput()[1];
		GeoElement p_ = algo.getOutput()[0];
		
		if (p instanceof GeoPoint && m instanceof GeoPoint) {
	        this.setEnds((GeoPoint) p, (GeoPoint) p_);
	        this.setMidpoint((GeoPoint) m);		
		} else {
			App.error("unimplemented");
		}
		
	}
}
