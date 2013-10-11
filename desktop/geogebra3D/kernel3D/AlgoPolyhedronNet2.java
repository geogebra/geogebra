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

import java.util.Collection;

/** Algo that compute the net for a polyhedron (with side face collected)
 * @author Vincent
 *
 */
public class AlgoPolyhedronNet2 extends AlgoElement3D {

	private GeoPolyhedron p ; 
	private NumberValue v;

	protected OutputHandler<GeoPolyhedronNet> outputNet;

	/** points generated as output  */
	private OutputHandler<GeoPoint3D> outputPointsBottom, outputPointsSide, outputPointsTop;
	protected OutputHandler<GeoSegment3D> outputSegmentsBottom, outputSegmentsSide, outputSegmentsTop;
	protected OutputHandler<GeoPolygon3D> outputPolygonsBottom, outputPolygonsSide, outputPolygonsTop;


	/**
	 * @param c construction
	 */
	public AlgoPolyhedronNet2(Construction c, String[] labels, GeoPolyhedron p, NumberValue v) {
		super(c);
		this.p = p;
		this.v = v;	



		outputNet=new OutputHandler<GeoPolyhedronNet>(new elementFactory<GeoPolyhedronNet>() {
			public GeoPolyhedronNet newElement() {
				GeoPolyhedronNet p = new GeoPolyhedronNet(cons);
				p.setParentAlgorithm(AlgoPolyhedronNet2.this);
				return p;
			}
		});


		outputNet.adjustOutputSize(1);

		outputPointsBottom = createOutputPoints();
		outputPointsSide = createOutputPoints();
		outputPointsTop = createOutputPoints();

		outputPolygonsBottom = createOutputPolygons();
		outputPolygonsSide = createOutputPolygons();
		outputPolygonsTop = createOutputPolygons();

		outputSegmentsBottom = createOutputSegments();
		outputSegmentsSide = createOutputSegments();
		outputSegmentsTop = createOutputSegments();

		AlgoPolyhedronPoints algo = (AlgoPolyhedronPoints) p.getParentAlgorithm();
		int n = algo.getBottomPoints().length;
		createNet(n);

		input = new GeoElement[] {p, (GeoElement) v};		
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		getNet().createFaces();

		setOutput(n);


		// set labels
		setLabels();

		update();


	}

	private void setLabels()
	{
		getNet().setLabel(null);
		outputPolygonsBottom.setLabels(null);
		outputPolygonsSide.setLabels(null);
		outputPolygonsTop.setLabels(null);

		outputSegmentsBottom.setLabels(null);
		outputSegmentsSide.setLabels(null);
		outputSegmentsTop.setLabels(null);

	}

	private void createNet(int n) {

		GeoPolyhedronNet net = getNet();

		switch(p.getType()) {

		case GeoPolyhedron.TYPE_PYRAMID:
			
			//create bottom face
			outputPointsBottom.adjustOutputSize(n);
			outputPointsSide.adjustOutputSize(n+1);
			outputPointsTop.adjustOutputSize(1);
			net.startNewFace();
			for (int i = 0; i < n; i++){
				net.addPointToCurrentFace(outputPointsBottom.getElement(i));
			}
			net.endCurrentFace();

			//create side faces
			int cut = (int) Math.ceil((outputPointsSide.size())/2);
			for (int i=0; i < cut ; i++){
				net.startNewFace();
				net.addPointToCurrentFace(outputPointsSide.getElement(i));
				net.addPointToCurrentFace(outputPointsSide.getElement((i+1)%n));
				net.addPointToCurrentFace(outputPointsTop.getElement(0));
				net.endCurrentFace();
			}
			for (int i=cut+1; i < n ; i++){
				int j = n-(i-cut)+1;
				net.startNewFace();
				net.addPointToCurrentFace(outputPointsSide.getElement(j));
				net.addPointToCurrentFace(outputPointsSide.getElement(j-1));
				net.addPointToCurrentFace(outputPointsTop.getElement(0));
				net.endCurrentFace();
			} 
			// last side face
			net.startNewFace();
			net.addPointToCurrentFace(outputPointsSide.getElement(cut+1));
			net.addPointToCurrentFace(outputPointsSide.getElement(0));
			net.addPointToCurrentFace(outputPointsTop.getElement(0));
			net.endCurrentFace();
			break;
	/*	case GeoPolyhedron.TYPE_PRISM:

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
*/
		}
	}



	private void setOutput(int n) {

		GeoPolyhedronNet net = getNet();
		Collection<GeoPolygon3D> faces = net.getFacesCollection();
		int step = 1;

		for (GeoPolygon polygon : faces){
			GeoSegmentND[] segments = polygon.getSegments();
			if (step == 1) { //bottom
				outputPolygonsBottom.addOutput((GeoPolygon3D) polygon, false);
				for (int i = 0; i < segments.length; i++) {
					outputSegmentsBottom.addOutput((GeoSegment3D) segments[i],false);	
				}
				step++;
			} else {//sides

				switch(p.getType()) {

				case GeoPolyhedron.TYPE_PYRAMID:
					
					outputPolygonsSide.addOutput((GeoPolygon3D) polygon, false);
					outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2], false);
					outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[1], false);
					outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[0], false);
					step++;
					//Application.debug(outputSegmentsSide.size());

					 
					break;

				case GeoPolyhedron.TYPE_PRISM:
					if (step == n) {
						outputPolygonsTop.addOutput((GeoPolygon3D) polygon, false);
						for (int i=1; i<segments.length; i++) {
							outputSegmentsTop.addOutput((GeoSegment3D) segments[i], false);	
						}
					} else {
						outputPolygonsSide.addOutput((GeoPolygon3D) polygon, false);
						outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[3], false);
						outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2], false);
						outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[1], false);		
						step++;
					}
				}	
			}
		}
		refreshOutput();
	}



	private OutputHandler<GeoPoint3D> createOutputPoints() {
		return new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoPolyhedronNet2.this);
				return p;
			}
		});
	}

	protected OutputHandler<GeoSegment3D> createOutputSegments(){
		return new OutputHandler<GeoSegment3D>(new elementFactory<GeoSegment3D>() {
			public GeoSegment3D newElement() {
				GeoSegment3D s=new GeoSegment3D(cons);
				//s.setParentAlgorithm(AlgoPolyhedron.this);
				return s;
			}
		});
	}

	protected OutputHandler<GeoPolygon3D> createOutputPolygons(){
		return new OutputHandler<GeoPolygon3D>(new elementFactory<GeoPolygon3D>() {
			public GeoPolygon3D newElement() {
				GeoPolygon3D p=new GeoPolygon3D(cons);
				//p.setParentAlgorithm(AlgoPolyhedron.this);
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
	 * @param dist distance between point and projected point
	 * @param test value (XOR)
	 */
	private void rotate(GeoPoint3D point, Coords pointCoords, Coords projectCoords,  Coords o, Coords vs, double f, Coords fd, double dist, boolean test){

		Coords v2 = projectCoords.sub(o);
		double d2 = pointCoords.distLine(o, vs);
		double angle = Math.asin(dist/d2);		

		if (test ^ (v2.crossProduct(vs).dotproduct(fd) < 0)) { // top point is inside bottom face
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
			outputPointsSide.adjustOutputSize(points.length + 1);
			outputPointsSide.setLabels(null);
			outputPointsTop.adjustOutputSize(1);
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
			int cut = (int) Math.ceil((outputPointsSide.size())/2);
			for (int i = 0 ; i < outputPointsSide.size() ; i++) {
				if (i <= cut) {
					GeoPoint3D wpoint = outputPointsSide.getElement(i);
					wpoint.setCoords(points[i].getInhomCoordsInD(3));
				} else {
					GeoPoint3D wpoint = outputPointsSide.getElement(i);
					wpoint.setCoords(points[outputPointsSide.size()-(i-cut)-1].getInhomCoordsInD(3));
				} 
				GeoPoint3D wpoint3 = outputPointsTop.getElement(0);
				wpoint3.setCoords(topCoords);
			}

			//rotate each side point and connected side points -- first part
			for (int i = cut ; i > 1 ; i--) {
				GeoPoint3D wpoint1 = outputPointsSide.getElement(i);
				Coords cCoord = wpoint1.getInhomCoordsInD(3);
				Coords pp1 = cCoord.projectPlane(algo.getSide(i-2).getCoordSys().getMatrixOrthonormal())[0];
				double dist =  pp1.distance(cCoord);
				Coords o = points[i-1].getInhomCoordsInD(3);
				Coords vs = algo.getSide(i-2).getSegments()[1].getDirectionInD3();
				Coords v2 = pp1.sub(o);
				double d2 = cCoord.distLine(o, vs);
				double angle = Math.asin(dist/d2);		
				if (v2.crossProduct(vs).dotproduct(algo.getSide(i-2).getDirectionInD3()) < 0) { // top point is inside bottom face
					angle = Math.PI - angle;
				}
				for (int j = i ; j <= cut ; j++) {
					wpoint1 = outputPointsSide.getElement(j);
					cCoord = wpoint1.getInhomCoordsInD(3);
					wpoint1.rotate(-f * angle, o, vs);	
				}
			}
			// rotate second part
			for (int i = cut+1  ; i < outputPointsSide.size() ; i++) {
				GeoPoint3D wpoint1 = outputPointsSide.getElement(outputPointsSide.size()-(i-cut));
				Coords cCoord = wpoint1.getInhomCoordsInD(3);
				int sideNumber = (i)%(outputPointsSide.size()-1);
				Coords pp1 = cCoord.projectPlane(algo.getSide(sideNumber).getCoordSys().getMatrixOrthonormal())[0];
				double dist =  pp1.distance(cCoord);
				Coords o = points[sideNumber].getInhomCoordsInD(3);
				Coords vs = algo.getSide(sideNumber).getSegments()[2].getDirectionInD3();
				Coords v2 = pp1.sub(o);
				double d2 = cCoord.distLine(o, vs);
				double angle = Math.asin(dist/d2);		
				if ((sideNumber ==0)^(v2.crossProduct(vs).dotproduct(algo.getSide(sideNumber).getDirectionInD3()) > 0)) { // top point is inside bottom face
					angle = Math.PI - angle;
				}
				for (int j = i ; j > cut; j--) {
					wpoint1 = outputPointsSide.getElement(outputPointsSide.size()-(j-cut));
					cCoord = wpoint1.getInhomCoordsInD(3);
					int sgn=1;
					if (i==outputPointsSide.size()-1) {
						sgn=-1;
					}
					wpoint1.rotate(sgn*f * angle, o, vs);	
				}
			}

			//rotate every point (except 2 firsts) like the top point
			// angle between side face and bottom face
			GeoSegmentND seg = bottomSegments[0];
			Coords o = points[0].getInhomCoordsInD(3);
			Coords vs = seg.getDirectionInD3();
			GeoPoint3D wpoint1 = outputPointsTop.getElement(0);
			Coords cCoord = wpoint1.getInhomCoordsInD(3);
			Coords v2 = p1.sub(o);
			double d2 = cCoord.distLine(o, vs);
			double angle = Math.asin(d1/d2);		
			if (v2.crossProduct(vs).dotproduct(faceDirection) < 0) { // top point is inside bottom face
				angle = Math.PI - angle;
			}	
			wpoint1.rotate(f * angle, o, vs);	
			for (int i = 2 ; i <outputPointsSide.size() ; i++) {
				wpoint1 = outputPointsSide.getElement(i);
				cCoord = wpoint1.getInhomCoordsInD(3);
				wpoint1.rotate(f * angle, o, vs);
			}

			break;
			/*
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
		for (int i = 0 ; i < sz - 2 ; i++) {
			wpoint3 = outputPointsTop.getElement(i);
			cCoord = wpoint3.getInhomCoordsInD(3);
			pp1 = cCoord.projectPlane(algo.getSide(0).getCoordSys().getMatrixOrthonormal())[0];
			double dist =  pp1.distance(cCoord);
			rotate(wpoint3, cCoord, pp1, o, vs, f, algo.getSide(0).getDirectionInD3(), dist, true);		
		}
		for (int i = 0 ; i < 2 * sz ; i+=2) {
			// rotate wpoint1
			// angle between side face and bottom face
			o = points[i/2].getInhomCoordsInD(3);
			vs = bottomSegs[i/2].getDirectionInD3();
			wpoint1 = outputPointsSide.getElement(i);
			cCoord = wpoint1.getInhomCoordsInD(3);
			pp1 = cCoord.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
			rotate(wpoint1, cCoord, pp1, o, vs, f, faceDirection, dd1, false);
			// rotate wpoint2	
			wpoint2 = outputPointsSide.getElement(i+1);
			cCoord = wpoint2.getInhomCoordsInD(3);
			pp1 = cCoord.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
			rotate(wpoint2, cCoord, pp1, o, vs, f, faceDirection, dd1, false);

			if (i == 0) { // the rotation for the top face is made with the same angle
				for (int j = 0 ; j < sz - 2 ; j++) {
					wpoint3 = outputPointsTop.getElement(j);
					rotate(wpoint3, cCoord, pp1, o, vs, f, faceDirection, dd1, false);			
				}
			}
		}
		break;
			 */
		}


	}

	@Override
	public GetCommand getClassName() {
		return Commands.PolyhedronNet2;
	}

	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedronNet getNet(){
		return outputNet.getElement(0);
	}

}
