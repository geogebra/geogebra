package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * dot plot algo with vertical scale
 * @author mathieu
 *
 */
public class AlgoDotPlotScale extends AlgoDotPlot {

	
	private GeoNumeric scale;
	
	/**
	 * 
	 * @param cons
	 * @param label
	 * @param inputList
	 * @param scale vertical scale
	 */
	public AlgoDotPlotScale(Construction cons, String label, GeoList inputList, GeoNumeric scale) {
		super(cons, label, inputList, scale);
	}
	
    @Override
	protected void setScale(GeoNumeric scale){
    	this.scale = scale;
    }

    @Override
    protected void setInput(){
    	input = new GeoElement[2];
    	input[0] = inputList;
    	input[1] = scale;
    }

    private double scaleFactor;
    
    @Override
	public final void compute() {
    	
    	if (!scale.isDefined()){
    		outputList.setUndefined();
    		return;
    	}
    	
    	scaleFactor = scale.getValue();
    	
    	super.compute();
    }

    @Override
    protected double getScaledY(int y){
    	return y * scaleFactor;
    }

}
