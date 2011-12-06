package geogebra.common.euclidian;

import java.util.ArrayList;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoNumericInterface;
import geogebra.common.kernel.geos.GeoPointInterface;
import geogebra.common.kernel.algos.AlgoElementInterface;

public interface EuclidianViewInterfaceSlim extends View{

	boolean isDefault2D();
	public ArrayList<GeoPointInterface> getFreeInputPoints(AlgoElementInterface algoParent);
	boolean isMoveable(GeoElementInterface geoElement);
	int getWidth();
	int getHeight();
	double toRealWorldCoordX(double i);
	double toRealWorldCoordY(double i);
	void updateBounds();
	void replaceBoundObject(GeoNumericInterface num, GeoNumericInterface geoNumeric);		

}
