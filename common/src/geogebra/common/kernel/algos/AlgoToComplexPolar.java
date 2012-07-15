package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

public class AlgoToComplexPolar extends AlgoElement {
	private boolean polar;
	private GeoPoint inPoint;
	private GeoVector inVector;
	private GeoPoint outPoint;
	private GeoList inList;
	private GeoVector outVector;
	public AlgoToComplexPolar(Construction cons, String label, GeoPoint geoPoint,boolean polar) {
		super(cons);
		inPoint = geoPoint;
		outPoint = new GeoPoint(cons);
		init(polar,outPoint,label);
	}
	
	public AlgoToComplexPolar(Construction cons, String label, GeoList geoList,boolean polar) {
		super(cons);
		inList = geoList;
		outPoint = new GeoPoint(cons);
		init(polar,outPoint,label);
	}
	
	public AlgoToComplexPolar(Construction cons, String label, GeoVector geoVector,boolean polar) {
		super(cons);
		inVector = geoVector;
		outVector = new GeoVector(cons);
		init(polar,outVector,label);
	}

	private void init(boolean polar,GeoElement out,String label){
		this.polar = polar;
		setInputOutput();
		compute();
		((VectorValue)out).setMode(polar?Kernel.COORD_POLAR:Kernel.COORD_COMPLEX);
		out.setLabel(label);
	}
	@Override
	protected void setInputOutput() {
		if(inVector!=null){
			setOnlyOutput(outVector);
			input = new GeoElement[]{inVector};
		}else{
			setOnlyOutput(outPoint);
			input = new GeoElement[]{inPoint == null ? inList : inPoint};
		}
		setDependencies();
	}

	@Override
	public void compute() {
		if(inPoint!=null){
			outPoint.set((GeoElement)inPoint);
			return;
		}
		if(inVector!=null){
			outVector.set(inVector);
			return;
		}
		outPoint.setCoords(((NumberValue)inList.get(0)).getDouble(),((NumberValue)inList.get(1)).getDouble(),1);

	}

	@Override
	public Algos getClassName() {
		return polar ? Algos.AlgoToPolar :Algos.AlgoToComplex;
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return resulting point/vector
	 */
	public GeoElement getResult() {
		return inVector == null ? outPoint : outVector;
	}

}
