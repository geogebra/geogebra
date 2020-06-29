package org.geogebra.common.geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D.DrawableComparator;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;

/**
 * 3D hits (for picking, selection, ...)
 * 
 * @author Mathieu
 *
 */
public class Hits3D extends Hits {

	private static final long serialVersionUID = 1L;
	/** set of hits by picking order */
	private TreeSetOfDrawable3D[] hitSet = new TreeSetOfDrawable3D[Drawable3D.DRAW_PICK_ORDER_MAX];
	/** other hits */
	private TreeSetOfDrawable3D hitsOthers = new TreeSetOfDrawable3D(
			new Drawable3D.DrawableComparator());
	/** label hits */
	private TreeSetOfDrawable3D hitsLabels = new TreeSetOfDrawable3D(
			new Drawable3D.DrawableComparator());
	/** set of all the sets */
	private TreeSet<TreeSetOfDrawable3D> hitSetSet = new TreeSet<>(
			new Drawable3D.SetComparator());

	private Hits topHits = new Hits();

	private ArrayList<Drawable3D> drawables3D = new ArrayList<>();

	/** number of quadrics 2D */
	private int quadCount;

	/**
	 * class for tree set of drawable 3D
	 * 
	 */
	private static class TreeSetOfDrawable3D extends TreeSet<Drawable3D> {

		private static final long serialVersionUID = 1L;

		public TreeSetOfDrawable3D(DrawableComparator drawableComparator) {
			super(drawableComparator);
		}

		public void add(Drawable3D d, double zNear, double zFar) {

			// if already contained and not nearer, do nothing
			if (contains(d)) {
				if (d.getZPickNear() > zNear) {
					return;
				}

				// will re-add it at the correct z values
				remove(d);
			}

			d.setZPick(zNear, zFar, false, d.getPositionOnHitting());

			super.add(d);

		}
	}

	/**
	 * common constructor
	 */
	public Hits3D() {
		super();

		for (int i = 0; i < Drawable3D.DRAW_PICK_ORDER_MAX; i++) {
			hitSet[i] = new TreeSetOfDrawable3D(
					new Drawable3D.DrawableComparator());
		}

		// init counters
		quadCount = 0;
	}

	@Override
	public Hits3D cloneHits() {

		Hits3D ret = (Hits3D) super.cloneHits();
		ret.topHits = this.topHits.cloneHits();
		ret.quadCount = quadCount;

		// TreeSets are not cloned because they are only used when the hits are
		// constructed

		return ret;
	}

	@Override
	public boolean add(GeoElement geo) {

		if (geo == null) {
			Log.error("adding null geo");
			return false;
		}

		if (geo instanceof GeoQuadric3D) {
			quadCount++;
		}

		return super.add(geo);
	}

	@Override
	public void init() {
		super.init();
		for (int i = 0; i < Drawable3D.DRAW_PICK_ORDER_MAX; i++) {
			hitSet[i].clear();
		}
		hitsOthers.clear();
		hitsLabels.clear();

		topHits.init();

	}

	/**
	 * init and set geo as only element
	 * 
	 * @param geo
	 *            geo
	 */
	public void init(GeoElement geo) {
		init();
		if (geo != null) {
			add(geo);
			topHits.add(geo);
		}
	}

	/**
	 * insert a drawable in the hitSet, called by EuclidianRenderer3D
	 * 
	 * @param d
	 *            the drawable
	 * @param type
	 *            type of picking
	 * @param zNear
	 *            nearest z for picking
	 * @param zFar
	 *            most far z for picking
	 */
	public void addDrawable3D(Drawable3D d, PickingType type, double zNear,
			double zFar) {

		if (type == PickingType.LABEL) {
			if (!d.getGeoElement().isGeoText()) {
				hitsLabels.add(d, zNear, zFar);
			}
		} else { // remember last type for picking
			d.setPickingType(type);
		}

		// Log.debug("\n"+d+"\n"+type);

		if (d.getPickOrder() < Drawable3D.DRAW_PICK_ORDER_MAX) {
			hitSet[d.getPickOrder()].add(d, zNear, zFar);
		} else {
			hitsOthers.add(d, zNear, zFar);
		}

	}

	/**
	 * insert a drawable in the hitSet
	 * 
	 * @param d
	 *            the drawable
	 * @param type
	 *            type of picking
	 */
	public void addDrawable3D(Drawable3D d, PickingType type) {

	    if (d.hasRelevantPickingValues()) {
            if (type == PickingType.LABEL) {
                if (!d.getGeoElement().isGeoText()) {
                    hitsLabels.add(d);
                }
            } else { // remember last type for picking
                d.setPickingType(type);
            }

            if (d.getPickOrder() < Drawable3D.DRAW_PICK_ORDER_MAX) {
                hitSet[d.getPickOrder()].add(d);
            } else {
                hitsOthers.add(d);
            }
        }
	}

	/**
	 * sort all hits in different sets
	 * 
	 * @return nearest zNear
	 */
	public double sort() {
		hitSetSet.clear();

		for (int i = 0; i < Drawable3D.DRAW_PICK_ORDER_MAX; i++) {
			hitSetSet.add(hitSet[i]);
		}

		// return nearest zNear
		double zNear = Double.NaN;

		// top hits
		Iterator<Drawable3D> iter1 = hitSetSet.first().iterator();
		if (iter1.hasNext()) {
			Drawable3D d = iter1.next();
			addToHits(d, topHits);
			zNear = d.getZPickNear();
		}
		while (iter1.hasNext()) {
			addToHits(iter1.next(), topHits);
		}

		// sets the hits to this
		ArrayList<GeoElement> segmentList = new ArrayList<>();
		drawables3D.clear();

		for (TreeSetOfDrawable3D set : hitSetSet) {
			for (Drawable3D d : set) {
				drawables3D.add(d);
				GeoElement geo = d.getGeoElement();
				addToHits(d, this);

				// add the parent of this if it's a segment from a GeoPolygon3D
				// or GeoPolyhedron
				if (geo.isGeoSegment()) {
					segmentList.add(geo);
				} else if (geo.isGeoConic()) {
					if (d.getPickingType() == PickingType.POINT_OR_CURVE) {
						((GeoConicND) geo).setLastHitType(HitType.ON_BOUNDARY);
					} else { // PickingType.SURFACE
						((GeoConicND) geo).setLastHitType(HitType.ON_FILLING);
					}
				}
			}
		}

		return zNear;
	}

	private void addToHits(Drawable3D d, Hits hits) {
		GeoElement geo = d.getGeoElement();
		if (geo.getMetasLength() > 0 && geo.getKernel().getApplication()
				.has(Feature.G3D_SELECT_META)) {
			hits.addAll(Arrays.asList(((FromMeta) geo).getMetas()));
		}
		hits.add(geo);
	}

	/**
	 * WARNING : sort() should be called before
	 * 
	 * @return all drawables, in pick order
	 */
	public ArrayList<Drawable3D> getDrawables() {

		return drawables3D;
	}

	@Override
	public Hits getTopHits() {

		if (topHits.isEmpty()) {
			return cloneHits();
		}
		return topHits;

	}

	@Override
	public Hits getTopHits(int depth, int geoN) {
		Hits3D ret = new Hits3D();
		int depthCount = 0;
		int geoNCount = 0;
		for (Iterator<TreeSetOfDrawable3D> iterSet = hitSetSet
				.iterator(); iterSet.hasNext() && depthCount < depth;) {
			TreeSetOfDrawable3D set = iterSet.next();
			if (set.size() > 0) {
				depthCount++;
			}

			for (Iterator<Drawable3D> iter = set.iterator(); iter.hasNext()
					&& geoNCount < geoN;) {
				Drawable3D d = iter.next();
				GeoElement geo = d.getGeoElement();
				ret.add(geo);
				geoNCount++;
			}
		}
		return ret;
	}

	/**
	 * return the first label hit, if one
	 * 
	 * @return the first label hit
	 */
	public GeoElement getLabelHit() {

		if (hitsLabels.isEmpty()) {
			return null;
		}

		// Log.debug("\nlabel:"+hitsLabels.first().zPickMin+"\nfirst
		// hit:"+drawables3D.get(0).zPickMin);
		GeoElement labelGeo = hitsLabels.first().getGeoElement();
		// check if the label hit is the first geo hitted
		if (labelGeo == topHits.get(0)) {
			return labelGeo;
		}
		// else label is not hitted
		return null;
	}

	/**
	 * remove all polygons but one
	 */
	@Override
	public void removeAllPolygonsButOne() {
		super.removeAllPolygonsButOne();
		topHits.clear(); // getTopHits() return this
	}

	@Override
	public void removeAllPolygonsAndQuadricsButOne() {
		boolean foundTarget = false;
		for (int i = 0; i < size() - 1; ++i) {
			GeoElement geo = get(i);
			if (geo.isGeoPolygon() || geo instanceof GeoQuadric3D
					|| geo.isGeoConic()) {
				if (foundTarget) {
					// not removing when found first time
					remove(i);
				}
				foundTarget = true;
			}
		}
	}

	@Override
	protected Hits createNewHits() {
		return new Hits3D();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj); // see EQ_DOESNT_OVERRIDE_EQUALS in SpotBugs
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
