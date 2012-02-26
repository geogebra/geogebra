/*
 * Created on 12.11.2005
 *
 */
package geogebra.common.kernel;

/**
 * @author HOHENWARTER
 * 
 *         sorted list of point pairs (sorted by distance) used in
 *         AlgoInteresectConics and AlgoIntersecLineConic
 */
public class PointPairList {
	private PointPair head;

	/**
	 * @return whether this list is empty
	 */
	final public boolean isEmpty() {
		return head == null;
	}

	/**
	 * Clears the list
	 */
	public final void clear() {
		head = null;
	}

	/**
	 * Inserts pair (indexD, indexQ) in ascending order of distance where alive
	 * points come before others and points Q on path come before others.
	 * @param indexD index of point in D
	 * @param isPalive tru if point in P is alive
	 * @param indexQ  index of point in Q
	 * @param isQonPath true if point in Q is on path
	 * @param distance distance between point in D and point in Q
	 */
	public final void insertPointPair(int indexD, boolean isPalive, int indexQ,
			boolean isQonPath, double distance) {
		PointPair newPair = new PointPair(indexD, isPalive, indexQ, isQonPath,
				distance);

		// insert as head
		if (head == null || smallerThan(newPair, head)) {
			newPair.next = head;
			head = newPair;
			return;
		}

		PointPair currentPair = head;
		while (currentPair.next != null) {
			if (smallerThan(newPair, currentPair.next))
				break;
			currentPair = currentPair.next;
		}
		// add after currentPair
		newPair.next = currentPair.next;
		currentPair.next = newPair;
	}

	/**
	 * Checks a < b. Ascending ordering by isPalive and isQonPath and distance,
	 * where isPalive == true comes before isPalive == false and isQonPath ==
	 * true comes before isQonPath == false.
	 */
	private static boolean smallerThan(PointPair a, PointPair b) {
		if (a.isPalive) {
			if (b.isPalive) {
				// both are alive
				return smallerThan2(a, b);
			}
			// a alive, b not
			return true;
		}
		// a not alive, b is alive: a > b
		if (b.isPalive) {
			return false;
		}
		// both not alive
		return smallerThan2(a, b);
	}

	/**
	 * Checks a < b. Ascending ordering by isQonPath and distance, where
	 * isQonPath == true comes before isQonPath == false.
	 */
	private static boolean smallerThan2(PointPair a, PointPair b) {
		if (a.isQonPath) {
			if (b.isQonPath) {
				// both on path
				return (a.dist < b.dist);
			}
			// a on path, b not on path: a < b
			return true;
		}
		// a not on path, b on path: a > b
		if (b.isQonPath) {
			return false;
		}
		// both not on path
		return (a.dist < b.dist);
	}

	/**
	 * Removes all PointPairs where indexP == pair.indexP or indexQ ==
	 * pair.indexQ
	 * @param pair pair such that pairs with one same point must be removed
	 */
	public final void removeAllPairs(PointPair pair) {
		if (head == null)
			return;
		while (head.indexP == pair.indexP || head.indexQ == pair.indexQ) {
			head = head.next;
			if (head == null)
				return;
		}

		PointPair prevPair = head, currentPair = head.next;
		while (currentPair != null) {
			if (currentPair.indexP == pair.indexP
					|| currentPair.indexQ == pair.indexQ) {
				// remove currentPair
				prevPair.next = currentPair.next;
				currentPair = currentPair.next;
			} else {
				// move on to next pair
				prevPair = currentPair;
				currentPair = currentPair.next;
			}
		}
	}

	/**
	 * @return first pair in the list
	 */
	public final PointPair getHead() {
		return head;
	}

	/*
	 * final public String toString() { StringBuilder sb = new StringBuilder();
	 * PointPair currentPair = head; while (currentPair != null) {
	 * sb.append(currentPair); currentPair = currentPair.next; } return
	 * sb.toString(); }
	 */

}
