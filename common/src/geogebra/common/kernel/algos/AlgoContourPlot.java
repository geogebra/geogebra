package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
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
	private GeoElement xmin_geo, xmax_geo, ymin_geo, ymax_geo;
	private GeoList list; // output
	private double visibleArea, epsilon;
	private Equation equ;
	private Polynomial poly;
	private ExpressionNode en;
	private GeoImplicitPoly implicitPoly;
	private double min, max, step, xstep, ystep;
	private int divisionPoints;
	private double calcmin, calcmax, calcxmin, calcxmax, calcymin, calcymax, minadded, maxadded;
	
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
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.func = func;
		this.visibleArea = Math.abs(xmax-xmin)*Math.abs(ymax-ymin);
		this.epsilon = visibleArea*1.0E-15;
		this.divisionPoints = 5;
		xmin_geo = new MyDouble(kernel, xmin).toGeoElement();
		xmax_geo = new MyDouble(kernel, xmax).toGeoElement();
		ymin_geo = new MyDouble(kernel, ymin).toGeoElement();
		ymax_geo = new MyDouble(kernel, ymax).toGeoElement();
		list = new GeoList(cons);		
		setInputOutput();
		compute();
		list.setLabel(label);
	}	

	@Override
	protected void setInputOutput() {
		list.setTypeStringForXML("ImplicitPoly");
		input = new GeoElement[1];
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
		val = checkPolyValue(-order, divisionPoints+order-1);
		if (val<min){
			calcmin = val;
		}
		if (val>max){
			calcmax = val;
		}
		val = checkPolyValue(-order, divisionPoints+order-1);
		if (val<min){
			calcmin = val;
		}
		if (val>max){
			calcmax = val;
		}
		val = checkPolyValue(divisionPoints+order-1, divisionPoints+order-1);
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
		int elementCount = list.size();
		calcmin = min;
		calcmax = max;
		// add first boundary
		int added = calculateBoundary(1);
		if (elementCount+added<25){
			added += calculateBoundary(2);
		}
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
			if(Kernel.isEqual(max, min)){
				list.setUndefined();
				return;
			}
			step = Math.abs((max-min)/10.0);
			double freeTerm = implicitPoly.getCoeff()[0][0];
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
				minadded = min;
				System.out.println(min+","+max+","+step);
				for (double i=min;i<max;i+=step){
					addToList(list, i);
					maxadded = i;
				}
				if (freeTerm<-epsilon||freeTerm>epsilon){
					addToList(list, freeTerm);
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
		xmin_geo.set(new MyDouble(kernel, xmin).toGeoElement());
		xmax_geo.set(new MyDouble(kernel, xmax).toGeoElement());
		ymin_geo.set(new MyDouble(kernel, ymin).toGeoElement());
		ymax_geo.set(new MyDouble(kernel, ymax).toGeoElement());
		double newVisibleArea = Math.abs(xmax-xmin)*Math.abs(ymax-ymin);
		if (visibleArea-newVisibleArea<-epsilon){
			if (movedOut()){
				list.clear();
				compute();
			}
		}else if (visibleArea-newVisibleArea>epsilon){
			if (getVisibleContourCount()<7){
				list.clear();
				compute();
			}
		}else{
			if (movedOut() || getVisibleContourCount()<7){
				list.clear();
				compute();
			}
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
