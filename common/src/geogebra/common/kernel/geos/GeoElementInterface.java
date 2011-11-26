package geogebra.common.kernel.geos;

public interface GeoElementInterface {
	public boolean needsReplacingInExpressionNode();
	public String getLabel();
	public String getRealLabel();	
	public boolean isLabelSet();
	public boolean isIndependent();
	public boolean isLocalVariable();
	public boolean isGeoVector();
	public boolean isGeoLine();
	public boolean isNumberValue();
	public String toValueString();
	public String getAlgebraDescriptionTextOrHTML();
	public String getNameDescriptionTextOrHTML();
	public String getCaptionDescriptionHTML(boolean b);
	public int getConstructionIndex();
	public String getDefinitionDescription();
	public String getCommandDescription();
	public String getAlgebraDescription();
	public String getNameDescription();
	public String getAlgebraDescriptionHTML(boolean b);
	public String getCommandDescriptionHTML(boolean b);
	public String getDefinitionDescriptionHTML(boolean b);
	public boolean isConsProtocolBreakpoint();
	public void setConsProtocolBreakpoint(boolean newVal);
	public int getRelatedModeID();
	public void update();
	public boolean setCaption(String string);
	public String getNameDescriptionHTML(boolean b, boolean c);	
	
}
