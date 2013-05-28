package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.App;
import geogebra.common.main.MyError;


/**
 *	Contour lines of a given function
 */
public class AlgoContourPlot extends AlgoElement {

	private GeoFunctionNVar func; // input expression
	private double xmin, xmax, ymin, ymax; // the definition domain where the contour plot is defined
	private GeoElement contourStep;
	private GeoList list; // output
	private double visibleArea, epsilon;
	private Equation equ;
	private Polynomial poly;
	private ExpressionNode en;
	private GeoImplicitPoly implicitPoly;
	private double min, max, step, xstep, ystep;
	private int divisionPoints;
	private double calcmin, calcmax, calcxmin, calcxmax, calcymin, calcymax, minadded, maxadded;
	private boolean fixed;
	private static final int minContours = 7;
	private static final int maxContours = 25;
	
	/**
	 * Creates a new algorithm to create a list of implicit functions that form the 
	 * 		contour plot of a function
	 * 
	 * @param c
	 *			construction
	 * @param label
	 * 			label
	 * @param func
	 * 			function
	 * @param xmin
	 * 			lower bound of x
	 * @param xmax
	 * 			upper bound of x
	 * @param ymin
	 * 			lower bound of y
	 * @param ymax
	 * 			upper bound of y
	 */
	public AlgoContourPlot(Construction c, String label, GeoFunctionNVar func,
			double xmin, double xmax, double ymin, double ymax) {
		super(c);
		c.registerEuclidianViewCE(this);
		step = 0;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.func = func;
		this.visibleArea = Math.abs(xmax-xmin)*Math.abs(ymax-ymin);
		this.epsilon = visibleArea*1.0E-15;
		this.divisionPoints = 5;
		this.fixed = false;
		list = new GeoList(cons);		
		setInputOutput();
		compute();
		list.setLabel(label);
	}
	
	
	/**
	 * Creates a new algorithm to create a list of implicit functions that form the 
	 * 		contour plot of a function
	 * 
	 * @param c
	 *			construction
	 * @param label
	 * 			label
	 * @param func
	 * 			function
	 * @param xmin
	 * 			lower bound of x
	 * @param xmax
	 * 			upper bound of x
	 * @param ymin
	 * 			lower bound of y
	 * @param ymax
	 * 			upper bound of y
	 * @param contourStep the value of the contour line height multiplier
	 */
	public AlgoContourPlot(Construction c, String label, GeoFunctionNVar func,
			double xmin, double xmax, double ymin, double ymax, double contourStep){
		super(c);
		c.registerEuclidianViewCE(this);
		step = contourStep;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.func = func;
		this.visibleArea = Math.abs(xmax-xmin)*Math.abs(ymax-ymin);
		this.epsilon = visibleArea*1.0E-15;
		this.divisionPoints = 5;
		this.fixed = true;
		list = new GeoList(cons);		
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		list.setTypeStringForXML("ImplicitPoly");
		contourStep = new MyDouble(kernel, step).toGeoElement();
		if (this.fixed){
			input = new GeoElement[2];			
			input[1] = contourStep;
		}else{
			input = new GeoElement[1];			
		}
		input[0] = func;		
		setOutputLength(1);
		setOutput(0, list);
		setDependencies(); // done by AlgoElement
	}
	
	private void addToList(GeoList list, double value){
		equ=new Equation(kernel,en,new MyDouble(kernel,value));	
		equ.initEquation();
		poly =  equ.getNormalForm();
		implicitPoly.setCoeff(poly.getCoeff());
		list.add(new GeoImplicitPoly(implicitPoly));
	}
	
	private double checkPolyValue(int i, int j){
		double x = xmin+xstep*i;
		double y = ymin+ystep*j;
		return implicitPoly.evalPolyAt(x, y);		
	}
	
	private int calculateBoundary(int order){
		double val;
		int newContours = 0;
		for (int i=order-1;i<divisionPoints+order-1;i++){
			val = checkPolyValue(i, -order);
			if (val<min){
				calcmin = val;
			}
			if (val>max){
				calcmax = val;
			}
			val = checkPolyValue(i, divisionPoints+order-1);
			if (val<min){
				calcmin = val;
			}
			if (val>max){
				calcmax = val;
			}
			val = checkPolyValue(-order, i);
			if (val<min){
				calcmin = val;
			}
			if (val>max){
				calcmax = val;
			}
			val = checkPolyValue(divisionPoints+order-1, i);
			if (val<min){
				calcmin = val;
			}
			if (val>max){
				calcmax = val;
			}
					
		}
		//add the 4 edges
		val = checkPolyValue(-order, -order);
		if (val<min){
			calcmin = val;
		}
		if (val>max){
			calcmax = val;
		}
		val = checkPolyValue(-order, divisionPoints+order);
		if (val<min){
			calcmin = val;
		}
		if (val>max){
			calcmax = val;
		}
		val = checkPolyValue(-order, divisionPoints+order);
		if (val<min){
			calcmin = val;
		}
		if (val>max){
			calcmax = val;
		}
		val = checkPolyValue(divisionPoints+order, divisionPoints+order);
		if (val<min){
			calcmin = val;
		}
		if (val>max){
			calcmax = val;
		}
		newContours += minadded>calcmin ? Math.ceil(Math.abs(minadded-calcmin)/step) : 0;
		newContours += maxadded<calcmax ? Math.ceil(Math.abs(calcmax-maxadded)/step) : 0;
		calcxmin -= xstep;
		calcxmax += xstep;
		calcymin -= ystep;
		calcymax += ystep;
		return newContours;
	}
	
	private void addAdditionalElements(GeoList list){
		calcmin = min;
		calcmax = max;
		// add boundaries
		calculateBoundary(1);
		calculateBoundary(2);
		for (double i=minadded-step;i>calcmin-step;i-=step){
			addToList(list, i);
			minadded = i;
		}
		for (double i=maxadded+step;i<calcmax+step;i+=step){
			addToList(list, i);
			maxadded = i;
		}
	}
	
	@Override
	public void compute() {
		calcxmin = xmin;
		calcxmax = xmax;
		calcymin = ymin;
		calcymax = ymax;
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
		implicitPoly = new GeoImplicitPoly(cons);		
		implicitPoly.setDefined();
		FunctionNVar f=func.getFunction();
		FunctionVariable[] fvars=f.getFunctionVariables();
		xstep = (xmax-xmin)/(divisionPoints-1.0);
		ystep = (ymax-ymin)/(divisionPoints-1.0);
		if (fvars.length!=2){
			implicitPoly.setUndefined();
			return;
		}
		try{
			en = f.getExpression().getCopy(kernel);
			Polynomial xVar=new Polynomial(kernel,"x");
			Polynomial yVar=new Polynomial(kernel,"y");
			en.replace(fvars[0], xVar);
			en.replace(fvars[1], yVar);
			equ =new Equation(kernel,en,new MyDouble(kernel));	
			equ.initEquation();
			poly =  equ.getNormalForm();
			implicitPoly.setCoeff(poly.getCoeff());
			for (int i=0;i<divisionPoints;i++){
				for (int j=0;j<divisionPoints;j++){
					double val = checkPolyValue(i,j);
					if (val<min){
						min = val;
					}
					if (val>max){
						max = val;
					}
				}
			}
			double freeTerm = 0;
			if (step == 0 && !fixed){
				freeTerm = implicitPoly.getCoeff()[0][0];
				step = Math.abs((max-min)/10.0);
				contourStep.set(new MyDouble(kernel, step).toGeoElement());
			}
			
			if ((min<=freeTerm) && (max>=freeTerm)){
				for (double i=freeTerm;i>min-step;i-=step){
					addToList(list, i);
					minadded = i;
				}
				for (double i=freeTerm+step;i<max+step;i+=step){
					addToList(list, i);
					maxadded = i;
				}
			}else{
				minadded = step * Math.floor((min-freeTerm)/step);
				for (double i=minadded;i<max+step;i+=step){
					addToList(list, i);
					maxadded = i;
				}
			}
			addAdditionalElements(list);
		}catch(MyError e){
			App.debug(e.getMessage());
			implicitPoly.setUndefined();
			list.add(new GeoImplicitPoly(implicitPoly));
		}
	}	

	private boolean movedOut(){
		return xmin<calcxmin || xmax > calcxmax || ymin<calcymin || ymax>calcymax;
	}
	private int getVisibleContourCount(){
		int count = 0;
		for (int i=0;i<list.size();i++){
			if (((GeoImplicitPoly)(list.get(i))).isOnScreen()){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public void update(){
		xmin = cons.getApplication().getActiveEuclidianView().getXmin();
		xmax = cons.getApplication().getActiveEuclidianView().getXmax();
		ymin = cons.getApplication().getActiveEuclidianView().getYmin();
		ymax = cons.getApplication().getActiveEuclidianView().getYmax();
		double newVisibleArea = Math.abs(xmax-xmin)*Math.abs(ymax-ymin);
		int visible = getVisibleContourCount();
		if (movedOut()){
			list.clear();
			compute();
		}
		if (visible<minContours && !fixed){
			step = step/2;
			contourStep.set(new MyDouble(kernel, step).toGeoElement());
			list.clear();
			compute();
		}
		if (visible>maxContours && !fixed){
			step = step*2;
			contourStep.set(new MyDouble(kernel, step).toGeoElement());
			list.clear();
			compute();
		}
		visibleArea = newVisibleArea;
		epsilon = visibleArea*1.0E-15;
		getOutput(0).update();
	}
	
	@Override
	public GetCommand getClassName() {		
		return Commands.ContourPlot;
	}

}
