/*
 * Created on 12.11.2005
 *
 */
package geogebra.kernel;

/**
 * @author HOHENWARTER
 *
 * sorted list of point pairs (sorted by distance)
 * used in AlgoInteresectConics and AlgoIntersecLineConic
 */
public class PointPairList {
	private PointPair head;

	final public boolean isEmpty() {
		return head == null;
	}

	final void clear() {
		head = null;
	}

	/**
	 *  Inserts pair (indexD, indexQ) in ascending order of distance
	 * where alive points come before others and points Q on path come before others.
	 */
	final void insertPointPair(int indexD, boolean isPalive, int indexQ, boolean isQonPath, double distance) {
		PointPair newPair = new PointPair(indexD, isPalive, indexQ, isQonPath, distance);

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
	 * Checks a < b. Ascending ordering by isPalive and isQonPath and distance, where isPalive == true  comes
	 * before isPalive == false and isQonPath == true comes
	 * before isQonPath == false.	 
	 */
	private boolean smallerThan(PointPair a, PointPair b) {
		if (a.isPalive) {
			if (b.isPalive) // both are alive
				return smallerThan2(a, b);
			else // a alive, b not
				return true;
		} else { // a not alive, b is alive: a > b
			if (b.isPalive)
				return false;
			else // both not alive
				return smallerThan2(a, b);
		}
	}
	
	/**
	 * Checks a < b. Ascending ordering by isQonPath and distance, where isQonPath == true comes
	 * before isQonPath == false.	 
	 */
	private boolean smallerThan2(PointPair a, PointPair b) {
		if (a.isQonPath) {
			if (b.isQonPath) // both on path
				return (a.dist < b.dist);
			else // a on path, b not on path: a < b
				return true;
		} else { // a not on path, b on path: a > b
			if (b.isQonPath)
				return false;
			else // both not on path
				return (a.dist < b.dist);
		}
	}

	/**
	 * Removes all PointPairs where indexP == pair.indexP or 
	 * indexQ == pair.indexQ
	 */
	final void removeAllPairs(PointPair pair) {
		if (head == null) return;
		while ( head.indexP == pair.indexP || 
				head.indexQ == pair.indexQ) {
			head = head.next;
			if (head == null) return;
		}
		
		PointPair prevPair = head, currentPair = head.next;
		while (currentPair != null) {
			if (currentPair.indexP == pair.indexP || 
				currentPair.indexQ == pair.indexQ) {
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

	final PointPair getHead() {
		return head;
	}

	/*
	final public String toString() {
		StringBuilder sb = new StringBuilder();
		PointPair currentPair = head;
		while (currentPair != null) {
			sb.append(currentPair);
			currentPair = currentPair.next;
		}
		return sb.toString();
	}
	*/
	

}

//point pair (i, j, dist) stores the point 
//pair D_i, Q_j and their distance
class PointPair {
	int indexP;
	boolean isPalive;
	int indexQ;	
	boolean isQonPath;
	double dist;
	
	PointPair next;
	
	PointPair(int i, boolean isPalive, int j, boolean isQjOnPath, double distance) {
		indexP = i;
		this.isPalive = isPalive;
		indexQ = j;
		isQonPath = isQjOnPath;
		dist = distance;
	}
	
	/*
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(indexP);
		sb.append(", ");
		sb.append(isPalive);
		sb.append(", ");
		sb.append(indexQ);
		sb.append(", ");
		sb.append(isQonPath);
		sb.append(", ");
		sb.append(dist);
		sb.append(")\n");
		return sb.toString();
	}*/
} 

