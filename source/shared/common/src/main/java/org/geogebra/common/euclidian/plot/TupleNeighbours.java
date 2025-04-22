package org.geogebra.common.euclidian.plot;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

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

	/**
	 * @return whether left exists and is defined
	 */
	public boolean hasLeft() {
		return !(left == null || left.isUndefined());
	}

	/**
	 * @return whether right exists and is defined
	 */
	public boolean hasRight() {
		return !(right == null || right.isUndefined());
	}

	/**
	 * @return left x low
	 */
	public double leftXLow() {
		return left.x().getLow();
	}

	/**
	 * @return left x high
	 */
	public double leftXHigh() {
		return left.x().getHigh();
	}

	/**
	 * @return left y low
	 */
	public double leftYLow() {
		return left.y().getLow();
	}

	/**
	 * @return left y high
	 */
	public double leftYHigh() {
		return left.y().getHigh();
	}

	/**
	 * @return current x low
	 */
	public double currentXLow() {
		return current.x().getLow();
	}

	/**
	 * @return current x high
	 */
	public double currentXHigh() {
		return current.x().getHigh();
	}

	/**
	 * @return current y low
	 */
	public double currentYLow() {
		return current.y().getLow();
	}

	/**
	 * @return current y high
	 */
	public double currentYHigh() {
		return current.y().getHigh();
	}

	/**
	 * @return right x low
	 */
	public double rightXLow() {
		return right.x().getLow();
	}

	/**
	 * @return right x high
	 */
	public double rightXHigh() {
		return right.x().getHigh();
	}

	/**
	 * @return right y low
	 */
	public double rightYLow() {
		return right.y().getLow();
	}

	/**
	 * @return right y high
	 */
	public double rightYHigh() {
		return right.y().getHigh();
	}

	/**
	 * @return left
	 */
	public IntervalTuple left() {
		return left;
	}

	/**
	 * @return current
	 */
	public IntervalTuple current() {
		return current;
	}

	/**
	 * @return right
	 */
	public IntervalTuple right() {
		return right;
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
		if (tuple == null) {
			return "null";
		}
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

	/**
	 * @return whether left is infinite
	 */
	public boolean isLeftInfinite() {
		return left != null && left.y().hasInfinity();
	}

	/**
	 * @return whether right is infinite
	 */
	public boolean isRightInfinite() {
		return right != null && right.y().hasInfinity();
	}
}
