package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;

public class AlgoCASPlot extends AlgoElement {
	private NumberValue cellIndex;
	private GeoList result;
	private GeoCasCell oldCell;
	public AlgoCASPlot(Construction construction, String label, NumberValue num) {
		super(construction);
		this.cellIndex = num;
		result = new GeoList(construction);
		setInputOutput();
		compute();
		result.setLabel(label);
		
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input=new GeoElement[]{cellIndex.toGeoElement()};
		setDependencies();
	}

	@Override
	public void compute() {
		if(oldCell!=null)
			oldCell.registerPlotAlgo(null);
		GeoCasCell cell = cons.getCasCell((int)cellIndex.getDouble()-1);
		if(cell==null){
			result.setUndefined();
			return;
		}
		cell.registerPlotAlgo(this);
		//the easy part -- CAS was not used
		if(!cell.isNative()){
			result.clear();
			result.add(((GeoElement)cell.getOutputValidExpression().unwrap()).copyInternal(cons));
			return;
		}
		//the interesting parts
		AbstractApplication.debug(cell.getOutputValidExpression().unwrap());
		ExpressionValue out =cell.getOutputValidExpression().unwrap();
		boolean solutions = true;
		double[] solnx=new double[1], solny=new double[1];
		if(out instanceof MyList){
			MyList list = (MyList) out;
			solnx=new double[list.size()];
			solny = new double[list.size()];
			for(int i = 0;i<list.size();i++){
				ExpressionValue el = list.getListElement(i).unwrap();
				if(el instanceof MyList && ((MyList)el).size()==2){
					solutions &= addSolution(solnx,i,((MyList)el).getListElement(0));
					solutions &= addSolution(solny,i,((MyList)el).getListElement(1));
				}else{
					solutions = false;
					break;
				}
			}
		}
		if(solutions){
			result.clear();
			for(int i=0;i<solnx.length;i++){
				GeoPoint2 pt = new GeoPoint2(cons);
				pt.setCoords(solnx[i],solny[i],1);
				result.add(pt);
			}
			return;
		}
		
		//fallback
		boolean oldMode = kernel.isSilentMode();
		try {
			kernel.setSilentMode(true);
			GeoElement[] geos = kernel.getAlgebraProcessor().processValidExpression(cell.getOutputValidExpression());
			if(geos.length==1 && geos[0] instanceof GeoList){
				result.set(geos[0]);
			}
			
		} catch (MyError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		kernel.setSilentMode(oldMode);
	}

	private static boolean addSolution(double[] array, int i,
			ExpressionValue eq) {
		if(!(eq instanceof Equation))
			return false;
		array[i] = ((Equation)eq).getRHS().evaluateNum().getDouble();
		return true;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoCASPlot;
	}

	public GeoElement getResult() {
		return result;
	}

}
