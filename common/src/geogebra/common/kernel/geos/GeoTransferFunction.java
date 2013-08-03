package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.Traversing;
import geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
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
			AlgebraProcessor ap = kernel.getAlgebraProcessor();
			String strFunc = createFunction(num, den);
			GeoFunction f = ap.evaluateToFunction(strFunc, true);
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

	
	private static String createFunction(GeoList num, GeoList den) {
		StringBuilder sb = new StringBuilder();
		sb.append("G(s)=");
		sb.append(createPolynom(num));
		sb.append("/");
		sb.append(createPolynom(den));
		return sb.toString();
	}

	private static String createPolynom(GeoList values) {
		double value;
		ArrayList<Integer> exp=new ArrayList<Integer>();
		String s="";
		int size = values.size();		
		for (int i = 0; i < size; i++) {
			value=((GeoNumberValue) values.get(i)).getDouble();
			if (value>0){
				exp.add(size - i - 1);
				if (value==1){
					s+="+§s^°";
				} else {
					s+="+"+value+"s^°";
				}
			} else {
				if (value<0){
					exp.add(size - i - 1);
					if (value==-1){
						s+="-§s^°";
					} else {
						s+=value+"s^°";
					}
				} 
			}
		}
		int j=0;
		for (int i = 0; i < size; i++) {
			if (s.indexOf('°')!=-1){
				s=s.replaceFirst("°", ""+exp.get(j).intValue());
				j++;
			}			
		}
		s=s.replaceFirst("s\\^1", "s");	
		s=s.replaceFirst("s\\^0", "§");
		s=s.replaceAll("§§", "1");
		s=s.replaceAll("§", "");
		if (s.charAt(s.length()-1)=='+' || s.charAt(s.length()-1)=='-'){
			s=s.substring(0,s.length()-1);
		}
		if (s.charAt(0)=='+'){
			s=s.substring(1);
		}
		s="("+s+")";
		return s;
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
	public void set(GeoElement geo) {
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
		if (isDefined){
			return originalFunction.toValueString(tpl);
		} else {
			return loc.getPlain("Undefined");
		}
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
