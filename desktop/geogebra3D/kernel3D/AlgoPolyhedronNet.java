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

	
	
	private void rotate(GeoPoint3D point, Coords pointCoords, Coords projectCoords,  Coords o, Coords vs, double f, Coords fd, double dist){

		Coords v2 = projectCoords.sub(o);
		double d2 = pointCoords.distLine(o, vs);
		double angle = Math.asin(dist/d2);		
		
		if (v2.crossProduct(vs).dotproduct(fd) < 0) { // top point is inside bottom face
			angle = Math.PI - angle;
		}	
		
		point.rotate(f * angle, o, vs);
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
		for (int i = 0 ; i < outputPointsBottom.size() ; i++) {
			outputPointsBottom.getElement(i).setCoords(points[i].getInhomCoordsInD(3));
		}

		switch(p.getType()) {

		case GeoPolyhedron.TYPE_PYRAMID:
			// update top points
			outputPointsTop.adjustOutputSize(points.length);
			outputPointsTop.setLabels(null);
			GeoPolygon bottomPolygon = algo.getBottom();
			GeoPointND topPoint = algo.getTopPoint();

			Coords topCoords = topPoint.getInhomCoordsInD(3);
			Coords p1 = topCoords.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
			double d1 = p.getOrientedHeight();
			
			Coords faceDirection = bottomPolygon.getDirectionInD3();
			
			if (d1 < 0) { // top point below the bottom face : negative rotation
				f *= -1;
				d1 *= -1;
			}
			
			GeoSegmentND[] bottomSegments = bottomPolygon.getSegments();
			
			for (int i = 0 ; i < outputPointsTop.size() ; i++) {
				GeoPoint3D wpoint = outputPointsTop.getElement(i);
				wpoint.setCoords(topPoint);

				// angle between side face and bottom face
				GeoSegmentND si = bottomSegments[i];
				Coords o = points[i].getInhomCoordsInD(3);
				Coords vs = si.getDirectionInD3();
				Coords v2 = p1.sub(o);
				double d2 = topCoords.distLine(o, vs);
				double angle = Math.asin(d1/d2);		
				
				if (v2.crossProduct(vs).dotproduct(faceDirection) < 0) { // top point is inside bottom face
					angle = Math.PI - angle;
				}	
					
				wpoint.rotate(f * angle, si);
			}
			break;

		case GeoPolyhedron.TYPE_PRISM:
			// update top points
			outputPointsTop.adjustOutputSize(points.length * 3 - 2);
			outputPointsTop.setLabels(null);
			GeoPolygon bottomPolyg = algo.getBottom();
			GeoPointND[] topP = algo.getTopFace().getPointsND();

			Coords topCo = topP[0].getInhomCoordsInD(3);
			Coords pp1 = topCo.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
			Coords vv1 = pp1.sub(topCo);
			double dd1 = p.getOrientedHeight();
			if (dd1 < 0) { // top point below the bottom face : negative rotation
				f *= -1;
				dd1 *= -1;
			}
			Coords vp = topCo.sub(outputPointsBottom.getElement(0).getInhomCoordsInD(3));
			GeoSegmentND[] bottomSegs = bottomPolyg.getSegments();
			int sz = outputPointsBottom.size();
			Coords vectrans = new Coords(4); 
			faceDirection = bottomPolyg.getDirectionInD3();
			for (int i = 0 ; i < sz ; i++) {
				//triple creation of top points
				GeoPoint3D wpoint1 = outputPointsTop.getElement(2 * i);
				int j = 2 * i - 1;
				if (j < 0) {
					j = 2 * sz - 1;
				}
				GeoPoint3D wpoint2 = outputPointsTop.getElement(j);
				Coords cCoord = topP[i].getInhomCoordsInD(3);
				wpoint1.setCoords(cCoord);
				wpoint2.setCoords(cCoord);
				if (i > 1) {  //wpoint3 if for the top face, except 2 first points (already exist)
					GeoPoint3D wpoint3 = outputPointsTop.getElement(i + 2 * sz - 2);
					wpoint3.setCoords(cCoord);
				}
				// rotate wpoint1
				// angle between side face and bottom face
				GeoSegmentND si1 = bottomSegs[i];
				Coords o = points[i].getInhomCoordsInD(3);
				Coords vs = si1.getDirectionInD3();
				pp1 = wpoint1.getInhomCoordsInD(3).projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];

				
				rotate(wpoint1, cCoord, pp1, o, vs, f, faceDirection, dd1);
				
				if (i == 0) { // the translation used for the top face is calculated
					vectrans.set(wpoint1.getInhomCoordsInD(3).sub(topCo)); 
				}

				// rotate wpoint2	(rotate with the precedent bottom segment)
				int segNum = i - 1;
				if (segNum < 0) {
					segNum = sz - 1;
				}
				GeoSegmentND si2 = bottomSegs[segNum];	
				// angle between side face and bottom face
				o = points[i].getInhomCoordsInD(3);
				vs = si2.getDirectionInD3();
				pp1 = wpoint2.getInhomCoordsInD(3).projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
				vv1 = pp1.sub(wpoint2.getInhomCoordsInD(3));

				
				rotate(wpoint2, cCoord, pp1, o, vs, f, faceDirection, dd1);
				
			}
			
			GeoPoint3D wpoint1 = outputPointsTop.getElement(0);
			Coords vs = bottomSegs[0].getDirectionInD3();
			for (int i = 0 ; i < sz-2 ; i++) {
				// rotate wpoint3 (rotate pi rad around top segment 0)
				GeoPoint3D wpoint3 = outputPointsTop.getElement(i + sz * 2);
				wpoint3.translate(vectrans);
				wpoint3.rotate(f * Math.PI, wpoint1.getInhomCoordsInD(3),vs);             	
			}
			break;

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
