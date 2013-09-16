package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolver;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.arithmetic.Traversing;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.plugin.Operation;

/**
 * Algo for computing the shortest distance between a point and a 2D object
 * @author bencze
 *
 */
public class AlgoShortestDistancePointObject extends AlgoElement implements DistanceAlgo {
	
	private GeoPoint point;
	private GeoElement object;
	private GeoNumeric distance;
	// for non-polynomial functions
	// the value where we should look for the roots
	private GeoNumeric value;

    /**
     * @param cons Construction
     * @param label output label
     * @param p Point
     * @param o Object
     */
    public AlgoShortestDistancePointObject(Construction cons, String label, GeoPoint p, GeoElement o) {
    	super(cons);
    	initAlgo(label, p, o, null);
    }
    
    /**
     * @param cons Construction
     * @param label output label
     * @param p Point
     * @param o Object
     * @param num Value where we should search for roots
     */
    public AlgoShortestDistancePointObject(Construction cons, String label, 
    		GeoPoint p, GeoElement o, GeoNumeric num) {
    	super(cons);
    	initAlgo(label, p, o, num);
    }
    
    private void initAlgo(String label, 
    		GeoPoint p, GeoElement o, GeoNumeric num) {
    	point = p;
    	object = o;
    	value = num;
    	
    	distance = new GeoNumeric(cons);
    	setInputOutput();
    	
    	if (!o.isGeoFunction()) {
    		AlgoElement algo;
    		if (o.isGeoPoint()) 
    			algo = new AlgoDistancePoints(cons, label, p, (GeoPoint) o);
    		else 
    			algo = new AlgoDistancePointObject(cons, label, p, o);
    		cons.removeFromConstructionList(algo);
    		distance = ((DistanceAlgo) algo).getDistance();
    		setInputOutput();
    		distance.setLabel(label);
    		return;
    	}
    	compute();
    	distance.setLabel(label);
    }

	@Override
	protected void setInputOutput() {
		input = new GeoElement[value != null ? 3 : 2];
		input[0] = point;
		input[1] = object;
		if (value != null) {
			input[2] = value;
		}
		setOnlyOutput(distance);
		setDependencies(); // by AlgoElement
	}

	@Override
	public void compute() {
		if (!object.isDefined() || !point.isDefined()) {
			distance.setUndefined();
			return;
		}
		// Algorithm inspired by 
		// http://bact.mathcircles.org/files/Winter2011/CM2_Posters/TPham_BACTPoster.pdf
		distance.setUndefined();
		GeoFunction fun = (GeoFunction) object;
		Function function = fun.getFunction();
		PolyFunction polyFunction = function.expandToPolyFunction(function.getExpression(), false, true);
		if (polyFunction != null) {
			PolyFunction polyDervi = polyFunction.getDerivative();
			// calculate coeffs for 2*(x - a) + 2(f(x) - b)f'(x) where a and b are the coordinates of point
			// expanding it gives 2x - 2a + 2*f(x)*f'(x) - 2*b*f'(x)
			double[] funCoeffs = polyFunction.getCoeffs();
			double[] derivCoeffs = polyDervi.getCoeffs();
			int n = funCoeffs.length - 1;
			int m = derivCoeffs.length - 1;
			double[] eq = new double[(m + n < 1) ? 2 : m + n + 1];
			// calculate 2*f(x)*f'(x)
			for (int i = 0; i < eq.length; i++) { // c_i
				for (int j = Math.max(0, i - m); j <= Math.min(i, n); j++) { // sum
					eq[i] += 2 * funCoeffs[j] * derivCoeffs[i - j];
				}
			}
			// add -2*b*f'(x)
			for (int i = 0; i <= m; i++) {
				eq[i] += (-2) * point.y * derivCoeffs[i]; 
			}
			// add 2x - 2a
			eq[1] += 2;
			eq[0] -= 2 * point.x;
			// new polynomial coeffs in eq
			// calculate the roots and find the minimum
			EquationSolver solver = new EquationSolver(kernel);
			int nrOfRoots = solver.polynomialRoots(eq, false);
			if (nrOfRoots == 0) {
				distance.setUndefined();
				return;
			}
			double min = distancePointFunctionAt(polyFunction, point, eq[0]);
			for (int i = 1; i < nrOfRoots; i++) {
				min = Math.min(min, distancePointFunctionAt(polyFunction, point, eq[i]));
			}
			distance.setValue(min);
			return;
		}
		// non polynomial case
		FunctionVariable fVar = function.getFunctionVariable();
		Function deriv = function.getDerivative(1);
		// replace derivatives' function variable with functions'
		// we need this, so our new function created below, can be evaluated
		deriv.traverse(Traversing.Replacer.getReplacer(deriv.getFunctionVariable(), fVar));
		// build expression 2*(x - a) + 2(f(x) - b)f'(x) where a and b are the coordinates of point
		ExpressionNode expr = new ExpressionNode(kernel, fVar, Operation.MINUS, new MyDouble(kernel, point.x));
		expr = expr.multiply(2);
		ExpressionNode expr2 = new ExpressionNode(kernel, function.getExpression(), Operation.MINUS, new MyDouble(kernel, point.y));
		expr2 = expr2.multiplyR(deriv.getExpression());
		expr2 = expr2.multiply(2);
		expr = expr.plus(expr2);
		// create algo and remove it from construction
		AlgoRootNewton algoRoot = new AlgoRootNewton(cons);
		cons.removeFromConstructionList(algoRoot);
		// calculate root
		double root = algoRoot.calcRoot(new Function(expr, fVar), value != null ? value.getDouble() : point.x);
		distance.setValue(distancePointFunctionAt(function, point, root));
	}
	
	private static double distancePointFunctionAt(final RealRootFunction fun, final GeoPoint p, double x) {
		// D(x) = sqrt((x - a)^2+(f(x) - b)^2)
		return Math.sqrt(Math.pow((x - p.x), 2) + Math.pow((fun.evaluate(x) - p.y), 2));
	}

	@Override
	public GetCommand getClassName() {
		return Commands.ShortestDistance;
	}
	
	public GeoNumeric getResult() {
		return distance;

	}

	public GeoNumeric getDistance() {
		return getResult();
	}

}
