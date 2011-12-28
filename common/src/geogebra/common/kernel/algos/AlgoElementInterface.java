package geogebra.common.kernel.algos;

import java.util.ArrayList;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;


public interface AlgoElementInterface {

	public AlgoElementInterface getUpdateAfterAlgo();

	public void update();

	public int getConstructionIndex();

	public int getOutputLength();

	public GeoElement getOutput(int i);

	public ArrayList<GeoPoint2> getFreeInputPoints();

	

}
