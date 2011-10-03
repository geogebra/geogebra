/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.optimization.RealRootFunctionVariable;
import geogebra.main.Application;

/**
 * AlgoOptimize: Abstract class for AlgoMaximize and AlgoMinimize
 * Command Minimize[ <dependent variable>, <independent variable> ] (and Minimize[] )
 * which searches for the independent variable which gives the 
 * smallest/largest result for the dependent variable.
 * 
 *  Packages the relationship as a RealRootFunction for the ExtremumFinder.
 *  
 * @author 	Hans-Petter Ulven
 * @version 20.02.2011
 * 
 * ToDo: 
 * -Bug: Intermediate steps in searching produces traces in Graphic view
 * -Find a better way to avoid all the recursive calls, even if they are not executed all the way
 * 
 */

public abstract class AlgoOptimize extends AlgoElement{

	private static final long serialVersionUID = 1L;
	public  static final int  MINIMIZE = 0;
	public  static final int  MAXIMIZE = 1;
	
	private     	Construction				cons		=	null;
	private 	 	ExtremumFinder				extrFinder	=	null;		//Uses ExtremumFinder for the dirty work
	private 	   	RealRootFunctionVariable	i_am_not_a_real_function=null;	
	private			GeoElement					dep			=	null;
	private			GeoNumeric					indep		=	null;
	private			GeoNumeric					result		=	null;
	private			int							type		=	MINIMIZE;
	private 		boolean						isrunning	=	false;		//To stop recursive calls. Both Maximize and Minimize.
	
	/** Constructor for Maximize*/
	public AlgoOptimize(Construction cons,String label,GeoElement dep,GeoNumeric indep,int type){
		super(cons);
		this.cons=cons;
		this.dep=dep;
		this.indep=indep;
		this.type=type;
    	extrFinder=new ExtremumFinder();
    	i_am_not_a_real_function=new RealRootFunctionVariable(dep,indep);		
		result=new GeoNumeric(cons);    	
        setInputOutput();
        compute();		
        result.setLabel(label);
	}//Constructor for Maximize
	
	
    /** Implementing AlgoElement */
	protected void setInputOutput(){
        /*input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = max;
        */
		input=new GeoElement[2];
		input[0]=dep;
		input[1]=indep;

		output=new GeoNumeric[1];
		output[0]=result;
		
        setDependencies(); // done by AlgoElement
    }//setInputOutput()
	
	/** Implementing AlgoElement */
    protected final void compute() {
    	double old=0.0d,res=0.0;;

    	if(isrunning){return;}   		//do nothing return as fast as possible		

   		old=indep.getValue();
   		isrunning=true;
   		if(indep.getIntervalMaxObject()==null||
   				indep.getIntervalMinObject()==null){
   			result.setUndefined();
   			return;
   		}
   		if(type==MINIMIZE){
   			res=extrFinder.findMinimum(indep.getIntervalMin(),indep.getIntervalMax(),
    									i_am_not_a_real_function,5.0E-8);	//debug("Minimize ("+counter+") found "+res);
   		}else{
   			res=extrFinder.findMaximum(indep.getIntervalMin(),indep.getIntervalMax(),
   										i_am_not_a_real_function,5.0E-8);	//debug("Maximize ("+counter+") found "+res);
   		}
   		result.setValue(res);
   		indep.setValue(old);

   		//indep.updateCascade();
   		cons.updateConstruction();  
   		isrunning=false;
   		return;

    }//compute()
    
    GeoElement getResult(){
    	return result;
    
    }//getMinimized()
    
    public abstract String getClassName();
    


    
	
	private final static boolean	DEBUG	=	true;			//debug or errormsg
	
    private final static void debug(String s) {
        if(DEBUG) {
            System.out.println(s);
        }else{
        	Application.debug(s);
        }//if debug or errormsg
    }//debug()
   	


}//abstract class AlgoOptimize
