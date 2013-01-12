package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;

/**
 * @author ggb3D
 *
 */
public class AlgoCylinderAxisRadius extends AlgoQuadric {
	
	
	private GeoLineND axis;
	

	/**
	 * @param c construction
	 */
	public AlgoCylinderAxisRadius(Construction c, String label, GeoLineND axis, NumberValue r) {		
		super(c,(GeoElement) axis,r,new AlgoQuadricComputerCylinder());
		
		this.axis=axis;
		
		setInputOutput(new GeoElement[] {(GeoElement) axis,(GeoElement) r}, new GeoElement[] {getQuadric()});
		compute();
		
		getQuadric().setLabel(label);
	}
	
	
	

	@Override
	public void compute() {
		

		if (!((GeoElement) axis).isDefined()){
			getQuadric().setUndefined();
			return;
		}
		 
		
		Coords o = axis.getPointInD(3, 0);
		Coords d = axis.getPointInD(3, 1).sub(o);
		
		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getQuadric().setUndefined();
			return;
		}
		
		// check number
		double r = getComputer().getNumber(((NumberValue) getNumber()).getDouble());	
		if (Double.isNaN(r)){
			getQuadric().setUndefined();
			return;
		}
		
		
		//compute the quadric
		d.normalize();
		
		getQuadric().setDefined();
		
		getQuadric().setCylinder(o,d,r);
		
	}
	
	

	@Override
	protected Coords getDirection(){
		return axis.getPointInD(3, 1).sub(axis.getPointInD(3, 0));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return app.getPlain("CylinderWithAxisARadiusB",
				((GeoElement) axis).getLabel(tpl),getNumber().getLabel(tpl));

	}
	

	@Override
	public Commands getClassName() {
		return Commands.Cylinder;
	}
	
	

}
