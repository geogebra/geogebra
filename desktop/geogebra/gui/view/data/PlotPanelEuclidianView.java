package geogebra.gui.view.data;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;

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
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;

/**
 * 
 * An extension of EuclidianView used for display of a set of GeoElements
 * without all of the mouse and key controls of the full EuclidianView. Unlike
 * EuclidianView, this view remains centered in the panel when resized.
 * 
 * Includes a right-click context menu and DnD support for exporting either the
 * set of GeoElements or an image of the view.
 * 
 * 
 * @author G.Sturr 2010-6-30
 * 
 */
public class PlotPanelEuclidianView extends EuclidianViewD implements
		ComponentListener, DragGestureListener, DragSourceListener {

	private final EuclidianControllerD ec;
	private final PlotPanelEuclidianView plotPanelEV;

	private static boolean[] showAxes = { true, true };
	private static boolean showGrid = false;

	/** Plot panel viewID. This is not a constant; it is assigned by GuiManager. */
	private int viewID;

	/** Settings to control EuclidianView features (e.g. axes visibility) */
	private PlotSettings plotSettings;

	/** Mouse listener to trigger context menu */
	private MyMouseListener myMouseListener;

	/**
	 * Flag to determine if the mouse is over the drag region, a thin rectangle
	 * at the top of the panel
	 */
	private boolean overDragRegion = false;

	/** Drag source for DnD */
	private DragSource ds;

	/** DnD cursors */
	private Cursor grabbingCursor, grabCursor;

	/** List of AbstractActions for the popup context menu */
	ArrayList<AbstractAction> actionList;

	/**
	 * Action method export of GeoElements to EuclidianView. Since the action is
	 * specific to the parent container, it is injected in the constructor.
	 * */
	private AbstractAction exportToEVAction;

	/** DataFlavor for plotPanel drags */
	public final static DataFlavor plotPanelFlavor = new DataFlavor(
			DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=javax.swing.AbstractAction", "plotPanelFlavor");

	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianView(Kernel kernel, AbstractAction exportAction) {
		super(new PlotPanelEuclidianController(kernel), showAxes, showGrid,
				null);

		// set fields
		plotPanelEV = this;
		this.ec = this.getEuclidianController();
		this.exportToEVAction = exportAction;
		plotSettings = new PlotSettings();

		// get viewID from GuiManager
		viewID = ((GuiManagerD) kernel.getApplication().getGuiManager())
				.assignPlotPanelID(this);

		// create cursors for DnD
		grabCursor = getCursorForImage(getApplication().getImageIcon(
				"cursor_grab.gif").getImage());
		grabbingCursor = getCursorForImage(getApplication().getImageIcon(
				"cursor_grabbing.gif").getImage());

		// enable/disable mouseListeners
		setMouseEnabled(false, true);
		setMouseMotionEnabled(false);
		setMouseWheelEnabled(false);
		this.addMouseMotionListener(new MyMouseMotionListener());

		// set some default EV features
		setAllowShowMouseCoords(false);
		setAxesCornerCoordsVisible(false);
		setAntialiasing(true);
		updateFonts();

		// set preferred size so that updateSize will work and this EV can be
		// properly initialized
		setPreferredSize(new Dimension(300, 200));
		setSize(new Dimension(300, 200));
		updateSize();

		// add a component listener that will allow the view to resize in a
		// centered
		addComponentListener(this);
		enableDnD();

	}

	/*********** End Constructor **********************/

	/**
	 * Overrides EuclidianView setMode method so that no action is taken on a
	 * mode change.
	 */
	@Override
	public void setMode(int mode) {
		// .... do nothing
	}

	/** Returns viewID */
	@Override
	public int getViewID() {
		return viewID;
	}

	/**
	 * Override updateSize() so that our plots stay centered and scaled in a
	 * resized window.
	 */
	@Override
	public void updateSize() {

		// record the old coord system
		double xminTemp = getXmin();
		double xmaxTemp = getXmax();
		double yminTemp = getYmin();
		double ymaxTemp = getYmax();

		// standard update: change the coord system to match new window
		// dimensions with the upper left corner fixed and the other bounds
		// adjusted.
		super.updateSize();

		// now reset the coord system so that our view dimensions are restored
		// using the new scaling factors.
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}

	// ==================================================
	// Plot Settings
	// =================================================

	/**
	 * Returns plotSettings field for this panel.
	 * 
	 * @return
	 */
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}

	/**
	 * Sets the plotSettings field and updates the panel accordingly.
	 * 
	 * @param plotSettings
	 */
	public void updateSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
		this.setEVParams();
	}

	/**
	 * Uses the values stored in the plotSettings field to update the features
	 * of this EuclidianView (e.g. axes visibility)
	 */
	public void setEVParams() {

		showGrid(plotSettings.showGrid);
		setShowAxis(EuclidianViewInterfaceCommon.AXIS_Y,
				plotSettings.showYAxis, false);

		setAutomaticGridDistance(plotSettings.gridIntervalAuto);
		if (!plotSettings.gridIntervalAuto) {
			this.setGridDistances(plotSettings.gridInterval);
		}

		if (plotSettings.showArrows) {
			setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_ARROW);
		} else {
			setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_FULL);
		}

		setDrawBorderAxes(plotSettings.isEdgeAxis);
		if (!plotSettings.isEdgeAxis[0]) {
			setAxisCross(0, 0);
		}
		if (!plotSettings.isEdgeAxis[1]) {
			setAxisCross(1, 0);
		}

		setPositiveAxes(plotSettings.isPositiveOnly);

		if (plotSettings.forceXAxisBuffer) {
			// ensure that the axis labels are shown
			// by forcing a fixed pixel height below the x-axis
			double pixelOffset = (30 * getApplication().getSmallFont()
					.getSize()) / 12.0;
			double pixelHeight = this.getHeight();
			plotSettings.yMin = (-pixelOffset * plotSettings.yMax)
					/ (pixelHeight + pixelOffset);
		}

		setAxesCornerCoordsVisible(false);

		this.setAutomaticAxesNumberingDistance(plotSettings.xAxesIntervalAuto,
				0);
		this.setAutomaticAxesNumberingDistance(plotSettings.yAxesIntervalAuto,
				1);
		if (!plotSettings.xAxesIntervalAuto) {
			setAxesNumberingDistance(plotSettings.xAxesInterval, 0);
		} else {
			plotSettings.xAxesInterval = getAxesNumberingDistances()[0];
		}
		if (!plotSettings.yAxesIntervalAuto) {
			setAxesNumberingDistance(plotSettings.yAxesInterval, 1);
		} else {
			plotSettings.yAxesInterval = getAxesNumberingDistances()[1];
		}

		setPointCapturing(plotSettings.pointCaptureStyle);

		// do this last ?
		setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax,
				plotSettings.yMin, plotSettings.yMax);

		repaint();
	}

	// ===========================================================
	// Component Listener
	// ===========================================================

	public void componentHidden(ComponentEvent arg0) {
		// ignore
	}

	public void componentMoved(ComponentEvent arg0) {
		// ignore
	}

	public void componentResized(ComponentEvent arg0) {
		// make sure that we force a pixel buffer under the x-axis
		setEVParams();
	}

	public void componentShown(ComponentEvent arg0) {
		// ignore
	}

	// ==================================================
	// Mouse Listeners
	// =================================================

	/**
	 * Enables/disables the default EuclidianController mouse listener and
	 * myMouseListener, the listener that handles the right-click context menu.
	 * 
	 * @param enableECMouseListener
	 *            default = false
	 * @param enableMyMouseListener
	 *            default = true
	 */
	public void setMouseEnabled(boolean enableECMouseListener,
			boolean enableMyMouseListener) {
		if (myMouseListener == null) {
			myMouseListener = new MyMouseListener();
		}
		removeMouseListener(myMouseListener);
		removeMouseListener(ec);

		if (enableMyMouseListener) {
			addMouseListener(myMouseListener);
		}
		if (enableECMouseListener) {
			addMouseListener(ec);
		}
	}

	/**
	 * Enables/disables the EuclidianController mouse motion listener
	 * 
	 * @param enableMouseMotion
	 *            default = false
	 */
	public void setMouseMotionEnabled(boolean enableMouseMotion) {
		removeMouseMotionListener(ec);
		if (enableMouseMotion) {
			addMouseMotionListener(ec);
		}
	}

	/**
	 * Enables/disables the EuclidianController mouse wheel listener
	 * 
	 * @param enableMouseWheel
	 *            default = false
	 */
	public void setMouseWheelEnabled(boolean enableMouseWheel) {
		removeMouseWheelListener(ec);
		if (enableMouseWheel) {
			addMouseWheelListener(ec);
		}
	}

	/**
	 * Mouse listener class to handle right click trigger for the context menu.
	 * Right click events are consumed to prevent the EuclidianController from
	 * handling right-clicks as well.
	 */
	private class MyMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			Object ob = e.getSource();
			// right click shows context menu
			if (AppD.isRightClick(e)) {
				e.consume();
				ContextMenu popup = new ContextMenu();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mousePressed(MouseEvent e) {
			if (AppD.isRightClick(e)) {
				e.consume();
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (AppD.isRightClick(e)) {
				e.consume();
			}
		}

		public void mouseEntered(MouseEvent e) {
			// ignore
		}

		public void mouseExited(MouseEvent e) {
			// ignore
		}

	}

	/**
	 * Mouse motion listener for handling DnD drags
	 */
	class MyMouseMotionListener implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {
			// ignore
		}

		/** handles mouse motion over the drag region */
		public void mouseMoved(MouseEvent e) {
			overDragRegion = e.getPoint().y < 10;
			setDefaultCursor();
		}
	}

	/**
	 * Overrides EuclidianView.setDefaultCursor so that DnD grab hand cursors
	 * are drawn when over the drag region.
	 */
	@Override
	public void setDefaultCursor() {
		if (overDragRegion) {
			setCursor(grabCursor);
		} else {
			setCursor(defaultCursor);
		}
	}

	// =============================================
	// Context Menu Popup
	// =============================================

	/**
	 * Popup menu with menu items for exporting either the GeoElements or an
	 * image of the view.
	 */
	private class ContextMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public ContextMenu() {
			this.setOpaque(true);
			setFont(getApplication().getPlainFont());

			for (AbstractAction action : getActionList()) {
				add(action);
			}
		}
	}

	public JPopupMenu getContextMenu() {
		return new ContextMenu();
	}

	/**
	 * Sets the list of AbstractActions to be used in the popup context menu.
	 * 
	 * @param actionList
	 */
	public void setActionList(ArrayList<AbstractAction> actionList) {
		this.actionList = actionList;
	}

	/**
	 * Returns the list of AbstractActions to be used in the popup context menu.
	 * 
	 * @return
	 */
	public ArrayList<AbstractAction> getActionList() {

		if (actionList == null) {
			actionList = new ArrayList<AbstractAction>();

			if (exportToEVAction != null) {
				exportToEVAction.putValue(Action.NAME, getApplication()
						.getMenu("CopyToGraphics"));
				exportToEVAction.putValue(Action.SMALL_ICON, getApplication()
						.getEmptyIcon());
				actionList.add(exportToEVAction);
			}
			actionList.add(drawingPadToClipboardAction);
			actionList.add(exportGraphicAction);
		}
		return actionList;
	}

	/**
	 * Adds an AbstractAction to the end of the list of AbstractActions
	 * displayed in the context menu.
	 * 
	 * @param action
	 */
	public void appendActionList(AbstractAction action) {
		getActionList().add(action);
	}

	/**
	 * Action to export an image of the view as a file.
	 */
	AbstractAction exportGraphicAction = new AbstractAction(getApplication()
			.getPlain("ExportAsPicture") + "...", getApplication()
			.getImageIcon("image-x-generic.png")) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				Thread runner = new Thread() {
					@Override
					public void run() {
						getApplication().setWaitCursor();
						try {
							getApplication().clearSelectedGeos(true,false);
							getApplication().updateSelection(false);

							// use reflection for
							JDialog d = new geogebra.export.GraphicExportDialog(
									getApplication(), plotPanelEV);
							d.setVisible(true);

						} catch (Exception e) {
							App.debug("GraphicExportDialog not available");
						}
						getApplication().setDefaultCursor();
					}
				};
				runner.start();
			}

			catch (java.lang.NoClassDefFoundError ee) {
				getApplication().showError("ExportJarMissing");
				ee.printStackTrace();
			}
		}
	};

	/**
	 * Action to export an image of the view to the clipboard.
	 */
	AbstractAction drawingPadToClipboardAction = new AbstractAction(
			getApplication().getMenu("CopyToClipboard"), getApplication()
					.getEmptyIcon()) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			getApplication().clearSelectedGeos(true,false);
			getApplication().updateSelection(false);

			Thread runner = new Thread() {
				@Override
				public void run() {
					getApplication().setWaitCursor();
					getApplication().copyGraphicsViewToClipboard(plotPanelEV);
					getApplication().setDefaultCursor();
				}
			};
			runner.start();
		}
	};

	// =====================================================
	// Drag and Drop
	// =====================================================

	protected void enableDnD() {
		ds = new DragSource();
		DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(
				this.getJPanel(), DnDConstants.ACTION_COPY, this);
	}

	public void dragDropEnd(DragSourceDropEvent e) {
		// clean up selection rectangle
		plotPanelEV.setSelectionRectangle(null);
		plotPanelEV.repaint();
	}

	public void dragEnter(DragSourceDragEvent e) {
		// ignore
	}

	public void dragExit(DragSourceEvent e) {
		// ignore
	}

	public void dragOver(DragSourceDragEvent e) {
		// ignore
	}

	public void dropActionChanged(DragSourceDragEvent e) {
		// ignore
	}

	public void dragGestureRecognized(DragGestureEvent dge) {

		if (overDragRegion) {
			plotPanelEV.setSelectionRectangle(null);
			// start drag
			ds.startDrag(dge, DragSource.DefaultCopyDrop, null,
					new Point(0, 0), new TransferablePlotPanel(), this);
		}

	}

	/**
	 * Extension of Transferable for exporting PlotPanelEV contents
	 */
	public class TransferablePlotPanel implements Transferable {

		private final DataFlavor supportedFlavors[] = { plotPanelFlavor,
				DataFlavor.imageFlavor };

		private final String plotPanelIdentifier;
		private final Image image;

		// private final Action act;

		public TransferablePlotPanel() {
			image = plotPanelEV.getExportImage(1d);
			plotPanelIdentifier = "ProbabilityCalculator";
			// act = sampleAction;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			for (int i = 0; i < supportedFlavors.length; i++) {
				if (flavor.equals(supportedFlavors[i])) {
					return true;
				}
			}
			return false;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (flavor.equals(plotPanelFlavor)) {
				return exportToEVAction;
			}
			if (flavor.equals(DataFlavor.imageFlavor)) {
				return image;
			}
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
