package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

public class AlgoDimension extends AlgoElement {
	
	private GeoList matrixDimension;
	private GeoNumeric firstDimension, secondDimension;
	private boolean matrix;
	private GeoList list;
	public AlgoDimension(Construction cons, String label, GeoList geoList) {
		super(cons);
		list = geoList;
		
		firstDimension = new GeoNumeric(cons);
		matrix = list.isMatrix();
		if(matrix){
			matrixDimension = new GeoList(cons);
			secondDimension = new GeoNumeric(cons);
			matrixDimension.add(firstDimension);
			matrixDimension.add(secondDimension);
		}
		
		setInputOutput();
		compute();
		getResult().setLabel(label);
		
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{list};
		
		if(matrix){
			setOnlyOutput(matrixDimension);
		}else{
			setOnlyOutput(firstDimension);
		}
		setDependencies();
	}

	@Override
	public void compute() {
		int size = list.size();
		firstDimension.setValue(size);
		if(matrix){
			matrixDimension.setDefined(true);
			if(!list.get(0).isGeoList()){
				matrixDimension.setUndefined();
				return;
			}
			int n = ((GeoList)list.get(0)).size();
			for(int i=0;i<size;i++){
				if(!list.get(i).isGeoList() || ((GeoList)list.get(i)).size()!=n){
					matrixDimension.setUndefined();
					return;
				}
			}
			secondDimension.setValue(n);
		}

	}
	
	public GeoElement getResult(){
		return matrix ? matrixDimension : firstDimension;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDimension;
	}

}
