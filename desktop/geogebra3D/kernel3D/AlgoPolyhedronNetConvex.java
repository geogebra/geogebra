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
	
	
	
	
	private class SegmentParents{
		int segmentParent1;
		int segmentParent2;
	}

	private class PolygonInfoElement{
		int linkSegNumber;
		int rank;
		ArrayList<Integer> pointsIndex = new ArrayList<Integer>();
		
	}
	
	
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
		ArrayList<SegmentParents> segmentParentsList = new ArrayList<SegmentParents>();
		ArrayList<ArrayList<Integer>> polygonChildSegsList = new ArrayList<ArrayList<Integer>>();

		setSegmentsToFacesLink(p,segmentList,segmentParentsList,polygonChildSegsList);

		ArrayList<ArrayList<Integer>> netMap = new ArrayList<ArrayList<Integer>>();
		ArrayList<PolygonInfoElement> polygonInfo = new ArrayList<PolygonInfoElement>();
		
		int iBottom = 0; // number of the polygon used as bottom -> may be selected by the user
		makeNetMap(p,iBottom,polygonChildSegsList,segmentParentsList,netMap,polygonInfo);

		createNet(iBottom, polygonInfo);
	}

	

	/**
	 * 
	 * @param p : polyhedron
	 * @param segmentList : list of the segments of the polyhedron, built by this function
	 * @param segmentParentsList : lists the 2 polygons connected to each segment, built by this function
	 * @param polygonChildSegsList : lists the linked segments of each polygon, built... 
	 */
	private void setSegmentsToFacesLink(GeoPolyhedron p, ArrayList<GeoSegmentND> segmentList, ArrayList<SegmentParents> segmentParentsList, ArrayList<ArrayList<Integer>> polygonChildSegsList) {
		GeoPolygon3D[] polygonList = p.getFaces();
		for (int iP=0 ; iP<polygonList.length ; iP++){
			GeoPolygon3D thisPolygon = polygonList[iP];
			for (GeoSegmentND thisSegment : thisPolygon.getSegments()) {
				// search for thisSegment in the segment list
				boolean found = false;
				for (int i=0 ; i<segmentList.size() ; i++){
					if (segmentList.get(i) == thisSegment) {
						found = true;
						//add the second polygon parent to thisSegment
						segmentParentsList.get(i).segmentParent2 = iP;
						break;
					}
				}
				if (!found) {
					//add thisSegment to the segmentList
					segmentList.add(thisSegment);
					//add iP as the first polygon parent to thisSegment
					SegmentParents newSegParent = new SegmentParents();
					newSegParent.segmentParent1 = iP;
					segmentParentsList.add(newSegParent);
				}
			}
		}
		//write the result:
		//for (int i=0 ; i<segmentList.size();i++){
		//	App.debug(" "+i+" -> "+segmentParentsList.get(i).segmentParent1+" "+segmentParentsList.get(i).segmentParent2);
		//}
		// create the list of segs for each polygon
		for (int iP=0 ; iP<polygonList.length ; iP++){
			ArrayList<Integer> segsList = new ArrayList<Integer>();
			polygonChildSegsList.add(segsList);
		}
		for (int i=0 ; i<segmentParentsList.size(); i++ ) {
			polygonChildSegsList.get(segmentParentsList.get(i).segmentParent1).add(i);
			polygonChildSegsList.get(segmentParentsList.get(i).segmentParent2).add(i);
		}
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
	private void makeNetMap(GeoPolyhedron p,int firstFaceNumber,ArrayList<ArrayList<Integer>> polygonChildSegsList, ArrayList<SegmentParents> segmentParentsList,ArrayList<ArrayList<Integer>> netMap,ArrayList<PolygonInfoElement> polygonInfo){
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
		//write the result:
		App.debug("netMap :");
		for (int i=0 ; i<netMap.size();i++){
			App.debug(" "+i+" -> "+netMap.get(i));
			App.debug("rank: "+polygonInfo.get(i).rank);
			App.debug("seg: "+polygonInfo.get(i).linkSegNumber);
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
	protected void createNet(int iBottomFace, ArrayList<PolygonInfoElement> polygonInfo ) {

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
		
		net.startNewFace();
		for (int i = 0; i < p.getFace(iBottomFace).getPointsLength(); i++){
			net.addPointToCurrentFace(outputPointsNet.getElement(i));
			//Add the point number in the list of this polygon
			polygonInfo.get(iBottomFace).pointsIndex.add(i);
		}
		net.endCurrentFace();

		//create child faces
		/*
		for (int i=0; i<n; i++){
			createChildFace(net, i, n);
		}
		*/
	}
	
	private void createChildFace(GeoPolyhedronNet net, int index, int bottomPointsLength){
		net.startNewFace();
		//net.addPointToCurrentFace(outputPointsNet.getElement(index));
		//net.addPointToCurrentFace(outputPointsNet.getElement((index+1)%bottomPointsLength));
		//net.addPointToCurrentFace(outputPointsNet.getElement(index));
		net.endCurrentFace();
	}


}
