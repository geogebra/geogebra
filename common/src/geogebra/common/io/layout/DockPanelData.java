package geogebra.common.io.layout;

import geogebra.common.awt.GDimension;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;

/**
 * A storage container with all information which need to
 * be stored for a DockPanel.
 * 
 * @author Florian Sonner
 */
public class DockPanelData {
	private int viewId;
	private boolean isVisible;
	private boolean openInFrame;
	private boolean showStyleBar;
	private GRectangle frameBounds;
	// see DockManager.show() for an explanation of this, eg "1,1,1"
	private String embeddedDef;
	private String toolbarString;
	private int embeddedSize;
	private String plane;
	
	/**
	 * @param viewId		The view ID.
	 * @param toolbar		The toolbar string of this panel or null.
	 * @param isVisible		If this view is visible at the moment.
	 * @param openInFrame 	If this view should be opened in a separate frame.
	 * @param showStyleBar	If the style bar is visible
	 * @param windowRect 	The rectangle which defines the location and size of the window for this view. 
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 * @param plane         Plane that created the view (for EuclidianViewForPlane)
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible, boolean openInFrame, boolean showStyleBar, GRectangle windowRect, String embeddedDef, int embeddedSize, String plane) {
		this.viewId = viewId;
		this.toolbarString = toolbar;
		this.isVisible = isVisible;
		this.openInFrame = openInFrame;
		this.showStyleBar = showStyleBar;
		this.frameBounds = windowRect;
		this.embeddedDef = embeddedDef;
		this.embeddedSize = embeddedSize;
		this.plane = plane;
	}
	
	/**
	 * @param viewId		The view ID.
	 * @param toolbar		The toolbar string of this panel or null.
	 * @param isVisible		If this view is visible at the moment.
	 * @param openInFrame 	If this view should be opened in a separate frame.
	 * @param showStyleBar	If the style bar is visible
	 * @param windowRect 	The rectangle which defines the location and size of the window for this view. 
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible, boolean openInFrame, boolean showStyleBar, GRectangle windowRect, String embeddedDef, int embeddedSize) {
		this(viewId, toolbar, isVisible, openInFrame, showStyleBar, windowRect, embeddedDef, embeddedSize, null);
	}
	
	/**
	 * @param viewId		The view ID.
	 * @param toolbar		The toolbar string of this view or null.
	 * @param isVisible		If this view is visible at the moment.
	 * @param inFrame 		If this view is in an separate window at the moment.
	 * @param showStyleBar	If the style bar is visible
	 * @param windowX		The x location of the window.
	 * @param windowY		The y location of the window.
	 * @param windowWidth	The width of the window.
	 * @param windowHeight	The height of the window.
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible, boolean inFrame, boolean showStyleBar, int windowX, int windowY, int windowWidth, int windowHeight, String embeddedDef, int embeddedSize) {
		this(viewId, toolbar, isVisible, inFrame, showStyleBar, geogebra.common.factories.AwtFactory.prototype.newRectangle(windowX, windowY, windowWidth, windowHeight), embeddedDef, embeddedSize, null);
	}
	
	/**
	 * @param viewId		The view ID.
	 * @param toolbar		The toolbar string of this view or null.
	 * @param isVisible		If this view is visible at the moment.
	 * @param inFrame 		If this view is in an separate window at the moment.
	 * @param showStyleBar	If the style bar is visible
	 * @param windowLoc		The location of the window.
	 * @param windowSize 	The size of the window.
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible, boolean inFrame, boolean showStyleBar, GPoint windowLoc, GDimension windowSize, String embeddedDef, int embeddedSize) {
		this(viewId, toolbar, isVisible, inFrame, showStyleBar, geogebra.common.factories.AwtFactory.prototype.newRectangle(windowLoc.getX(), windowLoc.getY(),windowSize.getWidth(),windowSize.getHeight()), embeddedDef, embeddedSize, null);
	}

	/** 
	 * @return The view ID.
	 */
	public int getViewId() {
		return viewId;
	}
	
	/**
	 * set the view id
	 * @param id id
	 */
	public void setViewId(int id){
		viewId = id;
	}
	
	/**
	 * @return The toolbar string of this view (or an empty string).
	 */
	public String getToolbarString() {
		return toolbarString;
	}

	/**
	 * @return If this view is visible at the moment.
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @return If the DockPanel was shown in a frame the last time it 
	 * 		was visible.
	 */
	public boolean isOpenInFrame() {
		return openInFrame;
	}
	
	/**
	 * @return If the style bar is visible
	 */
	public boolean showStyleBar() {
		return showStyleBar;
	}

	/**
	 * @return the frameBounds
	 */
	public GRectangle getFrameBounds() {
		return frameBounds;
	}

	/**
	 * @return the embeddedDef
	 */
	public String getEmbeddedDef() {
		return embeddedDef;
	}

	/**
	 * @return the embeddedSize
	 */
	public int getEmbeddedSize() {
		return embeddedSize;
	}
	
	/**
	 * 
	 * @return the plane creator
	 */
	public String getPlane(){
		return plane;
	}
	
	/**
	 * 
	 * @return view id for XML
	 */
	protected int getViewIdForXML(){
		return getViewId();
	}
	

	protected StringBuilder getStartXml() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<view id=\"");
		sb.append(getViewIdForXML());
		
		if(getToolbarString() != null) {
			sb.append("\" toolbar=\"");
			sb.append(getToolbarString());
		}
		
		sb.append("\" visible=\"");
		sb.append(isVisible());
		sb.append("\" inframe=\"");
		sb.append(isOpenInFrame());
		sb.append("\" stylebar=\"");
		sb.append(showStyleBar());
		sb.append("\" location=\"");
		sb.append(getEmbeddedDef());
		sb.append("\" size=\"");
		sb.append(getEmbeddedSize());
		sb.append("\" window=\"");
		sb.append((int)getFrameBounds().getX());
		sb.append(",");
		sb.append((int)getFrameBounds().getY());
		sb.append(",");
		sb.append((int)getFrameBounds().getWidth());
		sb.append(",");
		sb.append((int)getFrameBounds().getHeight());
		return sb;
	}
	
	/**
	 * @return An XML representation of the data stored in this class.
	 */
	public String getXml() {
		
		StringBuilder sb = getStartXml();
		sb.append("\" />\n");
		return sb.toString();
		
	}
	
	/**
	 * Clone this object. Required as dock panels would change the loaded perspective 
	 * automatically otherwise.
	 */
	@SuppressWarnings("all")
	public Object clone() {
		return new DockPanelData(viewId, toolbarString, isVisible, openInFrame, showStyleBar, frameBounds, embeddedDef, embeddedSize, plane);
	}
}
