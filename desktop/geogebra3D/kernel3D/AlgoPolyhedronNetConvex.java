package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.ArrayList;

public class AlgoPolyhedronNetConvex extends AlgoElement3D {

	protected GeoPolyhedron p ; 
	protected NumberValue v;

	protected OutputHandler<GeoPolyhedronNet> outputNet;

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
		
		input = new GeoElement[] {p, (GeoElement) v};		
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		
		refreshOutput();
		
		
	 
	}

	private class SegmentParents{
		int segmentParent1;
		int segmentParent2;
	}
	
	private void setSegmentsToFacesLink(GeoPolyhedron p) {
		GeoPolygon3D[] polygonList = p.getFaces();
		ArrayList<GeoSegment3D> segmentList = new ArrayList<GeoSegment3D>();
		ArrayList<SegmentParents> segmentParentsList = new ArrayList<SegmentParents>();
		ArrayList<ArrayList<Integer>> polygonChild = new ArrayList<ArrayList<Integer>>();
		for (int iP=0 ; iP<polygonList.length ; iP++){
			GeoPolygon3D thisPolygon = polygonList[iP];
			for (GeoSegmentND thisSegment : thisPolygon.getSegments()) {
				// search for thisSegment in the segment list
				boolean found = false;
				for (int i=0 ; i<segmentList.size() ; i++){
					if (segmentList.get(i) == thisSegment) {
						found = true;
						segmentParentsList.get(i).segmentParent2 = iP;
					}
				}
				if (!found) {
					SegmentParents newSegParent = new SegmentParents();
					newSegParent.segmentParent1 = iP;
					segmentParentsList.add(newSegParent);
				}
			}
		}
	}
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GetCommand getClassName() {
		return Commands.PolyhedronNet;
	}
	
}
