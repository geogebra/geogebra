package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.Functional;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.StringUtil;

public class AlgoSolveODECas extends AlgoElement {
	private CasEvaluableFunction f;
	private GeoElement g;
	private GeoPointND pt;
	private AlgoElement helper;
	public AlgoSolveODECas(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons);
		this.f = f;
		/** g is created in compute */
		compute();
		setInputOutput();
		g.setLabel(label);
	}
	
	public AlgoSolveODECas(Construction cons,  String label, CasEvaluableFunction f,GeoPointND pt) {
		super(cons);
		this.f = f;
		this.pt = pt;
		/** g is created in compute */
		compute();
		setInputOutput();
		g.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoSolveODECas;
	}
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	

	@Override
	protected void setInputOutput() {
		if(pt==null){
			input = new GeoElement[]{f.toGeoElement()};
		}
		else{
			input = new GeoElement[]{f.toGeoElement(),pt.toGeoElement()};
		}
		setOnlyOutput(g);
		setDependencies();
		
	}
	private String oldCASstring;
	@Override
	public void compute() {
		if (!f.isDefined() && g!=null) {
			g.setUndefined();
			return;
		}
		
		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		sb.append("SolveODE(");
		sb.append(f.toString(StringTemplate.prefixedDefault)); // function expression
		sb.append(")");
		String casString = sb.toString();
		if(!casString.equals(oldCASstring)){
			updateG(casString);
			oldCASstring = casString;
		}
		if(pt!=null && arbconst.getTotalNumberOfConsts()==1){
			findPathThroughPoint();
		}

	}
	private void findPathThroughPoint() {
		GeoNumeric c1 = arbconst.getConst(0);
		if(c1==null){
			g.setUndefined();
			return;
		}
		c1.setAlgebraVisible(false);
		if(g instanceof Functional){
			GeoPoint2 ptt = (GeoPoint2)pt;
			c1.setValue(0);
			double val0 = ((Functional)g).evaluate(ptt.getX()/ptt.getZ());
			c1.setValue(1);
			double val1 = ((Functional)g).evaluate(ptt.getX()/ptt.getZ());
			double d= (ptt.getY()/ptt.getZ()-val0)/(val1-val0);
			c1.setValue(d);
			double val = ((Functional)g).evaluate(ptt.getX()/ptt.getZ());
			if(!Kernel.isEqual(ptt.getY()/ptt.getZ(), val)){
				g.setUndefined();
			}
		}
		else if(g instanceof GeoConic){
			GeoPoint2 ptt = (GeoPoint2)pt;
			c1.setValue(0);
			helper.update();
			double val0 = ((GeoConic)g).evaluate(ptt);
			c1.setValue(1);
			helper.update();
			double val1 = ((GeoConic)g).evaluate(ptt);
			double d= (0-val0)/(val1-val0);
			c1.setValue(d);
			helper.update();
			double val = ((GeoConic)g).evaluate(ptt);
			if(!Kernel.isZero(val)){
				g.setUndefined();
			}
		}else{
			AbstractApplication.debug("Unhandled case "+g.getClass());
		}
		
	}

	private void updateG(String casString) {
		String functionOut;
		boolean ok=false;
		try {
			//TODO put cacheing back
			functionOut = kernel.evaluateGeoGebraCAS(casString,arbconst);
			boolean flag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoElement[]res = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(functionOut, false);
			cons.setSuppressLabelCreation(flag);
			if(g==null){
				g = res[0];
				helper = g.getParentAlgorithm();
			}else
				g.set(res[0]);
			ok =true;
		} catch (Throwable e) {
			AbstractApplication.debug("AlgoDegree: " + e.getMessage());
		}
		if(!ok){
			if(g!=null)
				g.setUndefined();
			else
				g= new GeoFunction(cons);
		}	
	}

	public GeoElement getResult(){
		return g;
	}	
}
