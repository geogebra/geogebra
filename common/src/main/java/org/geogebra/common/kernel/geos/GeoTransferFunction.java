package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.ParserInterface;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.Complex;
import org.geogebra.common.util.Unicode;

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
	private GeoFunction geoFunction;
	private double step = 1.001;

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
			GeoList den, int omega) {
		super(c);
		if (num.getElementType().equals(GeoClass.NUMERIC) && den.getElementType().equals(GeoClass.NUMERIC)){
			omegaStart = omega;
			Function strFunc = createFunction(num, den);
			GeoFunction f = new GeoFunction(c, strFunc);
			geoFunction = new GeoFunction(f);
			originalFunction = geoFunction.getFunction();
			parser = kernel.getParser();
			this.setEuclidianVisible(true);
		} else {
			isDefined=false;
		}
	}

	/**
	 *  for defalut value of omega
	 */
	public GeoTransferFunction(Construction c, String label, GeoList num,
			GeoList den) {
		this(c,label,num,den,10);
	}

	
	private Function createFunction(GeoList num, GeoList den) {
		FunctionVariable s= new FunctionVariable(kernel,"s");
		return new Function(createPolynom(num, s).divide(createPolynom(den,s)),s);
		
	}

	private static ExpressionNode createPolynom(GeoList values, FunctionVariable s) {

		ExpressionNode exs = s.wrap();
		int size = values.size();
		ExpressionNode ret =  ((GeoNumberValue)values.get(values.size()-1)).getNumber().wrap();
		for (int i = 1; i < size; i++) {
			MyDouble coeff = ((GeoNumberValue)values.get(values.size()-1-i)).getNumber();
			ret = exs.power(i).multiply(coeff).plus(ret); 
		}
		return ret;
	}


	/**
	 * @return GeoFunction
	 */
	public GeoFunction getGeoFunction() {
		return geoFunction;
	}

	/**
	 * Calc values of function
	 */
	public void evaluate() {
		coordsList.clear();
		Coords po = evaluate(omegaStart);
		coordsList.add(po);
		double p = omegaStart / step;
		for (; !Kernel.isEqual(p, 0, 0.01); p /= step) {
			po = evaluate(p);
			if (!coordsList.contains(po)){
				coordsList.add(po);
			}
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
	public void set(GeoElementND geo) {
		GeoTransferFunction gcf = (GeoTransferFunction) geo;
		originalFunction = gcf.getFunction();
		omegaStart = gcf.getOmega();
		coordsList = gcf.getCoordsList();
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
		return true;
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
					.getVarString(StringTemplate.defaultTemplate), exp, kernel);
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
			GeoVec2D xi = new GeoVec2D(kernel,0,x);
			xi.setMode(Kernel.COORD_COMPLEX);
			exp = xi.wrap();
			currentFunction = new Function(originalFunction, kernel);
			t = VariableReplacer.getReplacer(currentFunction
					.getVarString(StringTemplate.defaultTemplate), exp, kernel);
			currentFunction.traverse(t);
			v = (GeoVec2D) currentFunction.evaluateComplex().getExpression()
					.evaluate(StringTemplate.defaultTemplate);
			return new Coords(v.getX(), v.getY(), 1);
		} catch (Exception e) {
			e.printStackTrace();
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
		if (isDefined){
			return originalFunction.toValueString(tpl);
		}

		return "?";
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
		return "?";

	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}
	

	
	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}
}
