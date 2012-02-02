package geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Collection;

import geogebra.common.awt.Rectangle;
import geogebra.common.kernel.LayerView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.algos.AlgoElementInterface;

public interface EuclidianViewInterfaceSlim extends LayerView{

	boolean isDefault2D();
	public ArrayList<GeoPoint2> getFreeInputPoints(AlgoElementInterface algoParent);
	boolean isMoveable(GeoElement geoElement);
	int getWidth();
	int getHeight();
	double toRealWorldCoordX(double i);
	double toRealWorldCoordY(double i);
	void updateBounds();
	void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric);
	AbstractEuclidianController getEuclidianController();
	double[] getGridDistances();
	double getXmax();		
	double getYmax();
	double getXmin();
	double getYmin();
	double getXscale();
	double getYscale();
	DrawableND getDrawableND(GeoElement listElement);
	DrawableND createDrawableND(GeoElement listElement);
	void zoom(double d, double e, double i, int j, boolean b);
	int getPointCapturingMode();
	void setPointCapturing(int pointCapturingStickyPoints);
	Collection<? extends GeoPointND> getStickyPointList();
	void setSelectionRectangle(Rectangle r);
}
