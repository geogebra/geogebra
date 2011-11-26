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
}
