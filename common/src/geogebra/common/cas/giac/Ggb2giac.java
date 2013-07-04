package geogebra.common.cas.giac;

import java.util.Map;
import java.util.TreeMap;

/***
 * IMPORTANT: Every time this file is changed a robot will automatically
 * create a new version of giac.js and modify kickstart.xml for the web.
 */

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

		// replace _ with \_ to make sure it's not replaced with "unicode95u"

		commandMap.put(signature, casSyntax.replace("_",  "\\_"));
	}

	/**
	 * @return map signature => syntax
	 */
	public static Map<String,String> getMap() {
		p("Append.2",
				"append(%0,%1)");
		// simplify() to make sure Binomial[n,1] gives n
		p("Binomial.2",
				"simplify(binomial(%0,%1))");
		p("BinomialDist.4",
				"if %3=true then binomial_cdf(%0,%1,%2) else binomial(%0,%1,%2) fi");
		p("Cauchy.3", "normal(1/2+1/pi*atan(((%2)-(%1))/(%0)))");

		// factor over complex rationals
		// [ggbans:=%0] first in case something goes wrong, eg  CFactor[sqrt(21) - 2sqrt(7) x ί + 3sqrt(3) x² ί + 6x³]
		p("CFactor.1","[with_sqrt(0),[ggbans:=%0],[ggbans:=cfactor(ggbans)],with_sqrt(1),ggbans][4]");
		p("CFactor.2","[with_sqrt(0),[ggbans:=%0],[ggbans:=cfactor(ggbans,%1)],with_sqrt(1),ggbans][4]");

		// factor over complex irrationals
		p("CIFactor.1","[with_sqrt(1),[ggbans:=%0],[ggbans:=cfactor(ggbans)],ggbans][3]");
		p("CIFactor.2","[with_sqrt(1),[ggbans:=%0],[ggbans:=cfactor(ggbans,%1)],ggbans][3]");

		p("ChiSquared.2", 
				//"chisquare_cdf(%0,%1)");
				"igamma(%0/2,%1/2,1)");
		p("Coefficients.1",
				"when(is_polynomial(%0),"+
						"coeffs(%0),"+
				"{})");

		p("Coefficients.2", "coeffs(%0,%1)");
		p("CompleteSquare.1",
				"canonical_form(%0)");
		p("CommonDenominator.2", "lcm(denom(%0),denom(%1))");
		p("Covariance.2",
				"covariance(%0,%1)");
		p("Covariance.1",
				"normal(covariance(%0))");
		p("Cross.2", "cross(%0,%1)");
		p("ComplexRoot.1", "normal(cZeros(%0,x))");
		p("CSolutions.1", "normal(cZeros(%0,x))");
		p("CSolutions.2",
				"normal(cZeros(%0,%1))");
		p("CSolve.1",
				"normal(csolve(%0,x))");
		p("CSolve.2", "normal(csolve(%0,%1))");
		p("Degree.1",
				"degree(%0)");
		p("Degree.2", "degree(%0,%1)");
		p("Denominator.1", "denom(%0)");
		p("Derivative.1",
				"regroup(diff(%0, x))");
		p("Derivative.2", 
				"regroup(diff(%0,%1))");
		p("Derivative.3", 
				"regroup(diff(%0,%1,%2))");
		p("Determinant.1", "det(%0)");
		p("Dimension.1", "dim(%0)");
		p("Div.2",
				"if type(%0)==DOM_INT && type(%1)==DOM_INT then iquo(%0,%1) else quo(%0,%1,x) fi");
		p("Division.2",
				"if type(%0)==DOM_INT && type(%1)==DOM_INT then iquorem(%0,%1) else quorem(%0,%1,x) fi");
		p("Divisors.1",
				"dim(idivis(%0))");
		p("DivisorsList.1",
				"idivis(%0)");
		p("DivisorsSum.1",
				"sum(idivis(%0))");
		p("Dot.2", "dot([%0],[%1])");
		// GeoGebra indexes lists from 1, giac from 0

		// equations:
		// (4x-3y=2x+1)[0] ='='
		// (4x-3y=2x+1)[1] = left side
		// (4x-3y=2x+1)[2] = right side

		// expressions:
		// (4x+3y-1)[0] = '+'
		// (4x+3y-1)[1] = 4x
		// (4x+3y-1)[2] = 3y
		// (4x+3y-1)[3] = -1
		p("Element.2", "when(type(%0)==DOM_LIST,(%0)[%1-1],(%0)[%1])");

		//if %0[0]=='=' then %0[%1] else when(...) fi;

		// GeoGebra indexes lists from 1, giac from 0
		p("Element.3",
				"%0[%1 - 1,%2 - 1]");

		// used in regular mode
		// Giac doesn't auto-simplify
		// normal so f(x):=(x^2-1)/(x-1) -> x+1 (consistent with Reduce)
		// regroup so that r*r^n -> r^(n+1)
		p("Evaluate.1", "normal(regroup(%0))");
		//p("Evaluate.1", "%0");

		p("Expand.1",
				"normal(%0)");
		p("Exponential.2", "1-exp(-(%0)*(%1))");

		// factor over rationals
		// add x so that Factor[(-k x² + 4k x + x³)] gives a nicer answer
		p("Factor.1",
				"[with_sqrt(0),[ggbans:=%0],[if type(ggbans)==DOM_INT then ggbans:=ifactor(ggbans); else ggbans:=factor(ggbans,x); fi],with_sqrt(1),ggbans][4]");
		p("Factor.2",
				"[with_sqrt(0),[ggbans:=%0],[ggbans:=factor(ggbans,%1)],with_sqrt(1),ggbans][4]");

		// factor over irrationals
		p("IFactor.1",
				"[with_sqrt(1),[ggbans:=%0],[if type(ggbans)==DOM_INT then ggbans:=ifactor(ggbans); else ggbans:=factor(ggbans,x); fi],ggbans][3]");
		p("IFactor.2",
				"[with_sqrt(1),[ggbans:=%0],[ggbans:=factor(ggbans,%1)],ggbans][3]");

		// convert {x-1,1,x+1,1} to {{x-1,1},{x+1,1}}
		p("Factors.1",
				//"factors(%0)");
				"[[ggbans:=%0],[if type(ggbans)==DOM_INT then calc_mode(0); ggbans:=ifactors(ggbans); calc_mode(1); else ggbans:=factors(ggbans); fi],matrix(dim(ggbans)/2,2,ggbans)][2]");
		p("FDistribution.3",
				"fisher_cdf(%0,%1,%2)");
		// alternative for exact answers
		// "Beta(exact(%0)/2,%1/2,%0*%2/(%0*%2+%1),1)");
		p("Flatten.1", "flatten(%0)");
		p("First.1", "{%0[0]}");
		p("First.2",
				"%0[0..%1-1]");

		// These implementations follow the one in GeoGebra
		p("FitExp.1",
				"[[ggbans:=%0],[ggbans:=exponential_regression(ggbans)],evalf(ggbans[1])*exp(ln(evalf(ggbans[0]))*x)][2]");
		p("FitLog.1",
				"[[ggbans:=%0],[ggbans:=logarithmic_regression(%0)],evalf(ggbans[0])*ln(x)+evalf(ggbans[1])][2]");
		p("FitPoly.2",
				"normal(evalf(horner(polynomial_regression(%0,%1),x)))");
		p("FitPow.1",
				"[[ggbans:=%0],[ggbans:=power_regression(ggbans)],evalf(ggbans[1])*x^evalf(ggbans[0])][2]");

		p("Gamma.3", "igamma((%0),(%2)/(%1),1)");
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
				"[[m:=%1],[ng:=%0],[n:=%2],[kk:=%3],if %4=true then sum(binomial(m,k)*binomial((ng-m),(n-k))/binomial(ng,n),k,0,floor(kk)) " +
				"else binomial(m,kk)*binomial((ng-m),(n-kk))/binomial(ng,n) fi][4]");
		p("Identity.1", "identity(round(%0))");
		p("If.2", "when(%0,%1,undef)");
		p("If.3", "when(%0,%1,%2)");

		// normal(regroup()) so that ImplicitDerivative[x^2 + y^2, y, x] gives a nice answer
		// the danger is that this could multiply something out eg (x+1)^100 (unlikely)
		p("ImplicitDerivative.3", "normal(regroup(-diff(%0,%2)/diff(%0,%1)))");
		p("ImplicitDerivative.1", "normal(regroup(-diff(%0,x)/diff(%0,y)))");

		p("Integral.1",
				"regroup(integrate(%0))");
		p("Integral.2",
				"regroup(integrate(%0,%1))");

		p("Integral.3",
				"normal(integrate(%0,%1,%2))");

		p("Integral.4",
				"normal(integrate(%0,%1,%2,%3))");
		p("IntegralBetween.4",
				"normal(int(%0-(%1),x,%2,%3))");
		p("IntegralBetween.5",
				"normal(int(%0-(%1),%2,%3,%4))");

		// need to wrap in coordinates() for Intersect[Curve[t,t^2,t,-10,10],Curve[t2,1-t2,t2,-10,10] ]
		// but not for Intersect[x^2,x^3]
		// ggbans:=0/0 to make sure if there's an error, we don't output previous answer
		// TODO: remove wrapping in equation when Giac supports intersecting equation, parametric
		p("Intersect.2",
				"[[ggbans:=0/0],[ggbans:=inter(equation(%0),equation(%1))],[ggbans:=when(type(ggbans[0])==DOM_LIST,ggbans,coordinates(ggbans))],ggbans][3]");

		p("Conic.5", "equation(conic(point(%0),point(%1),point(%2),point(%3),point(%4)))=0");
		p("Ellipse.3", "equation(ellipse(point(%0),point(%1),point(%2)))=0");
		p("Iteration.3",
				"(unapply(%0,x)@@%2)(%1)");
		p("IterationList.3",
				"[[ggbans(f,x0,n):=begin local l,k; l:=[x0]; for k from 1 to n do l[k]:=f(l[k-1]); od; l; end],ggbans(unapply(%0,x),%1,%2)][1]");
		p("PointList.1",
				"flatten(coordinates(%0))");
		p("RootList.1",
				"apply(x->convert([x,0],25),%0)");
		p("Invert.1", "[[ggbans:=0/0], [ggbarg:=%0], [ggbans:=when(type(ggbarg)!=DOM_LIST,"+
				// invert function (answer is function, not mapping)
				"subst(right([op(solve(tmpvar=ggbarg,lname(ggbarg)[0]))][0]),tmpvar,lname(ggbarg)[0])"+
				","+
				// invert matrix
				"inv(ggbarg))"+
				"],ggbans][3]");

		p("IsPrime.1", "isprime(%0)");
		p("Join.N","flatten(%)");
		p("Last.1",
				"{%0[dim(%0)-1]}");
		p("Last.2",
				"%0[size(%0)-%1..size(%0)-1]");
		p("LCM.1",
				"lcm(%0)");
		p("LCM.2",
				"lcm(%0,%1)");
		p("LeftSide.1",
				"when(type(%0)==DOM_LIST,map(%0,left),left(%0))");
		p("LeftSide.2",
				"left(%0[%1-1])");
		p("Length.1",
				"size(%0)");
		p("Length.3",
				"arcLen(%0,%1,%2)");
		p("Length.4", "arcLen(%0,%1,%2,%3)");

		// regroup so that exp(1)^2 is simplified
		// regroup(inf) doesn't work, so extra check needed
		p("Limit.2",
				"[[ggbans:=?],[ggbans:=limit(%0,%1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("Limit.3",
				"[[ggbans:=?],[ggbans:=limit(%0,%1,%2)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitAbove.2",
				"[[ggbans:=?],[ggbans:=limit(%0,x,%1,1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitAbove.3", 
				"[[ggbans:=?],[ggbans:=limit(%0,%1,%2,1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitBelow.2",
				"[[ggbans:=?],[ggbans:=limit(%0,x,%1,-1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitBelow.3", 
				"[[ggbans:=?],[ggbans:=limit(%0,%1,%2,-1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");

		p("Max.N", "max(%)");
		p("MatrixRank.1", "rank(%0)");
		p("Mean.1",
				"mean(%0)");
		p("Median.1",
				"median(%0)");
		p("Min.N", "min(%)");
		p("Midpoint.2", "convert(coordinates(midpoint(%0,%1)),25)");
		p("MixedNumber.1",
				"propfrac(%0)");
		p("Mod.2",
				"if type(%0)==DOM_INT && type(%1)==DOM_INT then irem(%0,%1) else rem(%0,%1,x) fi");
		p("NextPrime.1", "nextprime(%0)");
		p("NIntegral.3",
				"romberg(%0,%1,%2)");
		p("NIntegral.4",
				"romberg(%0,%1,%2,%3)");
		p("Normal.3",
				"normald_cdf(%0,%1,%2)");
		p("Normal.4",
				"if %3=true then normald_cdf(%0,%1,%2) else (1/sqrt(2*pi*((%1)^2))) * exp(-((%2-%0)^2) / (2*((%1)^2))) fi");
		p("nPr.2", "perm(%0,%1)");

		p("NSolve.1",
				"[[ggbans:=%0],[ggbans:=when(type(ggbans)==DOM_LIST,"+
						// eg NSolve[{π / x = cos(x - 2y), 2 y - π = sin(x)}]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars[irem(j,dim(ggbans))]=ggbans[j],j,0,dim(ggbans)-1)][3],"+
						// eg NSolve[a^4 + 34a^3 = 34]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars[0])],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars[0]=ggbans[j],j,0,dim(ggbans)-1)][3])],"+
				"ggbans][2]");

		p("NSolve.2",
				"[[ggbans:=%0],[ggbans:=when(type(ggbans)==DOM_LIST,"+
						// eg NSolve[{π / x = cos(x - 2y), 2 y - π = sin(x)},{x=1,y=1}]
						// eg NSolve[{π / x = cos(x - 2y), 2 y - π = sin(x)},{x,y}]
						"[[ggbvars:=seq(left(%1[j]),j,0,dim(%1))],[ggbans:=fsolve(%0,%1)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars[irem(j,dim(ggbans))]=ggbans[j],j,0,dim(ggbans)-1)][3],"+
						// eg NSolve[a^4 + 34a^3 = 34, a=3]
						// eg NSolve[a^4 + 34a^3 = 34, a]
						"[[ggbvars:=when(type(%1)==DOM_LIST,left(%1[0]),left(%1))],[ggbans:=fsolve(%0,%1)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars=ggbans[j],j,0,dim(ggbans)-1)][3])],"+
				"ggbans][2]");

		// fsolve starts at x=0 if no initial value is specified and if the search is not successful
		// it will try a few random starting points.

		p("NSolutions.1",
				"[[ggbans:=%0],[ggbans:=when(type(ggbans)==DOM_LIST,"+
						// eg NSolutions[{π / x = cos(x - 2y), 2 y - π = sin(x)}]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],ggbans][3],"+
						// eg NSolutions[a^4 + 34a^3 = 34]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars[0])],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],ggbans][3])],"+
				"ggbans][2]");

		p("NSolutions.2",
				"[[ggbans:=fsolve(%0,%1)],when(type(ggbans)==DOM_LIST,ggbans,[ggbans])][1]");

		p("Numerator.1", "numer(%0)");

		p("Numeric.1",
				"[[ggbans:=%0],when(dim(lname(ggbans))==0 || count_eq(unicode0176u,lname(ggbans))>0,"+
						// normal() so that Numeric(x + x/2) works
						// check for unicode0176u so that Numeric[acos((-11.4^2+5.8^2+7.2^2)/(2 5.8 7.2))]
						// is better when returning degrees from inverse trig
						"evalf(ggbans)"+
						","+
						"evalf(normal(ggbans))"+
				")][1]");

		p("Numeric.2",
				"[[ggbans:=%0],when(dim(lname(ggbans))==0 || lname(ggbans)==[unicode0176u],"+
						// normal() so that Numeric(x + x/2) works
						// check for unicode0176u so that Numeric[acos((-11.4^2+5.8^2+7.2^2)/(2 5.8 7.2))]
						// is better when returning degrees from inverse trig
						"evalf(ggbans,%1)"+
						","+
						"evalf(normal(ggbans),%1)"+
				")][1]");

		p("OrthogonalVector.1",
				"convert([[0,-1],[1,0]]*(%0),25)");
		//using sub twice in opposite directions seems to fix #2198, though it's sort of magic
		// with_sqrt(0) to factor over rationals
		p("PartialFractions.1",
				"partfrac(%0)");
		p("PartialFractions.2", "partfrac(%0,%1)");
		p("Pascal.4",
				"if %3=true then Beta(%0,1+floor(%2),%1,1) else (1-(%1))^(%2)*(%1)^(%0)*binomial(%0+%2-1,%0-1) fi");
		p("Poisson.3",
				"if %2=true then " +
						"exp(-(%0))*sum ((%0)^k/k!,k,0,floor(%1)) " +
				"else (%0)^(%1)/factorial(floor(%1))*exp(-%0) fi");
		p("PreviousPrime.1",
				"if (%0 > 2) then prevprime(%0) else 0/0 fi");
		p("PrimeFactors.1",
				"ifactors(%0)");
		// normal() makes sure answer is expanded
		// TODO: do we want this, or do it in a more general way
		p("Product.1",
				"normal(product(%0))");
		p("Product.4", "normal(product(%0,%1,%2,%3))");
		// p("Prog.1","<<%0>>");
		// p("Prog.2","<<begin scalar %0; return %1 end>>");

		p("Random.2", "%0+rand(%1-(%0)+1)"); // "RandomBetween"
		p("RandomBinomial.2",
				"binomial_icdf(%0,%1,rand(0,1))");
		p("RandomElement.1", "rand(1,%0)[0]");
		p("RandomPoisson.1",
				"poisson_icdf(%0,rand(0,1))"); // could also make the product of rand(0,1) until less than exp(-%0)
		p("RandomNormal.2",
				"randnorm(%0,%1)");
		p("RandomPolynomial.3",
				"randpoly(%0,x,%1,%2)");
		p("RandomPolynomial.4",
				"randpoly(%1,%0,%2,%3)");
		p("Rationalize.1", "if type(%0)==DOM_RAT then %0 else normal(exact(%0)) fi");
		p("Reverse.1","revlist(%0)");
		p("RightSide.1",
				"when(type(%0)==DOM_LIST,map(%0,right),right(%0))");
		p("RightSide.2",
				"right(%0[%1-1]) ");

		p("ReducedRowEchelonForm.1",
				"rref(%0)");
		p("Sample.2",
				"flatten(seq(rand(1,%0),j,1,%1))");
		p("Sample.3",
				"if %2=true then flatten(seq(rand(1,%0),j,1,%1)) else rand(%1,%0) fi");
		p("SampleVariance.1",
				" [[ggbans:=%0],[ggbans:=normal(variance(ggbans)*size(ggbans)/(size(ggbans)-1))],ggbans][2]");
		p("SampleSD.1",
				"normal(stddevp(%0))");
		p("Sequence.1", "seq(j,j,1,%0)");
		p("Sequence.4",
				"seq(%0,%1,%2,%3)");
		p("Sequence.5",
				"seq(%0,%1,%2,%3,%4)");	
		p("SD.1",
				"normal(stddev(%0))");
		p("Shuffle.1", "randperm(%0)");
		// regroup for r*r^n
		p("Simplify.1", "tlin(simplify(regroup(%0)))");

		p("Solutions.1",
				"normal(zeros(%0,x))");
		p("Solutions.2",
				"normal(zeros(%0,%1))");

		// Root.1 and Solve.1 should be the same		
		String root1 = "normal([op(solve(%0))])";
		p("Root.1", root1);
		p("Solve.1", root1);

		p("Solve.2",
				"normal([op(solve(%0,%1))])");
		p("SolveODE.1",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0),x->y=x)[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0),x->y=x)[0])"
						+")");
		p("SolveODE.2",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0,%1),x->y=x)[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0,%1),x->y=x)[0])"
						+")");
		p("SolveODE.3",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0,%2,%1),type(%1)==6?(x->%1=x):(x->y=x))[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0,%2,%1),type(%1)==6?(x->%1=x):(x->y=x))[0])"
						+")");
		p("SolveODE.4",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0,%2,%1,%3),x->%1=x)[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0,%2,%1,%3),x->%1=x)[0])"
						+")");
		p("SolveODE.5",//SolveODE[y''=x,y,x,A,{B}]
				"normal(map(desolve(%0,%2,%1,%3,%4),x->%1=x)[0])");
		p("Substitute.2","regroup(subst(%0,%1))");
		p("Substitute.3",
				"regroup(subst(%0,%1,%2)))");
		// p("SubstituteParallel.2","if hold!!=0 then sub(%1,%0) else sub(%1,!*hold(%0))");

		// remove normal from Sum, otherwise
		// Sum[1/n*sqrt(1-(k/n)^2),k,1,n]
		// Sum[1/10*sqrt(1-(k/10)^2),k,1,10]
		// don't work
		p("Sum.1",
				"sum(%0)");
		p("Sum.4",
				"sum(%0,%1,%2,%3)");

		// GeoGebra counts elements from 1, giac from 0
		p("Take.3",
				"%0[%1-1..%2-1]");
		p("TaylorSeries.3",
				"convert(series(%0,x,%1,%2),polynom)");
		p("TaylorSeries.4",
				"convert(series(%0,%1,%2,%3),polynom)");
		p("TDistribution.2",
				"student_cdf(%0,%1)");
		// alternative for exact calculations, but Numeric[TDistribution[4,2],15] doesn't work with this
		// "1/2 + (Beta(%0 / 2, 1/2, 1, 1) - Beta(%0 / 2, 1/2, %0 / (%0 + (%1)^2 ) ,1) )* sign(%1) / 2");
		p("ToComplex.1",
				"%0[0]+i*%0[1]");
		p("ToExponential.1",
				"rectangular2polar(%0)");
		p("ToPolar.1",
				"([[ggbans:=%0],[ggbans:=polar_coordinates(ggbans)],[ggbans:=convert([ggb_ang(ggbans[0],ggbans[1])],25)],ggbans])[3]");
		p("ToPoint.1",
				"convert(coordinates(%0),25)");
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
				"tlin(%0)");
		p("TrigCombine.1",
				"tcollect(%0)");
		p("TrigCombine.2",
				"tcollect(%0)");
		p("Unique.1", "[op(set[op(%0)])]");
		p("UnitOrthogonalVector.1",
				"convert(unitV([-%0[1],%0[0]]),25)");
		p("UnitVector.1",
				"normalize(%0)");
		p("Variance.1",
				"normal(variance(%0))");
		p("Weibull.3", "1-exp(-((%2)/(%1))^(%0))");
		p("Zipf.4", // %1=exponent
				"if %3=true then harmonic(%1,%2)/harmonic(%1,%0) else 1/((%2)^%1*harmonic(%1,%0)) fi");

		// SolveCubic[x^3+3x^2+x-1]
		// SolveCubic[x^3+3x^2+x-2]
		// SolveCubic[x^3+3x^2+x-3]

		// SolveCubic[x^3 + 6x^2 - 7*x - 2]
		// x³ - 6x² - 7x + 9

		// check with CSolve first, eg
		// f(x) = x³ - 9x² - 2x + 8
		
		// adapted from xcas example by Bernard Parisse
		p("SolveCubic.1", "["+
			"[j:=exp(2*i*pi/3)],"+
			"[V:=symb2poly(%0,x)],"+
			"[n:=size(V)],"+
			
			//if (n!=4){
			//  throw(afficher(P)+" n'est pas de degre 3");
			//}
			// Reduction de l'equation
			
			"[V:=V/V[0]],"+
			"[b:=V[1]],"+
			"[V:=ptayl(V,-b/3)],"+
			"[p:=V[2]],"+
			"[q:=V[3]],"+
			// on est ramen  x^3+p*x+q=0
			// x=u+v -> u^3+v^3+(3uv+p)(u+v)+q=0
			// On pose uv=-p/3 donc u^3+v^3=-q et u^3 et v^3 sont solutions
			// de u^3 v^3 = -p^3/27 et u^3+v^3=-q
			// donc de x^2+q*x-p^3/27=0
			"[d:=q^2/4+p^3/27],"+
			
			//if (d==0){
			//  // racine double
			//  return solve(P,x);
			//}
			"[d:=sqrt(d)],"+
			"[u:=(-q/2+d)^(1/3)],"+
			"[v:=-p/3/u],"+
			"[x1:=u+v-b/3],"+
			"[x2:=u*j+v*conj(j)-b/3],"+
			"[x3:=u*conj(j)+v*j-b/3],"+
			"[x1s:=simplify(x1)],"+
			"[x2s:=simplify(x2)],"+
			"[x3s:=simplify(x3)],"+
			//"[when(d==0,[solve(%0,x)],[when(x1s[1][0]=='rootof',x1,x1s),when(x2s[1][0]=='rootof',x2,x2s),when(x3s[1][0]=='rootof',x3,x3s)])]"+
			"[[x1,x2,x3]]"+
			"][18][0]");


		/*
		// SolveQuartic[2x^4+3x^3+x^2+1]
		// SolveQuartic[x^4+6x^2-60x+36]
		// SolveQuartic[3x^4   + 6x^3   - 123x^2   - 126x + 1080]
		p("SolveQuartic.1", "["+
				"[ggbcoeffs:=coeffs(%0)],"+
				// divide thorugh by coeffient of x^4
				"[ggbcoeffs2:= ggbcoeffs / ggbcoeffs[0]],"+
				"[a3:=ggbcoeffs[0]],"+
				"[a2:=ggbcoeffs[1]],"+
				"[a1:=ggbcoeffs[2]],"+
				"[a0:=ggbcoeffs[3]],"+
				"[p:=a2-3*a3^2/8],"+
				"[q:=a1-a2*a3/2+a3^3/8],"+
				"[r:=a0-a1*a3/4+a2*a3^2/16-3*a3^4/256],"+
				// root of resolvent cubic
				//"[u1:=zeros(4*(x-p)*(x^2/4-r)-q^2,x)[0]]"+
				"[y1:=zeros(x^3-a2*x^2+(a1*a3-4*a0)*x+4*a2*a0-a1*a1-a3*a3*a0,x)[0]]"+
				//"[a3:=ggbcoeffs2[0]/4],"+
				// substitute x=x-a3/4 to eliminate x^3 term
				//"[ggbcoeffs3:=coeffs(  (x-a3)^4 + ggbcoeffs2[0] * (x-a3)^3 + ggbcoeffs2[1] (x-a3)^2 + ggbcoeffs2[2]*(x-a3) + ggbcoeffs2[3])]"+
				//"[],"+
				//"[],"+
				//"[],"+
				"]"+
				"[9]");
		 */

		// Experimental Geometry commands. Giac only
		p("Radius.1", "normal(regroup(radius(%0)))"); 
		p("Center.1", "coordinates(center(%0))"); 
		p("Midpoint.2", "normal(regroup(coordinates(midpoint(%0,%1))))");

		// center-point:      point(%0),point(%1)
		// or center-radius:  point(%0),%1
		p("Circle.2", "equation(circle(point(%0),when(type(%1)==DOM_LIST,point(%1),%1))");

		p("Area.1", "normal(regroup(area(circle(%0))))");
		p("Circumference.1", "normal(regroup(perimeter(%0)))");

		p("LineBisector.2", "equation(perpen_bisector(%0,%1))");
		p("AngularBisector.3", "equation(bisector(%1,%0,%2))");

		// can it be plotted in GeoGebra? Do we want it to be plotted?
		p("Angle.3", "normal(regroup(angle(%1,%0,%2)))");

		// eg distance((4,5),(0,3))
		// eg distance((2,3,4),(0,3,1))
		// eg distance(conic(y=x^2),(0,3))
		// TODO: maybe need to wrap conics with conic() in ConicND, and change Radius.1, Center.1?
		// TODO: what about functions?
		p("Distance.2", "normal(regroup(distance(%0,%1)))");

		// wrap (2,3) as point(2,3), but not eg
		// Line[(2,3),y=x]
		p("Line.2","equation(line(point(%0),when(type(%1)==DOM_LIST,point(%1),%1)))");

		p("OrthogonalLine.2", "equation(perpendicular(%0,line(%1)))");
		// TODO: return Segment() not equation
		p("Segment.2","equation(segment(%0,%1))");

		// TODO: needs to get back from Giac into GeoGebra as a parametric eqn
		//p("Curve.5", "equation(plotparam([%0,%1],%2,%3,%4))");
		//p("Polygon.N", "polygon(%)");
		//p("PolyLine.N", "open_polygon(%)");

		// Tangent[x-value, function]
		p("Tangent.2",
				"y=subst(diff(%1,x),x=%0)*(x-%0)+subst(%1,x=%0)");


		return commandMap;
	}

}