package geogebra.web.euclidian;

import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.web.awt.BasicStroke;
import geogebra.web.main.Application;

import com.google.gwt.canvas.client.Canvas;



public class EuclidianView extends AbstractEuclidianView implements SettingListener{
	
	geogebra.web.awt.Graphics2D g2 = null;
	
	protected static final long serialVersionUID = 1L;

	public EuclidianView(Canvas canvas,
            AbstractEuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, EuclidianSettings settings) {		
		super(euclidiancontroller, settings);
		evNo = 1;
	    // TODO Auto-generated constructor stub
		this.g2 = new geogebra.web.awt.Graphics2D(canvas);
		setAxesColor(geogebra.common.awt.Color.black);
		updateFonts();
		setStandardCoordSystem();
		attachView();
		
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = getApplication().getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
    }


	// STROKES
	protected static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	protected static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	protected boolean unitAxesRatio;

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}
	
	private boolean disableRepaint;
	public void setDisableRepaint(boolean disableRepaint) {
		this.disableRepaint = disableRepaint;
	}
	
	
	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * @param width 
	 * @param type 
	 * @return stroke
	 */
	public static BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianStyleConstants.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	public static void setAntialiasingStatic(Graphics2D g2d) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }
	
	public void setCoordinateSpaceSize(int width, int height) {
		g2.setCoordinateSpaceWidth(width);
		g2.setCoordinateSpaceHeight(height);
	}
	
	public void synCanvasSize() {
		setCoordinateSpaceSize(g2.getOffsetWidth(), g2.getOffsetHeight());
	}
	
	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	public int getWidth() {
		return g2.getCoordinateSpaceWidth();
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	public int getHeight() {
		return g2.getCoordinateSpaceHeight();
	}
	
	/**
	 * Gets pixel width of the &lt;canvas&gt;.
	 * 
	 * @return the physical width in pixels
	 */
	public int getPhysicalWidth() {
		return g2.getOffsetWidth();
	}
	
	/**
	 * Gets pixel height of the &lt;canvas&gt;.
	 * 
	 * @return the physical height in pixels
	 */
	public int getPhysicalHeight() {
		return g2.getOffsetHeight();
	}
	
	public int getAbsoluteTop() {
		return g2.getAbsoluteTop();
	}
	
	public int getAbsoluteLeft() {
		return g2.getAbsoluteLeft();
	}

	public EuclidianController getEuclidianController() {
		return (EuclidianController)euclidianController;
    }

	
	public void clearView() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

    @Override
    public Application getApplication() {
    	return (Application)application;
    }

	@Override
    public Graphics2D getBackgroundGraphics() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }
	private Graphics2D g2dtemp;

	private geogebra.common.awt.Color backgroundColor = geogebra.web.awt.Color.white;
	@Override
    public Graphics2D getTempGraphics2D(Font plainFontCommon) {
	    if(g2dtemp==null)
	    	g2dtemp = new geogebra.web.awt.Graphics2D(Canvas.createIfSupported());
	    g2dtemp.setFont(plainFontCommon);
	    return g2dtemp;
    }

	@Override
    public Font getFont() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

    public geogebra.common.awt.Color getBackgroundCommon() {
	    return backgroundColor ;
    }

	@Override
    protected void setHeight(int h) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    protected void setWidth(int h) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	
    public void repaint() {
	    paint(g2);
    }

	@Override
    protected void initCursor() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    protected void setStyleBarMode(int mode) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

    public void zoom(double px, double py, double factor, int i, boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void zoomAxesRatio(double d, boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public boolean hitAnimationButton(AbstractEvent e) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return false;
    }


	@Override
    public void updateSize() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public boolean requestFocusInWindow() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return false;
    }

	public void setDefaultCursor() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void setHitCursor() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }
	
	public geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = new EuclidianStyleBar(this);
		}

		return styleBar;
	}

	public boolean setAnimationButtonsHighlighted(boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return false;
    }

	@Override
    protected void drawActionObjects(Graphics2D g) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void setShowAxis(int axisX, boolean b, boolean c) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    protected void setAntialiasing(Graphics2D g2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public Drawable newDrawImage(GeoImage geo) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public Drawable newDrawButton(GeoButton geo) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public Drawable newDrawTextField(GeoTextField geo) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public Drawable newDrawBoolean(GeoBoolean geo) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public void setBackground(geogebra.common.awt.Color bgColor) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void setPreferredSize(Dimension preferredSize) {
	    g2.setPreferredSize(preferredSize);
    }

	public void setDragCursor() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void setAnimatedRealWorldCoordSystem(double realWorldCoordX,
            double realWorldCoordX2, double realWorldCoordY,
            double realWorldCoordY2, int i, boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void setToolTipText(String plainTooltip) {
	    // TODO Auto-generated method stub
	    
    }

	public void setResizeXAxisCursor() {
	    // TODO Auto-generated method stub
	    
    }

	public void setResizeYAxisCursor() {
	    // TODO Auto-generated method stub
	    
    }

	public void updatePreviewable() {
	    // TODO Auto-generated method stub
	    
    }
	
}
