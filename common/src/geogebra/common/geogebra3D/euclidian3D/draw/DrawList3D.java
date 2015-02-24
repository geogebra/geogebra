package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hitting;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public class DrawList3D extends Drawable3D {

	private GeoList geoList;
	private boolean isVisible;

	private Drawable3DListsForDrawList3D drawable3DLists;

	private DrawList3DArray drawables;

	/**
	 * common constructor
	 * 
	 * @param view3D
	 * @param geo
	 */
	public DrawList3D(EuclidianView3D view3D, GeoList geo) {
		super(view3D, geo);
		drawables = new DrawList3DArray(view3D, this);
		this.geoList = geo;
		drawable3DLists = new Drawable3DListsForDrawList3D(view3D);

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	protected boolean updateForItSelf() {

		// App.debug("LIST -- "+getGeoElement());

		isVisible = geoList.isEuclidianVisible();
		if (!isVisible)
			return true;

		// getView3D().removeGeoToPick(drawables.size());

		// go through list elements and create and/or update drawables
		int size = geoList.size();
		drawables.ensureCapacity(size);
		int oldDrawableSize = drawables.size();

		int drawablePos = 0;
		for (int i = 0; i < size; i++) {
			GeoElement listElement = geoList.get(i);
			// only new 3D elements are drawn
			if (!listElement.hasDrawable3D())
				continue;

			// add drawable for listElement
			if (drawables.addToDrawableList(listElement, drawablePos,
					oldDrawableSize, this))
				drawablePos++;

		}

		// remove end of list
		for (int i = drawables.size() - 1; i >= drawablePos; i--) {
			// getView3D().remove(drawables.get(i).getGeoElement());
			drawable3DLists.remove((Drawable3D) drawables.get(i));
			drawables.remove(i);

		}

		// update for list of lists
		for (int i = 0; i < drawables.size(); i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (/* createdByDrawList() || */!d.getGeoElement().isLabelSet()) {
				if (d.waitForUpdate()) {
					d.update();
				}
			}
		}

		/*
		 * // check if a new update is needed in a next loop for (int i = 0; i <
		 * drawables.size(); i++) { Drawable3D d = (Drawable3D)
		 * drawables.get(i); if (!d.getGeoElement().isLabelSet()) {
		 * //App.debug("\n"
		 * +geoList+"\n -- "+d.getGeoElement()+" -- "+d.waitForUpdate()); if
		 * (d.waitForUpdate()){ return false; } } }
		 */

		return true;
	}

	/*
	 * http://dev.geogebra.org/trac/changeset/37937 kills ManySpheres.ggb
	 * 
	 * @Override public boolean waitForUpdate(){ for (int i = 0; i <
	 * drawables.size(); i++) { Drawable3D d = (Drawable3D) drawables.get(i); if
	 * (!d.getGeoElement().isLabelSet()) { if (d.waitForUpdate()){ return true;
	 * } } }
	 * 
	 * return false; }
	 */

	@Override
	protected void updateForView() {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
				d.updateForView();
			}
		}
	}

	@Override
	protected void clearTraceForViewChanged() {
		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable3D d = (Drawable3D) drawables.get(i);
			if (createdByDrawList() || !d.getGeoElement().isLabelSet()) {
				d.clearTraceForViewChanged();
			}
		}
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_LISTS);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_LISTS);
	}

	/**
	 * 
	 * @return drawable lists
	 */
	public Drawable3DListsForDrawList3D getDrawable3DLists() {
		return drawable3DLists;
	}

	@Override
	public void drawOutline(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawNotTransparentSurface(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHiding(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawTransp(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection,
			PickingType type) {

		// not picked as drawable
		return null;

	}

	@Override
	public void drawLabel(Renderer renderer) {
		// no label
	}

	@Override
	public boolean drawLabelForPicking(Renderer renderer) {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return false;
	}
	
	
	
	@Override
	protected boolean isLabelVisible() {
		return isVisible(); // as for texts
	}

	@Override
	protected void updateLabel() {
		for (DrawableND d : drawables) {
			if (d instanceof DrawList3D || d instanceof DrawText3D){
				((Drawable3D) d).updateLabel();
			}			
		}
	}

	@Override
	protected void updateLabelPosition() {
		for (DrawableND d : drawables) {
			if (d instanceof DrawList3D || d instanceof DrawText3D){
				((Drawable3D) d).updateLabelPosition();
			}			
		}

	}

	@Override
	protected double getColorShift() {
		return COLOR_SHIFT_NONE; // not needed here
	}

	@Override
	public void setWaitForUpdateVisualStyle() {

		super.setWaitForUpdateVisualStyle();
		for (DrawableND d : drawables) {
			d.setWaitForUpdateVisualStyle();
		}

		// also update for e.g. line width
		setWaitForUpdate();
	}

	@Override
	public void setWaitForReset() {

		super.setWaitForReset();
		for (DrawableND d : drawables) {
			((Drawable3D) d).setWaitForReset();
		}
	}

	// @Override
	// public void setWaitForUpdate(){
	//
	// super.setWaitForUpdate();
	// for (DrawableND d : drawables){
	// d.setWaitForUpdate();
	// }
	// }

	@Override
	protected Drawable3D getDrawablePicked(Drawable3D drawableSource) {

		pickOrder = drawableSource.getPickOrder();
		setPickingType(drawableSource.getPickingType());

		return super.getDrawablePicked(drawableSource);
	}

	private int pickOrder = DRAW_PICK_ORDER_MAX;

	@Override
	public int getPickOrder() {
		return pickOrder;
	}

	@Override
	public boolean hit(Hitting hitting) {

		boolean ret = false;

		double listZNear = Double.NEGATIVE_INFINITY;
		double listZFar = Double.NEGATIVE_INFINITY;
		for (DrawableND d : drawables) {
			final Drawable3D d3d = (Drawable3D) d;
			if (d3d.hitForList(hitting)) {
				double zNear = d3d.getZPickNear();
				double zFar = d3d.getZPickFar();
				if (!ret || zNear > listZNear) {
					listZNear = zNear;
					listZFar = zFar;
					setPickingType(d3d.getPickingType());
					pickOrder = d3d.getPickOrder();
					ret = true;
				}
			}
		}

		if (pickOrder == DRAW_PICK_ORDER_POINT) { // list of points are paths
			pickOrder = DRAW_PICK_ORDER_PATH;
		}

		if (ret) {
			setZPick(listZNear, listZFar);
		}

		return ret;

	}

	/**
	 * remove all geos to pick counter
	 */
	public void removeAllGeosToPick() {
		for (int i = 0; i < drawables.size(); i++) {
			DrawableND d = drawables.get(i);
			if (d instanceof DrawList3D) {
				((DrawList3D) d).removeAllGeosToPick();
			} else {
				getView3D().removeOneGeoToPick();
			}
		}
	}

	
	@Override
	public void enlargeBounds(Coords min, Coords max) {
		for (DrawableND d : drawables) {
			((Drawable3D) d).enlargeBounds(min, max);
		}
	}
}
