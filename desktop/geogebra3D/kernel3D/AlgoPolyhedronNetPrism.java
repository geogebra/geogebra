package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoSegmentND;

/** Algo that compute the net for a polyhedron
 * @author Vincent
 *
 */
public class AlgoPolyhedronNetPrism extends AlgoPolyhedronNet {

	/**
	 * @param c construction
	 */
	public AlgoPolyhedronNetPrism(Construction c, String[] labels, GeoPolyhedron p, NumberValue v) {
		super(c,labels,p,v);
	}


	@Override
	protected int getPointLengthFromLabelsLength(int length){
		return length/10;
	}


	@Override
	protected void createNet(int n) {

		GeoPolyhedronNet net = getNet();

		outputPointsBottom.adjustOutputSize(n);
		outputPointsSide.adjustOutputSize(2 * n);
		outputPointsTop.adjustOutputSize(n - 2);

		//create bottom face
		net.startNewFace();
		for (int i = 0; i < n; i++) {
			net.addPointToCurrentFace(outputPointsBottom.getElement(i));
		}
		net.endCurrentFace();

		//create side faces
		for (int i=0; i<n; i++){
			net.startNewFace();
			net.addPointToCurrentFace(outputPointsBottom.getElement(i));
			net.addPointToCurrentFace(outputPointsBottom.getElement((i+1)%n));
			net.addPointToCurrentFace(outputPointsSide.getElement((2*i+1)%(2*n)));
			net.addPointToCurrentFace(outputPointsSide.getElement(2*i));
			net.endCurrentFace();
		}

		//create top face
		net.startNewFace();
		net.addPointToCurrentFace(outputPointsSide.getElement(0));
		net.addPointToCurrentFace(outputPointsSide.getElement(1));
		for (int i = 0; i < n - 2; i++) {
			net.addPointToCurrentFace(outputPointsTop.getElement(i));
		}
		net.endCurrentFace();
	}

	@Override
	protected void setOutputSideTop(int n, GeoPolygon3D polygon, int step, GeoSegmentND[] segments){
		if (step == n) {
			outputPolygonsTop.addOutput(polygon, false);
			for (int i=1; i<segments.length; i++) {
				outputSegmentsTop.addOutput((GeoSegment3D) segments[i], false);	
			}
		} else {
			outputPolygonsSide.addOutput(polygon, false);
			outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[3], false);
			outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2], false);
			outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[1], false);		
		}
	}


	@Override
	public void compute(double f, GeoPolygon bottomPolygon, Coords[] points) {
		// update top points
		outputPointsSide.adjustOutputSize(2 * points.length);
		outputPointsSide.setLabels(null);
		outputPointsTop.adjustOutputSize(points.length - 2);
		outputPointsTop.setLabels(null);
		
		
		Coords[] topP = getPointsCoords(p.getTopFace());

		Coords topCo = topP[0];
		Coords pp1 = topCo.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
		double dd1 = p.getOrientedHeight();
		if (dd1 < 0) { // top point below the bottom face : negative rotation
			f *= -1;
			dd1 *= -1;
		}

		int sz = outputPointsBottom.size();

		Coords faceDirection = bottomPolygon.getDirectionInD3();

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
			cCoord = topP[i];
			wpoint1.setCoords(cCoord);
			wpoint2.setCoords(cCoord);
			if (i > 1) {  // wpoint3 is for the top face, except 2 first points (already exist)
				wpoint3 = outputPointsTop.getElement(i - 2);
				wpoint3.setCoords(cCoord);
			}
		}
		
		Coords[] bottomSegsDirections = new Coords[points.length];
		Coords p1 = points[points.length - 1];
		Coords p2 = points[0];
		bottomSegsDirections[points.length - 1] = p2.sub(p1).normalized();
		for (int i = 0 ; i < points.length - 1 ; i++){
			p1 = p2;
			p2 = points[i+1];
			bottomSegsDirections[i] = p2.sub(p1).normalized();
		}
		
		// rotation of the top face around first top segment
		Coords o = topP[1];
		Coords vs = bottomSegsDirections[0];
		GeoPolygon side0 = p.getFirstSideFace();
		for (int i = 0 ; i < sz - 2 ; i++) {
			wpoint3 = outputPointsTop.getElement(i);
			cCoord = wpoint3.getInhomCoordsInD(3);
			pp1 = cCoord.projectPlane(side0.getCoordSys().getMatrixOrthonormal())[0];
			double dist =  pp1.distance(cCoord);
			rotate(wpoint3, cCoord, pp1, o, vs, f, side0.getDirectionInD3(), dist, true);		
		}
		for (int i = 0 ; i < 2 * sz ; i+=2) {
			// rotate wpoint1
			// angle between side face and bottom face
			o = points[i/2];
			vs = bottomSegsDirections[i/2];
			wpoint1 = outputPointsSide.getElement(i);
			cCoord = wpoint1.getInhomCoordsInD(3);
			pp1 = cCoord.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
			rotate(wpoint1, cCoord, pp1, o, vs, f, faceDirection, dd1, false);
			// rotate wpoint2	
			wpoint2 = outputPointsSide.getElement(i+1);
			cCoord = wpoint2.getInhomCoordsInD(3);
			pp1 = cCoord.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
			rotate(wpoint2, cCoord, pp1, o, vs, f, faceDirection, dd1, false);

			if (i == 0) { // the rotation for the top face is made with the same angle
				for (int j = 0 ; j < sz - 2 ; j++) {
					wpoint3 = outputPointsTop.getElement(j);
					rotate(wpoint3, cCoord, pp1, o, vs, f, faceDirection, dd1, false);			
				}
			}
		}


	}


}
