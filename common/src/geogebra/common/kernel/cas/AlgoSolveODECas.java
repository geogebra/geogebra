package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.EvaluateAtPoint;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;

/**
 * @author zbynek
 *
 */
public class AlgoSolveODECas extends AlgoElement {
	private CasEvaluableFunction f;
	private GeoElement g;
	private GeoPointND pt;
	private AlgoElement helper;
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param f input function
	 */
	public AlgoSolveODECas(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons);
		this.f = f;
		/** g is created in compute */
		compute();
		setInputOutput();
		g.setLabel(label);
	}
	
	/**
	 *  @param cons construction
	 * @param label label for output
	 * @param f input function
	 * @param pt point through which the integral line should go
	 */
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
		if(pt!=null){
			sb.append(",");
			sb.append(pt.toValueString(StringTemplate.prefixedDefault));
		}
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
	}

	private void updateG(String casString) {
		String functionOut;
		boolean ok=false;
		try {
			//TODO put cacheing back
			functionOut = kernel.evaluateGeoGebraCAS(casString,arbconst);
			boolean flag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoElement[]res = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(functionOut,
					false,false,false);
			cons.setSuppressLabelCreation(flag);
			if(res!=null && res.length>0){
				if(g==null){
					g = res[0];
					helper = g.getParentAlgorithm();
				}else
					g.set(res[0]);
				ok =true;
			}
		} catch (Throwable e) {
			App.debug("AlgoDegree: " + e.getMessage());
		}
		if(!ok){
			if(g!=null)
				g.setUndefined();
			else
				g= new GeoFunction(cons);
		}	
	}

	/**
	 * @return resulting function, conic or line
	 */
	public GeoElement getResult(){
		return g;
	}	

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}
}
