package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.plugin.GeoClass;

/**
 * Simple geo class for the whole space
 * 
 * @author matthieu
 *
 */
public class GeoSpace extends GeoElement3D implements GeoDirectionND {

	/**
	 * @param c
	 */
	public GeoSpace(Construction c) {
		super(c);
		label = "space";
		labelSet=true;
		setFixed(true);
	}


	@Override
	public Coords getLabelPosition() {
		return null;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SPACE;
	}

	@Override
	public GeoElement copy() {
		return new GeoSpace(cons);
	}

	@Override
	public void set(GeoElement geo) {
		// no need here

	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() { 
		//no need here
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return false;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the space available
		// in empty constructions too (for step == -1)
		return true;
	}


	
	
	public Coords getDirectionInD3() {
		// return null since there's no specific direction
		// used for commands that should need a direction, like OrthogonalLine
		return null;
	}
	

	@Override
	public String getLabel(StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			return app.getPlain(label);
		}
		return label;

	}
}
