package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.javax.swing.GBox;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.openGL.RendererW;
import geogebra.geogebra3D.web.euclidian3D.openGL.RendererWebGL;
import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanelW3D;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.MyEuclidianViewPanel;

import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 3D view
 * @author mathieu
 *
 */
public class EuclidianViewW3D extends EuclidianView3D {
	
	protected EuclidianPanelWAbstract EVPanel;

	/**
	 * constructor
	 * @param ec euclidian controller
	 * @param settings euclidian settings
	 */
	public EuclidianViewW3D(EuclidianController3D ec, EuclidianSettings settings) {
	    super(ec, settings);
	    
    }
	
	
	
	/**
	 * @return panel component
	 */
	public Widget getComponent() {
	    return EVPanel.getAbsolutePanel();
    }
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////
	// MyEuclidianViewPanel
	////////////////////////////////////////////////////////////
	
	/**
	 * current dockPanel (if exists)
	 */
	EuclidianDockPanelW3D dockPanel = null;
	
	/**
	 * 
	 * @param dockPanel current dockPanel (if exists)
	 */
	public void setDockPanel(EuclidianDockPanelW3D dockPanel){
		this.dockPanel = dockPanel;
	}
	

	
    protected MyEuclidianViewPanel newMyEuclidianViewPanel(){
		return new MyEuclidianViewPanel3D(this);
	}
	
	/**
	 * panel for 3D
	 * @author mathieu
	 *
	 */
	private class MyEuclidianViewPanel3D extends MyEuclidianViewPanel implements RequiresResize {
		
		private RendererWebGL renderer;
		
		/**
		 * constructor
		 * @param ev euclidian view
		 */
		public MyEuclidianViewPanel3D(EuclidianView ev) {
	        super(ev);
        }
		
		@Override
        protected void createCanvas(){
			renderer = new RendererWebGL();
			canvas = renderer.getGLCanvas();
		}
		
		@Override
		public void onResize() {
			super.onResize();
			if (dockPanel != null){
				int w = dockPanel.getComponentInteriorWidth();
				int h = dockPanel.getComponentInteriorHeight();

				//App.debug("------------------ resize -----------------------");
				//App.debug("w = "+w+" , h = "+h);
				renderer.setDimension(w, h);
			}
		}
		
	}

	public void repaint() {
	    // TODO Auto-generated method stub
	    
    }



	public GColor getBackgroundCommon() {
	    // TODO Auto-generated method stub
	    return null;
    }



	public void setToolTipText(String plainTooltip) {
	    // TODO Auto-generated method stub
	    
    }



	public boolean hasFocus() {
	    // TODO Auto-generated method stub
	    return false;
    }



	public void requestFocus() {
	    // TODO Auto-generated method stub
	    
    }



	public int getWidth() {
	    // TODO Auto-generated method stub
	    return 0;
    }



	public int getHeight() {
	    // TODO Auto-generated method stub
	    return 0;
    }



	public boolean isShowing() {
	    // TODO Auto-generated method stub
	    return false;
    }



	@Override
    protected void createPanel() {
		EVPanel = newMyEuclidianViewPanel();
    }



	@Override
    protected Renderer createRenderer() {
	    return new RendererW(this);
    }



	@Override
    protected boolean getShiftDown() {
	    // TODO Auto-generated method stub
	    return false;
    }



	@Override
    protected void setDefault2DCursor() {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
	    // TODO Auto-generated method stub
	    return null;
    }



	@Override
    public GFont getFont() {
	    // TODO Auto-generated method stub
	    return null;
    }



	@Override
    protected void setHeight(int h) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void setWidth(int h) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void setStyleBarMode(int mode) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void updateSizeKeepDrawables() {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public boolean requestFocusInWindow() {
	    // TODO Auto-generated method stub
	    return false;
    }



	@Override
    public void paintBackground(GGraphics2D g2) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void drawActionObjects(GGraphics2D g) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected void setAntialiasing(GGraphics2D g2) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void setBackground(GColor bgColor) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void setPreferredSize(GDimension preferredSize) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected MyZoomer newZoomer() {
	    // TODO Auto-generated method stub
	    return null;
    }



	@Override
    public void add(GBox box) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void remove(GBox box) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    public void setTransparentCursor() {
	    // TODO Auto-generated method stub
	    
    }



	@Override
    protected EuclidianStyleBar newEuclidianStyleBar() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
}
