package geogebra.common.kernel.optimization;
/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

/**
<h3>FitRealFunction</h3>
<pre>
  Class with FitRealFunction which will be used when Fit[<list>,<function>] does
  nonlinear curve-fitting on a copy of <function> where gliders a,b,c,...
  are used as parameters.
  
  Implements:

        org.apache.commons.math.optimization.fitting.ParametricRealFunction
        which can be given to org.apache....fitting.CurveFitter which
        does the rest of the job.
  
  Interface:
  
  	FitRealFunction(Function)				Makes a copy of Function with gliders replaced by mydouble parameters
  	value(double,double[])					Evaluates for x and pars[]
  	gradient(double,double[])				Evaluates a gradient for x and pars[] numerically
  
  For AlgoFitNL:
  
  	getNumberOfParameters()					Get number of gliders/parameters found and changed
  	getStartParameters()					Get array of startvalues for parameters.
    getGeoFunction(double[])				Get FitFunction as GeoFunction with parameters replaced
  
  For later extensions and external use:
  
  	evaluate(double,double[])				As value(...), perhaps implementing other interfaces later?
  	evaluate(double)						As an ordinary function
  	evaluate()								Last value
  	getFunction(double[])					Get as Function with parameters replaced
  	
  	ToDo:		The gradient could be more sophisticated, but the Apache lib is quite robust :-)
  				Some tuning of numerical precision both here and in the setup of LM-optimizer
  				
  				Should probably make an abstract, and make this a subclass,
  				will do if the need arises.
	
</pre>  

  @author  Hans-Petter Ulven
  @version 15.03.2011
 */
public class FitRealFunction implements org.apache.commons.math.optimization.fitting.ParametricRealFunction {
	
	/// --- Properties --- ///
	private		Kernel			kernel				=	null;
	private 	int				numberOfParameters	=	0;
	private		Object[]		gliders				=	null;			//Pointers to gliders, need for new startvalues
	private		Function		newf				=	null;
	private		double			lastvalue			=	0.0d;
	private		MyDouble[]		mydoubles			=	null;
	
	
	
	/// --- Interface --- ///
	
	/** Probably not needed? */
	public FitRealFunction() {		
	}//Constructor
	
	/** Main constructor
	 * @param f	Function to be copied and manipulated
	 */
	public FitRealFunction(Function f)throws Exception{
		super();
		setFunction(f);
	}//Constructor 
	
	/** Implementing org.apache...fitting.ParametricRealFunction
	 *  @param x	double		variable
	 *  @param pars	double[]	parameters
	 *  @return functionvalue
	 */
	public final double value(double x, double[] pars) {
		for(int i=0;i<numberOfParameters;i++) {
			mydoubles[i].set(pars[i]);
			//mydoubles[i].setLabel("p_{"+i+"}");
		}//for all parameter
		lastvalue=newf.evaluate(x);					
		return lastvalue;
	}//evaluate(x,pars[])
	
	/** Implementing org.apache...fitting.ParametricRealFunction
	 *  @param x	double		variable
	 *  @param pars double[]	parameters
	 */
	public final double[] gradient(double x,double[] pars) {
		double oldf,newf;
		double	deltap	=1.0E-5;// 1E-10 and 1E-15 is far too small, keep E-5 until search algo is made
		double[] gradient=new double[numberOfParameters];
		for(int i=0;i<numberOfParameters;i++) {
			oldf=value(x,pars);
			pars[i]+=deltap;
			newf=value(x,pars);
			gradient[i]=(newf-oldf)/deltap;
			pars[i]-=deltap;
		}//for all parameters		
		return gradient;
	}//gradient(x,pars)
	
	public void setFunction(Function f) throws Exception{
		kernel= f.getKernel();
		FunctionVariable fvar=f.getFunctionVariable();

		java.util.HashSet<GeoElement> hash= f.getVariables();		//Get a,b,c,... to array
		if(hash==null){	
			throw(new Exception("No gliders/parameters in fit-function..."));	
		}else{	
			gliders=hash.toArray();		
		}//if no gliders
		
		numberOfParameters=gliders.length;
		
		mydoubles=new MyDouble[numberOfParameters];				//Make my own parameters
		double temp;
		for(int i=0;i<numberOfParameters;i++){
			temp=((NumberValue)gliders[i]).getDouble();
			mydoubles[i]=new MyDouble(kernel);
			mydoubles[i].set(temp);								//Set mydoubles to start values from a,b,c
		}//for all parameters
		
		ExpressionNode node=f.getExpression();
		
		ExpressionNode enf=(ExpressionNode) node.deepCopy(kernel);	//Make new tree for new function
		//ExpressionNode  enf=new ExpressionNode(kernel,evf);		//System.out.println("enf(f�r replace): "+enf.toString());
		
		for(int i=0;i<numberOfParameters;i++){
			enf=enf.replace((ExpressionValue)gliders[i], mydoubles[i].evaluate(StringTemplate.defaultTemplate)).wrap(); 
																//System.out.println("Replaced: "+((NumberValue)pars[i]).toString()+"with: "+mydoubles[i].toString());
		}//for all parameters
																//System.out.println("enf(etter replace): "+enf.toString());
		enf.resolveVariables(false);
		// should we dispose this??? if(this.newf!=null) 
		this.newf=new Function(enf,fvar);						//System.out.println("new function: "+newf.toString());
	
	}//setFunction(Function)
	
	public final int getNumberOfParameters() {return numberOfParameters; }	//Needed by AlgoFitNL
	
	public final double[] getStartValues(){
		double[] startvalues=new double[numberOfParameters];
		for(int i=0;i<numberOfParameters;i++) {
			startvalues[i]=((NumberValue)gliders[i]).getDouble();												//Only first time: mydoubles[i].getDouble();
		}//for all parameters
		return startvalues;
	}//getStartValues()

	/** For other uses later? */
	public final double evaluate(double x,double[] pars){ return value(x,pars); }
	
	public final double evaluate(){return lastvalue;}
	
	public final double evaluate(double x){
		return newf.evaluate(x);
	}//evaluate(x);
	
	public final Function getFunction(){return newf;}

	
  // --- SNIP --- /// *** Comment out when finished ***
 	
    // Hook for plugin scripts
	public FitRealFunction(Kernel k,String fname) throws Exception{
		GeoElement geo=k.lookupLabel(fname);
		if(geo.isGeoFunction()){
			setFunction(((GeoFunction)geo).getFunction());
		}else{
			System.err.println(""+geo.toString(StringTemplate.defaultTemplate)+" is not a GeoFunction");
		}//if GeoFunction
	}//Test constructor
	
	/* Obs, makes a new one, don't use in algo!!! */ 
	public final GeoFunction getGeoFunction(){
		GeoFunction geof=new GeoFunction(kernel.getConstruction(),"ny",newf);
		return geof;
	}//getGeoFunction()
	


 
  // --- SNIP --- /// 	
	

}//Class FitRealFunction