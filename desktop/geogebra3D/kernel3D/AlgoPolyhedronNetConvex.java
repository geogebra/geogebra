package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.App;

import java.util.ArrayList;

public class AlgoPolyhedronNetConvex extends AlgoElement3D {

	protected GeoPolyhedron p ; 
	protected NumberValue v;

	protected OutputHandler<GeoPolyhedronNet> outputNet;

	/** points generated as output  */
	protected OutputHandler<GeoPoint3D> outputPointsNet;
	int pointsCounter = 0; //counter of the current number of points created in the net




	private class SegmentInfo{
		int segmentParent1;
		int segmentParent2;
		int pointIndex1 = -1;
		int pointIndex2 = -1;
	}

	private class PolygonInfoElement{
		int linkSegNumber;
		int rank;
		ArrayList<Integer> pointIndex = new ArrayList<Integer>();
	}


	private ArrayList<ArrayList<Integer>> netMap = new ArrayList<ArrayList<Integer>>();
	private ArrayList<PolygonInfoElement> polygonInfo = new ArrayList<PolygonInfoElement>();
	private ArrayList<ArrayList<Integer>> polygonChildSegsList = new ArrayList<ArrayList<Integer>>(); 

	private ArrayList<SegmentInfo> segmentInfoList = new ArrayList<SegmentInfo>();

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


		input = new GeoElement[] {p, (GeoElement) v};		
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		outputPointsNet = createOutputPoints();

		refreshOutput();

		ArrayList<GeoSegmentND> segmentList = new ArrayList<GeoSegmentND>();

		setSegmentsToFacesLink(p,segmentList);


		
		
		int iBottom = 0; // number of the polygon used as bottom -> may be selected by the user
		
		
		
		makeNetMap(p,iBottom,segmentInfoList);

		createNet(iBottom);
	}



	/**
	 * 
	 * @param p : polyhedron
	 * @param segmentList : list of the segments of the polyhedron, built by this function
	 * @param segmentInfoList : lists the 2 polygons connected to each segment, built by this function
	 * @param polygonChildSegsList : lists the linked segments of each polygon, built... 
	 */
	private void setSegmentsToFacesLink(GeoPolyhedron p, ArrayList<GeoSegmentND> segmentList) {
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
	 * @param firstFaceNumber : number of the polygon used as bottom
	 * @param polygonChildSegsList : list of the linked segments of each polygon
	 * @param segmentParentsList ; list of the 2 polygons linked to each segment
	 * @param netMap : lists the parent polygon of each polygon in the net, built here 
	 * @param polygonInfo : lists the rank of each polygon in the net, the segment to use for the rotation, built here
	 */
	private void makeNetMap(GeoPolyhedron p,int firstFaceNumber, ArrayList<SegmentInfo> segmentParentsList){
		//create the netmap of the polyhedron
		// each polygon is referred to its father number (-1 if it has no father) and then its sons if it has any
		GeoPolygon3D[] polygonList = p.getFaces();
		for (int iP=0 ; iP<polygonList.length ; iP++){
			ArrayList<Integer> linkedPolygonList = new ArrayList<Integer>();
			netMap.add(linkedPolygonList);
			PolygonInfoElement infoElt = new PolygonInfoElement();
			polygonInfo.add(infoElt);
		}



		netMap.get(firstFaceNumber).add(-1); //this one has no parent
		polygonInfo.get(firstFaceNumber).rank = 0;  // rank is 0
		polygonInfo.get(firstFaceNumber).linkSegNumber = -1; // no segment to rotate around

		int foundedFaces = 1;
		int maxRank = 0;
		while (foundedFaces < polygonList.length) {
			maxRank++;	
			for (int iP = 0 ; iP<polygonList.length; iP++){
				if ((netMap.get(iP).size()==1)&(polygonInfo.get(iP).rank)<maxRank) { //if this polygon has been found but is not yet connected to a son
					for (int iSeg : polygonChildSegsList.get(iP)){
						//select the child polygon (parent1 or 2 of the seg)
						int iChildPoly=0;
						if (segmentParentsList.get(iSeg).segmentParent1 == iP) {
							iChildPoly = segmentParentsList.get(iSeg).segmentParent2;
						}else {
							iChildPoly = segmentParentsList.get(iSeg).segmentParent1;
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
		// TODO Auto-generated method stub

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
	protected void createNet(int iBottomFace) {

		GeoPolyhedronNet net = getNet();

		//Number of points needed in the net
		int iNetPoints = 0;
		for (int i=0; i < p.getPolygons().size(); i++ ){
			iNetPoints = iNetPoints+p.getFace(i).getPointsLength();
			if (i != iBottomFace){
				iNetPoints -= 2;
			}
		}
		outputPointsNet.adjustOutputSize(iNetPoints);

		//create bottom face and recursive call for child faces
		createFace(iBottomFace);

		//write the result
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
			for (int segNumber=0;segNumber<currentPolygonSegList.size();segNumber++){	
				polygonInfo.get(faceNumber).pointIndex.add(segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex1);
			}
			// READY TO CREATE A NEW FACE
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
			for (segNumber=0;segNumber<currentPolygonSegList.size();segNumber++){	
				polygonInfo.get(faceNumber).pointIndex.add(segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex1);
			}
			// READY TO CREATE THE FIRST FACE
		
		}
		//recursive call
		for (int childPolygonIndex=1;childPolygonIndex<netMap.get(faceNumber).size();childPolygonIndex++){	
			createFace(netMap.get(faceNumber).get(childPolygonIndex));
		}
	}



	private void createChildFace(GeoPolyhedronNet net, int index, int bottomPointsLength){
		net.startNewFace();
		//net.addPointToCurrentFace(outputPointsNet.getElement(index));
		//net.addPointToCurrentFace(outputPointsNet.getElement((index+1)%bottomPointsLength));
		//net.addPointToCurrentFace(outputPointsNet.getElement(index));
		net.endCurrentFace();
	}


}
