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




	private class SegmentInfo{
		int segmentParent1;
		int segmentParent2;
		boolean isLink = false;
		int pointIndex1 = -1;
		int pointIndex2 = -1;
	}

	private class PolygonInfoElement{
		int linkSegNumber;
		int rank;
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
		//write the result:
		//for (int i=0 ; i<segmentList.size();i++){
		//	App.debug(" "+i+" -> "+segmentParentsList.get(i).segmentParent1+" "+segmentParentsList.get(i).segmentParent2);
		//}
		// create the list of segs for each polygon
		/*
		 for (int iP=0 ; iP<polygonList.length ; iP++){

			ArrayList<Integer> segsList = new ArrayList<Integer>();
			polygonChildSegsList.add(segsList);
		}
		for (int i=0 ; i<segmentParentsList.size(); i++ ) {
			polygonChildSegsList.get(segmentParentsList.get(i).segmentParent1).add(i);
			polygonChildSegsList.get(segmentParentsList.get(i).segmentParent2).add(i);
		}
		 */
		//write the result:
		//App.debug("list of segs :");
		//for (int i=0 ; i<polygonChildSegsList.size();i++){
		//	App.debug(" "+i+" -> "+polygonChildSegsList.get(i));
		//}
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
							segmentParentsList.get(iSeg).isLink = true;
							foundedFaces++;
							// set it as a new iP child
							netMap.get(iP).add(iChildPoly);						}
					}
				}
			}
		}
		//write the result:
		App.debug("netMap :");
		for (int i=0 ; i<netMap.size();i++){
			App.debug(" "+i+" -> "+netMap.get(i));
			App.debug("rank: "+polygonInfo.get(i).rank);
			App.debug("seg: "+polygonInfo.get(i).linkSegNumber);
			App.debug("liste des segs: "+polygonChildSegsList.get(i)) ;
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
		//App.debug("nb points:"+iNetPoints);
		outputPointsNet.adjustOutputSize(iNetPoints);

		//create bottom face
		createFace(iBottomFace);

		//create child faces
		/*
		for (int i=0; i<n; i++){
			createChildFace(net, i, n);
		}
		 */
	}

	private void createFace(int faceNumber) {
		int pointsCounter=0; //only for the App.debut writing
		App.debug("face: "+faceNumber );
		int linkSegNumber = polygonInfo.get(faceNumber).linkSegNumber;
		App.debug("Segment: "+ linkSegNumber);
		ArrayList<Integer> currentPolygonSegList = polygonChildSegsList.get(faceNumber);

		if (linkSegNumber != -1){
			SegmentInfo linkSeg = segmentInfoList.get(linkSegNumber);
			//	App.debug("linkSeg : "+linkSeg.pointIndex1+","+linkSeg.pointIndex1);
			int linkSegIndex;
			for (linkSegIndex = 0 ; linkSegIndex<currentPolygonSegList.size(); linkSegIndex++ ){
				if (currentPolygonSegList.get(linkSegIndex)==linkSegNumber){
					if (linkSegIndex==0) {//seg is the first of the list
						segmentInfoList.get(currentPolygonSegList.get(1)).pointIndex1=segmentInfoList.get(currentPolygonSegList.get(0)).pointIndex2;
						segmentInfoList.get(currentPolygonSegList.get(currentPolygonSegList.size()-1)).pointIndex2=segmentInfoList.get(currentPolygonSegList.get(0)).pointIndex1;
					}
					else {

						segmentInfoList.get(currentPolygonSegList.get((linkSegIndex+1)%currentPolygonSegList.size())).pointIndex1=segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex2;
						segmentInfoList.get(currentPolygonSegList.get(linkSegIndex-1)).pointIndex2=segmentInfoList.get(currentPolygonSegList.get(linkSegIndex)).pointIndex1;
					}
				}
				else{ 
					// TO DO
					// do nothing, segmentinfolist(...) is initialized to -1
					// not so true: points for this seg may have been created for another face
				}
				// second turn -> create needed points
			}
			//Write the result
			for (int segNumber=0;segNumber<segmentInfoList.size();segNumber++){	
				App.debug(segmentInfoList.get(segNumber).pointIndex1+"<->"+segmentInfoList.get(segNumber).pointIndex2);
			}
		}
		else { //bottom face
			App.debug("Face de base: "+faceNumber);
			int segNumber;
			for (segNumber=0;segNumber<currentPolygonSegList.size();segNumber++){
				//create the second point of the segment
				App.debug("Create Point "+pointsCounter);
				segmentInfoList.get(currentPolygonSegList.get(segNumber)).pointIndex2=pointsCounter;
				//notice it is the second point of the precedent segment
				segmentInfoList.get(currentPolygonSegList.get((segNumber+1)%(currentPolygonSegList.size()))).pointIndex1=pointsCounter;
				pointsCounter++;
			}

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
