package geogebra.common.kernel.barycentric;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.Traversing.Replacer;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;


/**
 * @author Darko Drakulic
 * @version 23-10-2011
 * 
 *  This class makes a curve in barycentric coordinates 
 *          
 */

public class AlgoTriangleCurve extends AlgoElement implements ExpressionNodeConstants{

	
	private GeoPoint A, B, C; // input
	private GeoImplicitPoly n;	// number of curve
	private GeoElement poly; // output
	private Equation eq;
	private GeoNumeric[] xcoef,ycoef,constant;
	private AlgoDependentImplicitPoly dd;
	/**
	 * @param cons construction
	 * @param label label
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 * @param e equation in A,B,C
	 * @param a variable "A"
	 * @param b variable "B"
	 * @param c variable "C"
	 */
	public AlgoTriangleCurve(Construction cons, String label, GeoPoint A, GeoPoint B,
			GeoPoint C, GeoImplicitPoly e, GeoNumeric a,GeoNumeric b,GeoNumeric c) {
		super(cons);
		this.A = A;
		this.B = B;
		this.C = C;
		this.n = e;
		
		AlgoDependentImplicitPoly d = (AlgoDependentImplicitPoly) n.getParentAlgorithm();
		ExpressionNode lhs = (ExpressionNode)d.getEquation().getLHS().deepCopy(kernel);
		ExpressionNode rhs = (ExpressionNode)d.getEquation().getRHS().deepCopy(kernel);
		ExpressionNode[] abcExp = new ExpressionNode[3];
		GeoDummyVariable x = new GeoDummyVariable(cons,"x");
		GeoDummyVariable y = new GeoDummyVariable(cons,"y");
		
		xcoef = new GeoNumeric[3];
		ycoef = new GeoNumeric[3];
		constant = new GeoNumeric[3];
		
		for(int i=0;i<3;i++){
		xcoef[i] = new GeoNumeric(cons);
		ycoef[i] = new GeoNumeric(cons);
		constant[i] = new GeoNumeric(cons);
				
		abcExp[i] =new ExpressionNode(kernel, 
				new ExpressionNode(kernel,
				new ExpressionNode(kernel,xcoef[i],Operation.MULTIPLY,x),Operation.PLUS,
				new ExpressionNode(kernel,ycoef[i],Operation.MULTIPLY,y)
				),Operation.PLUS,constant[i]);
		}
		
		eq = new Equation(kernel, lhs, rhs);
		eq.traverse(Replacer.getReplacer(a, abcExp[0]));
		eq.traverse(Replacer.getReplacer(b, abcExp[1]));
		eq.traverse(Replacer.getReplacer(c, abcExp[2]));
		
		eq.setForceImplicitPoly();
		eq.initEquation();
		boolean flag = cons.isSuppressLabelsActive();
		App.debug(flag);
		dd = new AlgoDependentImplicitPoly(cons, label, eq);
		cons.removeFromConstructionList(dd);
		poly = dd.getOutput()[0];		
				
		setInputOutput();
		compute();		
		cons.setSuppressLabelCreation(flag);
		poly.setLabel(label);
		
	}

	@Override
	public Commands getClassName() {
		return Commands.TriangleCurve;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = A;
		input[1] = B;
		input[2] = C;
		input[3] = n.toGeoElement();

		setOutputLength(1);
		setOutput(0, poly);
		setDependencies(); // done ycoef[1] AlgoElement
	}
	/**
	 * @return resulting implicit polynomial
	 */
	public GeoElement getResult() {
		return poly;
	}

	@Override
	public final void compute() {
		// Check if the points are aligned
		
		double x1 = -A.inhomX;
		double y1 = -A.inhomY;
		double x2 = -B.inhomX;
		double y2 = -B.inhomY;
		double x3 = -C.inhomX;
		double y3 = -C.inhomY;
		
		
		
		double det = (x2 - x3)*(y1 - y3) + (x3 - x1)*(y2 - y3);
		if(Kernel.isZero(det)){
			poly.setUndefined();
		}
		else{
			ycoef[0].setValue((x3-x2)/det);
			xcoef[0].setValue((y2 - y3)/det);
			constant[0].setValue(((x3-x2)*y3+(y2 - y3)*x3)/det);
			ycoef[1].setValue((x1-x3)/det);
			xcoef[1].setValue((y3 - y1)/det);
			constant[1].setValue(((x1-x3)*y1+(y3 - y1)*x1)/det);
			ycoef[2].setValue((x2-x1)/det);
			xcoef[2].setValue((y1 - y2)/det);
			constant[2].setValue(((x2-x1)*y2+(y1 - y2)*x2)/det);
			dd.update();
			poly.update();
		}
		
	}

	// TODO Consider locusequability

	
	
	
	
}