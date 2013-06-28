package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.Traversing;
import geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import geogebra.common.kernel.cas.AlgoSimplify;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.kernel.parser.ParserInterface;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.Complex;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for transfer function (see linear time-invariant system) not for all
 * complex function
 * 
 * @author Giuliano
 * 
 */
public class GeoTransferFunction extends GeoElement {

	private boolean isDefined = true;
	private ParserInterface parser;
	private Function originalFunction;
	private Function currentFunction;
	private Traversing t;
	private ExpressionNode exp;
	private int omegaStart = 50;
	private List<Coords> coordsList = new ArrayList<Coords>();
	private GeoVec2D v;
	private boolean nyquist;
	private int startBode = -3;
	private int endBode = 3;
	private GeoFunction geoFunction;
	private double step = 1.01;

	/**
	 * Copy costructor
	 * 
	 * @param gcf
	 *            GeoComplexFunction
	 * 
	 */
	public GeoTransferFunction(GeoTransferFunction gcf) {
		super(gcf.cons);
		set(gcf);
	}

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param num
	 *            list of coefficients of numerator
	 * @param den
	 *            list of coefficients of denominator
	 * @param omega
	 *            value of interval [-omega;omega]
	 * @param step
	 *            step for calculus of function
	 */
	public GeoTransferFunction(Construction c, String label, GeoList num,
			GeoList den, int omega, double step) {
		super(c);
		AlgebraProcessor ap = kernel.getAlgebraProcessor();
		String strFunc = createFunction(num, den);
		AlgoSimplify algo = new AlgoSimplify(cons, label,
				ap.evaluateToFunction(strFunc, true));
		cons.removeFromConstructionList(algo);
		GeoFunction f = (GeoFunction) algo.getResult();
		f.remove();
		geoFunction = new GeoFunction(f);
		originalFunction = geoFunction.getFunction();
		omegaStart = omega;
		parser = kernel.getParser();
		this.nyquist = true;
		this.setEuclidianVisible(true);
		this.step = step;
	}

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param num
	 *            list of coefficients of numerator
	 * @param den
	 *            list of coefficients of denominator
	 * @param startBode
	 *            start value of the exponent of omega
	 * @param endBode
	 *            end value of the exponent of omega
	 */
	public GeoTransferFunction(Construction c, String label, GeoList num,
			GeoList den, int startBode, int endBode) {
		super(c);
		AlgebraProcessor ap = kernel.getAlgebraProcessor();
		String strFunc = createFunction(num, den);
		AlgoSimplify algo = new AlgoSimplify(cons, label,
				ap.evaluateToFunction(strFunc, true));
		cons.removeFromConstructionList(algo);
		GeoFunction f = (GeoFunction) algo.getResult();
		f.remove();
		geoFunction = new GeoFunction(f);
		originalFunction = geoFunction.getFunction();
		parser = kernel.getParser();
		this.endBode = endBode;
		this.startBode = startBode;
		this.nyquist = false;
		this.setEuclidianVisible(true);
	}

	private static String createFunction(GeoList num, GeoList den) {
		StringBuilder sb = new StringBuilder();
		sb.append("G(s)=(");
		int size = num.size();
		int i = 0;
		if (size == 1) {
			sb.append(((GeoNumberValue) num.get(0)).getDouble());
		} else {
			for (; i < size - 2; i++) {
				sb.append("+(" + ((GeoNumberValue) num.get(i)).getDouble()
						+ ")s^" + (size - i - 1));
			}
			sb.append("+(" + ((GeoNumberValue) num.get(size - 2)).getDouble()
					+ ")s");
			sb.append("+(" + ((GeoNumberValue) num.get(size - 1)).getDouble()
					+ ")");
		}
		sb.append(")/(");
		size = den.size();
		if (size == 1) {
			sb.append(((GeoNumberValue) den.get(0)).getDouble());
		} else {
			for (i = 0; i < size - 2; i++) {
				sb.append("+(" + ((GeoNumberValue) den.get(i)).getDouble()
						+ ")s^" + (size - i - 1));
			}
			sb.append("+(" + ((GeoNumberValue) den.get(size - 2)).getDouble()
					+ ")s");
			sb.append("+(" + ((GeoNumberValue) den.get(size - 1)).getDouble()
					+ ")");
		}
		sb.append(")");
		return sb.toString();
	}

	private void evaluateForBode() {
		for (double i = startBode; i <= endBode; i += 0.01) {
			coordsList.add(evaluate(Math.pow(10, i)));
		}
	}

	/**
	 * @return min exponent
	 */
	public int getStartBode() {
		return startBode;
	}

	/**
	 * @return GeoFunction
	 */
	public GeoFunction getGeoFunction() {
		return geoFunction;
	}

	/**
	 * @return max exponent
	 */
	public int getEndBode() {
		return endBode;
	}

	private void evaluateForNyquist() {
		coordsList.clear();
		Coords po = evaluate(omegaStart);
		coordsList.add(po);
		double p = omegaStart / step;
		for (; !Kernel.isEqual(p, 0, 0.01); p /= step) {
			po = evaluate(p);
			coordsList.add(po);
		}
	}

	/**
	 * Calc values of function
	 */
	public void evaluate() {
		if (nyquist) {
			evaluateForNyquist();
		} else {
			evaluateForBode();
		}

	}

	/**
	 * @return list of values
	 */
	public List<Coords> getCoordsList() {
		return coordsList;
	}

	private int getOmega() {
		return omegaStart;
	}

	/**
	 * @return function
	 */
	public Function getFunction() {
		return originalFunction;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CURVE_POLAR;
	}

	/**
	 * @param isDefined
	 */
	public void setDefined(boolean isDefined) {
		this.isDefined = isDefined;
	}

	@Override
	public GeoElement copy() {
		return new GeoTransferFunction(this);
	}

	@Override
	public void set(GeoElement geo) {
		GeoTransferFunction gcf = (GeoTransferFunction) geo;
		originalFunction = gcf.getFunction();
		omegaStart = gcf.getOmega();
		coordsList = gcf.getCoordsList();
		startBode = gcf.getStartBode();
		endBode = gcf.getEndBode();
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	@Override
	public void setUndefined() {
		isDefined = false;
	}

	@Override
	public boolean showInAlgebraView() {
		return nyquist;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined;
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		return false;
	}

	/**
	 * @param z
	 *            value to evaluate
	 * @return function(z)
	 */
	public Coords evaluate(Complex z) {
		try {
			if (Kernel.isEqual(z.getRe(), 0, Kernel.MIN_PRECISION)) {
				return (new Coords(originalFunction.evaluate(0), 0, 1));
			}
			exp = parser.parseExpression(z.toString());
			currentFunction = new Function(originalFunction, kernel);
			t = VariableReplacer.getReplacer(currentFunction
					.getVarString(StringTemplate.defaultTemplate), exp);
			currentFunction.traverse(t);
			v = (GeoVec2D) currentFunction.evaluateComplex().getExpression()
					.evaluate(StringTemplate.defaultTemplate);
			return new Coords(v.getX(), v.getY());
		} catch (ParseException e) {
			e.printStackTrace();
			setUndefined();
		}
		return null;
	}

	/**
	 * @param x
	 *            value of omega (0+j*omega)
	 * @return function(0+j*omega)
	 */
	public Coords evaluate(double x) {
		try {
			if (Kernel.isEqual(x, 0, Kernel.MIN_PRECISION)) {
				return (new Coords(originalFunction.evaluate(0), 0, 1));
			}
			exp = parser.parseExpression("i*" + x);
			currentFunction = new Function(originalFunction, kernel);
			t = VariableReplacer.getReplacer(currentFunction
					.getVarString(StringTemplate.defaultTemplate), exp);
			currentFunction.traverse(t);
			v = (GeoVec2D) currentFunction.evaluateComplex().getExpression()
					.evaluate(StringTemplate.defaultTemplate);
			return new Coords(v.getX(), v.getY(), 1);
		} catch (ParseException e) {
			e.printStackTrace();
			setUndefined();
		} catch (ClassCastException e) {
			setUndefined();
		}
		return null;
	}

	@Override
	public String getLaTeXAlgebraDescription(final boolean substituteNumbers,
			StringTemplate tpl) {
		return toLaTeXString(substituteNumbers, tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return originalFunction.toValueString(tpl);
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (isDefined) {
			StringBuilder sb = new StringBuilder();
			if (kernel.getApplication().isHTML5Applet()) {
				sb.append(originalFunction.toLaTeXString(symbolic, tpl));
				sb.append(" , ");
				sb.append(kernel.format(-omegaStart, tpl));
				sb.append(" \\le ");
				sb.append(Unicode.omega);
				sb.append(" \\le ");
				sb.append(kernel.format(omegaStart, tpl));
			} else {
				sb.append("\\left.");
				sb.append(label + ":  ");
				sb.append(originalFunction.toLaTeXString(symbolic, tpl));
				sb.append("\\right\\} \\; ");
				sb.append(kernel.format(-omegaStart, tpl));
				sb.append(" \\le ");
				sb.append(Unicode.omega);
				sb.append(" \\le ");
				sb.append(kernel.format(omegaStart, tpl));
			}
			return sb.toString();
		}
		return " \\text{" + kernel.getApplication().getPlain("Undefined") + "} ";

	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}
}
