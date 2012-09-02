package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.util.GgbMat;

public class AlgoMatrixRank extends AlgoElement {

	private GeoList inputList;
	private GeoNumeric rank;
	
	public AlgoMatrixRank(Construction cons, String label, GeoList matrix) {
		 super(cons);
	     this.inputList = matrix;
	               
	     rank = new GeoNumeric(cons);

	     setInputOutput();
	     compute();
	     rank.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(rank);
		input = new GeoElement[]{inputList};
		setDependencies();
	}

	@Override
	public void compute() {
   		GgbMat matrix = new GgbMat(inputList);
   		
   		if (matrix.isUndefined()) {
  			rank.setUndefined();
	   		return;   		
	   	}
   
   		matrix.reducedRowEchelonFormImmediate();
   		int rows = matrix.getRowDimension();
   		int cols = matrix.getColumnDimension();
   		for(int i=0;i<rows;i++){
   			boolean onlyZeros = true;
   			for(int j=0;j<cols;j++){
   				if(!Kernel.isZero(matrix.getEntry(i, j))){
   					onlyZeros = false;
   					break;
   				}   				
   			}
   			if(onlyZeros){
				rank.setValue(i);
				return;
			}
   		}
   		rank.setValue(rows);

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoMatrixRank;
	}

	public GeoNumeric getResult() {
		return rank;
	}

	// TODO Consider locusequability

}
