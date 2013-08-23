package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdVertex;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra3D.kernel3D.AlgoDrawingPadCorner3D;

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
}
