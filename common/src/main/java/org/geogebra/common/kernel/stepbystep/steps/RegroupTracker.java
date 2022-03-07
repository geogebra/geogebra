package org.geogebra.common.kernel.stepbystep.steps;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.steptree.StepNode;

public class RegroupTracker {

	private int colorTracker;

	private boolean decimalSimplify;
	private boolean changed;

	private Set<Mark> marks;

	public enum MarkType {
		EXPAND, // marked for expansion
		FACTOR, // marked for factoring common
		PERIOD, // trigonometric function with period extracted
		EXPAND_FRAC, // marked for expansion (in the fraction expansion sense)
	}

	private static class Mark {
		private StepNode marked;
		private MarkType type;

		Mark(StepNode marked, MarkType type) {
			this.marked = marked;
			this.type = type;

		}

		@Override
		public int hashCode() {
			int result = marked != null ? marked.hashCode() : 0;
			result = 31 * result + (type != null ? type.hashCode() : 0);
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Mark) {
				return type == ((Mark) o).type && marked.equals(((Mark) o).marked);
			}

			return false;
		}
	}

	RegroupTracker() {
		this.colorTracker = 1;
	}

	void addMark(StepNode toMark, MarkType type) {
		if (marks == null) {
			marks = new HashSet<>();
		}

		changed |= marks.add(new Mark(toMark, type));
	}

	boolean isMarked(StepNode toCheck, MarkType type) {
		return marks != null && marks.remove(new Mark(toCheck, type));
	}

	RegroupTracker setDecimalSimplify() {
		this.decimalSimplify = true;
		return this;
	}

	boolean isDecimalSimplify() {
		return decimalSimplify;
	}

	boolean stepAdded() {
		return colorTracker > 1;
	}

	boolean wasChanged() {
		return changed || colorTracker > 1;
	}

	int incColorTracker() {
		return colorTracker++;
	}

	public int getColorTracker() {
		return colorTracker;
	}

	void setColorTracker(int colorTracker) {
		this.colorTracker = colorTracker;
	}

	void resetTracker() {
		changed = false;
		this.colorTracker = 1;
	}
}
