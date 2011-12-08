package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;

/**
 * Dummy GeoElement to be used for symbolic variable resolving
 * for the GeoGebra CAS.
 * 
 * @see geogebra.kernel.Kernel#setResolveUnkownVarsAsDummyGeos(boolean)
 * @author Markus Hohenwarter
 */
public class GeoDummyVariable extends GeoNumeric {

	private String varName;
	
	public GeoDummyVariable(Construction c, String varName) {
		super(c);	
		this.varName = varName;
	}
	
	@Override
	public String toString() {
		return kernel.printVariableName(varName);
	}		
	
	@Override
	public String toValueString() {
		return toString();	
	}		

}
