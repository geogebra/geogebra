package geogebra.euclidianND;

import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianStyleBar;
import geogebra.euclidian.EuclidianViewJPanel;
import geogebra.euclidian.EuclidianViewTransferHandler;
import geogebra.euclidian.MyZoomer;
import geogebra.main.Application;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Euclidian view for 2D/3D
 * 
 * @author matthieu
 *
 */
public abstract class EuclidianViewND extends AbstractEuclidianView{
	
	protected EuclidianViewJPanel evjpanel;


	/**
	 * @param ec euclidian controller
	 * @param settings euclidian settings
	 */
	public EuclidianViewND(AbstractEuclidianController ec,
			EuclidianSettings settings) {
		super(ec, settings);

		evjpanel = new EuclidianViewJPanel(this);
		
		// algebra controller will take care of our key events
		evjpanel.setFocusable(true);

		evjpanel.setLayout(null);
		evjpanel.setMinimumSize(new Dimension(20, 20));
		
		// register Listener
		evjpanel.addMouseMotionListener((EuclidianController)euclidianController);
		evjpanel.addMouseListener((EuclidianController)euclidianController);
		evjpanel.addMouseWheelListener((EuclidianController)euclidianController);
		evjpanel.addComponentListener((EuclidianController)euclidianController);
		
		
		// enable drop transfers
		evjpanel.setTransferHandler(new EuclidianViewTransferHandler(this));
	}
	
	
	/**
	 * @param cursor new cursor
	 */
	public void setCursor(Cursor cursor) {
		evjpanel.setCursor(cursor);
	}

	public boolean hasFocus() {
		return evjpanel.hasFocus();
	}

	public void repaint() {
		evjpanel.repaint();
	}

	@Override
	public void paintBackground(geogebra.common.awt.Graphics2D g2) {
		g2.drawImage(bgImage, null, 0, 0);
	}
	
	@Override
	public void add(geogebra.common.javax.swing.Box box){
		evjpanel.add(((geogebra.javax.swing.Box)box).getImpl());
	}
	
	@Override
	public void remove(geogebra.common.javax.swing.Box box) {
		evjpanel.remove(((geogebra.javax.swing.Box)box).getImpl());
	}
	/**
	 * @return underlying component
	 */
	public JPanel getJPanel() {
		return evjpanel;
	}
	/**
	 * This view should be focused
	 */
	public void requestFocus() {
		evjpanel.requestFocus();		
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return new geogebra.awt.Font(evjpanel.getFont());
	}
	/**
	 * @return mouse position
	 */
	public java.awt.Point getMousePosition() {
		return evjpanel.getMousePosition();
	}
	/**
	 * @see JPanel#getFontMetrics(java.awt.Font)
	 * @param font font
	 * @return font metrics
	 */
	public FontMetrics getFontMetrics(java.awt.Font font) {
		return evjpanel.getFontMetrics(font);
	}
	/**
	 * @return whethe this view is visible
	 */
	public boolean isShowing() {
		return evjpanel.isShowing();
	}

	@Override
	public boolean requestFocusInWindow() {
		return evjpanel.requestFocusInWindow();	
	}
	/**
	 * @see JPanel#setPreferredSize(Dimension)
	 * @param preferredSize prefered size
	 */
	public void setPreferredSize(Dimension preferredSize) {
		evjpanel.setPreferredSize(preferredSize);
	}
	
	@Override
	public void setPreferredSize(geogebra.common.awt.Dimension preferredSize) {
		evjpanel.setPreferredSize(geogebra.awt.Dimension.getAWTDimension(preferredSize));
	}
	
	/**
	 * @see JPanel#revalidate()
	 */
	public void revalidate() {
		evjpanel.revalidate();
	}
	
	/**
	 * @see JPanel#addMouseListener(MouseListener)
	 * @param ml mouse listener
	 */
	public void addMouseListener(MouseListener ml) {
		evjpanel.addMouseListener(ml);
	}
	
	/**
	 * @see JPanel#removeComponentListener(ComponentListener)
	 * @param ml mouse listener
	 */
	public void removeMouseListener(MouseListener ml) {
		evjpanel.removeMouseListener(ml);
	}
	
	/**
	 * @see JPanel#addMouseMotionListener(MouseMotionListener)
	 * @param mml mouse motion listener
	 */
	public void addMouseMotionListener(MouseMotionListener mml) {
		evjpanel.addMouseMotionListener(mml);
	}
	
	/**
	 * @see JPanel#removeMouseMotionListener(MouseMotionListener)
	 * @param mml mouse motion listener
	 */
	public void removeMouseMotionListener(MouseMotionListener mml) {
		evjpanel.removeMouseMotionListener(mml);
	}
	
	/**
	 * @see JPanel#addMouseWheelListener(MouseWheelListener)
	 * @param mwl mouse wheel listener
	 */
	public void addMouseWheelListener(MouseWheelListener mwl) {
		evjpanel.addMouseWheelListener(mwl);
	}
	
	/**
	 * @see JPanel#removeMouseWheelListener(MouseWheelListener)
	 * @param mwl mouse wheel listener
	 */
	public void removeMouseWheelListener(MouseWheelListener mwl) {
		evjpanel.removeMouseWheelListener(mwl);
	}
	/**
	 * @see JPanel#dispatchEvent(AWTEvent)
	 * @param componentEvent component event
	 */
	public void dispatchEvent(ComponentEvent componentEvent) {
		evjpanel.dispatchEvent(componentEvent);
	}
	
	/**
	 * @see JPanel#setBorder(Border)
	 * @param border new border
	 */
	public void setBorder(Border border) {
		evjpanel.setBorder(border)	;
	}
	
	/**
	 * @see JPanel#addComponentListener(ComponentListener)
	 * @param componentListener component listener
	 */
	public void addComponentListener(
			ComponentListener componentListener) {
		evjpanel.addComponentListener(componentListener);
		
	}
	
	/**
	 * @param dimension new size
	 */
	public void setSize(Dimension dimension) {
		evjpanel.setSize(dimension);
		
	}
	/**
	 * @return prefered size
	 */
	public Dimension getPreferredSize() {
		return evjpanel.getPreferredSize();
	}
	/**
	 * @see EuclidianViewJPanel#processMouseEventImpl(MouseEvent)
	 * @param e mouse event
	 */
	protected void processMouseEvent(MouseEvent e) {
		evjpanel.processMouseEventImpl(e);
	}
	
	
	@Override
	public Application getApplication() {
		return (Application)super.getApplication();
	}
	
	
	/**
	 * Initializes this panel
	 * @param repaint ignored parameter
	 */
	protected void initPanel(boolean repaint) {
		// preferred size
		evjpanel.setPreferredSize(null);
	}
	
	

	//@Override
	public void setToolTipText(String plain) {
		if ((tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_ON)
				|| (tooltipsInThisView == EuclidianStyleConstants.TOOLTIPS_AUTOMATIC)) {
			evjpanel.setToolTipText(plain);
		}
	}
	
	

	public int getWidth() {
		return evjpanel.getWidth();
	}
	
	public int getHeight() {
		return evjpanel.getHeight();
	}
	

	@Override
	public void updateSize() {

		// record the old coord system

		setWidth(getWidth());
		setHeight(getHeight());
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		setRealWorldBounds();

		// ================================================
		// G.Sturr 8/27/10: test: rescale on window resize
		//
		// reset the coord system so that our view dimensions are restored
		// using the new scaling factors.

		// setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);

		GraphicsConfiguration gconf = evjpanel.getGraphicsConfiguration();
		try {
			createImage(gconf);
		} catch (OutOfMemoryError e) {
			bgImage = null;
			bgGraphics = null;
		}

		updateBackgroundImage();
		updateAllDrawables(true);
	}
	
	private void createImage(GraphicsConfiguration gc) {
		if (gc != null) {
			bgImage = new geogebra.awt.BufferedImage(gc.createCompatibleImage(getWidth(), getHeight()));
			bgGraphics = bgImage.createGraphics();
			if (antiAliasing) {
				setAntialiasing(bgGraphics);
			}
		}
	}
	

	@Override
	public void drawActionObjects(geogebra.common.awt.Graphics2D g2){
		// TODO layers for Buttons and Textfields
		// for cross-platform UI the stroke must be reset to show buttons
		// properly, see #442
		g2.setStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());
		evjpanel.paintChildren(
				geogebra.awt.Graphics2D.getAwtGraphics(g2)); // draws Buttons and Textfields
	}


	public void clearView() {
		evjpanel.removeAll(); // remove hotEqns
		resetLists();
		initView(false);
		updateBackgroundImage(); // clear traces and images
		// resetMode();
	}
		
	
	public geogebra.common.awt.Color getBackgroundCommon() {
		return new geogebra.awt.Color(evjpanel.getBackground());
	}

	@Override
	public void setBackground(geogebra.common.awt.Color bgColor) {
		evjpanel.setBackground(geogebra.awt.Color.getAwtColor(bgColor));
	}
	
	
	
	
	
	
	
	
	
	
	// temp image
	private final Graphics2D g2Dtemp = new BufferedImage(5, 5,
			BufferedImage.TYPE_INT_RGB).createGraphics();
	/**
	 * @return temporary graphics that is stored in this view
	 */
	final public Graphics2D getTempGraphics2D() {
		g2Dtemp.setFont(getApplication().getPlainFont());
		return g2Dtemp;
	}
	
	@Override
	final public geogebra.common.awt.Graphics2D getTempGraphics2D(geogebra.common.awt.Font font) {
		g2Dtemp.setFont(geogebra.awt.Font.getAwtFont(font)); // Michael Borcherds 2008-06-11 bugfix for
								// Corner[text,n]
		return new geogebra.awt.Graphics2D(g2Dtemp);
	}


	/**
	 * Sets antialiasing of given graphics to ON
	 * (both for text and drawings)
	 * @param g2 graphics
	 */
	final public static void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	@Override
	final public void setAntialiasing(geogebra.common.awt.Graphics2D g2) {
		setAntialiasing(geogebra.awt.Graphics2D.getAwtGraphics(g2));
	}
	
	@Override
	final protected void setHeight(int height) {
	}

	@Override
	final protected void setWidth(int width) {
	}
	
	
	@Override
	final protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
	}
	
	/**
	 * 
	 * @return new euclidian style bar
	 */
	protected EuclidianStyleBar newEuclidianStyleBar(){
		return new EuclidianStyleBar(this);
	}


	final public geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = newEuclidianStyleBar();
		}

		return styleBar;
	}

	/**
	 * @return whether stylebar of this view exists
	 */
	final public boolean hasStyleBar() {
		return styleBar != null;
	}
	
	
	// for AlgebraView
	/***************************************************************************
	 * ANIMATED ZOOMING
	 **************************************************************************/
	@Override
	protected MyZoomer newZoomer() {
		return new MyZoomer(this);
	}
		
	public abstract EuclidianController getEuclidianController();


}
