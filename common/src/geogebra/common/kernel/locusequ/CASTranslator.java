/**
 * 
 */
package geogebra.common.kernel.locusequ;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.cas.singularws.SingularWebService;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.locusequ.arith.Equation;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * @author sergio
 * @author zoltan
 * Translates a system to MPReduce or Singular.
 */
public class CASTranslator extends EquationTranslator<StringBuilder> {
	
	private Set<String> varsToEliminate;
	private Kernel kernel;
	
	/**
	 * Constructor
	 * @param kernel the current kernel
	 */
	public CASTranslator(Kernel kernel) {
		this.kernel = kernel;
		this.varsToEliminate = new HashSet<String>();
	}

	/**
	 * List of variables to eliminate
	 * @return comma separated list of variables 
	 */
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
	
	/**
	 * Variables used in this computation
	 * @return comma separated list of variables
	 */
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
		MyDouble md = new MyDouble(kernel, number);
		return new StringBuilder("(").append(md).append(")");
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
		
		String script, result;
		
		// If SingularWS is available and quick enough, let's use it:
		if (App.singularWS != null && App.singularWS.isAvailable() && App.singularWS.isFast()) {
			script = this.createSingularScript(translatedRestrictions);
			App.info("[LocusEqu] input to singular: "+script);
			result = App.singularWS.directCommand(script);
			App.info("[LocusEqu] output from singular: "+result);
			// Comment this to disable computation via SingularWS:
			return getCoefficientsFromSingularResult(result);
		}
		
		App.debug("TEST: " + this.createSingularScript(translatedRestrictions));

		// Falling back to use Reduce/Cali:
		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		script = this.createMPReduceScript(translatedRestrictions);
		App.info("[LocusEqu] input to cas: "+script);
		cas.getCurrentCAS().loadGroebner();
		result = cas.evaluate(script);
		App.info("[LocusEqu] output from cas: "+result);
		return getCoefficientsFromResult(result, cas);
	}

	private static double[][] getCoefficientsFromResult(String rawResult, GeoGebraCAS cas) {
		String result = getResultFromRaw(rawResult);
		App.info("[LocusEqu] to-be-parsed result: "+result);
		
		if("".equals(result.trim())) {
			return new double[][]{{}};
		}
		return simplifyResult(parseResult(result,cas));
	}

	private static double[][] getCoefficientsFromSingularResult(String rawResult) {
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
	
	private static double[][] simplifyResult(double[][] original) {
		if(original[0][0] == 0.0) {
			return original;
		}
		
		int xLength = original.length;
		int yLength = original[0].length;
		double[][] result = new double[xLength][yLength];
		
		// double indepCoeff = original[0][0];
		// result[0][0] = 1.0;
		// We don't simplify the result, but leave it to the user:
		double indepCoeff = 1.0;
		result[0][0] = original[0][0];
		
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

	private static double[][] parseResult(String result, GeoGebraCAS cas) {
		return MPReducePolynomialParser.parsePolynomial(result, cas);
	}

	private static String getResultFromRaw(String rawResult) {
		int index = rawResult.lastIndexOf("{");
		return rawResult.substring(index+1,rawResult.length()-1).trim();
	}

	private String createMPReduceScript(Collection<StringBuilder> restrictions) {
		StringBuilder script = new StringBuilder();
		return script.append("off numval, rounded, roundall, factor$").
				append("algebraic; \n").
				append("load cali; \n").
				append("vars := {").
					append(this.getVars()).
				append("}; \n").
				append("setring(vars, degreeorder vars, revlex); \n").
				append("setideal(m,{").
					append(convertFloatsToRationals(CASTranslator.constructRestrictions(restrictions))).
				append("}); \n").
				append("s := eliminate(m, {").
				    append(this.getVarsToEliminate()).
				append("}) ;\n").
				append("coeff(s,{x,y}); \n").toString();
	}

	private String createSingularScript(Collection<StringBuilder> restrictions) {
		StringBuilder script = new StringBuilder();
		String locusLib = SingularWebService.getLocusLib();
		locusLib = "";

		if (locusLib.length() != 0) {
			script.append("LIB \"" + locusLib + ".lib\";ring r=(0,x,y),(" + this.getVarsToEliminate()).
					append("),dp;").
					append("short=0;ideal I=" + convertFloatsToRationals(CASTranslator.constructRestrictions(restrictions))).
					append(";def Gp=grobcov(I);locus2d(Gp);");
			App.debug(script);
			String result = App.singularWS.directCommand(script.toString());
			App.debug(result);
			script = new StringBuilder();
		}
		
		final String SINGULAR_COEFFS = "0"; // "(real,30)"; // may be "real", but inaccurate for cubic computations
		/**
		 * Singular does not convert rationals automatically into
		 * its internal rationals, i.e. we get an error message if we still try to force it:
		 * 
         * > ring rr=0,(x,y,x2,x1),dp
		 * > ideal m=.48*x1;
         *    ? `.48` is not defined
         *    ? error occurred in or before STDIN line 4: `ideal m=.48*x1;`
         *    ? expected ideal-expression. type 'help ideal;'
		 *
		 * This means that here we can use float point arithmetics, but with higher precision.
		 * This approach is not really symbolic, but fast and accurate enough for every day use.
		 * 
		 * See Singular Online Manual, 3.3.1 "Examples of ring declarations" for more details.
		 * 
		 * Another approach is to convert all floats to rationals as we do it right now.
		 */
		return script.append("ring rr=" + SINGULAR_COEFFS + ",(").
				append(this.getVars()).
				append("),dp;ideal m=").
				// We should not convert floats to rationals if SINGULAR_COEFFS != "0":
				// append(MPReduceTranslator.constructRestrictions(restrictions)).
				append(convertFloatsToRationals(CASTranslator.constructRestrictions(restrictions))).
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

	/**
	 * Converts floats to rationals. Uses some kind of heuristics
	 * since it simply replaces e.g. ".2346" to "2346/10000".
	 * This will also work e.g. for "89.2346" since it will be
	 * changed to "892346/10000". The "." character should not be use
	 * for other purposes, so we naively assume that this holds.

	 * @param input the input expression in floating point format
	 * @return the output expression in rational divisions
	 */
	public static String convertFloatsToRationals(String input) {
		
		/* It was a pain to convert this code to a GWT compliant one.
		 * See http://stackoverflow.com/questions/6323024/gwt-2-1-regex-class-to-parse-freetext
		 * for details.
		 */
		
		StringBuffer output = new StringBuffer();
			
		RegExp re = RegExp.compile("\\.[\\d]+", "g");
		
		int from = 0;
		
		for (MatchResult mr = re.exec(input); mr != null; mr = re.exec(input)) {
			String divisor = "1";
			int length = mr.getGroup(0).length();
			for (int j = 1; j < length; ++j) {
				divisor += "0";
			}
			// Adding the non-matching part from the previous match (or from the start):
			if (from <= mr.getIndex() - 1)
				output.append(input.substring(from, mr.getIndex()));
			// Adding the matching part in replaced form (removing the first "." character):
			output.append(input.substring(mr.getIndex() + 1, mr.getIndex()
					+ length) + "/" + divisor);
			from = mr.getIndex() + length; // Preparing then next "from".
		}
		// Adding tail:
		output.append(input.substring(from, input.length()));
		
		return output.toString();
	}

	
}
