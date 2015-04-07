package org.geogebra.common.kernel;

import java.util.ArrayList;

import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author mathieu
 * 
 *         Class that stores an ordered set of ConstructionElements, beginning
 *         with the minor ceID value, and going in the direction of the second
 *         minor ceID value.
 * 
 *         This allow to compare cycles between each others.
 * 
 *         Warning: the cycle supposes that there's no ConstructionElement that
 *         appears twice; but there's no test to ensure this when using add()
 *         method.
 * 
 *         Use setDirection() when the cycle is fed to ensure good direction
 *         comparison.
 *
 *         This class is used e.g. for describing points of polygons.
 */

public class ConstructionElementCycle extends ArrayList<ConstructionElement>
		implements Comparable<ConstructionElementCycle> {

	private static final long serialVersionUID = -880160148856127100L;

	/** minimum ceID of elements contained */
	private long minID = Long.MAX_VALUE;
	/** index of the minimum element */
	private int minIndex = 0;
	/** direction to the second minimum element */
	private int direction = 1;
	/** index to read through the cycle */
	private int cycleIndex;

	/**
	 * return the cycle constituted of P1 and P2
	 * 
	 * @param P1
	 *            first element
	 * @param P2
	 *            second element
	 * @return the cycle constituted of P1 and P2
	 */
	static final public ConstructionElementCycle SegmentDescription(
			GeoElement P1, GeoElement P2) {
		ConstructionElementCycle cycle = new ConstructionElementCycle();
		cycle.add(P1);
		cycle.add(P2);
		return cycle;
	}

	@Override
	public boolean add(ConstructionElement ce) {

		if (minID > ce.getID()) {
			minID = ce.getID();
			minIndex = size();
		}

		return super.add(ce);
	}

	/**
	 * set the direction to the second minimum element
	 */
	public void setDirection() {
		if (size() < 3)
			direction = 1;
		else {
			int before = minIndex - 1;
			if (before == -1)
				before = size() - 1;
			int after = minIndex + 1;
			if (after == size())
				after = 0;
			// if element before first minimum element is less than the element
			// after,
			// then the direction is ascendant, else the direction is descendant
			if (get(before).getID() < get(after).getID())
				direction = -1;
			else
				direction = 1;
		}
	}

	public int compareTo(ConstructionElementCycle cycle) {

		if (this == cycle)
			return 0;

		if (size() < cycle.size())
			return -1;
		if (size() > cycle.size())
			return 1;

		setCycleFirst();
		cycle.setCycleFirst();
		int diff = 0;
		// find the first two different elements, return the difference or 0
		for (int i = 0; diff == 0 && i < size(); i++)
			diff = getCycleNext().compareTo(cycle.getCycleNext());
		return diff;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConstructionElementCycle))
			return false;

		return compareTo((ConstructionElementCycle) obj) == 0;
	}

	private void setCycleFirst() {
		cycleIndex = minIndex;
	}

	private ConstructionElement getCycleNext() {

		ConstructionElement ret = get(cycleIndex);

		// update cycleIndex
		cycleIndex += direction;
		if (cycleIndex == -1)
			cycleIndex = size() - 1;
		else if (cycleIndex == size())
			cycleIndex = 0;

		return ret;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		setCycleFirst();
		for (int i = 0; i < size(); i++) {
			sb.append(getCycleNext().toString());
			sb.append(" - ");
		}

		return sb.toString();
	}

}
