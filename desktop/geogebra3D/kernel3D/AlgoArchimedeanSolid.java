package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra3D.archimedean.support.ArchimedeanSolidFactory;
import geogebra3D.archimedean.support.IArchimedeanSolid;
import geogebra3D.archimedean.support.IFace;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoArchimedeanSolid extends AlgoPolyhedron{


	protected OutputHandler<GeoPolygon3D> outputPolygons;
	protected OutputHandler<GeoSegment3D> outputSegments;
	
	private GeoPointND A, B;
	private GeoDirectionND v;
	
	protected CoordMatrix4x4 matrix;


	
	private Coords[] coords;
	
	
	private String name, className;
	
	
	
	/** creates an archimedean solid
	 * @param c construction 
	 * @param labels
	 * @param A 
	 * @param B 
	 * @param v 
	 * @param name 
	 */
	public AlgoArchimedeanSolid(Construction c, String[] labels, 
			GeoPointND A, GeoPointND B, GeoDirectionND v,
			String name) {
		super(c);
		
		this.name = name;
		this.className = "Algo"+name;


		this.A=A;
		this.B=B;
		this.v=v;
		
		matrix = new CoordMatrix4x4();

		createPolyhedron();
		
		compute();
		
		// input 
		setInput();	
		addAlgoToInput();
		
		polyhedron.createFaces();
		
		polyhedron.setReverseNormals();
		setOutput();
		
		
		setLabels(labels);
        
        
        update();
	}
	
	
	/**
	 * set the labels
	 * @param labels lables
	 */
	protected void setLabels(String[] labels){
	
		if(isOldFileVersion() && labels.length>1){
			String name = labels[0];
			labels = new String[1];
			labels[0] = name;
		}
		
		if (labels==null || labels.length <= 1 || isOldFileVersion())
			polyhedron.initLabels(labels);
		else{
			for (int i=0; i<labels.length; i++)
				getOutput(i).setLabel(labels[i]);
		}
		
	}
	
	
	protected GeoPointND getA(){
		return A;
	}
	
	protected GeoPointND getB(){
		return B;
	}
	
	protected Coords getDirection(){
		return v.getDirectionInD3();
	}
	
	
	protected void setInput(){
		input = new GeoElement[3];
		input[0]=(GeoElement) A;
		input[1]=(GeoElement) B;
		input[2]=(GeoElement) v;
		
	}
	
	@Override
	protected void createOutputSegments(){
		outputSegments=createOutputSegmentsHandler();
	}
	
	@Override
	protected void createOutputPolygons(){
		outputPolygons=createOutputPolygonsHandler();
	}
	

	@Override
	protected void updateOutput(){
		
		//add polyhedron's segments and polygons, without setting this algo as algoparent
		
		outputPolygons.addOutput(polyhedron.getFaces(),false,false);
		outputSegments.addOutput(polyhedron.getSegments3D(),false,true);
		
	}

	
	/**
	 * create the polyhedron (faces and edges)
	 * @param polyhedron
	 */
	protected void createPolyhedron() {
		
		IArchimedeanSolid solid = ArchimedeanSolidFactory.create(name);
		int vertexCount = solid.getVertexCount();
		
		outputPoints.augmentOutputSize(vertexCount-2);
		outputPoints.setLabels(null);
		
		
		//coords
		coords = solid.getVerticesInABv();
		
		//points
		GeoPointND[] points = new GeoPointND[vertexCount];
		points[0] = getA();
		points[1] = getB();
		for (int i=2; i<vertexCount; i++){
			GeoPoint3D point = outputPoints.getElement(i-2);
			points[i] = point;
			point.setCoords(coords[i]);
			polyhedron.addPointCreated(point);
		}
		
		//faces
		IFace[] faces = solid.getFaces();
		for (int i=0; i<solid.getFaceCount(); i++){
			polyhedron.startNewFace();
			for (int j=0; j<faces[i].getVertexCount(); j++)
				polyhedron.addPointToCurrentFace(points[faces[i].getVertexIndices()[j]]);
			polyhedron.endCurrentFace();
		}
		
	}

	@Override
	public void compute() {
		
		polyhedron.setDefined();
		
		Coords o = getA().getInhomCoordsInD(3);
		
		Coords v1l = getB().getInhomCoordsInD(3).sub(o);
		
		//check if A!=B
		if (v1l.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			setUndefined();
			return;
		}
		
		v1l.calcNorm();
		double l = v1l.getNorm();
		Coords v1 = v1l.mul(1/l);
		
		//check if vn!=0
		Coords vn = getDirection();
		if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			setUndefined();
			return;
		}		
		
		//check if vn is ortho to AB
		if (!Kernel.isZero(vn.dotproduct(v1))){
			setUndefined();
			return;
		}		
		
		
		Coords v2 = getDirection().crossProduct(v1);
		v2.normalize();
		
		Coords v3 = v1.crossProduct(v2);
		
		
		matrix.setOrigin(o);
		matrix.setVx(v1l);
		matrix.setVy(v2.mul(l));
		matrix.setVz(v3.mul(l));
		
		for (int i=0; i<coords.length-2; i++){
			outputPoints.getElement(i).setCoords(matrix.mul(coords[i+2]),true);
		}
		

	}	
	
	
	private void setUndefined(){
		polyhedron.setUndefined();
		
		for (int i=0; i<outputPoints.size(); i++)
			outputPoints.getElement(i).setUndefined();
		
	}
	
	
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	

	
	
	
	
	@Override
	public void update() {

		// compute and polyhedron
		super.update();
		
		//output points
		for (int i=0;i<outputPoints.size();i++)
			outputPoints.getElement(i).update();

	}
	
	




	@Override
	public Algos getClassName() {
		return Algos.valueOf(className);
	}

	// TODO Consider locusequability
	
    
  
    
	
	
}
