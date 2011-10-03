package geogebra.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

/**
 * AlgoFit
 * A general linear curvefit:
 * 		Fit[<List of Points>,<List of Functions>]
 * Example:
 *     f(x)=1, g(x)=x, h(x)=e^x
 *     L={A,B,...}
 *     c(x)=Fit[L,{f,g,h}]
 *     will give a least square curvefit:
 *     c(x)= a+b*x+c*e^x
 *     
 * Simple test procedure:
 * 		Make points A,B, ...
 * 		L={A,B,...}
 * 		f(x)=1, g(x)=x, h(x)=x^2,... =x^n
 * 		F={f,g,h,...}
 * 		right(x)=Regpoly[L,n]
 * 		fit(x)=Fit[L,F]
 * 
 * The solution is the usual: M_t*M*X=M_t*Y, the solution of overdetermined linear equation systems..
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-23
 */
public class AlgoFit extends AlgoElement {
	
	private static final boolean	DEBUG	=	true;		//false in distribution

	private static final long serialVersionUID = 1L;
	private GeoList pointlist; 			// input
	private GeoList functionlist;  		// output
	private GeoFunction fitfunction; 	// output
	
	//variables:
	private	int 			datasize		=	0;				//rows in M and Y
	private	int				functionsize	=	0;				//cols in M
	private	GeoFunction[]	functionarray	=	null;
	private	RealMatrix		M				=	null;
	private	RealMatrix		Y				=	null;
	private	RealMatrix		P				=	null;

	public AlgoFit(Construction cons, String label, GeoList pointlist,GeoList functionlist) {
		super(cons);

		this.pointlist = pointlist;
		this.functionlist=functionlist;
		fitfunction = new GeoFunction(cons);
		setInputOutput();
		compute();
		fitfunction.setLabel(label);
	}// Constructor

	public String getClassName() {
		return "AlgoFit";
	}

	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = pointlist;
		input[1]=  functionlist;
		output = new GeoElement[1];
		output[0] = fitfunction;
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFit() {
		return fitfunction;
	}

	protected final void compute() {
		GeoElement 	geo1	=	null;
		GeoElement  geo2	=	null;
		datasize		=	pointlist.size();				//rows in M and Y
		functionsize	=	functionlist.size();			//cols in M
		functionarray	=	new GeoFunction[functionsize];
		M				=	new Array2DRowRealMatrix(datasize,functionsize);
		Y				=	new Array2DRowRealMatrix(datasize,1);
		P				=	new Array2DRowRealMatrix(functionsize,1);  //Solution parameters

		
		if (!pointlist.isDefined() 		||	//Lot of things can go wrong...	
			!functionlist.isDefined() 	||
			(functionsize>datasize)   	||
			(functionsize<1)			||
			(datasize<1)					//Perhaps a max restriction of functions and data?
			)								//Even if noone would try 500 datapoints and 100 functions...
		{
				fitfunction.setUndefined();
				return;
		} else {							//We are in business...
			//Best to also check:
			geo1=functionlist.get(0);
			geo2=pointlist.get(0);
			if(!geo1.isGeoFunction() || !geo2.isGeoPoint()){
				fitfunction.setUndefined();
				return;
			}//if wrong contents in lists
			try{
				makeMatrixes();					//Get functions, x and y from lists
				
				//Solve for parametermatrix P:
				DecompositionSolver solver=new QRDecompositionImpl(M).getSolver();
				P=solver.solve(Y);
				
				//mprint("P:",P);					//debug, erase later
						
				fitfunction=makeFunction();
				//walk(fitfunction.getFunctionExpression());  //debug, erase later
				//System.out.println(fitfunction.getFunctionExpression().toString());
				
				//First solution (Borcherds):
				//fitfunction.set(kernel.getAlgebraProcessor().evaluateToFunction(buildFunction()));
				//fitfunction.setDefined(true);		
				
			}catch(Throwable t){
				fitfunction.setUndefined();
				errorMsg(t.getMessage());
				if(DEBUG){t.printStackTrace();}
			}//try-catch
		}//if	

	}//compute()
	
	//Get info from lists into matrixes and functionarray
	private final  void makeMatrixes() throws Exception{
		GeoElement	geo=null;
		GeoPoint	point=null;
		double		x,y;
		
		//Make array of functions:
		for(int i=0;i<functionsize;i++){
			geo=functionlist.get(i);
			if(!geo.isGeoFunction()){
				throw(new Exception("Not functions in function list..."));
			}//if not function
			functionarray[i]=(GeoFunction) functionlist.get(i);
		}//for all functions
		//Make matrixes with the right values: M*P=Y
		M=new Array2DRowRealMatrix(datasize,functionsize);
		Y=new Array2DRowRealMatrix(datasize,1);
		for(int r=0;r<datasize;r++){
			geo=pointlist.get(r);
			if(!geo.isGeoPoint()){
				throw(new Exception("Not points in function list..."));
			}//if not point
			point=(GeoPoint)geo;
			x=point.getX();
			y=point.getY();
			Y.setEntry(r,0,y);
			for(int c=0;c<functionsize;c++){
				M.setEntry(r,c,functionarray[c].evaluate(x));
			}//for columns (=functions)			
		}//for rows (=datapoints)
		//mprint("M:",M);
		//mprint(Y:",Y);
	}//makeMatrixes()
	
	//First solution (Borcherds)
	private final String buildFunction() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<functionsize;i++){
			sb.append(P.getEntry(i,0));
			sb.append('*');
			sb.append(((GeoFunction)functionlist.get(i)).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, true));
			if (i != functionsize - 1) {
				sb.append('+');
			}
		}
		return sb.toString();
	}
	
	// Making GeoFunction fit(x)= p1*f(x)+p2*g(x)+p3*h(x)+...
	private final GeoFunction makeFunction(){
		double p;
		GeoFunction gf=null;
		GeoFunction product=new GeoFunction(cons);
		
		//First product:
		p=P.getEntry(0,0);		//parameter
		gf=(GeoFunction)functionlist.get(0);		//Checks done in makeMatrixes...
		fitfunction=GeoFunction.mult(fitfunction,p,gf);	//p1*f(x)
		for(int i=1;i<functionsize;i++){
			p=P.getEntry(i,0);
			gf=(GeoFunction)functionlist.get(i);
			product=GeoFunction.mult(product,p,gf);		//product= p*func
			fitfunction=GeoFunction.add(fitfunction,fitfunction,product);	//fit(x)=...+p*func
		}//for
		
		return fitfunction;
	}//makeFunction()
	
	
	/// --- Debug --- ///
    private final static void errorMsg(String s){
    	geogebra.main.Application.debug(s);
    }//errorMsg(String)   
    
  // --- SNIP --- /// *** Comment out when finished ***
 	
    // Hook for plugin scripts
    public final void test(){
    	
    }//test()
    
    public void mprint(String s,RealMatrix m){
    	System.out.println(s);
    	int rows=m.getRowDimension();
    	int cols=m.getColumnDimension();
    	for(int r=0;r<rows;r++){
    		for(int c=0;c<cols;c++){
    			System.out.print(m.getEntry(r,c)+"  ");
    		}//for c
    		System.out.println();
    	}//for r
    }//mprint()
    
    //Walk  the node tree and print some info
    public void walk(ExpressionValue ev){
    	ExpressionNode n=null;
    	if(ev==null){
    		return;
    	}else{
    		if(ev.isExpressionNode()){
    			n=(ExpressionNode)ev;
    			walk(n.left);    			walk(n.right);
    			System.out.println("  Op: "+getOpString(n.getOperation()));
    			System.out.println();
    		}else if(ev.isGeoElement()){
    			GeoElement geo=(GeoElement)ev;
    			System.out.print("   geo.label "+geo.toString());
    		}else if(ev.isVariable()){
    			System.out.print("   var: ");
    		}else if(ev.isNumberValue()){
    			NumberValue nv=(NumberValue)ev;
    			System.out.print("   number: "+nv.getDouble());
    		}else if(ev.isConstant()){
    			NumberValue nv=(NumberValue)ev;
    			System.out.print("   const: "+nv.getDouble());
    		
    		}else{
    			System.out.print("   type??");
    			
    		}//if right type
    	}//if
    }//walk node tree
    
    private String getOpString(int i){
    	switch (i){
    	case -2147483648: return("noop");
    	case 0:return("add");
    	case 1: return("minus");
    	case 2: return("mullt");
    	case 3: return("div");
    	case 4: return("pow");
    	case 43:return("Func");
    		default:  break;
    	}//switch
    	return ""+i;
    }//getOpString

 
 
  // --- SNIP --- /// 

}// class AlgoFit
