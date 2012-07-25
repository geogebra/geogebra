package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.GeoClass;

/**
 * Simple geo class for clipping cube
 * 
 * @author matthieu
 *
 */
public class GeoClippingCube3D extends GeoElement3D {

	/**
	 * @param c
	 */
	public GeoClippingCube3D(Construction c) {
		super(c);
	}


	@Override
	public Coords getLabelPosition() {
		return null;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CLIPPINGCUBE3D;
	}

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() { }

	@Override
	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public String getTypeString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

	//cube reduction
	static public int REDUCTION_SMALL = 0;
	static public int REDUCTION_MEDIUM = 1;
	static public int REDUCTION_LARGE = 2;
	
	
	private int reduction = REDUCTION_LARGE;

	/**
	 * sets the reduction of the cube
	 * @param value reduction
	 */
	public void setReduction(int value){
		reduction = value;
	}
	
	/**
	 * 
	 * @return the reduction of the cube
	 */
	public int getReduction(){
		return reduction;
	}
	
}
