package geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import geogebra.common.kernel.algos.AlgoElement.elementFactory;
import geogebra.common.kernel.arithmetic.IneqTree;
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
		int validVertices = 0;
		IneqTree ineqs = p.getIneqs();
		int size = ineqs.getSize();
		for(int i = 0; i<size; i++){
			for(int j = i+1; j<size; j++){
				GeoLine a = ineqs.get(i).getLineBorder();
				GeoLine b = ineqs.get(j).getLineBorder();
				if(a!=null && b!=null){
					while(vertices.size()<=validVertices || vertices.get(validVertices)==null)
						vertices.add(new GeoPoint(cons));
					GeoVec3D.cross(a,b,vertices.get(validVertices));
					validVertices++;
				}
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
