package org.geogebra.common.euclidian.plot;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTuple;

public class TupleNeighbours {
	private IntervalTuple left;
	private IntervalTuple current;
	private IntervalTuple right;

	/**
	 * Constructor
	 */
	public TupleNeighbours() {
		// unset
	}

	/**
	 *
	 * @param left neighbour tuple
	 * @param current neighbour tuple
	 * @param right neighbour tuple
	 */
	public TupleNeighbours(IntervalTuple left, IntervalTuple current, IntervalTuple right) {
		set(left, current, right);
	}

	/**
	 *
	 * @param left neighbour tuple
	 * @param current neighbour tuple
	 * @param right neighbour tuple
	 */
	public void set(IntervalTuple left, IntervalTuple current, IntervalTuple right) {
		this.left = left;
		this.current = current;
		this.right = right;
	}

	public boolean hasLeft() {
		return !(left == null || left.isUndefined());
	}

	public boolean hasRight() {
		return !(right == null || right.isUndefined());
	}

	public double leftXLow() {
		return left.x().getLow();
	}
	
	public double leftXHigh() {
		return left.x().getHigh();
	}
	
	public double leftYLow() {
		return left.y().getLow();
	}
	
	public double leftYHigh() {
		return left.y().getHigh();
	}
	
	public double currentXLow() {
		return current.x().getLow();
	}
	
	public double currentXHigh() {
		return current.x().getHigh();
	}
	
	public double currentYLow() {
		return current.y().getLow();
	}
	
	public double currentYHigh() {
		return current.y().getHigh();
	}

	public double rightXLow() {
		return right.x().getLow();
	}

	public double rightXHigh() {
		return right.x().getHigh();
	}

	public double rightYLow() {
		return right.y().getLow();
	}

	public double rightYHigh() {
		return right.y().getHigh();
	}

	public IntervalTuple left() {
		return left;
	}

	public IntervalTuple current() {
		return current;
	}

	public IntervalTuple right() {
		return right;
	}

	public boolean isRightUndefined() {
		return right.isUndefined();
	}

	@Override
	public String toString() {
		return toStringForCode();
	}

	private String toStringForCode() {
		return "TupleNeighbours neighbours = new TupleNeighbours(\n"
				+ tuple(left) + ", \n"
				+ tuple(current) + ", \n"
				+ tuple(right) + ");";
	}

	private String tuple(IntervalTuple tuple) {
		String result = "Tuples.";
		if (tuple.isUndefined()) {
			result += "undefined(" + comma(tuple.x()) + ") ";
		} else if (tuple.y().isInverted()) {
			result += "inverted(" + comma(tuple.x()) + ", " + comma(tuple.y()) + ") ";
		} else {
			result += "normal(" + comma(tuple.x()) + ", " + comma(tuple.y())  + ") ";
		}
		return result;
	}

	private String comma(Interval x) {
		return (x.getLow() + ", " + x.getHigh())
				.replace("Infinity", "Double.POSITIVE_INFINITY")
				.replace("-Infinity", "Double.POSITIVE_INFINITY");
	}

	public boolean isLeftWhole() {
		return left.y().isWhole();
	}

	public boolean isRightWhole() {
		return right.y().isWhole();
	}

	public boolean isLeftInfinite() {
		return left != null && left.y().hasInfinity();
	}

	public boolean isRightInfinite() {
		return right != null && right.y().hasInfinity();
	}
}
