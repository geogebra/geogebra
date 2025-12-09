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

package org.geogebra.common.euclidian;

import javax.annotation.CheckForNull;

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
	public double getLabelX() {
		return 0; // fallback for 3D
	}

	/**
	 * @return y-coord of the label
	 */
	public double getLabelY() {
		return 0; // fallback for 3D
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
	 * @return rectangle for stylebar
	 */
	public @CheckForNull GRectangle2D getBoundsForStylebarPosition() {
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
	 * By default create new drawable from view, overridden for composite
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
