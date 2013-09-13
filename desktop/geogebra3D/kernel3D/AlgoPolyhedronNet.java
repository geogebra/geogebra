package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

/** Algo that compute the net for a polyhedron
 * @author Vincent
 *
 */
public class AlgoPolyhedronNet extends AlgoElement3D {

	private GeoPolyhedron p ; 
	private NumberValue v;
	
	protected OutputHandler<GeoPolyhedronNet> outputNet;
	
	/** points generated as output  */
	protected OutputHandler<GeoPoint3D> outputPointsBottom, outputPointsTop;

	
	/**
	 * @param c construction
	 */
	public AlgoPolyhedronNet(Construction c, String[] labels, GeoPolyhedron p, NumberValue v) {
		super(c);
		this.p = p;
		this.v = v;	
		
		
		
		outputNet=new OutputHandler<GeoPolyhedronNet>(new elementFactory<GeoPolyhedronNet>() {
			public GeoPolyhedronNet newElement() {
				GeoPolyhedronNet p = new GeoPolyhedronNet(cons);
				p.setParentAlgorithm(AlgoPolyhedronNet.this);
				return p;
			}
		});
		
		
		outputNet.adjustOutputSize(1);
		
		outputPointsBottom = createOutputPoints();
		outputPointsTop = createOutputPoints();
		
		
		
		input = new GeoElement[] {p, (GeoElement) v};		
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		refreshOutput();

		
		// set labels
		getNet().setLabel(null);
		
		
		update();

		
	}
	
	private OutputHandler<GeoPoint3D> createOutputPoints(){
		return new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoPolyhedronNet.this);
				return p;
			}
		});
	}

	@Override
	public void compute() {
		//App.debug("coucou");
		//App.printStacktrace("coucou");
		double f = v.getDouble();
		
		// update bottom points
		AlgoPolyhedronPoints algo = (AlgoPolyhedronPoints) p.getParentAlgorithm();
		GeoPointND[] points = algo.getBottomPoints();
		outputPointsBottom.adjustOutputSize(points.length);
		outputPointsBottom.setLabels(null);
		for (int i=0;i<outputPointsBottom.size() ;i++)
			outputPointsBottom.getElement(i).setCoords(points[i].getInhomCoordsInD(3));
		
		
		// update top points
		outputPointsTop.adjustOutputSize(points.length);
		outputPointsTop.setLabels(null);
		GeoPolygon bottomPolygon = algo.getBottom();
		GeoPointND topPoint = algo.getTopPoint();
		
		Coords topCoords = topPoint.getInhomCoordsInD(3);
		Coords p1 = topCoords.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
		Coords v1 = p1.sub(topCoords);
		double d1 = p.getOrientedHeight();
		if (d1<0) { // top point below the bottom face : negative rotation
			f*=-1;
			d1*=-1;
		}
		GeoSegmentND[] bottomSegments = bottomPolygon.getSegments();
		for (int i=0;i<outputPointsTop.size() ;i++){
		    GeoPoint3D wpoint = outputPointsTop.getElement(i);
			wpoint.setCoords(topPoint);
			
			// angle between side face and bottom face
			GeoSegmentND si = bottomSegments[i];
			Coords o = points[i].getInhomCoordsInD(3);
			Coords vs = si.getDirectionInD3();
			Coords v2 = p1.sub(o);
			double d2 = topCoords.distLine(o, vs);
			double angle = Math.asin(d1/d2);			
			if (v2.crossProduct(vs).dotproduct(v1)*f > 0){ // top point is inside bottom face
				angle = Math.PI-angle;
			}			
			wpoint.rotate(f*angle, si);
		}
		


	}

	@Override
	public GetCommand getClassName() {
		return Commands.PolyhedronNet;
	}
	
	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedronNet getNet(){
		return outputNet.getElement(0);
	}

}
