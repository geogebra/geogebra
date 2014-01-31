package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Net for a polyhedron
 * @author Vincent
 *
 */
public class GeoPolyhedronNet extends GeoPolyhedron {

	/**
	 * @param c  construction
	 */
	public GeoPolyhedronNet(Construction c) {
		super(c);
	}

	@Override
	public String getTypeString() {
		return "Net";
	}
	
	@Override
	public boolean isGeoPolyhedron() {
		return false;
	}
	
	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(getArea(), tpl));
		return sbToString.toString();
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(getArea()));
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);

	@Override
	final public String toValueString(StringTemplate tpl) {
		return kernel.format(getArea(), tpl);
	}
	
	
	
	private OutputHandler<GeoPolygon3D> algoParentPolygons;
	
	/**
	 * 
	 * @param algoParentPolygons algoParent output polygons
	 */
	public void setAlgoParentPolygons(OutputHandler<GeoPolygon3D> algoParentPolygons){
		this.algoParentPolygons = algoParentPolygons;
	}

	
	@Override
	public GeoPolygon3D createPolygon(GeoPointND[] points, int index) {
		
		if (algoParentPolygons != null && index < algoParentPolygons.size()){ // reuse algoParent output			
			GeoPolygon3D polygon = algoParentPolygons.getElement(index);
			polygon.modifyInputPoints(points); 

			return polygon;
		}

		return super.createPolygon(points, index);
	}
}


