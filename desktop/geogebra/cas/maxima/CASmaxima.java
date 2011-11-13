package geogebra.cas.maxima;


import geogebra.cas.CASgeneric;
import geogebra.cas.CASparser;
import geogebra.kernel.arithmetic.ValidExpression;


public class CASmaxima extends CASgeneric {

	public CASmaxima(CASparser casParser, String translationResourcePath) {
		super(casParser, translationResourcePath);
		// TODO Auto-generated constructor stub
	}
/*
	private final static String RB_GGB_TO_Maxima = "/geogebra/cas/maxima/ggb2maxima";
	
	// 5.22.0 needed by #295
	// 5.23.0 needed by #314
	final public int[] MINIMAL_REQUIRED_VERSION = {5, 23};
	
	private final CasParserTools parserTools;
	
	private MaximaInteractiveProcess ggbMaxima;

	
	public CASmaxima(CASparser casParser, CasParserTools parserTools) {
		super(casParser, RB_GGB_TO_Maxima);
		this.parserTools = parserTools;
		
		Application.setCASVersionString("Maxima"); // called later on with eg "Maxima 5.22.1"
	}
	
//	/**
//	 * Returns whether var is a defined variable in Maxima.
//	 */
//	public boolean isVariableBound(String var) {
//		// check if var is assigned a value or defined as function in Maxima
//		StringBuilder sb = new StringBuilder();
//		sb.append("issymbolbound('");
//		sb.append(var);
//		sb.append(");");
//		return "true".equals(evaluateMaxima(sb.toString()).replaceAll(" ", ""));
//	}

	@Override
	protected String evaluateGeoGebraCAS(ValidExpression casInput) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String evaluateRaw(String exp) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEvaluateGeoGebraCASerror() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbindVariable(String var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String translateFunctionDeclaration(String label, String parameters, String body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Unbinds (deletes) var in Maxima.
	 * @param var
	 * @param isFunction
	 *
	public void unbindVariable(String var) {		
		StringBuilder sb = new StringBuilder();
		
		// kill variable var
		sb.append("kill(");
		sb.append(var);
		sb.append(");");

		evaluateMaxima(sb.toString());
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @param useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return evaluation result
	 * @throws Throwable
	 *
	public String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable {
		// convert parsed input to Maxima string
		String MaximaString = translateToCAS(casInput, ExpressionNode.STRING_TYPE_MAXIMA);
		
		// Maxima simplification is turned off by default using simp:false;
		// We turn it on here using ev(command, simp) when KeepInput is not used
		if (!casInput.isKeepInputUsed()) {
			StringBuilder sb = new StringBuilder();
			sb.append("ev(");
			sb.append(MaximaString);
			sb.append(",simp)");
			MaximaString = sb.toString();
		}
		
		// EVALUATE input in Maxima 
		String result = evaluateMaxima(MaximaString);
		// convert Maxima result back into GeoGebra syntax

		// TODO: remove
		System.out.println("eval with Maxima: " + MaximaString);
		System.out.println("   result: " + result);

		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	/** 
	 * Evaluates an expression in the syntax of Maxima.
	 * 
     * @return result string (null possible)
	 * @throws Throwable 
     *
	public String evaluateRaw(String exp) throws Throwable {
		return evaluateMaxima(exp);
	}
	
	final synchronized public String getEvaluateGeoGebraCASerror() {
		// TODO: implement for Maxima
		return null;
	}
	
	/**
	 * Tries to parse a given Maxima string and returns a String in GeoGebra syntax.
	 *
	public synchronized String toGeoGebraString(String maximaString) throws Throwable {
		ValidExpression ve = casParser.parseMaxima(maximaString);
		return casParser.toGeoGebraString(ve);
	}
	
	public String translateFunctionDeclaration(String label, String parameters, String body)
	{
		return label + '(' + parameters + ") := " + body;
	}
	
    /**
	 * Evaluates a Maxima expression and returns the result as a string in Maxima syntax, 
	 * e.g. evaluateMaxima("integrate (sin(x)^3, x);") returns "cos(x)^3/3-cos(x)".
	 * 
	 * @return result string (null possible)
	 *
	final synchronized public String evaluateMaxima(String exp) {
		try {
			String result;
						
			// MathPiper has problems with indices like a_3, b_{12}
			exp = casParser.replaceIndices(exp);
			
			final boolean debug = false;
			if (debug) Application.debug("Expression for Maxima: "+exp, 1);
			
			
			String res = executeRaw(exp);
			
			if (res.indexOf("Division by 0") > 0)
			{
				Application.debug("WARNING: caught division by 0");
				return "?";
			}
			
			if (res.indexOf("~M has been generated") > 0)
			{
				Application.debug("WARNING: Maxima error");
				return "?";				
			}
			
			if (res.indexOf("?merror") > 0)
			{
				Application.debug("WARNING: maxima error: " + res);
				return "?";
			}
			
			// Matrix notation
			res = res.replaceAll("matrix\\(([^)]+)\\)", "\\[$1\\]");
			while (res.indexOf('\n') > -1 ) res = res.replace('\n', ' ');
			
			String results[] = res.split("\\(%[oi]\\d+\\)\\s*");
			
			result = results[results.length - 1];
			
			// if last line is empty, look for next non-empty previous line
			if (result.trim().length() == 0 && results.length > 1) {
				int i = results.length - 2;
				while (results[i].trim().length() == 0 && i > 0) i--;
				result = results[i];
			}
			
			// remove (%o1) at start
			//result = result.replaceFirst("\\(%o\\d+\\)\\s*", "").trim();
			
			if (result.indexOf("%c") > -1) {
				result = result.replaceAll("%c","const");
				Application.debug("WARNING: replacing %c by const",1);
			}
			
			if (result.indexOf(" =") > -1) { // y = not :=
				result = result.replaceAll(" =","==");
				//Application.debug("WARNING: replacing = by ==",1);
			}
			
			if (debug) {
				for (int i = 0 ; i < results.length ; i++)
					System.err.println("Result "+i+": "+results[i]);
				System.out.println("result: "+result);
			}
				
			// undo special character handling
			result = casParser.insertSpecialChars(result);
			
			// convert Maxima's bfloat notation from e.g. 3.24b-4 to 3.2E-4
			result = parserTools.convertScientificFloatNotation(result);
			
			// replace eg [x=0,x=1] with {x=0,x=1}
			while (result.indexOf('[') > -1) result = result.replace('[','{');
			while (result.indexOf(']') > -1) result = result.replace(']','}');
			
			return result;
		} catch (MaximaTimeoutException e) {
			Application.debug("Timeout from Maxima, resetting");
			ggbMaxima = null;
			return "?";
		}
	}
	
	/**
	 * Initializes Maxima.
	 *
	synchronized public void initialize()
	{
		if (ggbMaxima != null) // this should never happen :)
			throw new IllegalStateException();
		
		MaximaConfiguration configuration = casParser.getKernel().getApplication().maximaConfiguration;
		
		if (configuration == null) configuration = JacomaxAutoConfigurator.guessMaximaConfiguration();
		
		MaximaProcessLauncher launcher = new MaximaProcessLauncher(configuration);
		ggbMaxima = launcher.launchInteractiveProcess();
		try {
			initMyMaximaFunctions();
//			System.out.println(ggbMaxima.executeCall("1+2;"));
			
			int[] version = determineMaximaVersion();
			StringBuilder v = new StringBuilder("Maxima ");
			for (int i = 0; i < version.length; ++i)
			{
				v.append(version[i]);
				v.append('.');
			}
			Application.setCASVersionString(v.toString());
			
			if (version[0] < MINIMAL_REQUIRED_VERSION[0] ||
					(version[0] == MINIMAL_REQUIRED_VERSION[0] && version[1] < MINIMAL_REQUIRED_VERSION[1]))
					throw new MaximaVersionUnsupportedExecption(version);

		} catch (MaximaTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	
	private synchronized MaximaInteractiveProcess getMaxima() {
		if (ggbMaxima == null) // this should never happen :)
			throw new IllegalStateException("CASmaxima was not initialized");
		return ggbMaxima;
	}
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 *
	public synchronized void reset() {

		try {
			ggbMaxima.executeCall("kill(all);");
			initMyMaximaFunctions();
		} catch (MaximaTimeoutException e) {
			e.printStackTrace();
			
			// completely re-initialize!
			ggbMaxima = null;
			getMaxima();
		}

	}
	
	private void initMyMaximaFunctions() throws MaximaTimeoutException, geogebra.cas.maxima.jacomax.MaximaTimeoutException {
	
		// turn auto-simplification off, so a+a gives a+a
		// with this setting ev( a+a, simp ) is needed to get 2*a
	    ggbMaxima.executeCall("simp:false;");
	    
	    // Variable Ordering. See Tickets #311 and #281 in trac
	    ggbMaxima.executeCall("powerdisp:false;");
	    ggbMaxima.executeCall("orderless(z,y,x,w,v,u,t,s,r,q,p,o,n,m,l,k,j,i,h,g,f,e,d,c,b,a);");
	    
	    // change binding power of multiplication in Maxima 
	    // In Maxima a*b/c is read as (a*b)/c by default but GeoGebra reads this as a*(b/c)
	    // see #631 and http://maxima.sourceforge.net/docs/manual/en/maxima_6.html
	    ggbMaxima.executeCall("infix(\"*\", 110, 110);");
	    
		// set line length of "terminal"
		// we don't want lines broken
	    ggbMaxima.executeCall("linel:10000;");
	    
		// make sure results are returned
	    ggbMaxima.executeCall("display2d:false;");
	    
	    // make sure integral(1/x) = log(abs(x))
	    ggbMaxima.executeCall("logabs:true;");
	    
	    // make sure algsys (solve) doesn't return complex roots
	    ggbMaxima.executeCall("realonly:true;");
	    
	    // eg x^-1 displayed as 1/x
	    ggbMaxima.executeCall("exptdispflag:true;");
	    
	    // suppresses the printout of the message informing the user of the conversion of floating point numbers to rational numbers
	    ggbMaxima.executeCall("ratprint:false;");
	    
	    // When true, r some rational number, and x some expression, %e^(r*log(x)) will be simplified into x^r . It should be noted that the radcan command also does this transformation, and more complicated transformations of this ilk as well. The logcontract command "contracts" expressions containing log. 
	    ggbMaxima.executeCall("%e_to_numlog:true;");
	    
	    // Define e as the euler constant
	    ggbMaxima.executeCall("e:%e;");
	    
	    // define custom functions
	    ggbMaxima.executeCall("log_a(x,a) := block([oldLE, ret], " +
	    		"oldLE:logexpand, logexpand:super, " +
	    		"ret: (log((a)^dummy*x)/log(a))-dummy, " +
	    		"logexpand:oldLE, logcontract(ret));");
	    //ggbMaxima.executeCall("log10(x) := log(x) / log(10);");
	    //ggbMaxima.executeCall("log2(x) := log(x) / log(2);");
	    ggbMaxima.executeCall("log10(x) := log_a(x,10);");
	    ggbMaxima.executeCall("log2(x) := log_a(x,2);");
	    ggbMaxima.executeCall("logB(b, x) := log(x) / log(b);");
	    ggbMaxima.executeCall("cbrt(x) := x^(1/3);");
	    
	    //necessary because parser doesn't like _
	    ggbMaxima.executeCall("prevprime(x):=prev_prime(x);");
	    ggbMaxima.executeCall("nextprime(x):=next_prime(x);");
	    
	    // needed to define lcm()
	    ggbMaxima.executeCall("load(functs)$");
	    
	    // needed for eg pdf_exp (Exponential.2)
	    // TODO: doesn't seem to work
	    ggbMaxima.executeCall("load(distrib)$");
	    
	    // needed for degree()
	    ggbMaxima.executeCall("load(powers)$");
	    
	    // needed for mean, variance, ...
	    ggbMaxima.executeCall("load(descriptive)$");
	    
	    //needed for random_distrib
	    ggbMaxima.executeCall("load(distrib)$");
	    
	    // needed for ???
	    ggbMaxima.executeCall("load(format)$");
	    
	    // needed for unitvector (and possible also other algebra functions?)
	    ggbMaxima.executeCall("load(eigen)$");
	       
	    // turn {x=3} into {3} etc
	    ggbMaxima.executeCall("stripequals(ex):=block(" +
	    		 "if atom(ex) then return(ex)" +
	    		 "else if op(ex)=\"=\" then return(stripequals(rhs(ex)))" +
	    		 "else apply(op(ex),map(stripequals,args(ex)))" +
	    		")$");
	    // gets all solutions in x of eq
	    ggbMaxima.executeCall("csolve(eq,x):=block(" +
	    		"[s, realonlytemp:realonly]," +
	    		" realonly:false, s:solve(eq,x)," +
	    		" realonly:realonlytemp," +
	    		" for i : 1 thru length (%rnum\\_list) do " +
	    		"s : subst (simplode([t, i]), %rnum\\_list[i], s)," +
	    		" return (if (length(s) = 1) then flatten(s) else (s)))$");
	    /* This function takes an expression ex and returns a list of coefficients of v *
	    ggbMaxima.executeCall("coefflist(ex,v):= block([deg,kloop,cl]," +
	    		"cl:[]," +
	      "ex:ev(expand(ex),simp)," +
	      "deg:degree(ex,v)," +
	      "ev(for kloop:0 thru deg do\n" +
	      "cl:append(cl,[coeff(ex,v,kloop)]),simp)," +
	      "cl" +
	      ")$");
	    
	    /*
	     * Tests if a given symbol is a function-symbol (function name)
	     * Implemented as LISP-function as you cannot do this with maxima-functions
	     *
	    ggbMaxima.executeCall(":lisp (defun $myfun (sym) " +
	    		"(cond ((fboundp sym)) ((get sym 'mprops) t) (t nil)))");
	    
	    /*
	     * Takes a symbol and tests if a it is already bound to something.
	     * ( = bound to a function-name or a variable-name).
	     *  Note: weird formal parameter name as due to maxima's dynamic scoping rules
	     *        using a "normal" formal parameter just doesn't work.
	     *        (just try changing "ggbnsrphdbgse5tfd" and then call "issymbolbound('x)")
	     *
	    ggbMaxima.executeCall("issymbolbound(ggbnsrphdbgse5tfd):= " +
	    		"myfun(ggbnsrphdbgse5tfd) " +
	    		"or ?boundp(ggbnsrphdbgse5tfd);");
	   
	    /*
	     * eg integrate(x^n,x) asks if n+1 is zero
	     * this disables the interactivity
	     * but we get:
	     * if equal(n+1,0) then log(abs(x)) else x^(n+1)/(n+1)
	     * TODO: change to ggb syntax
	     *
	    ggbMaxima.executeCall("load(\"noninteractive\");");
	    
	    // define Degree
	    ggbMaxima.executeCall("Degree : %pi/180;");
	    
	    // access functions for elements of a vector (for the "if" part, see #556)
	    ggbMaxima.executeCall("x(a) := if listp(a) then part(a, 1) else x*a;");
	    ggbMaxima.executeCall("y(a) := if listp(a) then part(a, 2) else y*a;");
	    ggbMaxima.executeCall("z(a) := if listp(a) then part(a, 3) else z*a;");
	    
	    //the rref function implemented by Antoine Chambert-Loir
	    ggbMaxima.executeCall("request_rational_matrix(m, pos, fn) :=  " +
	    		"if every('identity, map(lambda([s], every('ratnump,s)), " +
	    		"args(m))) then true else " +
	    		"print(\"Some entries in the matrix are not rational numbers. " +
	    		"The result might be wrong.\")$");
	    ggbMaxima.executeCall("rowswap(m,i,j) := block([n, p, r]," +
	    		"  require_matrix(m, \"first\", \"rowswap\")," +
	    		"  require_integer(i, \"second\", \"rowswap\")," +
	    		"  require_integer(j, \"third\", \"rowswap\")," +
	    		"  n : length(m)," +
	    		"  if (i < 1) or (i > n) or (j < 1) or (j > n)" +
	    		"     then error(\"Array index out of bounds\")," +
	    		"  p : copymatrix(m)," +
	    		"  r : p[i]," +
	    		"  p[i] : p[j]," +
	    		"  p[j] : r," +
	    		"  p)$");
	    ggbMaxima.executeCall("addrow(m,i,j,k) := block([n,p]," +
	    		"  require_matrix(m, \"first\", \"addrow\")," +
	    		"  require_integer(i, \"second\", \"addrow\")," +
	    		"  require_integer(j, \"third\", \"addrow\")," +
	    		"  require_rational(k, \"fourth\", \"addrow\")," +
	    		"  n : length(m)," +
	    		"  if (i < 1) or (i > n) or (j < 1) or (j > n) " +
	    		"      then error(\"Array index out of bounds\")," +
	    		"  p : copymatrix(m)," +
	    		"  p [i] : p[i] + k * p[j]," +
	    		"  p)$");
	    ggbMaxima.executeCall("rowmul(m,i,k) := block([n,p]," +
	    		"  require_matrix(m, \"first\", \"addrow\")," +
	    		"  require_integer(i, \"second\", \"addrow\")," +
	    		"  require_rational(k, \"fourth\", \"addrow\")," +
	    		"  n : length(m)," +
	    		"  if (i < 1) or (i > n) then error(\"Array index out of bounds\")," +
	    		"  p : copymatrix(m)," +
	    		"  p [i] : k * p[i]," +
	    		"  p)$");
	    ggbMaxima.executeCall("rref(m):= block([p,nr,nc,i,j,k,pivot,pivot_row]," +
	    		"  request_rational_matrix(m,\" \",\"rref\")," +
	    		"  nc: length(first(m))," +
	    		"  nr: length(m)," +
	    		"  if nc = 0 or nr = 0 then" +
	    		"    error (\"The argument to 'rref' must be a matrix with one or more rows and columns\")," +
	    		"  p:copymatrix(m)," +
	    		"  ci : 1, cj : 1," +
	    		"  while (ci<=nr) and (cj<=nc) do " +
	    		"  (" +
	    		"    pivot_row : 0, pivot : 0," +
	    		"    for k : ci thru nr do (" +
	    		"       if ( abs(p[k,cj]) > pivot ) then (" +
	    		"         pivot_row : k," +
	    		"         pivot : abs(p[k,cj])))," +
	    		"    if (pivot = 0) then (cj : cj +1)" +
	    		"    else (" +
	    		"      p : rowswap(p,ci,pivot_row)," +
	    		"      p : rowmul(p,ci,1/p[ci,cj]),        " +
	    		"      for k : 1 thru nr do (" +
	    		"         if not (k=ci) then (p : addrow (p,k,ci,-p[k,cj])))," +
	    		"      ci : ci+1, cj : cj+1))," +
	    		"  p)$");
	    
	    // Function providing lexical scoping for variables. See Ticket #496
	    ggbMaxima.executeCall(":lisp (defmspec $ggblexicalblock (x)" +
	    		 "(let*" +
	    		   "((args (cdr x))" +
	    		    "(vars (cdr (car args)))" +
	    		    "(exprs (cdr args))" +
	    		    "(gensym-vars (mapcar #'(lambda (s) (let ((s1 (gensym))) (setf (get" +
	    		    "		s1 'reversealias) (or (get s 'reversealias) s)) s1)) vars))" +
	    		    "(subst-eqns (mapcar #'(lambda (x y) `((mequal) ,x ,y)) vars gensym-vars))" +
	    		    "(gensym-mprogn ($psubstitute `((mlist) ,@subst-eqns) `((mprogn) ,@exprs))))" +
	    		   "(meval gensym-mprogn)))");
	    
	    // for intfudu
	    ggbMaxima.executeCall("load(\"partition\");");
	    
	    // so that intfdu uses logabs
	    // http://permalink.gmane.org/gmane.comp.mathematics.maxima.general/34149
	    ggbMaxima.executeCall("intable[\"^\"] : lambda([u,v],if freeof(%voi,u) then [u^v/log(u),diff(v,%voi)] else if freeof(%voi,v) then if v#-1 then  [u^(1+v)/(1+v),diff(u,%voi)] else [log(if logabs then  abs(u) else u),diff(u,%voi)] );");

	}

	private String executeRaw(String maximaInput) throws MaximaTimeoutException, geogebra.cas.maxima.jacomax.MaximaTimeoutException {
        char lastChar = maximaInput.charAt(maximaInput.length() - 1);
        if (lastChar != ';' && lastChar != '$' && !maximaInput.startsWith(":lisp")) {
        	maximaInput += ";";
        }    
       
        String result = getMaxima().executeCall(maximaInput);

		return result;

	}
	
	/**
	 * Determines the version of the underlying maxima version.
	 * @return An array of 3 elements, containing, major, minor and build version number, respectively.
	 * @throws MaximaTimeoutException
	 *
	private int[] determineMaximaVersion() throws MaximaTimeoutException
	{
		String buildinfo = ggbMaxima.executeCall("build_info();");
		
		int[] retval = new int[3];
		Pattern p = Pattern.compile("Maxima version: (\\d+).(\\d+).(\\d+)");
		Matcher m = p.matcher(buildinfo);
		if (!m.find())
			retval[0] = retval[1] = retval[2] = -1;
		else
		{
			retval[0] = Integer.parseInt(m.group(1));
			retval[1] = Integer.parseInt(m.group(2));
			retval[2] = Integer.parseInt(m.group(3));
		}
		return retval;	
	}
	*/
}
