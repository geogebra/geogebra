package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoSpline;

/**
 * Algorithm for spline. 
 * 
 * @author Giuliano Bellucci
 * 
 */
public class AlgoSpline extends AlgoElement  {

	/**
	 * list of points
	 */
	private GeoList inputList;
	private GeoSpline spline;
	private GeoNumberValue degree;


	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 */
	public AlgoSpline(Construction cons, String label, GeoList inputList) {
		this(cons,label,inputList,new GeoNumeric(cons,3));
	}
	
	/**
	 * @param cons
	 * 			construction
	 * @param label
	 * 			label
	 * @param inputList 
	 * 			list of points
	 * @param degree 
	 * 			grade of polynoms
	 */
	public AlgoSpline(final Construction cons, final String label, final GeoList inputList, final GeoNumberValue degree) {
		super(cons);
		this.degree = degree;
		this.inputList = inputList;
		spline = new GeoSpline(cons,inputList,degree);
		spline.setEuclidianVisible(true);
		compute();
		setInputOutput();
		spline.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = degree.toGeoElement();
		super.setOutputLength(1);
		super.setOutput(0, spline);
		setDependencies();
	}

	@Override
	public void compute() {
		spline.recalculate();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Spline;
	}
	
	/**
	 * @return spline
	 */
	public GeoSpline getSpline(){
		return spline;
	}
}
