package org.geogebra.common.cas.giac;

import java.util.Map;
import java.util.TreeMap;

/***
 * # Command translation table from GeoGebra to giac # e.g. Factor[ 2(x+3) ] is
 * translated to factor( 2*(x+3) ) ###
 * 
 * Giac Constants
 * 
 * 
 * DOM_INT = 2 DOM_IDENT = 6 DOM_LIST = 7 DOM_RAT = 10
 */

public class Ggb2giac {
	private static Map<String, String> commandMap = new TreeMap<String, String>();

	/**
	 * @param signature
	 *            GeoGebra command signature (i.e. "Element.2")
	 * @param casSyntax
	 *            CAS syntax, parameters as %0,%1
	 */
	public static void p(String signature, String casSyntax) {

		// replace _ with \_ to make sure it's not replaced with "unicode95u"

		commandMap.put(signature, casSyntax.replace("_", "\\_"));
	}

	/**
	 * @return map signature => syntax
	 */
	public static Map<String, String> getMap() {
		p("Append.2", "append(%0,%1)");
		// simplify() to make sure Binomial[n,1] gives n
		p("Binomial.2", "simplify(binomial(%0,%1))");
		p("BinomialDist.4",
				"[[[ggbbinarg0:=%0], [ggbbinarg1:=%1], [ggbbinarg2:=%2]],"
						+ "if %3=true then binomial_cdf(ggbbinarg0,ggbbinarg1,ggbbinarg2) else binomial(ggbbinarg0,ggbbinarg2,ggbbinarg1) fi][1]");

		p("Cauchy.3", "normal(1/2+1/pi*atan(((%2)-(%1))/(%0)))");

		// factor over complex rationals
		// [ggbans:=%0] first in case something goes wrong, eg CFactor[sqrt(21)
		// - 2sqrt(7) x <complexi> + 3sqrt(3) x^2 <complexi> + 6x^3]
		p("CFactor.1",
				"[with_sqrt(0),[ggbcfactans:=%0],[ggbcfactans:=cfactor(ggbcfactans)],with_sqrt(1),ggbcfactans][4]");
		p("CFactor.2",
				"[with_sqrt(0),[ggbcfactans:=%0],[ggbcfactans:=cfactor(ggbcfactans,%1)],with_sqrt(1),ggbcfactans][4]");

		// factor over complex irrationals
		p("CIFactor.1",
				"[with_sqrt(1),[ggbcfactans:=%0],[ggbcfactans:=cfactor(ggbcfactans)],ggbcfactans][3]");
		p("CIFactor.2",
				"[with_sqrt(1),[ggbcfactans:=%0],[ggbcfactans:=cfactor(ggbcfactans,%1)],ggbcfactans][3]");

		p("ChiSquared.2",
		// "chisquare_cdf(%0,%1)");
				"igamma(%0/2,%1/2,1)");
		p("Coefficients.1", "when(is_polynomial(%0)," + "coeffs(%0)," + "{})");

		p("Coefficients.2", "coeffs(%0,%1)");
		p("CompleteSquare.1", "canonical_form(%0)");
		p("CommonDenominator.2", "lcm(denom(%0),denom(%1))");
		p("Covariance.2", "covariance(%0,%1)");
		p("Covariance.1", "normal(covariance(%0))");

		// also in ExpressionNode for crossed-circle
		p("Cross.2",
				"[[[ggbcrossarg0:=%0], [ggbcrossarg1:=%1]],when(is3dpoint(ggbcrossarg0)||is3dpoint(ggbcrossarg1),point(cross(ggbcrossarg0,ggbcrossarg1)),cross(ggbcrossarg0,ggbcrossarg1))][1]");

		p("ComplexRoot.1", "normal(cZeros(%0,x))");
		p("CSolutions.1",
				"ggbsort([[[ggbcsans:=0/0],[ggbcsans:=%0],[ggbvars:=lname(ggbcsans)]],"
						+ "normal(cZeros(%0,when(size(ggbvars)==1,ggbvars[0],x)))][1])");
		p("CSolutions.2", "ggbsort(normal(cZeros(%0,%1)))");

		// DO NOT wrap in normal() otherwise these don't work
		// CSolve[z^2=k a^2,z]
		// CSolve[3z^2-2a*(k+1)*z+a^2*k=0,z]
		// (alternative is assume(ggbtmpvara, complex))
		p("CSolve.1",
				"ggbsort([[[ggbcsans:=0/0],[ggbcsans:=%0],[ggbvars:=lname(ggbcsans)]],"
						+ "regroup(csolve(%0,when(size(ggbvars)==1,ggbvars[0],x)))][1])");

		p("CSolve.2", "ggbsort(regroup(csolve(%0,%1)))");
		p("Degree.1", "total_degree(%0,lname(%0))");
		p("Degree.2", "degree(%0,%1)");

		// denom() cancels down first
		// p("Denominator.1", "denom(%0)");
		p("Denominator.1", "getDenom(%0)");

		// this chooses x if it's in the expression
		// otherwise the first variable alphabetcially
		// when(count_eq(x,lname(%0))==0,lname(%0)[0],x)

		p("Derivative.1",
				"[[ggbderivarg0:=%0], regroup(diff(ggbderivarg0, "
						+
						// check for constant
						"when(length(lname(ggbderivarg0))==0,x,"
						+ "when(count_eq(x,lname(ggbderivarg0))==0,lname(ggbderivarg0)[0],x))))][1]");
		p("Derivative.2",
				"[[[ggbderiv2arg0:=%0],[ggbderiv2arg1:=%1]],"
						+ "when(type(ggbderiv2arg1)==DOM_INT,"
						+ "regroup(diff(ggbderiv2arg0,when(count_eq(x,lname(ggbderiv2arg0))==0,lname(%0)[0],x),ggbderiv2arg1))"
						+ "," + "regroup(diff(ggbderiv2arg0,ggbderiv2arg1))"
						+ ")][1]");

		p("Derivative.3", "regroup(diff(%0,%1,%2))");
		p("Determinant.1", "det(%0)");
		p("Dimension.1",
				"[[ggbdimarg:=%0], when(ggbdimarg[0]=='pnt',when(is3dpoint(ggbdimarg),3,2),dim(ggbdimarg))][1]");
		p("Div.2",
				"[[[ggbdivarg0:=%0],[ggbdivarg1:=%1]],if type(ggbdivarg0)==DOM_INT && type(ggbdivarg1)==DOM_INT then iquo(ggbdivarg0,ggbdivarg1) else quo(ggbdivarg0,ggbdivarg1,x) fi][1]");
		p("Division.2",
				"[[[ggbdivarg0:=%0],[ggbdivarg1:=%1]],if type(ggbdivarg0)==DOM_INT && type(%1)==DOM_INT then iquorem(ggbdivarg0,ggbdivarg1) else quorem(ggbdivarg0,ggbdivarg1,x) fi][1]");
		p("Divisors.1", "dim(idivis(%0))");
		p("DivisorsList.1", "idivis(%0)");
		p("DivisorsSum.1", "sum(idivis(%0))");

		// p("Dot.2", "regroup(dot(%0,%1))");
		// p("Dot.2",
		// "[[[ggbdotarg0:=%0], [ggbdotarg1:=%1]], regroup(dot(ggbdotarg0,ggbdotarg1))][1]");
		p("Dot.2",
				"[[[ggbdotarg0:=%0], [ggbdotarg1:=%1]],"+
						"when(type(ggbdotarg0)==DOM_LIST && subtype(ggbdotarg0)!=27,"+
						// eg lists length 4 (and not ggbvect)
						"regroup(ggbdotarg0 * ggbdotarg1)"+
						","+
						" regroup(xcoord(ggbdotarg0)*xcoord(ggbdotarg1)+ycoord(ggbdotarg0)*ycoord(ggbdotarg1)+zcoord(ggbdotarg0)*zcoord(ggbdotarg1))"+
						")][1]");

		// GeoGebra indexes lists from 1, giac from 0

		// equations:
		// (4x-3y=2x+1)[0] ='='
		// (4x-3y=2x+1)[1] = left side
		// (4x-3y=2x+1)[2] = right side

		// expressions:
		// (4x+3y-1)[0] = '+' -- no way to handle in GGB, return ?
		// (4x+3y-1)[1] = 4x
		// (4x+3y-1)[2] = 3y
		// (4x+3y-1)[3] = -1
		p("Element.2",
				"[[[ggbelarg0:=%0], [ggbelarg1:=%1]],when(type(ggbelarg0)==DOM_LIST,(ggbelarg0)[ggbelarg1-1],when(ggbelarg1>0,(ggbelarg0)[ggbelarg1],?))][1]");

		// if %0[0]=='=' then %0[%1] else when(...) fi;

		// GeoGebra indexes lists from 1, giac from 0
		p("Element.3", "(%0)[%1 - 1,%2 - 1]");

		p("Eliminate.2", "eliminate(%0,%1)");

		// used in regular mode
		// Giac doesn't auto-simplify
		// normal so f(x):=(x^2-1)/(x-1) -> x+1 (consistent with Reduce)
		// regroup so that r*r^n -> r^(n+1)
		// regroup/normal swapped for improved variable order eg x^2 + a*x + b
		// #5500 don't expand brackets automatically
		p("Evaluate.1",
				"[[[ggbevalarg:=%0],[ggbevalans:=regroup(normal(ggbevalarg))],[ggbevalans2:=regroup(ggbevalarg)]], when(length(\"\"+ggbevalans)<=length(\"\"+ggbevalans2),ggbevalans,ggbevalans2)][1]");

		// split into real + imag #4522
		p("Expand.1", "normal(real(%0)) + normal(i*im(%0))");
		p("Exponential.2", "1-exp(-(%0)*(%1))");

		p("Extremum.1",
				"[[[ggbextremumfun:=%0], [ggbans:=extrema(%0)]], seq(point(ggbans[j],ggbextremumfun(ggbans[j])),j,0,size(ggbans)-1) ][1]");

		// factor over rationals
		// add x so that Factor[(-k x^2 + 4k x + x^3)] gives a nicer answer
		p("Factor.1",
				"[with_sqrt(0),[ggbfacans:=%0],[if type(ggbfacans)==DOM_INT then ggbfacans:=ifactor(ggbfacans); else ggbfacans:=factor(ggbfacans,x); fi],with_sqrt(1),ggbfacans][4]");
		p("Factor.2",
				"[with_sqrt(0),[ggbfacans:=%0],[ggbfacans:=factor(ggbfacans,%1)],with_sqrt(1),ggbfacans][4]");

		// factor over irrationals
		p("IFactor.1",
				"[with_sqrt(1),[ggbfacans:=%0],[if type(ggbfacans)==DOM_INT then ggbfacans:=ifactor(ggbfacans); else ggbfacans:=factor(ggbfacans,x); fi],ggbfacans][3]");
		p("IFactor.2",
				"[with_sqrt(1),[ggbfacans:=%0],[ggbfacans:=factor(ggbfacans,%1)],ggbfacans][3]");

		// convert {x-1,1,x+1,1} to {{x-1,1},{x+1,1}}
		p("Factors.1",
				// "factors(%0)");
				"[[ggbfacans:=%0],[if type(ggbfacans)==DOM_INT then calc_mode(0); ggbfacans:=ifactors(ggbfacans); calc_mode(1); else ggbfacans:=factors(ggbfacans); fi],matrix(dim(ggbfacans)/2,2,ggbfacans)][2]");
		p("FDistribution.3", "fisher_cdf(%0,%1,%2)");
		// alternative for exact answers
		// "Beta(exact(%0)/2,%1/2,%0*%2/(%0*%2+%1),1)");

		p("Flatten.1", "flatten(%0)");

		p("First.1",
				"[[ggbfiarg0:=%0],{when(type(ggbfiarg0)==DOM_LIST,(ggbfiarg0)[0],(ggbfiarg0)[1])}][1]");
		p("First.2",
				"[[[ggbfiarg0:=%0],[ggbfiarg1:=%1]],when(type(ggbfiarg0)==DOM_LIST,(ggbfiarg0)[0..ggbfiarg1-1],seq((ggbfiarg0)[j],j,1,ggbfiarg1))][1]");

		// These implementations follow the one in GeoGebra
		p("FitExp.1",
				"[[ggbfitans:=%0],[ggbfitans:=exponential_regression(ggbfitans)],evalf(ggbfitans[1])*exp(ln(evalf(ggbfitans[0]))*x)][2]");
		p("FitLog.1",
				"[[ggbfitans:=%0],[ggbfitans:=logarithmic_regression(%0)],evalf(ggbfitans[0])*ln(x)+evalf(ggbfitans[1])][2]");

		p("FitPoly.2",
				"[[[ggbfitpans:=0/0], [ggbvar:=x], [ggbinput:=%0], [ggborder:=%1], "
						+ "when(ggborder + 1 == size(ggbinput),"
						// use exact fit when correct number of points
						// eg FitPoly[ {(-6.64803509914449, -9.72031412828010),
						// (7.22538138096244, 7.18002958385020),
						// (20.0000000000000, -20.0000000000000),
						// (32.4497749811568, -13.2517292323073),
						// (-10.4316941391736, -13.5039731683093)} , 4]
						// adapted from Polynomial.N + evalf()
						+ "[[xvals := [seq(evalf(xcoord(ggbinput[j])),j=0..size(ggbinput)-1)]], [yvals := [seq(evalf(ycoord(ggbinput[j])),j=0..size(ggbinput)-1)]], [ggbfitpans := normal(lagrange(xvals,yvals,x))]]"
						+ ","
						// eg FitPoly[ {(0.44, 0.42), (1.7, 0.48), (2.7, 1.2),
						// (3.5, 1.78), (4.36, 2.64), (5.12, 3.76), (5.78,
						// 4.66)} ,3]
						+ "[ggbfitpans:=normal(evalf(horner(polynomial_regression(%0,%1),x)))]"
						+ ")],ggbfitpans][1]");

		p("FitPow.1",
				"[[ggbfitans:=%0],[ggbfitans:=power_regression(ggbfitans)],evalf(ggbfitans[1])*x^evalf(ggbfitans[0])][2]");

		// Function[sin(x),0, 2 pi]
		// Function[sin(p),0, 2 pi]
		// Function[5,0, 1]
		p("Function.3",
				"[[ggbvars:=lname(%0)],[ggbvar:=when(size(ggbvars)==0,x,ggbvars[0])], when(ggbvar>=%1 && ggbvar<=%2, %0, undef)][2]");

		p("Gamma.3", "igamma((%0),(%2)/(%1),1)");
		p("GCD.2", "gcd(%0,%1)");
		p("GCD.1", "lgcd(%0)");
		// GetPrecision.1

		// Groebner basis related commands.
		// See http://en.wikipedia.org/wiki/Gr%C3%B6bner_basis#Monomial_ordering
		// to learn more about the following.
		// Also http://en.wikipedia.org/wiki/Monomial_order is very helpful.
		// Naming convention follows the (first) Wikipedia article, however,
		// other pieces of software
		// (like Sage) may have different names. There is no common scientific
		// naming for the orderings.
		// 1. (Pure) lexicographical ordering (original, "classical" method):
		p("GroebnerLex.1", "gbasis(%0,indets(%0),plex)");
		p("GroebnerLex.2", "gbasis(%0,%1,plex)");
		// We will not use the former "Groebner" command since for educational
		// purposes it is crucial
		// to make an emphasis on the monomial ordering.
		// 2. Total degree reverse lexicographical ordering (best method), also
		// called as "grevlex":
		p("GroebnerDegRevLex.1", "gbasis(%0,indets(%0),revlex)");
		p("GroebnerDegRevLex.2", "gbasis(%0,%1,revlex)");
		// 3. Total degree lexicographical ordering (useful for elimination),
		// also called as "grlex":
		p("GroebnerLexDeg.1", "gbasis(%0,indets(%0),tdeg)");
		p("GroebnerLexDeg.2", "gbasis(%0,%1,tdeg)");

		p("HyperGeometric.5",
				"[[m:=%1],[ng:=%0],[n:=%2],[kk:=%3],if %4=true then sum(binomial(m,k)*binomial((ng-m),(n-k))/binomial(ng,n),k,0,floor(kk)) "
						+ "else binomial(m,kk)*binomial((ng-m),(n-kk))/binomial(ng,n) fi][4]");
		p("Identity.1", "identity(round(%0))");

		// #4705
		p("If.2", "when(%0,%1,undef)");
		p("If.3", "when(%0,%1,%2)");

		// normal(regroup()) so that ImplicitDerivative[x^2 + y^2, y, x] gives a
		// nice answer
		// the danger is that this could multiply something out eg (x+1)^100
		// (unlikely)
		p("ImplicitDerivative.3", "normal(regroup(-diff(%0,%2)/diff(%0,%1)))");
		p("ImplicitDerivative.1", "normal(regroup(-diff(%0,x)/diff(%0,y)))");

		p("Integral.1", "regroup(integrate(%0))");
		p("Integral.2", "regroup(integrate(%0,%1))");

		// The symbolic value of the integral is checked against a numeric
		// evaluation of the integral
		// if they return different answers then a list with both values is
		// returned.
		// get the first element of the list to ignore the warning
		// simplify() added to improve Integral[2*exp(0.5x),0,ln(4)]
		p("Integral.3",
				"[[[ggbintans:=0/0],[ggbintans:=integrate(%0,%1,%2)]],"
				+ "normal(when(type(ggbintans)==DOM_LIST,ggbintans[0],simplify(ggbintans)))][1]");
		p("Integral.4",
				"[[[ggbintans:=0/0],[ggbintans:=integrate(%0,%1,%2,%3)]],"
						+ "normal(when(type(ggbintans)==DOM_LIST,ggbintans[0],simplify(ggbintans)))][1]");
		p("IntegralBetween.4",
				"[[[ggbintans:=0/0],[ggbintans:=int(%0-(%1),x,%2,%3)]],"
						+ "normal(when(type(ggbintans)==DOM_LIST,ggbintans[0],simplify(ggbintans)))][1]");
		p("IntegralBetween.5",
				"[[[ggbintans:=0/0],[ggbintans:=int(%0-(%1),%2,%3,%4)]],"
						+ "normal(when(type(ggbintans)==DOM_LIST,ggbintans[0],simplify(ggbintans)))][1]");

		// need to wrap in coordinates() for
		// Intersect[Curve[t,t^2,t,-10,10],Curve[t2,1-t2,t2,-10,10] ]
		// but not for Intersect[x^2,x^3]
		// ggbans:=0/0 to make sure if there's an error, we don't output
		// previous answer
		// add y= so that Intersect[(((2)*(x))+(1))/((x)-(5)),y=2] ie
		// Intersect[f,a] works
		p("Intersect.2",
				"[[ggbinterans:=0/0],"
						+

						"[ggbinarg0:=%0],"
						+ "[ggbinarg1:=%1],"
						+ "[ggbinterans:=normal(inter(when(ggbinarg0[0]=='=',ggbinarg0,y=ggbinarg0),when(ggbinarg1[0]=='=',ggbinarg1,y=ggbinarg1)))],[ggbinterans:=when(ggbinterans=={},ggbinterans,when(type(ggbinterans[0])==DOM_LIST,ggbinterans,coordinates(ggbinterans)))],ggbinterans][5]");

		// Giac currently uses approximation for this
		// p("Conic.5", "equation(conic((%0),(%1),(%2),(%3),(%4)))");

		// http://www.had2know.com/academics/conic-section-through-five-points.html
		// exact method
		p("Conic.5",
				"[[[M:=0/0],[A:=(0/0,0/0)],[B:=(0/0,0/0)],[C:=(0/0,0/0)],[D:=(0/0,0/0)],[E:=(0/0,0/0)]],"
						+
						// "[[A:=%0],[B:=%1],[C:=%2],[D:=%3],[E:=%4]],"+
						"[[A:=(real(%0[1]),im(%0[1]))],[B:=(real(%1[1]),im(%1[1]))],[C:=(real(%2[1]),im(%2[1]))],[D:=(real(%3[1]),im(%3[1]))],[E:=(real(%4[1]),im(%4[1]))],],"
						+ "[M:={{x^2,x*y,y^2,x,y,1},{A[0]^2,A[0]*A[1],A[1]^2,A[0],A[1],1},{B[0]^2,B[0]*B[1],B[1]^2,B[0],B[1],1},{C[0]^2,C[0]*C[1],C[1]^2,C[0],C[1],1},{D[0]^2,D[0]*D[1],D[1]^2,D[0],D[1],1},{E[0]^2,E[0]*E[1],E[1]^2,E[0],E[1],1}}],"
						+ "[M:=det(M)],"
						+

						// eg Conic[(5,0),(-5,0),(0,5),(0,-5),(4,1)]
						// simplify to x^2 +2 x y + y^2 - 25 from 10000x^2 + 20000x
						// y + 10000y^2 - 250000
						"[hcf:=factors(M)[0]],"
						+ "when(type(hcf)==DOM_INT,normal(M/hcf)=0,M=0)"
						+ "][5]");

		// version using Giac's internal commands: slower and not robust
		// (converts to parametric form as an intermediate step)
		// Ellipse[point, point, point/number]
		// p("Ellipse.3", "equation(ellipse(%0,%1,%2))");
		// Hyperbola[point, point, point/number]
		// p("Hyperbola.3", "equation(hyperbola(%0,%1,%2))");

		// adapted from GeoConicND.setEllipseHyperbola()
		final String ellipseHyperbola1 = "[[" + "[ggbellipsearg0:=%0],"
				+ "[ggbellipsearg1:=%1]," + "[a:=0/0]," + "[b1:=0/0],"
				+ "[b2:=0/0]," + "[c1:=0/0]," + "[c2:=0/0]," + "[a:=%2],"
				+ "[b1:=xcoord(ggbellipsearg0)],"
				+ "[b2:=ycoord(ggbellipsearg0)],"
				+ "[c1:=xcoord(ggbellipsearg1)],"
				+ "[c2:=ycoord(ggbellipsearg1)]," +
				// AlgoEllipseFociPoint, AlgoHyperbolaFociPoint
				"[a := when(%2[0]=='pnt',(sqrt((b1-real(a[1]))^2+(b2-im(a[1]))^2) ";

		final String ellipseHyperbola2 = "sqrt((c1-real(a[1]))^2+(c2-im(a[1]))^2))/2,a)],"
				+ "[diff1 := b1 - c1],"
				+ "[diff2 := b2 - c2],"
				+ "[sqsumb := b1 * b1 + b2 * b2],"
				+ "[sqsumc := c1 * c1 + c2 * c2],"
				+ "[sqsumdiff := sqsumb - sqsumc],"
				+ "[a2 := 2 * a],"
				+ "[asq4 := a2 * a2],"
				+ "[asq := a * a],"
				+ "[afo := asq * asq],"
				+ "[ggbeherans:=simplify(4 * (a2 - diff1) * (a2 + diff1) * x^2 -8 * diff1 * diff2 * x * y + 4 * (a2 - diff2) * (a2 + diff2)* y^2 -4 * (asq4 * (b1 + c1) - diff1 * sqsumdiff)*x - 4 * (asq4 * (b2 + c2) - diff2 * sqsumdiff)*y-16 * afo - sqsumdiff * sqsumdiff + 8 * asq * (sqsumb + sqsumc))]],"
				+
				// simplify (...)/1000 = 0
				"when(type(denom(ggbeherans))==DOM_INT,numer(ggbeherans)=0,ggbeherans=0)][1]";

		// simplify (...)/1000 = 0
		// "[ggbans:=numer(ggbans)],"+
		// simplify eg 28x^2 - 24x y - 160x + 60y^2 - 96y + 256 = 0
		// "[hcf:=factors(ggbans)[0]]],"+
		// "when(type(hcf)==DOM_INT,normal(ggbans/hcf)=0,ggbans=0)][1]";

		p("Ellipse.3", ellipseHyperbola1 + "+" + ellipseHyperbola2);

		p("Hyperbola.3", ellipseHyperbola1 + "-" + ellipseHyperbola2);
		p("Intersection.2", "%0 intersect %1");
		p("Iteration.3", "regroup((unapply(%0,x)@@%2)(%1))");
		p("IterationList.3",
				"[[ggbilans(f,x0,n):=begin local l,k; l:=[x0]; for k from 1 to n do l[k]:=regroup(f(l[k-1])); od; l; end],ggbilans(unapply(%0,x),%1,%2)][1]");
		p("PointList.1", "flatten1(coordinates(%0))");
		p("RootList.1", "apply(x->convert([x,0],25),%0)");
		p("Invert.1",
				"[[ggbinvans:=0/0], [ggbinvarg:=%0], [ggbinvans:=when(type(ggbinvarg)!=DOM_LIST,"
						+
						// invert function (answer is function, not mapping)
						"subst(right(revlist([op(solve(tmpvar=ggbinvarg,lname(ggbinvarg)[0]))])[0]),tmpvar,lname(ggbinvarg)[0])"
						+ "," +
						// invert matrix
						"inv(ggbinvarg))" + "],ggbinvans][3]");

		p("IsPrime.1", "isprime(%0)");

		// flatten1 is non-recursive flatten
		p("Join.N", "flatten1(%)");

		p("Laplace.1",
				"[[ggblaparg0:=%0],when(lname(ggblaparg0)[0]=ggbtmpvart, laplace(ggblaparg0, ggbtmpvart, ggbtmpvars), laplace(ggblaparg0, lname(ggblaparg0)[0]))][1]");
		p("Laplace.2", "laplace(%0, %1)");
		p("Laplace.3", "laplace(%0, %1, %2)");
		p("InverseLaplace.1",
				"[[ggblaparg0:=%0],when(lname(ggblaparg0)[0]=ggbtmpvars, ilaplace(ggblaparg0, ggbtmpvars, ggbtmpvart), ilaplace(ggblaparg0, lname(ggblaparg0)[0]))][1]");
		p("InverseLaplace.2", "ilaplace(%0, %1)");
		p("InverseLaplace.3", "ilaplace(%0, %1, %2)");

		p("Last.1",
				"[[ggblastarg0:=%0],{when(type(ggblastarg0)==DOM_LIST,(ggblastarg0)[size(ggblastarg0)-1],(ggblastarg0)[dim(ggblastarg0)])}][1]");
		p("Last.2",
				"[[[ggblastarg0:=%0],[ggblastarg1:=%1]],when(type(ggblastarg0)==DOM_LIST,(ggblastarg0)[size(ggblastarg0)-ggblastarg1..size(ggblastarg0)-1],seq((ggblastarg0)[j],j,dim(ggblastarg0)-ggblastarg1+1,dim(ggblastarg0)))][1]");

		p("LCM.1", "lcm(%0)");
		p("LCM.2", "lcm(%0,%1)");
		p("LeftSide.1",
				"[[ggbleftarg0:=%0],when(type(ggbleftarg0)==DOM_LIST,map(ggbleftarg0,left),left(ggbleftarg0))][1]");
		p("LeftSide.2", "left(%0[%1-1])");
		// subtype 27 is ggbvect()
		p("Length.1",
				"[[ggbv:=%0],regroup(when(ggbv[0]=='pnt' || (type(ggbv)==DOM_LIST && subtype(ggbv)==27), l2norm(ggbv),size(ggbv)))][1]");
		p("Length.3", "arcLen(%0,%1,%2)");
		p("Length.4", "arcLen(%0,%1,%2,%3)");

		// regroup so that exp(1)^2 is simplified
		// regroup(inf) doesn't work, so extra check needed
		p("Limit.2",
				"[[ggblimvans:=?],[ggblimvans:=limit(%0,%1)], [ggblimvans:=when(ggblimvans==inf || ggblimvans==-inf || ggblimvans==undef,ggblimvans,regroup(ggblimvans))],ggblimvans][3]");
		p("Limit.3",
				"[[ggblimvans:=?],[ggblimvans:=limit(%0,%1,%2)], [ggblimvans:=when(ggblimvans==inf || ggblimvans==-inf || ggblimvans==undef,ggblimvans,regroup(ggblimvans))],ggblimvans][3]");
		p("LimitAbove.2",
				"[[ggblimvans:=?],[ggblimvans:=limit(%0,x,%1,1)], [ggblimvans:=when(ggblimvans==inf || ggblimvans==-inf || ggblimvans==undef,ggblimvans,regroup(ggblimvans))],ggblimvans][3]");
		p("LimitAbove.3",
				"[[ggblimvans:=?],[ggblimvans:=limit(%0,%1,%2,1)], [ggblimvans:=when(ggblimvans==inf || ggblimvans==-inf || ggblimvans==undef,ggblimvans,regroup(ggblimvans))],ggblimvans][3]");
		p("LimitBelow.2",
				"[[ggblimvans:=?],[ggblimvans:=limit(%0,x,%1,-1)], [ggblimvans:=when(ggblimvans==inf || ggblimvans==-inf || ggblimvans==undef,ggblimvans,regroup(ggblimvans))],ggblimvans][3]");
		p("LimitBelow.3",
				"[[ggblimvans:=?],[ggblimvans:=limit(%0,%1,%2,-1)], [ggblimvans:=when(ggblimvans==inf || ggblimvans==-inf || ggblimvans==undef,ggblimvans,regroup(ggblimvans))],ggblimvans][3]");

		p("Max.N",
				"[[ggbmaxarg:=%],when(type(ggbmaxarg)==DOM_LIST, when(type((ggbmaxarg)[0])==DOM_LIST, ?, max(ggbmaxarg)), ?)][1]");
		p("MatrixRank.1", "rank(%0)");
		p("Mean.1", "mean(%0)");
		p("Median.1", "median(%0)");
		p("Min.N",
				"[[ggbminarg:=%],when(type(ggbminarg)==DOM_LIST, when(type((ggbminarg)[0])==DOM_LIST, ?, min(ggbminarg)), ?)][1]");
		p("MixedNumber.1", "propfrac(%0)");
		p("Mod.2",
				"[[[ggbmodarg0:=%0],[ggbmodarg1:=%1]],if type(ggbmodarg0)==DOM_INT && type(ggbmodarg1)==DOM_INT then irem(ggbmodarg0,ggbmodarg1) else rem(ggbmodarg0,ggbmodarg1,x) fi][1]");
		p("NextPrime.1", "nextprime(%0)");
		p("NIntegral.3", "romberg(%0,%1,%2)");
		p("NIntegral.4", "romberg(%0,%1,%2,%3)");
		p("Normal.3", "normald_cdf(%0,%1,%2)");
		p("Normal.4",
				"[[[ggbnormarg0:=%0],[ggbnormarg1:=%1],[ggbnormarg2:=%2]],if %3=true then normald_cdf(ggbnormarg0,ggbnormarg1,ggbnormarg2) else (1/sqrt(2*pi*((ggbnormarg1)^2))) * exp(-((ggbnormarg2-(ggbnormarg0))^2) / (2*((ggbnormarg1)^2))) fi][1]");
		p("nPr.2", "perm(%0,%1)");

		// #4124 wrap input in evalf
		p("NSolve.1",
				"ggbsort([[ggbnsans:=evalf(%0)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,"
						+
						// eg NSolve[{pi / x = cos(x - 2y), 2 y - pi = sin(x)}]
						"[[ggbvars:=lname(ggbnsans)],[ggbnsans:=fsolve(ggbnsans,ggbvars)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,when(type(ggbnsans[0])==DOM_LIST,ggbnsans[0],ggbnsans),[ggbnsans])],seq(ggbvars[irem(j,dim(ggbnsans))]=ggbnsans[j],j,0,dim(ggbnsans)-1)][3],"
						+
						// eg NSolve[a^4 + 34a^3 = 34]
						// regroup() added for
						// NSolve[BC^2=4^2+3^2-2*4*3*cos(50degrees)]
						"[[ggbvars:=lname(ggbnsans)],[ggbnsans:=fsolve(regroup(ggbnsans),ggbvars[0])],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,ggbnsans,[ggbnsans])],seq(ggbvars[0]=ggbnsans[j],j,0,dim(ggbnsans)-1)][3])],"
						+ "ggbnsans][2])");

		p("NSolve.2",
				"ggbsort([[ggbnsans:=evalf(%0)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,"
						+
						// eg NSolve[{pi / x = cos(x - 2y), 2 y - pi =
						// sin(x)},{x=1,y=1}]
						// eg NSolve[{pi / x = cos(x - 2y), 2 y - pi =
						// sin(x)},{x,y}]
						// eg NSolve[{3=c*a^5, 3=c*a^4},{a,c}]
						"[[ggbvars:=seq(left(%1[j]),j,0,dim(%1)-1)],[ggbnsans:=fsolve(ggbnsans,%1)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,when(type(ggbnsans[0])==DOM_LIST,ggbnsans[0],ggbnsans),[ggbnsans])],seq(ggbvars[irem(j,dim(ggbnsans))]=ggbnsans[j],j,0,dim(ggbnsans)-1)][3],"
						+
						// eg NSolve[a^4 + 34a^3 = 34, a=3]
						// eg NSolve[a^4 + 34a^3 = 34, a]
						"[[ggbvars:=when(type(%1)==DOM_LIST,left(%1[0]),left(%1))],[ggbnsans:=fsolve(ggbnsans,%1)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,ggbnsans,[ggbnsans])],seq(ggbvars=ggbnsans[j],j,0,dim(ggbnsans)-1)][3])],"
						+ "ggbnsans][2])");

		// fsolve starts at x=0 if no initial value is specified and if the
		// search is not successful
		// it will try a few random starting points.

		p("NSolutions.1",
				"ggbsort([[ggbnsans:=evalf(%0)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,"
						+
						// eg NSolutions[{pi / x = cos(x - 2y), 2 y - pi =
						// sin(x)}]
						"[[ggbvars:=lname(ggbnsans)],[ggbnsans:=fsolve(ggbnsans,ggbvars)],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,ggbnsans,[ggbnsans])],ggbnsans][3],"
						+
						// eg NSolutions[a^4 + 34a^3 = 34]
						"[[ggbvars:=lname(ggbnsans)],[ggbnsans:=fsolve(ggbnsans,ggbvars[0])],[ggbnsans:=when(type(ggbnsans)==DOM_LIST,ggbnsans,[ggbnsans])],ggbnsans][3])],"
						+ "ggbnsans][2])");

		p("NSolutions.2",
				"ggbsort([[ggbnsans:=fsolve(evalf(%0),%1)],when(type(ggbnsans)==DOM_LIST,ggbnsans,[ggbnsans])][1])");

		// numer() cancels down first
		// p("Numerator.1", "numer(%0)");
		p("Numerator.1", "getNum(%0)");

		p("Numeric.1",
				"[[ggbnumans:=%0],when(dim(lname(ggbnumans))==0 || count_eq(unicode0176u,lname(ggbnumans))>0,"
						+
						// normal() so that Numeric(x + x/2) works
						// check for unicode0176u so that
						// Numeric[acos((-11.4^2+5.8^2+7.2^2)/(2 5.8 7.2))]
						// is better when returning degrees from inverse trig
						"evalf(ggbnumans)" + "," +
						// #4537
						"evalf(regroup(normal(ggbnumans)))" + ")][1]");

		p("Numeric.2",
				"[[ggbnumans:=%0],when(dim(lname(ggbnumans))==0 || lname(ggbnumans)==[unicode0176u],"
						+
						// normal() so that Numeric(x + x/2) works
						// check for unicode0176u so that
						// Numeric[acos((-11.4^2+5.8^2+7.2^2)/(2 5.8 7.2))]
						// is better when returning degrees from inverse trig
						"evalf(ggbnumans,%1)" + "," +
						// #4537
						"evalf(regroup(normal(ggbnumans)),%1)" + ")][1]");

		// using sub twice in opposite directions seems to fix #2198, though
		// it's sort of magic
		// with_sqrt(0) to factor over rationals
		p("PartialFractions.1", "partfrac(%0)");
		p("PartialFractions.2", "partfrac(%0,%1)");
		p("Pascal.4",
				"[[[ggbpasarg0:=%0],[ggbpasarg1:=%1],[ggbpasarg2:=%2]],if %3=true then Beta(ggbpasarg0,1+floor(ggbpasarg2),ggbpasarg1,1) else (1-(%1))^(ggbpasarg2)*(ggbpasarg1)^(ggbpasarg0)*binomial(ggbpasarg0+ggbpasarg2-1,ggbpasarg0-1) fi][1]");
		p("Poisson.3", "if %2=true then "
				+ "exp(-(%0))*sum ((%0)^k/k!,k,0,floor(%1)) "
				+ "else normal((%0)^(%1)/factorial(floor(%1))*exp(-%0)) fi");
		p("Polynomial.N",
				"[[[ggbpolans:=0/0], [ggbvar:=x], [ggbinput:=%], "
						// DOM_IDENT: something like x or y
						+ "when(type(ggbinput)==DOM_LIST && type(ggbinput[1]) != DOM_IDENT,"
						// eg Polynomial[{(1, 1), (2, 3)}]
						// eg Polynomial[(1, 1), (2, 3)]
						// eg Polynomial[{(1, 1), (2, 3), (3, 6)}]
						// eg Polynomial[(1, 1), (2, 3), (3, 6)]
						+ "[[xvals := [seq(xcoord(ggbinput[j]),j=0..size(ggbinput)-1)]], [yvals := [seq(ycoord(ggbinput[j]),j=0..size(ggbinput)-1)]], [ggbpolans := normal(lagrange(xvals,yvals,x))]]"
						+ ","
						// eg Polynomial[x^2+a x + b x +c]
						// eg Polynomial[y^2+a y + b y +c,y]
						+ "[[ggbinput:=when(type(ggbinput)==DOM_LIST,[[ggbvar:=ggbinput[1]],coeffs(ggbinput[0],ggbinput[1])][1],coeffs(ggbinput,x))], "
						+ "[ggbpolans:=add(seq(ggbinput[j]*ggbvar^(size(ggbinput)-1-j),j=0..size(ggbinput)-1))]]"
						+ ")],ggbpolans][1]");

		p("PreviousPrime.1",
				"[[ggbpparg0:=%0],if (ggbpparg0 > 2) then prevprime(ggbpparg0) else 0/0 fi][1]");
		p("PrimeFactors.1", "ifactors(%0)");
		// normal() makes sure answer is expanded
		// TODO: do we want this, or do it in a more general way
		p("Product.1", "normal(product(%0))");
		p("Product.4", "normal(product(%0,%1,%2,%3))");
		// p("Prog.1","<<%0>>");
		// p("Prog.2","<<begin scalar %0; return %1 end>>");

		p("Random.2",
				"[[ggbranarg0:=%0],ggbranarg0+rand(%1-(ggbranarg0)+1)][1]"); // "RandomBetween"
		p("RandomBinomial.2", "binomial_icdf(%0,%1,rand(0,1))");
		p("RandomElement.1", "rand(1,%0)[0]");
		p("RandomPoisson.1", "poisson_icdf(%0,rand(0,1))"); // could also make
															// the product of
															// rand(0,1) until
															// less than
															// exp(-%0)
		p("RandomNormal.2", "randnorm(%0,%1)");
		p("RandomPolynomial.3", "randpoly(%0,x,%1,%2)");
		p("RandomPolynomial.4", "randpoly(%1,%0,%2,%3)");
		p("Rationalize.1",
				"[[ggbratarg0:=%0],if type(ggbratarg0)==DOM_RAT then ggbratarg0 else normal(exact(ggbratarg0)) fi][1]");
		p("Reverse.1", "revlist(%0)");
		p("RightSide.1",
				"[[ggbrightarg0:=%0],when(type(ggbrightarg0)==DOM_LIST,map(ggbrightarg0,right),right(ggbrightarg0))][1]");
		p("RightSide.2", "right(%0[%1-1]) ");

		p("ReducedRowEchelonForm.1", "rref(%0)");
		p("Sample.2", "flatten1(seq(rand(1,%0),j,1,%1))");
		p("Sample.3",
				"[[[ggbsamarg0:=%0],[ggbsamarg1:=%1]],if %2=true then flatten1(seq(rand(1,ggbsamarg0),j,1,ggbsamarg1)) else rand(ggbsamarg1,ggbsamarg0) fi][1]");
		p("SampleVariance.1",
				" [[ggbsvans:=%0],[ggbsvans:=normal(variance(ggbsvans)*size(ggbsvans)/(size(ggbsvans)-1))],ggbsvans][2]");
		p("SampleSD.1", "normal(stddevp(%0))");
		p("Sequence.1", "seq(j,j,1,%0)");
		p("Sequence.4", "seq(%0,%1,%2,%3)");
		p("Sequence.5", "seq(%0,%1,%2,%3,%4)");

		// default 15, like Input Bar version
		p("ScientificText.1",
				" [[[ggbstinput:=%0],[ggbstans:=?],[ggbstabsans:=abs(ggbstinput)],[ggbstpower:=floor(log10(ggbstinput))],"
						+ "[ggbstans:=evalf(ggbstinput / 10^ggbstpower, 15) + \" 10^ \" + ggbstpower]],when(ggbstinput==0,0,ggbstans)][1]");

		p("ScientificText.2",
				" [[[ggbstinput:=%0],[ggbstans:=?],[ggbstabsans:=abs(ggbstinput)],[ggbstpower:=floor(log10(ggbstinput))],"
						+ "[ggbstans:=evalf(ggbstinput / 10^ggbstpower, %1) + \" 10^ \" + ggbstpower]],when(ggbstinput==0,0,ggbstans)][1]");

		p("SD.1", "normal(stddev(%0))");

		// removed, Shuffle[{1,2}] kills Giac
		p("Shuffle.1", "randperm(%0)");

		// regroup for r*r^n
		// tlin() removed, see #3956
		p("Simplify.1", "simplify(regroup(%0))");

		p("Solutions.1", "ggbsort(normal(zeros(%0,x)))");
		p("Solutions.2", "ggbsort(normal(zeros(%0,%1)))");

		// Root.1 and Solve.1 should be the same
		String root1 = "ggbsort(normal([op(solve(%0))]))";
		p("Root.1", root1);
		p("Solve.1", root1);

		p("Solve.2", "when(size(%1) == 1,"
				+ "flatten1(ggbsort(normal([op(solve(%0,%1))]))),"
				+ "ggbsort(normal([op(solve(%0,%1))])))");
		p("SolveODE.1", "when((%0)[0]=='=',"
						// case the equation contains only y and other variable
						// as x, by default use for variable list y, x
						// #5099
						+ " when (size(lname(%0) intersect [x]) == 0 && size(lname(%0) intersect [y]) == 1 && size(lname(%0) minus [y]) > 0,normal(map(desolve(%0,x,y),x->y=x)[0]),normal(map(desolve(%0),x->y=x)[0]))"
						+ ","
				// add y'= if it's missing
				+ "normal(map(desolve(y'=%0),x->y=x)[0])" + ")");
		
		// goes through 1 point
		// SolveODE[y''=x, (1,1)]
		// goes through 1 point, y'= missing
		// SolveODE[x,(1, 1)]
		// goes through 2 points
		// SolveODE[y''=x, {(1,1),(2,2)}]
		// can't do [solveodearg0:=%0] as y' is immediately simplified to 1
		p("SolveODE.2", ""+
				"normal(y=when(type(%1)==DOM_LIST,"+
				// list of 2 points
				"desolve([%0,y(xcoord(%1[0]))=ycoord(%1[0]),y(xcoord(%1[1]))=ycoord(%1[1])],x,y)"+
				","+
				// one point
				"desolve(when((%0)[0]=='=',%0,y'=%0),x,y,%1)"+
				")"+
				""+
				"[0])");
				
		p("SolveODE.3",
				"when((%0)[0]=='=',"
						+ "normal(map(desolve(%0,%2,%1),(type(%1)==DOM_IDENT)?(x->%1=x):(x->y=x))[0])"
						+ ","
						// add y'= if it's missing
						+ "normal(map(desolve(y'=%0,%2,%1),(type(%1)==DOM_IDENT)?(x->%1=x):(x->y=x))[0])"
						+ ")");
		p("SolveODE.4", "when((%0)[0]=='=',"
				+ "normal(map(desolve(%0,%2,%1,%3),x->%1=x)[0])" + ","
				// add y'= if it's missing
				+ "normal(map(desolve(y'=%0,%2,%1,%3),x->%1=x)[0])" + ")");
		p("SolveODE.5",// SolveODE[y''=x,y,x,A,{B}]
				"normal(map(desolve(%0,%2,%1,%3,%4),x->%1=x)[0])");
		p("Substitute.2", "subst(%0,%1)");
		p("Substitute.3", "subst(%0,%1,%2)");
		// p("SubstituteParallel.2","if hold!!=0 then sub(%1,%0) else sub(%1,!*hold(%0))");

		p("Sum.1", "sum(%0)");

		// remove normal from Sum, otherwise
		// Sum[1/n*sqrt(1-(k/n)^2),k,1,n]
		// Sum[1/10*sqrt(1-(k/10)^2),k,1,10]
		// don't work
		// Sum[Sum[x+2y, x, 1, 3], y, 2, 4]
		// expand added for Sum[2+3(n-1),n,1,n]
		p("Sum.4",
				"expand(subst(sum(subst(%0,%1,ggbsumvar@1),ggbsumvar@1,%2,%3), ggbsumvar@1, %1))");

		// svd = singular value decomposition
		// svd(M)=[U,S,V]
		// such that M=U*diag(S)*tran(V)
		p("Svd.1", "svd(%0)");

		// GeoGebra counts elements from 1, giac from 0
		// p("Take.3", "%0[%1-1..%2-1]");
		p("Take.3",
				"[[[ggbtakearg0:=%0],[ggbtakearg1:=%1],[ggbtakearg2:=%2]],ggbtakearg0[ggbtakearg1-1..ggbtakearg2-1]][1]");
		p("TaylorSeries.3", "convert(series(%0,x,%1,%2),polynom)");
		p("TaylorSeries.4", "convert(series(%0,%1,%2,%3),polynom)");
		p("TDistribution.2", "student_cdf(%0,%1)");
		// alternative for exact calculations, but
		// Numeric[TDistribution[4,2],15] doesn't work with this
		// "1/2 + (Beta(%0 / 2, 1/2, 1, 1) - Beta(%0 / 2, 1/2, %0 / (%0 + (%1)^2 ) ,1) )* sign(%1) / 2");
		p("ToComplex.1",
				"[[ggbtcans:=?],[ggbtcans:=%0],[ggbtype:=type(evalf(ggbtcans))],"
						+
						// ToComplex[3.1]
						"when(ggbtype==DOM_INT || ggbtype==DOM_FLOAT,ggbtcans,"
						+
						// ToComplex[(3,4)]
						"when(ggbtcans[0]=='pnt',xcoord(%0)+i*ycoord(%0)," +
						// ToComplex[ln(i)], ToComplex[a]
						"real(ggbtcans)+i*im(ggbtcans)" + "))][3]");
		p("ToExponential.1", "rectangular2polar(%0)");
		p("ToPolar.1",
				"([[ggbtpans:=%0],[ggbtpans:=polar_coordinates(ggbtpans)],[ggbtpans:=convert([ggb_ang(ggbtpans[0],ggbtpans[1])],25)],ggbtpans])[3]");
		p("ToPoint.1", "point(convert(coordinates(%0),25))");
		p("Transpose.1", "transpose(%0)");
		// http://reduce-algebra.com/docs/trigsimp.pdf
		// possible Giac commands we can use:
		// halftan, tan2sincos2, tan2cossin2, sincos, trigtan, trigsin, trigcos
		// cos2sintan, sin2costan, tan2sincos, trigexpand, tlin, tcollect,
		// trig2exp, exp2trig

		// eg tlin(halftan(csc(x) - cot(x) + csc(y) - cot(y))) ->
		// tan(x/2)+tan(y/2)
		p("TrigExpand.1", "tan2sincos(trigexpand(%0))");
		p("TrigExpand.2",
				"[[ggbtrigarg0:=%0],when((%1)[0]=='tan', trigexpand(ggbtrigarg0),tan2sincos(trigexpand(ggbtrigarg0)))][1]");

		// subst(trigexpand(subst(sin(x),solve(tmpvar=x/2,lname(sin(x)))),tmpvar=x/2)
		// gives 2*cos(x/2)*sin(x/2)
		p("TrigExpand.3",
				"[[[ggbtrigarg0:=%0],[ggbtrigarg2:=%2]],"
						+ "when(%1==tan(x),"
						+
						// if %1=tan(x), assume %2=x/2
						"tlin(halftan(ggbtrigarg0))"
						+ ","
						+ "subst(trigexpand(subst(ggbtrigarg0,solve(ggbtmp=ggbtrigarg2,lname(ggbtrigarg0)))),ggbtmp=ggbtrigarg2)"
						+ ")][1]");

		// subst(subst(trigexpand(subst(subst((sin(x))+(sin(y)),solve(tmpvar=(x)/(2),lname((sin(x))+(sin(y)))
		// )),solve(tmpvar2=(y)/(2),lname((sin(x))+(sin(y)))
		// ))),tmpvar=x/2),tmpvar2=y/2)
		// 2*cos(x/2)*sin(x/2)+2*cos(y/2)*sin(y/2)
		p("TrigExpand.4",
				"[[[ggbtrigarg0:=%0],[ggbtrigarg2:=%2],[ggbtrigarg3:=%3]],"
						+ "when(%1==tan(x),"
						+
						// if %1=tan(x), assume %2=x/2, %3=y/2
						"tlin(halftan(ggbtrigarg0))"
						+ ","
						+ "subst(subst(trigexpand(subst(subst(ggbtrigarg0,solve(tmpvar=ggbtrigarg2,lname(ggbtrigarg0))),solve(tmpvar2=ggbtrigarg3,lname(ggbtrigarg0)))),tmpvar=ggbtrigarg2),tmpvar2=ggbtrigarg3)"
						+ ")][1]");

		// calculate trigsin, trigcos, trigtan and check which is shortest (as a
		// string)
		p("TrigSimplify.1",
				"[[[ggbtrigarg:=%0], [ggbsin:=trigsin(ggbtrigarg)], [ggbcos:=trigcos(ggbtrigarg)], [ggbtan:=trigtan(ggbtrigarg)], "
						+ "[ggbsinlen:=length(\"\"+ggbsin)],[ggbcoslen:=length(\"\"+ggbcos)],[ggbtanlen:=length(\"\"+ggbtan)]],"
						+ "when(ggbsinlen<=ggbcoslen && ggbsinlen<=ggbtanlen,ggbsin,when(ggbcoslen<=ggbtanlen,ggbcos,ggbtan))][1]");

		// try with and without tan2sincos(), tcollectsin() and check which i		// eg TrigCombine[(tan(x) + tan(2x)) / (1 - tan(x) tan(2x))]
		p("TrigCombine.1",
				"[[[ggbtrigarg:=%0], [ggbsin:=tcollectsin(normal(ggbtrigarg))], [ggbcos:=tcollect(normal(ggbtrigarg))], [ggbtan:=tcollect(normal(tan2sincos(ggbtrigarg)))], "
						+ "[ggbsinlen:=length(\"\"+ggbsin)],[ggbcoslen:=length(\"\"+ggbcos)],[ggbtanlen:=length(\"\"+ggbtan)]],"
						+ "when(ggbcoslen<=ggbsinlen && ggbcoslen<=ggbtanlen,ggbcos,when(ggbsinlen<=ggbtanlen,ggbsin,ggbtan))][1]");

		// eg TrigCombine[sin(x)+cos(x),sin(x)]
		p("TrigCombine.2",
				"[[[ggbtrigarg0:=%0],[ggbtrigarg1:=%1]],when(ggbtrigarg1[0]=='sin',tcollectsin(normal(ggbtrigarg0)),when(ggbtrigarg1[0]=='tan',tcollect(normal(tan2sincos(ggbtrigarg0))),tcollect(normal(ggbtrigarg0))))][1]");

		p("Union.2", "%0 union %1");
		p("Unique.1", "[op(set[op(%0)])]");
		p("Variance.1", "normal(variance(%0))");
		p("Weibull.3", "1-exp(-((%2)/(%1))^(%0))");
		p("Zipf.4", // %1=exponent
				"[[[ggbzipfarg0:=%0],[ggbzipfarg1:=%1],[ggbzipfarg2:=%2]],if %3=true then harmonic(ggbzipfarg1,ggbzipfarg2)/harmonic(ggbzipfarg1,ggbzipfarg0) else 1/((ggbzipfarg2)^ggbzipfarg1*harmonic(ggbzipfarg1,ggbzipfarg0)) fi][1]");
		// TODO check if it's easier to implement with giac's zip command
		p("Zip.N",
				"[[ggbzipans(l):=begin local len,res,sbl,xpr,k,j;xpr:=l[0];len:=length(l[2]);res:={};"
						+ "for k from 4 to length(l)-1 step +2 do len:=min(len,length(l[k])); od;"
						+ "for k from 0 to len-1 do sbl:={};for j from 2 to length(l)-1 step +2 do"
						+ "sbl:=append(sbl, l[j-1]=l[j][k]);od;res:=append(res, subst(xpr,sbl));od; res; end],ggbzipans(%)][1]");
		// SolveCubic[x^3+3x^2+x-1]
		// SolveCubic[x^3+3x^2+x-2]
		// SolveCubic[x^3+3x^2+x-3]

		// SolveCubic[x^3 + 6x^2 - 7*x - 2]
		// x^3 - 6x^2 - 7x + 9

		// check with CSolve first, eg
		// f(x) = x^3 - 9x^2 - 2x + 8

		// adapted from xcas example by Bernard Parisse
		p("SolveCubic.1", "["
				+ "[j:=exp(2*i*pi/3)],"
				+ "[V:=symb2poly(%0,x)],"
				+ "[n:=size(V)],"
				+

				// if (n!=4){
				// throw(afficher(P)+" n'est pas de degre 3");
				// }
				// Reduction de l'equation

				"[V:=V/V[0]],"
				+ "[b:=V[1]],"
				+ "[V:=ptayl(V,-b/3)],"
				+ "[p:=V[2]],"
				+ "[q:=V[3]],"
				+
				// on est ramen x^3+p*x+q=0
				// x=u+v -> u^3+v^3+(3uv+p)(u+v)+q=0
				// On pose uv=-p/3 donc u^3+v^3=-q et u^3 et v^3 sont solutions
				// de u^3 v^3 = -p^3/27 et u^3+v^3=-q
				// donc de x^2+q*x-p^3/27=0
				"[d:=q^2/4+p^3/27],"
				+

				// if (d==0){
				// // racine double
				// return solve(P,x);
				// }
				"[d:=sqrt(d)]," + "[u:=(-q/2+d)^(1/3)]," + "[v:=-p/3/u],"
				+ "[x1:=u+v-b/3]," + "[x2:=u*j+v*conj(j)-b/3],"
				+ "[x3:=u*conj(j)+v*j-b/3]," + "[x1s:=regroup(normal(x1))],"
				+ "[x2s:=regroup(normal(x2))]," + "[x3s:=regroup(normal(x3))],"

				// for debugging
				// + "[p,q,d,u,v,x1,x2,x3]"
				// SolveCubic[x^3+1] -> u=0, v=?
				+ "when(d==0 || u==0, csolve(%0,x), [x1s,x2s,x3s])"

				+ "][18]");

		// SolveQuartic[2x^4+3x^3+x^2+1]
		// SolveQuartic[x^4+6x^2-60x+36] approx = {(-1.872136644123) -
		// (3.810135336798 * <complexi>), (-1.872136644123) + (3.810135336798 *
		// <complexi>),
		// 0.6443988642267, 3.099874424019}
		// SolveQuartic[3x^4 + 6x^3 - 123x^2 - 126x + 1080] = {(-6), (-4), 3, 5}
		// SolveQuartic[x^(4) - (10 * x^(3)) + (35 * x^(2)) - (50 * x) + 24] =
		// {1, 3, 2, 4}
		// SolveQuartic[x^4 + 2x^3 - 41x^2 - 42x + 360] = {(-6), (-4), 3, 5}
		// SolveQuartic[ x^4 + 2x^2 + 6sqrt(10) x + 1] approx
		// {(-2.396488591753), (-0.05300115102973), 1.224744871392 -
		// (2.524476846043 * <complexi>), 1.224744871392 + (2.524476846043 *
		// <complexi>)}
		// SolveQuartic[x^4 + x^3 + x + 1] = {(-1), (-1), (((-<complexi>) *
		// sqrt(3)) + 1)
		// / 2, ((<complexi> * sqrt(3)) + 1) / 2}
		// SolveQuartic[x^(4) - (4 * x^(3)) + (6 * x^(2)) - (4 * x) + 1] = {1}
		// 3 repeated roots, S=0, SolveQuartic[ x^4 - 5x^3 + 9x^2 - 7x + 2 ] =
		// {2,1}
		// SolveQuartic[x^(4) - (2 * x^(3)) - (7 * x^(2)) + (16 * x) - 5] =
		// ((x^(2) - (3 * x) + 1) * (x^(2) + x - 5))
		// http://en.wikipedia.org/wiki/Quartic_function
		/*
		 * p("SolveQuartic.1", "["+ "[ggbsqans:={}],"+ "[ggbfun:=%0],"+
		 * "[ggbcoeffs:=coeffs(ggbfun)],"+ "[a:=ggbcoeffs[0]],"+
		 * "[b:=ggbcoeffs[1]],"+ "[c:=ggbcoeffs[2]],"+ "[d:=ggbcoeffs[3]],"+
		 * "[ee:=ggbcoeffs[4]],"+ // for checking, but unnecessary //
		 * "[delta:=256*a^3*ee^3-192*a^2*b*d*ee^2-128*a^2*c^2*ee^2+144*a^2*c*d^2*ee-27*a^2*d^4+144*a*b^2*c*ee^2-6*a*b^2*d^2*ee-80*a*b*c^2*d*ee+18*a*b*c*d^3+16*a*c^4*ee-4*a*c^3*d^2-27*b^4*ee^2+18*b^3*c*d*ee-4*b^3*d^3-4*b^2*c^3*ee+b^2*c^2*d^2],"
		 * + "[p:=(8*a*c-3*b*b)/(8*a*a)],"+
		 * "[q:=(b^3-4*a*b*c+8*a^2*d)/(8*a^3)],"+
		 * "[delta0:=c^2-3*b*d+12*a*ee],"+//OK
		 * "[delta1:=2*c^3-9*b*c*d+27*b^2*ee+27*a*d^2-72*a*c*ee],"+//OK
		 * 
		 * "if (delta0 == 0 && delta1 == 0) then"+ // giac's solve should give
		 * exact in this case " ggbsqans:=zeros(ggbfun) else " +
		 * 
		 * " ["+ "[minusdelta27:=delta1^2-4*delta0^3],"+//OK // use surd rather
		 * than cbrt so that simplify cbrt(27) works //
		 * "[Q:=simplify(surd((delta1 + when(delta0==0, delta1, sqrt(minusdelta27)))/2,3))],"
		 * + // find all 3 cube-roots
		 * "[Qzeros:=czeros(x^3=(delta1 + when(delta0==0, delta1, sqrt(minusdelta27)))/2)],"
		 * + // czeros can return an empty list eg czeros(x^(3) = (1150 + ((180
		 * * i) * sqrt(35))) / 2) // from SolveQuartic[x^(4) - (2 * x^(3)) - (7
		 * * x^(2)) + (16 * x) - 5]
		 * "[Qzeros:=when(length(Qzeros)==0,{cbrt((delta1 + when(delta0==0, delta1, sqrt(minusdelta27)))/2)},Qzeros)],"
		 * + "[Q:=Qzeros[0]],"+
		 * "[Q1:=when(length(Qzeros) > 1,Qzeros[1],Qzeros[0])],"+
		 * "[Q2:=when(length(Qzeros) > 2,Qzeros[2],Qzeros[0])],"+ // pick a
		 * cube-root to make S non-zero // always possible unless quartic is in
		 * form (x+a)^4 "[S:=sqrt(-2*p/3+(Q+delta0/Q)/(3*a))/2],"+
		 * "[S:=when(S!=0,S,sqrt(-2*p/3+(Q1+delta0/Q1)/(3*a))/2)],"+
		 * "[S:=when(S!=0,S,sqrt(-2*p/3+(Q2+delta0/Q2)/(3*a))/2)],"+
		 * 
		 * // could use these for delta > 0 ie minusdelta27 < 0
		 * //"[phi:=acos(delta1/2/sqrt(delta0^3))],"+
		 * //"[Salt:=sqrt(-2*p/3+2/(3*a)*sqrt(delta0)*cos(phi/3))/2],"+
		 * 
		 * "[ggbsqans:={simplify(-b/(4*a)-S-sqrt(-4*S^2-2*p+q/S)/2),simplify(-b/(4*a)-S+sqrt(-4*S^2-2*p+q/S)/2),simplify(-b/(4*a)+S-sqrt(-4*S^2-2*p-q/S)/2),simplify(-b/(4*a)+S+sqrt(-4*S^2-2*p-q/S)/2)}]"
		 * + "]" + "fi"+ //")]" + " ,ggbsqans][13]");
		 */

		// Experimental Geometry commands. Giac only
		p("Radius.1", "normal(regroup(radius(%0)))");
		p("Center.1", "coordinates(center(%0))");
		p("Midpoint.2", "normal(regroup(coordinates(midpoint(%0,%1))))");

		// center-point: point(%0),point(%1)
		// or center-radius: point(%0),%1
		// regroup r*r -> r^2 without multiplying out
		// circle(2*(%0)-(%1),%1) to convert centre,point -> points on diameter
		p("Circle.2",
				"[[[ggbcirarg0:=%0],[ggbcirarg1:=%1]],regroup(equation(when(ggbcirarg1[0]=='pnt',circle(2*(ggbcirarg0)-(ggbcirarg1),ggbcirarg1),circle(ggbcirarg0,ggbcirarg1))))][1]");

		p("Area.1", "normal(regroup(area(circle(%0))))");
		p("Circumference.1", "normal(regroup(perimeter(%0)))");

		p("LineBisector.2", "equation(perpen_bisector(%0,%1))");
		p("AngularBisector.2",
				"[[ggbabarg0:=%0],[ggbabarg1:=%1],[B:=inter(ggbabarg0,ggbabarg1)],[eqa:=equation(ggbabarg0)],[eqb:=equation(ggbabarg1)],"
						+ "[uva:=convert([unitV(coeff(left(eqa)-right(eqa),y,1),-coeff(left(eqa)-right(eqa),x,1))],25)],"
						+ "[uvb:=convert([unitV(coeff(left(eqb)-right(eqb),y,1),-coeff(left(eqb)-right(eqb),x,1))],25)],"
						+ "when(uva==uvb,[eqa],[equation(line(B[0],B[0]+uva+uvb)),equation(line(B[0],B[0]+uva-uvb))])][5]");
		p("AngularBisector.3", "equation(bisector(%1,%0,%2))");

		p("Angle.1", "regroup(%0 *180 / pi) * unicode0176u");

		// point(xcoord(ggbangarg0),ycoord(ggbangarg0),zcoord(ggbangarg0))
		// so we can mix 2d and 3d points
		p("Angle.2",
				"[[[ggbangarg0:=%0], [ggbangarg1:=%1]], normal(regroup(angle(point(0,0,0),point(xcoord(ggbangarg0),ycoord(ggbangarg0),zcoord(ggbangarg0)),point(xcoord(ggbangarg1),ycoord(ggbangarg1),zcoord(ggbangarg1)))))][1]");
		p("Angle.3",
				"[[[ggbangarg0:=%0], [ggbangarg1:=%1], [ggbangarg2:=%2]], normal(regroup(angle(point(xcoord(ggbangarg1),ycoord(ggbangarg1),zcoord(ggbangarg1)),point(xcoord(ggbangarg0),ycoord(ggbangarg0),zcoord(ggbangarg0)),point(xcoord(ggbangarg2),ycoord(ggbangarg2),zcoord(ggbangarg2)))))][1]");
		// p("Angle.3", "normal(regroup(angle(%1,%0,%2)))");

		// eg distance((4,5),(0,3))
		// eg distance((2,3,4),(0,3,1))
		// eg distance(conic(y=x^2),(0,3))
		// don't want normal(), eg Distance[(a,b),(c,d)]
		// bit do want it for Distance[(0.5,0.5),x^2+y^2=1]

		// use type(evalf(ggbarg1))==DOM_FLOAT to catch DOM_INT, DOM_FLOAT,
		// DOM_RAT, DOM_IDENT (eg pi)
		p("Distance.2",
				"[[[ggbdisans:=0/0],[ggbdisarg0:=%0],[ggbdisarg1:=%1],[ggbdisans:=when(ggbdisarg0[0]!='pnt',undef,when(type(evalf(ggbdisarg1))==DOM_FLOAT,undef,regroup(distance(ggbdisarg0,"
						+
						// #3907 add "y=" for functions but not points
						"when(ggbdisarg1[0]!='pnt' && ggbdisarg1[0] != '=',y=ggbdisarg1,"
						+
						// if variable list contains 'z', wrap in plane()
						"when(count_eq(z,lname(ggbdisarg1))==0,ggbdisarg1,plane(ggbdisarg1))"
						+ ")"
						+ "))))]],"
						+ "when(lname(ggbdisans)=={},normal(ggbdisans),ggbdisans)][1]");

		// regroup: y = -2 a + b + 2x -> y = 2x - 2 a + b
		// don't want normal(), eg Line[(a,b),(c,d)]
		p("Line.2",
				"[[ggblinearg0:=%0],[ggblinearg1:=%1],"
						+ "when(is3dpoint(ggblinearg0),"
						+ "when(is3dpoint(ggblinearg1),"
						// case Line[3dPoint,3dPoint]
						+ "regroup(equation(cat(\"y=\",ggblinearg0,\"+\u03BB*\",point(xcoord(ggblinearg1-ggblinearg0),ycoord(ggblinearg1-ggblinearg0),zcoord(ggblinearg1-ggblinearg0))))),"
						// case Line[3dPoint,Vect]
						+ "equation(cat(\"y=\",ggblinearg0,\"+\u03BB*\",point(ggblinearg1[0],ggblinearg1[1],when(size(ggblinearg1) == 3,ggblinearg1[2],0))))),"
						// case Line[2dPoint,2dPoint] or Line[2dPoint,Vector]
						+ "regroup(equation(line(ggblinearg0,ggblinearg1))))][2]");

		p("Point.1",
				"when(length(%0)==3,point(xcoord(%0),ycoord(%0),zcoord(%0)),point(xcoord(%0),ycoord(%0)))");
		p("Point.2",
				"when(length(%0)==3,{point(xcoord(%0),ycoord(%0),zcoord(%0)),point(xcoord(%1),ycoord(%1),zcoord(%1))},{point(xcoord(%0),ycoord(%0)),point(xcoord(%1),ycoord(%1))})");

		// p("Midpoint.2",
		// "[[ggbans:=factor((normal(convert(coordinates(midpoint(%0,%1)),25))))],"
		// +
		// "(ggbans[0],ggbans[1])][1]");

		// normal: nice form for Midpoint[(1/2,pi),(1,1)]
		// factor: nice form for Midpoint[(a,b),(c,d)]
		p("Midpoint.2",
				"convert(factor((normal(coordinates(midpoint(%0,%1))))),25)");

		p("OrthogonalLine.2", "equation(perpendicular(%0,line(%1)))");
		// TODO: return Segment() not equation
		p("Segment.2", "equation(segment(%0,%1))");

		// TODO: needs to get back from Giac into GeoGebra as a parametric eqn
		// p("Curve.5", "equation(plotparam([%0,%1],%2,%3,%4))");
		// p("Polygon.N", "polygon(%)");
		// p("PolyLine.N", "open_polygon(%)");

		p("Tangent.2",
				"[[[ggbtanarg0:=%0],[ggbtanarg1:=%1]],when((%0)[0]=='pnt',"
						+ "when((ggbtanarg1)[0]=='=',"
						+
						// Tangent[conic/implicit, point on curve]
						"equation(tangent(ggbtanarg1,ggbtanarg0)),"
						+
						// Tangent[point, function]
						// just use x-coordinate real(%0[1])
						"y=normal(subst(diff(ggbtanarg1,x),x=real(ggbtanarg0[1]))*(x-real(ggbtanarg0[1]))+subst(ggbtanarg1,x=real(%0[1]))))"
						+ ","
						+
						// Tangent[x-value, function]
						// use lname(function) instead of x
						// e.g. lname(sin(t)) = t
						// needed for #5526
						"y=normal(subst(diff(ggbtanarg1,lname(ggbtanarg1)[0]),lname(ggbtanarg1)[0]=ggbtanarg0)*(lname(ggbtanarg1)[0]-(ggbtanarg0))+subst(ggbtanarg1,lname(ggbtanarg1)[0]=ggbtanarg0))"
						+ ")][1]");

		// p("TangentThroughPoint.2",
		// "[[ggbans:=?],[ggbans:=equation(tangent(when((%1)[0]=='=',%1,y=%1),%0))],"
		// +
		// "[ggbans:=when(((ggbans)[0])=='=' && lhs(ggbans)==1 && rhs(ggbans)==0,?,ggbans)],"
		// +
		// "[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,{ggbans})],ggbans][4]");

		// see ToPoint.1
		// eg Dot[Vector[(a,b)],Vector[(c,d)]]

		p("Vector.1",
		// "point(convert(coordinates(%0),25))");
				"when(is3dpoint(%0),"
						+
						// 3D
						// "ggbvect[((%0)[1])[0], ((%0)[1])[1], ((%0)[1])[2]]"+
						"ggbvect[xcoord(%0),ycoord(%0),zcoord(%0)]"
						+ ","
						+ "when((%0)[0]=='pnt',"
						+
						// 2D point
						// "ggbvect[real((%0)[1]), im((%0)[1])]"+
						"ggbvect[xcoord(%0),ycoord(%0)]" + ","
						+ "when(im(%0)==ggbvect[0,0]," +
						// already a vector
						"%0" + "," +
						// complex
						"ggbvect[re(%0),im(%0)]" + ")))");

		p("Vector.2", "when(is3dpoint(%0)||is3dpoint(%1),"
				+
				// 3D points
				// "ggbvect[((%1)[1])[0]-((%0)[1])[0], ((%1)[1])[1]-((%0)[1])[1], ((%1)[1])[2]-((%0)[1])[2] ]"+
				"ggbvect[xcoord(%1)-xcoord(%0),ycoord(%1)-ycoord(%0),zcoord(%1)-zcoord(%0)]"
				+ "," + "when((%0)[0]=='pnt'," +
				// 2D points
				// "ggbvect[real((%1)[1])-real((%0)[1]), im((%1)[1])-im((%0)[1])]"+
				"ggbvect[xcoord(%1)-xcoord(%0),ycoord(%1)-ycoord(%0)]" + "," +
				// numbers
				"ggbvect[%0,%1]" + "))");

		p("OrthogonalVector.1", "convert([[0,-1],[1,0]]*(%0),25)");
		p("UnitOrthogonalVector.1",
				"when(type(%0)==DOM_LIST && size(%0) != 2,?,"
						+ "when(is3dpoint(%0),?,"
						+ "regroup(convert(unitV([-ycoord(%0),xcoord(%0)]),25))"
						+ "))");
		p("UnitVector.1",
				"[[ggin:=%0],[ggbuvans:=when(type(ggin)==DOM_LIST,normalize(ggin),when((ggin)[0]=='=',"
						+ "convert([unitV(coeff(left(ggin)-right(ggin),y,1),-coeff(left(ggin)-right(ggin),x,1))],25),"
						+ "when(ggin[0]='pnt' && size(ggin[1])==3,normal(unitV(ggin)),convert(unitV([real(ggin[1]),im(ggin[1])]),25))))],ggbuvans][2]");
		// Tecna[(10,1),log10(x)]
		return commandMap;
	}

}