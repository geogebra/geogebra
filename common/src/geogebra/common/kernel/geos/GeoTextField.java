package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.util.StringUtil;

public class GeoTextField extends GeoButton {
	private static int defaultLength = 20;
	private int length;
	public GeoTextField(Construction c) {
		super(c);
		length = defaultLength;
	}
	public GeoTextField(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
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

	public void setLength(int l){
		length = l;
		this.updateVisualStyle();
	}

	public int getLength() {
		return length;
	}

	protected void getXMLtags(StringBuilder sb) {

		super.getXMLtags(sb);
		if (linkedGeo != null) {
   	
			sb.append("\t<linkedGeo exp=\"");
			sb.append(StringUtil.encodeXML(linkedGeo.getLabel()));
			sb.append("\"");			    		    	
			sb.append("/>\n");
		}
		
		if (getLength() != defaultLength) {
			sb.append("\t<length val=\"");
			sb.append(getLength());
			sb.append("\"");			    		    	
			sb.append("/>\n");			
		}

	}
	@Override
	public GeoElement copy() {
		return new GeoTextField(cons, labelOffsetX, labelOffsetY);
	}

}
