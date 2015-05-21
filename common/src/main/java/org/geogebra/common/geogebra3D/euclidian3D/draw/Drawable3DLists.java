package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.Hits3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class to list the 3D drawables for EuclidianView3D
 * 
 * @author ggb3D
 * 
 *
 */
public class Drawable3DLists {

	protected class Drawable3DList extends ArrayList<Drawable3D> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Drawable3D d : this) {
				sb.append(d);
				sb.append(" -- ");
				sb.append(d.getGeoElement().getLabel(
						StringTemplate.defaultTemplate));
				sb.append("\n");
			}
			return sb.toString();

		}
	}

	/** lists of Drawable3D */
	protected Drawable3DList[] lists;

	/**
	 * default constructor
	 * 
	 * @param view3D
	 */
	public Drawable3DLists() {

		lists = new Drawable3DList[Drawable3D.DRAW_TYPE_MAX];
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++)
			lists[i] = new Drawable3DList();
	}

	/**
	 * add the drawable to the correct list
	 * 
	 * @param drawable
	 *            drawable to add
	 */
	public void add(Drawable3D drawable) {

		drawable.addToDrawable3DLists(this);
	}

	/**
	 * add a list of drawables
	 * 
	 * @param list
	 */
	public void add(LinkedList<Drawable3D> list) {

		for (Drawable3D d : list)
			add(d);

	}

	/**
	 * remove the drawable from the correct list
	 * 
	 * @param drawable
	 *            drawable to remove
	 */
	protected void remove(Drawable3D drawable) {

		// TODO fix it
		if (drawable != null) {
			// App.debug(drawable.getGeoElement()+"");
			drawable.removeFromDrawable3DLists(this);
		}

	}

	/**
	 * remove all drawables contained in the list
	 * 
	 * @param list
	 */
	public void remove(LinkedList<Drawable3D> list) {
		for (Drawable3D d : list)
			remove(d);

	}

	/**
	 * 
	 * @param type
	 *            list type
	 * @return list
	 */
	public Drawable3DList getList(int type) {
		return lists[type];
	}

	/**
	 * return the size of the cummulated lists
	 * 
	 * @return the size of the cummulated lists
	 */
	public int size() {
		int size = 0;
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++)
			size += lists[i].size();
		return size;
	}

	/**
	 * 
	 * @return true if contains clipped surfaces
	 */
	public boolean containsClippedSurfacesInclLists() {
		return !lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES].isEmpty()
				|| !lists[Drawable3D.DRAW_TYPE_LISTS].isEmpty();
	}

	/**
	 * 
	 * @return true if contains clipped curves
	 */
	private boolean containsClippedCurves() {
		return !lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES].isEmpty();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			sb.append("list #");
			sb.append(i);
			sb.append(":\n");
			sb.append(lists[i].toString());
		}

		return sb.toString();
	}

	/**
	 * clear all the lists
	 */
	public void clear() {
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++)
			lists[i].clear();
	}

	// private boolean isUpdatingAll = false;

	/** update all 3D objects */
	public void updateAll() {

		/*
		 * if (isUpdatingAll){
		 * Application.printStacktrace("is already updating"); return; }
		 * 
		 * isUpdatingAll = true;
		 */

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) {
				Drawable3D d3d = d.next();
				// Application.debug("updating :"+d3d.getGeoElement());
				d3d.update();
			}

		// isUpdatingAll = false;

	}

	/** says all have to be reset */
	public void resetAllDrawables() {

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) {
				d.next().setWaitForReset();
			}
		}

	}

	/** says all have to be reset */
	public void resetAllLabels() {

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) {
				d.next().setLabelWaitForReset();
			}
		}

	}

	/** says all visual styles to be updated */
	public void resetAllVisualStyles() {

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) {
				d.next().setWaitForUpdateVisualStyle();
			}
		}

	}

	/**
	 * draw hidden parts not dashed
	 * 
	 * @param renderer
	 */
	public void drawHiddenNotTextured(Renderer renderer) {
		// points TODO hidden aspect ?
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_POINTS]
				.iterator(); d.hasNext();)
			d.next().drawHidden(renderer);

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();)
			((DrawList3D) d.next()).getDrawable3DLists().drawHiddenNotTextured(
					renderer);

	}

	/**
	 * draw in .obj format through renderer
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawInObjFormat(Renderer renderer) {
		for (Drawable3DList list : lists) {
			for (Drawable3D d : list) {
				d.drawInObjFormat(renderer);
			}
		}
	}

	/**
	 * draw surfaces that are not transparent
	 * 
	 * @param renderer
	 */
	public void drawNotTransparentSurfaces(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES]
				.iterator(); d.hasNext();)
			d.next().drawNotTransparentSurface(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawNotTransparentSurfaces(renderer);
		}

	}

	/**
	 * draw closed surfaces that are not transparent
	 * 
	 * @param renderer
	 */
	public void drawNotTransparentSurfacesClosed(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]
				.iterator(); d.hasNext();)
			d.next().drawNotTransparentSurface(renderer);
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]
				.iterator(); d.hasNext();)
			d.next().drawNotTransparentSurface(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawNotTransparentSurfacesClosed(renderer);
		}
	}

	/**
	 * draw clipped surfaces that are not transparent
	 * 
	 * @param renderer
	 */
	public void drawNotTransparentSurfacesClipped(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]
				.iterator(); d.hasNext();)
			d.next().drawNotTransparentSurface(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawNotTransparentSurfacesClipped(renderer);
		}

	}

	/**
	 * draw the hidden (dashed) parts of curves and points
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawHiddenTextured(Renderer renderer) {

		// curves
		// TODO if there's no surfaces, no hidden part has to be drawn
		// if(!lists[Drawable3D.DRAW_TYPE_SURFACES].isEmpty())
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CURVES]
				.iterator(); d.hasNext();)
			d.next().drawHidden(renderer);

		if (containsClippedCurves()) {
			renderer.enableClipPlanesIfNeeded();
			for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]
					.iterator(); d.hasNext();)
				d.next().drawHidden(renderer);
			renderer.disableClipPlanesIfNeeded();
		}

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists().drawHiddenTextured(
					renderer);
		}

	}

	/**
	 * draw surfaces as transparent parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawTransp(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES]
				.iterator(); d.hasNext();)
			d.next().drawTransp(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists().drawTransp(renderer);
		}

	}

	/**
	 * draw transparent closed surfaces
	 * 
	 * @param renderer
	 */
	public void drawTranspClosedNotCurved(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]
				.iterator(); d.hasNext();)
			d.next().drawTransp(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawTranspClosedNotCurved(renderer);
		}

	}

	/**
	 * draw transparent closed surfaces
	 * 
	 * @param renderer
	 */
	public void drawTranspClosedCurved(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]
				.iterator(); d.hasNext();)
			d.next().drawTransp(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawTranspClosedCurved(renderer);
		}

	}

	/**
	 * draw transparent clipped surfaces
	 * 
	 * @param renderer
	 */
	public void drawTranspClipped(Renderer renderer) {
		
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]
				.iterator(); d.hasNext();)
			d.next().drawTransp(renderer);
		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists().drawTranspClipped(
					renderer);
		}

	}

	/**
	 * draw the not hidden (solid) parts of curves and points
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void draw(Renderer renderer) {

		// curves
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CURVES]
				.iterator(); d.hasNext();) {
			d.next().drawOutline(renderer);
		}

		if (containsClippedCurves()) {
			renderer.enableClipPlanesIfNeeded();
			for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]
					.iterator(); d.hasNext();)
				d.next().drawOutline(renderer);
			renderer.disableClipPlanesIfNeeded();
		}

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists().draw(renderer);
		}

	}

	/**
	 * draw the labels of objects
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawLabel(Renderer renderer) {

		for (int i = 0; i < Drawable3D.DRAW_TYPE_TEXTS; i++){
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();){
				d.next().drawLabel(renderer);
			}
		}
		
	}
	
	/**
	 * draw texts (not in absolute position)
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawNotAbsoluteText(Renderer renderer) {

		// texts
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_TEXTS]) {
			((DrawText3D) d).drawNotAbsolutePosition(renderer);
		}
		
		// lists
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d).getDrawable3DLists().drawNotAbsoluteText(renderer);
		}

	}
	
	
	/**
	 * draw texts (in absolute position)
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawAbsoluteText(Renderer renderer) {

		// texts
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_TEXTS]) {
			((DrawText3D) d).drawAbsolutePosition(renderer);
		}
		
		// lists
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d).getDrawable3DLists().drawAbsoluteText(renderer);
		}
		

	}

	/**
	 * draw the hiding (surfaces) parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawSurfacesForHiding(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES]
				.iterator(); d.hasNext();)
			d.next().drawHiding(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists().drawSurfacesForHiding(
					renderer);
		}

	}

	/**
	 * draw the hiding (closed surfaces) parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawClosedSurfacesForHiding(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]
				.iterator(); d.hasNext();)
			d.next().drawHiding(renderer);
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]
				.iterator(); d.hasNext();)
			d.next().drawHiding(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawClosedSurfacesForHiding(renderer);
		}

	}

	/**
	 * draw the hiding (clipped surfaces) parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawClippedSurfacesForHiding(Renderer renderer) {

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]
				.iterator(); d.hasNext();)
			d.next().drawHiding(renderer);

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawClippedSurfacesForHiding(renderer);
		}

	}

	private static void drawListForPickingPointOrCurve(Renderer renderer,
			Drawable3DList list) {
		for (Iterator<Drawable3D> iter = list.iterator(); iter.hasNext();) {
			Drawable3D d = iter.next();
			renderer.pick(d, PickingType.POINT_OR_CURVE);
		}
	}

	private static void drawListForPickingSurface(Renderer renderer,
			Drawable3DList list) {
		for (Iterator<Drawable3D> iter = list.iterator(); iter.hasNext();) {
			Drawable3D d = iter.next();
			renderer.pick(d, PickingType.SURFACE);
		}
	}

	/**
	 * draw points and curves to pick them
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawForPickingPointsAndCurves(Renderer renderer) {
		drawForPickingPointsAndCurves(renderer, null);
	}

	private void drawForPickingPointsAndCurves(Renderer renderer,
			DrawList3D parent) {

		renderer.disableCulling();

		drawListForPickingPointOrCurve(renderer,
				lists[Drawable3D.DRAW_TYPE_DEFAULT]);
		drawListForPickingPointOrCurve(renderer,
				lists[Drawable3D.DRAW_TYPE_POINTS]);
		drawListForPickingPointOrCurve(renderer,
				lists[Drawable3D.DRAW_TYPE_CURVES]);

		if (containsClippedCurves()) {
			renderer.enableClipPlanesIfNeeded();
			drawListForPickingPointOrCurve(renderer,
					lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]);
			renderer.disableClipPlanesIfNeeded();
		}

		renderer.enableCulling();
		renderer.setCullFaceBack();

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawForPickingPointsAndCurves(renderer);
		}

	}

	/**
	 * draw surfaces to pick them
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawForPickingSurfaces(Renderer renderer) {

		renderer.disableCulling();

		drawListForPickingSurface(renderer,
				lists[Drawable3D.DRAW_TYPE_SURFACES]);
		drawListForPickingSurface(renderer,
				lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]);

		renderer.enableCulling();

		renderer.setCullFaceFront();
		drawListForPickingSurface(renderer,
				lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]);
		renderer.setCullFaceBack();
		drawListForPickingSurface(renderer,
				lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]);

		renderer.disableCulling();

		if (containsClippedSurfacesInclLists()) {
			renderer.enableClipPlanesIfNeeded();
			drawListForPickingSurface(renderer,
					lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]);
			renderer.disableClipPlanesIfNeeded();
		}

		renderer.enableCulling();

		// lists
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_LISTS]
				.iterator(); d.hasNext();) {
			((DrawList3D) d.next()).getDrawable3DLists()
					.drawForPickingSurfaces(renderer);
		}

	}

	/**
	 * process the hit
	 * 
	 * @param hitting
	 *            e.g. ray
	 * @param hits
	 *            hits where drawables are stored
	 */
	public void hit(Hitting hitting, Hits3D hits) {
		for (Drawable3DList list : lists) {
			for (Drawable3D d : list) {
				d.hitIfVisibleAndPickable(hitting, hits);
			}
		}
	}

	/**
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @return first hitted label geo
	 */
	public GeoElement getLabelHit(GPoint mouseLoc) {
		for (Drawable3DList list : lists) {
			for (Drawable3D d : list) {
				if (d.isVisible()) {
					GeoElement geo = d.getGeoElement();
					if (!geo.isGeoText() && geo.isPickable()
							&& d.label.hit(mouseLoc)) {
						return geo;
					}
				}
			}
		}

		return null;
	}

}
