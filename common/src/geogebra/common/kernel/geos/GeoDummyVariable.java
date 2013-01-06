package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;

/**
 * Dummy GeoElement to be used for symbolic variable resolving
 * for the GeoGebra CAS.
 * 
 * @see geogebra.common.kernel.Kernel#setResolveUnkownVarsAsDummyGeos(boolean)
 * @author Markus Hohenwarter
 */
public class GeoDummyVariable extends GeoNumeric {

	private String varName;
	/**
	 * Creates new dummy variable
	 * @param c construction
	 * @param varName variable name
	 */
	public GeoDummyVariable(Construction c, String varName) {
		super(c);	
		this.varName = varName;
	}
	
	@Override
	public String toString(StringTemplate tpl) {
		return tpl.printVariableName(varName);
	}		
	
	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);	
	}		
	
	/**
	 * @return variable name
	 */
	public String getVarName(){
		return varName;
	}
	
	/**
	 * @return GeoElement with same name (null if not found)
	 */
	public GeoElement getElementWithSameName(){
		GeoElement ge = kernel.lookupCasCellLabel(varName);
		if(ge==null)
			ge = kernel.lookupLabel(varName);
		return ge;
	}
	@Override
	public boolean hasCoords() {
		GeoElement ge = getElementWithSameName();
		if(ge != null && !(ge instanceof GeoDummyVariable))
			return ge.hasCoords();
		return false;
	}

}
