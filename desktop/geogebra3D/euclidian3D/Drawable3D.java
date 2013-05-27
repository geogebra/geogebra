package geogebra3D.euclidian3D;



import geogebra.common.euclidian.DrawableND;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra3D.euclidian3D.opengl.Manager;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Renderer.PickingType;
import geogebra3D.kernel3D.GeoElement3D;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;



/**
 * 3D representation of a {@link GeoElement3D}
 * 
 * 
 * <h3> How to create the drawable of a new element </h3>
 * 
 * We'll call here our new element "GeoNew3D" and create a drawable3D linked to it:
 * <ul>

    <li> It extends {@link Drawable3DCurves} (for points, lines, ...) 
         or {@link Drawable3DSurfaces} (for planes, surfaces, ...)
         <p>
         <code>
         public class DrawNew3D extends ... {
         </code> 
	</li>
    <li> Create new constructor
         <p>
         <code>
         public DrawNew3D(EuclidianView3D a_view3d, GeoNew3D a_new3D){ <br> &nbsp;&nbsp;
            super(a_view3d, a_new3D); <br> 
         }
         </code>
	</li>
    <li> <b> NOTE: </b>  a Drawable3D uses the {@link GeoElement3D#getDrawingMatrix()} method to know where to draw itself
    </li>
    <li> Eclipse will add auto-generated methods :
         <ul>
         <li> getPickOrder() : for picking objects order ; use {@link #DRAW_PICK_ORDER_MAX} first
              <p>
              <code>
                  public int getPickOrder() { <br> &nbsp;&nbsp;
                        return DRAW_PICK_ORDER_MAX; <br> 
                  }
              </code>
         </li>
         <li> for {@link Drawable3DCurves} :
              <p>
              <code>
                public void drawGeometry(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
            	       // call the geometry to be drawn <br>
            	}
            	<br>
            	public void drawGeometryHidden(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
            	       // for hidden part, let it empty first <br>
            	}
            	<br>
            	public void drawGeometryPicked(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
            	       // to show the object is picked, let it empty first <br>
            	}
              </code>
		 </li>
         <li> for {@link Drawable3DSurfaces} :
              <p>
              <code>
            public void drawGeometry(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
                    // call the geometry to be drawn <br>
            }
            <br>
	        void drawGeometryHiding(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
	           // call the geometry that hides other objects <br>&nbsp;&nbsp;
                   // first sets it to :  <br>&nbsp;&nbsp;
                   drawGeometry(renderer);      <br>
	        }
	        <br>
	        public void drawGeometryHidden(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
	           // for hidden part, let it empty first   <br> 
	        }
	        <br>
	        public void drawGeometryPicked(EuclidianRenderer3D renderer) { <br> &nbsp;&nbsp;
                   // to show the object is picked, let it empty first <br>
	        }
	      </code>
	      </li>
	      </ul>
	</li>
	</ul>
	
	<h3> See </h3> 
	<ul>
	<li> {@link EuclidianView3D#createDrawable(GeoElement)} 
	     to make the drawable be created when the GeoElement is created 
	</li>
	</ul>

 * 
 * @author ggb3D
 * 
 *
 * 
 * 
 *
 */
public abstract class Drawable3D extends DrawableND {

	
	private static final boolean DEBUG = false;

	
	//constants for rendering
	/** objects that are picked are drawn with a thickness * PICKED_DILATATION*/
	protected static final float PICKED_DILATATION = 1.3f;	
	/** default radius for drawing 3D points*/
	//protected static final float POINT3D_RADIUS = 1.2f;
	/** points on a path are a little bit more bigger than others */
	protected static final float POINT_ON_PATH_DILATATION = 1.01f;
	/** default thickness of 3D lines, segments, ... */
	//protected static final float LINE3D_THICKNESS = 0.5f;
	/** default thickness of lines of a 3D grid ... */	
	protected static final float GRID3D_THICKNESS = 0.005f;
		
	
	
	/** view3D */
	private EuclidianView3D m_view3D; 
	
	
	/** says if it has to be updated */
	private boolean waitForUpdate;
	
	/** says if the label has to be updated */
	private boolean labelWaitForUpdate;
	
	/** says if this has to be reset */
	protected boolean waitForReset;
	
	
	/** gl index of the geometry */
	private int geomIndex = -1;
	
	/** gl index of the surface geometry (used for elements that have outline and surface) */
	private int surfaceIndex = -1;
	
	//links to the GeoElement
	private GeoElement geo; 
	
	/** label */
	protected DrawLabel3D label;

	//picking
	//private boolean m_isPicked = false;	
	/** max picking value, used for odering elements with openGL picking */
	public float zPickMax; 
	/** min picking value, used for odering elements with openGL picking */	
	public float zPickMin;

	/** (r,g,b,a) vector */
	private Coords 
	color = new Coords(4), 
	colorHighlighted = new Coords(4), 
	surfaceColor = new Coords(4), 
	surfaceColorHighlighted = new Coords(4); 
	
	private static final float EPSILON_Z = 0.0001f;//0.0001f;//10000000; //limit to consider two objects to be at the same place
	
	//constants for picking : have to be from 0 to DRAW_PICK_ORDER_MAX-1, regarding to picking order
	/** default value for picking order */
	static final public int DRAW_PICK_ORDER_MAX = 4;
	/** picking order value for 0-Dimensional objects (points) */
	static final public int DRAW_PICK_ORDER_0D = 0; 
	/** picking order value for 1-Dimensional objects (lines, segments, ...) */
	static final public int DRAW_PICK_ORDER_1D = 1; 
	/** picking order value for 2-Dimensional objects (polygons, planes, ...) */
	static final public int DRAW_PICK_ORDER_2D = 2; 	
	/** picking order value for lists */
	static final public int DRAW_PICK_ORDER_LIST = 3; 
	
	
	

	//type constants
	/** type for drawing default (GeoList, ...) */
	public static final int DRAW_TYPE_DEFAULT = 0;
	/** type for drawing points */
	public static final int DRAW_TYPE_POINTS = DRAW_TYPE_DEFAULT+1;
	/** type for drawing lines, circles, etc. */
	public static final int DRAW_TYPE_CURVES = DRAW_TYPE_POINTS+1;
	/** type for drawing planes, polygons, etc. */
	public static final int DRAW_TYPE_SURFACES = DRAW_TYPE_CURVES+1;
	/** type for drawing polyhedrons, etc. */
	public static final int DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED = DRAW_TYPE_SURFACES+1;
	/** type for drawing  quadrics, etc. */
	public static final int DRAW_TYPE_CLOSED_SURFACES_CURVED = DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED+1;
	/** type for drawing parametric surfaces, etc., that need clipping */
	public static final int DRAW_TYPE_CLIPPED_SURFACES = DRAW_TYPE_CLOSED_SURFACES_CURVED+1;
	/** number max of drawing types */
	public static final int DRAW_TYPE_MAX = DRAW_TYPE_CLIPPED_SURFACES+1;
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	// constructors
	
	/**
	 * construct the Drawable3D with a link to a_view3D
	 * @param view3D the view linked to this
	 */
	public Drawable3D(EuclidianView3D view3D){
		setView3D(view3D);
		
		label = newDrawLabel3D(view3D);
	}
	
	protected DrawLabel3D newDrawLabel3D(EuclidianView3D view3D){
		return new DrawLabel3D(view3D);
	}
	
	
		
	/** 
	 * Call the {@link #update()} method.
	 * @param a_view3D the {@link EuclidianView3D} using this Drawable3D
	 * @param a_geo the {@link GeoElement3D} linked to this GeoElement3D
	 */
	public Drawable3D(EuclidianView3D a_view3D, GeoElement a_geo){
		this(a_view3D);
		setGeoElement(a_geo);
		
		waitForUpdate = true;
		
	}
	
	
	protected void realtimeUpdate() {
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	// update
	

	/** update this according to the {@link GeoElement3D} 
	 *
	 */
	@Override
	final public void update(){

		clearTraceForViewChanged();

		if (waitForUpdateVisualStyle || waitForUpdate){
			updateColors();
			setLabelWaitForUpdate();
			waitForUpdateVisualStyle = false;
		}
		
		boolean isVisible = isVisible();

		if (isVisible){
			updateForView();
		}
		
		
		if (waitForUpdate && isVisible){
			if (updateForItSelf()){
				recordTrace();
				waitForUpdate = false;
			}
			setLabelWaitForUpdate();//TODO remove that
		}
		
		if (labelWaitForUpdate && isLabelVisible()){
			updateLabel();
			labelWaitForUpdate = false;
		}

		
		
		realtimeUpdate();
		
		waitForReset = false;
	}
	
	/**
	 * 
	 * @return true if the geo is traced
	 */
	private boolean hasTrace(){
		
		if (createdByDrawList())
			return ((Drawable3D) getDrawListCreator()).hasTrace();

		if (geo==null){
			return false;
		}

		if (!geo.isTraceable()){
			return false;
		}
		
		return ((Traceable) geo).getTrace();
	}
	
	
	/**
	 * update the label
	 */
	protected void updateLabel(){
		
		label.update(getGeoElement().getLabelDescription(), getView3D().getApplication().getPlainFont(), 
				getGeoElement().getObjectColor(),
				getLabelPosition(),
				getLabelOffsetX(),-getLabelOffsetY());

	}
	
	
	/**
	 * 
	 * @return x offset for the label
	 */
	protected float getLabelOffsetX(){
		return getGeoElement().labelOffsetX;
	}
	
	/**
	 * 
	 * @return y offset for the label
	 */
	protected float getLabelOffsetY(){
		return getGeoElement().labelOffsetY;
	}
	
	/**
	 * update the drawable when view has changed
	 * TODO separate translation/rotation/zoom of the view
	 */
	abstract protected void updateForView();
	
	/**
	 * update the drawable when element has changed
	 * @return true if the update is finished
	 */
	abstract protected boolean updateForItSelf();
	
	
	/**
	 * says that it has to be updated
	 */
	@Override
	public void setWaitForUpdate(){
		
		waitForUpdate = true;
	}
	
	/**
	 * @return true if this wait for update
	 */
	public boolean waitForUpdate(){
		return waitForUpdate;
	}
	
	
	
	/**
	 * says that the label has to be updated
	 */
	public void setLabelWaitForUpdate(){
		labelWaitForUpdate = true;
	}
	
	/**
	 * reset the drawable
	 */
	public void setWaitForReset(){
	
		waitForReset = true;
		label.setWaitForReset();
		setLabelWaitForUpdate();
		setWaitForUpdate();
	}
	
	
	private boolean waitForUpdateVisualStyle = true;
	
	/**
	 * wait for reset color
	 */
	public void setWaitForUpdateVisualStyle(){
		
		waitForUpdateVisualStyle = true;
	}
	
	
	protected void removeGeometryIndex(int index){
		if (!waitForReset){
			if (!hasTrace()){
				doRemoveGeometryIndex(index);
			}
		}

	}

	private void doRemoveGeometryIndex(int index){
		getView3D().getRenderer().getGeometryManager().remove(index);
	}
	
	protected void setGeometryIndex(int index){
		removeGeometryIndex(geomIndex);
		geomIndex = index;
	}
	
	
	protected int getGeometryIndex(){
		return geomIndex;
	}
	
	protected void setSurfaceIndex(int index){
		removeGeometryIndex(surfaceIndex);
		surfaceIndex = index;
	}
	
	
	protected int getSurfaceIndex(){
		return surfaceIndex;
	}	

	
	
	
	/**
	 * get the label position
	 * 
	 * @return the label position
	 */
	public Coords getLabelPosition(){
		
		return getGeoElement().getLabelPosition().copyVector();
	}
	
	/**
	 * get the 3D view
	 * 
	 * @return the 3D view
	 */	
	protected EuclidianView3D getView3D(){
		return m_view3D; 
	}
	
	/**
	 * set the 3D view
	 * 
	 * @param a_view3D the 3D view
	 */		
	protected void setView3D(EuclidianView3D a_view3D){
		m_view3D=a_view3D; 
	}
	
	/**
	 * say if the Drawable3D is visible
	 * 
	 * @return the visibility
	 */
	protected boolean isVisible(){
		
		boolean visible;
		
		if (createdByDrawList()){
			visible = isCreatedByDrawListVisible() && ((Drawable3D) getDrawListCreator()).isVisible();
		}else{
			visible = true;
		}
		

		return visible && 
				(getGeoElement().hasDrawable3D() 
				&& getGeoElement().isEuclidianVisible() 
				&& getGeoElement().isDefined());  
	
	}
	

	

	

	
	
	
	/////////////////////////////////////////////////////////////////////////////
	// drawing
	

	/**
	 * draw the geometry for not hidden parts
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawGeometry(Renderer renderer); 
	
	/**
	 * draw the geometry for picked visibility
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawGeometryPicked(Renderer renderer); 
	
	/**
	 * draw the geometry to show the object is picked (highlighted)
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawGeometryHidden(Renderer renderer); 
	
	/**
	 * draw the outline for hidden parts
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawOutline(Renderer renderer); 
	
	/**
	 * draw the surface for hidden parts (when not transparent)
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawNotTransparentSurface(Renderer renderer); 
	
	/**
	 * sets the matrix, the pencil and draw the geometry for hidden parts
	 * @param renderer the 3D renderer where to draw
	 */	
	public void drawHidden(Renderer renderer){
		
		if(isVisible() 
				&& getGeoElement().getLineTypeHidden() != EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE){


			setHighlightingColor();

			setLineTextureHidden(renderer);

			drawGeometryHidden(renderer);		

		}

	} 	
	
	/**
	 * set dash texture for lines
	 * @param renderer renderer
	 */
	protected void setLineTextureHidden(Renderer renderer){
		if (getGeoElement().getLineTypeHidden()==EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN){
			renderer.getTextures().setDashFromLineType(getGeoElement().getLineType());
		}else{
			renderer.getTextures().setDashFromLineTypeHidden(getGeoElement().getLineType()); 
		}

	}
	
	
	/**
	 * sets the matrix, the pencil and draw the geometry for transparent parts
	 * @param renderer the 3D renderer where to draw
	 */	
	abstract public void drawTransp(Renderer renderer); 
	
	
	/**
	 * sets the matrix, the pencil and draw the geometry for hiding parts
	 * @param renderer the 3D renderer where to draw
	 */	
	abstract public void drawHiding(Renderer renderer); 
	
	

	
	/**
	 * draw for picking, and verify (or not) if pickable
	 * @param renderer
	 * @param intersection says if it's for intersection (in this case, no check for pickable/visible)
	 * @return this, or the DrawList that created it, or null if not pickable/visible
	 */
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection, PickingType type) {
		
		
		if (intersection){ // used for intersection tool
			
			drawGeometryForPickingIntersection(renderer);
			
		}else{ // check pickability only if needed		

			if (!getGeoElement().isPickable())
				return null;
			if(!isVisible())
				return null;
			
			drawGeometryForPicking(renderer, type);
			
		}
		
		

		return getDrawablePicked();
	}
	
	/**
	 * 
	 * @return the drawable that is really picked (e.g. parent list)
	 */
	protected Drawable3D getDrawablePicked(){
		
		if (createdByDrawList())//if it is part of a DrawList3D, the list is picked
			return (Drawable3D) getDrawListCreator();
		
		return this;
		
	}
	
	
	
	/**
	 * draws the geometry for picking (in most case, draws the default geometry)
	 * @param renderer renderer
	 * @param type type of picking
	 */
	protected void drawGeometryForPicking(Renderer renderer, PickingType type){
		drawGeometry(renderer);
	}

	
	
	/**
	 * draws the geometry for picking an intersection
	 * @param renderer renderer
	 */
	protected void drawGeometryForPickingIntersection(Renderer renderer){
		drawGeometryForPicking(renderer, PickingType.POINT_OR_CURVE);
	}

	
	/** draws the label (if any)
     * @param renderer 3D renderer
     * */
	public void drawLabel(Renderer renderer){
		drawLabel(renderer, false);
	}
	
	/** draws the label for picking it 
     * @param renderer 3D renderer
     * */
	public boolean drawLabelForPicking(Renderer renderer){
		return drawLabel(renderer, true);		
	}
    
    /** draws the label (if any)
     * @param renderer 3D renderer
     * @param forPicking says if this method is called for picking
     * @return if picking occurred
     */
    private boolean drawLabel(Renderer renderer, boolean forPicking){

    	
    	if (forPicking) 
    		if(!(getGeoElement().isPickable()))
    			return false;
    	
		if(!isLabelVisible())
			return false;
    	
    	
    	
    	label.draw(renderer);
    	
    	return true;
				
    }
    
    /**
     * @return true if the label is visible
     */
    protected boolean isLabelVisible(){
    	return getGeoElement()!=null && isVisible() && getGeoElement().isLabelVisible();
    }
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////
	// picking
	
	/** get picking order 
	 * @return the picking order
	 */
	abstract public int getPickOrder();
	
	/** say if another object is pickable through this Drawable3D. 
	 * @return if the Drawable3D is transparent
	 */
	abstract public boolean isTransparent();
	
	/** compare this to another Drawable3D with picking 
	 * @param d the other Drawable3D
	 * @param checkPickOrder say if the comparison has to look to pick order
	 * @return 1 if this is in front, 0 if equality, -1 either*/
	public int comparePickingTo(Drawable3D d, boolean checkPickOrder){
		
		/*
		App.debug("\ncheckPickOrder="+checkPickOrder
				+"\nzMin= "+(this.zPickMin)
				+" | zMax= "+(this.zPickMax)
				+" ("+this.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n"
				+"zMin= "+(d.zPickMin)
				+" | zMax= "+(d.zPickMax)
				+" ("+d.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n");
		*/
		
		
		//check if one is transparent and the other not -- ONLY FOR DIFFERENT PICK ORDERS
		if ( (!this.isTransparent()) && (d.isTransparent()) ){
			//return -1;
			if (checkPickOrder && this.getPickOrder()<d.getPickOrder()){
				return -1;
			}
		}else if ( (this.isTransparent()) && (!d.isTransparent()) ){
			//return 1;
			if (checkPickOrder && this.getPickOrder()>d.getPickOrder()){
				return 1;
			}
		}


		//check if one is selected and not the other 
		// -- ONLY when same pickorder to avoid last created geo to get the focus
		if (this.getPickOrder()==d.getPickOrder()){
			if (this.getGeoElement().isSelected() && !d.getGeoElement().isSelected())
				return -1;
			if (!this.getGeoElement().isSelected() && d.getGeoElement().isSelected())
				return 1;
		}
		
		
		//check if the two objects are "mixed"	
		if (this.zPickMax>d.zPickMin && d.zPickMax>this.zPickMin){
			
			if (DEBUG){
				DecimalFormat df = new DecimalFormat("0.000000000");
				App.debug("mixed :\n"
						+"zMin= "+df.format(this.zPickMin)+" | zMax= "+df.format(this.zPickMax)+" ("+this.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n"
						+"zMin= "+df.format(d.zPickMin)+" | zMax= "+df.format(d.zPickMax)+" ("+d.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n");
			}
			
			
			if (checkPickOrder){
				if (this.getPickOrder()<d.getPickOrder())
					return -1;
				if (this.getPickOrder()>d.getPickOrder())
					return 1;
			}
			
			
			
			// if both are points
			if (this.getGeoElement().isGeoPoint() && d.getGeoElement().isGeoPoint()){
				//check if one is on a path and the other not
				if ((((GeoPointND) this.getGeoElement()).hasPath())&&(!((GeoPointND) d.getGeoElement()).hasPath()))
					return -1;
				if ((!((GeoPointND) this.getGeoElement()).hasPath())&&(((GeoPointND) d.getGeoElement()).hasPath()))
					return 1;	
				//check if one is the child of the other
				if (this.getGeoElement().isChildOf(d.getGeoElement()))
					return -1;
				if (d.getGeoElement().isChildOf(d.getGeoElement()))
					return 1;
			}
			
			

			//smaller object is more likely to be picked
			//Note: all objects that are not yet have defined a measure have a default measure = 0
			//so that they are not affected by this comparison.
			/*
			if (Kernel.isGreater(d.getGeoElement().getMeasure(),this.getGeoElement().getMeasure()))
				return -1;
			if (Kernel.isGreater(this.getGeoElement().getMeasure(),d.getGeoElement().getMeasure()))
				return 1;
				*/
			
			
			
		
		}
		

		//finally check if one is before the other
		if (this.zPickMin<d.zPickMin){
			//App.debug("-1");
			return -1;
		}
		if (this.zPickMin>d.zPickMin){
			//App.debug("1");
			return 1;
		}

		//says that the two objects are equal for the comparator
		if (DEBUG){
			DecimalFormat df = new DecimalFormat("0.000000000");
			App.debug("equality :\n"
					+"zMin= "+df.format(this.zPickMin)
					+" | zMax= "+df.format(this.zPickMax)
					+" ("+this.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n"
					+"zMin= "+df.format(d.zPickMin)
					+" | zMax= "+df.format(d.zPickMax)
					+" ("+d.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n");
		}
		return 0;

		
	}
	
	
	/** Comparator for Drawable3Ds */
	static final public class drawableComparator implements Comparator<Drawable3D>{
		public int compare(Drawable3D d1, Drawable3D d2) {
			/*
			Drawable3D d1 = (Drawable3D) arg1;
			Drawable3D d2 = (Drawable3D) arg2;
			*/
			
						
			return d1.comparePickingTo(d2,false);


		}
	}
	
	/** Comparator for sets of Drawable3Ds */
	static final public class setComparator implements Comparator<TreeSet<Drawable3D>>{
		public int compare(TreeSet<Drawable3D> set1, TreeSet<Drawable3D> set2) {

			/*
			TreeSet set1 = (TreeSet) arg1;
			TreeSet set2 = (TreeSet) arg2;
			*/
			
			//check if one set is empty
			if (set1.isEmpty())
				return 1;
			if (set2.isEmpty())
				return -1;
			
			Drawable3D d1 = set1.first();
			Drawable3D d2 = set2.first();
			
						
			return d1.comparePickingTo(d2,true);


		}
	}		
	
	
	/**
	 * @return true if geo is highlighted, or if it is part of a list highlighted
	 */
	public boolean doHighlighting(){
		
		if (getGeoElement().doHighlighting())
			return true;
		if (createdByDrawList())
			return ((Drawable3D) getDrawListCreator()).doHighlighting();
		return false;
	}
	

	/**
	 * sets the color for drawing
	 * and alpha value
	 */
	protected void setHighlightingColor(){
		
		if(doHighlighting()){
			Manager manager = getView3D().getRenderer().getGeometryManager();
			setDrawingColor(manager.getHigthlighting(color,colorHighlighted));
		}else
			setDrawingColor(color);
	}
	
	/**
	 * sets the renderer drawing color
	 * @param color color
	 */
	protected void setDrawingColor(Coords color){
		getView3D().getRenderer().setColor(color);
	}
	
	/**
	 * sets the color of surface for drawing
	 * and alpha value
	 */
	protected void setSurfaceHighlightingColor(){
		
		if(doHighlighting()){
			Manager manager = getView3D().getRenderer().getGeometryManager();
			setDrawingColor(manager.getHigthlighting(surfaceColor,surfaceColorHighlighted));
		}else{
			setDrawingColor(surfaceColor);
		}
	}	
	

	
	private static final double ALPHA_MIN_HIGHLIGHTING = 0.25;
	private static final double LIGHT_COLOR = 3*0.5;
	
	
	
	protected void updateColors(){
		setColors(alpha,color,colorHighlighted);
	}
	
	protected void setColorsOutlined(){
		setColors(1,color,colorHighlighted);//for outline
		setColors(alpha,surfaceColor,surfaceColorHighlighted);
	}
	
	protected void setColors(double alpha, Coords color, Coords colorHighlighted){
		Color c = geogebra.awt.GColorD.getAwtColor(getGeoElement().getObjectColor());
		color.set(new Coords((double) c.getRed()/255, (double) c.getGreen()/255, (double) c.getBlue()/255,alpha));

		if (getView3D().isGrayScaled())
			color.convertToGrayScale();
		
		
		
		//creates corresponding color for highlighting
		
		double r = color.getX();
		double g = color.getY();
		double b = color.getZ();
		double d = r+g+b;
		
		Coords color2;
		double distance;
		
		if (d>LIGHT_COLOR){//color is closer to white : darken it
			distance = Math.sqrt(r*r+g*g+b*b); //euclidian distance to black
			color2 = new Coords(0, 0, 0, color.getW()); //black
		}else{//color is closer to black : lighten it
			r=1-r;g=1-g;b=1-b;
			distance = Math.sqrt(r*r+g*g+b*b); //euclidian distance to white
			color2 = new Coords(1, 1, 1, color.getW()); //white
		}
		
		double s = getColorShift()/distance;
		colorHighlighted.set(color.mul(1-s).add(color2.mul(s)));

		//sufficient alpha to be seen
		if (colorHighlighted.getW()<ALPHA_MIN_HIGHLIGHTING)
			colorHighlighted.setW(ALPHA_MIN_HIGHLIGHTING);
	}
	
	
	/** alpha value for rendering transparency */
	private float alpha = 1f;
	
	protected void setAlpha(float alpha){
		this.alpha = alpha;
	}
	
	protected float getAlpha(){
		return alpha;
	}

	protected void updateAlpha(){
		//only used by surfaces
		//use 1-(1-alpha)^(1/3) because transparent parts are drawn twice
		setAlpha((float) (1-Math.pow(1-getGeoElement().getAlphaValue(),1./3.)));
		//setAlpha(getGeoElement().getAlphaValue());
	}


	/**
	 * 
	 * @return true if has alpha that leads to a transparent surface
	 */
	protected boolean hasTransparentAlpha(){
		return getAlpha()>0 && getAlpha() < 1;
	}
	
	
	
	abstract protected double getColorShift();
	
	
	/////////////////////////////////////////////////////////////////////////////
	// links to the GeoElement
	

    
    /**
     * get the GeoElementInterface linked to the Drawable3D 
     * @return the GeoElement3DInterface linked to
     */  
    @Override
	public GeoElement getGeoElement() {
        return geo;
    }   
    
    
    /**
     * set the GeoElement linked to the Drawable3D
     * @param a_geo the GeoElement
     */
    public void setGeoElement(GeoElement a_geo) {
        this.geo = a_geo;
        //((GeoElement3DInterface) a_geo).setDrawable3D(this);
    } 
    
    
    
    
    

    /////////////////////////////
    // TYPE
    
    
    /**
     * add this to the correct lists
     * @param lists
     */
    abstract public void addToDrawable3DLists(Drawable3DLists lists);
    
	protected void addToDrawable3DLists(Drawable3DLists lists, int type){
		lists.getList(type).add(this);
	}
	
    /**
     * remove this from the correct lists
     * @param lists
     */
    abstract public void removeFromDrawable3DLists(Drawable3DLists lists);
    
    protected void removeFromDrawable3DLists(Drawable3DLists lists, int type){
    	lists.getList(type).remove(this);
    }

    //////////////////////////////
    // FOR PREVIEWABLE INTERFACE
    
    /**
     * remove this from the draw list 3D
     */
	public void disposePreview() {
		getView3D().remove(this);		
		
	}

 	
	/** unused for 3D
	 * @param g2
	 */
	public void drawPreview(geogebra.common.awt.GGraphics2D g2) {
		
	}
	
	
	
	
	
	/////////////////////////////////////////
	// TIME
	
    /** time the last update (for drawing ends) occured */
	private long time=0 ;
	
	/** delay to wait before doing the update */
	private static final int TIME_WAIT = 500;
	/** delay for the update to have ended */
	private static final int TIME_END = TIME_WAIT+500;
	/** duration of the update */
	protected static final int TIME_DURATION = TIME_END-TIME_WAIT;
	/** time factor for min/max calculation */
	protected static final float TIME_FACTOR = 1f/TIME_DURATION;


	
	protected void setTime(){

		time = System.currentTimeMillis();
	}
	
	protected long getDeltaT(){
		long deltaT = System.currentTimeMillis()-(time+TIME_WAIT);
		
		return deltaT;
	}
	
	protected boolean timesUpForUpdate(){
		return (System.currentTimeMillis()>time+TIME_END);
	}

	
	//////////////////////
	// LAST PICKING TYPE
	//////////////////////

	/** 
	 * set last picking type
	 * @param type picking type
	 */
	public void setPickingType(PickingType type){
		// nothing to do for almost all drawables
	}
	
	/**
	 * 
	 * @return last picking type
	 */
	abstract public PickingType getPickingType();
	/*
	public PickingType getPickingType(){
		return PickingType.POINT_OR_CURVE;
	}
	*/
    
	

	private TreeMap<TraceSettings,ArrayList<TraceIndex>> trace;
	
	private TraceSettings traceSettingsCurrent;
	
	private class TraceIndex{
		public int geom, surface;
		public TraceIndex(int geom, int surface){
			this.geom = geom;
			this.surface = surface;
		}
		
	}
	
	private TraceIndex lastTraceIndex;
	private ArrayList<TraceIndex> lastTraceIndices;
	
	
	
	final protected void recordTrace(){

		if (!hasTrace()){
			return;
		}
		
		if (trace == null){
			trace = new TreeMap<TraceSettings, ArrayList<TraceIndex>>(); 
			traceSettingsCurrent = new TraceSettings(color, alpha);
		}
		
		traceSettingsCurrent.setAlpha(alpha);
		ArrayList<TraceIndex> indices = trace.get(traceSettingsCurrent);
		if (indices==null){
			indices = new ArrayList<TraceIndex>();
			trace.put(traceSettingsCurrent.clone(),indices);
		}
		
		//really add trace at next current geometry record
		if (lastTraceIndices != null){
			lastTraceIndices.add(lastTraceIndex);
		}
		
		lastTraceIndices = indices;
		lastTraceIndex = new TraceIndex(geomIndex, surfaceIndex);

	}
	
	
	/**
	 * draw traces
	 * @param renderer renderer
	 */
	final protected void drawTracesOutline(Renderer renderer){

		if (trace==null){
			return;
		}

		
		for (TraceSettings settings : trace.keySet()){
			ArrayList<TraceIndex> indices = trace.get(settings);
			setDrawingColor(settings.getColor());
			//App.debug(indices.size());
			for (TraceIndex index : indices){
				renderer.getGeometryManager().draw(index.geom);
			}
		}
		
	}
	
	/**
	 * draw traces
	 * @param renderer renderer
	 */
	final protected void drawTracesTranspSurface(Renderer renderer){

		if (trace==null){
			return;
		}


		for (TraceSettings settings : trace.keySet()){
			ArrayList<TraceIndex> indices = trace.get(settings);
			double a = settings.getAlpha();
			if (a>0 && a<1){
				Coords c = settings.getColor().copyVector();
				c.set(4, a);
				setDrawingColor(c);
				for (TraceIndex index : indices){
					renderer.getGeometryManager().draw(index.surface);
				}
			}
		}
		
	}
	
	/**
	 * draw traces
	 * @param renderer renderer
	 */
	final protected void drawTracesHidingSurface(Renderer renderer){

		if (trace==null){
			return;
		}


		for (TraceSettings settings : trace.keySet()){
			ArrayList<TraceIndex> indices = trace.get(settings);
			double a = settings.getAlpha();
			if (a>0 && a<1){
				for (TraceIndex index : indices){
					renderer.getGeometryManager().draw(index.surface);
				}
			}
		}
		
	}
	
	
	/**
	 * draw traces
	 * @param renderer renderer
	 */
	final protected void drawTracesNotTranspSurface(Renderer renderer){

		if (trace==null){
			return;
		}


		for (TraceSettings settings : trace.keySet()){
			ArrayList<TraceIndex> indices = trace.get(settings);
			double a = settings.getAlpha();
			if (a>=1){
				setDrawingColor(settings.getColor());
				for (TraceIndex index : indices){
					renderer.getGeometryManager().draw(index.surface);
				}
			}
		}
		
	}
	

	final private void clearTraceForViewChanged(){
		if (getView3D().viewChangedByZoom() || getView3D().viewChangedByTranslate()){
			if (trace!=null){
				//remove all geometry indices from openGL manager
				for (ArrayList<TraceIndex> indices : trace.values()){
					for (TraceIndex index : indices){
						doRemoveGeometryIndex(index.geom);
						doRemoveGeometryIndex(index.surface);
					}
				}
				
				
				trace.clear();
				lastTraceIndices = null;
			}
		}
	}
	
	
	
	
	
	private class TraceSettings implements Comparable<TraceSettings>{
		
		private Coords c;
		private double a;
		
		public TraceSettings(Coords c, double a){
			this.c = c;
			this.a = a;
		}
		
		@Override
		public TraceSettings clone(){
			Coords c1 = this.c.copyVector();
			return new TraceSettings(c1, a);
		}
		
		public Coords getColor(){
			return c;
		}
		
		public double getAlpha(){
			return a;
		}
		
		public void setAlpha(double a){
			this.a = a;
		}
		
		private int getInt(double value){
			return (int) (256*value);
		}

		public int compareTo(TraceSettings settings) {
			
			// compare colors (r,g,b)
			for (int i=1; i<=3; i++){
				int v1 = getInt(this.c.get(i));
				int v2 = getInt(settings.c.get(i));
				if(v1<v2){
					return -1;
				}
				if(v1>v2){
					return 1;
				}
			}
			
			
			// compare alpha
			int v1 = getInt(this.a);
			int v2 = getInt(settings.a);
			if(v1<v2){
				return -1;
			}
			if(v1>v2){
				return 1;
			}

			
			return 0;
		}
	}
	
	
	
}




