package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;

/**
 * Net for a polyhedron
 * @author Vincent
 *
 */
public class GeoPolyhedronNet extends GeoPolyhedron {

	/**
	 * @param c  construction
	 */
	public GeoPolyhedronNet(Construction c) {
		super(c);
	}

	@Override
	public String getTypeString() {
		return "Net";
	}
	
	@Override
	public boolean isGeoPolyhedron() {
		return false;
	}
	
	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(getArea(), tpl));
		return sbToString.toString();
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(getArea()));
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);

	@Override
	final public String toValueString(StringTemplate tpl) {
		return kernel.format(getArea(), tpl);
	}
}


