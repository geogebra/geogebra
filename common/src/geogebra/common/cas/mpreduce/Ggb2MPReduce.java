package geogebra.common.cas.mpreduce;

import java.util.Map;
import java.util.TreeMap;

/***
 * # Command translation table from GeoGebra to MPReduce # e.g. Expand[ 2(x+3) ]
 * is translated to ExpandBrackets( 2*(x+3) ) ###
 */

public class Ggb2MPReduce {
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
	public Map<String,String> getMap() {
		p("Append.2",
				"myappend(%0,%1)");
		p("Binomial.2",
				"<< begin scalar n!!, k!!, result1!!, result2!!; n!!:=(%0); k!!:=(%1); result1!!:= if numberp(n!!) and numberp(k!!) and k!!>n!! then 0 else factorial(n!!)/(factorial(k!!)*factorial(n!!-k!!)); return result1!! end >>");
		p("BinomialDist.4",
				"<< begin scalar n!!, p!!, k!!; n!!:=(%0); p!!:=(%1); k!!:=(%2);return if %3=\\'true then for i:=0:floor(k!!) sum binomial(n!!,i)*p!!^i*(1-p!!)^(n!!-i) else binomial(n!!,k!!)*p!!^k!!*(1-p!!)^(n!!-k!!) end >>");
		p("Cauchy.3", "1/2+1/pi*atan(((%2)-(%1))/(%0))");
		p("CFactor.1",
				"<<on complex$ off combinelogs$ begin scalar factorlist!!; factorlist!!:=factorize(%0); return part(!*hold((for each x in factorlist!! collect (if arglength(x)<0 or part(x,0) neq \\'list then x else if part(x,2)=1 then part(x,1) else part(x,0):=**))),0):=* end >>");
		p("CFactor.2",
				"<<on complex$ off combinelogs$ begin scalar factorlist!!; korder append(list(%1),varorder!!); factorlist!!:=factorize(%0); korder varorder!!;return part(!*hold(for each x in factorlist!! collect (if arglength(x)<0 or part(x,0) neq \\'list then x else if part(x,2)=1 then part(x,1) else part(x,0):=**)),0):=* end >>");
		p("ChiSquared.2", "igamma((%0)/2,(%1)/2)/gamma((%0)/2)");
		p("Coefficients.1",
				"<<begin scalar input!!; input!!:=(%0); return mycoeff(input!!,mymainvar(input!!)) end >>");
		p("Coefficients.2", "mycoeff(%0,%1)");
		p("CompleteSquare.1",
				"<<begin scalar input!!, co, va;off exp; input!!:=(%0); va := mymainvar(input!!);co:= mycoeff(input!!,va);return if length(co)=3 then part(co,1)*(va+part(co,2)/part(co,1)/2)^2+part(co,3)-part(co,2)^2/part(co,1)/4 else \\'?; end >>");
		p("CommonDenominator.2", "lcm(den(%0),den(%1))");
		p("Covariance.2",
				"<<begin scalar ret, tmpmean1, tmpmean2, tmplength, list1!!, list2!!; list1!!:=(%0); list2!!:=(%1); ret:=0$ tmpmean1:=0$ tmpmean2:=0$ tmplength:=length(list1!!)$ tmpmean1:=1/tmplength*for i:=1:tmplength sum part(list1!!,i) $ tmpmean2:=1/tmplength*for i:=1:tmplength sum part(list2!!,i)$  return 1/tmplength*for i:=1:tmplength sum (part(list1!!,i)-tmpmean1)*(part(list2!!,i)-tmpmean2) end>>");
		p("Covariance.1",
				"<<" +
				"begin scalar ret, tmpmean1, tmpmean2, tmplength, input!!;" +
				"input!!:=%0; ret:=0; tmpmean1:=0; tmpmean2:=0; clear tmp;" +
				"tmplength:=length(input!!);" +
				"tmpmean1:=1/tmplength* for each element!! in input!! sum <<tmp:=element!!;tmp[0]>> $ tmpmean2:=1/tmplength*for each element!! in input!! sum <<tmp:=element!!;tmp[1]>>$ return 1/tmplength*for each element!! in input!! sum <<tmp:=element!!;(tmp[0]-tmpmean1)*(tmp[1]-tmpmean2)>> end>>");
		p("Cross.2", "mycross(%0,%1)");
		p("CSolutions.1",
				"<<begin scalar input!!; input!!:=(%0); on complex$ return flattenlist(for each element!! in mycsolve(input!!,mymainvar(input!!)) collect map(rhs,element!!)) end>>");
		p("CSolutions.2",
				"<<on complex$ flattenlist(for each element!! in mycsolve(%0,%1) collect map(rhs,element!!))>>");
		p("CSolve.1",
				"<<begin scalar input!!; input!!:=(%0); on complex$ return flattenlist(mycsolve(input!!,mymainvar(input!!))) end>>");
		p("CSolve.2", "<<on complex$ flattenlist(mycsolve(%0,%1))>>");
		p("Degree.1",
				"<<begin scalar input!!, variables!!; input!!:=(%0); torder(list(),gradlex); input!!:=part(gsplit(input!!),1); variables!!:=gvars(list(input!!)); return for each variable!! in variables!! sum deg(input!!,variable!!) end>>");
		p("Degree.2", "deg(%0,%1)");
		p("Delete.1", " << clear %0; \\'true>>");
		p("Denominator.1", "den(%0)");
		p("Derivative.1",
				"<< begin scalar input!!, result!!; input!!:=(%0);let solverules; result!!:=df(input!!,mymainvar(input!!)); clearrules solverules; return result!! end>>");
		p("Derivative.2", "<<begin scalar result!!; let solverules; result!!:=df(%0,%1); clearrules solverules; return result!! end>>");
		p("Derivative.3", "<<begin scalar result!!; let solverules; result!!:=df(%0,%1,%2); clearrules solverules; return result!! end>>");
		p("Determinant.1", "<<tmpmat!!:=(%0); det(tmpmat!!)>>");
		p("Dimension.1", "<<begin scalar input!!; input!!:=%0; return if myvecp input!! then dim input!! else length(input!!) end>>");
		p("Div.2", "div(%0,%1)");
		p("Division.2", "list(div(%0,%1),mod!!(%0,%1))");
		p("Divisors.1",
				"<<off combinelogs$ if numberp(%0) then if (%0)=1 then 1 else for each i in map(part(~w,2)+1,factorize(%0)) product i else \\'?>>");
		p("DivisorsList.1",
				"<<off combinelogs$ if numberp(%0) then if %0=1 then list(1) else <<begin scalar divlist!!, return!!; divlist!!:=for each x in factorize(%0) collect for i:=0:part(x,2) collect part(x,1)^i; return!!:=part(divlist!!,1); for i:=2:length(divlist!!) do return!!:=for each x in part(divlist!!,i) join for each y in return!! collect x*y; return mysort(return!!) end>> else \\'? >>");
		p("DivisorsSum.1",
				"<<off combinelogs$ if numberp(%0) then if %0=1 then 1 else for each x in factorize(%0) product (part(x,1)^(part(x,2)+1)-1)/(part(x,1)-1) else \\'? >>");
		p("Dot.2", "mydot(%0,%1)");
		p("erf.1", "myerf(%0)");
		p("Element.2", "part(%0,%1)");
		p("Element.3",
				"<<clear input!!; input!!:=(%0); if arglength(input!!)>-1 and part(input!!,0)=\\'mat then input!!(%1,%2) else part(part(input!!,%1),%2)>>");
		p("Expand.1",
				"<<clear tmp!!; off factor, rat, combinelogs, allfac$ on pri, expandlogs$ tmp!!:=(%0); off factor, combinelogs$on expandlogs$  tmp!!>>");
		p("Exponential.2", "1-exp(-(%0)*(%1))");
		p("Factor.1",
				"<<on combineexpt; off combinelogs$ begin scalar factorlist!!, tmpexp!!; if numberp(den(%0)) then <<factorlist!!:=factorize(%0); return part(!*hold((for each x in factorlist!! collect (if arglength(x)<0 or part(x,0) neq \\'list then x else if part(x,2)=1 then part(x,1) else part(x,0):=**))),0):=*>> else <<on factor; tmpexp!!:=(%0); on factor; return tmpexp!! >> end >>");
		p("Factor.2",
				"<<on combineexpt; off combinelogs$ begin scalar factorlist!!; korder append(list(%1),varorder!!); factorlist!!:=factorize(%0); korder varorder!!;return part(!*hold(for each x in factorlist!! collect (if arglength(x)<0 or part(x,0) neq \\'list then x else if part(x,2)=1 then part(x,1) else part(x,0):=**)),0):=* end >>");
		p("Factors.1",
				"<<on combineexpt; off combinelogs$ off complex, rounded; for each x in factorize(%0) collect if arglength(x)<0 or part(x,0) neq \\'list then x:=list(x,1) else x >>");
		p("FDistribution.3",
				"ibeta((%0)/2,(%1)/2,(%0)*(%2)/((%0)*(%2)+%1))");
		p("Flatten.1", "mkdepthone(%0)");
		p("First.1", "list(first(%0))");
		p("First.2",
				"<<begin scalar list!!; list!!:=(%0); return if length(list!!)<=(%1) then list!! else for i:=1:%1 collect part(list!!,i) end>>");

		// These implementations follow the one in GeoGebra
		p("FitExp.1",
				"<<on rounded, roundall, numval; begin scalar p1!!, p2!!, input!!, sigmax!!, sigmay!!, sigmaxy!!, sigmax2!!, length!!, denominator!!, xlist!!, ylist!!; input!!:=mattolistoflists(%0); xlist!!:=map(part(~w!!,1), input!!); ylist!!:=map(log,map(part(~w!!,2), input!!)); length!!:=length(ylist!!); sigmax!!:=for each i in xlist!! sum i; sigmay!!:= for each i in ylist!! sum i; sigmax2!!:= for each i in xlist!! sum i^2; sigmaxy!!:=for i:=1:length!! sum part(xlist!!,i)*part(ylist!!,i);denominator!!:= length!!*sigmax2!!-sigmax!!**2; p2!!:=(length!!*sigmaxy!!-sigmax!!*sigmay!!)/denominator!!; p1!!:= exp((sigmay!!*sigmax2!!-sigmax!!*sigmaxy!!)/denominator!!); return p1!!*exp(x*p2!!) end>>");
		p("FitLog.1",
				"<<on rounded, roundall, numval; begin scalar p1!!, p2!!, input!!, sigmax!!, sigmay!!, sigmaxy!!, sigmax2!!, length!!, denominator!!, xlist!!, ylist!!; input!!:=mattolistoflists(%0); xlist!!:=map(log,map(part(~w!!,1), input!!)); ylist!!:=map(part(~w!!,2), input!!); length!!:=length(ylist!!); sigmax!!:=for each i in xlist!! sum i; sigmay!!:= for each i in ylist!! sum i; sigmax2!!:= for each i in xlist!! sum i^2; sigmaxy!!:=for i:=1:length!! sum part(xlist!!,i)*part(ylist!!,i);denominator!!:= length!!*sigmax2!!-sigmax!!**2; p2!!:=(length!!*sigmaxy!!-sigmax!!*sigmay!!)/denominator!!; p1!!:= (sigmay!!*sigmax2!!-sigmax!!*sigmaxy!!)/denominator!!; return p1!!+log(x)*p2!! end >>");
		p("FitPoly.2",
				"<< clear tmpx!!, tmpy!!; on rounded, roundall, numval; begin scalar xvec!!, yvec!!, input!!; input!!:= mattolistoflists(%0); xvec!!:=map(part(~w!!,1), input!!); yvec!!:= map(part(~w!!,2), input!!); tmpx!!:=vandermonde(xvec!!); tmpx!!:=sub\\_matrix(tmpx!!,for i:=1:length(input!!) collect i, for i:=1:(%1+1) collect i); tmpy!!:=listtocolumnvector(yvec!!);tmpx!!:=(1/(tp(tmpx!!)*tmpx!!))*tp(tmpx!!); tmpy!!:=tmpx!!*tmpy!!; return for i:=1:part(length(tmpy!!),1) sum tmpy!!(i,1)*x^(i-1) end>>");
		p("FitPow.1",
				"<<on rounded, roundall, numval; begin scalar p1!!, p2!!, input!!, sigmax!!, sigmay!!, sigmaxy!!, sigmax2!!, length!!, denominator!!, xlist!!, ylist!!; " +
				"input!!:=mattolistoflists(%0); xlist!!:=map(log,map(xcoord(~w!!), input!!)); ylist!!:=map(log,map(ycoord(~w!!), input!!)); length!!:=length(ylist!!); sigmax!!:=for each i in xlist!! sum i; sigmay!!:= for each i in ylist!! sum i; sigmax2!!:= for each i in xlist!! sum i^2; sigmaxy!!:=for i:=1:length!! sum part(xlist!!,i)*part(ylist!!,i);denominator!!:= length!!*sigmax2!!-sigmax!!**2; p2!!:=(length!!*sigmaxy!!-sigmax!!*sigmay!!)/denominator!!; p1!!:= exp((sigmay!!*sigmax2!!-sigmax!!*sigmaxy!!)/denominator!!); return p1!!*x^p2!! end >>");
		// FitSin
		p("Gamma.3",
				"(((%1)*(%2))^(%0))/(%0)*kummerm(%0,%0+1,-(%1)*(%2))/beta(%0)");
		p("GCD.2",
				"<<begin scalar gcd!!; off rounded, roundall, numval; gcd!!:=gcd(%0,%1); if numeric!!=0 then off rounded, roundall, numval; return gcd!! end>>");
		p("GCD.1",
				"<<begin scalar gcd!!; off rounded, roundall, numval; gcd!!:=0; for each term in (%0) do gcd!!:=gcd(gcd!!,term); if numeric!!=0 then off rounded, roundall, numval; return gcd!! end >>");
		// GetPrecision.1
		p("Groebner.1", "groebner(%0)");
		p("Groebner.2", "<<torder(%1,lex); groebner(%0)>>");
		p("Groebner.3",
				"<<if %2=1 then torder(%1,lex) else if %2=2 then torder(%1,gradlex) else if %2=3 then torder(%1,revgradlex); groebner(%0)>>");
		p("HyperGeometric.5",
				"<<begin scalar m,kk,ng,n; m:=(%1)$ ng:=(%0)$ n:=(%2)$ kk:=(%3)$ return if %4=true then sum(binomial(m,k)*binomial((ng-m),(n-k))/binomial(ng,n),k,0,kk) else binomial(m,kk)*binomial((ng-m),(n-kk))/binomial(ng,n) end>>");
		p("Identity.1", "<<make\\_identity(myround(%0))>>");
		p("If.2", "if %0=true then %1 else '? else iffun(%0,%1)");
		p("If.3", "if %0=true then %1 else if %0=false then %2 else ifelsefun(%0,%1,%2)");
		
		p("ImplicitDerivative.3", "-df(%0,%1)/df(%0,%2)");
		p("ImplicitDerivative.1", "-df(%0,currentx!!)/df(%0,currenty!!)");
		p("Integral.1",
				"<<begin scalar integral!!, input!!; input!!:=(%0); on combineexpt; let intrules!!; integral!!:=int(input!!,mymainvar(input!!)); clearrules intrules!!;  return if  freeof(integral!!,\\'int) then part(list(integral!!,newarbconst()),0):=+ else \\'? end>>");
		p("Integral.2",
				"<<begin scalar integral!!; let intrules!!; on combineexpt; integral!!:=int(%0,%1); clearrules intrules!!; return if freeof(integral!!,\\'int) then part(list(integral!!,newarbconst()),0):=+ else \\'? end>>");
		p("Integral.3",
				"<<begin scalar integral!!, input!!; input!!:=(%0);on combineexpt; let intrules!!; integral!!:=myint(input!!,mymainvar(input!!),%1,%2); clearrules intrules!!; return if freeof(integral!!,\\'int) then integral!! else num\\_int(input!!,mainvar(input!!),%1,%2) end>>");
		p("Integral.4",
				"<<begin scalar integral!!; let intrules!!;on combineexpt; integral!!:=myint(%0,%1,%2,%3); clearrules intrules!!; return if freeof(integral!!,\\'int) then integral!! else num\\_int(%0,%1,%2,%3) end>>");
		p("IntegralBetween.4",
				"<< begin scalar integral!!, input1!!, input2!!, variable!!; input1!!:=(%0); input2!!:=(%1); variable!!:=mymainvar(list(input1!!, input2!!)); let intrules!!; integral!!:=myint(input1!!-input2!!,mymainvar(list(input1!!, input2!!)),%2,%3); clearrules intrules!!; return if freeof(integral!!,\\'int) then integral!! else num\\_int(input1!!-input2!!,variable,%2,%3) end >>");
		p("IntegralBetween.5",
				"<< begin scalar integral!!; let intrules!!; integral!!:=int(%0-(%1),%2,%3,%4); clearrules intrules!!; return if freeof(integral!!,\\'int) then integral!! else num\\_int(%0-(%1),%2,%3,%4) end >>");
		p("Intersect.2",
				"<<begin scalar eqn1!!, eqn2!!; eqn1!!:=(%0); eqn2!!:=(%1); return map(listtomyvect,flattenlist(for each element!! in mysolve(list(if freeof(eqn1!!,=) then eqn1!!=currenty!! else eqn1!!,if freeof(eqn2!!,=) then eqn2!!=currenty!! else eqn2!!),list(mymainvar(mymainvar(eqn1!!)+mymainvar(eqn2!!)), currenty!!)) collect map(rhs,element!!))) end>>");
		p("PointList.1",
				"pointlist(mattolistoflists(%0))");
		p("RootList.1",
				"rootlist(mattolistoflists(%0))");
		p("Invert.1", "<<begin scalar a; a:=%0; return 1/a;end>>");
		p("IntegerPart.1", "if %0>0 then floor(%0) else ceiling(%0)");
		p("IsPrime.1", "if primep(%0) and (%0)>1 then true else false");
		//p("Join.N","<<begin scalar list!!=list(%); if length(list!!)=1 then list!!:=part(list!!,0); return for each x!! in list!! join x!! end>>");
		p("Join.N","<<begin scalar list!!; list!!:=list(%); if length(list!!)=1 then list!!:=mattolistoflists(part(list!!,1)); return for each x!! in list!! join x!! end>>");
		p("Line.2","<<begin scalar xa,ya,xb,yb; xa:=xcoord(%0); ya:=ycoord(%0); xb:=xcoord(%1);yb:=ycoord(%1);return lcm(lcm(den(xa-xb),den(ya-yb)),den(xb*ya-yb*xa))*((ya-yb)*currentx!!+(xb-xa)*currenty!!=xb*ya-yb*xa)/gcd(gcd(num(xa-xb),num(ya-yb)),num(xb*ya-yb*xa)); end>>");
		// p("IsBound.1","if << symbolic; p!!:=isbound!!('%0); algebraic; p!!>>=1 then 'true else 'false");
		p("Last.1",
				"<<begin scalar list!!; list!!:=(%0); return list(part(list!!,length(list!!))) end >>");
		p("Last.2",
				"<<begin scalar list!!; list!!:=(%0); return if length(list!!)<=(%1) then list!! else for i:=1:%1 collect part(list!!,length(list!!)-%1+i) end >>");
		p("LCM.1",
				"<<begin scalar lcm!!; off rounded, roundall, numval; lcm!!:=1; for each term in (%0) do lcm!!:=lcm(lcm!!,term); if numeric!!=0 then off rounded, roundall, numval; return lcm!! end >>");
		p("LCM.2",
				"<<begin scalar lcm!!; off rounded, roundall, numval; lcm!!:=lcm(%0,%1); if numeric!!=0 then off rounded, roundall, numval; return lcm!! end>>");
		p("LeftSide.1",
				"<<begin scalar input!!; input!!:=(%0); return if arglength(input!!) and part(%0,0)=\\'list then map(lhs,input!!) else lhs(input!!) end>>");
		p("LeftSide.2",
				"<<begin scalar input!!; input!!:=(%0); return if arglength(input!!) and part(%0,0)=\\'list then lhs(part(input!!,%1)) else \\'? end>>");
		p("Length.1",
				"mylength(%0)");
		p("Length.3",
				"<<begin scalar input!!, variable!!; input!!:=(%0); variable!!:=mymainvar(input!!); return myint(sqrt(1+df(input!!,variable!!)^2),variable!!,%1,%2) end>>");
		p("Length.4", "int(sqrt(1+df(%0,%1)^2),%1,%2,%3)");
		p("Limit.2",
				"<<begin scalar input!!,limitabove!!, limitbelow!!, result!!; input!!:=(%0); result!! := if (%1)=infinity or (%1)=-infinity then limit(input!!,<<mymainvar(input!!)>>,%1) else <<limitabove!!:=limit!+(input!!,mymainvar(input!!),%1); limitbelow!!:=limit!-(input!!,mymainvar(input!!),%1); if limitabove!!=limitbelow!! then limitabove!! else \\'?>>; return if freeof(result!!,\\'limit) then result!! else \\'? end>>");
		p("Limit.3",
				"<<begin scalar input!!,limitabove!!, limitbelow!!, result!!; input!!:=(%0); result!! := if (%2)=infinity or (%2)=-infinity then limit(input!!,%1,%2) else << limitabove!!:=limit!+(input!!,%1,%2); limitbelow!!:=limit!-(input!!,%1,%2); if limitabove!!=limitbelow!! then limitabove!! else \\'?>>; return if freeof(result!!,\\'limit) then result!! else \\'? end>>");
		p("LimitAbove.2",
				"<<begin scalar input!!, result!!; input!!:=(%0); result!!:= limit!+(input!!,mymainvar(input!!),%1); return if freeof(result!!,\\'limit!+) then result!! else \\'? end>>");
		p("LimitAbove.3", "<<begin scalar result!!; result!! := limit!+(%0,%1,%2); return if freeof(result!!,\\'limit!+) then result!! else \\'? end >>");
		p("LimitBelow.2",
				"<<begin scalar input!!, result!!; input!!:=(%0); result!! := limit!-(input!!,mymainvar(input!!),%1); return if freeof(result!!,\\'limit!-) then result!! else \\'?end>>");
		p("LimitBelow.3", "<<begin scalar result!!; result!! :=limit!-(%0,%1,%2); return if freeof(result!!,\\'limit!-) then result!! else \\'? end >>");
		p("Max.N", "max(%)");
		p("MatrixRank.1", "<<begin scalar a; a:=%0; return rank(a);end>>");
		p("Mean.1",
				"<<begin scalar list!!; list!!:=(%0)$ return 1/length(list!!)*for i:=1:length(list!!) sum part(list!!,i) end>>");
		p("Median.1",
				"<<begin scalar list!!; list!!:=(%0)$ list!!:= mysort list!!$ return if remainder(length(list!!),2)=0 then (part(list!!,length(list!!)/2)+part(list!!,1+length(list!!)/2))/2 else part(list!!,(length(list!!)+1)/2) end>>");
		p("Min.N", "min(%)");
		p("Midpoint.2", "multiplication((%0)+(%1),1/2)");
		p("Mod.2", "mod!!(%0,%1)");
		p("NextPrime.1", "if %0<2 then 2 else nextprime(%0)");
		p("NIntegral.3",
				"<<begin scalar input!!, var!!; input!!:=(%0); var!!:=mymainvar(input!!); input!!:=sub(var!!=variable!!, input!!); on numval, roundall$ return num\\_int(input!!,variable!!,%1,%2) end>>");
		p("NIntegral.4",
				"<<on numval, roundall$ num\\_int(%0,%1=((%2) .. (%3))) >>");
		p("Normal.3",
				"<<on pri; off rationalize; (1/sqrt(2*pi*(%1^2))) * exp(-((%2-%0)^2) / (2*(%1^2)))>>");
		p("nPr.2", "factorial(%0)/factorial(%0-%1)");
		p("NSolve.1",
				"<<begin scalar input!!; input!!:=(%0); on rounded, numval, roundall$ return mynumsolve(input!!,mymainvars(input!!, length(mkdepthone(list(input!!))))) end>>");
		p("NSolve.2",
				"<<on rounded, numval, roundall$ mynumsolve(%0,%1) >>");
		p("NSolutions.1",
				"<<begin scalar input!!; input!!:=(%0); on rounded, numval, roundall$ return map(rhs,mynumsolve(input!!,mymainvars(input!!, length(mkdepthone(list(input!!)))), iterations=10000)) end>>");
		p("NSolutions.2",
				"<<on rounded, numval, roundall$ map(rhs,mynumsolve(%0,%1)) >>");
		p("Numerator.1", "num(%0)");
		p("Numeric.1",
				"<<numeric!!:=1; on rounded, roundall, numval$ if printprecision!!<=16 then <<print\\_precision(printprecision!!)$ %0>> else <<precision(printprecision!!)$ print\\_precision(printprecision!!)$ %0 >> >>");
		p("Numeric.2",
				"<<numeric!!:=1; on rounded, roundall, numval$ if %1<=16 then <<print\\_precision(%1)$ %0>> else <<precision(%1)$ print\\_precision(%1)$ %0 >> >>");
		p("OrthogonalVector.1",
				"if myvecp then perpendicular %0 else if arglength(%0)>-1 and part(%0,0)=\\'mat then part mat((0,-1),(1,0))*(%0) else '?");
		//using sub twice in opposite directions seems to fix #2198, though it's sort of magic
		p("PartialFractions.1",
				"<<begin scalar input!!, tmpret!!;on factor; input!!:=(%0); tmpret!!:= pf(sub(mymainvar(input!!)=dum,input!!),dum); return part(sub(dum=mymainvar(input!!),tmpret!!),0):=+; end>>");
		p("PartialFractions.2", "<<on factor$ part(pf(%0,%1),0):=+>>");
		p("Pascal.4",
				"if %3=true then betaRegularized(%0,1+floor(%2),%1) else (1-(%1))^(%2)*(%1)^(%0)*binomial(%0+%2-1,%0-1)");
		p("Poisson.3",
				"if %2=true then exp(-(%0))*for i:=0:(%1) sum (%0)^i/factorial(floor(i)) else (%0)^(%1)/factorial(floor(%1))*exp(-%0)");
		p("PreviousPrime.1",
				"<<begin scalar tmp!!;return if (%0)>2 then <<tmp!!:=(%0)-1; while not(primep(tmp!!)) do tmp!!:=tmp!!-1; tmp!!>> else \\'? end>>");
		p("PrimeFactors.1",
				"<<off combinelogs, rounded; if (%0)=1 then {} else begin scalar factorlist!!; factorlist!!:=factorize(%0); factorlist!!:= for each x!! in factorlist!! collect for i:=1:part(x!!,2) collect part(x!!,1); return mkdepthone(factorlist!!) end>>");
		p("Product.1",
				"<< begin scalar input!!; input!!:=(%0); return for i:=1:length(input!!) product part(input!!,i) end >>");
		p("Product.4", "prod(%0,%1,%2,%3)");
		// p("Prog.1","<<%0>>");
		// p("Prog.2","<<begin scalar %0; return %1 end>>");
		p("MixedNumber.1",
				"<<begin scalar result!!, input!!; off pri; input!!:=(%0); if part(input!!,0) neq quotient then result!!:=list(input!!,0) else <<arguments!!:=(part(abs(input!!),0):=list); result!!:=list(sign(input!!)*div(first(arguments!!), second(arguments!!)), mod!!(first(arguments!!), second(arguments!!))/second(arguments!!))>>; return if part(result!!,2)=0 then part(result!!,1) else if part(result!!,1)=0 then sign(input!!)*part(result!!,2) else if input!!<0 then part(result!!,0):=- else part(result!!,0):=+ end>>");
		p("Random.2", "random(%1-%0+1)+%0");
		p("RandomBinomial.2",
				"<<on rounded, roundall, numval; for each x in for i:=1:%0 collect random(10000000)/10000000 sum if (x<%1) then 1 else 0 >>");
		p("RandomElement.1", "part(%0,random(length(%0))+1)");
		p("RandomPoisson.1",
				"<<on rounded, roundall, numval; begin scalar L!!, k!!, p!!; L!!:=exp(-%0); k!!:=0; p!!:=1; repeat <<k!!:=k!!+1; p!!:=p!!*(random(10000000)/10000000)>> until p!!<=L!!; return k!!-1 end>>");
		p("RandomNormal.2",
				"<<on rounded, roundall, numval; %0+(%1)*cos(2*pi*random(10000000)/10000000)*sqrt(-2*log(random(10000000)/10000000)) >>");
		p("RandomPolynomial.3",
				"<<begin scalar a, b, min!!, max!!; min!!:=myround(%1); max!!:=myround(%2); a:=for i:=0:%0-1 sum (random(max!!-(min!!)+1)+(min!!))*currentx!!^i; return a+currentx!!^(%0)*if min!!<=0 and max!!>0 then <<b:=random(max!!-(min!!))+(min!!); if b>=0 then b+1 else b>> else random(max!!-(min!!)+1)+min!! end>>");
		p("RandomPolynomial.4",
				"<<begin scalar a, b, min!!, max!!; min!!:=myround(%2); max!!:=myround(%3); a:=for i:=0:%1-1 sum (random(max!!-min!!+1)+min!!)*(%0)^i; return a+(%0)^(%1)*if min!!<=0 and max!!>0 then <<b:=random(max!!-min!!)+min!!; if b>=0 then b+1 else b>> else random(max!!-min!!+1)+min!! end>>");
		p("Rationalize.1", "<<off rounded; %0 >>");
		p("Reverse.1","reverse(%0)");
		p("RightSide.1",
				"<<begin scalar input!!; input!!:=(%0); return if arglength(input!!) and part(%0,0)=\\'list then map(rhs,input!!) else rhs(input!!) end>>");
		p("RightSide.2",
				"<<begin scalar input!!; input!!:=(%0); return if arglength(input!!) and part(%0,0)=\\'list then rhs(part(input!!,%1)) else \\'? end>>");
		p("Root.1",
				"<<begin scalar input!!; input!!:=(%0); return flattenlist(mysolve(input!!,mymainvar(input!!))) end>>");
		p("ReducedRowEchelonForm.1",
				"<<begin scalar tmpcolumn!!; clear tmpmatrix!!; tmpmatrix!!:=(%0)$ tmpcolumn!!:=1$ for i:=1:row\\_dim(tmpmatrix!!) do <<if tmpcolumn!!<column\\_dim(tmpmatrix!!) and tmpmatrix!!(i,tmpcolumn!!)=0 then while (tmpcolumn!!<=column\\_dim(tmpmatrix!!) and tmpmatrix!!(i,tmpcolumn!!)=0 and i<=row\\_dim(tmpmatrix!!)) do << for j:=i+1:row\\_dim(tmpmatrix!!) do if (j<=column\\_dim(tmpmatrix!!) and tmpmatrix!!(i,j) neq 0) then tmpmatrix!!:=swap\\_rows(tmpmatrix!!,tmpcolumn!!, j)$ if (tmpmatrix!!(i,tmpcolumn!!)=0) then tmpcolumn!!:=tmpcolumn!!+1 >>$ if tmpcolumn!!<=column\\_dim(tmpmatrix!!) then if tmpmatrix!!(i, tmpcolumn!!) neq 0 then << tmpmatrix!!:=pivot(tmpmatrix!!,i,tmpcolumn!!); tmpmatrix!!:=mult\\_rows(tmpmatrix!!,i,1/tmpmatrix!!(i,tmpcolumn!!))$ tmpcolumn!!:=tmpcolumn!!+1 >>; >>$ return tmpmatrix!! end>>");
		p("Sample.2",
				"<< begin scalar list!!; list!!:=(%0); return for i:=1:%1 collect part(list!!,1+random(length(list!!))) end >>");
		p("Sample.3",
				"<< begin scalar list!!; list!!:=(%0); return if %2=true then for i:=1:%1 collect part(list!!,1+random(length(list!!))) else <<list!!:=shuffle(list!!); for i:=1:%1 collect part(list!!,i)>> end >>");
		p("SampleVariance.1",
				"<< begin scalar n!!, list!!; list!!:=(%0); n!!:=length(list!!); return 1/(n!!-1)*(for each i in list!! sum i^2)-1/(n!!^2-n!!)*(for each i in list!! sum i)^2 end >>");
		p("SampleSD.1",
				"<< begin scalar n!!, list!!; list!!:=(%0); n!!:=length(list!!); return sqrt(1/(n!!-1)*(for each i in list!! sum i^2)-1/(n!!^2-n!!)*(for each i in list!! sum i)^2) end >>");
		p("Sequence.1", "<<listofliststomat(for i:=1:(%0) collect i)>>");
		p("Sequence.4",
				"<<listofliststomat(<<begin scalar %1; return for %1:=(%2):(%3) collect (%0) end>>)>>");
		p("Sequence.5",
				"<<listofliststomat(<<begin scalar %1; return for %1:=(%2) step (%4) until (%3) collect (%0) end>>)>>");
		p("SetSignificantNumbersNumeric.1", "printprecision!!:=(%0)");
		p("SD.1",
				"<<begin scalar tmpmean, tmplist; tmplist:=(%0)$ tmpmean:=0$ tmpmean:= (1/length(tmplist))*for i:=1:length(tmplist) sum part(tmplist,i)$ return sqrt((1/length(tmplist))*for i:=1:length(tmplist) sum (part(tmplist,i)^2-tmpmean^2)) end>>");
		p("Shuffle.1", "shuffle(%0)");
		p("Simplify.1", "<<on combinelogs,combineexpt; trigsimp(%0, combine)>>");
		// p("SimplifyFull.1","trigsimp(%0, combine)");
		p("Solutions.1",
				"<< begin scalar input!!; input!!:=(%0); return flattenlist(for each element!! in mysolve(input!!,mymainvar(input!!)) collect map(rhs,element!!)) end>>");
		p("Solutions.2",
				"flattenlist(for each element!! in mysolve(%0,%1) collect map(rhs,element!!))");
		p("Solve.1",
				"<<begin scalar input!!; input!!:=(%0);" +
				" return flattenlist(mysolve1(input!!)) end>>");
		p("Solve.2",
				"<< begin scalar equations!!; equations!!:=(%0); if arglength(equations!!)>-1 and part(equations!!,0)=\\'list then equations!!:=mkdepthone(equations!!); return flattenlist(mysolve(aeval(equations!!),%1)) end >>");
		p("SolveODE.1",
				"<<begin scalar tmpret, tmpeqn, result!!; korder list(); tmpeqn:=sub(list(currentx!!=x!!, currenty!!=y!!),(%0))$ depend y!!,x!!; if freeof(tmpeqn,=) then tmpeqn:=df(y!!,x!!)=tmpeqn; " +
				"tmpret:=odesolve(sub(list(%@y'=df(y!!,x!!),%@y''=df(y!!,x!!,2)),tmpeqn),y!!,x!!)$" +
				" nodepend y!!,x!!;korder varorder!!; let list(x!!=>currentx!!, y!!=>currenty!!); result!!:= if length(tmpret)=1 then sub(list(x!!=currentx!!, y!!=currenty!!),first(tmpret)) else sub(list(x!!=currentx!!, y!!=currenty!!),tmpret); clearrules list(x!!=currentx!!, y!!=currenty!!); return result!! end>>");
		p("SolveODE.2",
				"<<begin scalar tmpret, tmpeqn, result!!; korder list(); tmpeqn:=sub(list(currentx!!=x!!, currenty!!=y!!),(%0))$ depend y!!,x!!; if freeof(tmpeqn,=) then tmpeqn:=df(y!!,x!!)=tmpeqn; " +
				"tmpret:=odesolve(sub(list(%@y'=df(y!!,x!!),%@y''=df(y!!,x!!,2)),tmpeqn),y!!,x!!,list(x!!=xcoord(%1),y!!=ycoord(%1)))$" +
				" nodepend y!!,x!!;korder varorder!!; let list(x!!=>currentx!!, y!!=>currenty!!); result!!:= if length(tmpret)=1 then sub(list(x!!=currentx!!, y!!=currenty!!),first(tmpret)) else sub(list(x!!=currentx!!, y!!=currenty!!),tmpret); clearrules list(x!!=currentx!!, y!!=currenty!!); return result!! end>>");
		p("SolveODE.3",
				"if myvecp(%2) then "
				+		"<<begin scalar tmpret, tmpeqn, result!!; korder list(); tmpeqn:=sub(list(currentx!!=x!!, currenty!!=y!!),(%0))$ depend y!!,x!!; if freeof(tmpeqn,=) then tmpeqn:=df(y!!,x!!)=tmpeqn; " +
						"tmpret:=odesolve(sub(list(%@y'=df(y!!,x!!),%@y''=df(y!!,x!!,2)),tmpeqn),y!!,x!!,list(list(x!!=xcoord(%1),y!!=ycoord(%1)),list(x!!=xcoord(%2),df(y!!,x!!)=ycoord(%2))))$" +
						" nodepend y!!,x!!;korder varorder!!; let list(x!!=>currentx!!, y!!=>currenty!!); result!!:= if length(tmpret)=1 then sub(list(x!!=currentx!!, y!!=currenty!!),first(tmpret)) else sub(list(x!!=currentx!!, y!!=currenty!!),tmpret); clearrules list(x!!=currentx!!, y!!=currenty!!); return result!! end>>"+
				" else <<begin scalar tmpret, tmpeqn; korder list(); tmpeqn:=(%0)$ depend %1,%2; if freeof(tmpeqn,=) then tmpeqn:=df(%1,%2)=tmpeqn; " +
				"tmpret:=odesolve(sub(list(@1'=df(@1,%2),@1''=df(@1,%2,2)),tmpeqn),%1,%2)$" +
				" nodepend %1,%2; korder varorder!!; return if length(tmpret)=1 then first(tmpret) else tmpret end>>");
		p("SolveODE.4",
				"<<begin scalar tmpret, tmpeqn; korder list(); tmpeqn:=(%0)$ depend %1,%2; if freeof(tmpeqn,=) then tmpeqn:=df(%1,%2)=tmpeqn; " +
				"tmpret:=odesolve(sub(list(%1'=df(%1,%2),%1''=df(%1,%2,2)),tmpeqn),%1,%2,list(%2=xcoord(%3),%1=ycoord(%3)))$" +
				" nodepend %1,%2; korder varorder!!; return if length(tmpret)=1 then first(tmpret) else tmpret end>>");
		p("SolveODE.5",
				"<<begin scalar tmpret, tmpeqn; korder list(); tmpeqn:=(%0)$ depend %1,%2; if freeof(tmpeqn,=) then tmpeqn:=df(%1,%2)=tmpeqn; " +
				"tmpret:=odesolve(sub(list(%1'=df(%1,%2),%1''=df(%1,%2,2)),tmpeqn),%1,%2,list(list(%2=xcoord(%3),%1=ycoord(%3)),list(%2=xcoord(%4),df(%1,%2)=ycoord(%4))))$" +
				" nodepend %1,%2; korder varorder!!; return if length(tmpret)=1 then first(tmpret) else tmpret end>>");
		p("Substitute.2",
				"<<if keepinput!!=1 then <<keepinput!!:=2; sub(%1, !*hold(%0)) >> else begin scalar rulelist!!, replacements!!; replacements!!:=(%1); if arglength(replacements!!)>-1 and part(replacements!!,0)=\\'list then 0 else replacements!!:=list(replacements!!); rulelist!!:= for each element in replacements!! collect part(element,0):=replaceby; return %0 where rulelist!! end>>");
		// p("Substitute.3","if hold!!=0 then sub(%1=(%2),%0) else sub(%1=(%2),!*hold(%0))");
		p("Substitute.3",
				"<<if keepinput!!=1 then <<keepinput!!:=2; sub(%1=(%2), !*hold(%0)) >> else begin scalar rulelist!!; rulelist!!:=list(%1,%2); rulelist!!:=list(part(rulelist!!,0):=replaceby); return %0 where rulelist!! end>>");
		// p("SubstituteParallel.2","if hold!!=0 then sub(%1,%0) else sub(%1,!*hold(%0))");
		p("Sum.1",
				"<<begin scalar input!!; input!!:=(%0);return for i:=1:length(input!!) sum part(input!!,i) end>>");
		p("Sum.4",
				"<<begin scalar sb, sm!!; sb:=sum(%0,%1,%2,%3)$ sm!!:=if freeof(sb,\\'sum) and freeof(sb,\\'infinity) then sb else if %2=-infinity and %3=infinity then limit(limit(sum(%0,%1,k!!,m!!),k!!,-infinity),m!!,infinity) else if %2=-infinity then limit(sum(%0,%1,k!!,%3),k!!,-infinity) else if %3=infinity then limit(sum(%0,%1,%2,k!!),k!!,infinity) else sb; return sm!! end>>");
		p("Tangent.2",
				"if myvecp(%0) then mytangent(xcoord(%0),(%1)) else mytangent((%0),(%1))");
		p("Take.3",
				"<<begin scalar tmpret, list!!; list!!:=(%0); tmpret:=list()$ for i:=(%1):%2 do tmpret:=part(list!!,i).tmpret$ return reverse(tmpret) end>>");
		p("TaylorSeries.3",
				"<< begin scalar tmpret; korder list(); off pri; tmpret:=taylor(%0,currentx!!,%1,%2); korder varorder!!; return if taylortostd =1 then taylortostandard(tmpret) else tmpret end>>");
		p("TaylorSeries.4",
				"<< begin scalar tmpret; korder list(); off pri; tmpret:=taylor(%0,%1,%2,%3); korder varorder!!; return if taylortostd =1 then taylortostandard(tmpret) else tmpret end>>");
		p("TDistribution.2",
				"<<begin scalar t!!,n!!; n!!:=(%0); t!!:=(%1) ;beta!Regularized(((t!!+sqrt(t!!^2+n!!)/(2*sqrt(t!!^2+n!!)),n!!/2,n!!/2)");
		p("ToComplex.1",
				"<<begin scalar list!!; list!!:=(%0); return if myvecp list!! then get(list!!,0)+i*get(list!!,1) else part(list!!,1)+i*part(list!!,2) end >>");
		p("ToExponential.1",
				"<< begin scalar real!!, imag!!, input!!, mag!!, phi!!; input!!:=(%0); if arglength(input!!)>-1 and part(input!!,0)=\\'list then <<real!!:= part(input!!,1); imag!!:=part(input!!,2)>> else <<real!!:= repart(input!!); imag!!:=impart(input!!)>>;mag!!:=sqrt(real!!^2+imag!!^2); phi!!:=myatan2(imag!!,real!!); return if mag!!=0 then 0 else if phi!!=0 then mag!! else if mag!!=1 then part(list(i*phi!!),0):=exp else part(list(mag!!,part(list(i*phi!!),0):=exp),0):=* end>>");
		p("ToPolar.1",
				"<< begin scalar input!!, r!!, phi!!;" +
				" input!!:=(%0);" +
				" if myvecp(input!!) then <<" +
				//the input was a point
				"   r!! := sqrt(aeval(xvcoord(input!!))^2+aeval(yvcoord(input!!))^2);" +
				"   phi!! := myatan2(yvcoord(input!!),xvcoord(input!!))" +
				" >> else <<" +
				//the input was an imaginary number
				"  r!! := sqrt(repart(input!!)^2+impart(input!!)^2);" +
				"  phi!!:= myatan2(impart(input!!),repart(input!!))" +
				" >>;" +
				" return if phi!!=\\'? then" +
				"   \\'?" +
				" else" +
				"   part(list(r!!,phi!!),0):=polartopoint!\u00a7 end >>");
		p("ToPoint.1",
				"<< begin scalar input!!; input!!:=(%0); return if arglength(input!!)>-1 and part(input!!,0)=\\'list then listtomyvect input!! else myvect(repart(input!!),impart(input!!)) end >>");
		p("Transpose.1", "tp(<<listofliststomat(%0)>>)");
		// http://reduce-algebra.com/docs/trigsimp.pdf
		p("TrigExpand.1",
				"sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0)))");
		p("TrigExpand.2",
				"sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0), part(%1,0)))");
		p("TrigExpand.3",
				"<< begin scalar input1!!; input1!!:=sub(list(currentx!!=x),%2); return sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0), input1!!, part(%1,0))) end >>");
		p("TrigExpand.4",
				"<< begin scalar input1!!, input2!!; input1!!:=sub(list(currentx!!=x),%2); input2!!:=sub(list(currentx!!=x),%3); return sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0), input1!!, input2!!, part(%1,0))) end >>");
		p("TrigSimplify.1",
				"sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0),compact))");
		p("TrigCombine.1",
				"sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0),combine))");
		p("TrigCombine.2",
				"sub(list(x=currentx!!),trigsimp(sub(list(currentx!!=x),%0),part(%1,0),combine))");
		p("Unique.1", "mkset(%0)");
		p("UnitOrthogonalVector.1",
				"<<begin; clear mat!!,norm!!; input!!:=(%0);return if myvecp input!! then unitperpendicular input!! else if length(input!!)=list(2,1) then <<norm!!:=sqrt(<<for i:=1:row\\_dim(input!!) sum input!!(i,1)^2>>); norm!!*mat((0,-1),(1,0))*input!! >> else if length(input!!)=list(1,2) then <<norm!!:=sqrt(<<for i:=1:column\\_dim(input!!) sum input!!(1,i)^2>>); norm!!*input!!*mat((0,-1),(1,0)) >> else \\'? end >>");
		p("UnitVector.1",
				"<<begin; clear input!!; input!!:=(%0); return if myvecp input!! then unitvector input!! else input!!/sqrt(<<for i:=1:row\\_dim(input!!) sum input!!(i,1)^2>>) end>>");
		p("Variance.1",
				"<<begin scalar x!!,n!!,xd!!; x!!:=(%0)$ n!!:=length(x!!)$ xd!!:=1/n!!*for each i in x!! sum i; return 1/n!!* for each i in x!! sum (i-xd!!)**2 end>>");
		p("Weibull.3", "1-exp(-(%0)*(%2)^(%1))");
		p("Zipf.4",
				"<<begin scalar s; s:= %1; return if %3=true then harmonic(%2,s)/harmonic(%0,s) else 1/((%2)^s*harmonic(%0,s)) end>>");
		return commandMap;
	}
}