package geogebra.common.euclidian;

import java.util.ArrayList;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoPointInterface;
import geogebra.common.kernel.algos.AlgoElementInterface;

public interface EuclidianViewInterfaceSlim extends View{

	boolean isDefault2D();
	public ArrayList<GeoPointInterface> getFreeInputPoints(AlgoElementInterface algoParent);
	boolean isMoveable(GeoElementInterface geoElement);

}
