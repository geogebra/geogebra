package geogebra.common.cas.giac;

import java.util.Map;
import java.util.TreeMap;

/***
 * # Command translation table from GeoGebra to giac # e.g. Factor[ 2(x+3) ]
 * is translated to factor( 2*(x+3) ) ###
 */

public class Ggb2giac {
	private static Map<String, String> commandMap = new TreeMap<String, String>();

	/**
	 * @param signature GeoGebra command signature (i.e. "Element.2")
	 * @param casSyntax CAS syntax, parameters as %0,%1
	 */
	public static void p(String signature, String casSyntax) {
		commandMap.put(signature, casSyntax);
	}

	/**
	 * @return map signature => syntax
	 */
	public static Map<String,String> getMap() {
		p("Append.2",
				"append(%0,%1)");
		p("Binomial.2",
				"binomial(%0,%1)");
		p("BinomialDist.4",
				"if %3=true then binomial\\_cdf(%0,%1,%2) else binomial(%0,%1,%2) fi");
		p("Cauchy.3", "1/2+1/pi*atan(((%2)-(%1))/(%0))");
		p("CFactor.1","collect(%0,i)");
		p("CFactor.2","collect(%0,i)");
		p("ChiSquared.2", "chisquare\\_cdf(%0,%1)");
		
		// TODO: ggbtmpvarx
		p("Coefficients.1",
				"coeffs(%0,ggbtmpvarx)");
		
		p("Coefficients.2", "coeffs(%0,%1)");
		p("CompleteSquare.1",
				"canonical\\_form(%0,ggbtmpvarx)");
		p("CommonDenominator.2", "lcm(denom(%0),denom(%1))");
		p("Covariance.2",
				"covariance(%0,%1)");
		p("Covariance.1",
				"covariance(%0)");
		p("Cross.2", "cross([%0],[%1])");
		p("ComplexRoot.1", "csolve(%0, ggbtmpvarx)");
		p("CSolutions.1", "csolve(%0)");
		p("CSolutions.2",
				"csolve(%0,%1)");
		p("CSolve.1",
				"csolve(%0)");
		p("CSolve.2", "csolve(%0,%1)");
		p("Degree.1",
				"degree(%0,ggbtmpvarx)");
		p("Degree.2", "degree(%0,%1)");
		p("Denominator.1", "denom(%0)");
		// TODO: diff(t^2) gives 0 not 2*t
		p("Derivative.1",
				"diff(%0, ggbtmpvarx)");
		p("Derivative.2", 
				"diff(%0,%1)");
		p("Derivative.3", 
				"diff(%0,%1,%2)");
		p("Determinant.1", "det(%0)");
		p("Dimension.1", "dim(%0)");
		p("Div.2",
				"if type(%0)==DOM\\_INT && type(%1)==DOM\\_INT then iquo(%0,%1) else quo(%0,%1) fi");
		p("Division.2",
				"if type(%0)==DOM\\_INT && type(%1)==DOM\\_INT then iquorem(%0,%1) else quorem(%0,%1) fi");
		p("Divisors.1",
				"dim(idivis(%0))");
		p("DivisorsList.1",
				"idivis(%0)");
		p("DivisorsSum.1",
				"sum(idivis(%0))");
		p("Dot.2", "dot([%0],[%1])");
		// GeoGebra indexes lists from 1, giac from 0
		p("Element.2", "%0[%1-1]");
		// GeoGebra indexes lists from 1, giac from 0
		p("Element.3",
				"%0[%1 - 1,%2 - 1]");
		p("Expand.1",
				"expand(%0)");
		p("Exponential.2", "1-exp(-(%0)*(%1))");
		
		// factor over rationals
		p("Factor.1",
				"collect(%0)");
		// TODO: %1
		p("Factor.2",
				"collect(%0)");

		// factor over irrationals
		// might not need with_sqrt() as we're using collect() for Factor.1
		//p("RFactor.1","with_sqrt(1);factor(%0);with_sqrt(0);");
		
		// convert {x-1,1,x+1,1} to {{x-1,1},{x+1,1}}
		p("Factors.1",
				//"factors(%0)");
				"[[ggbans:=factors(%0)],matrix(dim(ggbans),2,ggbans)][1]");
		p("FDistribution.3",
				"fisher\\_cdf(%0,%1,%2)");
		p("Flatten.1", "flatten(%0)");
		p("First.1", "{%0[0]}");
		p("First.2",
				"todo");

		// These implementations follow the one in GeoGebra
		p("FitExp.1",
				"[[ggbans:=exponential\\_regression(%0)],(ggbans[1])*exp(ln(ggbans[0])*ggbtmpvarx)][1]");
		p("FitLog.1",
				"[[ggbans:=logarithmic\\_regression(%0)],(ggbans[0])*ln(ggbtmpvarx)+ggbans[1]][1]");
		p("FitPoly.2",
				"polynomial\\_regression(%0,%1)");
		p("FitPow.1",
				"[[ggbans:=power\\_regression(%0)],(ggbans[1])*ggbtmpvarx^(ggbans[0])][1]");

		p("Gamma.3", "igamma((%0),(%2)/(%1))");
		p("GCD.2",
				"gcd(%0,%1)");
		p("GCD.1",
				"lgcd(%0)");
		// GetPrecision.1
		p("Groebner.1", "groebner(%0,indets(%0)))");
		p("Groebner.2", "groebner(%0,%1)");
		p("Groebner.3",
				"groebner(%0,%1,%2)");
		p("HyperGeometric.5",
				"todo");
		p("Identity.1", "identity(round(%0))");
		p("If.2", "when(%0,%1,undef)");
		p("If.3", "when(%0,%1,%2)");
		
		p("ImplicitDerivative.3", "-df(%0,%2)/df(%0,%1)");
		p("ImplicitDerivative.1", "-df(%0,ggbtmpvarx)/df(%0,ggbtmpvary)");
		
		// TODO: arbconst(1) always goes to c_1
		p("Integral.1",
				"integrate(%0,ggbtmpvarx,arbconst(1))");
		// TODO: arbconst(1) always goes to c_1
		p("Integral.2",
				"integrate(%0,%1,arbconst(1))");
		
		// TODO: deal with ggbtmpvarx
		p("Integral.3",
				"integrate(%0,ggbtmpvarx,%1,%2)");
		
		p("Integral.4",
				"integrate(%0,%1,%2,%3)");
		p("IntegralBetween.4",
				"int(%0-(%1),ggbtmpvarx,%2,%3)");
		p("IntegralBetween.5",
				"romberg(%0-(%1),%2,%3,%4)");
		p("Intersect.2",
				"%0 intersect %1");
		p("Iteration.3",
				"(unapply(%0,ggbtmpvarx)@@%2)(%1)");
		p("IterationList.3",
				"apply(unapply(%0,ggbtmpvarx)@@%2,%1)");
		p("PointList.1",
				"coordinates(%0)");
		p("RootList.1",
				"rootlist(mattolistoflists(%0))");
		p("Invert.1", "inv(%0)");
		p("IsPrime.1", "isprime(%0)");
		//p("Join.N","<<begin scalar list!!=list(%); if length(list!!)=1 then list!!:=part(list!!,0); return for each x!! in list!! join x!! end>>");
		p("Join.N","todo");
		p("Line.2","equation(line(%0,%1))");
		// p("IsBound.1","if << symbolic; p!!:=isbound!!('%0); algebraic; p!!>>=1 then 'true else 'false");
		p("Last.1",
				"{%0[dim(%0)-1]}");
		p("Last.2",
				"todo()");
		p("LCM.1",
				"lcm(%0)");
		p("LCM.2",
				"lcm(%0,%1)");
		p("LeftSide.1",
				"left(%0)");
		p("LeftSide.2",
				"todo");
		p("Length.1",
				"size(%0)");
		p("Length.3",
				"arclen(%0,%1,%2)");
		p("Length.4", "arclen(%0,%1,%2,%3)");
		p("Limit.2",
				"limit(%0,ggbtmpvarx,%1)");
		p("Limit.3",
				"limit(%0,%1,%2)");
		p("LimitAbove.2",
				"limit(%0,ggbtmpvarx,%1,1)");
		p("LimitAbove.3", 
				"limit(%0,%1,%2,1)");
		p("LimitBelow.2",
				"limit(%0,ggbtmpvarx,%1,-1)");
		p("LimitBelow.3", "limit(%0,%1,%2,-1)");
		p("Max.N", "max(%)");
		p("MatrixRank.1", "rank(%0)");
		p("Mean.1",
				"mean(%0)");
		p("Median.1",
				"median(%0)");
		p("Min.N", "min(%)");
		p("Midpoint.2", "midpoint(%0,%1)");
		p("Mod.2", "(%0)%(%1)");
		p("NextPrime.1", "nextprime(%0)");
		p("NIntegral.3",
				"romberg(%0,%1,%2)");
		p("NIntegral.4",
				"romberg(%0,%1,%2,%3)");
		p("Normal.3",
				"normald\\_cdf(%0,%1,%2)");
		p("Normal.4",
				"if %3=true then normald\\_cdf(%0,%1,%2) else (1/sqrt(2*pi*(%1^2))) * exp(-((%2-%0)^2) / (2*(%1^2))) fi");
		p("nPr.2", "perm(%0,%1)");
		// first element of list, wrapped back in list
		p("NSolve.1",
				"{ggbtmpvarx=fsolve(%0,ggbtmpvarx)[0]}");
		p("NSolve.2",
				"{left(%1)=fsolve(%0,%1)}");
		// first element of list, wrapped back in list
		p("NSolutions.1",
				"{fsolve(%0,ggbtmpvarx)[0]}");
		p("NSolutions.2",
				"{fsolve(%0,%1)}");
		p("Numerator.1", "numer(%0)");
		p("Numeric.1",
				"todo");
		p("Numeric.2",
				"todo");
		p("OrthogonalVector.1",
				"[[0,-1],[1,0]]*(%0)");
		//using sub twice in opposite directions seems to fix #2198, though it's sort of magic
		// with_sqrt(0) to factor over rationals
		p("PartialFractions.1",
				"with\\_sqrt(0);partfrac(%0)");
		p("PartialFractions.2", "with\\_sqrt(0);partfrac(%0,%1)");
		p("Pascal.4",
				"if %3=true then ibeta(%0,1+floor(%2),%1) else (1-(%1))^(%2)*(%1)^(%0)*binomial(%0+%2-1,%0-1) fi");
		p("Poisson.3",
				"if %2=true then " +
				"exp(-(%0))*sum ((%0)^k/factorial(floor(k)),k,0,floor(%1)) " +
				"else (%0)^(%1)/factorial(floor(%1))*exp(-%0) fi");
		p("PreviousPrime.1",
				"prevprime(%0)");
		p("PrimeFactors.1",
				"ifactors(%0)");
		p("Product.1",
				"product(%0)");
		p("Product.4", "product(%0,%1,%2,%3)");
		// p("Prog.1","<<%0>>");
		// p("Prog.2","<<begin scalar %0; return %1 end>>");
		p("MixedNumber.1",
				"todo");
		p("Random.2", "rand(%0,%1)");
		p("RandomBinomial.2",
				"todo");
		p("RandomElement.1", "rand(1,%0)[0]");
		p("RandomPoisson.1",
				"todo");
		p("RandomNormal.2",
				"randnorm(%0,%1)");
		p("RandomPolynomial.3",
				"randpoly(%0,%1,%2)");
		p("RandomPolynomial.4",
				"randpoly(%0,%1,%2..%3)");
		p("Rationalize.1", "exact(%0)");
		p("Reverse.1","revlist(%0)");
		p("RightSide.1",
				"right(%0)");
		p("RightSide.2",
				"todo");
		p("Root.1",
				"solve(%0,ggbtmpvarx)");
		p("ReducedRowEchelonForm.1",
				"rref(%0)");
		p("Sample.2",
				"seq(rand(1,%0),j,1,%1)");
		p("Sample.3",
				"if %3=true then seq(rand(1,%0),j,1,%1) else todo fi");
		p("SampleVariance.1",
				"todo");
		p("SampleSD.1",
				"todo");
		p("Sequence.1", "seq(j,j,1,%0)");
		p("Sequence.4",
				"seq(%0,%1,%2,%3)");
		p("Sequence.5",
				"seq(%0,%1,%2,%3,%4)");	
		p("SD.1",
				"stddev(%0)");
		p("Shuffle.1", "randperm(%0)");
		p("Simplify.1", "simplify(%0)");
		// p("SimplifyFull.1","trigsimp(%0, combine)");
		
		// solve({x+y-1,x-y-3},{x,y}) -> list[{2,-1}]
		
		p("Solutions.1",
				"solve(%0,ggbtmpvarx)");
		p("Solutions.2",
				"solve(%0,%1)");
		p("Solve.1",
				"solve(%0,ggbtmpvarx)");
		p("Solve.2",
				"solve(%0,%1)");
		p("SolveODE.1",
				"odesolve(%0)");
		p("SolveODE.2",
				"odesolve(%0,%1)");
		//@ is a hack: only use the value if it does not contain () to avoid (1,2)' in CAS
		p("SolveODE.3",
				"odesolve(%0,%1,%2)");
		p("SolveODE.5",//SolveODE[y''=x,y,x,A,{B}]
				"odesolve(%0,%1,%2,%3,%4)");
		p("Substitute.2","todo");
		p("Substitute.3",
				"subst(%0,%1,%2))");
		// p("SubstituteParallel.2","if hold!!=0 then sub(%1,%0) else sub(%1,!*hold(%0))");
		p("Sum.1",
				"sum(%0)");
		p("Sum.4",
				"sum(%0,%1,%2,%3)");
		p("Tangent.2",
				"tangent(%0,%1)");
		p("Take.3",
				"todo");
		p("TaylorSeries.3",
				"series(%0,ggbtmpvarx,%1,%2)");
		p("TaylorSeries.4",
				"series(%0,%1,%2,%3)");
		p("TDistribution.2",
				"student(%0,%1)");
		p("ToComplex.1",
				"%0[0]+i*%0[1]");
		p("ToExponential.1",
				"trig2exp(%0)");
		p("ToPolar.1",
				"exp2trig(%0)");
		p("ToPoint.1",
				"todo");
		p("Transpose.1", "transpose(%0)");
		// http://reduce-algebra.com/docs/trigsimp.pdf
		p("TrigExpand.1",
				"trigexpand(%0)");
		p("TrigExpand.2",
				"trigexpand(%0)");
		p("TrigExpand.3",
				"trigexpand(%0)");
		p("TrigExpand.4",
				"trigexpand(%0)");
		p("TrigSimplify.1",
				"simplify(%0)");
		p("TrigCombine.1",
				"tcollect(%0)");
		p("TrigCombine.2",
				"tcollect(%0)");
		p("Unique.1", "set[op(%0)]");
		p("UnitOrthogonalVector.1",
				"todo");
		p("UnitVector.1",
				"normalize(%0)");
		p("Variance.1",
				"variance(%0)");
		p("Weibull.3", "1-exp(-((%2)/(%1))^(%0))");
		p("Zipf.4",
				"todo");
		return commandMap;
	}	

}