package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoArchimedeanSolid extends AlgoPolyhedron{

	
	
	private GeoPointND A, B;
	private GeoDirectionND v;
	
	protected CoordMatrix4x4 matrix;

	
	
	
	/** creates an archimedean solid
	 * @param c construction 
	 * @param labels
	 * @param A 
	 * @param B 
	 * @param v 
	 */
	public AlgoArchimedeanSolid(Construction c, String[] labels, GeoPointND A, GeoPointND B, GeoDirectionND v) {
		super(c);

		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);

		this.A=A;
		this.B=B;
		this.v=v;
		
		matrix = new CoordMatrix4x4();

		createPolyhedron(polyhedron);
		//polyhedron.updateFaces();
		
		compute();
		
		// input 
		setInput();	
		addAlgoToInput();
		
		polyhedron.updateFaces();
		setOutput();
		
		
        
        polyhedron.initLabels(labels);
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
	
	
	/**
	 * create the polyhedron (faces and edges)
	 * @param polyhedron
	 */
	protected abstract void createPolyhedron(GeoPolyhedron polyhedron);
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	

	
	
	
	
	public void update() {

		// compute and polyhedron
		super.update();
		
		//output points
		for (int i=0;i<outputPoints.size();i++)
			outputPoints.getElement(i).update();

	}
	
	

	
	
    
  
    
	
	
}
