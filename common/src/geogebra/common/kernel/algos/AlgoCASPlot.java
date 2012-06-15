package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.main.MyError;

/**
 * Algorithm that tries to find reasonable graphical representation of given CAS cell 
 * @author zbynek
 *
 */
public class AlgoCASPlot extends AlgoElement {
	private NumberValue cellIndex;
	private GeoList result;
	private GeoCasCell oldCell;
	private GeoBoolean plotOutput;
	/**
	 * @param construction construction
	 * @param label label for output
	 * @param num cell index
	 */
	public AlgoCASPlot(Construction construction, String label, NumberValue num,GeoBoolean plotOutput) {
		super(construction);
		this.cellIndex = num;
		this.plotOutput = plotOutput;
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
		if(plotOutput.getBoolean()){
			plotOutput(cell);
		}else{
			plotInput(cell);
		}
	}

	private void plotInput(GeoCasCell cell) {
		ValidExpression in = cell.getInputVE();
		if(in.getTopLevelCommand()==null){
			plotFormula(in);
			return;
		}
		switch(Commands.valueOf(in.getTopLevelCommand().getName())){
		case Solutions:
		case Solve:
			boolean oldMode = kernel.isSilentMode();
			try {
				kernel.setSilentMode(true);
				GeoElement[] geos = kernel.getAlgebraProcessor().processValidExpression(in.getTopLevelCommand().getArgument(0));
				if(geos.length==1 && geos[0] instanceof GeoList){
					result.set(geos[0]);
				}
				else{
					result.clear();
					for(int i =0;i<geos.length;i++)
						result.add(geos[i]);
				}
				
			} catch (MyError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			kernel.setSilentMode(oldMode);
			break;
		default: plotFormula(in);	
		}
	
		
		
	}

	private void plotFormula(ValidExpression in) {
		String text = in.toString(StringTemplate.latexTemplate);
		GeoText txt = new GeoText(cons);
		txt.setTextString(text);
		txt.setLaTeX(true, false);
		result.clear();
		result.add(txt);
	}

	private void plotOutput(GeoCasCell cell) {
		//the easy part -- CAS was not used
				if(!cell.isNative()){
					result.clear();
					result.add(((GeoElement)cell.getOutputValidExpression().unwrap()).copyInternal(cons));
					return;
				}
				//the interesting parts
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
							solutions &= addSolution(solnx,i,((MyList)el).getListElement(0).unwrap());
							solutions &= addSolution(solny,i,((MyList)el).getListElement(1).unwrap());
						}else{
							solutions = false;
							break;
						}
					}
				}else{
					solutions = false;
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
					else{
						result.clear();
						for(int i =0;i<geos.length;i++)
							result.add(geos[i]);
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
		if(eq instanceof Equation)
			array[i] = ((Equation)eq).getRHS().evaluateNum().getDouble();
		else if(eq instanceof MyDouble)
			array[i] = ((MyDouble)eq).getDouble();
		else return false;
		return true;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoCASPlot;
	}

	/**
	 * @return resulting list
	 */
	public GeoElement getResult() {
		return result;
	}

}
