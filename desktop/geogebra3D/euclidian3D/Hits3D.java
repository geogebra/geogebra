package geogebra3D.euclidian3D;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicND.HitType;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.main.App;
import geogebra3D.euclidian3D.Drawable3D.drawableComparator;
import geogebra3D.euclidian3D.opengl.Renderer.PickingType;
import geogebra3D.kernel3D.GeoQuadric3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 3D hits (for picking, selection, ...)
 * @author matthieu
 *
 */
public class Hits3D extends Hits {
	

	private static final long serialVersionUID = 1L;
	
	/**
	 * class for tree set of drawable 3D
	 * @author matthieu
	 */
	private class TreeSetOfDrawable3D extends TreeSet<Drawable3D>{

		private static final long serialVersionUID = 1L;

		public TreeSetOfDrawable3D(drawableComparator drawableComparator) {
			super(drawableComparator);
		}

		public void add(Drawable3D d, float zMin, float zMax) {

			
			//if already contained and not nearer, do nothing
			if (contains(d)){
				if(d.zPickMin < zMin){
					return;
				}
				
				//will re-add it at the correct z values
				remove(d);
			}
			
				
			d.zPickMin = zMin;
			d.zPickMax = zMax;

			super.add(d);
			




		}
	}
	
	/** set of hits by picking order */
	private TreeSetOfDrawable3D[] hitSet = new TreeSetOfDrawable3D[Drawable3D.DRAW_PICK_ORDER_MAX];
	/** other hits */
	private TreeSetOfDrawable3D hitsOthers = new TreeSetOfDrawable3D(new Drawable3D.drawableComparator()); 
	/** label hits */
	private TreeSetOfDrawable3D hitsLabels = new TreeSetOfDrawable3D(new Drawable3D.drawableComparator()); 
	/** set of all the sets */
	private TreeSet<TreeSetOfDrawable3D> hitSetSet = new TreeSet<TreeSetOfDrawable3D>(new Drawable3D.setComparator()); 
	
	private Hits topHits = new Hits();
	
	private ArrayList<Drawable3D> drawables3D = new ArrayList<Drawable3D>();

	/** number of coord sys 2D */
	private int cs2DCount;
	/** number of quadrics 2D */
	private int QuadCount;
	
	/**
	 * common constructor
	 */
	public Hits3D(){
		super();
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i] = new TreeSetOfDrawable3D(new Drawable3D.drawableComparator());
		
		// init counters
		cs2DCount = 0;
		QuadCount = 0;
	}
	
	
	
	@Override
	public Hits3D clone() {

		Hits3D ret = (Hits3D) super.clone();
		ret.topHits = this.topHits.clone();
		ret.cs2DCount = cs2DCount;
		ret.QuadCount = QuadCount;
		
		// TreeSets are not cloned because they are only used when the hits are constructed

		return ret;
	} 
	
	@Override
	protected Hits newHits(){
		return new Hits3D();
	}
	
	
	@Override
	public boolean add(GeoElement geo){
		
		if (geo==null){
			App.error("adding null geo");
			return false;
		}
		
		if (geo instanceof GeoCoordSys2D) {
			cs2DCount++;
			//Application.debug("cs2DCount="+cs2DCount+"/"+(size()+1));
		}
		if (geo instanceof GeoQuadric3D) {
			QuadCount++;
		}
		
		return super.add(geo);
	}
	
	
	
	@Override
	public void init(){
		super.init();
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i].clear();
		hitsOthers.clear();
		hitsLabels.clear();
		
		topHits.init();
		
		
	}
	
	

	
	/** insert a drawable in the hitSet, called by EuclidianRenderer3D 
	 * @param d the drawable
	 * @param type type of picking
	 * @param zMin z minimum for picking
	 * @param zMax z maximum for picking*/
	public void addDrawable3D(Drawable3D d, PickingType type, float zMin, float zMax){

		if (type == PickingType.LABEL){
			if (!d.getGeoElement().isGeoText()){
				hitsLabels.add(d, zMin, zMax);
			}
		}else{ //remember last type for picking
			d.setPickingType(type);
		}
		
		//App.debug("\n"+d+"\n"+type);
		
		if(d.getPickOrder()<Drawable3D.DRAW_PICK_ORDER_MAX)
			hitSet[d.getPickOrder()].add(d, zMin, zMax);
		else
			hitsOthers.add(d, zMin, zMax);	
		
		
		
		
	}
	
	/** sort all hits in different sets */
	public void sort(){
				

		hitSetSet.clear();
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSetSet.add(hitSet[i]);		

		//top hits
		for (Iterator<Drawable3D> iter = hitSetSet.first().iterator(); iter.hasNext();) {
			Drawable3D d = iter.next();
			topHits.add(d.getGeoElement());
		}
		
		
		// sets the hits to this
		ArrayList<GeoElement> segmentList = new ArrayList<GeoElement>();
		drawables3D.clear();
		
		for (Iterator<TreeSetOfDrawable3D> iterSet = hitSetSet.iterator(); iterSet.hasNext();) {
			TreeSetOfDrawable3D set = iterSet.next();
			for (Iterator<Drawable3D> iter = set.iterator(); iter.hasNext();) {
				Drawable3D d = iter.next();
				drawables3D.add(d);
				GeoElement geo = d.getGeoElement();
				this.add(geo);
				
				// add the parent of this if it's a segment from a GeoPolygon3D or GeoPolyhedron
				if (geo.isGeoSegment()){
					segmentList.add(geo);
				}else if (geo.isGeoConic()){
					if (d.getPickingType() == PickingType.POINT_OR_CURVE){
						((GeoConicND) geo).setLastHitType(HitType.ON_BOUNDARY);
					}else{ // PickingType.SURFACE
						((GeoConicND) geo).setLastHitType(HitType.ON_FILLING);
					}
				}
			}
		}
		
		// add the parent of this if it's a segment from a GeoPolygon3D or GeoPolyhedron
		/* TODO ?
		for (Iterator<GeoElement> iter = segmentList.iterator(); iter.hasNext();) {
			GeoSegment3D seg = (GeoSegment3D) iter.next();
			GeoElement parent = seg.getGeoParent();
			if (parent!=null)
				if (!this.contains(parent))
					this.add(seg.getGeoParent());				
		}
		*/
		
		//debug
		/*
		if (getLabelHit()==null)
			Application.debug(toString());
		else
			Application.debug(toString()+"\n first label : "+getLabelHit().getLabel());
		*/
	}
	
	
	/**
	 * WARNING : sort() should be called before
	 * @return all drawables, in pick order
	 */
	public ArrayList<Drawable3D> getDrawables(){

		return drawables3D;
	}
	
	
	
	
	
	@Override
	public Hits getTopHits() {

		if (topHits.isEmpty())
			return clone();
		return topHits;
		
	}
	
	@Override
	public Hits getTopHits(int depth, int geoN) {
		Hits3D ret = new Hits3D();
		int depthCount = 0;
		int geoNCount = 0;
		for (Iterator<TreeSetOfDrawable3D> iterSet = hitSetSet.iterator(); 
		iterSet.hasNext() && depthCount < depth;) {
			TreeSetOfDrawable3D set = iterSet.next();
			if (set.size()>0)
				depthCount++;
			
			for (Iterator<Drawable3D> iter = set.iterator();
			iter.hasNext() && geoNCount < geoN;) {
				Drawable3D d = iter.next();
				GeoElement geo = d.getGeoElement();
				ret.add(geo);
				geoNCount++;
			}
		}
		return ret;	
	}
	
	/** return the first label hit, if one
	 * @return the first label hit
	 */
	public GeoElement getLabelHit(){
		
		if (hitsLabels.isEmpty())
			return null;
		
		//App.debug("\nlabel:"+hitsLabels.first().zPickMin+"\nfirst hit:"+drawables3D.get(0).zPickMin);
		GeoElement labelGeo = hitsLabels.first().getGeoElement();
		//check if the label hit is the first geo hitted
		if (labelGeo == topHits.get(0))
			return labelGeo;
		// else label is not hitted
		return null;
	}
	
	
	
	
	/**
	 * remove all polygons, if hits are not all instance of GeoCoordSys2D
	 */
	public void removePolygonsIfNotOnlyCS2D(){
		
		//String s = "cs2DCount="+cs2DCount+"/"+(size());
		
		if (size() - cs2DCount > 0) {
			removePolygons();
			//s+="\n"+toString();
			/*
			for (int i = 0; i < size(); ) {
				GeoElement geo = (GeoElement) get(i);
				
				if (geo instanceof GeoCoordSys2D)
					remove(i);
				else
					i++;
			}
			*/
			//Application.debug(s+"\n"+toString());
		}
	}
	
	
	/**
	 * remove all polygons but one
	 */
	@Override
	public void removeAllPolygonsButOne(){
		super.removeAllPolygonsButOne();
		topHits.clear(); //getTopHits() return this
	}
	
	@Override
	public void removeAllPolygonsAndQuadricsButOne(){
		boolean foundTarget = false;
		for (int i = 0 ; i < size() - 1 ; ++i ) {
			GeoElement geo = get(i);
			if (geo.isGeoPolygon() || geo instanceof GeoQuadric3D || geo.isGeoConic()){
				if (foundTarget)
					//not removing when found first time
					remove(i);
				foundTarget = true;
			}
		}
	}
	
	public void removeAllGeoCoordSys2DsButOne(){
		boolean foundTarget = false;
		for (int i = 0 ; i < size() - 1 ; ++i ) {
			GeoElement geo = get(i);
			if (geo instanceof GeoCoordSys2D || geo instanceof GeoQuadric3D){
				if (foundTarget)
					//not removing when found first time
					remove(i);
				foundTarget = true;
			}
		}
	}
	
	@Override
	protected Hits createNewHits() {
		return new Hits3D();
	}	
	

}
