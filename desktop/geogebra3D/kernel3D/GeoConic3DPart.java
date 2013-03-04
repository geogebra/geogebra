package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.plugin.GeoClass;

import java.util.TreeSet;

/**
 * Partial conic for intersection of (limited) cylinders and cones
 * @author mathieu
 *
 */
public class GeoConic3DPart extends GeoConic3D {
	
	private double[] paramStart, paramEnd, paramExtent;

	/**
	 * @param c construction
	 */
	public GeoConic3DPart(Construction c) {
		super(c);
		
		paramStart = new double[2];
		paramEnd = new double[2];
		paramExtent = new double[2];
		
	}
	
	
	private class IndexedParameter implements Comparable<IndexedParameter>{
		protected double value;
		protected int index;
		
		public IndexedParameter(double value, int index){
			this.value = value;
			this.index = index;
		}

		public int compareTo(IndexedParameter parameter) {
			
			//NaN are the greatest
			if (Double.isNaN(value))
				return 1;
			
			if (Double.isNaN(parameter.value))
				return -1;
			
			
			//compare values
			if (value<parameter.value)
				return -1;
			
			if (value>parameter.value)
				return 1;
			
			
			//compare indices
			if (index<parameter.index)
				return -1;
			
			return 1; //never return 0 to ensure having four parameters
		}
	}
	
	private TreeSet<IndexedParameter> parametersTree = new TreeSet<IndexedParameter>();
	private IndexedParameter[] parametersArray = new IndexedParameter[4];
	
	/**
	 * set parameters for "hole" regarding the index
	 * @param startParam start parameter
	 * @param endParam end parameter
	 * @param index index
	 */
	final public void setParameters(double segStart1, double segEnd1, double segStart2, double segEnd2) {

		// handle conic types
		switch (type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
			
			parametersTree.clear();
			parametersTree.add(new IndexedParameter(segStart1, 1));
			parametersTree.add(new IndexedParameter(segEnd1, 1));
			parametersTree.add(new IndexedParameter(segStart2, 2));
			parametersTree.add(new IndexedParameter(segEnd2, 2));
			parametersTree.toArray(parametersArray);
			
			double start1, end1, start2, end2;
			if (parametersArray[0].index == parametersArray[1].index){
				start1 = parametersArray[0].value;
				end1 = parametersArray[1].value;
				start2 = parametersArray[2].value;
				end2 = parametersArray[3].value;
			}else{
				start1 = parametersArray[1].value;
				end1 = parametersArray[2].value;
				start2 = parametersArray[3].value;
				end2 = parametersArray[0].value;				
			}
			
			//App.debug(start1+","+end1+","+start2+","+end2);
			
			//if no parameter for second hole (NaN), set second parameter to NaN
			if (start2==end2){
				start2 = start1; 
				end2 = Double.NaN;
				start1 = Double.NaN;
			}else if(start1==end1){
				end1 = end2; 
				end2 = Double.NaN;
				start1 = Double.NaN;
			}
			
			// set parameters
			paramStart[0] = Kernel.convertToAngleValue(end1);
			paramEnd[0] = Kernel.convertToAngleValue(start2);
			paramExtent[0] = paramEnd[0] - paramStart[0];
			if (paramExtent[0] < 0)
				paramExtent[0] += Kernel.PI_2;
			
			paramStart[1] = Kernel.convertToAngleValue(end2);
			paramEnd[1] = Kernel.convertToAngleValue(start1);
			paramExtent[1] = paramEnd[1] - paramStart[1];
			if (paramExtent[1] < 0)
				paramExtent[1] += Kernel.PI_2;
			
			
			
		}
	}
	
	/**
	 * @param index index of the hole
	 * @return start parameter
	 */
	final public double getParameterStart(int index) {
		return paramStart[index];
	}

	/**
	 * @param index index of the hole
	 * @return end parameter
	 */
	final public double getParameterEnd(int index) {
		return paramEnd[index];
	}

	/**
	 * @param index index of the hole
	 * @return end parameter - start parameter
	 */
	final public double getParameterExtent(int index) {
		return paramExtent[index];
	}
	
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CONIC3DPART;
	}
	
	/**
	 * Sector or arc
	 * 
	 * @return CONIC_PART_ARCS
	 */
	final public int getConicPartType() {
		return CONIC_PART_ARC;
	}


}
