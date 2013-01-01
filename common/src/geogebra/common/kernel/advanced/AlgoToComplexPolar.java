package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;

public class AlgoToComplexPolar extends AlgoElement {
	private int coordStyle;
	private GeoPoint inPoint;
	private GeoVector inVector;
	private GeoPoint outPoint;
	private GeoList inList;
	private GeoVector outVector;
	public AlgoToComplexPolar(Construction cons, String label, GeoPoint geoPoint,int coordStyle) {
		super(cons);
		inPoint = geoPoint;
		outPoint = new GeoPoint(cons);
		init(coordStyle,outPoint,label);
	}
	
	public AlgoToComplexPolar(Construction cons, String label, GeoList geoList,int coordStyle) {
		super(cons);
		inList = geoList;
		outPoint = new GeoPoint(cons);
		init(coordStyle,outPoint,label);
	}
	
	public AlgoToComplexPolar(Construction cons, String label, GeoVector geoVector,int coordStyle) {
		super(cons);
		inVector = geoVector;
		outVector = new GeoVector(cons);
		init(coordStyle,outVector,label);
	}

	private void init(int coordStyle1,GeoElement out,String label){
		this.coordStyle = coordStyle1;
		setInputOutput();
		compute();
		((VectorValue)out).setMode(coordStyle1);
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
			outPoint.setMode(coordStyle);
			return;
		}
		if(inVector!=null){
			outVector.set(inVector);
			outVector.setMode(coordStyle);
			return;
		}
		outPoint.setCoords(((NumberValue)inList.get(0)).getDouble(),((NumberValue)inList.get(1)).getDouble(),1);
		outPoint.setMode(coordStyle);
	}

	@Override
	public Commands getClassName() {
		switch(coordStyle){
		case Kernel.COORD_COMPLEX:return Commands.ToComplex;
		case Kernel.COORD_POLAR:return Commands.ToPolar;
		default: return Commands.ToPoint;
		}
	}

	

	

	/**
	 * @return resulting point/vector
	 */
	public GeoElement getResult() {
		return inVector == null ? outPoint : outVector;
	}

}
