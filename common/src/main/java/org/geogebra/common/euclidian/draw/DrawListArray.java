package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class for storing drawables includes in a DrawList
 * 
 * @author Mathieu
 * 
 */
public class DrawListArray extends ArrayList<DrawableND> {

	private static final long serialVersionUID = 1L;

	/** view */
	protected transient EuclidianViewInterfaceSlim view;

	/**
	 * common constructor
	 * 
	 * @param view
	 *            view
	 */
	public DrawListArray(EuclidianViewInterfaceSlim view) {

		this.view = view;
		// drawables = new ArrayList<DrawableND>();
	}

	/**
	 * Add the listElement's drawable
	 * 
	 * @param listElement
	 *            element to insert
	 * @param drawablePos
	 *            position to insert
	 * @param oldDrawableSize
	 *            old size of drawable list
	 * @param drawList
	 *            list in which we might find drawable for given geo
	 * @return false if the drawable == null
	 */
	public boolean addToDrawableList(GeoElement listElement, int drawablePos,
			int oldDrawableSize, DrawableND drawList) {
		DrawableND d = null;
		boolean inOldDrawableRange = drawablePos < oldDrawableSize;
		if (inOldDrawableRange) {
			// try to reuse old drawable
			DrawableND oldDrawable = get(drawablePos);
			if (oldDrawable.getGeoElement() == listElement) {
				d = oldDrawable;
				update(d);
			} else {
				d = getDrawable(oldDrawable, listElement, drawList);
			}
		} else {
			d = getDrawable(listElement, drawList);
		}

		if (d != null) {
			if (inOldDrawableRange) {
				set(drawablePos, d);
			} else {
				add(drawablePos, d);
			}
			return true;
		}
		return false;
	}

	/**
	 * For 3D: remove old drawable
	 * 
	 * @param oldDrawable
	 *            old drawable at same position
	 * @param listElement
	 *            list element
	 * @param drawList
	 *            list that wants to add this geo
	 * @return new drawable
	 */
	protected DrawableND getDrawable(DrawableND oldDrawable,
			GeoElement listElement, DrawableND drawList) {
		return getDrawable(listElement, drawList);
	}

	/**
	 * update the drawable
	 * 
	 * @param d
	 *            drawable to be updated
	 */
	protected void update(DrawableND d) {
		d.update();
	}

	/**
	 * Returns UPDATED drawable for the geo
	 * 
	 * @param listElement
	 *            geo
	 * @param drawList
	 *            list that wants to add this geo
	 * @return drawable for the geo
	 */
	private DrawableND getDrawable(GeoElement listElement, DrawableND drawList) {

		DrawableND d = view.getDrawableND(listElement);
		if (d == null) {
			// create a new drawable for geo
			d = createDrawableND(listElement);// view.createDrawableND(listElement);
			if (d != null)
				d.setCreatedByDrawList(drawList);
		} else {
			update(d);
		}
		return d;
	}

	/**
	 * @param listElement
	 *            geo
	 * @return the drawable create by the view for this geo
	 */
	protected DrawableND createDrawableND(GeoElement listElement) {
		return view.newDrawable(listElement);
	}

}
