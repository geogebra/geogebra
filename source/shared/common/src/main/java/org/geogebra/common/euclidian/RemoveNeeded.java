package org.geogebra.common.euclidian;

/** interface for drawables that need to be removed explicitly */
public interface RemoveNeeded {
	/**
	 * This method is called once the drawable is not needed. It should remove
	 * all auxiliary objects of this drawable.
	 */
	public void remove();
}
