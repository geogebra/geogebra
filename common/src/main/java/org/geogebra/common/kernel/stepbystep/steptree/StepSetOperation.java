package org.geogebra.common.kernel.stepbystep.steptree;



import org.geogebra.common.main.Localization;

import java.util.*;

public class StepSetOperation extends StepLogical implements Iterable<StepLogical> {

	private SetOperation operation;
	private List<StepLogical> operands;

	public StepSetOperation(SetOperation operation) {
		this.operation = operation;
		this.operands = new ArrayList<>();
	}

	public int noOfOperands() {
		return operands.size();
	}

	public StepLogical getOperand(int i) {
		return operands.get(i);
	}

	public SetOperation getOperation() {
		return operation;
	}

	public void addOperand(StepLogical sl) {
		if (sl != null) {
			if (isSetOperation(SetOperation.UNION)
					&& sl.isSetOperation(SetOperation.UNION)
					|| isSetOperation(SetOperation.INTERSECT)
							&& sl.isSetOperation(SetOperation.INTERSECT)) {
				for (StepLogical operand : (StepSetOperation) sl) {
					addOperand(operand);
				}
			} else {
				operands.add(sl);
			}
		}
	}

	@Override
	public boolean contains(StepExpression se) {
		switch (operation) {
			case UNION:
				for (StepLogical sl : operands) {
					if (sl.contains(se)) {
						return true;
					}
				}
				return false;
			case INTERSECT:
				for (StepLogical sl : operands) {
					if (!sl.contains(se)) {
						return false;
					}
				}
				return true;
			case DIFFERENCE:
				return getOperand(0).contains(se) && !getOperand(1).contains(se);
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((operands == null) ? 0 : operands.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepSetOperation) {
			StepSetOperation so = (StepSetOperation) obj;

			if (so.operation != operation || so.operands.size() != operands.size()) {
				return false;
			}

			StepSetOperation copyOfThis = deepCopy().sort();
			StepSetOperation copyOfThat = so.deepCopy().sort();

			return copyOfThis.operands.equals(copyOfThat.operands);
		}

		return false;
	}

	public StepSetOperation sort() {
		for (StepLogical operand : this) {
			if (operand instanceof StepSetOperation) {
				((StepSetOperation) operand).sort();
			}
		}

		if (isSetOperation(SetOperation.INTERSECT) || isSetOperation(SetOperation.UNION)) {
			Collections.sort(operands, new Comparator<StepLogical>() {
				@Override
				public int compare(StepLogical o1, StepLogical o2) {
					return o1.hashCode() - o2.hashCode();
				}
			});
		}

		return this;
	}

	@Override
	public Iterator<StepLogical> iterator() {
		return operands.iterator();
	}

	@Override
	public StepSetOperation deepCopy() {
		StepSetOperation so = new StepSetOperation(operation);
		for (StepLogical operand : operands) {
			so.addOperand(operand.deepCopy());
		}
		return so;
	}

	@Override
	public String toString() {
		switch (operation) {
			case UNION:
				StringBuilder su = new StringBuilder();
				for (int i = 0; i < operands.size(); i++) {
					if (i != 0) {
						su.append(" u ");
					}
					su.append(operands.get(i).toString());
				}
				return su.toString();
			case INTERSECT:
				StringBuilder si = new StringBuilder();
				for (int i = 0; i < operands.size(); i++) {
					if (i != 0) {
						si.append(" n ");
					}
					si.append(operands.get(i).toString());
				}
				return si.toString();
			case DIFFERENCE:
				return operands.get(0).toString() + " \\ " + operands.get(1).toString();
		}
		return "";
	}

	@Override
	public String toLaTeXString(Localization loc) {
		return convertToString(loc, false);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		return convertToString(loc, colored);
	}

	private String convertToString(Localization loc, boolean colored) {
		switch (operation) {
			case UNION:
				StringBuilder su = new StringBuilder();
				for (int i = 0; i < operands.size(); i++) {
					if (i != 0) {
						su.append(" \\cup ");
					}
					su.append(operands.get(i).toLaTeXString(loc, colored));
				}
				return su.toString();
			case INTERSECT:
				StringBuilder si = new StringBuilder();
				for (int i = 0; i < operands.size(); i++) {
					if (i != 0) {
						si.append(" \\cap ");
					}
					si.append(operands.get(i).toLaTeXString(loc, colored));
				}
				return si.toString();
			case DIFFERENCE:
			return operands.get(0).toLaTeXString(loc, colored) + " \\setminus "
					+ operands.get(1).toLaTeXString(loc, colored);

		}
		return "";
	}
}
