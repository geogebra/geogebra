package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hits3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Geometry3DGetterManager;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * 3D representation of a {@link GeoElement3D}
 * 
 * 
 * <h3>How to create the drawable of a new element</h3>
 * 
 * We'll call here our new element "GeoNew3D" and create a drawable3D linked to
 * it:
 * <ul>
 * 
 * <li>It extends {@link Drawable3DCurves} (for points, lines, ...) or
 * {@link Drawable3DSurfaces} (for planes, surfaces, ...)
 * <p>
 * <code>
         public class DrawNew3D extends ... {
         </code></li>
 * <li>Create new constructor
 * <p>
 * <code>
         public DrawNew3D(EuclidianView3D a_view3d, GeoNew3D a_new3D){ <br> &nbsp;&nbsp;
            super(a_view3d, a_new3D); <br> 
         }
         </code></li>
 * <li>Eclipse will add auto-generated methods :
 * <ul>
 * <li>getPickOrder() : for picking objects order ; use
 * {@link #DRAW_PICK_ORDER_MAX} first
 * <p>
 * <code>
                  public int getPickOrder() { <br> &nbsp;&nbsp;
                        return DRAW_PICK_ORDER_MAX; <br> 
                  }
              </code></li>
 * <li>for {@link Drawable3DCurves} :
 * <p>
 * <code>
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
              </code></li>
 * <li>for {@link Drawable3DSurfaces} :
 * <p>
 * <code>
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
	      </code></li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <h3>See</h3>
 * <ul>
 * <li>{@link EuclidianView3D#newDrawable(GeoElementND)} to make the drawable be
 * created when the GeoElement is created</li>
 * </ul>
 * 
 * 
 * @author ggb3D
 */
public abstract class Drawable3D extends DrawableND {
	// constants for rendering
	/**
	 * objects that are picked are drawn with a thickness * PICKED_DILATATION
	 */
	protected static final float PICKED_DILATATION = 1.3f;
	/** default radius for drawing 3D points */
	// protected static final float POINT3D_RADIUS = 1.2f;
	/** points on a path are a little bit more bigger than others */
	protected static final float POINT_ON_PATH_DILATATION = 1.01f;
	/** default thickness of 3D lines, segments, ... */
	// protected static final float LINE3D_THICKNESS = 0.5f;
	/** default thickness of lines of a 3D grid ... */
	protected static final float GRID3D_THICKNESS = 0.005f;

	/** value for surface / geometry index when not reusable */
	protected static final int NOT_REUSABLE_INDEX = -1;

	private static final int ALPHA_MIN_HIGHLIGHTING = 64;
	private static final int LIGHT_COLOR = 3 * 127;
	protected final static double COLOR_SHIFT_SURFACE = 0.75; // 0.2
	protected final static double COLOR_SHIFT_CURVES = 0.75; // 0.2
	protected final static double COLOR_SHIFT_POINTS = 0.86; // mostly sqrt(3)/2
	protected final static double COLOR_SHIFT_NONE = 0;

	/** view3D */
	private EuclidianView3D m_view3D;

	/** says if it has to be updated */
	private boolean waitForUpdate;
	private boolean waitForUpdateVisualStyle = true;
	private boolean waitForUpdateColor = false;
	private boolean waitForUpdateVisibility = false;
	/** geometries have been set visible */
	protected boolean geometriesSetVisible;

	/** says if the label has to be updated */
	private boolean labelWaitForUpdate;

	/** says if this has to be reset */
	protected boolean waitForReset;

	/** gl index of the geometry */
	private int geomIndex = -1;

	/**
	 * gl index of the surface geometry (used for elements that have outline and
	 * surface)
	 */
	private int surfaceIndex = -1;

	// links to the GeoElement
	private GeoElement geo;

	/** label */
	protected DrawLabel3D label;

	// picking
	// private boolean m_isPicked = false;
	/**
	 * most far picking value, used for ordering elements with openGL picking
	 */
	private double zPickFar;
	/** nearest picking value, used for ordering elements with openGL picking */
	private double zPickNear;

	private double positionOnHitting;

	private boolean relevantPickingValues;

	/** (r,g,b,a) vector */
	protected GColor[] color = new GColor[]{GColor.BLACK, GColor.BLACK};
	protected GColor[] surfaceColor = new GColor[]{GColor.BLACK, GColor.BLACK};
	private GColor tmpColor2;
	protected Trace trace;
	private PickingType lastPickingType = PickingType.POINT_OR_CURVE;
	/** alpha value for rendering transparency */
	private int alpha = 255;

	/** simple traces stack used for packed buffers */
	protected LinkedList<Integer> tracesPackingBuffer;

	// constants for picking : have to be from 0 to DRAW_PICK_ORDER_MAX-1,
	// regarding to picking order
	/** default value for picking order */
	static final public int DRAW_PICK_ORDER_MAX = 4;
	/** picking order value for points */
	static final public int DRAW_PICK_ORDER_POINT = 0;
	/** picking order value for texts */
	static final public int DRAW_PICK_ORDER_TEXT = 1;
	/** picking order value for path objects (lines, segments, ...) */
	static final public int DRAW_PICK_ORDER_PATH = 2;
	/** picking order value for surface objects (polygons, planes, ...) */
	static final public int DRAW_PICK_ORDER_SURFACE = 3;

	// type constants
	/** type for drawing default (GeoList, ...) */
	public static final int DRAW_TYPE_DEFAULT = 0;
	/** type for drawing points */
	public static final int DRAW_TYPE_POINTS = DRAW_TYPE_DEFAULT + 1;
	/** type for drawing lines, circles, etc. */
	public static final int DRAW_TYPE_CURVES = DRAW_TYPE_POINTS + 1;
	/** type for drawing clipped curves (functions) */
	public static final int DRAW_TYPE_CLIPPED_CURVES = DRAW_TYPE_CURVES + 1;
	/** type for drawing planes, polygons, etc. */
	public static final int DRAW_TYPE_SURFACES = DRAW_TYPE_CLIPPED_CURVES + 1;
	/** type for drawing polyhedrons, etc. */
	public static final int DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED = DRAW_TYPE_SURFACES
			+ 1;
	/** type for drawing quadrics, etc. */
	public static final int DRAW_TYPE_CLOSED_SURFACES_CURVED = DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED
			+ 1;
	/** type for drawing parametric surfaces, etc., that need clipping */
	public static final int DRAW_TYPE_CLIPPED_SURFACES = DRAW_TYPE_CLOSED_SURFACES_CURVED
			+ 1;
	/** type for drawing texts */
	public static final int DRAW_TYPE_TEXTS = DRAW_TYPE_CLIPPED_SURFACES + 1;
	/** type for drawing lists */
	public static final int DRAW_TYPE_LISTS = DRAW_TYPE_TEXTS + 1;
	/** number max of drawing types */
	public static final int DRAW_TYPE_MAX = DRAW_TYPE_LISTS + 1;

	/** visibility as intersection curve */
	protected boolean intersectionCurveVisibility;

	// /////////////////////////////////////////////////////////////////////////////
	// constructors

	/**
	 * construct the Drawable3D with a link to a_view3D
	 * 
	 * @param view3D
	 *            the view linked to this
	 */
	public Drawable3D(EuclidianView3D view3D) {
		setView3D(view3D);

		label = newDrawLabel3D(view3D);
        relevantPickingValues = false;
	}

	protected DrawLabel3D newDrawLabel3D(EuclidianView3D view3D) {
		return new DrawLabel3D(view3D, this);
	}

	/**
	 * Call the {@link #update()} method.
	 * 
	 * @param a_view3D
	 *            the {@link EuclidianView3D} using this Drawable3D
	 * @param a_geo
	 *            the {@link GeoElement3D} linked to this GeoElement3D
	 */
	public Drawable3D(EuclidianView3D a_view3D, GeoElement a_geo) {
		this(a_view3D);
		init(a_geo);

	}

	/**
	 * init
	 * 
	 * @param geoElement
	 *            geo
	 */
	protected void init(GeoElement geoElement) {
		setGeoElement(geoElement);
		waitForUpdate = true;

	}

	// /////////////////////////////////////////////////////////////////////////////
	// update

	/**
	 * update for view when not visible
	 */
	protected void updateForViewNotVisible() {
		// not implemented by default
	}

	/**
	 * update this according to the {@link GeoElement3D}
	 *
	 */
	@Override
	public void update() {
		clearTraceForViewChanged();
		if (isVisible()) {
			if (waitForUpdateVisualStyle || waitForUpdate) {
				updateColors();
				setLabelWaitForUpdate();
				waitForUpdateVisualStyle = false;
			}

			updateForViewVisible();

			if (waitForUpdate) {
				if (updateForItSelf()) {
					recordTrace();
					waitForUpdate = false;
					waitForUpdateColor = false;
					waitForUpdateVisibility = false;
					geometriesSetVisible = true;
				} else {
					// we need a new repaint after current one to refine the
					// drawable (used DrawSurface3DOld)
					getView3D().waitForNewRepaint();
				}
				setLabelWaitForUpdate(); // TODO remove that
			}

			if (waitForUpdateColor) {
				updateGeometriesColor();
				setLabelWaitForUpdate();
				waitForUpdateColor = false;
				waitForUpdateVisibility = false;
			} else if (waitForUpdateVisibility) {
				updateGeometriesVisibility();
				waitForUpdateVisibility = false;
			}

			if (isLabelVisible()) {
				// make sure we won't use packing for labels
				getView3D().getRenderer().getGeometryManager().endPacking();
				if (labelWaitForUpdate) {
					updateLabel();
					updateLabelPosition();
					labelWaitForUpdate = false;
				} else if (getView3D().viewChanged()) {
					updateLabelPosition();
				}
			}
		} else {
			updateForViewNotVisible();
		}
		waitForReset = false;
	}

	/**
	 * 
	 * @return true if the geo is traced
	 */
	final protected boolean hasTrace() {

		if (createdByDrawList()) {
			return ((Drawable3D) getDrawListCreator()).hasTrace();
		}

		if (getGeoElement() == null) {
			return false;
		}

		if (!getGeoElement().isTraceable()) {
			return false;
		}

		return ((Traceable) getGeoElement()).getTrace();
	}

	/**
	 * 
	 * @return true if something is recorded in trace
	 */
	final protected boolean hasRecordedTrace() {
		if (shouldBePackedForManager()) {
			return tracesPackingBuffer != null
					&& !tracesPackingBuffer.isEmpty();
		}
		return trace != null && !trace.isEmpty();
	}

	/**
	 * update the label
	 */
	protected void updateLabel() {

		label.update(getGeoElement().getLabelDescription(),
				getView3D().getFontPoint(), getGeoElement().getObjectColor(),
				getLabelPosition(), getLabelOffsetX(), -getLabelOffsetY(), 0);

	}

	/**
	 * update label position on screen
	 */
	protected void updateLabelPosition() {

		label.updatePosition(getView3D().getRenderer());

	}

	/**
	 * 
	 * @return x offset for the label
	 */
	protected float getLabelOffsetX() {
		return getGeoElement().labelOffsetX;
	}

	/**
	 * 
	 * @return y offset for the label
	 */
	protected float getLabelOffsetY() {
		return getGeoElement().labelOffsetY;
	}

	/**
	 * update the drawable when view has changed and drawable visible
	 */
	protected void updateForViewVisible() {
		updateForView();
	}

	/**
	 * update the drawable when view has changed
	 */
	abstract protected void updateForView();

	/**
	 * update the drawable when element has changed
	 * 
	 * @return true if the update is finished
	 */
	abstract protected boolean updateForItSelf();

	/**
	 * for logic hitting, we may need an update
	 */
	public void updateForHitting() {
		updateForItSelf();
	}

	/**
	 * says that it has to be updated
	 */
	@Override
	public void setWaitForUpdate() {
		waitForUpdate = true;
	}

	/**
	 * @return true if this wait for update
	 */
	final public boolean waitForUpdate() {
		return waitForUpdate;
	}

	/**
	 * says that the label has to be updated
	 */
	final public void setLabelWaitForUpdate() {
		labelWaitForUpdate = true;
	}

	/**
	 * says that the label has to be reset
	 */
	public void setLabelWaitForReset() {
		label.setWaitForReset();
		setLabelWaitForUpdate();
	}

	/**
	 * reset the drawable
	 */
	public void setWaitForReset() {
		// reset geometry indices
		geomIndex = -1;
		surfaceIndex = -1;

		waitForReset = true;
		label.setWaitForReset();
		setLabelWaitForUpdate();
		setWaitForUpdate();
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {
		waitForUpdateVisualStyle = true;
	}

	/**
	 * wait for update color
	 */
	protected void setWaitForUpdateColor() {
		waitForUpdateColor = true;
	}

	/**
	 * update color
	 */
	protected void updateGeometriesColor() {
		// not implemented here
	}

	/**
	 * 
	 * @param updateSurface
	 *            if surface has to be updated too
	 */
	protected void updateGeometriesColor(boolean updateSurface) {
		updateColors();
		getView3D().getRenderer().getGeometryManager().updateColorAndLayer(
				getColor(), Renderer.LAYER_DEFAULT, getGeometryIndex());
		if (updateSurface) {
			getView3D().getRenderer().getGeometryManager().updateColorAndLayer(
					getSurfaceColor(), getLayer(), getSurfaceIndex());
		}
		if (!isVisible()) {
			setGeometriesVisibility(false);
		}
	}

	/**
	 * wait for update visibility
	 */
	protected void setWaitForUpdateVisibility() {
		waitForUpdateVisibility = true;
	}

	/**
	 * update visibility
	 */
	protected void updateGeometriesVisibility() {
		// not implemented here
	}

	/**
	 * set geomeotry visibility
	 * 
	 * @param visible
	 *            geometry visibility flag
	 */
	protected void setGeometriesVisibility(boolean visible) {
		// not implemented here
	}

	/**
	 * set geometry visibility with surface
	 * 
	 * @param visible
	 *            geometry visibility flag
	 */
	protected void setGeometriesVisibilityWithSurface(boolean visible) {
		getView3D().getRenderer().getGeometryManager().updateVisibility(visible,
				getSurfaceIndex(), getSurfaceColor().getAlpha(), getLayer());
		setGeometriesVisibilityNoSurface(visible);
	}

	/**
	 * set geometry visibility with no surface
	 * 
	 * @param visible
	 *            geometry visibility flag
	 */
	protected void setGeometriesVisibilityNoSurface(boolean visible) {
		getView3D().getRenderer().getGeometryManager().updateVisibility(visible,
				getGeometryIndex(), 255, Renderer.LAYER_DEFAULT);
		geometriesSetVisible = visible;
	}

	protected void removeGeometryIndex(int index) {
		if (!waitForReset) {
			if (!hasTrace()) {
				doRemoveGeometryIndex(index);
			}
		}

	}

	protected void doRemoveGeometryIndex(int index) {
		getView3D().getRenderer().getGeometryManager().remove(index);
	}

	protected void setGeometryIndex(int index) {
		removeGeometryIndex(geomIndex);
		geomIndex = index;
	}

	/**
	 * set geometry index to make it not visible
	 */
	protected void setGeometryIndexNotVisible() {
		setGeometryIndex(NOT_REUSABLE_INDEX);
	}

	/**
	 * @return geometry index
	 */
	final public int getGeometryIndex() {
		return geomIndex;
	}

	/**
	 * 
	 * @return current surface index if reusable (if no trace)
	 */
	protected int getReusableGeometryIndex() {
		if (hasTrace()) {
			return NOT_REUSABLE_INDEX;
		}

		return getGeometryIndex();
	}

	final protected void setSurfaceIndex(int index) {
		removeGeometryIndex(surfaceIndex);
		surfaceIndex = index;
	}

	/**
	 * set surface index to make it not visible
	 */
	protected void setSurfaceIndexNotVisible() {
		setSurfaceIndex(NOT_REUSABLE_INDEX);
	}

	/**
	 * @return surface index
	 */
	public final int getSurfaceIndex() {
		return surfaceIndex;
	}

	/**
	 * 
	 * @return current surface index if reusable (if no trace)
	 */
	public int getReusableSurfaceIndex() {
		if (hasTrace()) {
			return NOT_REUSABLE_INDEX;
		}

		return getSurfaceIndex();
	}

	/**
	 * get the label position
	 * 
	 * @return the label position
	 */
	public Coords getLabelPosition() {
		return getGeoElement().getLabelPosition();
	}

	/**
	 * get the 3D view
	 * 
	 * @return the 3D view
	 */
	protected EuclidianView3D getView3D() {
		return m_view3D;
	}

	/**
	 * set the 3D view
	 * 
	 * @param a_view3D
	 *            the 3D view
	 */
	protected void setView3D(EuclidianView3D a_view3D) {
		m_view3D = a_view3D;
	}

	/**
	 * say if the Drawable3D is visible
	 * 
	 * @return the visibility
	 */
	public boolean isVisible() {
		boolean visible;

		if (createdByDrawList()) {
			visible = isCreatedByDrawListVisible()
					&& ((Drawable3D) getDrawListCreator()).isVisible();
		} else {
			visible = true;
		}

		return visible && hasGeoElementVisible();

	}

	/**
	 * 
	 * @return true if geo is visible (and defined)
	 */
	protected boolean hasGeoElementVisible() {
		return getGeoElement().hasDrawable3D()
				&& getGeoElement().isEuclidianVisible()
				&& getGeoElement().isDefined();
	}

	/**
	 * 
	 * @return geo layer
	 */
	public int getLayer() {
		if (createdByDrawList()) {
			return ((Drawable3D) getDrawListCreator()).getLayer();
		}

		return getGeoElement().getLayer();
	}

	// ///////////////////////////////////////////////////////////////////////////
	// drawing

	/**
	 * draw the geometry for not hidden parts
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	abstract public void drawGeometry(Renderer renderer);

	/**
	 * draw the geometry to show the object is picked (highlighted)
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	abstract public void drawGeometryHidden(Renderer renderer);

	/**
	 * draw the outline for hidden parts
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	abstract public void drawOutline(Renderer renderer);

	/**
	 * draw the surface for hidden parts (when not transparent)
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	abstract public void drawNotTransparentSurface(Renderer renderer);

	/**
	 * sets the matrix, the pencil and draw the geometry for hidden parts
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	public void drawHidden(Renderer renderer) {
		if (isVisible() && getGeoElement()
				.getLineTypeHidden() != EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE) {

			setHighlightingColor();

			setLineTextureHidden(renderer);

			drawGeometryHidden(renderer);

		}

	}

	/**
	 * draw in export format through renderer
	 * 
	 * @param exportToPrinter3D
	 *            exporter
	 * @param exportSurface
	 *            true if it is the surface that it is exported
	 */
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		// default : do nothing
	}

	/**
	 * translate to getter through renderer
	 * 
	 * @param manager
	 *            geometry getter manager
	 * @param exportSurface
	 *            true if it is the surface that it is exported
	 */
	public void export(Geometry3DGetterManager manager, boolean exportSurface) {
		// default : do nothing
	}

	/**
	 * set dash texture for lines
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void setLineTextureHidden(Renderer renderer) {
		if (getGeoElement()
				.getLineTypeHidden() == EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN) {
			renderer.getTextures()
					.setDashFromLineType(getGeoElement().getLineType());
		} else {
			renderer.getTextures()
					.setDashFromLineTypeHidden(getGeoElement().getLineType());
		}

	}

	/**
	 * sets the matrix, the pencil and draw the geometry for transparent parts
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	abstract public void drawTransp(Renderer renderer);

	/**
	 * sets the matrix, the pencil and draw the geometry for hiding parts
	 * 
	 * @param renderer
	 *            the 3D renderer where to draw
	 */
	abstract public void drawHiding(Renderer renderer);

	/**
	 * draw for picking, and verify (or not) if pickable
	 * 
	 * @param renderer
	 *            renderer
	 * @param intersection
	 *            says if it's for intersection (in this case, no check for
	 *            pickable/visible)
	 * @param type
	 *            picking type
	 * @return this, or the DrawList that created it, or null if not
	 *         pickable/visible
	 */
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection,
			PickingType type) {

		// check pickability

		if (!isVisible()) {
			return null;
		}

		if (intersection) { // used for intersection tool

			drawGeometryForPickingIntersection(renderer);

		} else {

			if (!getGeoElement().isPickable()) {
				return null;
			}

			drawGeometryForPicking(renderer, type);

		}

		return getDrawablePicked();
	}

	/**
	 * 
	 * @return the drawable that is really picked (e.g. parent list)
	 */
	protected Drawable3D getDrawablePicked() {
		return getDrawablePicked(this);
	}

	/**
	 * 
	 * @param drawableSource
	 *            drawable at source of picking
	 * @return the drawable that is really picked (e.g. parent list)
	 */
	protected Drawable3D getDrawablePicked(Drawable3D drawableSource) {

		if (createdByDrawList()) { // if it is part of a DrawList3D, the list is
									// picked
			return ((Drawable3D) getDrawListCreator())
					.getDrawablePicked(drawableSource);
		}

		return this;

	}

	/**
	 * draws the geometry for picking (in most case, draws the default geometry)
	 * 
	 * @param renderer
	 *            renderer
	 * @param type
	 *            type of picking
	 */
	protected void drawGeometryForPicking(Renderer renderer, PickingType type) {
		drawGeometry(renderer);
	}

	/**
	 * draws the geometry for picking an intersection
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void drawGeometryForPickingIntersection(Renderer renderer) {
		drawGeometryForPicking(renderer, PickingType.POINT_OR_CURVE);
	}

	/**
	 * draws the label (if any)
	 * 
	 * @param renderer
	 *            3D renderer
	 */
	public void drawLabel(Renderer renderer) {
		drawLabel(renderer, false);
	}

	/**
	 * draws the label for picking it
	 * 
	 * @param renderer
	 *            3D renderer
	 * @return if picking occured
	 */
	public boolean drawLabelForPicking(Renderer renderer) {
		return drawLabel(renderer, true);
	}

	/**
	 * draws the label (if any)
	 * 
	 * @param renderer
	 *            3D renderer
	 * @param forPicking
	 *            says if this method is called for picking
	 * @return if picking occurred
	 */
	private boolean drawLabel(Renderer renderer, boolean forPicking) {

		if (forPicking) {
			if (!(getGeoElement().isPickable())) {
				return false;
			}
		}

		if (!isLabelVisible()) {
			return false;
		}

		label.draw(renderer, forPicking);

		return true;

	}

	/**
	 * @return true if the label is visible
	 */
	protected boolean isLabelVisible() {
		return getGeoElement() != null && isVisible()
				&& getGeoElement().isLabelVisible();
	}

	// ///////////////////////////////////////////////////////////////////////////
	// picking

	/**
	 * get picking order
	 * 
	 * @return the picking order
	 */
	abstract public int getPickOrder();

	/**
	 * say if another object is pickable through this Drawable3D.
	 * 
	 * @return if the Drawable3D is transparent
	 */
	abstract public boolean isTransparent();

    /**
     *
     * @return true if it has relevant values for picking
     */
    public boolean hasRelevantPickingValues() {
        return relevantPickingValues;
    }

	/**
	 * compare this to another Drawable3D with picking
	 * 
	 * @param d
	 *            the other Drawable3D
	 * @param checkPickOrder
	 *            say if the comparison has to look to pick order
	 * @return 1 if this is in front, 0 if equality, -1 either
	 */
    public int comparePickingTo(Drawable3D d, boolean checkPickOrder) {

		// Log.debug("\ncheckPickOrder=" + checkPickOrder + "\n" + "zPickNear= "
		// + (this.zPickNear) + " | zPickFar= " + (this.zPickFar)
		// + " | relevant= " + (this.hasRelevantPickingValues()) + " ("
		// + this.getGeoElement() + ") " + this + "\n" + "zPickFar= "
		// + (d.zPickNear) + " | zPickFar= " + (d.zPickFar)
		// + " | relevant= " + (d.hasRelevantPickingValues()) + " ("
		// + d.getGeoElement() + ") " + d + "\n");

        if (hasRelevantPickingValues() && !d.hasRelevantPickingValues()) {
            return -1;
        }

        if (!hasRelevantPickingValues() && d.hasRelevantPickingValues()) {
            return 1;
        }

		// check if one is transparent and the other not -- ONLY FOR DIFFERENT
		// PICK ORDERS
		if (getView3D().getEuclidianController()
				.checkTransparencyForSortingDrawables()) {
			if ((!this.isTransparent()) && (d.isTransparent())) {
				if (checkPickOrder && this.getPickOrder() < d.getPickOrder()) {
					return -1;
				}
			} else if ((this.isTransparent()) && (!d.isTransparent())) {
				if (checkPickOrder && this.getPickOrder() > d.getPickOrder()) {
					return 1;
				}
			}
		}

		// check if one is selected (and moveable) and not the other
		// to keep handling last moved or selected geo
		// -- ONLY when same pickorder to avoid last created geo to get the
		// focus
		if (this.getPickOrder() == d.getPickOrder()) {

			GeoElement thisGeo = this.getGeoElement();
			GeoElement otherGeo = d.getGeoElement();
			// check one (only) is selected
			if (thisGeo.isSelected() && !otherGeo.isSelected()) {
						return -1;
					}
			if (!thisGeo.isSelected() && otherGeo.isSelected()) {
						return 1;
					}
		}

		if (DoubleUtil.isRatioEqualTo1(this.zPickNear, d.zPickNear)) {
			// geos are nearly at the same depth
			GeoElement geo1 = this.getGeoElement();
			GeoElement geo2 = d.getGeoElement();
			if (geo1 == geo2) {
				return 0;
			}
			// point wins over others
			if (geo1.isGeoPoint() && !geo2.isGeoPoint()) {
				return -1;
			}
			if (!geo1.isGeoPoint() && geo2.isGeoPoint()) {
				return 1;
			}
			// check can drag one (only) -- if both points
			if (geo1.isGeoPoint() && geo2.isGeoPoint()) {
				boolean thisDraggable = EuclidianController3D.isDraggable(geo1,
						getView3D());
				boolean otherDraggable = EuclidianController3D.isDraggable(geo2,
						getView3D());
				if (thisDraggable && !otherDraggable) {
					return -1;
				}
				if (!thisDraggable && otherDraggable) {
					return 1;
				}
			}
			// latter created object wins
			if (geo1.getConstructionIndex() > geo2.getConstructionIndex()) {
				return -1;
			}
			if (geo1.getConstructionIndex() < geo2.getConstructionIndex()) {
				return 1;
			}
		}

		// check if the two objects are "mixed"
		if (this.zPickFar <= d.zPickNear && d.zPickFar <= this.zPickNear) {

			if (checkPickOrder) {
				if (this.getPickOrder() < d.getPickOrder()) {
					return -1;
				}
				if (this.getPickOrder() > d.getPickOrder()) {
					return 1;
				}
			}

			GeoElement geo1 = this.getGeoElement();
			GeoElement geo2 = d.getGeoElement();

			// if both are points
			if (geo1.isGeoPoint() && geo2.isGeoPoint()) {
				// check if one is on a path and the other not
				if ((((GeoPointND) geo1).isPointOnPath())
						&& (!((GeoPointND) geo2).isPointOnPath())) {
					return -1;
				}
				if ((!((GeoPointND) geo1).isPointOnPath())
						&& (((GeoPointND) geo2).isPointOnPath())) {
					return 1;
				}
				// check if one is the child of the other
				if (geo1.isMoveable() && geo1.isChildOf(geo2)) {
					return -1;
				}
				if (geo2.isMoveable() && geo2.isChildOf(geo1)) {
					return 1;
				}
			} else {
				// any geo before a plane
				if (!geo1.isGeoPlane() && geo2.isGeoPlane()) {
					return -1;
				}
				if (geo1.isGeoPlane() && !geo2.isGeoPlane()) {
					return 1;
				}
			}

			// smaller object is more likely to be picked
			// Note: all objects that are not yet have defined a measure have a
			// default measure = 0
			// so that they are not affected by this comparison.
			/*
			 * if (Kernel.isGreater(d.getGeoElement().getMeasure(),this.
			 * getGeoElement ().getMeasure())) return -1; if
			 * (Kernel.isGreater(this.getGeoElement
			 * ().getMeasure(),d.getGeoElement().getMeasure())) return 1;
			 */

		}

		// finally check if one is before the other
		if (this.zPickNear > d.zPickNear) {
			// Log.debug("-1");
			return -1;
		}
		if (this.zPickNear < d.zPickNear) {
			// Log.debug("1");
			return 1;
		}

		// says that the two objects are equal for the comparator
		/*
		 * if (DEBUG){ DecimalFormat df = new DecimalFormat("0.000000000");
		 * Log.debug("equality :\n" +"zMin= "+df.format(this.zPickNear) +
		 * " | zMax= "+df.format(this.zPickFar) +" ("
		 * +this.getGeoElement().getLabel (StringTemplate.defaultTemplate)+")\n"
		 * +"zMin= "+df.format(d.zPickNear) +" | zMax= "+df.format(d.zPickFar) +
		 * " ("
		 * +d.getGeoElement().getLabel(StringTemplate.defaultTemplate)+")\n"); }
		 */
		return 0;

	}

	/** Comparator for Drawable3Ds */
	static final public class DrawableComparator
			implements Comparator<Drawable3D> {
		@Override
		public int compare(Drawable3D d1, Drawable3D d2) {
			return d1.comparePickingTo(d2, false);

		}
	}

	/** Comparator for sets of Drawable3Ds */
	static final public class SetComparator
			implements Comparator<TreeSet<Drawable3D>> {
		@Override
		public int compare(TreeSet<Drawable3D> set1, TreeSet<Drawable3D> set2) {
			/*
			 * TreeSet set1 = (TreeSet) arg1; TreeSet set2 = (TreeSet) arg2;
			 */

			// check if one set is empty
			if (set1.isEmpty()) {
				return 1;
			}
			if (set2.isEmpty()) {
				return -1;
			}

			Drawable3D d1 = set1.first();
			Drawable3D d2 = set2.first();

			return d1.comparePickingTo(d2, true);
		}
	}

	/**
	 * @return true if geo is highlighted, or if it is part of a list
	 *         highlighted
	 */
	public boolean doHighlighting() {
		// no highlighting if we're moving something
		if (getView3D().getEuclidianController()
				.getMoveMode() != EuclidianController.MOVE_NONE) {
			return false;
		}

		if (getGeoElement().doHighlighting()) {
			return true;
		}
		if (createdByDrawList()) {
			return ((Drawable3D) getDrawListCreator()).doHighlighting();
		}
		return false;
	}

	/**
	 * sets the color for drawing and alpha value
	 */
	final protected void setHighlightingColor() {
		setDrawingColor(getColor());
	}

	/**
	 * 
	 * @return current color (may be highlighted)
	 */
	final public GColor getColor() {
		if (doHighlighting()) {
			return color[1];
		}
		return color[0];
	}

	/**
	 * sets the renderer drawing color
	 * 
	 * @param color
	 *            color
	 */
	protected void setDrawingColor(GColor color) {
		getView3D().getRenderer().setColor(color);
	}

	/**
	 * sets the color of surface for drawing and alpha value
	 */
	protected void setSurfaceHighlightingColor() {
		setDrawingColor(getSurfaceColor());
	}

	/**
	 * 
	 * @return current surface color (may be highlighted)
	 */
	public GColor getSurfaceColor() {
		if (doHighlighting()) {
			return surfaceColor[1];
		}
		return surfaceColor[0];
	}

	/**
	 * update drawable colors
	 */
	public void updateColors() {
		setColors(getObjectColorForOutline(), alpha, color);
	}

	/**
	 * set colors when outlined object
	 */
	protected void setColorsOutlined() {
		setColors(getObjectColorForOutline(), 255, color); // for outline
		setColors(getObjectColorForSurface(), alpha, surfaceColor);
	}

	/**
	 * 
	 * @return object color for outline
	 */
	protected GColor getObjectColorForOutline() {
		return getGeoElement().getObjectColor();
	}

	/**
	 * 
	 * @return object color for surface
	 */
	private GColor getObjectColorForSurface() {
		return getGeoElement().getObjectColor();
	}

	private void setColors(GColor sourceColor, int alpha, GColor[] color) {
		GColor c = sourceColor.deriveWithAlpha(alpha);

		if (getView3D().isGrayScaled()) {
			color[0] = c.createGrayScale();
		} else {
			color[0] = c;
		}

		// creates corresponding color for highlighting

		int r = color[0].getRed();
		int g = color[0].getGreen();
		int b = color[0].getBlue();
		int d = r + g + b;

		double distance;

		if (d > LIGHT_COLOR) { // color is closer to white : darken it
			distance = Math.sqrt(r * r + g * g + b * b); // euclidian distance
															// to black
			tmpColor2 = GColor.BLACK;
		} else { // color is closer to black : lighten it
			r = 255 - r;
			g = 255 - g;
			b = 255 - b;
			distance = Math.sqrt(r * r + g * g + b * b); // euclidian distance
															// to white
			tmpColor2 = GColor.WHITE;
		}

		double s = 255 * getColorShift() / distance;
		int a = color[0].getAlpha();
		// sufficient alpha to be seen
		if (a > 0 && a < ALPHA_MIN_HIGHLIGHTING) {
			a = ALPHA_MIN_HIGHLIGHTING;
		}
		// highlighted color
		color[1] = GColor.mixColors(color[0], tmpColor2, s, a);
	}

	protected void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	protected int getAlpha() {
		return alpha;
	}

	protected void updateAlpha() {
		// only used by surfaces
		// use 1-(1-alpha)^(1/3) because transparent parts are drawn twice
		int a = (int) (255
				* (1 - Math.pow(1 - getGeoElement().getAlphaValue(), 1. / 3.)));

		if (a < 0) {
			a = 0;
		} else if (a > 255) {
			a = 255;
		}
		setAlpha(a);
	}

	/**
	 * 
	 * @return true if has alpha that leads to a transparent surface
	 */
	protected boolean hasTransparentAlpha() {
		return getAlpha() > 0 && getAlpha() < 255;
	}

	abstract protected double getColorShift();

	// ///////////////////////////////////////////////////////////////////////////
	// links to the GeoElement

	/**
	 * get the GeoElementInterface linked to the Drawable3D
	 * 
	 * @return the GeoElement3DInterface linked to
	 */
	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	/**
	 * set the GeoElement linked to the Drawable3D
	 * 
	 * @param a_geo
	 *            the GeoElement
	 */
	public void setGeoElement(GeoElement a_geo) {
		this.geo = a_geo;
	}

	// ///////////////////////////
	// TYPE

	/**
	 * add this to the correct lists
	 * 
	 * @param lists
	 *            lists
	 */
	abstract public void addToDrawable3DLists(Drawable3DLists lists);

	protected void addToDrawable3DLists(Drawable3DLists lists, int type) {
		lists.getList(type).add(this);
	}

	/**
	 * remove this from the correct lists
	 * 
	 * @param lists
	 *            lists
	 */
	abstract public void removeFromDrawable3DLists(Drawable3DLists lists);

	protected void removeFromDrawable3DLists(Drawable3DLists lists, int type) {
		lists.getList(type).remove(this);
	}

	/**
	 * remove from GPU memory
	 */
	public void removeFromGL() {
		doRemoveGeometryIndex(getGeometryIndex());
		doRemoveGeometryIndex(getSurfaceIndex());
		label.removeFromGL();
		if (shouldBePackedForManager()) {
			if (tracesPackingBuffer != null) {
				for (int index : tracesPackingBuffer) {
					doRemoveGeometryIndex(index);
				}
				tracesPackingBuffer.clear();
			}
		}
	}

	/**
	 * remove preview from GL geometries (used when pack buffers)
	 */
    protected void removePreviewFromGL() {
        removeFromGL();
        geomIndex = NOT_REUSABLE_INDEX;
        surfaceIndex = NOT_REUSABLE_INDEX;
    }

	// ////////////////////////////
	// FOR PREVIEWABLE INTERFACE

	/**
	 * remove this from the draw list 3D
	 */
	public void disposePreview() {
		getView3D().remove(this);

	}

	/**
	 * unused for 3D
	 * 
	 * @param g2
	 *            graphics
	 */
	public void drawPreview(GGraphics2D g2) {
		// overrides 2D, needed for subclasses that implement previewable
	}

	// ////////////////////
	// LAST PICKING TYPE
	// ////////////////////

	/**
	 * set last picking type
	 * 
	 * @param type
	 *            picking type
	 */
	final public void setPickingType(PickingType type) {
		lastPickingType = type;
	}

	/**
	 * 
	 * @return last picking type
	 */
	final public PickingType getPickingType() {
		return lastPickingType;
	}

	protected Trace getTrace() {
		if (trace == null) {
			trace = new Trace();
		}
		return trace;
	}

	/**
	 * record trace
	 */
	protected void recordTrace() {
		if (!hasTrace()) {
			return;
		}

		getTrace().record(this);
	}

	/**
	 * 
	 * @return new trace index for current geometry
	 */
	protected TraceIndex newTraceIndex() {
		return new TraceIndex(geomIndex, surfaceIndex);
	}

	/**
	 * draw traces
	 * 
	 * @param renderer
	 *            renderer
	 * @param hidden
	 *            says if its hidden outline
	 */
	protected void drawTracesOutline(Renderer renderer, boolean hidden) {

		if (trace == null) {
			return;
		}

		if (hidden) {
			setLineTextureHidden(renderer);
		} else {
			renderer.getTextures()
					.setDashFromLineType(getGeoElement().getLineType());
		}

		for (Entry<TraceSettings, ArrayList<TraceIndex>> settings : trace
				.entrySet()) {
			ArrayList<TraceIndex> indices = settings.getValue();
			setDrawingColor(settings.getKey().getColor());
			// Log.debug(indices.size());
			for (TraceIndex index : indices) {
				drawGeom(renderer, index);
			}
		}
	}

	/**
	 * draws the geometry of the trace index
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param index
	 *            trace index
	 */
	protected void drawGeom(Renderer renderer, TraceIndex index) {
		renderer.getGeometryManager().draw(index.geom);
	}

	/**
	 * draws the surface of the trace index
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param index
	 *            trace index
	 */
	protected void drawSurface(Renderer renderer, TraceIndex index) {
		renderer.getGeometryManager().draw(index.surface);
	}

	/**
	 * draw traces
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void drawTracesTranspSurface(Renderer renderer) {

		if (trace == null) {
			return;
		}

		for (Entry<TraceSettings, ArrayList<TraceIndex>> settings : trace
				.entrySet()) {
			ArrayList<TraceIndex> indices = settings.getValue();
			TraceSettings key = settings.getKey();
			double a = key.getAlpha();
			if (a > 0 && a < 1) {
				setDrawingColor(key.getColor());
				for (TraceIndex index : indices) {
					drawSurface(renderer, index);
				}
			}
		}
	}

	/**
	 * draw traces
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void drawTracesHidingSurface(Renderer renderer) {
		if (trace == null) {
			return;
		}

		for (Entry<TraceSettings, ArrayList<TraceIndex>> settings : trace
				.entrySet()) {
			ArrayList<TraceIndex> indices = settings.getValue();
			double a = settings.getKey().getAlpha();
			if (a > 0 && a < 1) {
				for (TraceIndex index : indices) {
					drawSurface(renderer, index);
				}
			}
		}
	}

	/**
	 * draw traces
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void drawTracesNotTranspSurface(Renderer renderer) {

		if (trace == null) {
			return;
		}

		for (Entry<TraceSettings, ArrayList<TraceIndex>> settings : trace
				.entrySet()) {
			ArrayList<TraceIndex> indices = settings.getValue();
			TraceSettings key = settings.getKey();
			double a = key.getAlpha();
			if (a >= 1) {
				setDrawingColor(key.getColor());
				for (TraceIndex index : indices) {
					drawSurface(renderer, index);
				}
			}
		}

	}

	/**
	 * clear trace for view changed
	 */
	final protected void clearTraceForViewChanged() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			clearTraceForViewChangedByZoomOrTranslate();
		}
	}

	/**
	 * clear trace for view changed by zoom or translate
	 */
	protected void clearTraceForViewChangedByZoomOrTranslate() {
		if (trace != null) {
			// remove all geometry indices from openGL manager
			for (ArrayList<TraceIndex> indices : trace.values()) {
				for (TraceIndex index : indices) {
					doRemoveGeometryIndex(index.geom);
					doRemoveGeometryIndex(index.surface);
				}
			}

			trace.clear();
		}
	}

	/**
	 * set near/far z values when picked (positive value in direction to the
	 * eye)
	 * 
	 * @param zNear
	 *            nearest value
	 * @param zFar
	 *            most far value
	 *
	 * @param discardPositive
	 *            if discard positive values
	 * @param positionOnHitting
	 *            position on hitting ray
	 */
	final public void setZPick(double zNear, double zFar,
			boolean discardPositive, double positionOnHitting) {
		if (needsDiscardZPick(discardPositive, zNear, zFar)) {
			resetZPick();
		} else {
			setZPickValue(zNear, zFar);
		}
		this.positionOnHitting = positionOnHitting;
	}

	/**
	 * @param discardPositive whether to discard hits behind the eye position
	 * @param zNear front hit
	 * @param zFar back hit
	 * @return whether to discard
	 */
	protected boolean needsDiscardZPick(boolean discardPositive,
			double zNear, double zFar) {
		return discardPositive && (zNear > 0 || zFar > 0);
	}

	/**
	 * @param zNear
	 *            front hit position
	 * @param zFar
	 *            back hit position
	 */
	protected void setZPickValue(double zNear, double zFar) {
		zPickNear = zNear;
		zPickFar = zFar;
		relevantPickingValues = !Double.isInfinite(zPickNear)
				&& !Double.isInfinite(zPickFar) && !Double.isNaN(zPickNear)
				&& !Double.isNaN(zPickFar);
	}

	/**
	 * @param positionOnHitting
	 *            position on hitting ray
	 */
	protected void setPositionOnHitting(double positionOnHitting) {
		this.positionOnHitting = positionOnHitting;
	}

	/**
	 * Reset z picking values
	 */
	protected void resetZPick() {
		zPickNear = Double.NEGATIVE_INFINITY;
		zPickFar = Double.NEGATIVE_INFINITY;
		relevantPickingValues = false;
	}

	/**
	 * 
	 * @return position on hitting ray
	 */
	public double getPositionOnHitting() {
		return positionOnHitting;
	}

	/**
	 * Note that z are positive in direction to the eye
	 * 
	 * @return nearest z value from last picking
	 */
	final public double getZPickNear() {
		return zPickNear;
	}

	/**
	 * Note that z are positive in direction to the eye
	 * 
	 * @return far z value from last picking
	 */
	final public double getZPickFar() {
		return zPickFar;
	}

	/**
	 * says if the drawable is hit by the hitting (e.g. ray)
	 * 
	 * @param hitting
	 *            e.g. ray
	 * @return true if hit
	 */
	public boolean hit(Hitting hitting) {
		// do nothing by default
		return false;
	}

	/**
	 * called when part of list
	 * 
	 * @param hitting
	 *            e.g. ray
	 * @return true if hit
	 */
	public boolean hitForList(Hitting hitting) {
		if (hasGeoElementVisible() && getGeoElement().isPickable()) {
			return hit(hitting);
		}

		return false;
	}

	/**
	 * says if the drawable is hit by the hitting (e.g. ray), checking first if
	 * visible and pickable
	 * 
	 * @param hitting
	 *            e.g. ray
	 * @param hits
	 *            storing the drawable if hit
	 */
	final public void hitIfVisibleAndPickable(Hitting hitting, Hits3D hits) {
		if (isVisible() && getGeoElement().isPickable()) {

			// try to hit label
			if (hitLabel(hitting, hits)) {
				return; // label is hitten
			}

			// try to hit geo
			if (hit(hitting)) {
				hits.addDrawable3D(this, getPickingType());
			}

		}
	}

	/**
	 * 
	 * @param hitting
	 *            hitting
	 * @param hits
	 *            hits to record
	 * @return true if label is hitted
	 */
	protected boolean hitLabel(Hitting hitting, Hits3D hits) {
		if (isLabelVisible() && hitting.hitLabel(label)) {
			setZPick(label.getDrawZ(), label.getDrawZ(),
					hitting.discardPositiveHits(), -label.getDrawZ());
			hits.addDrawable3D(this, PickingType.LABEL);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return if the label is pickable
	 */
	public boolean hasPickableLable() {
		return true;
	}

	/**
	 * enlarge min and max values to enclose object
	 *  @param min
	 *            (x,y,z) min
	 * @param max
	 *            (x,y,z) max
     * @param dontExtend
     *            set to true if clipped curves/surfaces should not be larger
     *            than the view itself; and when point radius should extend
     */
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		// nothing done by default
	}

	/**
	 * enlarge min and max to boundsMin and boundsMax
	 * 
	 * @param min
	 *            (x,y,z) min
	 * @param max
	 *            (x,y,z) max
	 * @param boundsMin
	 *            (x,y,z) object bounds min
	 * @param boundsMax
	 *            (x,y,z) object bounds max
	 */
	static protected void enlargeBounds(Coords min, Coords max,
			Coords boundsMin, Coords boundsMax) {
		for (int i = 0; i < 3; i++) {
			if (min.val[i] > boundsMin.val[i]) {
				min.val[i] = boundsMin.val[i];
			}
			if (max.val[i] < boundsMax.val[i]) {
				max.val[i] = boundsMax.val[i];
			}
		}
	}

    /**
     * enlarge min and max to boundsMin and boundsMax
     *
     * @param min
     *            (x,y,z) min
     * @param max
     *            (x,y,z) max
     * @param boundsMin
     *            (x,y,z) object bounds min
     * @param boundsMax
     *            (x,y,z) object bounds max
     * @param radius
     *            e.g. line radius
     */
    static protected void enlargeBounds(Coords min, Coords max,
                                        Coords boundsMin, Coords boundsMax, double radius) {
        for (int i = 0; i < 3; i++) {
            if (min.val[i] > boundsMin.val[i] - radius) {
                min.val[i] = boundsMin.val[i] - radius;
            }
            if (max.val[i] < boundsMax.val[i] + radius) {
                max.val[i] = boundsMax.val[i] + radius;
            }
        }
    }

	/**
	 * enlarge min and max to boundsMin and boundsMax
	 * 
	 * @param min
	 *            (x,y,z) min
	 * @param max
	 *            (x,y,z) max
	 * @param coords
	 *            (x,y,z) object coords
	 */
	static public void enlargeBounds(Coords min, Coords max, Coords coords) {
		for (int i = 0; i < 3; i++) {
			if (min.val[i] > coords.val[i]) {
				min.val[i] = coords.val[i];
			}
			if (max.val[i] < coords.val[i]) {
				max.val[i] = coords.val[i];
			}
		}
	}

	/**
	 * reduce bounds to clipping cube
	 * 
	 * @param boundsMin
	 *            bounds min
	 * @param boundsMax
	 *            bounds max
	 */
	protected void reduceBounds(Coords boundsMin,
			Coords boundsMax) {
		Coords[] vertices = getView3D().getClippingCubeDrawable().getVertices();
		Coords min = vertices[0];
		Coords max = vertices[7];
		for (int i = 0; i < 3; i++) {
			if (boundsMin.val[i] < min.val[i]) {
				boundsMin.val[i] = min.val[i];
			}
			if (boundsMax.val[i] > max.val[i]) {
				boundsMax.val[i] = max.val[i];
			}
		}
	}

	/**
	 * add and sub v1+v2 or v1-v2 max values to bounds
	 * 
	 * @param min
	 *            (x,y,z) min
	 * @param max
	 *            (x,y,z) max
	 * @param center
	 *            center coords
	 * @param v1
	 *            first direction vector
	 * @param v2
	 *            second direction vector
	 * @param r1
	 *            first direction radius
	 * @param r2
	 *            second direction radius
	 * 
	 */
	static protected void enlargeBoundsToDiagonal(Coords min, Coords max,
			Coords center, Coords v1, Coords v2, double r1, double r2) {
		for (int i = 0; i < 3; i++) {
			double add = Math.abs(v1.val[i] * r1 + v2.val[i] * r2);
			double sub = Math.abs(v1.val[i] * r1 - v2.val[i] * r2);
			double v = Math.max(add, sub);
			double cMin = center.val[i] - v;
			double cMax = center.val[i] + v;
			if (min.val[i] > cMin) {
				min.val[i] = cMin;
			}
			if (max.val[i] < cMax) {
				max.val[i] = cMax;
			}
		}
	}

	@Override
	public boolean isTracing() {
		return false;
	}

	/**
	 * add last geometry to traces
	 */
	public void addLastTrace() {
		if (!shouldBePackedForManager()) {
			getTrace().addLastTraceIndex();
		}
	}

	/**
	 * Still needed for labels and texts that are not packed
	 * 
	 * @return true if it should be packed
	 */
	public boolean shouldBePacked() {
		return false;
	}

	/**
	 * 
	 * @return true if was created for closed surface (e.g. cube)
	 */
	public boolean addedFromClosedSurface() {
		return false;
	}

	/**
	 *
	 * @return true should be packed for the current geometry manager
	 */
	final protected boolean shouldBePackedForManager() {
		return getView3D().getRenderer().getGeometryManager().packBuffers() && shouldBePacked();
	}

	/**
	 * add index to traces (for packed buffer)
	 * 
	 * @param index
	 *            index
	 * @return index or NOT_REUSABLE_INDEX
	 */
	protected int addToTracesPackingBuffer(int index) {
		if (hasTrace()) {
			if (index != NOT_REUSABLE_INDEX) {
				if (tracesPackingBuffer == null) {
					tracesPackingBuffer = new LinkedList<>();
				}
				tracesPackingBuffer.add(index);
			}
			return NOT_REUSABLE_INDEX;
		}
		return index;
	}

	/**
	 * set visibility as intersection curve
	 * 
	 * @param visible
	 *            if visible
	 */
	public void setIntersectionCurveVisibility(boolean visible) {
		intersectionCurveVisibility = visible;
	}

	/**
	 * update visibility as intersection curve
	 * 
	 */
	public void updateIntersectionCurveVisibility() {
	    setGeometriesVisibility(intersectionCurveVisibility);
	}

	/**
	 * setup manager start packing curves (if possible)
	 */
	protected void setPackCurve() {
		setPackCurve(false);
	}

	/**
	 * setup manager start packing curves (if possible)
	 * 
	 * @param clipped
	 *            if curve is clipped
	 */
	final protected void setPackCurve(boolean clipped) {
        getView3D().getRenderer().getGeometryManager().setPackCurve(
                this, clipped);
    }

	/**
	 * 
	 * @return line type (visible)
	 */
	public int getLineType() {
		return getGeoElement().getLineType();
	}

	/**
	 * 
	 * @return line type (hidden)
	 */
	public int getLineTypeHidden() {
		return getGeoElement().getLineTypeHidden();
	}

	/**
	 * setup manager start packing surfaces (if possible)
	 * 
	 * @param clipped
	 *            true if surface needs clipping
	 */
    protected void setPackSurface(boolean clipped) {
        getView3D().getRenderer().getGeometryManager().setPackSurface(this,
                clipped);
    }

	/**
	 * setup manager start packing surfaces (if possible)
	 */
	protected void setPackSurface() {
		setPackSurface(false);
	}

	/**
	 * setup manager end packing
	 */
	protected void endPacking() {
	    getView3D().getRenderer().getGeometryManager().endPacking();
	}

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	public DrawableND createDrawableND(GeoElement subGeo) {
		return m_view3D.newDrawable(subGeo);
	}

	@Override
	public void setPartialHitClip(GRectangle rect) {
		// just strokes
	}

	@Override
	public GRectangle getPartialHitClip() {
		return null;
	}
}
