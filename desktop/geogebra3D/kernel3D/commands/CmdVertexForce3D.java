package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Command to force e.g Corner[n,1] to create a 3D point even if n!=3
 * @author mathieu
 *
 */
public class CmdVertexForce3D extends CmdVertex3D {

	public CmdVertexForce3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected GeoPointND cornerOfDrawingPad(String label, NumberValue number,
			NumberValue ev) {
		
		return cornerOfDrawingPad3D(label, number, ev);
		
	}

	
}
