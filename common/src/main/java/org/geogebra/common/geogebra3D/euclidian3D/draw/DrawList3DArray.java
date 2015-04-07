package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawListArray;

/**
 * Class for storing 3D drawables includes in a DrawList
 * 
 * @author mathieu
 *
 */
public class DrawList3DArray extends DrawListArray {

	private static final long serialVersionUID = 1L;

	private DrawList3D drawList3D;

	/**
	 * common constructor
	 * 
	 * @param view
	 *            view
	 * @param drawList3D
	 *            drawable for the list calling
	 */
	public DrawList3DArray(EuclidianViewInterfaceCommon view,
			DrawList3D drawList3D) {
		super(view);
		this.drawList3D = drawList3D;
	}

	@Override
	protected void update(DrawableND d) {

		if (d instanceof Drawable3D) {
			((Drawable3D) d).setWaitForUpdate();
		}
		d.update();

	}

	@Override
	public void add(int pos, DrawableND d) {
		super.add(pos, d);
		drawList3D.getDrawable3DLists().add((Drawable3D) d);
	}

	@Override
	public DrawableND set(int pos, DrawableND d) {
		DrawableND old = super.set(pos, d);
		drawList3D.getDrawable3DLists().remove((Drawable3D) old);
		drawList3D.getDrawable3DLists().add((Drawable3D) d);
		return old;
	}

}
