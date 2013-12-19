package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Collection;

public class AlgoPolyhedronNetConvex extends AlgoElement3D {

	protected GeoPolyhedron p ; 
	protected NumberValue v;
	protected int iBottom = 0; // number of the polygon used as bottom -> may later be selected by the user		


	protected OutputHandler<GeoPolyhedronNet> outputNet;

	/** points generated as output  */
	protected OutputHandler<GeoPoint3D> outputPointsNet;
	int pointsCounter = 0; //counter of the current number of points created in the net


	protected OutputHandler<GeoSegment3D> outputSegments;
	protected OutputHandler<GeoPolygon3D> outputPolygons;



	private class SegmentInfo{
		int segmentParent1;
		int segmentParent2;
		int pointIndex1 = -1;
		int pointIndex2 = -1;
	}

	private class PolygonInfoElement{
		int linkSegNumber;
		int rank;
		int segShift;
		ArrayList<Integer> pointIndex = new ArrayList<Integer>();
	}


	private ArrayList<ArrayList<Integer>> netMap = new ArrayList<ArrayList<Integer>>();
	private ArrayList<PolygonInfoElement> polygonInfo = new ArrayList<PolygonInfoElement>();
	private ArrayList<ArrayList<Integer>> polygonChildSegsList = new ArrayList<ArrayList<Integer>>(); 

	protected ArrayList<GeoSegmentND> segmentList = new ArrayList<GeoSegmentND>();

	protected ArrayList<SegmentInfo> segmentInfoList = new ArrayList<SegmentInfo>();

	/**
	 * @param c construction
	 */
	public AlgoPolyhedronNetConvex(Construction c, String[] labels, GeoPolyhedron p, NumberValue v) {
		super(c);
		this.p = p;
		this.v = v;	



		outputNet=new OutputHandler<GeoPolyhedronNet>(new elementFactory<GeoPolyhedronNet>() {
			public GeoPolyhedronNet newElement() {
				GeoPolyhedronNet p = new GeoPolyhedronNet(cons);
				p.setParentAlgorithm(AlgoPolyhedronNetConvex.this);
				return p;
			}
		});

		outputNet.adjustOutputSize(1);


		outputPointsNet = createOutputPoints();		
		outputPolygons = createOutputPolygons();
		outputSegments = createOutputSegments();

		setSegmentsToFacesLink(p);

		makeNetMap(p);
		createNet();

		// set input
		input = new GeoElement[] {p, (GeoElement) v};		
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		// create faces
		getNet().createFaces();

		GeoPolyhedronNet net = getNet();
		Collection<GeoPolygon3D> faces = net.getFacesCollection();

		for (GeoPolygon polygon : net.getFacesCollection()){
			outputPolygons.addOutput((GeoPolygon3D) polygon, false);
		}

		for (GeoSegment3D segment : net.getSegments3D()){
			outputSegments.addOutput(segment, false);
		}

		refreshOutput();

		// set labels
		setLabels(labels);

		update();

		updateOutputSegmentsAndPolygonsParentAlgorithms();

	}



	/**
	 * 
	 * @param p : polyhedron
	 */
	private void setSegmentsToFacesLink(GeoPolyhedron p) {
		GeoPolygon3D[] polygonList = p.getFaces();
		for (int iP=0 ; iP<polygonList.length ; iP++){
			ArrayList<Integer> segsList = new ArrayList<Integer>();
			GeoPolygon3D thisPolygon = polygonList[iP];
			for (GeoSegmentND thisSegment : thisPolygon.getSegments()) {
				// search for thisSegment in the segment list
				boolean found = false;
				for (int i=0 ; i<segmentList.size() ; i++){
					if (segmentList.get(i) == thisSegment) {
						found = true;
						//add the second polygon parent to thisSegment
						segmentInfoList.get(i).segmentParent2 = iP;
						segsList.add(i);
						break;
					}
				}
				if (!found) {
					//add thisSegment to the segmentList
					segmentList.add(thisSegment);
					//add iP as the first polygon parent to thisSegment
					SegmentInfo newSegParent = new SegmentInfo();
					newSegParent.segmentParent1 = iP;
					segmentInfoList.add(newSegParent);
					segsList.add(segmentInfoList.size()-1);
				}
			}
			polygonChildSegsList.add(segsList);
		}
	}

	/**
	 * 
	 * @param p : polyhedron
	 */
	private void makeNetMap(GeoPolyhedron p){
		//create the netmap of the polyhedron
		// each polygon is referred to its father number (-1 if it has no father) and then its sons if it has any
		GeoPolygon3D[] polygonList = p.getFaces();
		for (int iP=0 ; iP<polygonList.length ; iP++){
			ArrayList<Integer> linkedPolygonList = new ArrayList<Integer>();
			netMap.add(linkedPolygonList);
			PolygonInfoElement infoElt = new PolygonInfoElement();
			polygonInfo.add(infoElt);
		}

		netMap.get(iBottom).add(-1); //this one has no parent
		polygonInfo.get(iBottom).rank = 0;  // rank is 0
		polygonInfo.get(iBottom).linkSegNumber = -1; // no segment to rotate around

		int foundedFaces = 1;
		int maxRank = 0;
		while (foundedFaces < polygonList.length) {
			maxRank++;	
			for (int iP = 0 ; iP<polygonList.length; iP++){
				if ((netMap.get(iP).size()==1)&&(polygonInfo.get(iP).rank)<maxRank) { //if this polygon has been found but is not yet connected to a son
					for (int iSeg : polygonChildSegsList.get(iP)){
						//select the child polygon (parent1 or 2 of the seg)
						int iChildPoly=0;
						if (segmentInfoList.get(iSeg).segmentParent1 == iP) {
							iChildPoly = segmentInfoList.get(iSeg).segmentParent2;
						}else {
							iChildPoly = segmentInfoList.get(iSeg).segmentParent1;
						}
						if (netMap.get(iChildPoly).size()==0){  //if this poly is not yet connected to the net 
							// set its father as iP
							netMap.get(iChildPoly).add(iP);
							polygonInfo.get(iChildPoly).rank = maxRank;  
							polygonInfo.get(iChildPoly).linkSegNumber = iSeg; 
							foundedFaces++;
							// set it as a new iP child
							netMap.get(iP).add(iChildPoly);						}
					}
				}
			}
		}

	}






	@Override
	public void compute() {

		double f = v.getDouble();

		// update net points 
		outputPointsNet.setLabels(null);
		for (int iPoly = 0 ; iPoly<polygonInfo.size(); iPoly++){
			GeoPolygon currentFace = p.getFace(iPoly);
			Coords[] points = getPointsCoords(currentFace);
			int iBegin = 2;
			if (iPoly == iBottom){
				iBegin=0;
			}
			for (int i = iBegin ; i < points.length ; i++) {
				outputPointsNet.getElement(polygonInfo.get(iPoly).pointIndex.get(i)).setCoords(points[(i+polygonInfo.get(iPoly).segShift)%(points.length)]);
			}
		}
		
		//rotate faces by recursive call
		rotateFace(iBottom, f);
	}


	private ArrayList<Integer> rotateFace(int iFace, double f){

		ArrayList<Integer> pointsToRotate = new ArrayList <Integer>();

		//recursive call
		for (int i = 1; i < netMap.get(iFace).size(); i++){
			pointsToRotate.addAll(rotateFace(netMap.get(iFace).get(i), f));
		}
		if (iFace!=iBottom){
			
			//add points index to the list
			for (int index = 2; index<polygonInfo.get(iFace).pointIndex.size(); index++){
				pointsToRotate.add(polygonInfo.get(iFace).pointIndex.get(index));
			}
			
			//rotation angle
			GeoPoint3D facePoint = outputPointsNet.getElement(polygonInfo.get(iFace).pointIndex.get(2));
			Coords cCoord = facePoint.getInhomCoordsInD(3);
			Coords projCoord = cCoord.projectPlane(p.getFace(netMap.get(iFace).get(0)).getCoordSys().getMatrixOrthonormal())[0];
			double dist =  projCoord.distance(cCoord);
			Coords o = (outputPointsNet.getElement(polygonInfo.get(iFace).pointIndex.get(1))).getInhomCoordsInD(3);
			Coords o1 = segmentList.get(polygonInfo.get(iFace).linkSegNumber).getStartPoint().getInhomCoordsInD(3);
			Coords vs = segmentList.get(polygonInfo.get(iFace).linkSegNumber).getDirectionInD3();
			int sgn=1;
			if (o1.distance(o) > 0.01){
				sgn=-1;
			}
			Coords v2 = projCoord.sub(o);
		
			double d2 = cCoord.distLine(o, vs);
			double frac = Math.min(1,dist/d2); //some bugs occur at E-15... dist>d2
			
			double angle = Math.asin(frac);		
			if (((sgn==1)&&(v2.crossProduct(vs).dotproduct(p.getFace(netMap.get(iFace).get(0)).getDirectionInD3()) < 0))|((sgn==-1)&&(v2.crossProduct(vs).dotproduct(p.getFace(netMap.get(iFace).get(0)).getDirectionInD3()) > 0))) { // top point is inside bottom face
				angle =   Math.PI - angle;
			}
			
			// rotate the points of the list
			for (int iPoint = 0 ; iPoint < pointsToRotate.size() ; iPoint++) {
				facePoint = outputPointsNet.getElement(pointsToRotate.get(iPoint));
				facePoint.rotate(f*sgn*angle,o,vs);
			}
		}

		return pointsToRotate;
	}







	private OutputHandler<GeoPoint3D> createOutputPoints() {
		return new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoPolyhedronNetConvex.this);
				getNet().addPointCreated(p);
				return p;
			}
		});
	}

	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedronNet getNet(){
		return outputNet.getElement(0);
	}



	@Override
	public GetCommand getClassName() {
		return Commands.PolyhedronNet;
	}



	/**
	 * adjust output for n points
	 * @param n new points length
	 */
	protected void adjustOutputSize(int n){


		if (n > outputPointsNet.size()){ // augment output points
			outputPointsNet.adjustOutputSize(n);
		}
	}

	//iBottomFace : number of the face used as bottom
	protected void createNet() {

		GeoPolyhedronNet net = getNet();

		//Number of points needed in the net
		int iNetPoints = 0;
		for (int i=0; i < p.getPolygons().size(); i++ ){
			iNetPoints = iNetPoints+p.getFace(i).getPointsLength();
			if (i != iBottom){
				iNetPoints -= 2;
			}
		}
		outputPointsNet.adjustOutputSize(iNetPoints);

		//create bottom face and recursive call for child faces
		createFace(iBottom);

		//create faces
		for (int pNum=0;pNum<polygonInfo.size();pNum++){	
			net.startNewFace();
			for (int i : polygonInfo.get(pNum).pointIndex) {
				net.addPointToCurrentFace(outputPointsNet.getElement(i));
			}
			net.endCurrentFace();
		}
		App.debug("Points list for each polygon");
		for (int pNum=0;pNum<polygonInfo.size();pNum++){	
			App.debug(pNum+": "+polygonInfo.get(pNum).pointIndex);
		}

	}

	private void createFace(int faceNumber) {
		int linkSegNumber = polygonInfo.get(faceNumber).linkSegNumber;
		ArrayList<Integer> currentPolygonSegList = polygonChildSegsList.get(faceNumber);

		if (linkSegNumber != -1){
			SegmentInfo linkSeg = segmentInfoList.get(linkSegNumber);
			int linkSegIndex;
			// -1 until the link segment is found
			for (linkSegIndex = 0 ; (currentPolygonSegList.get(linkSegIndex) != linkSegNumber); linkSegIndex++ ){
				if (currentPolygonSegList.get(linkSegIndex)!=linkSegNumber){
					segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex1=-1;
					segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex2=-1;
				}
			}
			polygonInfo.get(faceNumber).segShift = linkSegIndex;
			// link segment found	//warning: the seg is seen in the reverse order of the parent polygon
			if (linkSegIndex==0) {//seg is the first of the list
				segmentInfoList.get(currentPolygonSegList.get(1)).pointIndex1=segmentInfoList.get(currentPolygonSegList.get(0)).pointIndex1;
				segmentInfoList.get(currentPolygonSegList.get(currentPolygonSegList.size()-1)).pointIndex2=segmentInfoList.get(currentPolygonSegList.get(0)).pointIndex2;
			}
			else {
				segmentInfoList.get(currentPolygonSegList.get((linkSegIndex+1)%currentPolygonSegList.size())).pointIndex1=segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex1;
				segmentInfoList.get(currentPolygonSegList.get(linkSegIndex-1)).pointIndex2=segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex2;
			}
			//reverse the linkseg
			int temp = segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex1;
			segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex1 = segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex2;
			segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex2 = temp;
			// -1 until the end of the list
			for (int linkSegIndex2 = linkSegIndex+1 ; linkSegIndex2<currentPolygonSegList.size(); linkSegIndex2++ ){
				if (linkSegIndex2 > linkSegIndex+1){
					segmentInfoList.get(currentPolygonSegList.get(linkSegIndex2)).pointIndex1=-1;
				}
				if ((linkSegIndex2 < currentPolygonSegList.size()-1)||(linkSegIndex != 0)){
					segmentInfoList.get(currentPolygonSegList.get(linkSegIndex2)).pointIndex2=-1;
				}
			}

			// second turn -> create needed points
			for (int segNumber = 0 ; segNumber<currentPolygonSegList.size(); segNumber++ ){
				if (segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex1 == -1){
					segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex1=pointsCounter;
					//notice it is the second point of the precedent segment
					if (segNumber>0){
						segmentInfoList.get(currentPolygonSegList.get((segNumber-1))).pointIndex2=pointsCounter;
					}
					else {
						segmentInfoList.get(currentPolygonSegList.get((currentPolygonSegList.size()-1))).pointIndex2=pointsCounter;	
					}
					pointsCounter++;			
				}
				if (segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex2 == -1){
					segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex2=pointsCounter;
					//notice it is the first point of the next segment
					segmentInfoList.get(currentPolygonSegList.get((segNumber+1)%(currentPolygonSegList.size()))).pointIndex1=pointsCounter;
					pointsCounter++;			
				}
			}
			//create the pointIndex list for this face
			for (int segNumber=polygonInfo.get(faceNumber).segShift;segNumber<polygonInfo.get(faceNumber).segShift+currentPolygonSegList.size();segNumber++){	
				polygonInfo.get(faceNumber).pointIndex.add(segmentInfoList.get(currentPolygonSegList.get((segNumber)%(currentPolygonSegList.size()))).pointIndex1);
			}

		}
		else { //bottom face
			int segNumber;
			for (segNumber=0;segNumber<currentPolygonSegList.size();segNumber++){
				//create the second point of the segment
				segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex2=pointsCounter;
				//notice it is the second point of the precedent segment
				segmentInfoList.get(currentPolygonSegList.get((segNumber+1)%(currentPolygonSegList.size()))).pointIndex1=pointsCounter;
				pointsCounter++;
			}
			//create the pointIndex list for this face
			for (segNumber=polygonInfo.get(faceNumber).segShift;segNumber<polygonInfo.get(faceNumber).segShift+currentPolygonSegList.size();segNumber++){	
				polygonInfo.get(faceNumber).pointIndex.add(segmentInfoList.get(currentPolygonSegList.get((segNumber)%(currentPolygonSegList.size()))).pointIndex1);
			}

		}
		//recursive call
		for (int childPolygonIndex=1;childPolygonIndex<netMap.get(faceNumber).size();childPolygonIndex++){	
			createFace(netMap.get(faceNumber).get(childPolygonIndex));
		}
	}





	private OutputHandler<GeoSegment3D> createOutputSegments(){
		return new OutputHandler<GeoSegment3D>(new elementFactory<GeoSegment3D>() {
			public GeoSegment3D newElement() {
				GeoSegment3D s=new GeoSegment3D(cons);
				//s.setParentAlgorithm(AlgoPolyhedron.this);
				return s;
			}
		});
	}

	private OutputHandler<GeoPolygon3D> createOutputPolygons(){
		return new OutputHandler<GeoPolygon3D>(new elementFactory<GeoPolygon3D>() {
			public GeoPolygon3D newElement() {
				GeoPolygon3D p=new GeoPolygon3D(cons);
				//p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
	}


	private void setLabels(String[] labels){

		if (labels==null || labels.length <= 1)
			getNet().initLabels(labels);
		else{
			getNet().setAllLabelsAreSet(true);
			for (int i=0; i<labels.length; i++){
				getOutput(i).setLabel(labels[i]);
			}
		}

	}


	/**
	 * force update for segments and polygons at creation
	 */
	private void updateOutputSegmentsAndPolygonsParentAlgorithms(){
		outputSegments.updateParentAlgorithm();
		outputPolygons.updateParentAlgorithm();

	}



	/**
	 * 
	 * @param polygon polygon
	 * @return 3D coords of all points
	 */
	protected static final Coords[] getPointsCoords(GeoPolygon polygon){
		int l = polygon.getPointsLength();
		Coords[] points = new Coords[l];
		for (int i = 0 ; i < l ; i++){
			points[i] = polygon.getPoint3D(i);
		}
		return points;
	}

}