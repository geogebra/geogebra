package geogebra.common.kernel.algos;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.NeedsEuclidianViewUpdate;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.AbstractApplication;

import java.util.ArrayList;

public class AlgoSlopeField extends AlgoElement implements NeedsEuclidianViewUpdate {

	private FunctionalNVar func; // input
	private GeoNumeric n, lengthRatio, minX, minY, maxX, maxY;
	//private GeoList g; // output        
	private GeoLocus locus; // output   
	ArrayList<MyPoint> al;

	public AlgoSlopeField(Construction cons, String label, FunctionalNVar func, GeoNumeric n, GeoNumeric lengthRatio, GeoNumeric minX, GeoNumeric minY, GeoNumeric maxX, GeoNumeric maxY) {
		super(cons);
		this.func = func;            	

		this.n = n;
		this.lengthRatio = lengthRatio;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

		//g = new GeoList(cons);   
		locus = new GeoLocus(cons);
		setInputOutput(); // for AlgoElement        
		compute();
		//g.setLabel(label);
		locus.setLabel(label);

		cons.registerEuclidianViewCE(this);

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoSlopeField;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		int noOfInputs = 1;
		if (n != null) noOfInputs++;
		if (lengthRatio != null) noOfInputs++;
		if (minX != null) noOfInputs++;
		if (minY != null) noOfInputs++;
		if (maxX != null) noOfInputs++;
		if (maxY != null) noOfInputs++;

		input = new GeoElement[noOfInputs];
		int i = 0;

		input[i++] = (GeoElement)func;
		if (n != null) input[i++] = (GeoElement)n;
		if (lengthRatio != null) input[i++] = (GeoElement)lengthRatio;
		if (minX != null) input[i++] = (GeoElement)minX;
		if (minY != null) input[i++] = (GeoElement)minY;
		if (maxX != null) input[i++] = (GeoElement)maxX;
		if (maxY != null) input[i++] = (GeoElement)maxY;

		super.setOutputLength(1);
		super.setOutput(0, locus);
		setDependencies(); // done by AlgoElement
	}

	public GeoLocus getResult() {
		return locus;
	}

	@Override
	public final void compute() {       
		if (!((GeoElement)func).isDefined()) {
			locus.setUndefined();
			return;
		}    

		if (al == null) al = new ArrayList<MyPoint>();
		else al.clear();



		AlgoNumerator numAlgo = new AlgoNumerator(cons, func);
		AlgoDenominator denAlgo = new AlgoDenominator(cons, func);
		cons.removeFromConstructionList(numAlgo);
		cons.removeFromConstructionList(denAlgo);

		FunctionalNVar num = (FunctionalNVar) numAlgo.getGeoElements()[0];
		FunctionalNVar den = (FunctionalNVar) denAlgo.getGeoElements()[0];

		double xmax = -Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmin = Double.MAX_VALUE;
		double ymax = -Double.MAX_VALUE;

		if (minX != null) {
			xmax = maxX.getDouble();
			ymax = maxY.getDouble();
			xmin = minX.getDouble(); 
			ymin = minY.getDouble();

		} else {

			// make sure it covers all of EV1 & EV2 if appropriate

			AbstractEuclidianView view = kernel.getApplication().getEuclidianView1();

			if (view.isVisibleInThisView(locus)) {
				xmax = Math.max(xmax,  view.toRealWorldCoordX((view.getWidth())));
				ymax = Math.max(ymax,  view.toRealWorldCoordY(0));
				xmin = Math.min(xmin,  view.toRealWorldCoordX(0));
				ymin = Math.min(ymin,  view.toRealWorldCoordY((view.getHeight())));
			}

			app = kernel.getApplication();
			if (app.hasEuclidianView2()) {
				AbstractEuclidianView view2 = app.getEuclidianView2();
				if (view2.isVisibleInThisView(locus)) {
					xmax = Math.max(xmax,  view2.toRealWorldCoordX((view.getWidth())));
					ymax = Math.max(ymax,  view2.toRealWorldCoordY(0));
					xmin = Math.min(xmin,  view2.toRealWorldCoordX(0));
					ymin = Math.min(ymin,  view2.toRealWorldCoordY((view.getHeight())));
				}
			}
		}

		// if it's visible in at least one view, calculate visible portion
		if (xmax > -Double.MAX_VALUE) {
			int nD = (int) (n == null ? 39 : n.getDouble() - 1);
			
			if (nD < 2 || nD > 100) {
				nD = 39;
			}

			double xStep = (xmax - xmin) / nD;
			double yStep = (ymax - ymin) / nD;

			double step1 = Math.max(xStep, yStep);



			double length = (lengthRatio == null ? 0.5 : lengthRatio.getDouble());
			
			if (length < 0 || length > 1 || Double.isInfinite(length) || Double.isNaN(length)) {
				length = 0.5;
			}
			
			length *= step1 / 2;
			
			boolean funcOfJustY = func instanceof GeoFunction && ((GeoFunction)func).isFunctionOfY();

			//AbstractApplication.debug(xStep+" "+yStep+" "+step);

			for (double xx = xmin ; xx < xmax + step1 / 2 ; xx += step1) {
				for (double yy = ymin ; yy < ymax + step1 / 2 ; yy += step1) {

					double [] input1 = {xx, yy};
					//double gradient = func.evaluate(input1);

					//AbstractApplication.debug(num.isDefined()+" "+den.isDefined());

					if (num.isDefined() && den.isDefined()) {
						// quotient function like x / y
						double numD = num.evaluate(input1);
						double denD = den.evaluate(input1);

						if (Kernel.isZero(denD)) {
							if (Kernel.isZero(numD)) {
								// just a dot
								al.add(new MyPoint(xx, yy, false));
								al.add(new MyPoint(xx, yy, true));
							} else {
								// vertical line
								al.add(new MyPoint(xx, yy - length, false));
								al.add(new MyPoint(xx, yy + length, true));							
							}
						} else {

							// standard case
							double gradient = numD / denD;
							drawLine(gradient, length, xx, yy);
						}
					} else {
						// non-quotient function like x y
						double gradient;
						
						if (funcOfJustY) {
							// eg SlopeField[y]
							gradient = ((GeoFunction)func).evaluate(input1[1]);
						} else {
							// standard case
							gradient = func.evaluate(input1);
						}
						drawLine(gradient, length, xx, yy);

					}

				}
			}
		}



		locus.setPoints(al);
		locus.setDefined(true);

	}
	
	private void drawLine(double gradient, double length, double xx, double yy) {
		double theta = Math.atan(gradient);
		double dx = length * Math.cos(theta);
		double dy = length * Math.sin(theta);
		al.add(new MyPoint(xx - dx, yy - dy, false));
		al.add(new MyPoint(xx + dx, yy + dy, true));

	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}


}

