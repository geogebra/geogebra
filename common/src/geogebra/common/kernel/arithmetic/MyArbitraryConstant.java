package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;

import java.util.ArrayList;
import java.util.Map;

/**
 * Arbitrary constant comming from reduce
 *
 */
public class MyArbitraryConstant  {
	/** arbitrary integer*/
	public static final int ARB_INT = 0;
	/** arbitrary double*/
	public static final int ARB_CONST = 1;
	/** arbitrary complex number*/
	public static final int ARB_COMPLEX = 2;

	
	private ArrayList<GeoNumeric> consts= new ArrayList<GeoNumeric>(), ints= new ArrayList<GeoNumeric>(), complexNumbers = new ArrayList<GeoNumeric>();
	
	private ConstructionElement ce;
	/**
	 * Creates new arbitrary constant handler
	 * @param ce associated construction element
	 */
	public MyArbitraryConstant(ConstructionElement ce){
		this.ce = ce;
	}
	/*public static String latexStr(String prefix,Map<Integer,String> map,Integer number,Construction cons){
		String s = map.get(number);
		if(s!=null)
			return s;
		s = cons.getIndexLabel(prefix, number);
		map.put(number, s);
		return s;
	}*/
	private int position = 0;
	/**
	 * @param myDouble constant index (global)
	 * @return real constant
	 */
	public ExpressionValue nextConst(MyDouble myDouble) {
		return nextConst(consts,ce.getConstruction().constsM,"c",myDouble);
	}
	/**
	 * @param myDouble constant index (global)
	 * @return integer constant
	 */
	public ExpressionValue nextInt(MyDouble myDouble) {
		return nextConst(ints,ce.getConstruction().intsM,"k",myDouble);
	}
	/**
	 * @param myDouble constant index (global)
	 * @return complex constant
	 */
	public ExpressionValue nextComplex(MyDouble myDouble) {
		return nextConst(complexNumbers,ce.getConstruction().complexNumbersM,"c",myDouble);
	}
	
	private ExpressionValue nextConst(ArrayList<GeoNumeric> consts2,Map<Integer, GeoNumeric> map, String prefix, MyDouble myDouble) {
		Integer index = new Integer((int)Math.round(myDouble.getDouble()));
		GeoNumeric found = map.get(index);
		if(found!=null)
			return found;
		Construction c = ce.getConstruction();
		if(position >= consts2.size() || consts2.get(position)==null){
			GeoNumeric add = new GeoNumeric(c);
			add.setAuxiliaryObject(true);
			boolean oldLabeling = c.isSuppressLabelsActive();
			c.setSuppressLabelCreation(false);
			add.setLabel(c.getIndexLabel(prefix));
			c.setSuppressLabelCreation(oldLabeling);
			AlgoDependentArbconst algo = new AlgoDependentArbconst(c,add,ce);
			c.removeFromConstructionList(algo);
			consts2.add(position,add);
			position++;
			map.put(index,add);
			return add;
		}
		GeoNumeric ret = consts2.get(position);
		map.put(index,ret);
		position++;
		return ret;
	}

	/**
	 * Resets the handler; must be called before the first next*() call
	 * in each update of the CAS algo that is creating arbconsts
	 */
	public void reset(){
		position=0;		
	}
	
	/**
	 * Gets arbconst
	 * @param i index of arbconst within this handler
	 * @return arbconst
	 */
	public GeoNumeric getConst(int i){
		return consts.get(i);
	}
	
	/**
	 * @return number of arbconsts, arbcomplexes and arbints together
	 */
	public int getTotalNumberOfConsts(){
		return consts.size()+ints.size()+complexNumbers.size();
	}
	
	/**
	 * Ensures that update of the constant (if visualised as slider) triggers
	 * update of resulting geo. This is not meant to be contained in construction protocol.
	 *
	 */
	public class AlgoDependentArbconst extends AlgoElement{
		private GeoElement constant;
		private ConstructionElement outCE;
		//private ArrayList<AlgoElement> updateList;
		/**
		 * @param c construction
		 * @param constant the constant as a (complex) number
		 * @param outCE element that needs updating if the constant changes
		 */
		public AlgoDependentArbconst(Construction c,GeoElement constant,ConstructionElement outCE) {
			super(c,false);
			this.constant=constant;
			this.outCE = outCE;
			/**if(outCE instanceof AlgoElement){
				updateList = new ArrayList<AlgoElement>();
				updateList.add((AlgoElement)outCE);
			}*/
				
			setInputOutput();
		}

		@Override
		protected void setInputOutput() {
			input = new GeoElement[] {constant}; 
			setDependencies();
		}

		@Override
		public void compute() {
			if(outCE instanceof AlgoElement && ((AlgoElement)outCE).getOutputLength()==1)
				((AlgoElement) outCE).getOutput(0).updateCascade();
			else if(outCE!=null)
				outCE.update();
		}

		@Override
		public Algos getClassName() {
			return Algos.AlgoDependentNumber;
		}
		
	}


	/**
	 * @return whether this handler is bound with CAS cell
	 */
	public boolean isCAS() {
		return ce instanceof GeoCasCell;
	}
	
	
	
		
}
