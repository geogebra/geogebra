package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.LinkedList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hits3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Geometry3DGetterManager;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class to list the 3D drawables for EuclidianView3D
 * 
 * @author ggb3D
 * 
 */
public class Drawable3DLists {

	/** 3D view */
	protected EuclidianView3D view3D;

	/** lists of Drawable3D */
	protected Drawable3DList[] lists;

	/**
	 * Array list with debugging toString
	 */
	protected static class Drawable3DList extends ArrayList<Drawable3D> {

		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Drawable3D d : this) {
				sb.append(d);
				sb.append(" -- ");
				sb.append(d.getGeoElement()
						.getLabel(StringTemplate.defaultTemplate));
				sb.append("\n");
			}
			return sb.toString();
		}
	}

	/**
	 * default constructor
	 * 
	 * @param view3D
	 *            view
	 */
	public Drawable3DLists(EuclidianView3D view3D) {
		this.view3D = view3D;
		lists = new Drawable3DList[Drawable3D.DRAW_TYPE_MAX];
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			lists[i] = new Drawable3DList();
		}
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
	 *            list of drawabls to be added
	 */
	public void add(LinkedList<Drawable3D> list) {
		for (Drawable3D d : list) {
			add(d);
		}
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
			// Log.debug(drawable.getGeoElement()+"");
			drawable.removeFromDrawable3DLists(this);
		}
	}

	/**
	 * remove all drawables contained in the list
	 * 
	 * @param list
	 *            list of drawables to be removed
	 */
	public void remove(LinkedList<Drawable3D> list) {
		for (Drawable3D d : list) {
			remove(d);
		}
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

	@Override
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
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Drawable3D d3d : lists[i]) {
				d3d.removeFromGL();
			}
			lists[i].clear();
		}
	}

	/**
	 * update all 3D objects
	 * 
	 * @param renderer
	 *            openGL renderer
	 */
	public void updateAll(Renderer renderer) {
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Drawable3D d3d : lists[i]) {
				d3d.update();
			}
		}
	}

	/**
	 * says all have to be reset
	 * 
	 */
	public void resetAllDrawables() {
		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Drawable3D d3d : lists[i]) {
				d3d.setWaitForReset();
			}
		}
	}

	/** says all have to be reset */
	public void resetAllLabels() {

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Drawable3D d3d : lists[i]) {
				d3d.setLabelWaitForReset();
			}
		}

	}

	/** says all visual styles to be updated */
	public void resetAllVisualStyles() {

		for (int i = 0; i < Drawable3D.DRAW_TYPE_MAX; i++) {
			for (Drawable3D d3d : lists[i]) {
				d3d.setWaitForUpdateVisualStyle(null);
			}
		}

	}

	/**
	 * draw hidden parts not dashed
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawHiddenNotTextured(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_POINTS]) {
			d3d.drawHidden(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawHiddenNotTextured(renderer);
		}
	}

	/**
	 * export to 3D printer format
	 * 
	 * @param exportToPrinter3D
	 *            exporter
	 * 
	 */
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D) {
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_POINTS]) {
			d.exportToPrinter3D(exportToPrinter3D, false);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CURVES]) {
			d.exportToPrinter3D(exportToPrinter3D, false);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]) {
			d.exportToPrinter3D(exportToPrinter3D, false);
		}

		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_SURFACES]) {
			d.exportToPrinter3D(exportToPrinter3D, true);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]) {
			d.exportToPrinter3D(exportToPrinter3D, true);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]) {
			d.exportToPrinter3D(exportToPrinter3D, true);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]) {
			d.exportToPrinter3D(exportToPrinter3D, true);
		}

		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			d.exportToPrinter3D(exportToPrinter3D, false);
			d.exportToPrinter3D(exportToPrinter3D, true);
		}
	}

	/**
	 * export to 3D printer format
	 * 
	 * @param manager
	 *            geometry getter manager
	 * 
	 */
	public void export(Geometry3DGetterManager manager) {

		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_POINTS]) {
			d.export(manager, false);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CURVES]) {
			d.export(manager, false);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]) {
			d.export(manager, false);
		}

		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_SURFACES]) {
			d.export(manager, true);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]) {
			d.export(manager, true);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]) {
			d.export(manager, true);
		}
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]) {
			d.export(manager, true);
		}

		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			d.export(manager, false);
			d.export(manager, true);
		}
	}

	/**
	 * draw surfaces that are not transparent
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawNotTransparentSurfaces(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_SURFACES]) {
			d3d.drawNotTransparentSurface(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawNotTransparentSurfaces(renderer);
		}
	}

	/**
	 * draw closed surfaces that are not transparent
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawNotTransparentSurfacesClosed(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]) {
			d3d.drawNotTransparentSurface(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]) {
			d3d.drawNotTransparentSurface(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawNotTransparentSurfacesClosed(renderer);
		}
	}

	/**
	 * draw clipped surfaces that are not transparent
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawNotTransparentSurfacesClipped(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]) {
			d3d.drawNotTransparentSurface(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
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
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CURVES]) {
			d3d.drawHidden(renderer);
		}
		if (containsClippedCurves()) {
			renderer.enableClipPlanesIfNeeded();
			for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]) {
				d3d.drawHidden(renderer);
			}
			renderer.disableClipPlanesIfNeeded();
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawHiddenTextured(renderer);
		}
	}

	/**
	 * draw surfaces as transparent parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawTransp(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_SURFACES]) {
			d3d.drawTransp(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists().drawTransp(renderer);
		}
	}

	/**
	 * draw transparent closed surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawTranspClosedNotCurved(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]) {
			d3d.drawTransp(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawTranspClosedNotCurved(renderer);
		}
	}

	/**
	 * draw transparent closed surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawTranspClosedCurved(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]) {
			d3d.drawTransp(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawTranspClosedCurved(renderer);
		}
	}

	/**
	 * draw transparent clipped surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawTranspClipped(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]) {
			d3d.drawTransp(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists().drawTranspClipped(renderer);
		}
	}

	/**
	 * draw the not hidden (solid) parts of curves and points
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void draw(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CURVES]) {
			d3d.drawOutline(renderer);
		}
		if (containsClippedCurves()) {
			renderer.enableClipPlanesIfNeeded();
			for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLIPPED_CURVES]) {
				d3d.drawOutline(renderer);
			}
			renderer.disableClipPlanesIfNeeded();
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists().draw(renderer);
		}
	}

	/**
	 * draw the labels of objects
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawLabel(Renderer renderer) {
		for (int i = 0; i < Drawable3D.DRAW_TYPE_TEXTS; i++) {
			for (Drawable3D d3d : lists[i]) {
				d3d.drawLabel(renderer);
			}
		}
	}

	/**
	 * draw texts (not in absolute position)
	 * 
	 * @param renderer
	 *            opengl context
	 * @param absolute
	 *            if absolute position
	 */
	public void drawForAbsoluteText(Renderer renderer, boolean absolute) {
		// texts
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_TEXTS]) {
			((DrawText3D) d).drawForAbsolutePosition(renderer, absolute);
		}

		// lists
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawComposite3D) d).getDrawable3DLists()
					.drawForAbsoluteText(renderer, absolute);
		}
	}

	/**
	 * draw the hiding (surfaces) parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawSurfacesForHiding(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_SURFACES]) {
			d3d.drawHiding(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawSurfacesForHiding(renderer);
		}
	}

	/**
	 * draw the hiding (closed surfaces) parts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawClosedSurfacesForHiding(Renderer renderer) {
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED]) {
			d3d.drawHiding(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES_CURVED]) {
			d3d.drawHiding(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
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
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES]) {
			d3d.drawHiding(renderer);
		}
		for (Drawable3D d3d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d3d).getDrawable3DLists()
					.drawClippedSurfacesForHiding(renderer);
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
	 * @param x
	 *            mouse x location
	 * @param y
	 *            mouse y location
	 * @return first hitted label geo
	 */
	final public GeoElement getLabelHit(double x, double y) {
		for (Drawable3DList list : lists) {
			for (Drawable3D d : list) {
				if (d.isVisible()) {
					GeoElement geo = d.getGeoElement();
					if (!geo.isGeoText() && geo.isPickable()
							&& d.label.hit(x, y)) {
						return geo;
					}
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * @param origin
	 *            mouse origin
	 * @param direction
	 *            mouse direction
	 * @return first hitted label geo
	 */
	public GeoElement getLabelHit(Coords origin, Coords direction) {
		for (Drawable3DList list : lists) {
			for (Drawable3D d : list) {
				if (d.isVisible()) {
					GeoElement geo = d.getGeoElement();
					if (!geo.isGeoText() && geo.isPickable()
							&& d.label.hit(origin, direction)) {
						return geo;
					}
				}
			}
		}

		return null;
	}

	/**
	 * @return whether all lists are empty
	 */
	public boolean isEmpty() {
		for (Drawable3DList list : lists) {
			if (!list.isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
