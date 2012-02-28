package geogebra.common.kernel.kernelND;

import geogebra.common.awt.Color;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.ToGeoElement;

public interface GeoElementND extends ExpressionValue,ToGeoElement{
	void setLabel(String string);
	void update();

	void setObjColor(Color objectColor);

	void setEuclidianVisible(boolean visible);
	
	boolean isEuclidianVisible();
	
	boolean isLabelVisible();
	
	public boolean isLabelSet();

	public String getLabel(StringTemplate tpl);

	public boolean isInfinite();
	
	public void updateVisualStyle();

	public void remove();
	
	public boolean getSpreadsheetTrace();
	
	public GeoElement copyInternal(Construction cons);

	public boolean isIndependent();

	public AlgoElement getParentAlgorithm();

	public boolean isDefined();

	public void setUndefined();

	public void setLineType(int type);

	public void setLineThickness(int th);

	

	public void setLabelVisible(boolean b);
	
	/**
	 * Returns whether this GeoElement is a point on a path.
	 * 
	 * @return true for points on path
	 */
	public boolean isPointOnPath();
	
	/**
	 * Returns whether this GeoElement is a point in a region
	 * 
	 * @return true for points on path
	 */
	public boolean isPointInRegion();
	
	/**
	 * @param p point
	 * @return distance from point
	 */
	public double distance(GeoPointND p);

}
