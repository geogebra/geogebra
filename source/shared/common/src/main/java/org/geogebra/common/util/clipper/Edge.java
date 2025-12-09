/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util.clipper;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.clipper.Clipper.ClipType;
import org.geogebra.common.util.clipper.Clipper.Direction;
import org.geogebra.common.util.clipper.Clipper.PolyFillType;
import org.geogebra.common.util.clipper.Clipper.PolyType;
import org.geogebra.common.util.clipper.Point.DoublePoint;

class Edge {
	// private final LongPoint bot;
	private final DoublePoint bot;

	// private final LongPoint current;
	private final DoublePoint current;

	// private final LongPoint top;
	private final DoublePoint top;

	// private final LongPoint delta;
	private final DoublePoint delta;

	double deltaX;

	PolyType polyType;

	Edge.Side side;

	int windDelta; // 1 or -1 depending on winding direction

	int windCnt;
	int windCnt2; // winding count of the opposite polytype
	int outIdx;
	Edge next;
	Edge prev;
	Edge nextInLML;
	Edge nextInAEL;
	Edge prevInAEL;
	Edge nextInSEL;
	Edge prevInSEL;

	protected final static int SKIP = -2;

	protected final static int UNASSIGNED = -1;

	protected final static double HORIZONTAL = -3.4E+38;

	static enum Side {
		LEFT, RIGHT
	}

	static boolean doesE2InsertBeforeE1(Edge e1, Edge e2) {
		if (e2.current.getX() == e1.current.getX()) {
			if (e2.top.getY() > e1.top.getY()) {
				return e2.top.getX() < topX(e1, e2.top.getY());
			}
			return e1.top.getX() > topX(e2, e1.top.getY());
		}
		return e2.current.getX() < e1.current.getX();
	}

	static boolean slopesEqual(Edge e1, Edge e2) {
		return DoubleUtil.isEqual(e1.getDelta().getY() * e2.getDelta().getX(),
				e1.getDelta().getX() * e2.getDelta().getY());

	}

	static void swapPolyIndexes(Edge edge1, Edge edge2) {
		final int outIdx = edge1.outIdx;
		edge1.outIdx = edge2.outIdx;
		edge2.outIdx = outIdx;
	}

	static void swapSides(Edge edge1, Edge edge2) {
		final Edge.Side side = edge1.side;
		edge1.side = edge2.side;
		edge2.side = side;
	}

	/**
	 * modified to be compatible with double
	 */
	static double topX(Edge edge, double currentY) {
		if (currentY == edge.getTop().getY()) {
			return edge.getTop().getX();
		}
		return edge.getBot().getX()
				+ edge.deltaX * (currentY - edge.getBot().getY());
	}

	/**
	 * modified to be compatible with double
	 */
	public Edge() {
		delta = new DoublePoint();
		top = new DoublePoint();
		bot = new DoublePoint();
		current = new DoublePoint();
	}

	public Edge findNextLocMin() {
		Edge e = this;
		Edge e2;
		boolean go = true;
		while (go) {
			while (!e.bot.equals(e.prev.bot) || e.current.equals(e.top)) {
				e = e.next;
			}
			if (!isEdgeHorizontal(e.deltaX)
					&& !isEdgeHorizontal(e.prev.deltaX)) {
				break;
			}
			while (isEdgeHorizontal(e.prev.deltaX)) {
				e = e.prev;
			}
			e2 = e;
			while (isEdgeHorizontal(e.deltaX)) {
				e = e.next;
			}
			if (e.top.getY() == e.prev.bot.getY()) {
				continue; // ie just an intermediate horz.
			}
			if (e2.prev.bot.getX() < e.bot.getX()) {
				e = e2;
			}
			go = false;
		}
		return e;
	}

	/**
	 * modified to be compatible with double
	 */
	public DoublePoint getBot() {
		return bot;
	}

	/**
	 * modified to be compatible with double
	 */
	public DoublePoint getCurrent() {
		return current;
	}

	/**
	 * modified to be compatible with double
	 */
	public DoublePoint getDelta() {
		return delta;
	}

	/**
	 * modified to be compatible with double
	 */
	public DoublePoint getTop() {
		return top;
	}

	public Edge getMaximaPair() {
		Edge result = null;
		if (next.top.equals(top) && next.nextInLML == null) {
			result = next;
		} else if (prev.top.equals(top) && prev.nextInLML == null) {
			result = prev;
		}
		if (result != null && (result.outIdx == Edge.SKIP
				|| result.nextInAEL == result.prevInAEL
						&& !result.isHorizontal())) {
			return null;
		}
		return result;
	}

	public Edge getNextInAEL(Direction direction) {
		return direction == Direction.LEFT_TO_RIGHT ? nextInAEL : prevInAEL;
	}

	public boolean isContributing(PolyFillType clipFillType,
			PolyFillType subjFillType, ClipType clipType) {

		PolyFillType pft, pft2;
		if (polyType == PolyType.SUBJECT) {
			pft = subjFillType;
			pft2 = clipFillType;
		} else {
			pft = clipFillType;
			pft2 = subjFillType;
		}

		switch (pft) {
		case EVEN_ODD:
			// return false if a subj line has been flagged as inside a subj
			// polygon
			if (windDelta == 0 && windCnt != 1) {
				return false;
			}
			break;
		case NON_ZERO:
			if (Math.abs(windCnt) != 1) {
				return false;
			}
			break;
		case POSITIVE:
			if (windCnt != 1) {
				return false;
			}
			break;
		default: // PolyFillType.pftNegative
			if (windCnt != -1) {
				return false;
			}
			break;
		}

		switch (clipType) {
		case INTERSECTION:
			switch (pft2) {
			case EVEN_ODD:
			case NON_ZERO:
				return windCnt2 != 0;
			case POSITIVE:
				return windCnt2 > 0;
			default:
				return windCnt2 < 0;
			}
		case UNION:
			switch (pft2) {
			case EVEN_ODD:
			case NON_ZERO:
				return windCnt2 == 0;
			case POSITIVE:
				return windCnt2 <= 0;
			default:
				return windCnt2 >= 0;
			}
		case DIFFERENCE:
			if (polyType == PolyType.SUBJECT) {
				switch (pft2) {
				case EVEN_ODD:
				case NON_ZERO:
					return windCnt2 == 0;
				case POSITIVE:
					return windCnt2 <= 0;
				default:
					return windCnt2 >= 0;
				}
			}
			switch (pft2) {
			case EVEN_ODD:
			case NON_ZERO:
				return windCnt2 != 0;
			case POSITIVE:
				return windCnt2 > 0;
			default:
				return windCnt2 < 0;
			}
		case XOR:
			if (windDelta == 0) {
				switch (pft2) {
				case EVEN_ODD:
				case NON_ZERO:
					return windCnt2 == 0;
				case POSITIVE:
					return windCnt2 <= 0;
				default:
					return windCnt2 >= 0;
				}
			}
			return true;
		}
		return true;
	}

	public boolean isEvenOddAltFillType(PolyFillType clipFillType,
			PolyFillType subjFillType) {
		if (polyType == PolyType.SUBJECT) {
			return clipFillType == PolyFillType.EVEN_ODD;
		}
		return subjFillType == PolyFillType.EVEN_ODD;
	}

	public boolean isEvenOddFillType(PolyFillType clipFillType,
			PolyFillType subjFillType) {
		if (polyType == PolyType.SUBJECT) {
			return subjFillType == PolyFillType.EVEN_ODD;
		}
		return clipFillType == PolyFillType.EVEN_ODD;
	}

	public boolean isHorizontal() {
		return delta.getY() == 0;
	}

	public boolean isIntermediate(double y) {
		return top.getY() == y && nextInLML != null;
	}

	public boolean isMaxima(double Y) {
		return top.getY() == Y && nextInLML == null;
	}

	public void reverseHorizontal() {
		// swap horizontal edges' top and bottom x's so they follow the natural
		// progression of the bounds - ie so their xbots will align with the
		// adjoining lower edge. [Helpful in the ProcessHorizontal() method.]
		// long temp = top.getX();
		double temp = top.getX();
		top.setX(bot.getX());
		bot.setX(temp);

		temp = top.getZ();
		top.setZ(bot.getZ());
		bot.setZ(temp);

	}

	/**
	 * modified to be compatible with double
	 */
	public void setBot(DoublePoint bot) {
		this.bot.set(bot);
	}

	/**
	 * modified to be compatible with double
	 */
	public void setCurrent(DoublePoint current) {
		this.current.set(current);
	}

	/**
	 * modified to be compatible with double
	 */
	public void setTop(DoublePoint top) {
		this.top.set(top);
	}

	@Override
	public String toString() {
		return "TEdge [Bot=" + bot + ", Curr=" + current + ", Top=" + top
				+ ", Delta=" + delta + ", Dx=" + deltaX + ", PolyTyp=" + polyType
				+ ", Side=" + side + ", WindDelta=" + windDelta + ", WindCnt="
				+ windCnt + ", WindCnt2=" + windCnt2 + ", OutIdx=" + outIdx
				+ ", Next=" + next + ", Prev=" + prev + ", NextInLML="
				+ nextInLML + ", NextInAEL=" + nextInAEL + ", PrevInAEL="
				+ prevInAEL + ", NextInSEL=" + nextInSEL + ", PrevInSEL="
				+ prevInSEL + "]";
	}

	public void updateDeltaX() {

		delta.setX(top.getX() - bot.getX());
		delta.setY(top.getY() - bot.getY());
		if (delta.getY() == 0) {
			deltaX = Edge.HORIZONTAL;
		} else {
			deltaX = delta.getX() / delta.getY();
		}
	}

	private static boolean isEdgeHorizontal(double d) {
		return MyDouble.exactEqual(d, Edge.HORIZONTAL);
	}

}
