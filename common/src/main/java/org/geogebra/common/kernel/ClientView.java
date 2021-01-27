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
	public void renameUpdatesComplete();

	/**
	 * Notify client that a polygon and all it's related GeoElms are about the
	 * be created
	 */
	public void addingPolygon();

	/**
	 * Notify the client that a new polygon is complete.
	 * 
	 * @param polygon
	 *            The fully defined, new polygon
	 */
	public void addPolygonComplete(GeoElement polygon);

	/**
	 * Notify the client that a group of objects are being moved together. Using
	 * this, the client can ignore the individual updates from the base View
	 * interface when a group of elms are being moved together.
	 */
	public void movingGeos();

	/**
	 * Update the client with the new location of the objects
	 * 
	 * @param elms
	 *            The list of GeoElements that were moved.
	 */
	public void movedGeos(ArrayList<GeoElement> elms);

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
	public void deleteGeos(ArrayList<GeoElement> elms);

	/**
	 * Notify client that objects are about to pasted into the construction.
	 * 
	 * @param pasteXml
	 *            XML of pasted construction
	 */
	public void pasteElms(String pasteXml);

	/**
	 * Provide the full list of pasted elms to the client, in their finished
	 * state (e.g. no temporary MAGIC_STRING labels)
	 * 
	 * @param pastedElms
	 *            The full list of pasted elms, in their finished state.
	 */
	public void pasteElmsComplete(ArrayList<GeoElement> pastedElms);

	/**
	 * Notifies client that the animation for a geoElement is starting.
	 *
	 * @param geo
	 * 			The geoElement that is to be animated
	 */
	public void startAnimation(GeoElement geo);

	/**
	 * Notifies client that the animation for a geoElement is stopping.
	 *
	 * @param geo
	 * 			The animated geoElement
	 */
	public void stopAnimation(GeoElement geo);

	public void groupObjects(ArrayList<GeoElement> geos);

	public void ungroupObjects(ArrayList<GeoElement> geos);
}
