package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.commands.CommandDispatcher;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.gui.layout.panels.EuclidianDockPanel3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;


public class AlgebraProcessor3D extends AlgebraProcessor {
	
	

	public AlgebraProcessor3D(Kernel kernel) {
		super(kernel);
	}
	
	protected CommandDispatcher newCommandDispatcher(Kernel kernel){
		return new CommandDispatcher3D(kernel);
	}
	
	
	
	
	
	/** creates 3D point or 3D vector
	 * @param n
	 * @param evaluate
	 * @return 3D point or 3D vector
	 */	
	protected GeoElement[] processPointVector3D(
			ExpressionNode n,
			ExpressionValue evaluate) {
		String label = n.getLabel();		

		double[] p = ((Vector3DValue) evaluate).getPointAsDouble();

		GeoElement[] ret = new GeoElement[1];
		boolean isIndependent = n.isConstant();

		// make vector, if label begins with lowercase character
		if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by MyXMLHandler
				if (Character.isLowerCase(label.charAt(0)))
					n.setForceVector();
				else
					n.setForcePoint();
			}
		}
		
		boolean isVector = n.isVectorValue();
		
		
		if (isIndependent) {
			// get coords
			double x = p[0];
			double y = p[1];
			double z = p[2];
			if (isVector)
				ret[0] = kernel.getManager3D().Vector3D(label, x, y, z);	
			else
				ret[0] = (GeoPoint3D) kernel.getManager3D().Point3D(label, x, y, z);			
		} else {
			if (isVector)
				ret[0] = kernel.getManager3D().DependentVector3D(label, n);
			else
				ret[0] = (GeoPoint3D) kernel.getManager3D().DependentPoint3D(label, n);
		}

		return ret;
	}

	
	protected void checkNoTermsInZ(Equation equ){
		if (!equ.getNormalForm().isFreeOf('z'))
			equ.setForcePlane();
	}
	
	protected GeoElement[] processLine(Equation equ, boolean inequality) {
		
		if (equ.isForcedLine() || inequality)
			return super.processLine(equ, inequality); //TODO add inequalities in 3D
		else{
			//check if the equ is forced plane or if the 3D view has the focus
			if (equ.isForcedPlane() ||
					app.getGuiManager().getLayout().getDockManager().getFocusedEuclidianPanel() instanceof EuclidianDockPanel3D){
				return processPlane(equ);
			}else
				return super.processLine(equ, inequality);
		}
	}

	protected GeoElement[] processPlane(Equation equ) {
		double a = 0, b = 0, c = 0, d = 0;
		GeoPlane3D plane = null;
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
	
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			// get coefficients            
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("z");
			d = lhs.getCoeffValue("");
			plane = (GeoPlane3D) kernel.getManager3D().Plane3D(label, a, b, c, d);
		} else
			plane = (GeoPlane3D) kernel.getManager3D().DependentPlane3D(label, equ);

		ret[0] = plane;
		return ret;
	}


	
	
	
}
