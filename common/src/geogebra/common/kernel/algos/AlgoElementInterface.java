package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoElementInterface;


public interface AlgoElementInterface {

	public AlgoElementInterface getUpdateAfterAlgo();

	public void update();

	public int getConstructionIndex();

	public int getOutputLength();

	public GeoElementInterface getOutput(int i);

	

}
