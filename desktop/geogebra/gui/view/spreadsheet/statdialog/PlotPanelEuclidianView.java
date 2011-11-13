package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;

/**
 * 
 * Creates a JPanel with an extended instance of EuclidianView and methods for 
 * creating geos in the panel.
 * 
 * @author gsturr 2010-6-30
 *
 */
public class PlotPanelEuclidianView extends EuclidianView 
implements ComponentListener, DragGestureListener, DragSourceListener {


	private EuclidianController ec;
	private PlotPanelEuclidianView plotPanelEV;

	public int viewID;

	private static boolean[] showAxes = { true, true };
	private static boolean showGrid = false;

	private double xMinData, xMaxData, yMinData, yMaxData;

	private PlotSettings plotSettings;

	private MyMouseListener myMouseListener;

	private boolean enableContextMenu;



	public PlotSettings getPlotSettings() {
		return plotSettings;
	}


	/**
	 * Sets the given plotSettings and updates the panel accordingly
	 * @param plotSettings
	 */
	public void updateSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
		this.setEVParams();
	}

	private boolean overDragRegion = false;
	private DragSource ds;

	protected void enableDnD(){
		ds = new DragSource();
		DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
	}

	//protected  Cursor defaultCursor = Cursor.getDefaultCursor(); 
	//protected  Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
	protected  Cursor grabbingCursor, grabCursor; 



	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianView(Kernel kernel) {
		super(new PlotPanelEuclidianController(kernel), showAxes, showGrid);

		plotPanelEV = this;
		this.ec = this.getEuclidianController();

		viewID = kernel.getApplication().getGuiManager().assignPlotPanelID(this);


		grabCursor = getCursorForImage(app.getImageIcon("cursor_grab.gif").getImage());
		grabbingCursor = getCursorForImage(app.getImageIcon("cursor_grabbing.gif").getImage());


		setMouseEnabled(false);
		setMouseMotionEnabled(false);
		setMouseWheelEnabled(false);
		setAllowShowMouseCoords(false);
		setAxesCornerCoordsVisible(false);
		setContextMenuEnabled(true);

		this.addMouseMotionListener( new MyMouseMotionListener());

		setAntialiasing(true);
		updateFonts();
		setPreferredSize(new Dimension(300,200));
		setSize(new Dimension(300,200));
		updateSize();

		plotSettings = new PlotSettings();

		addComponentListener(this);
		enableDnD();

	}

	public void setMouseEnabled(boolean enableMouse){

		removeMouseListener(ec);
		if(enableMouse)
			addMouseListener(ec);
		setContextMenuEnabled(enableContextMenu);
	}

	public void setMouseMotionEnabled(boolean enableMouseMotion){
		removeMouseMotionListener(ec);	
		if(enableMouseMotion)
			addMouseMotionListener(ec);
	}

	public void setMouseWheelEnabled(boolean enableMouseWheel){
		removeMouseWheelListener(ec);	
		if(enableMouseWheel)
			addMouseWheelListener(ec);
	}

	public void setContextMenuEnabled(boolean enableContextMenu){
		this.enableContextMenu = enableContextMenu;
		if(myMouseListener == null)
			myMouseListener = new MyMouseListener();
		removeMouseListener(myMouseListener);
		if(enableContextMenu)
			addMouseListener(myMouseListener);;
	}




	/**
	 * Override UpdateSize() so that our plots stay centered and scaled in a
	 * resized window.
	 */
	@Override
	public void updateSize(){

		// record the old coord system
		double xminTemp = getXmin();
		double xmaxTemp = getXmax();
		double yminTemp = getYmin();
		double ymaxTemp = getYmax();	

		// standard update: change the coord system to match new window dimensions
		// with the upper left corner fixed and the other bounds adjusted.  
		super.updateSize();		

		// now reset the coord system so that our view dimensions are restored 
		// using the new scaling factors. 
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}	


	@Override
	public void setMode(int mode) {
		// .... do nothing
	}


	public void setEVParams(){

		showGrid(plotSettings.showGrid);
		setShowAxis(EuclidianView.AXIS_Y, plotSettings.showYAxis, false);

		setAutomaticGridDistance(plotSettings.gridIntervalAuto);
		if(!plotSettings.gridIntervalAuto){
			this.setGridDistances(plotSettings.gridInterval);
		}


		if(plotSettings.showArrows){
			setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		}else{
			setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_FULL);
		}

		setDrawBorderAxes(plotSettings.isEdgeAxis);
		if(!plotSettings.isEdgeAxis[0])
			setAxisCross(0,0);
		if(!plotSettings.isEdgeAxis[1])
			setAxisCross(1,0);


		setPositiveAxes(plotSettings.isPositiveOnly);


		if(plotSettings.forceXAxisBuffer){
			// ensure that the axis labels are shown
			// by forcing a fixed pixel height below the x-axis	
			double pixelOffset = 30 * app.getSmallFont().getSize()/12.0;
			double pixelHeight = this.getHeight(); 
			plotSettings.yMin = - pixelOffset * plotSettings.yMax / (pixelHeight + pixelOffset);
		}


		setAxesCornerCoordsVisible(false);


		this.setAutomaticAxesNumberingDistance(plotSettings.xAxesIntervalAuto, 0);
		this.setAutomaticAxesNumberingDistance(plotSettings.yAxesIntervalAuto, 1);
		if(!plotSettings.xAxesIntervalAuto){
			setAxesNumberingDistance(plotSettings.xAxesInterval, 0);
		}
		if(!plotSettings.yAxesIntervalAuto){
			setAxesNumberingDistance(plotSettings.yAxesInterval, 1);
		}

		setPointCapturing(plotSettings.pointCaptureStyle);

		// do this last ?
		setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax, plotSettings.yMin, plotSettings.yMax);

		repaint();
	}




	//==================================================
	//       Component Listener  (for resizing our EV)
	//=================================================

	public void componentHidden(ComponentEvent arg0) {	
	}
	public void componentMoved(ComponentEvent arg0) {
	}
	public void componentResized(ComponentEvent arg0) {
		// make sure that we force a pixel buffer under the x-axis 
		setEVParams();
	}
	public void componentShown(ComponentEvent arg0) {
	}




	//==================================================
	//       Mouse Handlers
	//=================================================


	protected void processMouseEvent(MouseEvent e){
		if(e.getClickCount() > 1){
			e.consume();
			return;}

		else if(Application.isRightClick(e)){
			if(e.getID()==MouseEvent.MOUSE_RELEASED){
				ContextMenu contextMenu = new ContextMenu();
				contextMenu.show(e.getComponent(), e.getX(),e.getY());
				e.consume();
			}
			return;	}

		else
			super.processMouseEvent(e);
	}


	private static class MyMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			Object ob = e.getSource();	
			// right click shows context menu
			if (Application.isRightClick(e)) {
				e.consume();
				//app.getGuiManager().showPopupMenu(temp, table, origin);
			}
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

	}



	class MyMouseMotionListener implements MouseMotionListener{



		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseMoved(MouseEvent e) {
			overDragRegion = e.getPoint().y < 10;
			//if(overDragRegion)
			//plotPanelEV.setSelectionRectangle(new Rectangle(0, 0, plotPanelEV.getWidth(), plotPanelEV.getHeight()));
			//else
			//plotPanelEV.setSelectionRectangle(null);
		}
	}

	public void setDefaultCursor() {
		if(overDragRegion)
			setCursor(grabCursor);
		else
			setCursor(defaultCursor);
	}







	//=============================================
	//      Context Menu
	//=============================================

	private class ContextMenu extends JPopupMenu{

		public  ContextMenu(){
			this.setOpaque(true);
			//setBackground(bgColor);	
			setFont(app.getPlainFont());

			for(AbstractAction action : getActionList())
				add(action);
		}
	}


	ArrayList<AbstractAction> actionList;

	public ArrayList<AbstractAction> getActionList() {
		if(actionList == null){
			actionList = new ArrayList<AbstractAction>();
			//actionList.add(exportToEVAction);
			actionList.add(drawingPadToClipboardAction);
			actionList.add(exportGraphicAction);
		}
		return actionList;
	}

	public void setActionList(ArrayList<AbstractAction> actionList) {
		this.actionList = actionList;
	}

	public void appendActionList(AbstractAction action) {
		getActionList().add(action);
	}



	AbstractAction exportGraphicAction = new AbstractAction(app.getPlain("ExportAsPicture") + "...", app
			.getImageIcon("image-x-generic.png")) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				Thread runner = new Thread() {
					public void run() {
						app.setWaitCursor();
						try {
							app.clearSelectedGeos();

							// use reflection for
							JDialog d = new geogebra.export.GraphicExportDialog(app);
							d.setVisible(true);

						} catch (Exception e) {
							Application
							.debug("GraphicExportDialog not available");
						}
						app.setDefaultCursor();
					}
				};
				runner.start();
			}

			catch (java.lang.NoClassDefFoundError ee) {
				app.showError("ExportJarMissing");
				ee.printStackTrace();
			}
		}
	};

	AbstractAction drawingPadToClipboardAction = new AbstractAction(app
			.getMenu("CopyToClipboard"), app
			.getImageIcon("edit-copy.png")) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			app.clearSelectedGeos();

			Thread runner = new Thread() {
				public void run() {
					app.setWaitCursor();
					app.copyGraphicsViewToClipboard(plotPanelEV);
					app.setDefaultCursor();
				}
			};
			runner.start();
		}
	};






	//=====================================================
	// Drag and Drop 
	//=====================================================


	public void dragDropEnd(DragSourceDropEvent e) {}
	public void dragEnter(DragSourceDragEvent e) {}
	public void dragExit(DragSourceEvent e) {}
	public void dragOver(DragSourceDragEvent e) {}
	public void dropActionChanged(DragSourceDragEvent e) {}


	private ArrayList<String> geoLabelList;

	public void dragGestureRecognized(DragGestureEvent dge) {

		if(overDragRegion){
			plotPanelEV.setSelectionRectangle(null);
			// start drag
			ds.startDrag(dge, DragSource.DefaultCopyDrop, null, 
					new Point(0,0), new TransferablePlotPanel(),  this);
		}

	}


	public static final DataFlavor plotPanelFlavor = new DataFlavor(PlotPanelEuclidianView.class, "plotPanel");
	/**
	 * 	Extension of Transferable for exporting PlotPanelEV contents
	 */
	public class TransferablePlotPanel implements Transferable {


		private final DataFlavor supportedFlavors[] = { plotPanelFlavor, DataFlavor.imageFlavor };

		private String plotPanelIdentifier;
		private Image image;

		public TransferablePlotPanel() {
			image = plotPanelEV.getExportImage(1d);
			plotPanelIdentifier = "ProbabilityCalculator";
		}

		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			for(int i = 0; i < supportedFlavors.length; i++)
				if (flavor.equals(supportedFlavors[i]))
					return true;
			return false;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (flavor.equals(plotPanelFlavor))
				return plotPanelIdentifier;
			if (flavor.equals(DataFlavor.imageFlavor)){			
				return  image;
			}
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public int getViewID() {
		return viewID;
	}





}
