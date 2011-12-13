package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;

public abstract class AbstractGeoTextField extends GeoButton {

	public AbstractGeoTextField(Construction c) {
		super(c);
	}

	public String getClassName() {
		return "GeoTextField";
	}
	public boolean isChangeable(){
		return true;
	}
	
    protected String getTypeString() {
		return "TextField";
	}
    
    public GeoClass getGeoClassType() {
    	return GeoClass.TEXTFIELD;
    }
    
	public boolean isTextField() {
		return true;
	}
	
	public void setLinkedGeo(GeoElement geo) {
		linkedGeo = geo;
		text = geo.getValueForInputBar();
	}
	
	public GeoElement getLinkedGeo() {
		return linkedGeo;
	}

	protected GeoElement linkedGeo = null;
	

	protected String text = null;
	public String toValueString() {
		if (linkedGeo == null) return "";
		return text;
	}
	public void setText(String text2) {
		text = text2;		
	}
	
	public boolean isGeoTextField(){
		return true;
	}

	public abstract void setLength(int i);


}
