package geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import geogebra.common.kernel.algos.AlgoElement.elementFactory;
import geogebra.common.kernel.arithmetic.IneqTree;
import geogebra.common.kernel.arithmetic.Inequality;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;

public class AlgoVertexIneq extends AlgoElement {
	
	private OutputHandler<GeoElement> outputPoints;
	private GeoFunctionNVar p;
	private List<GeoPoint> vertices;
	private int validVertices;
	/**
	 * Creates algo for Vertex[poly] (many output points)
	 * Creates new unlabeled vertex algo
	 * @param cons construction
	 * @param p polygon or polyline
	 */
	AlgoVertexIneq(Construction cons, GeoFunctionNVar p) {
		super(cons);
		this.p = p;
		vertices = new ArrayList<GeoPoint>();
		outputPoints=createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}

	public AlgoVertexIneq(Construction cons, String[] labels, GeoFunctionNVar p) {
		this(cons, p);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);
		 
        update();
	}
	
	private void setLabels(String[] labels) {
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
        if (labels!=null &&
        		labels.length==1 &&
        		//outputPoints.size() > 1 &&
        		labels[0]!=null &&
        		!labels[0].equals("")) {
        	outputPoints.setIndexLabels(labels[0]);
        } else {
        	
        	outputPoints.setLabels(labels);
        	outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel(StringTemplate.defaultTemplate));
        }	
    }

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{p};
			
		setDependencies();

	}

	@Override
	public void compute() {
		validVertices = 0;
		IneqTree ineqs = p.getIneqs();
		int size = ineqs.getSize();
		for(int i = 0; i<size; i++){
			for(int j = i+1; j<size; j++){
				Inequality a,b;
				if(ineqs.get(i).getType().ordinal()<ineqs.get(j).getType().ordinal()){
					a = ineqs.get(i);
					b = ineqs.get(j);
				}else{
					b = ineqs.get(i);
					a = ineqs.get(j);
				}
				typeSwitch(a,b);
				
			}	
		}

		
		outputPoints.adjustOutputSize(validVertices >0?validVertices : 1);
		
		
		for (int i =0; i<validVertices; i++){
    		GeoPointND point = (GeoPointND) outputPoints.getElement(i);
    		point.set(vertices.get(i));    		
    	}
    	//other points are undefined
    	for(int i = validVertices;i<outputPoints.size();i++) {
    		outputPoints.getElement(i).setUndefined();
    	}
	}
	
	private void typeSwitch(Inequality a, Inequality b) {
		switch(a.getType()){
		case INEQUALITY_PARAMETRIC_X:
			switch(b.getType()){
				case INEQUALITY_PARAMETRIC_X:
					intParamXParamX(a,b);
					break;
				case INEQUALITY_PARAMETRIC_Y:
					intParamXParamY(a,b);
					break;
				case INEQUALITY_LINEAR:
					intParamXLinear(a,b);
					break;
				case INEQUALITY_CONIC:
					intParamXConic(a,b);
					break;
				case INEQUALITY_1VAR_X:
					intParamXX(a,b);
					break;
				case INEQUALITY_1VAR_Y:
					intParamXY(a,b);
			}
			break;
		case INEQUALITY_PARAMETRIC_Y:	
			switch(b.getType()){
			case INEQUALITY_PARAMETRIC_Y:
				intParamYParamY(a,b);
				break;
			case INEQUALITY_LINEAR:
				intParamYLinear(a,b);
				break;
			case INEQUALITY_CONIC:
				intParamYConic(a,b);
				break;
			case INEQUALITY_1VAR_X:
				intParamYX(a,b);
				break;
			case INEQUALITY_1VAR_Y:
				intParamYY(a,b);
		}
		break;	
		case INEQUALITY_LINEAR:
			switch(b.getType()){
				case INEQUALITY_LINEAR:
					intLinearLinear(a,b);
					break;
				case INEQUALITY_CONIC:
					intLinearConic(a,b);
					break;
				case INEQUALITY_1VAR_X:
					intLinearX(a,b);
					break;
				case INEQUALITY_1VAR_Y:
					intLinearY(a,b);
			}
			break;
		case INEQUALITY_CONIC:
			switch(b.getType()){
				case INEQUALITY_CONIC:
					intConicConic(a,b);
					break;
				case INEQUALITY_1VAR_X:
					intConicX(a,b);
					break;
				case INEQUALITY_1VAR_Y:
					intConicY(a,b);
			}
			break;
		case INEQUALITY_1VAR_X:
			switch(b.getType()){
				case INEQUALITY_1VAR_X:
					//no intersections possible
					break;
				case INEQUALITY_1VAR_Y:
					intXY(a,b);
					break;
			}
			break;
		case INEQUALITY_1VAR_Y:
			//no intersections possible
			break;
		}
		
		
	}

	private void intParamYY(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamYX(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamYConic(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamYLinear(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamYParamY(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamXY(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamXX(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamXConic(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamXLinear(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamXParamY(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intParamXParamX(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intXY(Inequality a, Inequality b) {
		GeoPoint[] az = a.getZeros();
		GeoPoint[] bz = b.getZeros();
		for(GeoPoint ap:az){
			for(GeoPoint bp:bz){
				ensurePoint();
				vertices.get(validVertices).setCoords(ap.getX(),bp.getX(),1);
				validVertices++;
				App.debug(ap+","+bp);
			}
		}
		
	}

	private void intConicY(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intConicX(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intConicConic(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intLinearY(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intLinearX(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intLinearConic(Inequality a, Inequality b) {
		// TODO Auto-generated method stub
		
	}

	private void intLinearLinear(Inequality a,Inequality b){
		ensurePoint();
		GeoVec3D.cross(a.getLineBorder(),b.getLineBorder(),vertices.get(validVertices));
		validVertices++;
	}

	private void ensurePoint() {
		while(vertices.size()<=validVertices || vertices.get(validVertices)==null)
			vertices.add(new GeoPoint(cons));
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoVertex;
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
	 * @return resulting vertices
	 */
	public GeoElement[] getVertex() {
		return getOutput();
	}
	
	private OutputHandler<GeoElement> createOutputPoints(){
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint pt=new GeoPoint(cons);
				pt.setCoords(0, 0, 1);
				pt.setParentAlgorithm(AlgoVertexIneq.this);
				return pt;
			}
		});
	}

}
