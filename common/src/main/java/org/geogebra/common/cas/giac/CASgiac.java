package org.geogebra.common.cas.giac;

import java.math.BigInteger;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry.ArbconstReplacer;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.DiffReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.PowerRootReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.PrefixRemover;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.debug.Log;
import org.geogebra.regexp.shared.MatchResult;
import org.geogebra.regexp.shared.RegExp;

/**
 * Platform (Java / GWT) independent part of giac CAS
 */
public abstract class CASgiac implements CASGenericInterface {

	/**
	 * Random number generator
	 */
	protected static final Random rand = new Random();

	/** Inputs that contain any of the strings should be excluded from caching */
	private static final List<String> EXCLUDE_FROM_CACHE =
			Arrays.asList("rand(", "randnorm(", "randpoly(", "randperm(");

	/**
	 * String that will force an error when evaluated in GeoGebra
	 */
	final public static String FORCE_ERROR = "(";

	/**
	 * string to put Giac into GeoGebra mode (not affected by 'restart')
	 * 
	 */
	public final static String initString = "caseval(\"init geogebra\")";
	/**
	 * In web we need to skip caseval because of emcscripten
	 */
	public final static String initStringWeb = "init geogebra";

	/**
	 * string to put Giac off GeoGebra mode
	 */
	// public final static String closeString = "caseval(\"close geogebra\")";
	// public final static String closeStringWeb = "close geogebra";

	public enum CustomFunctions {
		/**
		 * 
		 */
		RESTART(null, "restart"),

		/**
		 * suppress warning about = vs :=
		 */
		DISABLE_WARNING(null, "warn_equal_in_prog(0)"),

		/**
		 * 
		 */
		PROBA_EPSILON(null, "proba_epsilon:=0;"),

		/**
		 * NOTE: works for max 2 variable
		 */
		GGBIS_POLYNOMIAL("ggbisPolynomial", "ggbisPolynomial(a):= when (size(lname(a)) == 1, is_polynomial(a,lname(a)[0])," + "when (size(lname(a)) == 2, is_polynomial(a,lname(a)[0]) && is_polynomial(a,lname(a)[1]), ?))"),

		/**
		 * test if "=" or "%=" - needed for eg
		 * LeftSide({a,b}={1,2})
		 */
		GGB_IS_EQUALS("ggb_is_equals", "ggb_is_equals(a):=when(a==equal||a=='%=',true,false)"),

		CHECK_DERIVATIVE("check_derivative", "check_derivative(a,b):="
				+ "when(size(a)==1,a[0],flatten1([revlist(a),sort(remove(undef,map(a,r->"
				+ "when(isAlmostZero(evalf(subst(r,x=xcoord(b))-ycoord(b))),r,undef))))])[-1])"),

		/**
		 * test if "=" or "%=" or "&gt;" or "&gt;=" - needed for eg
		 * LeftSide({a,b}={1,2})
		 */
		GGB_IS_GREATER_OR_GREATER_THAN_OR_EQUALS("ggb_is_gt_or_ge_or_equals", "ggb_is_gt_or_ge_or_equals(a):=when(a=='>'||a=='>='||a==equal||a=='%=',true,false)"),

		/**
		 * wrap factor() in eg with_sqrt(0), with_sqrt(1)
		 */
		GGB_FACTOR("ggbfactor", "ggbfactor(a, b, c, d):=[with_sqrt(c), factor(a, b), with_sqrt(d)][1]"),

		/**
		 * wrap cfactor() in eg with_sqrt(0), with_sqrt(1)
		 */
		GGB_CFACTOR("ggbcfactor",
				"ggbcfactor(a, b, c, d):=[with_sqrt(c), cfactor(a, b), with_sqrt(d)][1]"),

		/**
		 * returns "?" if expression has more than one variable otherwise
		 * returns the variable
		 * 
		 * for checking that the second argument of eg Factor(x^2-y^2,x) is a
		 * variable
		 */
		GGB_IS_VARIABLE("ggb_is_variable", "ggb_is_variable(a):=when(length(lvar(a))==1,lvar(a)[0],?)"),

		/**
		 * Returns 1 if the parameter is a number (float, rational or integer). Returns 0 otherwise.
		 */
		GGB_IS_NUMBER("ggb_is_number", "ggb_is_number(a):=[[ggbtype:=type(a)], "
				+ "when(ggbtype==DOM_INT||ggbtype==DOM_FLOAT||ggbtype==DOM_RAT,1,0)][1]"),

		/**
		 * Returns the set difference of two lists while keeping duplicates in the result."
		 */
		GGB_LIST_DIFFERENCE("ggbListDifference",
				"ggbListDifference(x,y):=flatten1(map(x,e->when(member(e, y)==0,[e],[])))"),

		/**
		 * Used by Zip.N
		 * 
		 * TODO check if it's easier to implement with giac's zip command
		 */
		GGBZIPANS("ggbzipans", "ggbzipans(l):=begin local len0,res,sbl,xpr,k,j;xpr:=l[0];len0:=length(l[2]);res:={};"
						+ "for k from 4 to length(l)-1 step +2 do len0:=min(len0,length(l[k])); od;"
						+ "for k from 0 to len0-1 do sbl:={};for j from 2 to length(l)-1 step +2 do"
						+ " sbl:=append(sbl,l[j-1]=l[j][k]);od;res:=append(res,subst(xpr,sbl));od; res; end"),
		/**
		 * 
		 * need to use %0, %1 repeatedly (not using an intermediate variable)
		 * see GGB-2184 eg Sum(If(Mod(k,2)==0,k,0),k,0,10)
		 * 
		 * check if a,b are numbers or polynomials and use rem() / irem()
		 * accordingly
		 */
		GGBMOD("ggbmod", "ggbmod(a,b):=when(typeof(a)=='?',?,when(type(a)!=DOM_INT||type(b)!=DOM_INT,rem(a,b,when(length(lname(b))>0,lname(b)[0],x)),irem(a,b)))"),
		
		// for testing Zip(Mod(k, 2), k,{0, -2, -5, 1, -2, -4, 0, 4, 12})
		// GGBMOD("ggbmod",
		// "ggbmod(a,b):=when(((type((a))!=DOM_INT)&&(type((a))!=DOM_IDENT))||((type((b))!=DOM_INT)&&(type((b))!=DOM_IDENT)),rem(a,b,when(length(lname(b))>0,lname(b)[0],x)),irem(a,b))"),

		/**
		 * test if "=" or "%=" or inequality - needed for eg
		 * LeftSide({a,b}={1,2})
		 */
		GGB_IS_LESS_THAN("ggb_is_less_than", "ggb_is_less_than(a):=when(a=='<'||a=='<=',true,false)"),

		/**
		 * now implemented natively in Giac. This is just for reference /
		 * testing
		 */
		// GGBALT("ggbalt", "ggbalt(x):=when(type(x)==DOM_IDENT,altsymb(x),"
		// +
		// "when(x[0]=='pnt',when(is_3dpoint(x),atan2(x[1][2],sqrt(x[1][0]^2+x[1][1]^2)),0),?))"),

		/**
		 * xcoordsymb(A) converted back to x(A) in CommandDispatcherGiac
		 * 
		 * check for type(evalf(a)) needed as type(exact(-2.24)+i*exact(-1.54))
		 * gives DOM_RAT
		 * 
		 */
		XCOORD("xcoord", "xcoord(a):=when(type(evalf(a))==DOM_COMPLEX, real(a), when(type(a)==DOM_IDENT,xcoordsymb(a),when(a[0]=='pnt',when(is_3dpoint(a),a[1][0],real(a[1])),when(a[0]==equal,coeff(a[1]-a[2],x,1),a[0]))))"),
		/**
		 * altsymb(P) converted back to alt(P) in CommandDispatcherGiac
		 */
		YCOORD("ycoord", "ycoord(a):=when(type(evalf(a))==DOM_COMPLEX, im(a), when(type(a)==DOM_IDENT,ycoordsymb(a),when(a[0]=='pnt',when(is_3dpoint(a),a[1][1],im(a[1])),when(a[0]==equal,coeff(a[1]-a[2],y,1),a[1]))))"),

		/**
		 * make sure z((1,2)) = 0
		 */
		ZCOORD("zcoord", "zcoord(a):=when(type(a)==DOM_IDENT,zcoordsymb(a),when(a[0]=='pnt',when(is_3dpoint(a),a[1][2],0),when(length(a)<3 && a[0] != equal,0,when(a[0]==equal,coeff(a[1]-a[2],z,1),a[2]))))"),

		/**
		 * unicode0176u passes unaltered through Giac then gets decoded to
		 * degree sign in GeoGebra needed for
		 * "return angle from inverse trig function" see ExpressionNode.degFix()
		 */
		DEG_ASIN("asind", "asind(x):=normal(asin(x)/pi*180)*unicode0176u"),

		/**
		 * unicode0176u passes unaltered through Giac then gets decoded to
		 * degree sign in GeoGebra needed for
		 * "return angle from inverse trig function" see ExpressionNode.degFix()
		 */
		DEG_ACOS("acosd", "acosd(x):=normal(acos(x)/pi*180)*unicode0176u"),

		/**
		 * unicode0176u passes unaltered through Giac then gets decoded to
		 * degree sign in GeoGebra needed for
		 * "return angle from inverse trig function" see ExpressionNode.degFix()
		 */
		DEG_ATAN("atand", "atand(x):=normal(atan(x)/pi*180)*unicode0176u"),

		/**
		 * unicode0176u passes unaltered through Giac then gets decoded to
		 * degree sign in GeoGebra needed for
		 * "return angle from inverse trig function" see ExpressionNode.degFix()
		 */
		DEG_ATAN2("atan2d", "atan2d(y,x):=normal(arg(x+i*y)/pi*180)*unicode0176u"),

		/**
		 * Coefficient of Conic, same order as Algebra View command
		 */
		COEFFICIENT_CONIC("ggbcoeffconic",
				"ggbcoeffconic(coeffsarg):={coeffs(coeffsarg,[x,y],[2,0]),coeffs(coeffsarg,[x,y],[0,2]),coeffs(coeffsarg,[x,y],[0,0]),coeffs(coeffsarg,[x,y],[1,1]),coeffs(coeffsarg,[x,y],[1,0]),coeffs(coeffsarg,[x,y],[0,1])}"),

		/**
		 * Coefficient of Quadric, same order as Algebra View command
		 */
		COEFFICIENT_QUADRIC("ggbcoeffquadric", "ggbcoeffquadric(coeffsarg):={coeffs(coeffsarg,[x,y,z],[2,0,0]),coeffs(coeffsarg,[x,y,z],[0,2,0]),coeffs(coeffsarg,[x,y,z],[0,0,2]),coeffs(coeffsarg,[x,y,z],[0,0,0]),coeffs(coeffsarg,[x,y,z],[1,1,0]),coeffs(coeffsarg,[x,y,z],[1,0,1]),coeffs(coeffsarg,[x,y,z],[0,1,1]),coeffs(coeffsarg,[x,y,z],[1,0,0]),coeffs(coeffsarg,[x,y,z],[0,1,0]),coeffs(coeffsarg,[x,y,z],[0,0,1])}"),

		/**
		 * subtype 27 is ggbvect[]
		 * 
		 * abs() in Giac now works for vectors so this isn't needed
		 * 
		 */
		// ABS("ggbabs",
		// "ggbabs(x):=when(x[0]=='pnt' || (type(x)==DOM_LIST &&
		// subtype(x)==27),l2norm(x),abs(x))"),
		/**
		 * check list before equation to avoid out of bounds. flatten helps for
		 * {} and {{{0}}}
		 * 
		 * used for EQUAL_BOOLEAN in ExpressionNode
		 * sb.append("when(ggbIsZero(simplify(");
		 * 
		 * eg sin(x)^2+cos(x)^2==1
		 */
		IS_ZERO("ggbIsZero", "ggbIsZero(ggbx):=when(ggbx==0 || simplify(texpand(ggbx))==0 || exp2pow(lin(pow2exp(ggbx)))==0,true,when(type(ggbx)=='DOM_LIST',max(flatten({ggbx,0}))==min(flatten({ggbx,0}))&&min(flatten({ggbx,0}))==0,when(ggbx[0]==equal,lhs(ggbx)==0&&rhs(ggbx)==0,ggbx[0]=='pnt' && ggbx[1] == ggbvect[0,0,0])))"),
		IS_ALMOST_ZERO("isAlmostZero", "isAlmostZero(a):=len(lname(a))==0 && evalf(a) < 10^-8"),
		/**
		 * Convert the polys into primitive polys in the input list (contains
		 * temporary fix for primpart also):
		 */
		PRIM_POLY("primpoly", "primpoly(x):=begin local pps,ii; if (x==[0]) return [0]; pps:=[]; for ii from 0 to size(x)-1 do pps[ii]:=primpart(x[ii],lvar(x[ii])); od return pps end"),

		/** version of inString() but returns "undef" not "-1" when not found */
		GGB_IN_STRING("ggbinString",
				"ggbinString(x,y):=begin local ret; ret := inString(x,y); if (ret == -1) return undef; else return ret; end"),

		/** index of object in list. Gives "undef" when not found */
		INDEX_OF("indexOf",
				"indexOf(x, mylist):=begin local ii; for ii from 0 to length(mylist)-1 do if (mylist[ii] == x) begin print(ii); return ii; end; od; return undef; end"),

		/**
		 * Compute squarefree factorization of the input poly p. Strange why
		 * sommet(-x)!='-' (so we do an ugly hack here, FIXME)
		 */
		FACTOR_SQR_FREE("factorsqrfree", "factorsqrfree(p):=begin local pf,r,ii; pf:=factor(p); if (sommet(pf)!='*') begin if (sommet(pf)=='^') return op(pf)[0]; else begin if (sommet(pf)!=sommet(-x)) return pf; else return factorsqrfree(-pf); end; end; opf:=op(pf); r:=1; for ii from 0 to size(opf)-1 do r:=r*factorsqrfree(opf[ii]); od return r end"),
		/**
		 * Eliminate variables from a polynomial ideal. If the result is a set
		 * of discrete points, then convert the linear polynomials to a product
		 * of circle definitions with zero radius.
		 */
		GEOM_ELIM("geomElim", "geomElim(polys,elimvars,precision):=begin local ee, ll, ff, gg, ii; ee:=eliminate(polys,revlist(elimvars)); /*print(ee);*/ ll:=lvar(ee); /*print(ll);*/ if (size(ee)>1) begin /*print(fsolve(ee,ll));*/ ff:=round(fsolve(ee,ll)*precision)/precision; /*print(ff);*/ gg:=1; for ii from 0 to size(ff)-1 do gg:=gg*(((ll[0]-ff[ii,0])^2+(ll[1]-ff[ii,1])^2)); /*print(gg);*/ od; ee:=[expand(lcm(denom(coeff(gg)))*gg)]; end; if (size(ee)==0) return 0; else return primpoly(ee)[0]; end;"),
		/**
		 * Help simplifying the input when computing the Jacobian matrix in the
		 * Envelope command. Input: a list of polynomials and a list of
		 * variables which will not be used as derivatives. Output: another list
		 * of polynomials (a shorter list) which does not contain the linear
		 * ones and equivalent with the input. Note that
		 * op(solve(polys[ii]=0,linvar)[0])[1] is required in GeoGebra mode, in
		 * standard Giac here solve(polys[ii],linvar)[0] should be written.
		 * 
		 * The algorithm finds the polys which have one variable and it is
		 * linear. After solving such a poly=0 equation, the solution will be
		 * substituted into all other polys. After doing this for all one
		 * variable linear polys recursively, the resulted polys will be used in
		 * the Jacobian matrix in jacobiDet().
		 * 
		 * Used internally.
		 */
		JACOBI_PREPARE("jacobiPrepare", "jacobiPrepare(polys,excludevars):=begin local ii, degs, pos, vars, linvar; vars:=lvar(polys); ii:=0; while (ii<size(polys)-1) do degs:=degree(polys[ii],vars); if (sum(degs)=1) begin pos:=find(1,degs); linvar:=vars[pos[0]]; if (!is_element(linvar,excludevars)) begin substval:=op(solve(polys[ii]=0,linvar)[0])[1]; polys:=remove(0,expand(subs(polys,[linvar],[substval]))); /*print(polys);*/ ii:=-1; end; end; ii:=ii+1; od; return polys; end"),
		/**
		 * Compute the Jacobian determinant of the polys with respect to
		 * excludevars. Used internally.
		 */
		JACOBI_DET("jacobiDet", "jacobiDet(polys,excludevars):=begin local J, ii, vars, s, j, k; vars:=lvar(polys); for ii from 0 to size(excludevars)-1 do vars:=remove(excludevars[ii], vars); od; s:=size(vars); J:=matrix(s,s,(j,k)->diff(polys[j],vars[k])); return det_minor(J); end"),
		/**
		 * Compute the Jacobian determinant of the polys with respect to
		 * excludevars, but first some geometrical preparations are performed to
		 * simplify the result. Used internally.
		 */
		GEOM_JACOBI_DET("geomJacobiDet",
				"geomJacobiDet(polys,excludevars):=begin local J; J:=jacobiPrepare(polys,excludevars); return jacobiDet(J,excludevars); end"),
		/**
		 * Compute the coefficients of the envelope equation for the input
		 * polys, elimvars with given precision for the curve variables x and y.
		 * Used publicly.
		 */
		ENVELOPE_EQU("envelopeEqu",
				"envelopeEqu(polys,elimvars,precision,curvevarx,curvevary):=begin local D; D:=geomJacobiDet(polys,[curvevarx,curvevary]); polys:=append(polys,D); return locusEqu(polys,elimvars,precision,curvevarx,curvevary); end"),
		/**
		 * Compute the coefficients of the locus equation for the input polys,
		 * elimvars with given precision for the curve variables x and y. Used
		 * publicly.
		 */
		LOCUS_EQU("locusEqu", "locusEqu(polys,elimvars,precision,curvevarx,curvevary):=implicitCurveCoeffs(subst(geomElim(jacobiPrepare(polys,[curvevarx,curvevary]),elimvars,precision),[curvevarx=x,curvevary=y]))"),
		/**
		 * Compute coefficient matrix of the input polynomial. The output is a
		 * flattened variant of the matrix: the elements are returned row by
		 * row, starting with the sizes of the matrix: height and width. Used
		 * internally.
		 */
		COEFF_MATRIX("coeffMatrix", "coeffMatrix(aa):=begin local bb, sx, sy, ii, jj, ee, cc, kk; bb:=coeffs(aa,x); sx:=size(bb); sy:=size(coeffs(aa,y)); cc:=[sx,sy]; for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y); sd:=size(dd); for jj from sd-1 to 0 by -1 do ee:=dd[jj]; cc:=append(cc,ee); od; for kk from sd to sy-1 do ee:=0; cc:=append(cc,ee); od; od; return cc; end"),
		/**
		 * Compute the coefficient matrices for the factors of the input
		 * polynomial. The first number in the flattened output is the number of
		 * the coefficient matrices, then each coefficient matrix is added. Used
		 * internally.
		 */
		COEFF_MATRICES("coeffMatrices", "coeffMatrices(aa):=begin local ff, bb, ccf, ll, aaf; ff:=factors(aa); ccf:=[size(ff)/2]; for ll from 0 to size(ff)-1 by 2 do aaf:=ff[ll]; bb:=coeffMatrix(aaf); ccf:=append(ccf,bb); od; return flatten(ccf); end"),
		/**
		 * Compute the flattened coefficient matrix as it is directly used when
		 * the algebraic curve is plotted as an implicit poly. Used publicly.
		 */
		IMPLICIT_CURVE_COEFFS("implicitCurveCoeffs", "implicitCurveCoeffs(aa):=begin local bb; bb:=factorsqrfree(aa); return [coeffMatrix(bb),coeffMatrices(bb)]; end"),
		/**
		 * Decide if a poly is irreducible.
		 */
		IRRED("irred", "irred(p,x):=begin local f; f:=factors(primpart(p,x)); return (size(f)==2 && f[1]==1); end"),
		/**
		 * Absolute factorization of a poly in 2 vars: create the algebraic
		 * number to extend Q. We assume that the poly is irreducible over Q.
		 */
		AFACTOR_ALG_NUM("afactorAlgNum", "afactorAlgNum(p):=begin local k,l,j,d,extdeg,xx,lv,px,lc,lv2,py,fy,lfy,yy,fydeg,deg,pm,pdeg; l:=lname(p); if (!irred(p,l[0])) return \"Not irreducible\"; if (size(l)<2) return p; d:=[]; for j in l do d:=append(d,degree(p,j)); od; extdeg:=lgcd(d); if (extdeg==1) return \"Absolutely irreducible\"; xx:=head(l); pdeg:=degree(p,xx); l:=tail(l); for j from 1 to 1000 do lv:=ranv(size(l),j); px:=primpart(subst(p,l,lv),xx); if (degree(px,xx)!=pdeg) continue; if (irred(px,xx)) break; od; lc:=lcoeff(px,xx); if (lc!=1) px:=primpart(subst(px,xx,xx/lc),xx); for j from j to 1000 do lv2:=ranv(size(l),extdeg+j); if (lv2==lv) continue; py:=primpart(subst(p,l,lv2),xx); if (degree(py,xx)!=pdeg || !irred(py,xx)) continue; fy:=factors(py,rootof(px)); fydeg:=map(fy,yy->degree(yy,xx)); deg:=gcd(fydeg); deg:=d[0]/deg; if (deg==extdeg && degree(px)==extdeg) break; extdeg:=gcd(deg,extdeg); if (extdeg==1) return \"Absolutely irreducible\"; if (deg>extdeg) continue; for k from 0 to size(fydeg)-1 do if (fydeg[k]*extdeg==d[0]) break; od; if (k==size(fydeg)) continue; lfy:=coeff(fy[k],xx); for k from 0 to size(lfy)-1 do pm:=pmin(lfy[k]); if (degree(pm)==extdeg) begin px:=pm; break; end; od; od; return px; end"),
		/**
		 * Absolute factorization of a poly in 2 vars: return the factorization
		 * over the extension. We assume that the poly is irreducible over Q.
		 */
		ABSFACT("absfact", "absfact(p):=begin local algnum; algnum:=afactorAlgNum(p); /*print(algnum,type(algnum));*/ if (type(algnum)==DOM_LIST || type(algnum)==DOM_SYMBOLIC) return factor(p,rootof(algnum)); else return p; end"),
		/**
		 * Examples: absfact(y^4 +2*y^2*x+14*y^2-7*x^2 +6*x+47) should return
		 * -7*(x+(-2*sqrt(2)-1)/7*y^2+(-13*sqrt(2)-3)/7)*(x+(2*sqrt(2)-1)/7*y^2+(13*sqrt(2)-3)/7).
		 * absfact(16x^4+16y^4-16x^2*y^2-72x^2-72y^2+81) should return
		 * 16*(x^2+(-sqrt(3))*x*y+y^2-9/4)*(x^2+sqrt(3)*x*y+y^2-9/4).
		 * absfact(x^2*y^2-2) should return (x*y-sqrt(2))*(x*y+sqrt(2)).
		 * absfact(x^2*y^2+2) should return (x*y+i*sqrt(2))*(x*y-i*sqrt(2)).
		 */

		/**
		 * Giac uses round(x):=floor(x+0.5) but we want "round half up" to be
		 * consistent with the Algebra View
		 */
		GGB_ROUND("ggbround",
					"ggbround(x):=when(evalf(x)==?||evalf(x)=={?},?,when(type(evalf(x))==DOM_LIST,seq(ggbround(x[j]),j,0,length(x)-1),when(type(evalf(x))==DOM_COMPLEX,ggbround(real(x))+i*ggbround(im(x)),when(x<0,when(type(x)==DOM_LIST&&length(x)==2,-round(-x[0], x[1]),-round(-x)),round(x)))))"),

		/**
		 * Minimal polynomial of cos(2pi/n), see GGB-2137 for details.
		 */
		COS_2PI_OVER_N_MINPOLY("cos2piOverNMinpoly", "cos2piOverNMinpoly(n):=begin local j, p, q, r; p:=simplify((tchebyshev1(n)-1)/(x-1)); for j from 1 to n/2 do q:=tchebyshev1(j)-1; r:=gcd(p,q); p:=simplify(p/r); od; return factorsqrfree(primpart(p)); end");

		/** function name */
		final public String functionName;
		/** definition string */
		final public String definitionString;
		private static List<Entry<CustomFunctions, CustomFunctions>> CustomFunctionsDependencies;

		CustomFunctions(String functionName, String definitionString) {
			this.functionName = functionName;
			this.definitionString = definitionString;
		}

		@Override
		public String toString() {
			return functionName;
		}

		private static void setDependency(CustomFunctions cf1,
				CustomFunctions cf2) {
			Entry<CustomFunctions, CustomFunctions> pair = new SimpleEntry<>(
					cf1, cf2);
			CustomFunctionsDependencies.add(pair);
		}

		/**
		 * Create dependencies between two CAS custom functions. This is
		 * required to ensure that all dependencies will be loaded when a custom
		 * function is loaded.
		 */
		public static void setDependencies() {
			CustomFunctionsDependencies = new ArrayList<>();
			setDependency(IMPLICIT_CURVE_COEFFS, COEFF_MATRIX);
			setDependency(IMPLICIT_CURVE_COEFFS, COEFF_MATRICES);
			setDependency(IMPLICIT_CURVE_COEFFS, FACTOR_SQR_FREE);
			setDependency(GEOM_ELIM, PRIM_POLY);
			setDependency(LOCUS_EQU, IMPLICIT_CURVE_COEFFS);
			setDependency(LOCUS_EQU, GEOM_ELIM);
			setDependency(LOCUS_EQU, JACOBI_PREPARE);
			setDependency(ENVELOPE_EQU, LOCUS_EQU);
			setDependency(ENVELOPE_EQU, GEOM_JACOBI_DET);
			setDependency(GEOM_JACOBI_DET, JACOBI_PREPARE);
			setDependency(GEOM_JACOBI_DET, JACOBI_DET);
			setDependency(AFACTOR_ALG_NUM, IRRED);
			setDependency(ABSFACT, AFACTOR_ALG_NUM);
			setDependency(COS_2PI_OVER_N_MINPOLY, FACTOR_SQR_FREE);
			setDependency(CHECK_DERIVATIVE, XCOORD);
			setDependency(CHECK_DERIVATIVE, YCOORD);
			setDependency(CHECK_DERIVATIVE, IS_ALMOST_ZERO);
		}

		/**
		 * Create the list of prerequisites of a custom command. TODO: Currently
		 * we don't have a complex tree of dependencies. Later we may add a more
		 * sophisticated algorithm here to remove duplicates, be faster etc.
		 * 
		 * @param cf
		 *            the custom command
		 * @return the prerequisites
		 */
		public static ArrayList<CustomFunctions> prereqs(
				CustomFunctions cf) {
			ArrayList<CustomFunctions> list = new ArrayList<>();
			for (Entry<CustomFunctions, CustomFunctions> pair : CustomFunctionsDependencies) {
				CustomFunctions key = pair.getKey();
				CustomFunctions value = pair.getValue();
				if (key.equals(cf)) {
					list.add(0, value);
					list.addAll(0, prereqs(value));
				}
			}
			return list;
		}
	}

	/** CAS parser */
	protected CASparser casParser;

	private static int nrOfReplacedConst = 0;
	/**
	 * Timeout for CAS in milliseconds. This can be changed in the CAS options.
	 */
	public long timeoutMillis = 5000;
	final private static String EVALFA = "evalfa(";
	private StringBuilder expSB = new StringBuilder(EVALFA);
	private MaxSizeHashMap<String, String> casGiacCache = new MaxSizeHashMap<>(Kernel.GEOGEBRA_CAS_CACHE_SIZE);

	// eg {(ggbtmpvarx>(-sqrt(110)/5)) && ((sqrt(110)/5)>ggbtmpvarx)}
	// eg {(ggbtmpvarx>=(-sqrt(110)/5)) && ((sqrt(110)/5)>=ggbtmpvarx)}
	// eg (ggbtmpvarx>3) && (4>ggbtmpvarx)
	/** expression with at most 3 levels of brackets */
	public final static String expression = "(([^\\(\\)]|\\([^\\(\\)]+\\)|\\(([^\\(\\)]|\\([^\\(\\)]+\\))+\\))+)";
	/**
	 * inequality a &gt;=? ex1 &amp;&amp; ex2 &gt;=? b where a,b are literals and ex1, ex2 are
	 * expressions with at most 3 brackets
	 */
	public final static RegExp inequality = RegExp
			.compile("^(.*)\\(([A-Za-z]+)>(=*)" + expression + "\\) && \\("
					+ expression + ">(=*)([A-Za-z]+)\\)(.*)$", "");

	// eg 3.7 > ggbtmpvarx
	// eg (37/10) > ggbtmpvarx
	// eg 333 > ggbtmpvarx
	// eg (-33) > ggbtmpvarx
	// private final static RegExp inequalitySimple =
	// RegExp.compile("([-0-9.E/\\(\\)]+)>(=*)(ggbtmpvar.+)");
	// works only for variables in form [A-Za-z]+
	private final static RegExp inequalitySimple = RegExp
			.compile("^([-0-9.E/\\(\\)]+)>(=*)([A-Za-z]+)$");

	// eg {3, 3>ggbtmpvarx, x^2}
	// eg {3, 3>ggbtmpvarx}
	// eg {3>ggbtmpvarx, x^2}
	// eg {3>ggbtmpvarx}
	// works only for variables in form [A-Za-z]+ and if it's a simple number
	private final static RegExp inequalitySimpleInList = RegExp.compile(
			"(.*)([,{])(\\(*)?([-0-9.E]+)(\\)*)?>(=*)([A-Za-z]+)([,}\\)])(.*)");

	// old version, causes problems with eg Solve[exp(x)<2]
	// private final static RegExp inequalitySimpleInList =
	// RegExp.compile("(.*)([,{\\(])([-0-9.E/\\(\\)]+)>(=*)([A-Za-z]+)([,}\\)])(.*)");

	/**
	 * Creates new Giac CAS
	 * 
	 * @param casParser
	 *            parser
	 */
	public CASgiac(CASparser casParser) {
		this.casParser = casParser;
	}

	/**
	 * @param exp
	 *            Giac command
	 * @return value returned from CAS
	 */
	@Override
	public abstract String evaluateCAS(String exp);

	@Override
	final public String evaluateRaw(final String input) throws Throwable {

		String exp = input;

		Log.debug("input = " + input);

		String cachedResult = getResultFromCache(input);

		if (cachedResult != null && !cachedResult.isEmpty()) {
			return cachedResult;
		}

		String result = evaluate(exp, getTimeoutMilliseconds());

		// FIXME: This check is too heuristic: in giac.js we can get results
		// starting with \"
		// and they are still correct (e.g. from eliminateFactorized).
		// TODO: Find a better way for checking, now we assume that \"[ start is
		// OK (or \"\").

		String rtrimmed = result.trim();
		if (rtrimmed.startsWith("\"") && rtrimmed.endsWith("\"")) {
				result = result.substring(1, result.length() - 1); // removing
		}

		Log.debug("result = " + result);

		addResultToCache(input, result);

		return result;
	}

	protected void addResultToCache(String input, String result) {
		boolean inputContainsExcludedString =
				EXCLUDE_FROM_CACHE.stream().anyMatch(str -> input.contains(str));
		if (!inputContainsExcludedString) {
			casGiacCache.put(input, result);
		}
	}

	protected String getResultFromCache(String input) {
		return casGiacCache.get(input);
	}

	/**
	 * @param exp
	 *            expression string
	 * @param timeoutMilliseconds
	 *            timeout in milliseconds
	 * @return result in Giac syntax
	 * @throws Throwable
	 *             for CAS error
	 */
	protected abstract String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable;

	@Override
	final public synchronized String evaluateGeoGebraCAS(
			final ValidExpression inputExpression, ArbitraryConstantRegistry arbconst,
			StringTemplate tpl, GeoCasCell cell, Kernel kernel)
			throws CASException {

		ValidExpression casInput = inputExpression;
		Command cmd = casInput.getTopLevelCommand();
		boolean keepInput = (cell != null && cell.isKeepInputUsed())
				|| (cmd != null && "KeepInput".equals(cmd.getName()));
		String plainResult = getPlainResult(casInput, kernel);

		if (keepInput) {
			// remove KeepInput[] command and take argument
			if (cmd != null && cmd.getName().equals("KeepInput")) {
				// use argument of KeepInput as casInput
				if (cmd.getArgumentNumber() > 0) {
					casInput = cmd.getArgument(0);
				}
			}
		}

		// convert result back into GeoGebra syntax
		if (casInput instanceof FunctionNVar) {
			// delayed function definition f(x)::= Derivative[x^2] should return
			// Derivative[x^2]
			if (cell != null
					&& cell.getAssignmentType() == AssignmentType.DELAYED) {
				return casInput.toString(StringTemplate.numericNoLocal);
			}
			// function definition f(x) := x^2 should return x^2
			// f(x):=Derivative[x^2] should return 2x
			// do not return directly, must check keepinput
			/*
			 * plainResult = evaluateMPReduce(plainResult + "(" +
			 * ((FunctionNVar)
			 * casInput).getVarString(StringTemplate.casTemplate) + ")");
			 */
		}

		String result = plainResult;

		if (keepInput && (cell == null || !cell.isSubstitute())) {
			// assume keepinput was not treated in CAS
			return casParser.toGeoGebraString(casInput, tpl);
		}

		if (keepInput && (cell != null && cell.isSubstitute())) {
			// assume keepinput was not treated in CAS
			ExpressionValue substList = casInput.getTopLevelCommand()
					.getArgument(1).unwrap();
			ExpressionValue substArg = casInput.getTopLevelCommand()
					.getArgument(0);
			if (substList instanceof MyList) {
				for (int i = 0; i < ((MyList) substList).size(); i++) {
					substArg = subst(substArg, ((MyList) substList).getItem(i),
							cell.getKernel());
				}
			} else {
				substArg = subst(substArg, substList, cell.getKernel());
			}
			if (substArg != null) {
				return casParser.toGeoGebraString(substArg, tpl);
			}
		}

		// standard case
		if (result == null || result.isEmpty()) {
			return null;
		}
		return toGeoGebraString(result, arbconst, tpl, kernel);

	}

	private static ExpressionValue subst(ExpressionValue substArg,
			ExpressionValue item0, Kernel kernel) {
		ExpressionValue item = item0.unwrap();
		if (item instanceof Equation) {
			ExpressionValue lhs = ((Equation) item).getLHS().unwrap();
			if (lhs instanceof GeoDummyVariable || lhs instanceof Variable) {
				ExpressionValue rhs = ((Equation) item).getRHS().unwrap();
				ExpressionValue copy = substArg.deepCopy(kernel);
				copy.traverse(VariableReplacer.getReplacer(
						lhs.toString(StringTemplate.defaultTemplate), rhs,
						kernel));
				return copy;
			}
		}
		return null;
	}

	@Override
	final public synchronized ExpressionValue evaluateToExpression(
			final ValidExpression inputExpression, ArbitraryConstantRegistry arbconst,
			Kernel kernel) throws CASException {
		String result = getPlainResult(inputExpression, kernel);
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return replaceRoots(casParser.parseGiac(result), arbconst, kernel);

	}

	private String getPlainResult(ValidExpression casInput, Kernel kernel) {
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		Command cmd = casInput.getTopLevelCommand();

		if (cmd != null && "Delete".equals(cmd.getName())) {
			ExpressionValue toDelete = cmd.getArgument(0).unwrap();
			if (toDelete.isExpressionNode() && (((ExpressionNode) toDelete)
					.getOperation() == Operation.FUNCTION
					|| ((ExpressionNode) toDelete)
							.getOperation() == Operation.FUNCTION_NVAR)) {
				toDelete = ((ExpressionNode) toDelete).getLeft();
			}
			String label = toDelete.toString(StringTemplate.defaultTemplate);
			GeoElement geo = kernel.lookupLabel(label);
			if (geo == null) {
				geo = kernel.lookupCasCellLabel(label);
			}
			if (geo != null) {
				geo.remove();
			}
			return "true";
		}
		
		// convert parsed input to Giac string
		String giacInput = casParser.translateToCAS(casInput,
				StringTemplate.giacTemplate, this);

		// evaluate in Giac
		String plainResult = evaluateCAS(giacInput);

		// try again for undefined result
		// eg Numeric(0.99999874^(16500))
		// doesn't work in "exact" mode
		if (isUndefined(plainResult) && cmd != null
				&& "Numeric".equals(cmd.getName())) {
			giacInput = casParser.translateToCAS(casInput,
					StringTemplate.giacNumeric13, this);

			// evaluate in Giac
			plainResult = evaluateCAS(giacInput);

		}

		// get initial nr of vars
		int nrOfVars = casParser.getNrOfVars();
		StringBuilder newPlainResult = new StringBuilder();
		// case we need to process the result
		if (nrOfVars > 0) {
			// get array of potential results
			String[] partsOfResult = plainResult.split("},");
			for (int i = 0; i < partsOfResult.length; i++) {
				// get array of solutions
				String[] partsOfCurrSol = partsOfResult[i].split(",");
				// append only asked solutions
				for (int j = 0; j < nrOfVars; j++) {
					if (j == nrOfVars - 1) {
						newPlainResult.append(partsOfCurrSol[j]);
						newPlainResult.append("},");
					} else {
						newPlainResult.append(partsOfCurrSol[j]);
						newPlainResult.append(",");
					}
				}
			}
			newPlainResult.setLength(newPlainResult.length() - 1);
			newPlainResult.append("}");
			// reset nrOfVars
			casParser.setNrOfVars(0);
			return newPlainResult.toString();
		}
		return plainResult;
	}

	/**
	 * 
	 * @param result
	 *            result from Giac to check
	 * @return true if result is undefined
	 */
	public static boolean isUndefined(String result) {
		return "?".equals(result) || "".equals(result) || "undef".equals(result)
				|| FORCE_ERROR.equals(result) || result == null;
	}

	/**
	 * Tries to parse a given Giac string and returns a String in GeoGebra
	 * syntax.
	 * 
	 * @param giacString
	 *            String in Giac syntax
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            template that should be used for serialization. Should be
	 *            casCellTemplate for CAS and defaultTemplate for input bar
	 * @param kernel
	 *            kernel
	 * @return String in Geogebra syntax.
	 * @throws CASException
	 *             Throws if the underlying CAS produces an error
	 */
	final public synchronized String toGeoGebraString(String giacString,
			ArbitraryConstantRegistry arbconst,	final StringTemplate tpl,
			final Kernel kernel) throws CASException {

		ExpressionValue ve = replaceRoots(casParser.parseGiac(giacString),
				arbconst, kernel);
		// replace rational exponents by roots or vice versa

		ve = ve.traverse(new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof MyVecNDNode
						&& ((MyVecNDNode) ev).isCASVector()) {
					return new ExpressionNode(kernel,
							new Variable(kernel, "ggbvect"), Operation.FUNCTION,
							ev);
				}
				return ev;
			}
		});

		ve = ve.traverse(new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof ExpressionNode) {
					ExpressionNode node = (ExpressionNode) ev;
					if (node.isLeaf() && tpl.isRad(node.unwrap())) {
						node.setOperation(Operation.MULTIPLY);
						node.setRight(node.getLeft());
						node.setLeft(new MyDouble(kernel, 1));
					}
				}
				return ev;
			}
		});

		return casParser.toGeoGebraString(ve, tpl);
	}

	private static ExpressionValue replaceRoots(ExpressionValue ve0,
			ArbitraryConstantRegistry arbconst, Kernel kernel) {
		ExpressionValue ve = ve0;
		if (ve != null) {
			boolean toRoot = kernel.getApplication().getSettings()
					.getCasSettings().getShowExpAsRoots();
			ve = ve.traverse(DiffReplacer.INSTANCE);
			ve = ve.traverse(PowerRootReplacer.getReplacer(toRoot));
			if (arbconst != null) {
				arbconst.reset();
				ve = ve.traverse(ArbconstReplacer.getReplacer(arbconst));
			}
			PrefixRemover pr = PrefixRemover.getCollector();
			ve = ve.traverse(pr);
		}
		return ve;
	}

	/**
	 * @return CAS timeout in seconds
	 */
	protected long getTimeoutMilliseconds() {
		return timeoutMillis;
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		CASSettings s = (CASSettings) settings;
		timeoutMillis = s.getTimeoutMilliseconds();
	}

	@Override
	public String translateAssignment(final String label, final String body) {
		return label + " := " + body;
	}

	/**
	 * This method is called when asynchronous CAS call is finished. It tells
	 * the calling algo to update itself and adds the result to cache if
	 * suitable.
	 * 
	 * @param exp
	 *            parsed CAS output
	 * @param result2
	 *            output as string (for caching)
	 * @param exception
	 *            exception which stopped the computation (null if there wasn't
	 *            one)
	 * @param c
	 *            command that called the CAS asynchronously
	 * @param input
	 *            input string (for caching)
	 * @param cell
	 *            cas cell
	 */
	public void CASAsyncFinished(ValidExpression exp, String result2,
			Throwable exception, AsynchronousCommand c, String input,
			GeoCasCell cell) {
		String result = result2;
		// pass on exception
		if (exception != null) {
			c.handleException(exception, input.hashCode());
			return;
		}
		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (cell.isKeepInputUsed() && ("?".equals(result))) {
			// return original input
			c.handleCASoutput(exp.toString(StringTemplate.maxPrecision),
					input.hashCode());
		}

		// success
		if (result2 != null) {
			c.getKernel();
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = Kernel.removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result, input.hashCode());
		if (c.useCacheing()) {
			c.getKernel().putToCasCache(input, result);
		}
	}

	@Override
	public void appendListStart(StringBuilder sbCASCommand) {
		sbCASCommand.append("[");
	}

	@Override
	public void appendListEnd(StringBuilder sbCASCommand) {
		sbCASCommand.append("]");
	}

	@Override
	public String createEliminateFactorizedScript(String polys,
			String elimVars) {
		/*
		 * Some examples to understand the code below:
		 * 
		 * [[aa:=eliminate([-1*v1,-1*v11*v10+v12*v9+v11*v8+-1*v9*v8+-1*v12*v7+
		 * v10 *v7,v13*v8+-1*v14*v7,-1*v13+v13*v10+v9+-1*v14*v9,
		 * -1*v15*v10+v16*v9+v15
		 * *v2+-1*v9*v2+-1*v16*v1+v10*v1,v15*v12+-1*v16*v11,
		 * v17+-1*v17*v12+-1*v11+v18*v11,
		 * -1*v17*v8+v18*v7+v17*v2+-1*v7*v2+-1*v18
		 * *v1+v8*v1,-1+-1*v19*v17*v16+v19
		 * *v18*v15+v19*v17*v14+-1*v19*v15*v14+-1*v19*v18*v13+v19*v16*v13],
		 * [v17,v16,v19,v1,v18,v8,v13,v14,v15])],[bb:=size(aa)],[for ii from 0
		 * to bb-1 do cc[ii]:=factors(aa[ii]); od], cc][3]
		 * 
		 * table( 1 = [v2-1,1,v7,1,v12-1,1], 2 = [v2,1,v9,1,v12,1], 3 =
		 * [v7,1,v10-1,1], 4 = [v12,1,v12-1,1,-1,1,v2,1,v10-1,1,v10-v2,1], 5 =
		 * [-v2+1,1,v7,1,v11,1], 6 = [v2,1,v9,1,v11,1], 7 =
		 * [-v11*v10+v11*v2+v12*v9,1], 0 = [v7,1,v9,1] )
		 * 
		 * But we need the same output as Singular does, so we use this code
		 * instead:
		 * 
		 * [[aa:=eliminate([-1*v1,-1*v11*v10+v12*v9+v11*v8+-1*v9*v8+-1*v12*v7+
		 * v10 *v7,v13*v8+-1*v14*v7,-1*v13+v13*v10+v9+-1*v14*v9,
		 * -1*v15*v10+v16*v9+v15
		 * *v2+-1*v9*v2+-1*v16*v1+v10*v1,v15*v12+-1*v16*v11,
		 * v17+-1*v17*v12+-1*v11+v18*v11,
		 * -1*v17*v8+v18*v7+v17*v2+-1*v7*v2+-1*v18
		 * *v1+v8*v1,-1+-1*v19*v17*v16+v19
		 * *v18*v15+v19*v17*v14+-1*v19*v15*v14+-1*v19*v18*v13+v19*v16*v13],
		 * [v17,v16,v19,v2,v18,v8,v13,v14,v15])],[bb:=size(aa)],[for ii from 0
		 * to bb-1 do print("["+(ii+1)+"]:");print(" [1]:");print("  _[1]=1"
		 * );cc: =factors(aa[ii]);dd:=size(cc); for jj from 0 to dd-1 by 2 do
		 * print("  _["+(jj/2+2)+"]="+(cc[jj])); od; print(" [2]:"); print("  "
		 * +cc[1]);for kk from 1 to dd-1 by 2 do print("   ,"
		 * +cc[kk]);od;od],0][3]
		 * 
		 * which gives
		 * 
		 * [1]: [1]: _[1]=1 _[2]=v7 _[3]=v9 [2]: 1 ,1 ,1 [2]: [1]: _[1]=1
		 * _[2]=v7 _[3]=v10-1 [2]: 1 ,1 ,1 [3]: [1]: _[1]=1 _[2]=v9 _[3]=-1
		 * _[4]=v11*v10-v9*v12 [2]: 1 ,1 ,1 ,1 [4]: [1]: _[1]=1 _[2]=v1 [2]: 1
		 * ,1 0
		 * 
		 * in giac with CoCoA support on command line and runs forever in
		 * giac.js.
		 */
		StringBuilder script = new StringBuilder();

		/*
		 * return script.append("[[aa:=eliminate(["). append(polys).
		 * append("],["). append(elimVars). append(
		 * "])],[bb:=size(aa)],[for ii from 0 to bb-1 do print(\"[\"+(ii+1)+\"]:\");print(\" [1]:\");"
		 * ). append("print(\"  _[1]=1\");cc:=factors(aa[ii]);dd:=size(cc);").
		 * append(
		 * "for jj from 0 to dd-1 by 2 do print(\"  _[\"+(jj/2+2)+\"]=\"+(cc[jj])); od; print(\" [2]:\");"
		 * ). append(
		 * "print(\"  \"+cc[1]);for kk from 1 to dd-1 by 2 do print(\"   ,\"+cc[kk]);od;od],0][3]"
		 * )
		 * 
		 * .toString();
		 */

		String eliminateCommand = "eliminate([" + polys
				+ "],revlist(["
				+ elimVars + "]))";

		return script.append("[" + "[ff:=\"\"],[aa:=").append(eliminateCommand)
				.append("],")
				.append("[bb:=size(aa)],[for ii from 0 to bb-1 do ff+=(\"[\"+(ii+1)+\"]: [1]: ")
				.append(" _[1]=1\");ee:=aa[ii]/gcd(coeffs(aa[ii]));cc:=factors(ee);dd:=size(cc);")
				.append("for jj from 0 to dd-1 by 2 do ff+=(\"  _[\"+(jj/2+2)+\"]=\"+cc[jj]); od; ff+=(\" [2]: ")
				.append("\"+cc[1]);for kk from 1 to dd-1 by 2 do ff+=(\",\"+cc[kk]);od;od],[if(ff==\"\") begin ff:=[0] end],ff][5]")

				.toString();

		// We return text from the CAS here.
	}

	/**
	 * Create a script which eliminates variables from a set of polynomials.
	 * 
	 * @param polys
	 *            the input polynomials
	 * @param elimVars
	 *            the variables to be eliminated
	 * @param oneCurve
	 *            if the output consists of more polynomials, consider the
	 *            intersections of them as points with real coordinates and
	 *            convert them to a single product
	 * @return the Giac program which creates the output ideal
	 */
	@Override
	public String createEliminateScript(String polys, String elimVars,
			boolean oneCurve, Long precision) {
		if (!oneCurve) {
			return CustomFunctions.PRIM_POLY + "(eliminate([" + polys
					+ "],revlist([" + elimVars + "])))";
		}

		String PRECISION = Long.toString(precision);
		Log.debug("PRECISION = " + PRECISION);
		String retval;
		/*
		 * Exact approach. This will not work if there are irrationals since
		 * sqrt(...) cannot be directly converted to a number.
		 */
		/*
		 * retval = "primpoly([[ee:=eliminate([" + polys + "],revlist([" +
		 * elimVars +
		 * "]))],[ll:=lvar(ee)],[if(size(ee)>1) begin ff:=solve(ee,ll);" +
		 * "gg:=1;for ii from 0 to size(ff)-1 do gg:=gg*right(((ll[0]-ff[ii,0])^2+(ll[1]-ff[ii,1])^2)-"
		 * + FAKE_NULL + ");" +
		 * "od ee:=[lcm(denom(coeff(gg)))*gg]; end],ee][3])";
		 */
		/*
		 * Rounded approach. This works in general, but we should check how
		 * fsolve is implemented. The best would be to use symbolical
		 * computation during solve(...) and then do the numerical
		 * approximation. TODO: Check how giac implements fsolve and use a
		 * different method if needed (and available).
		 */
		retval = CustomFunctions.PRIM_POLY + "([[ee:=eliminate([" + polys
				+ "],revlist([" + elimVars
				+ "]))],[ll:=lvar(ee)],[if(size(ee)>1) begin ff:=round(fsolve(ee,ll)*"
				+ PRECISION + ")/" + PRECISION + ";"
				+ "gg:=1;for ii from 0 to size(ff)-1 do gg:=gg*(((ll[0]-ff[ii,0])^2+(ll[1]-ff[ii,1])^2));"
				+ "od ee:=[expand(lcm(denom(coeff(gg)))*gg)]; end],ee][3])";
		return retval;
	}

	@Override
	public String createGroebnerSolvableScript(
			HashMap<PVariable, BigInteger> substitutions, String polys,
			String freeVars, String dependantVars, boolean transcext) {
		/*
		 * Example syntax (from Groebner basis tester; but in GeoGebra v1, v2,
		 * ... are used for variables):
		 * 
		 * [[ii:=gbasis(subst([2*d1-b1-c1, 2*d2-b2-c2,2*e1-a1-c1,
		 * 2*e2-a2-c2,2*f1-a1-b1, 2*f2-a2-b2 ,
		 * (d1-o1)*(b1-c1)+(d2-o2)*(b2-c2),(e1-o1)*(c1-a1)+(e2-o2)*(c2-a2),
		 * s1*d2
		 * +a1*(s2-d2)-d1*s2-a2*(s1-d1),s1*e2+b1*(s2-e2)-e1*s2-b2*(s1-e1),(a1
		 * -m1)*(b1-c1)+(a2-m2)*(b2-c2),
		 * (b1-m1)*(c1-a1)+(b2-m2)*(c2-a2),z1*(b1*c2
		 * +a1*(b2-c2)-c1*b2-a2*(b1-c1))-1, z2
		 * *(s1*m2+o1*(s2-m2)-m1*s2-o2*(s1-m1
		 * ))-1],[d1=0,b1=3]),[a1,a2,b1,b2,c1,c2,d1,d2,e1,e2,f1,f2,o1,
		 * o2,s1,s2,m1,m2,z1,z2],revlex)],(degree(ii[0])!=0)||(ii[0]==0)][1]
		 * 
		 * In the last part we check if the Groebner basis is a constant neq 0,
		 * i.e. its degree is 0 but it is not 0. If yes, there is no solution.
		 * 
		 * The giac implementation does not handle the case for request for
		 * polynomial ring with coefficients from a transcendental extension. We
		 * silently use a polynomial ring instead.
		 */

		String idealVar = "ii";

		String ret = "[[" + idealVar + ":=gbasis(";

		if (substitutions != null) {
			ret += "subst(";
		}

		ret += "[" + polys + "]";

		if (substitutions != null) {
			String substParams = substitutionsString(substitutions);
			ret += ",[" + substParams + "])";
		}

		String vars = freeVars + PPolynomial.addLeadingComma(dependantVars);

		// ret += ",[" + vars + "],revlex)],(degree(" +
		// idealVar + "[0])!=0)||(" + idealVar + "[0]==0)][2]";
		ret += ",[" + vars + "],revlex)],(" + idealVar + "[0]!=1)&&(" + idealVar
				+ "[0]!=-1)][1]";

		return ret;
	}

	@Override
	public String createGroebnerInitialsScript(
			HashMap<PVariable, BigInteger> substitutions, String polys,
			String freeVars, String dependantVars) {
		/*
		 * Example syntax (from Groebner basis tester; but in GeoGebra v1, v2,
		 * ... are used for variables):
		 * 
		 * gbasis(subst([2*d1-b1-c1, 2*d2-b2-c2,2*e1-a1-c1,
		 * 2*e2-a2-c2,2*f1-a1-b1, 2*f2-a2-b2 ,
		 * (d1-o1)*(b1-c1)+(d2-o2)*(b2-c2),(e1-o1)*(c1-a1)+(e2-o2)*(c2-a2),
		 * s1*d2
		 * +a1*(s2-d2)-d1*s2-a2*(s1-d1),s1*e2+b1*(s2-e2)-e1*s2-b2*(s1-e1),(a1
		 * -m1)*(b1-c1)+(a2-m2)*(b2-c2),
		 * (b1-m1)*(c1-a1)+(b2-m2)*(c2-a2),z1*(b1*c2
		 * +a1*(b2-c2)-c1*b2-a2*(b1-c1))-1, z2
		 * *(s1*m2+o1*(s2-m2)-m1*s2-o2*(s1-m1
		 * ))-1],[d1=0,b1=3]),[a1,a2,b1,b2,c1,c2,d1,d2,e1,e2,f1,f2,o1,
		 * o2,s1,s2,m1,m2,z1,z2],revlex)
		 */

		String ret = "[[GB:=gbasis(";

		if (substitutions != null) {
			ret += "subst(";
		}
		ret += "[" + polys + "]";
		if (substitutions != null) {
			String substParams = substitutionsString(substitutions);
			ret += ",[" + substParams + "])";
		}
		String vars = freeVars + PPolynomial.addLeadingComma(dependantVars);
		if (vars.startsWith(",")) {
			vars = vars.substring(1);
		}
		ret += ",[" + vars + "],revlex)";
		ret += "],[s:=size(GB)],[out:=[]],[for ii from 0 to s-1 do if (size(GB[ii])==1) out[ii]:=lvar(GB[ii]); else out[ii]:=lvar(GB[ii][1]); od],out][4]";
		return ret;
	}

	/**
	 * Converts substitutions to giac strings
	 * 
	 * @param substitutions
	 *            input as a HashMap
	 * @return the parameters for giac (e.g. "v1=0,v2=0,v3=0,v4=1")
	 * 
	 *         Taken from prover.Polynomial, one character difference. Maybe
	 *         commonize.
	 */
	static String substitutionsString(
			HashMap<PVariable, BigInteger> substitutions) {
		StringBuilder ret = new StringBuilder();
		Iterator<Entry<PVariable, BigInteger>> it = substitutions.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<PVariable, BigInteger> v = it.next();
			ret.append(",");
			ret.append(v.getKey().toString());
			ret.append("=");
			ret.append(v.getValue());
		}
		if (ret.length() > 0) {
			return ret.substring(1);
		}
		return "";
	}

	/**
	 * Combine non-factorized and factorized results as a 3 dimensional array.
	 *
	 * The input is like this example:
	 * {{6,5,-184,304,-160,28,-2,52,-136,96,-14,1,-2,-12,-12,0,0,27,2,2,0,0,-10,
	 * 0,0,0,0,1,0,0,0,0},{2,2,1,-2,1,5,5,92,-152,80,-14,1,20,-8,-8,0,0,11,2,2,0
	 * ,0,-8,0,0,0,0,1,0,0,0,0}} describes a Steiner deltoid
	 * (http://www.geogebra.org/m/xWCP09dk) by its expanded polynomial
	 * x^5+x*y^4+2*x^3*y^2-10*x^4-2*y^4-14*x*y^3-12*x^2*y^2+2*x^3*y+27*x^3+28*y^
	 * 3+96*x*y^2-12*x^2*y-2*x^2-160*y^2-136*x*y+52*x+304*y-184 which is the
	 * first list {6,5,...} and its two factors: x-2=0 and
	 * y^4-14*y^3+2*y^2*x^2-8*y^2*x+80*y^2+2*y*x^2-8*y*x-152*y+x^4-8*x^3+11*x^2+
	 * 20*x+92=0. In the second list {2,2,...} the first element tells the
	 * number of factors (2), then the coefficients are listed (2,1,-2,1 for
	 * x-2=0, here the first two elements describe the size of the coefficient
	 * matrix) and the rest for the other one.
	 *
	 * In the output the 0. element in the 1. dimension contains the
	 * non-factorized values, the next elements contain the factorized ones.
	 *
	 * @param rawResult
	 *            input string of coefficients of a polynomial and its factors
	 *            in a custom format
	 * @return the output array of coefficients in GeoImplicitCurve's format
	 */
	@Override
	public double[][][] getBivarPolyCoefficientsAll(String rawResult) {
		double[][] coeff = getBivarPolyCoefficients(rawResult);
		double[][][] coeffSquarefree = getBivarPolySquarefreeCoefficients(
				rawResult);
		double[][][] retval = new double[coeffSquarefree.length + 1][][];
		retval[0] = coeff;
		for (int i = 0; i < coeffSquarefree.length; ++i) {
			retval[i + 1] = coeffSquarefree[i];
		}
		return retval;
	}

	private static double[][][] getBivarPolySquarefreeCoefficients(
			String rawResult) {

		int firstClosingBracket = rawResult.indexOf('}');
		String numbers = rawResult.substring(firstClosingBracket + 3,
				rawResult.length() - 2);
		String[] flatData = numbers.split(",");
		int factors = Integer.parseInt(flatData[0]);
		double[][][] result = new double[factors][][];
		int counter = 1;

		for (int factor = 0; factor < factors; ++factor) {

			int xLength = Integer.parseInt(flatData[counter++]);
			int yLength = Integer.parseInt(flatData[counter++]);
			result[factor] = new double[xLength][yLength];

			for (int x = 0; x < xLength; x++) {
				for (int y = 0; y < yLength; y++) {
					result[factor][x][y] = Double
							.parseDouble(flatData[counter]);
					Log.trace("[LocusEqu] result[" + factor + "][" + x + "," + y
							+ "]=" + result[factor][x][y]);
					++counter;
				}
			}
		}

		return result;

	}

	private static double[][] getBivarPolyCoefficients(String rawResult) {
		int firstClosingBracket = rawResult.indexOf('}');
		String numbers = rawResult.substring(2, firstClosingBracket);
		String[] flatData = numbers.split(",");
		int xLength = Integer.parseInt(flatData[0]);
		int yLength = Integer.parseInt(flatData[1]);
		double[][] result = new double[xLength][yLength];

		int counter = 2;
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				result[x][y] = Double.parseDouble(flatData[counter]);
				Log.trace("[LocusEqu] result[" + x + "," + y + "]="
						+ result[x][y]);
				++counter;
			}
		}

		return result;
	}

	/**
	 * convert x&gt;3 &amp;&amp; x&lt;7 into 3&lt;x&lt;7, convert 3&gt;x into x&lt;3,
	 * convert {3&gt;x} into {x&lt;3} eg output from Solve[x (x-1)(x-2)(x-3)(x-4)(x-5) &lt; 0]
	 * 
	 * Giac's normal command converts inequalities to &gt; or &gt;= so we don't need
	 * to check &lt;, &lt;=
	 * 
	 * @param exp
	 *            expression
	 * @return converted expression if changed
	 */
	protected String checkInequalityInterval(String exp) {

		String ret = exp;

		MatchResult matcher = inequalitySimple.exec(ret);

		// swap 3>x into x<3
		if (matcher != null && exp.startsWith(matcher.getGroup(1))) {
			ret = matcher.getGroup(3) + "<" + matcher.getGroup(2)
					+ matcher.getGroup(1);
			Log.debug("giac output (with simple inequality converted): " + ret);
			return ret;
		}

		// swap 5 > x && x > 3 into 3<x<5
		while ((matcher = inequality.exec(ret)) != null &&
		// TODO: check not x<3 && x<4

		// check variable the same
		// ie not x>5 && y<4
				matcher.getGroup(2).equals(matcher.getGroup(11))) {

			ret = matcher.getGroup(1) + matcher.getGroup(4) + "<"
					+ matcher.getGroup(3) + matcher.getGroup(2) + "<"
					+ matcher.getGroup(10) + matcher.getGroup(7)
					+ matcher.getGroup(12);
		}

		// swap {3>x, 6>y} into {x<3, y<6}
		while ((matcher = inequalitySimpleInList.exec(ret)) != null) {

			// matcher.getGroup(6) is either "" or "="

			// Log.debug("1 "+matcher.getGroup(1));
			// Log.debug("2 "+matcher.getGroup(2));
			// Log.debug("3XX"+matcher.getGroup(3)+"XX");
			// Log.debug(""+matcher.getGroup(3).equals("undefined"));
			// Log.debug("4 "+matcher.getGroup(4));
			// Log.debug("5XX"+matcher.getGroup(5)+"XX");
			// Log.debug("6 "+matcher.getGroup(6));
			// Log.debug("7 "+matcher.getGroup(7));
			// Log.debug("8 "+matcher.getGroup(8));
			// Log.debug("9 "+matcher.getGroup(9));

			String g3 = matcher.getGroup(3);
			String g5 = matcher.getGroup(5);

			// GWT regex bug? eg Solve[(2exp(x)-4)/(exp(x)-1) > 1],
			// Solve[(x^2-x-2)/(-x^3+7x^2-14x+8)<2]
			// #4710 GWT 2.7 beta 1 & RC1 needs the +"" as well
			if ((g3 + "").equals("undefined")) {
				g3 = "";
			}
			if ((g5 + "").equals("undefined")) {
				g5 = "";
			}

			// Log.debug("g3= "+g3);
			// Log.debug("g5= "+g5);

			// eg "(" + "-2" + ")"
			String g345 = g3 + matcher.getGroup(4) + g5;
			String g7 = matcher.getGroup(7);

			ret = matcher.getGroup(1) + matcher.getGroup(2) + g7 + "<"
					+ matcher.getGroup(6) + g345 + matcher.getGroup(8)
					+ matcher.getGroup(9);
		}

		if (!exp.equals(ret)) {
			Log.debug("giac output (with inequality converted): " + ret);
		}

		return ret;

	}

	/**
	 * various improvements and hack for Giac's output
	 * 
	 * @param s
	 *            output from Giac
	 * @return result that GeoGebra can parse
	 */
	protected String postProcess(String s) {

		if (s.indexOf("GIAC_ERROR") > -1) {
			// GIAC_ERROR: canonical_form(3*ggbtmpvarx^4+ggbtmpvarx^2) Error:
			// Bad Argument Value
			Log.debug("error from Giac: " + s);

			return "?";
		}

		String ret = s.trim();
		// output from ifactor can be wrapped to stop simplification
		// eg js giac output:-('3*5')

		int primeOpen = ret.indexOf('\'');
		while (primeOpen >= 0) {
			int primeClose = ret.indexOf('\'', primeOpen + 1);
			if (primeClose < 0) {
				break;
			}
			// ((a')') -- delete brackets
			if (primeClose == primeOpen + 2
					&& ret.charAt(primeOpen + 1) == ')') {
				int bracketOpen = ret.lastIndexOf('(', primeOpen);

				if (bracketOpen >= 0) {
					StringBuilder sb = new StringBuilder(ret);
					sb = sb.replace(primeOpen + 1, primeOpen + 2, "");
					sb = sb.replace(bracketOpen, bracketOpen + 1, "");
					ret = sb.toString();
				}
				// primeOpen = primeClose;
			} else {
				primeOpen = primeClose;
			}
		}

		// #5099 / TRAC-3566 GIAC_ERROR: string missing
		// if (ret.indexOf("Unable to solve differential equation") > 0) {
		// return "?";
		// }

		if (ret.contains("integrate(")) {
			// eg Integral[sqrt(sin(x))]
			return "?";
		}

		if (ret.contains("c_")) {
			// TODO with the current lookup constant numbers need to be globally
			// unique -- we should reset the lookup table for each computation
			// instead (e.g. revert r19766)
			nrOfReplacedConst += ret.length() * 3; // upper bound on number of
													// constants in result
			Log.debug("replacing arbitrary constants in " + ret);
			ret = ret.replaceAll("c_([0-9]*)",
					"arbconst($1+" + nrOfReplacedConst + ")");
		}

		if (ret.contains("n_")) {
			Log.debug("replacing arbitrary integers in " + ret);
			ret = ret.replaceAll("n_([0-9]*)", "arbint($1)");
		}

		// convert Giac's scientific notation from e.g. 3.24e-4 to
		// 3.2E-4
		// not needed, Giac now outputs E
		// ret = parserTools.convertScientificFloatNotation(ret);

		ret = casParser.insertSpecialChars(ret); // undo special character
													// handling

		// don't do check for long strings eg 7^99999
		if (ret.length() < 200) {

			// convert x>3 && x<7 into 3<x<7
			ret = checkInequalityInterval(ret);
		}

		return ret;
	}

	/**
	 * evalfa makes sure rootof() converted to decimal
	 * eg @rootof({{-4,10,-440,2025},{1,0,10,-200,375}})
	 * 
	 * @param s
	 *            input
	 * @return "evalfa(" + s + ")"
	 */
	protected String wrapInevalfa(String s) {
		expSB.setLength(EVALFA.length());
		expSB.append(s);
		expSB.append(")");

		return expSB.toString();
	}

	/**
	 * Test if Giac is up and running. Overridden in CASGiacW
	 * 
	 * @return true if Giac is already loaded
	 */
	@Override
	public boolean isLoaded() {
		return true;
	}

	public int getCasGiacCacheSize() {
		return casGiacCache.size();
	}

	public void clearCache() {
		casGiacCache.clear();
	}

	/**
	 * Use a constant for "fsolve" (needed for reproducible results of NSolve and NSolutions).
	 * For other commands use a random seed to make sure random results from Giac are not repeated.
	 * @param exp expression to be evaluated after
	 * @return RNG seed
	 */
	protected int getSeed(String exp) {
		return exp.contains("fsolve(") ? 9 : rand.nextInt(Integer.MAX_VALUE);
	}
}
