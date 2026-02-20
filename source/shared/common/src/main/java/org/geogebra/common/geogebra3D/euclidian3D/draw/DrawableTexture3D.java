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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;

public abstract class DrawableTexture3D {

	/** says if the label is visible */
	private boolean isVisible;
	/** width and height of the text */
	protected int height;
	protected int width;
	/** width and height of the texture */
	protected int height2;
	protected int width2;

	/** says it wait for reset */
	protected boolean waitForReset;

	/** index of the texture used for this label */
	protected int textureIndex = -1;
	protected int textIndex = -1;
	protected int pickingIndex = -1;
	protected int backgroundIndex = -1;

	protected int pickingX;
	protected int pickingY;
	protected int pickingW;
	protected int pickingH;

	protected double drawX;
	protected double drawY;
	protected double drawZ;

	/** current view where this label is drawn */
	protected EuclidianView3D view;
	/** origin of the label (left-bottom corner) */
	protected Coords origin;

	protected final float[] labelOrigin = new float[3];
	protected final Coords vScreen = new Coords(3);
	/** says if there's an anchor to do */
	private boolean anchor;

	/** x, y, z offset */
	protected float xOffset;
	protected float yOffset;
	protected float zOffset;
	protected float xOffset2;
	protected float yOffset2;

	private CoordMatrix4x4 positionMatrix;

	/**
	 *
	 * @return label width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 *
	 * @return label height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 *
	 * @return label width for texture (power of 2)
	 */
	public int getWidthPowerOfTwo() {
		return width2;
	}

	/**
	 *
	 * @return label height for texture (power of 2)
	 */
	public int getHeightPowerOfTwo() {
		return height2;
	}

	/**
	 * set dimension for picking
	 *
	 * @param x
	 *            bottom-left x position
	 * @param y
	 *            bottom-left y position
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setPickingDimension(int x, int y, int w, int h) {
		pickingX = x;
		pickingY = y;
		pickingW = w;
		pickingH = h;
	}

	/**
	 * @return whether the drawable can be picked
	 */
	public abstract boolean isPickable();

	/**
	 * set texture index
	 *
	 * @param i
	 *            index
	 */
	public void setTextureIndex(int i) {
		textureIndex = i;
	}

	/**
	 * @return texture indexl
	 *
	 */
	public int getTextureIndex() {
		return textureIndex;
	}

	/**
	 * @return true if this wait for reset
	 */
	public boolean waitForReset() {
		return waitForReset;
	}

	/**
	 * @param scale
	 *            scale
	 */
	public void scaleRenderingDimensions(float scale) {
		width2 = (int) (width2 * scale);
		height2 = (int) (height2 * scale);
		pickingX = (int) (pickingX * scale);
		pickingY = (int) (pickingY * scale);
		pickingW = (int) (pickingW * scale);
		pickingH = (int) (pickingH * scale);
	}

	/**
	 * set power of 2 width and height
	 *
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setDimensionPowerOfTwo(int w, int h) {
		width2 = w;
		height2 = h;
	}

	/**
	 * sets the anchor
	 *
	 * @param flag
	 *            anchor
	 */
	public void setAnchor(boolean flag) {
		anchor = flag;
	}

	/**
	 * update draw position
	 */
	public void updateDrawPosition() {

		if (origin == null) {
			return;
		}

		vScreen.setMul(view.getToScreenMatrix(), origin);

		origin.get3ForGL(labelOrigin);
		labelOrigin[0] = (float) (labelOrigin[0] * view.getXscale());
		labelOrigin[1] = (float) (labelOrigin[1] * view.getYscale());
		labelOrigin[2] = (float) (labelOrigin[2] * view.getZscale());

		if (!view.isXREnabled() || !anchor) {
			drawX = (int) (vScreen.getX() + xOffset);
			if (anchor && xOffset < 0) {
				drawX -= width / getFontScale();
			} else {
				drawX += xOffset2 / getFontScale();
			}

			drawY = (int) (vScreen.getY() + yOffset);
			if (anchor && yOffset < 0) {
				drawY -= height / getFontScale();
			} else {
				drawY += yOffset2 / getFontScale();
			}

			drawZ = (int) (vScreen.getZ() + zOffset);
		}
	}

	/**
	 * update label for view (update screen position)
	 *
	 * @param renderer
	 *            GL renderer
	 */
	public void updatePosition(Renderer renderer) {
		updateDrawPosition();

		if (origin == null) {
			renderer.getGeometryManager().remove(textIndex);
			textIndex = -1;
			renderer.getGeometryManager().remove(pickingIndex);
			pickingIndex = -1;
			renderer.getGeometryManager().remove(backgroundIndex);
			backgroundIndex = -1;
			return;
		}

		int old = textIndex;
		if (view.isXRDrawing()) {
			if (!view.isXREnabled() || !anchor) {
				textIndex = drawRectangle(renderer, 0, 0, 0,
						width2 / getFontScale(), height2 / getFontScale(), textIndex);
			} else {
				double w = width2 / getFontScale();
				double h = height2 / getFontScale();
				textIndex = drawRectangle(renderer, -(pickingX + pickingW / 2d) / getFontScale(),
						-(pickingY + pickingH / 2d) / getFontScale(), 0,
						w, h, textIndex);
			}
		} else {
			textIndex = drawRectangle(renderer, drawX, drawY, drawZ, width2 / getFontScale(),
					height2 / getFontScale(), textIndex);
		}
		renderer.getGeometryManager().remove(old);

		old = pickingIndex;
		pickingIndex = drawRectangle(renderer,
				drawX + pickingX / getFontScale(),
				drawY + pickingY / getFontScale(), drawZ,
				pickingW / getFontScale(), pickingH / getFontScale(),
				pickingIndex);
		renderer.getGeometryManager().remove(old);

		old = backgroundIndex;
		if (view.isXRDrawing()) {
			backgroundIndex = drawRectangle(renderer, 0, 0, 0,
					width / getFontScale(),
					height / getFontScale(), backgroundIndex);
		} else {
			backgroundIndex = drawRectangle(renderer, drawX, drawY, drawZ,
					width / getFontScale(), height / getFontScale(),
					backgroundIndex);
		}
		renderer.getGeometryManager().remove(old);

		// Log.debug("textIndex: "+textIndex+", pickingIndex: "+pickingIndex+",
		// backgroundIndex: "+backgroundIndex);
	}

	/**
	 * remove from GPU memory
	 */
	public void removeFromGL() {
		Manager manager = view.getRenderer().getGeometryManager();
		manager.remove(textIndex);
		manager.remove(pickingIndex);
		manager.remove(backgroundIndex);
		view.getRenderer().getTextures().removeTexture(textureIndex);
	}

	/**
	 * set the label to be reset
	 */
	public void setWaitForReset() {
		waitForReset = true;

		textIndex = -1;
		pickingIndex = -1;
		backgroundIndex = -1;
	}

	/**
	 * draws the label
	 *
	 * @param renderer
	 *            renderer
	 * @param forPicking
	 *            says if it's for picking
	 */
	public void draw(Renderer renderer, boolean forPicking) {
		if (!isVisible) {
			return;
		}

		if (textureIndex == -1) {
			return;
		}

		if (view.isXRDrawing()) {
			if (positionMatrix == null) {
				positionMatrix = new CoordMatrix4x4();
			}
			positionMatrix.set(renderer.getUndoRotationMatrixAR());
			Coords origin = positionMatrix.getOrigin();
			origin.setX(drawX);
			origin.setY(drawY);
			origin.setZ(drawZ);
			renderer.getRendererImpl().setMatrixView(positionMatrix);
		}

		renderer.getRendererImpl().setLabelOrigin(labelOrigin);
		renderer.getRendererImpl().setLabelLocation(
				new float[]{(float) drawX, (float) drawY, (float) drawZ});
		Coords backgroundColor = getBackgroundColor();
		if (forPicking) {
			// renderer.getGeometryManager().rectangle(drawX + pickingX, drawY +
			// pickingY, drawZ, pickingW, pickingH);
			if (backgroundColor != null) {
				renderer.getGeometryManager().draw(backgroundIndex);
			} else {
				renderer.getGeometryManager().draw(pickingIndex);
			}
		} else {

			// draw background
			if (backgroundColor != null) {
				renderer.setColor(backgroundColor);
				renderer.getRendererImpl().disableTextures();
				// renderer.getGeometryManager().rectangle(drawX, drawY, drawZ,
				// width, height);
				renderer.getGeometryManager().draw(backgroundIndex);

			}

			// draw text
			drawContent(renderer);

		}
	}

	protected abstract Coords getBackgroundColor();

	/**
	 * sets the visibility of the label
	 *
	 * @param flag
	 *            label visibility
	 */
	public void setIsVisible(boolean flag) {
		isVisible = flag;
	}

	protected abstract void drawContent(Renderer renderer);

	/**
	 *
	 * @return font scale (used for image export)
	 */
	protected double getFontScale() {
		return view.getFontScale();
	}

	private static int drawRectangle(Renderer renderer, double x,
			double y, double z, double w, double h, int index) {
		return renderer.getGeometryManager().rectangle(x, y, z, w, h, index);
	}

}
