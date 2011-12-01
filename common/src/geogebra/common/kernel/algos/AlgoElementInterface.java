package geogebra.common.kernel.algos;

import java.util.ArrayList;

import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoPointInterface;


public interface AlgoElementInterface {

	public AlgoElementInterface getUpdateAfterAlgo();

	public void update();

	public int getConstructionIndex();

	public int getOutputLength();

	public GeoElementInterface getOutput(int i);

	public ArrayList<GeoPointInterface> getFreeInputPoints();

	

}
