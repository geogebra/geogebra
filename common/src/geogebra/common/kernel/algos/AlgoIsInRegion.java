/**
 * 
 */
package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Determine whether point is in region.
 * @author kondr
 * 
 */
public class AlgoIsInRegion extends AlgoElement {

	private GeoPointND pi;
	private Region region;
	private GeoBoolean result;

	/**
	 * Creates new algo 
	 * @param c
	 * @param label
	 * @param pi
	 * @param region
	 */
	public AlgoIsInRegion(Construction c, String label, GeoPointND pi,
			Region region) {
		super(c);
		this.pi = pi;
		this.region = region;
		result = new GeoBoolean(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public void compute() {		
		pi.updateCoords2D();
		result.setValue(region.isInRegion(pi.getX2D(),pi.getY2D()));
	}

	@Override
	protected void setInputOutput() {
		setOutputLength(1);
		setOutput(0, result);
		input = new GeoElement[2];
		input[0] = (GeoElement) pi;
		input[1] = (GeoElement) region;
		setDependencies();
	}

	/** 
	 * Returns true iff point is in region.
	 * @return true iff point is in region
	 */
	public GeoBoolean getResult() {
		return result;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIsInRegion;
	}

	// TODO Consider locusequability

}
