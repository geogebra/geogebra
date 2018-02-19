package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.main.Localization;

public class StepSet extends StepLogical implements Iterable<StepNode> {

	private Set<StepNode> elements;

	public StepSet(StepNode... elements) {
		this.elements = new HashSet<>();
		for (StepNode element : elements) {
			this.elements.add(element.deepCopy());
		}
	}

	public StepNode[] getElements() {
		return elements.toArray(new StepNode[0]);
	}

	public void addElement(StepNode se) {
		elements.add(se);
	}

	public void remove(StepNode sn) {
		elements.remove(sn);
	}

	public void addAll(StepSet ss) {
		elements.addAll(ss.elements);
	}

	public boolean elementOf(StepNode se) {
		return elements.contains(se);
	}

	public int size() {
		return elements.size();
	}

	public boolean emptySet() {
		return elements.isEmpty();
	}

	@Override
	public boolean contains(StepExpression se) {
		return elements.contains(se);
	}

	@Override
	public StepSet deepCopy() {
		StepSet ss = new StepSet();
		for (StepNode sn : elements) {
			ss.addElement(sn.deepCopy());
		}
		
		return ss;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		for (StepNode sn : this) {
			sb.append(sn.toString());
			sb.append(", ");
		}

		sb.append("}");
		return sb.toString();
	}

	@Override
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + convertToString(loc, false) + "}";
		}
		return convertToString(loc, colored);
	}

	private String convertToString(Localization loc, boolean colored) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\left{");

		for (StepNode sn : this) {
			sb.append(sn.toLaTeXString(loc, colored));
			sb.append(", ");
		}

		sb.append("\\right}");
		return sb.toString();
	}

	@Override
	public Iterator<StepNode> iterator() {
		return elements.iterator();
	}

}
