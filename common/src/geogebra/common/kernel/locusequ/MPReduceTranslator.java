/**
 * 
 */
package geogebra.common.kernel.locusequ;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.locusequ.arith.Equation;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sergio
 * Translates a system to MPReduce.
 */
public class MPReduceTranslator extends EquationTranslator<StringBuilder> {
	
	private Set<String> varsToEliminate;
	private Kernel kernel;
	
	public MPReduceTranslator(Kernel kernel) {
		this.kernel = kernel;
		this.varsToEliminate = new HashSet<String>();
	}

	protected String getVarsToEliminate() {
		StringBuilder varsString = new StringBuilder(varsToEliminate.size());
		
		String[] vars = varsToEliminate.toArray(new String[varsToEliminate.size()]);
		
		for(int i = 0; i < vars.length; i++) {
			varsString.append(vars[i]);
			if(i < vars.length-1) {
				varsString.append(",");
			}
		}
		
		return varsString.toString();
	}
	
	protected String getVars() {
		return new StringBuilder("x,y,").append(getVarsToEliminate()).toString();
	}
	
	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#translate(geogebra.common.kernel.locusequ.EquationSystem)
	 */
	@Override
	public Collection<StringBuilder> translate(EquationSystem system) {
		this.setSystem(system);

		List<StringBuilder> restrictions = new ArrayList<StringBuilder>(this.getSystem().getEquations().size());
		
		for(Equation equ : system.getEquations()) {
			restrictions.add(equ.getExpression().translate(this));
		}
		
		return restrictions;
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#getLocus()
	 */
	@Override
	public EquationList getLocus() {
		// FIXME: do it.
		return null;
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#abs(java.lang.Object)
	 */
	@Override
	public StringBuilder abs(StringBuilder value) {
		return new StringBuilder("abs(").append(value).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#sum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public StringBuilder sum(StringBuilder a, StringBuilder b) {
		return new StringBuilder("(").append(a).append("+").append(b).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#diff(java.lang.Object, java.lang.Object)
	 */
	@Override
	public StringBuilder diff(StringBuilder a, StringBuilder b) {
		return new StringBuilder("(").append(a).append("-").append(b).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#product(java.lang.Object, java.lang.Object)
	 */
	@Override
	public StringBuilder product(StringBuilder a, StringBuilder b) {
		return new StringBuilder("(").append(a).append("*").append(b).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#div(java.lang.Object, java.lang.Object)
	 */
	@Override
	public StringBuilder div(StringBuilder num, StringBuilder denom) {
		return new StringBuilder("(").append(num).append("/").append(denom).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#exp(java.lang.Object, long)
	 */
	@Override
	public StringBuilder exp(StringBuilder base, long exp) {
		return new StringBuilder("(").append(base).append("**").append(exp).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#inverse(java.lang.Object)
	 */
	@Override
	public StringBuilder inverse(StringBuilder value) {
		return div(new StringBuilder("1"), value);
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#number(double)
	 */
	@Override
	public StringBuilder number(double number) {
		return new StringBuilder("(").append(number).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#auxiliarSymbolic(int)
	 */
	@Override
	public StringBuilder auxiliarSymbolic(int id) {
		StringBuilder var = new StringBuilder("y").append(id);
		this.varsToEliminate.add(var.toString());
		return new StringBuilder("(").append(var).append(")");
		
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#specialSymbolic(int)
	 */
	@Override
	public StringBuilder specialSymbolic(int id) {
		return new StringBuilder("(").append((id == 1) ? "x" : "y").append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#symbolic(int)
	 */
	@Override
	public StringBuilder symbolic(int id) {
		StringBuilder var = new StringBuilder("x").append(id);
		this.varsToEliminate.add(var.toString());
		return new StringBuilder("(").append(var).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#opposite(java.lang.Object)
	 */
	@Override
	public StringBuilder opposite(StringBuilder value) {
		return new StringBuilder("(-").append(value).append(")");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#sqrt(java.lang.Object)
	 */
	@Override
	public StringBuilder sqrt(StringBuilder value) {
		return new StringBuilder("(sqrt(").append(value).append("))");
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.EquationTranslator#eliminate()
	 */
	@Override
	public double[][] eliminate(Collection<StringBuilder> translatedRestrictions) {
		
		if (App.singularWS != null && App.singularWS.isAvailable()) {
			String script2 = this.createSingularScript(translatedRestrictions);
			App.info("[LocusEqu] input to singular: "+script2);
			String result2 = App.singularWS.directCommand(script2);
			App.info("[LocusEqu] output from singular: "+result2);
			// Ccomment this to disable computation via SingularWS:
			return getCoefficientsFromSingularResult(result2);
		}

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		String script = this.createMPReduceScript(translatedRestrictions);
		App.info("[LocusEqu] input to cas: "+script);
		String result = cas.evaluateMPReduce(script);
		App.info("[LocusEqu] output from cas: "+result);
		return getCoefficientsFromResult(result, cas);
	}

	private double[][] getCoefficientsFromResult(String rawResult, GeoGebraCAS cas) {
		String result = getResultFromRaw(rawResult);
		App.info("[LocusEqu] to-be-parsed result: "+result);
		
		if("".equals(result.trim())) {
			return new double[][]{{}};
		} else {
			return simplifyResult(parseResult(result,cas));
		}
	}

	private double[][] getCoefficientsFromSingularResult(String rawResult) {
		String[] flatData = rawResult.split(",");
		int xLength = Integer.parseInt(flatData[0]);
		int yLength = Integer.parseInt(flatData[1]);
		double[][] result = new double[xLength][yLength];

		int counter = 2;
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				result[x][y] = Double.parseDouble(flatData[counter]);
				// App.debug("[LocusEqu] result[" + x + "," + y + "]=" + result[x][y]);
				++counter;
			}
		}
		
		return result;
		
	}
	
	private double[][] simplifyResult(double[][] original) {
		if(original[0][0] == 0.0) {
			return original;
		}
		
		int xLength = original.length;
		int yLength = original[0].length;
		double[][] result = new double[xLength][yLength];
		
		double indepCoeff = original[0][0];
		
		result[0][0] = 1.0;
		
		for(int y = 1; y < yLength; y++) {
			result[0][y] = original[0][y] / indepCoeff;
		}
		
		for(int x = 1; x < xLength; x++) {
			for(int y = 0; y < yLength; y++) {
				result[x][y] = original[x][y] / indepCoeff;
			}
		}
		
		return result;
	}

	private double[][] parseResult(String result, GeoGebraCAS cas) {
		return MPReducePolynomialParser.parsePolynomial(result, cas);
	}

	private static String getResultFromRaw(String rawResult) {
		int index = rawResult.lastIndexOf("{");
		return rawResult.substring(index+1,rawResult.length()-1).trim();
	}

	private String createMPReduceScript(Collection<StringBuilder> restrictions) {
		StringBuilder script = new StringBuilder();
		return script.append("algebraic; \n").
				append("load cali; \n").
				append("vars := {").
					append(this.getVars()).
				append("}; \n").
				append("setring(vars, degreeorder vars, revlex); \n").
				append("setideal(m,{").
					append(MPReduceTranslator.constructRestrictions(restrictions)).
				append("}); \n").
				append("s := eliminate(m, {").
				    append(this.getVarsToEliminate()).
				append("}) ;\n").
				append("coeff(s,{x,y}); \n").toString();
	}

	private String createSingularScript(Collection<StringBuilder> restrictions) {
		StringBuilder script = new StringBuilder();
		final String SINGULAR_COEFFS = "(real,30)"; // may be "real", but inaccurate for cubic computations
		/**
		 * TODO: Singular does not seem to be able to convert rationals automatically into
		 * its internal rationals, i.e. we get an error message if we still try to force it:
		 * 
         * > ring rr=0,(x,y,x2,x1),dp
		 * > ideal m=.48*x1;
         *    ? `.48` is not defined
         *    ? error occurred in or before STDIN line 4: `ideal m=.48*x1;`
         *    ? expected ideal-expression. type 'help ideal;'
		 *
		 * This means that here we use float point arithmetics, but with higher precision.
		 * This approach is not really symbolic, but fast and accurate enough for every day use.
		 * 
		 * See Singular Online Manual, 3.3.1 "Examples of ring declarations" for more details.
		 */
		return script.append("ring rr=" + SINGULAR_COEFFS + ",(").
				append(this.getVars()).
				append("),dp;ideal m=").
				append(MPReduceTranslator.constructRestrictions(restrictions)).
				append(";ideal m1=eliminate(m,").
				append(this.getVarsToEliminate().replaceAll(",", "*")).
				append(");printf(\"%s,%s,%s\",size(coeffs(m1,x)),size(coeffs(m1,y)),").
				append("coeffs(coeffs(m1,x),y));").toString();
		/**
		 *  Singular will return degree of x (+1), degree of y (+1), and then
		 *  the coefficients as a matrix, in the same format as Sergio collects them
		 *  from Reduce. ;-)
		 *  
		 *  Example: 5,3,0,0,-4.000e+00,0,0,0,4.000e+00,0,1.000e+00,-4.000e+00,0,0,1.000e+00,0,0
 		 *  for x^4 - 4x^3 + x^2*y^2 + 4*x^2- 4*y^2.
 		 *  
 		 *  Unfortunately the "m2=m1[size(m1)]" hack does not work properly in some cases.
 		 *  I should learn why Singular returns multiple polynomials in the elimination ideal
 		 *  in such cases, and how to choose the proper one.
		 */
		
	}
	
	private static String constructRestrictions(Collection<StringBuilder> restrictions) {
		StringBuilder equations = new StringBuilder();
		
		StringBuilder[] rs= restrictions.toArray(new StringBuilder[restrictions.size()]);
		
		for(int i = 0; i < rs.length; i++) {
			equations.append(rs[i]);
			if(i < rs.length - 1) {
				equations.append(" , ");
			}
		}
		
		return equations.toString();
	}

}
