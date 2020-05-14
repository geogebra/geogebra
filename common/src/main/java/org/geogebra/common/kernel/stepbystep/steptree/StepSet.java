package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.main.Localization;

public class StepSet extends StepLogical implements Iterable<StepExpression> {

	private Set<StepExpression> elements;

	public StepSet(StepExpression... elements) {
		this.elements = new HashSet<>(Arrays.asList(elements));
	}

	public StepNode[] getElements() {
		return elements.toArray(new StepNode[0]);
	}

	public void addElement(StepExpression se) {
		elements.add(se);
	}

	public void remove(StepExpression sn) {
		elements.remove(sn);
	}

	public void addAll(StepSet ss) {
		elements.addAll(ss.elements);
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
		for (StepExpression sn : elements) {
			ss.addElement(sn.deepCopy());
		}

		return ss;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepSet) {
			return ((StepSet) obj).elements.equals(elements);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (StepNode sn : this) {
			if (!"".equals(sb.toString())) {
				sb.append(", ");
			}
			sb.append(sn.toString());
		}

		return "{" + sb.toString() + "}";
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
		StringBuilder sb = new StringBuilder();

		for (StepNode sn : this) {
			if (!"".equals(sb.toString())) {
				sb.append(", ");
			}
			sb.append(sn.toLaTeXString(loc, colored));
		}

		return "\\left\\{ " + sb.toString() + " \\right\\}";
	}

	@Override
	public Iterator<StepExpression> iterator() {
		return elements.iterator();
	}

}
