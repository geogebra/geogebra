package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolver;
import geogebra.common.kernel.Kernel;
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
	
	private static final double INTERVAL_START = 30;
	private static final double INTERVAL_GROWTH = 2;
	private static final double MAX_INTERVAL = 10000;
	
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
		Function function = (Function) fun.getFunction().deepCopy(kernel);
		double val = getClosestFunctionValueToPoint(function, point.x, point.y);
		if (Double.isNaN(val)) {
			distance.setUndefined();
			return;
		}
		distance.setValue(distancePointFunctionAt(function, point.x, point.y, val));
	}
	
	private static double distancePointFunctionAt(final RealRootFunction fun, final double px, final double py, double x) {
		// D(x) = sqrt((x - a)^2+(f(x) - b)^2)
		return Math.sqrt(Math.pow((x - px), 2) + Math.pow((fun.evaluate(x) - py), 2));
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
	
	
	/**
	 * Other classes are invited to use this method.
	 * @param function Function
	 * @param x x-coord of point
	 * @param y y-coord of point
	 * @return val such as the point (val, function(val)) is closest to point (x, y)
	 */
	public static final double getClosestFunctionValueToPoint(Function function, double x, double y) {
		// Algorithm inspired by 
		// http://bact.mathcircles.org/files/Winter2011/CM2_Posters/TPham_BACTPoster.pdf
		Kernel kernel = function.getKernel();
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
				eq[i] += (-2) * y * derivCoeffs[i]; 
			}
			// add 2x - 2a
			eq[1] += 2;
			eq[0] -= 2 * x;
			// new polynomial coeffs in eq
			// calculate the roots and find the minimum
			EquationSolver solver = new EquationSolver(kernel);
			int nrOfRoots = solver.polynomialRoots(eq, false);
			if (nrOfRoots == 0) {
				return Double.NaN;
			}
			int k = 0;
			double min = distancePointFunctionAt(polyFunction, x, y, eq[0]);
			for (int i = 1; i < nrOfRoots; i++) {
				double val = distancePointFunctionAt(polyFunction, x, y, eq[i]);
				if (Kernel.isGreater(min, val)) {
					min = val;
					k = i;
				}
			}
			return eq[k];
		}
		// non polynomial case
		FunctionVariable fVar = function.getFunctionVariable();
		Function deriv = function.getDerivative(1);
		// replace derivatives' function variable with functions'
		// we need this, so our new function created below, can be evaluated
		deriv.traverse(Traversing.Replacer.getReplacer(deriv.getFunctionVariable(), fVar));
		// build expression 2*(x - a) + 2(f(x) - b)f'(x) where a and b are the coordinates of point
		ExpressionNode expr = new ExpressionNode(kernel, fVar, Operation.MINUS, new MyDouble(kernel, x));
		expr = expr.multiply(2);
		ExpressionNode expr2 = new ExpressionNode(kernel, function.getExpression(), Operation.MINUS, new MyDouble(kernel, y));
		expr2 = expr2.multiplyR(deriv.getExpression());
		expr2 = expr2.multiply(2);
		expr = expr.plus(expr2);
		// calculate root
		Function func = new Function(expr, fVar);
		GeoFunction geoFunc = new GeoFunction(kernel.getConstruction(), func);
		double[] roots;
		double left = INTERVAL_START;
		double right = INTERVAL_START;
		while ((roots = AlgoRoots.findRoots(geoFunc, x - left, y + right,(int)((left + right) * 10))) == null 
				&& Kernel.isGreater(MAX_INTERVAL, left)) {
			left *= INTERVAL_GROWTH;
			right *= INTERVAL_GROWTH;
		}
		if (roots == null || roots.length == 0) {
			return Double.NaN;
		}
		int k = 0;
		double min = distancePointFunctionAt(function, x, y, roots[0]);
		for (int i = 1; i < roots.length; i++) {
			double val = distancePointFunctionAt(function, x, y, roots[i]);
			if (Kernel.isGreater(min, val)) {
				min = val;
				k = i;
			}
		}
		return roots[k];
	}

}
