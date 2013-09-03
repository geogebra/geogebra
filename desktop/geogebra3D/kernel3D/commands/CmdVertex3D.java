package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoVertexConic;
import geogebra.common.kernel.algos.AlgoVertexPolygon;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdVertex;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra3D.kernel3D.AlgoDrawingPadCorner3D;
import geogebra3D.kernel3D.AlgoVertexConic3D;
import geogebra3D.kernel3D.AlgoVertexPolygon3D;

public class CmdVertex3D extends CmdVertex{

	public CmdVertex3D(Kernel kernel) {
		super(kernel);
	}
	
	
	
	@Override
	protected GeoPointND cornerOfDrawingPad(String label, NumberValue number,
			NumberValue ev) {
		
		// Corner[ev, n] : if ev==3, check if loading - then do as <5.0 version (with 2D points)
		if (!kernelA.getLoadingMode() && ev != null && Kernel.isEqual(ev.getDouble() , 3)){
			return cornerOfDrawingPad3D(label, number, ev);
		}
		
		return super.cornerOfDrawingPad(label, number, ev);
	}

	protected GeoPointND cornerOfDrawingPad3D(String label, NumberValue number,
			NumberValue ev) {
		
		AlgoDrawingPadCorner3D algo = new AlgoDrawingPadCorner3D(cons, label,
				number, ev);
		return algo.getCorner();
	}
	
	@Override
	protected AlgoVertexPolygon newAlgoVertexPolygon(Construction cons, String[] labels, GeoPoly p){
		
		if (p.isGeoElement3D()){
			return new AlgoVertexPolygon3D(cons, labels, p);
		}
		
		return new AlgoVertexPolygon(cons, labels, p);
	}
	
	@Override
	protected AlgoVertexPolygon newAlgoVertexPolygon(Construction cons, String label, GeoPoly p, GeoNumberValue v){	

		if (p.isGeoElement3D()){
			return new AlgoVertexPolygon3D(cons, label, p, v);
		}

		return new AlgoVertexPolygon(cons, label, p, v);
	}
	
	
	@Override
	protected AlgoVertexConic newAlgoVertexConic(Construction cons, String[] labels, GeoConicND conic){		
		
		if (conic.isGeoElement3D()){
			return new AlgoVertexConic3D(cons, labels, conic);
		}

		
		return new AlgoVertexConic(cons, labels, conic);
	}
}
