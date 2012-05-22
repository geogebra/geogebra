package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.StringUtil;

public class AlgoSolveODECas extends AlgoElement {
	private CasEvaluableFunction f;
	private GeoElement g;
	public AlgoSolveODECas(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons);
		this.f = f;
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
		input = new GeoElement[]{f.toGeoElement()};
		setOnlyOutput(g);
		setDependencies();
		
	}

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
		String functionOut;
		boolean ok=false;
		try {
			functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),arbconst);
			boolean flag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoElement[]res = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(functionOut, false);
			cons.setSuppressLabelCreation(flag);
			if(g==null)
				g = res[0];
			else
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
