package org.geogebra.common.kernel;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for views that need more info from the kernel
 *
 */

public interface ClientView extends View {
	/**
	 * Notify the client that all updates related to renaming an object are
	 * finished
	 */
	void renameUpdatesComplete();

	/**
	 * Notify client that a polygon and all it's related GeoElms are about the
	 * be created
	 */
	void batchAddStarted();

	/**
	 * Notify the client that a new polygon is complete.
	 * 
	 * @param polygon
	 *            The fully defined, new polygon
	 */
	void batchAddComplete(GeoElement polygon);

	/**
	 * Notify the client that a group of objects are being moved together. Using
	 * this, the client can ignore the individual updates from the base View
	 * interface when a group of elms are being moved together.
	 */
	void movingGeos();

	/**
	 * Update the client with the new location of the objects
	 * 
	 * @param elms
	 *            The list of GeoElements that were moved.
	 */
	void movedGeos(ArrayList<GeoElement> elms);

	/**
	 * Notifies the client that elements have been delete. This includes geoElms
	 * that get deleted when and object with dependents is deleted. It also
	 * notifies when only one elm is deleted, so it can be a full replacement
	 * for the View's remove api.
	 * 
	 * @param elms
	 *            The full list of elms that were removed due to an object with
	 *            dependents being deleted.
	 */
	void deleteGeos(ArrayList<GeoElement> elms);

	/**
	 * Notify client that objects are about to pasted into the construction.
	 * 
	 * @param pasteXml
	 *            XML of pasted construction
	 */
	void pasteElms(String pasteXml);

	/**
	 * Provide the full list of pasted elms to the client, in their finished
	 * state (e.g. no temporary MAGIC_STRING labels)
	 * 
	 * @param pastedElms
	 *            The full list of pasted elms, in their finished state.
	 */
	void pasteElmsComplete(ArrayList<GeoElement> pastedElms);

	/**
	 * Notifies client that the automatic animation is starting.
	 */
	void startAnimation();

	/**
	 * Notifies client that the automatic animation is stopping.
	 */
	void stopAnimation();

	void groupObjects(ArrayList<GeoElement> geos);

	void ungroupObjects(ArrayList<GeoElement> geos);

	/**
	 * Notifies client that an text element is locked for moving by other users.
	 *
	 * @param geo
	 *           The geoElement that is to be locked for movement
	 */
	void lockTextElement(GeoElement geo);

	/**
	 * Notifies client that an text element is unlocked for moving by other users.
	 *
	 * @param geo
	 *           The geoElement that is to be unlocked for movement
	 */
	void unlockTextElement(GeoElement geo);
}
