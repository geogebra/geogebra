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
	protected OutputHandler<GeoPoint3D> outputPointsBottom, outputPointsSide, outputPointsTop;


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
		outputPointsSide = createOutputPoints();
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

	/**
	 * @param point   point to rotate
	 * @param pointCoords  coordinates of this point
	 * @param projectCoords coordinates of the projected point on bottom face 
	 * @param o coordinates of the origin of the rotation line
	 * @param vs direction of the rotation line
	 * @param f value of the cursor used in the rotation
	 * @param fd direction of the bottom face
	 * @param dist distance between point and projectedpoint
	 * @param test value : 1 test the angle, 0 do not test
	 */
	private void rotate(GeoPoint3D point, Coords pointCoords, Coords projectCoords,  Coords o, Coords vs, double f, Coords fd, double dist, int test){

		Coords v2 = projectCoords.sub(o);
		double d2 = pointCoords.distLine(o, vs);
		double angle = Math.asin(dist/d2);		

		if ((test == 0)^(v2.crossProduct(vs).dotproduct(fd) < 0)) { // top point is inside bottom face
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
			outputPointsSide.adjustOutputSize(points.length);
			outputPointsSide.setLabels(null);
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

			for (int i = 0 ; i < outputPointsSide.size() ; i++) {
				GeoPoint3D wpoint = outputPointsSide.getElement(i);
				wpoint.setCoords(topPoint);

				// angle between side face and bottom face
				GeoSegmentND si = bottomSegments[i];
				Coords o = points[i].getInhomCoordsInD(3);
				Coords vs = si.getDirectionInD3();
				GeoPoint3D wpoint1 = outputPointsSide.getElement(i);
				Coords cCoord = wpoint1.getInhomCoordsInD(3);
				rotate(wpoint1, cCoord, p1, o, vs, f, faceDirection, d1, 1);
			}
			break;

		case GeoPolyhedron.TYPE_PRISM:
			// update top points
			outputPointsSide.adjustOutputSize(2 * points.length);
			outputPointsSide.setLabels(null);
			outputPointsTop.adjustOutputSize(points.length - 2);
			outputPointsTop.setLabels(null);
			GeoPolygon bottomPolyg = algo.getBottom();
			GeoPointND[] topP = algo.getTopFace().getPointsND();

			Coords topCo = topP[0].getInhomCoordsInD(3);
			Coords pp1 = topCo.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
			double dd1 = p.getOrientedHeight();
			if (dd1 < 0) { // top point below the bottom face : negative rotation
				f *= -1;
				dd1 *= -1;
			}
			GeoSegmentND[] bottomSegs = bottomPolyg.getSegments();
			int sz = outputPointsBottom.size();

			faceDirection = bottomPolyg.getDirectionInD3();

			GeoPoint3D wpoint1 = null;
			GeoPoint3D wpoint2 = null;
			GeoPoint3D wpoint3 = null;
			Coords cCoord = null; //Coords of the current top point
			for (int i = 0 ; i < sz ; i++) {
				//triple creation of top points
				wpoint1 = outputPointsSide.getElement(2 * i);
				int j = 2 * i - 1;
				if (j < 0) {
					j = 2 * sz - 1;
				}
				wpoint2 = outputPointsSide.getElement(j);
				cCoord = topP[i].getInhomCoordsInD(3);
				wpoint1.setCoords(cCoord);
				wpoint2.setCoords(cCoord);
				if (i > 1) {  // wpoint3 is for the top face, except 2 first points (already exist)
					wpoint3 = outputPointsTop.getElement(i - 2);
					wpoint3.setCoords(cCoord);
				}
			}
			// rotation of the top face around first top segment
			Coords o = topP[1].getInhomCoordsInD(3);
			Coords vs = bottomSegs[0].getDirectionInD3();
			for (int i = 0 ; i < sz-2 ; i++) {
				wpoint3 = outputPointsTop.getElement(i);
				cCoord = wpoint3.getInhomCoordsInD(3);
				pp1 = cCoord.projectPlane(algo.getSide(0).getCoordSys().getMatrixOrthonormal())[0];
				double dist =  pp1.distance(cCoord);
				rotate(wpoint3, cCoord, pp1, o, vs, f, algo.getSide(0).getDirectionInD3(), dist, 0);		
			}
			for (int i = 0 ; i < 2*sz ; i+=2) {
				// rotate wpoint1
				// angle between side face and bottom face
				o = points[i/2].getInhomCoordsInD(3);
				vs = bottomSegs[i/2].getDirectionInD3();
				wpoint1 = outputPointsSide.getElement(i);
				cCoord = wpoint1.getInhomCoordsInD(3);
				pp1 = cCoord.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
				rotate(wpoint1, cCoord, pp1, o, vs, f, faceDirection, dd1, 1);
				// rotate wpoint2	
				wpoint2 = outputPointsSide.getElement(i+1);
				cCoord = wpoint2.getInhomCoordsInD(3);
				pp1 = cCoord.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
				rotate(wpoint2, cCoord, pp1, o, vs, f, faceDirection, dd1, 1);

				if (i == 0) { // the rotation for the top face is made with the same angle
					for (int j = 0 ; j < sz-2 ; j++) {
						wpoint3 = outputPointsTop.getElement(j);
						rotate(wpoint3, cCoord, pp1, o, vs, f, faceDirection, dd1, 1);			
					}
				}
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
