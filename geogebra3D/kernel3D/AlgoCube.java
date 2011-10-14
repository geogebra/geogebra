package geogebra3D.kernel3D;

import com.quantimegroup.solutions.archimedean.common.SolidDefinition;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.archimedean.support.ArchimedeanSolidFactory;
import geogebra3D.archimedean.support.IArchimedeanSolid;
import geogebra3D.archimedean.support.IFace;
import geogebra3D.archimedean.support.Point;


public class AlgoCube extends AlgoArchimedeanSolid {
	
	
	
	
	private Coords[] coords;
	

	public AlgoCube(Construction c, String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v) {
		super(c, labels, A, B, v);
	}
	
	

	
	protected void createPolyhedron(GeoPolyhedron polyhedron) {
		
		IArchimedeanSolid cube = ArchimedeanSolidFactory.create(SolidDefinition.CUBE);
		int vertexCount = cube.getVertexCount();
		
		outputPoints.augmentOutputSize(vertexCount-2);
		outputPoints.setLabels(null);
		
		
		//coords
		coords = cube.getVerticesInABv();
		
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
		IFace[] faces = cube.getFaces();
		for (int i=0; i<cube.getFaceCount(); i++){
			polyhedron.startNewFace();
			for (int j=0; j<faces[i].getVertexCount(); j++)
				polyhedron.addPointToCurrentFace(points[faces[i].getVertexIndices()[j]]);
			polyhedron.endCurrentFace();
		}
		
		

	}

	@Override
	protected void compute() {
		
		Coords o = getA().getInhomCoordsInD(3);
		
		Coords v1l = getB().getInhomCoordsInD(3).sub(o);
		if (v1l.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getPolyhedron().setUndefined();
			return;
		}
		
		v1l.calcNorm();
		double l = v1l.getNorm();
		Coords v1 = v1l.mul(1/l);
		
		Coords v2 = getDirection().crossProduct(v1);
		if (v2.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getPolyhedron().setUndefined();
			return;
		}
		
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

	public String getClassName() {
		return "AlgoCube";
	}

}
