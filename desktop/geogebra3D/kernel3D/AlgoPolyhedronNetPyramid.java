package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

/** Algo that compute the net for a polyhedron
 * @author Vincent
 *
 */
public class AlgoPolyhedronNetPyramid extends AlgoPolyhedronNet {




	/**
	 * @param c construction
	 */
	public AlgoPolyhedronNetPyramid(Construction c, String[] labels, GeoPolyhedron p, NumberValue v) {
		super(c,labels,p,v);

	}

	@Override
	protected int getPointLengthFromLabelsLength(int length){

		return (length-2)/6;
	}


	@Override
	protected void createNet(int n) {

		GeoPolyhedronNet net = getNet();


		//create bottom face
		outputPointsBottom.adjustOutputSize(n);
		outputPointsSide.adjustOutputSize(n);
		net.startNewFace();
		for (int i = 0; i < n; i++){
			net.addPointToCurrentFace(outputPointsBottom.getElement(i));
		}
		net.endCurrentFace();

		//create side faces
		for (int i=0; i<n; i++){
			createSideFace(net, i, n);
		}
	}
	
	private void createSideFace(GeoPolyhedronNet net, int index, int bottomPointsLength){
		net.startNewFace();
		net.addPointToCurrentFace(outputPointsBottom.getElement(index));
		net.addPointToCurrentFace(outputPointsBottom.getElement((index+1)%bottomPointsLength));
		net.addPointToCurrentFace(outputPointsSide.getElement(index));
		net.endCurrentFace();
	}

	@Override
	protected void setOutputSideTop(int n, GeoPolygon3D polygon, int step, GeoSegmentND[] segments){

		setOutputSide(polygon);
	}


	private void setOutputSide(GeoPolygon3D polygon){

		outputPolygonsSide.addOutput(polygon, false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2], false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[1], false);		
	}


	@Override
	protected int adjustOutputSize(int newBottomPointsLength){
		
		int nOld = super.adjustOutputSize(newBottomPointsLength);
		
		if (newBottomPointsLength > nOld){
			// update side points
			outputPointsSide.adjustOutputSize(newBottomPointsLength);
			outputPointsSide.setLabels(null);

			 
			// update bottom segment
			GeoSegmentND segmentBottom = outputSegmentsBottom.getElement(nOld-1);
			segmentBottom.modifyInputPoints(outputPointsBottom.getElement(nOld-1), outputPointsBottom.getElement(nOld));

			// update bottom
			updateBottom(newBottomPointsLength);
			

			//create new sides
			GeoPolyhedronNet net = getNet();
			for (int i = nOld; i < newBottomPointsLength; i++){
				createSideFace(net, i, newBottomPointsLength);
				GeoPolygon3D polygon = net.createPolygon(i+1); // +1 shift since bottom is face #0
				setOutputSide(polygon);
				outputSegmentsBottom.addOutput((GeoSegment3D) polygon.getSegments()[0], false);	// add segment to bottom list now
			}
			outputSegmentsBottom.setLabels(null);	
			outputSegmentsSide.setLabels(null);	
			outputPolygonsSide.setLabels(null);
			
			//update last side
			updateSide(nOld-1, newBottomPointsLength);


			refreshOutput();
			
			
		}
		
		return nOld;
	}
	
	
	private void updateBottom(int newBottomPointsLength){

		GeoPolygon polygon = outputPolygonsBottom.getElement(0);			
		polygon.modifyInputPoints(outputPointsBottom.getOutput(new GeoPoint3D[newBottomPointsLength]));
		polygon.setSegments(outputSegmentsTop.getOutput(new GeoSegment3D[newBottomPointsLength]));
		polygon.calcArea();  
		
	}

	
	private void updateSide(int index, int bottomPointsLength){
		
		GeoPointND pointBottom1 = outputPointsBottom.getElement(index);
		GeoPointND pointBottom2 = outputPointsBottom.getElement((index+1) % bottomPointsLength);
		GeoPointND pointSide = outputPointsSide.getElement(index);
				
		//update segments
		GeoSegmentND segmentBottom = outputSegmentsBottom.getElement(index);
		GeoSegmentND segmentSide1 = outputSegmentsSide.getElement(2*index);
		GeoSegmentND segmentSide2 = outputSegmentsSide.getElement((2*index+1) % (2*bottomPointsLength));
		//segmentBottom.modifyInputPoints(pointBottom1,pointBottom2);	 // use side face order
		segmentSide2.modifyInputPoints(pointBottom2,pointSide);		
		segmentSide1.modifyInputPoints(pointSide,pointBottom1);		
		
		//update side
		GeoPolygon polygon = outputPolygonsSide.getElement(index);
		GeoPointND[] points = new GeoPointND[3];
		points[0] = pointBottom1;
		points[1] = pointBottom2;
		points[2] = pointSide;
		polygon.modifyInputPoints(points);
		GeoSegmentND[] s = new GeoSegmentND[3];
		s[0] = segmentBottom;
		s[1] = segmentSide2;
		s[2] = segmentSide1;
		polygon.setSegments(s);
		polygon.calcArea();  
		
	}
	

	@Override
	public void compute(double f, GeoPolygon bottomPolygon, Coords[] points) {

		

		Coords topCoords = p.getTopPoint();
		Coords p1 = topCoords.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
		double d1 = p.getOrientedHeight();

		Coords faceDirection = bottomPolygon.getDirectionInD3(); 
		if (d1 < 0) { // top point below the bottom face : negative rotation
			f *= -1;
			d1 *= -1;
		}
		

		int n = outputPointsSide.size();
		Coords o2 = points[0];
		
		for (int i = 0 ; i < n ; i++) {
			GeoPoint3D wpoint = outputPointsSide.getElement(i);
			wpoint.setCoords(topCoords, false);
			
			// angle between side face and bottom face
			Coords o = o2;
			o2 = points[(i+1) % n];
			Coords vs = o2.sub(o).normalized();
			rotate(wpoint, topCoords, p1, o, vs, f, faceDirection, d1, false);
		}



	}



}
