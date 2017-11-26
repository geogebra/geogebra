package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.main.Localization;

public class StepSet extends StepNode implements Iterable<StepNode> {

	private Set<StepNode> elements;

	public StepSet(StepNode... elements) {
		this.elements = new HashSet<StepNode>(Arrays.asList(elements));
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
	public StepSet deepCopy() {
		StepSet ss = new StepSet();
		for (StepNode sn : elements) {
			ss.addElement(sn.deepCopy());
		}
		
		return ss;
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		return null;
	}

	@Override
	public Iterator iterator() {
		return elements.iterator();
	}

}
