package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;

import com.google.j2objc.annotations.Weak;

/**
 * Arbitrary constant comming from native CAS
 *
 * Each scope (cas cell or CAS using algo) should have an own instance of this
 * class
 */
public class ArbitraryConstantRegistry {
	/** arbitrary integer */
	public static final int ARB_INT = 0;
	/** arbitrary double */
	public static final int ARB_CONST = 1;
	/** arbitrary complex number */
	public static final int ARB_COMPLEX = 2;

	private final ArrayList<GeoNumeric> consts = new ArrayList<>();
	private final ArrayList<GeoNumeric> ints = new ArrayList<>();
	private final ArrayList<GeoNumeric> complexNumbers = new ArrayList<>();

	@Weak
	private ConstructionElement ce;
	private int position = 0;
	private boolean blocking;
	private boolean symbolic;

	/**
	 * Creates new arbitrary constant handler
	 * @param ce associated construction element
	 */
	public ArbitraryConstantRegistry(ConstructionElement ce) {
		this.ce = ce;
	}

	/**
	 * @param myDouble constant index (global)
	 * @return real constant
	 */
	public GeoNumeric nextConst(double myDouble) {
		return nextConst(myDouble, 0);
	}

	/**
	 * @param myDouble constant index (global)
	 * @param initialValue initial value
	 * @return real constant
	 */
	public GeoNumeric nextConst(double myDouble, double initialValue) {
		return nextConst(consts, ce.getConstruction().getArbitraryConstants(),
				"c", myDouble, initialValue, 0);
	}

	/**
	 * @param myDouble constant index (global)
	 * @return integer constant
	 */
	public GeoNumeric nextInt(double myDouble) {
		return nextConst(ints, ce.getConstruction().getArbitraryInts(), "k", myDouble, 0, 1);
	}

	/**
	 * @param myDouble constant index (global)
	 * @return complex constant
	 */
	public GeoNumeric nextComplex(double myDouble) {
		return nextConst(complexNumbers, ce.getConstruction().getArbitraryComplexNumbers(),
				"c", myDouble, 0, 0);
	}

	/**
	 * Returns a number for this scope that corresponds to given native index
	 * @param consts2 all constants of given type (integer / real / complex) in this
	 * scope cached from last computation
	 * @param map maps geo labels to constants in the whole construction
	 * @param prefix prefix fro this constant type: c for real / complex, k for
	 * integer
	 * @param index index we got from native CAS, we assume it's getting bigger
	 * with each computation but two constants with the same name
	 * always refer to the same number eg
	 * {x+arbonst(10),2x+arbconst(10)+arbconst(9)}
	 * @param initialValue initial value of the constant, set only if the constant
	 * @param increment slider increment
	 * does not yet exist and has to be created, used only for numeric constants
	 * @return element of consts2; if one with a given index already exists in
	 * map take that one, otherwise pick the next one from consts2 (or
	 * create one if there are not enough)
	 */
	protected GeoNumeric nextConst(ArrayList<GeoNumeric> consts2,
			Map<Integer, GeoNumeric> map, String prefix, double index,
			double initialValue, double increment) {
		int indexInt = (int) Math.round(index);
		GeoNumeric found = map.get(indexInt);
		if (found != null) {
			return found;
		}
		if (position >= consts2.size() || consts2.get(position) == null) {
			return createConstant(consts2, map, prefix, indexInt, initialValue, increment);
		}
		GeoNumeric ret = consts2.get(position);
		map.put(indexInt, ret);
		// put existent constant into construction
		// after geoCasCell update
		Construction cons = ce.getConstruction();
		if (cons.isFileLoading()) {
			cons.addToConstructionList(ret, false);
			cons.putLabel(ret);
		} else {
			cons.putLabel(ret);
		}
		position++;
		return ret;
	}

	private GeoNumeric createConstant(ArrayList<GeoNumeric> consts2,
			Map<Integer, GeoNumeric> map, String prefix, int index,
			double initialValue, double increment) {
		Construction construction = ce.getConstruction();
		GeoNumeric constant = symbolic ? null : getNextFreeNumber(prefix + "_");
		if (constant == null) {
			String label = construction.getIndexLabel(prefix, true);
			if (symbolic) {
				constant = createSymbolicConstant(construction, label);
			} else {
				constant = createNumericConstant(construction, label, initialValue, increment);
			}
		}

		consts2.add(position, constant);
		position++;
		map.put(index, constant);

		return constant;
	}

	private GeoNumeric getNextFreeNumber(String prefix) {
		if (ce.getGeoElements() == null) {
			return null;
		}
		GeoElementND geoElement = ce.getGeoElements()[0];
		SortedSet<GeoElement> after = ce.getConstruction().getGeoSetConstructionOrder()
				.tailSet(geoElement.toGeoElement());
		int found = 0;
		for (GeoElement next: after) {
			if (!next.isGeoElement()) {
				continue;
			}
			if (next.getCorrespondingCasCell() != null) {
				continue;
			}
			if (next.isGeoNumeric() && next.isIndependent()
					&& next.getLabelSimple() != null && next.getLabelSimple().startsWith(prefix)) {
				found++;
				if (found > position && !((GeoNumeric) next).isDependentConst()) {
					return wrapInAlgo((GeoNumeric) next);
				}
			} else {
				break;
			}
		}
		return null;
	}

	private GeoNumeric createNumericConstant(Construction cons,
			String label, double initialValue, double increment) {
		GeoNumeric numeric = new GeoNumeric(cons, initialValue);
		numeric.setShowExtendedAV(true);
		numeric.setSendValueToCas(false);
		if (!getKernel().getApplication().showAutoCreatedSlidersInEV()
				&& !getKernel().getConstruction().isFileLoading()) {
			numeric.initAlgebraSlider();
		}
		if (increment > 0) {
			numeric.setAnimationStep(increment);
			numeric.setAutoStep(false);
		}
		boolean oldLabeling = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(false);
		// let construction know that we need new constant
		// after geoCasCell update
		cons.setNotXmlLoading(true);
		numeric.setLabel(label);
		cons.setNotXmlLoading(false);
		cons.setSuppressLabelCreation(oldLabeling);

		return wrapInAlgo(numeric);
	}

	private GeoNumeric wrapInAlgo(GeoNumeric numeric) {
		AlgoDependentArbitraryConstant
				algo = new AlgoDependentArbitraryConstant(ce.getConstruction(), numeric, ce);
		ce.getConstruction().removeFromConstructionList(algo);
		numeric.setIsDependentConst(true);
		return numeric;
	}

	private GeoDummyVariable createSymbolicConstant(Construction cons, String label) {
		GeoDummyVariable variable = new GeoDummyVariable(cons, label);
		variable.setAuxiliaryObject(true);
		variable.setLabel(label);
		cons.getCASdummies().add(label);
		return variable;
	}

	/**
	 * Resets the handler; must be called before the first next*() call in each
	 * update of the CAS algo that is creating arbconsts
	 */
	public void reset() {
		position = 0;
	}

	/**
	 * Return the list of constants
	 * @return consts
	 */
	public ArrayList<GeoNumeric> getConstList() {
		if (consts != null) {
			return consts;
		}
		return null;
	}

	/**
	 * @return cas cell that contains the constant
	 */
	public GeoCasCell getCasCell() {
		if (isCAS()) {
			return (GeoCasCell) ce;
		}
		return null;
	}

	/**
	 * @param ce - geoCasCell
	 */
	public void setCasCell(GeoCasCell ce) {
		if (isCAS()) {
			this.ce = ce;
		}
	}

	/**
	 * @return number of arbconsts, arbcomplexes and arbints together
	 */
	public int getTotalNumberOfConsts() {
		return consts.size() + ints.size() + complexNumbers.size();
	}

	/**
	 * Ensures that update of the constant (if visualised as slider) triggers
	 * update of resulting geo. This is not meant to be contained in
	 * construction protocol.
	 */
	public static class AlgoDependentArbitraryConstant extends AlgoElement {
		private final GeoElement constant;
		private ConstructionElement outCE;

		/**
		 * @param c construction
		 * @param constant the constant as a (complex) number
		 * @param outCE element that needs updating if the constant changes
		 */
		public AlgoDependentArbitraryConstant(Construction c, GeoElement constant,
				ConstructionElement outCE) {
			super(c, false);
			this.constant = constant;
			this.outCE = outCE;

			setInputOutput();
		}

		@Override
		protected void setInputOutput() {
			input = new GeoElement[]{constant};
			setOutput(new GeoElement[0]);
			setDependencies();
		}

		@Override
		public void compute() {
			if (outCE instanceof AlgoElement
					&& ((AlgoElement) outCE).getOutputLength() == 1) {
				((AlgoElement) outCE).getOutput(0).updateCascade();
			} else if (outCE instanceof GeoCasCell) {
				outCE.update();
				if (((GeoCasCell) outCE).getTwinGeo() != null) {
					((GeoCasCell) outCE).getTwinGeo().update();
				}
			} else if (outCE instanceof GeoSymbolic) {
				((GeoSymbolic) outCE).updateCascade();
				if (((GeoSymbolic) outCE).getTwinGeo() != null) {
					((GeoSymbolic) outCE).getTwinGeo().update();
				}
			} else if (outCE != null) {
				outCE.update();
			}
		}

		@Override
		public Algos getClassName() {
			return Algos.Expression;
		}

		/**
		 * For cas cells replace CAS cell with the right cell on given row
		 */
		public void replaceOutCE() {
			if (outCE instanceof GeoCasCell) {
				this.outCE = cons
						.getCasCell(((GeoCasCell) outCE).getRowNumber());
			}
		}

	}

	/**
	 * @return whether this handler is bound with CAS cell
	 */
	public boolean isCAS() {
		return ce instanceof GeoCasCell;
	}

	/**
	 * TODO having just one position assumes that only one of real / integer /
	 * complex is used
	 * @return index of next constant
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Switch to blocking mode where no arb constants are treated as 0
	 */
	public void startBlocking() {
		this.blocking = true;
	}

	/**
	 * @return whether this replaces +2kp with 0
	 */
	protected boolean isBlocking() {
		return blocking;
	}

	public void setSymbolic(boolean symbolic) {
		this.symbolic = symbolic;
	}

	/**
	 * Replaces arbconst(), arbint(), arbcomplex() by auxiliary numerics
	 */
	public static class ArbconstReplacer implements Traversing {
		private ArbitraryConstantRegistry arbconst;
		private static final ArbconstReplacer replacer = new ArbconstReplacer();

		private ArbconstReplacer() {
			// singleton constructor
		}

		@Override
		public ExpressionValue process(ExpressionValue ev) {
			if (!ev.isExpressionNode() || arbconst == null) {
				return ev;
			}

			ExpressionNode en = (ExpressionNode) ev;

			if (arbconst.isBlocking()) {
				return handleSpecialCase(en);
			}
			if (en.getOperation() == Operation.MULTIPLY) {
				if (en.getLeft() != null && en.getLeftTree()
						.getOperation() == Operation.ARBCONST) {
					GeoNumeric newLeft = arbconst.nextConst(
							en.getLeftTree().getLeft().evaluateDouble(), 1);
					en.getRight().traverse(this);
					en.setLeft(newLeft);
				}
				if (en.getRight() != null && en.getRightTree()
						.getOperation() == Operation.ARBCONST) {
					GeoNumeric newRight = arbconst.nextConst(
							en.getRightTree().getLeft().evaluateDouble(), 1);
					en.getLeft().traverse(this);
					en.setRight(newRight);
				}
				return en;
			}
			if (en.getOperation() == Operation.ARBCONST) {
				return arbconst.nextConst(en.getLeft().evaluateDouble());
			}
			if (en.getOperation() == Operation.ARBINT) {
				return arbconst.nextInt(en.getLeft().evaluateDouble());
			}
			if (en.getOperation() == Operation.ARBCOMPLEX) {
				return arbconst.nextComplex(en.getLeft().evaluateDouble());
			}
			return en;
		}

		private ExpressionValue handleSpecialCase(ExpressionNode en) {
			if (en.getOperation().isPlusorMinus()) {
				if (isMultipleOfArbconst(en.getRight())) {
					return en.getLeft();
				}
				if (isMultipleOfArbconst(en.getLeft())) {
					ExpressionValue ret = en.getRight();
					if (en.getOperation() == Operation.MINUS) {
						ret = new ExpressionNode(arbconst.getKernel(),
								new MinusOne(arbconst.getKernel()),
								Operation.MULTIPLY, ret);
					}
					return ret;
				}
			} else if (isMultipleOfArbconst(en)) {
				return new MyDouble(arbconst.getKernel(), 0);
			}
			return en;
		}

		/**
		 * @param arbconst arbitrary constant handler
		 * @return replacer
		 */
		public static ArbconstReplacer getReplacer(
				ArbitraryConstantRegistry arbconst) {
			replacer.arbconst = arbconst;
			return replacer;
		}
	}

	/**
	 * @param right expression
	 * @return whether expression is a/b*arbconst() in some arrangement
	 */
	public static boolean isMultipleOfArbconst(ExpressionValue right) {
		if (right.isExpressionNode()) {
			ExpressionNode en = right.wrap();
			switch (en.getOperation()) {
			case ARBCONST:
			case ARBCOMPLEX:
			case ARBINT:
				return true;
			case MULTIPLY:
				return isMultipleOfArbconst(en.getLeft())
						|| isMultipleOfArbconst(en.getRight());
			case DIVIDE:
				return isMultipleOfArbconst(en.getLeft());
			default:
				return false;
			}
		}
		return false;
	}

	public Kernel getKernel() {
		return ce.getKernel();
	}

}
