package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.j2objc.annotations.Weak;

/**
 * Class for drawables in any dimension
 * 
 * @author Mathieu
 *
 */
public abstract class DrawableND {

	private boolean createdByDrawList = false;
	/** says if this drawable is visible when draw list is visible */
	private boolean createdByDrawListVisible = false;
	/** drawList that has created this */
	@Weak
	private DrawableND drawList;
	private boolean needsUpdate = false;

	/**
	 * sets that this has been created by the DrawList
	 * 
	 * @param drawList
	 *            true if created by DrawList
	 */
	public void setCreatedByDrawList(DrawableND drawList) {
		createdByDrawList = true;
		setCreatedByDrawListVisible(true);

		if (drawList.isCreatedByDrawListVisible()) {
			this.drawList = drawList.getDrawListCreator();
		} else {
			this.drawList = drawList;
		}

	}

	/**
	 * sets if this is visible when the DrawList is visible
	 * 
	 * @param flag
	 *            true to make this visible when the DrawList is visible
	 */
	public void setCreatedByDrawListVisible(boolean flag) {
		createdByDrawListVisible = flag;

	}

	/**
	 * @return if this has been created by a DrawList
	 */
	public boolean createdByDrawList() {
		return createdByDrawList;

	}

	/**
	 * @return if this is visible when the DrawList is visible
	 */
	public boolean isCreatedByDrawListVisible() {
		return createdByDrawListVisible;

	}

	/**
	 * @return the drawList that has created this (if one)
	 */
	public DrawableND getDrawListCreator() {
		return drawList;
	}

	/**
	 * @return the geo linked to this
	 */
	public abstract GeoElement getGeoElement();

	/**
	 * says that the drawable has to be updated in 2D : update it immediately in
	 * 3D : update at next frame
	 */
	public void setWaitForUpdate() {
		update();
	}

	/**
	 * says that the drawable has to be updated for visual style
	 * 
	 * @param prop
	 *            TODO
	 */
	public void setWaitForUpdateVisualStyle(GProperty prop) {
		setWaitForUpdate();
	}

	/**
	 * update it immediately
	 */
	public abstract void update();

	/**
	 * @return x-coord of the label
	 */
	public double getxLabel() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return y-coord of the label
	 */
	public double getyLabel() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param b
	 *            whether update is needed
	 */
	public void setNeedsUpdate(boolean b) {
		this.needsUpdate = b;
	}

	/**
	 * @return whether update is needed
	 */
	public boolean needsUpdate() {
		return this.needsUpdate;
	}

	/**
	 * @return true if trace is on for the geo
	 */
	public abstract boolean isTracing();

	/**
	 * @return recctangle for stylebar
	 */
	public GRectangle2D getBoundsForStylebarPosition() {
		return null;
	}

	/**
	 * @return whether this is a 3D drawable
	 */
	public boolean is3D() {
		return false;
	}

	/**
	 * Checks for broken compatibility (eg if inequality changes to function,
	 * drawable is invalid)
	 * 
	 * @return whether the drawable still fits the geo
	 */
	public boolean isCompatibleWithGeo() {
		return true;
	}

	/**
	 * By default create new drawable from view, overriden for composite
	 * drawable to avoid deep nesting.
	 * 
	 * @param listElement
	 *            element
	 * @return drawable for element
	 */
	public abstract DrawableND createDrawableND(GeoElement listElement);

	/**
	 * @param rect
	 *            clipping rectangle for partial hits
	 */
	public abstract void setPartialHitClip(GRectangle rect);

	/**
	 * @return partialHitClip (used for strokes to select part of stroke)
	 */
	public abstract GRectangle getPartialHitClip();
}
