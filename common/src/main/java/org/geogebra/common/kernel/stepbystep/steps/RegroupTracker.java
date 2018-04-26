package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.steptree.StepNode;

import java.util.HashSet;
import java.util.Set;

public class RegroupTracker {

	private int colorTracker;

	private boolean decimalSimplify;
	private boolean weakFactor;
	private boolean integerFractions;
	private boolean strongExpand;
	private boolean changed;

	private Set<Mark> marks;

	public enum MarkType {
		EXPAND,			// marked for expansion
		ROOT,			// marked for being under square root
		FACTOR 			// marked for factoring common
	}

	private static class Mark {
		private StepNode marked;
		private MarkType type;

		public Mark(StepNode marked, MarkType type) {
			this.marked = marked;
			this.type = type;

		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Mark) {
				return type == ((Mark) o).type && marked.equals(((Mark) o).marked);
			}

			return false;
		}

		@Override
		public int hashCode() {
			int result = marked != null ? marked.hashCode() : 0;
			result = 31 * result + (type != null ? type.hashCode() : 0);
			return result;
		}
	}

	public RegroupTracker() {
		this.colorTracker = 1;
		this.integerFractions = true;
	}

	public void addMark(StepNode toMark, MarkType type) {
		if (marks == null) {
			marks = new HashSet<>();
		}

		changed |= marks.add(new Mark(toMark, type));
	}

	public boolean isMarked(StepNode toCheck, MarkType type) {
		return marks != null && marks.remove(new Mark(toCheck, type));
	}

	public RegroupTracker setWeakFactor() {
		this.weakFactor = true;
		return this;
	}

	public RegroupTracker unsetIntegerFractions() {
		this.integerFractions = false;
		return this;
	}

	public RegroupTracker setStrongExpand(boolean strongExpand) {
		this.strongExpand = strongExpand;
		return this;
	}

	public RegroupTracker setDecimalSimplify() {
		this.decimalSimplify = true;
		return this;
	}

	public boolean getExpandSettings() {
		return strongExpand;
	}

	public boolean isWeakFactor() {
		return weakFactor;
	}

	public boolean isIntegerFractions() {
		return integerFractions;
	}

	public boolean isDecimalSimplify() {
		return decimalSimplify;
	}

	public boolean wasChanged() {
		return colorTracker > 1;
	}

	public boolean wasChanged2() {
		return changed || colorTracker > 1;
	}

	public int incColorTracker() {
		return colorTracker++;
	}

	public int getColorTracker() {
		return colorTracker;
	}

	public void setColorTracker(int colorTracker) {
		this.colorTracker = colorTracker;
	}

	public void resetTracker() {
		changed = false;
		this.colorTracker = 1;
	}
}
