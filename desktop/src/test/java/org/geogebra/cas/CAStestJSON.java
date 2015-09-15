package org.geogebra.cas;

import static org.geogebra.test.util.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.cas.logging.CASTestLogger;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.main.AppD;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CAStestJSON {

	  static GeoGebraCasInterface cas;
	  static Kernel kernel;
	  static AppD app;
	  static CASTestLogger logger;
	  static HashMap<String,HashMap<String, String>> testcases = new HashMap<String,HashMap<String,String>>();

	  private static String readFileAsString(String filePath) throws IOException {
	        StringBuffer fileData = new StringBuffer();
	        BufferedReader reader = new BufferedReader(
	                new FileReader(filePath));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	        }
	        reader.close();
		String[] parts = fileData.toString().split("\n");
		StringBuffer noComments = new StringBuffer();
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].trim().startsWith("//")) {
				noComments.append(parts[i]);
			}
		}
		return noComments.toString();
	}
	
	@BeforeClass
	  public static void setupCas () {
		app = new AppD(new CommandLineArguments(new String[] { "--giac" }), new JFrame(), false);

	    // Set language to something else than English to test automatic translation.
	    app.setLanguage(Locale.GERMANY);
	    // app.fillCasCommandDict();

	    kernel = app.getKernel();
	    cas = kernel.getGeoGebraCAS();
	    logger = new CASTestLogger();
	    // Setting the general timeout to 13 seconds. Feel free to change this.
	    kernel.getApplication().getSettings().getCasSettings().setTimeoutMilliseconds(13000);
		
		try {
			Log.debug("CAS: loading testcases");
			String json = readFileAsString("../web/war/__giac.js");
			Log.debug("CAS: parsing testcases");
			Log.debug("CAS: testcases parsed"
					+ json.substring("var __giac = ".length()));
			JSONArray testsJSON = new JSONArray(
					json.substring("var __giac = ".length()));

			int i = 1;
			while (i < testsJSON.length()) {
				
				JSONObject test = testsJSON.getJSONObject(i);
			i++;
			String cat = "general";
			if(test.has("cat")){
				cat = test.getString("cat");
			}
			if(!testcases.containsKey(cat)){
				/*System.out.println("@Test");
				System.out.println("public void test"+cat+"(){");
				System.out.println("	testCat(\""+cat+"\");");
				System.out.println("}\n");*/
				
				testcases.put(cat, new HashMap<String,String>());
			}
			testcases.get(cat).put(test.getString("cmd"),test.getString("result"));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		

	}
	
	private static void ta (boolean tkiontki, String input, String expectedResult, String ... validResults) {
	    String result;

	    try {
	      GeoCasCell f = new GeoCasCell(kernel.getConstruction());
	      kernel.getConstruction().addToConstructionList(f, false);

	      f.setInput(input);

	      if (tkiontki) {
	        f.setEvalCommand("KeepInput");
	      }

	      f.computeOutput();

	      boolean includesNumericCommand = false;
	      HashSet<Command> commands = new HashSet<Command>();

	      f.getInputVE().traverse(CommandCollector.getCollector(commands));

	      if (!commands.isEmpty()) {
	        for (Command cmd : commands) {
	          String cmdName = cmd.getName();
	          // Numeric used
	          includesNumericCommand = includesNumericCommand || ("Numeric".equals(cmdName) && cmd.getArgumentNumber() > 1);
	        }
	      }

	      result = f.getOutputValidExpression() != null ? f.getOutputValidExpression().toString(
	          includesNumericCommand ? StringTemplate.testNumeric : StringTemplate.testTemplateJSON) : f.getOutput(StringTemplate.testTemplate);
	    } catch (Throwable t) {
	      String sts = "";
	      StackTraceElement[] st = t.getStackTrace();

	      for (int i = 0; i < 10 && i < st.length; i++) {
	        StackTraceElement stElement = st[i];
	        sts += stElement.getClassName() + ":" + stElement.getMethodName() + stElement.getLineNumber() + "\n";
	      }

	      result = t.getClass().getName() + ":" + t.getMessage() + sts;
	    }
	    try{
	    assertThat(result, equalToIgnoreWhitespaces(logger, input, expectedResult, validResults));
	    }catch(Throwable t){
	    	if(!(t instanceof AssertionError) ){
	    		t.printStackTrace();
	    	}
	    	Assert.assertEquals(result, expectedResult + " input:"+input);
	    }
	  }
	
	private static void t (String input, String expectedResult) {
		String[] validResults = expectedResult.split("\\|OR\\|");
	    ta(false, input, validResults[0], validResults);
	}
	
	private static void testCat(String name){
		kernel.clearConstruction(true);
		Assert.assertNotNull(testcases.get(name)); 
		for(String cmd:testcases.get(name).keySet()){
			t(cmd, testcases.get(name).get(cmd));
		}
		Assert.assertNotEquals(0, testcases.get(name).size());
	}
	
	
	@Test
	public void testgeneral(){
		testCat("general");
	}

	@Test
	public void testIntegral(){
		testCat("Integral");
	}

	@Test
	public void testFactor(){
		testCat("Factor");
	}

	@Test
	public void testCoefficients(){
		testCat("Coefficients");
	}

	@Test
	public void testAppend(){
		testCat("Append");
	}

	@Test
	public void testBinomialCoefficient(){
		testCat("BinomialCoefficient");
	}

	@Test
	public void testBinomialDist(){
		testCat("BinomialDist");
	}

	@Test
	public void testCauchy(){
		testCat("Cauchy");
	}

	@Test
	public void testCFactor(){
		testCat("CFactor");
	}

	@Test
	public void testNumeric(){
		testCat("Numeric");
	}

	@Test
	public void testCompleteSquare(){
		testCat("CompleteSquare");
	}

	@Test
	public void testCommonDenominator(){
		testCat("CommonDenominator");
	}

	@Test
	public void testCovariance(){
		testCat("Covariance");
	}

	@Test
	public void testCross(){
		testCat("Cross");
	}

	@Test
	public void testComplexRoot(){
		testCat("ComplexRoot");
	}

	@Test
	public void testCSolutions(){
		testCat("CSolutions");
	}

	@Test
	public void testCSolve(){
		testCat("CSolve");
	}

	@Test
	public void testDegree(){
		testCat("Degree");
	}

	@Test
	public void testDenominator(){
		testCat("Denominator");
	}

	@Test
	public void testDerivative(){
		testCat("Derivative");
	}

	@Test
	public void testDeterminant(){
		testCat("Determinant");
	}

	@Test
	public void testDimension(){
		testCat("Dimension");
	}

	@Test
	public void testDiv(){
		testCat("Div");
	}

	@Test
	public void testDivision(){
		testCat("Division");
	}

	@Test
	public void testDivisors(){
		testCat("Divisors");
	}

	@Test
	public void testDivisorsList(){
		testCat("DivisorsList");
	}

	@Test
	public void testDivisorsSum(){
		testCat("DivisorsSum");
	}

	@Test
	public void testDot(){
		testCat("Dot");
	}

	@Test
	public void testElement(){
		testCat("Element");
	}

	@Test
	public void testExpand(){
		testCat("Expand");
	}

	@Test
	public void testExponential(){
		testCat("Exponential");
	}

	@Test
	public void testFactors(){
		testCat("Factors");
	}

	@Test
	public void testFDistribution(){
		testCat("FDistribution");
	}

	@Test
	public void testFlatten(){
		testCat("Flatten");
	}

	@Test
	public void testFirst(){
		testCat("First");
	}

	@Test
	public void testFitExp(){
		testCat("FitExp");
	}

	@Test
	public void testFitLog(){
		testCat("FitLog");
	}

	@Test
	public void testFitPoly(){
		testCat("FitPoly");
	}

	@Test
	public void testFitPow(){
		testCat("FitPow");
	}

	@Test
	public void testGamma(){
		testCat("Gamma");
	}

	@Test
	public void testGCD(){
		testCat("GCD");
	}

	@Test
	public void testHyperGeometric(){
		testCat("HyperGeometric");
	}

	@Test
	public void testIdentity(){
		testCat("Identity");
	}

	@Test
	public void testIf(){
		testCat("If");
	}

	@Test
	public void testImplicitDerivative(){
		testCat("ImplicitDerivative");
	}

	@Test
	public void testIntegralBetween(){
		testCat("IntegralBetween");
	}

	@Test
	public void testIntersect(){
		testCat("Intersect");
	}

	@Test
	public void testIteration(){
		testCat("Iteration");
	}

	@Test
	public void testIterationList(){
		testCat("IterationList");
	}

	@Test
	public void testPointList(){
		testCat("PointList");
	}

	@Test
	public void testRootList(){
		testCat("RootList");
	}

	@Test
	public void testInvert(){
		testCat("Invert");
	}

	@Test
	public void testIsPrime(){
		testCat("IsPrime");
	}

	@Test
	public void testJoin(){
		testCat("Join");
	}

	@Test
	public void testLine(){
		testCat("Line");
	}

	@Test
	public void testLast(){
		testCat("Last");
	}

	@Test
	public void testLCM(){
		testCat("LCM");
	}

	@Test
	public void testLeftSide(){
		testCat("LeftSide");
	}

	@Test
	public void testLength(){
		testCat("Length");
	}

	@Test
	public void testLimit(){
		testCat("Limit");
	}

	@Test
	public void testLimitBelow(){
		testCat("LimitBelow");
	}

	@Test
	public void testLimitAbove(){
		testCat("LimitAbove");
	}

	@Test
	public void testMax(){
		testCat("Max");
	}

	@Test
	public void testMatrixRank(){
		testCat("MatrixRank");
	}

	@Test
	public void testMean(){
		testCat("Mean");
	}

	@Test
	public void testMedian(){
		testCat("Median");
	}

	@Test
	public void testMin(){
		testCat("Min");
	}

	@Test
	public void testMidpoint(){
		testCat("Midpoint");
	}

	@Test
	public void testMod(){
		testCat("Mod");
	}

	@Test
	public void testNextPrime(){
		testCat("NextPrime");
	}

	@Test
	public void testNIntegral(){
		testCat("NIntegral");
	}

	@Test
	public void testNormal(){
		testCat("Normal");
	}

	@Test
	public void testnPr(){
		testCat("nPr");
	}

	@Test
	public void testNSolutions(){
		testCat("NSolutions");
	}

	@Test
	public void testNSolve(){
		testCat("NSolve");
	}

	@Test
	public void testNumerator(){
		testCat("Numerator");
	}

	@Test
	public void testPartialFractions(){
		testCat("PartialFractions");
	}

	@Test
	public void testPerpendicularVector(){
		testCat("PerpendicularVector");
	}

	@Test
	public void testOrthogonalVector(){
		testCat("OrthogonalVector");
	}

	@Test
	public void testPascal(){
		testCat("Pascal");
	}

	@Test
	public void testPoisson(){
		testCat("Poisson");
	}

	@Test
	public void testPreviousPrime(){
		testCat("PreviousPrime");
	}

	@Test
	public void testPrimeFactors(){
		testCat("PrimeFactors");
	}

	@Test
	public void testProduct(){
		testCat("Product");
	}

	@Test
	public void testMixedNumber(){
		testCat("MixedNumber");
	}

	@Test
	public void testRandomBetween(){
		testCat("RandomBetween");
	}

	@Test
	public void testRandomBinomial(){
		testCat("RandomBinomial");
	}

	@Test
	public void testRandomElement(){
		testCat("RandomElement");
	}

	@Test
	public void testRandomPoisson(){
		testCat("RandomPoisson");
	}

	@Test
	public void testRandomNormal(){
		testCat("RandomNormal");
	}

	@Test
	public void testRandomPolynomial(){
		testCat("RandomPolynomial");
	}

	@Test
	public void testRationalize(){
		testCat("Rationalize");
	}

	@Test
	public void testReverse(){
		testCat("Reverse");
	}

	@Test
	public void testRightSide(){
		testCat("RightSide");
	}

	@Test
	public void testRoot(){
		testCat("Root");
	}

	@Test
	public void testReducedRowEchelonForm(){
		testCat("ReducedRowEchelonForm");
	}

	@Test
	public void testSample(){
		testCat("Sample");
	}

	@Test
	public void testSort(){
		testCat("Sort");
	}

	@Test
	public void testSampleVariance(){
		testCat("SampleVariance");
	}

	@Test
	public void testSampleSD(){
		testCat("SampleSD");
	}

	@Test
	public void testSequence(){
		testCat("Sequence");
	}

	@Test
	public void testSD(){
		testCat("SD");
	}

	@Test
	public void testShuffle(){
		//testCat("Shuffle");//TODO
	}

	@Test
	public void testSimplify(){
		testCat("Simplify");
	}

	@Test
	public void testTrigCombine(){
		testCat("TrigCombine");
	}

	@Test
	public void testSolutions(){
		testCat("Solutions");
	}

	@Test
	public void testSolve(){
		testCat("Solve");
	}

	@Test
	public void testSolveODE(){
		testCat("SolveODE");
	}

	@Test
	public void testSubstitute(){
		testCat("Substitute");
	}

	@Test
	public void testSum(){
		testCat("Sum");
	}

	@Test
	public void testTangent(){
		testCat("Tangent");
	}

	@Test
	public void testTake(){
		testCat("Take");
	}

	@Test
	public void testTaylorPolynomial(){
		testCat("TaylorPolynomial");
	}

	@Test
	public void testTDistribution(){
		testCat("TDistribution");
	}

	@Test
	public void testToComplex(){
		testCat("ToComplex");
	}

	@Test
	public void testToExponential(){
		testCat("ToExponential");
	}

	@Test
	public void testToPolar(){
		testCat("ToPolar");
	}

	@Test
	public void testToPoint(){
		testCat("ToPoint");
	}

	@Test
	public void testTranspose(){
		testCat("Transpose");
	}

	@Test
	public void testTrigExpand(){
		testCat("TrigExpand");
	}

	@Test
	public void testTrigSimplify(){
		testCat("TrigSimplify");
	}

	@Test
	public void testUnique(){
		testCat("Unique");
	}

	@Test
	public void testUnitPerpendicularVector(){
		testCat("UnitPerpendicularVector");
	}

	@Test
	public void testUnitVector(){
		testCat("UnitVector");
	}

	@Test
	public void testVariance(){
		testCat("Variance");
	}

	@Test
	public void testWeibull(){
		testCat("Weibull");
	}

	@Test
	public void testZipf(){
		testCat("Zipf");
	}

	@Test
	public void testsin(){
		testCat("sin");
	}

	@Test
	public void testassignment(){
		testCat("assignment");
	}

	@Test
	public void testEvaluate(){
		testCat("Evaluate");
	}

	@Test
	public void testAbs(){
		testCat("Abs");
	}

	@Test
	public void testvecExpr(){
		testCat("vec expr");
	}

	@Test
	public void testChiSquared(){
		testCat("ChiSquared");
	}

	@Test
	public void testxx(){
		testCat("xx");
	}

	@Test
	public void testFractionalPart(){
		testCat("FractionalPart");
	}

	@Test
	public void testDelete(){
		testCat("Delete");
	}

	@Test
	public void testImaginary(){
		testCat("Imaginary");
	}

	@Test
	public void testNRoot(){
		testCat("NRoot");
	}

	@Test
	public void testReal(){
		testCat("Real");
	}

	@Test
	public void testRound(){
		testCat("Round");
	}

	@Test
	public void testFloor(){
		testCat("Floor");
	}

	@Test
	public void testCeil(){
		testCat("Ceil");
	}

	@Test
	public void testlistExpr(){
		testCat("list expr");
	}

	@Test
	public void testexpr(){
		testCat("expr");
	}

	@Test
	public void testtan(){
		testCat("tan");
	}

	@Test
	public void testcot(){
		testCat("cot");
	}

	@Test
	public void testAsymptote(){
		testCat("Asymptote");
	}

	@Test
	public void testconjugate(){
		testCat("conjugate");
	}

	@Test
	public void testtrig(){
		testCat("Trig");
	}

	@Test
	public void testarg(){
		testCat("arg");
	}

	@Test
	public void testln(){
		testCat("ln");
	}

	@Test
	public void testXXSolve(){
		testCat("XXSolve");
	}

	@Test
	public void testXXCFactor(){
		testCat("XXCFactor");
	}

	@Test
	public void testCIFactor(){
		testCat("CIFactor");
	}

	@Test
	public void testIFactor(){
		testCat("IFactor");
	}

	@Test
	public void testCurve(){
		testCat("Curve");
	}

	@Test
	public void testRadius(){
		testCat("Radius");
	}

	@Test
	public void testCenter(){
		testCat("Center");
	}

	@Test
	public void testCircumference(){
		testCat("Circumference");
	}

	@Test
	public void testDistance(){
		testCat("Distance");
	}

	@Test
	public void testAngle(){
		testCat("Angle");
	}

	@Test
	public void testCircle(){
		testCat("Circle");
	}

	@Test
	public void testAngularBisector(){
		testCat("AngularBisector");
	}

	@Test
	public void testLineBisector(){
		testCat("LineBisector");
	}

	@Test
	public void testEllipse(){
		testCat("Ellipse");
	}

	@Test
	public void testConic(){
		testCat("Conic");
	}

	@Test
	public void testHyperbola(){
		testCat("Hyperbola");
	}

	@Test
	public void testIntersection(){
		testCat("Intersection");
	}

	@Test
	public void testUnion(){
		testCat("Union");
	}

	@Test
	public void testsqrt(){
		testCat("sqrt");
	}

	@Test
	public void testexp(){
		testCat("exp");
	}

	@Test
	public void testPolynomial(){
		testCat("Polynomial");
	}

	@Test
	public void testXXXXNSolve(){
		testCat("XXXXNSolve");
	}

	@Test
	public void testabs(){
		testCat("abs");
	}

	@Test
	public void testlength(){
		testCat("length");
	}

	@Test
	public void testXXEvaluate(){
		testCat("XXEvaluate");
	}


	
}
