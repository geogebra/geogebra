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
		for (int i=0;i<outputPointsBottom.size() ;i++)
			outputPointsBottom.getElement(i).setCoords(points[i].getInhomCoordsInD(3));
		
		
		switch(p.getType()){

		case GeoPolyhedron.TYPE_PYRAMID:
			// update top points
			outputPointsTop.adjustOutputSize(points.length);
			outputPointsTop.setLabels(null);
			GeoPolygon bottomPolygon = algo.getBottom();
			GeoPointND topPoint = algo.getTopPoint();

			Coords topCoords = topPoint.getInhomCoordsInD(3);
			Coords p1 = topCoords.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal())[0];
			Coords v1 = p1.sub(topCoords);
			double d1 = p.getOrientedHeight();
			if (d1<0) { // top point below the bottom face : negative rotation
				f*=-1;
				d1*=-1;
			}
			GeoSegmentND[] bottomSegments = bottomPolygon.getSegments();
			for (int i=0;i<outputPointsTop.size() ;i++){
				GeoPoint3D wpoint = outputPointsTop.getElement(i);
				wpoint.setCoords(topPoint);

				// angle between side face and bottom face
				GeoSegmentND si = bottomSegments[i];
				Coords o = points[i].getInhomCoordsInD(3);
				Coords vs = si.getDirectionInD3();
				Coords v2 = p1.sub(o);
				double d2 = topCoords.distLine(o, vs);
				double angle = Math.asin(d1/d2);			
				if (v2.crossProduct(vs).dotproduct(v1)*f > 0){ // top point is inside bottom face
					angle = Math.PI-angle;
				}			
				wpoint.rotate(f*angle, si);
			}
			break;

		case GeoPolyhedron.TYPE_PRISM:
			// update top points
				outputPointsTop.adjustOutputSize(points.length*3);
				outputPointsTop.setLabels(null);
				GeoPolygon bottomPolyg = algo.getBottom();
				GeoPointND topP = algo.getTopPoint();
				
				Coords topCo = topP.getInhomCoordsInD(3);
				Coords pp1 = topCo.projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
				Coords vv1 = pp1.sub(topCo);
				double dd1 = p.getOrientedHeight();
				if (dd1<0) { // top point below the bottom face : negative rotation
					f*=-1;
					dd1*=-1;
				}
				Coords vp = topCo.sub(outputPointsBottom.getElement(0).getInhomCoordsInD(3));
				GeoSegmentND[] bottomSegs = bottomPolyg.getSegments();
				Integer sz = outputPointsBottom.size();
				Coords vectrans = topCo; //silly initialization
				for (int i=0;i<sz ;i++){
					//triple creation of top points
					GeoPoint3D wpoint1 = outputPointsTop.getElement(i);
					GeoPoint3D wpoint2 = outputPointsTop.getElement(i+sz);
					GeoPoint3D wpoint3 = outputPointsTop.getElement(i+2*sz);
					wpoint1.setCoords(outputPointsBottom.getElement(i).getInhomCoordsInD(3).add(vp));
					wpoint2.setCoords(wpoint1.getInhomCoordsInD(3));
					wpoint3.setCoords(wpoint1.getInhomCoordsInD(3));
					// rotate wpoint1
							// angle between side face and bottom face
							GeoSegmentND si1 = bottomSegs[i];
							Coords o = points[i].getInhomCoordsInD(3);
							Coords vs = si1.getDirectionInD3();
							pp1 = wpoint1.getInhomCoordsInD(3).projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
							vv1 = pp1.sub(wpoint1.getInhomCoordsInD(3));
							Coords v2 = pp1.sub(o);
							double d2 = wpoint1.getInhomCoordsInD(3).distLine(o, vs);
							double angle = Math.asin(dd1/d2);			
							if (v2.crossProduct(vs).dotproduct(vv1)*f > 0){ // top point is inside bottom face
								angle = Math.PI-angle;
							}			
							wpoint1.rotate(f*angle, si1);
							if (i==0){ // the translation used for the top face is calculated
								vectrans.set(wpoint1.getInhomCoordsInD(3).sub(topCo)); 
								//App.debug(vectrans);
							}
						
					// rotate wpoint2	(rotate with the precedent bottom segment)
							Integer segNum=i-1;
							if (segNum<0) {
								segNum=sz-1;
							}
							GeoSegmentND si2 = bottomSegs[segNum];	
							// angle between side face and bottom face
							o = points[i].getInhomCoordsInD(3);
							vs = si2.getDirectionInD3();
							pp1 = wpoint2.getInhomCoordsInD(3).projectPlane(bottomPolyg.getCoordSys().getMatrixOrthonormal())[0];
							vv1 = pp1.sub(wpoint2.getInhomCoordsInD(3));
							v2 = pp1.sub(o);
							d2 = wpoint2.getInhomCoordsInD(3).distLine(o, vs);
							angle = Math.asin(dd1/d2);			
							if (v2.crossProduct(vs).dotproduct(vv1)*f > 0){ // top point is inside bottom face
								angle = Math.PI-angle;
							}			
							wpoint2.rotate(f*angle, si2);
						}
					for (int i=0;i<sz ;i++){
					// rotate wpoint3 (rotate pi rad around top segment 0)
						GeoPoint3D wpoint1 = outputPointsTop.getElement(0);
						GeoPoint3D wpoint3 = outputPointsTop.getElement(i+sz*2);
				            if (i<2) { // 2 first points3 do not move around top segment)
								wpoint3.setCoords(outputPointsTop.getElement(i+i*sz).getInhomCoordsInD(3));
				            }
				            else {  // other points are moving around the first seg
				            	Coords vs = bottomSegs[0].getDirectionInD3();
				            	wpoint3.translate(vectrans);
				            	// Warning -> modified rotate(phi,o1,vn) in Public in GeoPoint3D
				            	wpoint3.rotate(f*Math.PI, wpoint1.getInhomCoordsInD(3),vs);             	
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
