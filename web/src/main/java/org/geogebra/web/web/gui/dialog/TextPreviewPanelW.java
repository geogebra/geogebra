package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.TextPreviewer;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Web implementation of TextPreviewPanel
 * 
 * @author G. Sturr
 * 
 */
public class TextPreviewPanelW extends TextPreviewer {

	private AppW app;
	private EuclidianViewW ev;
	private MyEuclidianViewPanelForTextPreviewPanelW evPanel;

	/**
	 * @param kernel
	 */
	public TextPreviewPanelW(Kernel kernel) {

		super(kernel);
		app = (AppW) kernel.getApplication();
		ev = getEuclidianView();
	}

	/**
	 * @return Widget that encloses the EuclidianView
	 */
	public Widget getPanel() {
		return getEVPanel();
	}

	/**
	 * Updates the preferred size of this panel to match the estimated size of
	 * the given preview geo. This forces the enclosing scrollpane to show
	 * scrollbars when the size of the preview geo grows larger than the
	 * scrollpane viewport.
	 * 
	 * Note: The preview geo uses absolute screen coords, so we can't easily get
	 * the bounding box dimensions and must use dummy containers to estimate
	 * these dimensions.
	 * 
	 * @param previewGeo
	 */
	@Override
	protected void updateViewportSize(GeoText previewGeo) {
		
		int padding = 5; // account for inset
		
		boolean isLatex = previewGeo.isLaTeX();
		boolean serif = previewGeo.isSerifFont();
		
		int size = (int) (previewGeo.getFontSizeMultiplier() * app
		        .getFontSize());
		GFont textFont = app.getFontCommon(serif, previewGeo.getFontStyle(),
		        size);
		
		
		GRectangle rect;
		if (isLatex) {
		
			rect = EuclidianStatic.drawMultilineLaTeX(app,
					ev.getTempGraphics2D(textFont), previewGeo,
					ev.getTempGraphics2D(textFont),
 textFont, GColor.BLACK,
					GColor.WHITE,
			        previewGeo.getTextString(), 0, 0, serif);
			
		} else {
			
			rect = EuclidianStatic.drawMultiLineText(app,
			        previewGeo.getTextString(), 0, 0, ev.g2p, serif, textFont);
		}
		//App.debug("text rect: " + rect.getWidth() + " x " +
	//	rect.getHeight());
		int w = (int) rect.getWidth() + padding;
		int h = (int) rect.getHeight() + padding;
		ev.setPreferredSize(w, h);
		evPanel.setSize(w + "px", h + "px");

	}

	@Override
	protected void removeEVMouseListeners() {
		// ev.removeMouseListener(ev.getEuclidianController());
		// ev.removeMouseMotionListener(ev.getEuclidianController());
		// ev.removeMouseWheelListener(ev.getEuclidianController());
	}

	@Override
	protected EuclidianViewW getEuclidianView() {
		boolean[] showAxes = { false, false };
		boolean showGrid = false;
		if (ev == null) {
			ev = new MyEuclidianView(getEVPanel(), new EuclidianControllerW(
			        kernel), showAxes, showGrid, EuclidianView.EVNO_GENERAL,
			        null);
		}
		return ev;
	}

	private MyEuclidianViewPanelForTextPreviewPanelW getEVPanel() {
		if (evPanel == null) {
			evPanel = new MyEuclidianViewPanelForTextPreviewPanelW();
		}
		return evPanel;
	}

	public void onResize() {
		getEVPanel().onResize();
	}

	/****************************************************************************
	 * Extension of EuclidianViewD for displaying preview text strings
	 * 
	 */
	private class MyEuclidianView extends EuclidianViewW {

		public MyEuclidianView(MyEuclidianViewPanelForTextPreviewPanelW panel,
		        EuclidianController ec, boolean[] showAxes, boolean showGrid,
		        int evno, EuclidianSettings settings) {
			super(panel, ec, showAxes, showGrid, evno, settings);

			// the show axis and show grid parameters currently do nothing, so
			// we do it here
			this.setShowAxes(false, false);
			this.showGrid(false);
			
			this.setAntialiasing(true);
		}

		/**
		 * Overrides attachView with an empty method to prevent this panel from
		 * attaching to the kernel
		 */
		@Override
		public void attachView() {
			// do nothing
		}

		@Override
		public int getViewID() {
			return App.VIEW_TEXT_PREVIEW;
		}

		@Override
		public boolean isPlotPanel() {
			return true;
		}
	}

	/****************************************************************************
	 * Panel for EuclidianView
	 * 
	 */
	private class MyEuclidianViewPanelForTextPreviewPanelW extends AbsolutePanel implements
	        EuclidianPanelWAbstract {

		private Canvas canvas;

		public MyEuclidianViewPanelForTextPreviewPanelW() {
			super();
			canvas = Canvas.createIfSupported();
			canvas.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			canvas.getElement().getStyle().setZIndex(0);
			add(canvas);

		}

		public AbsolutePanel getAbsolutePanel() {
			return this;
		}

		public Panel getEuclidianPanel() {
			return this;
		}

		public Canvas getCanvas() {
			return canvas;
		}

		public EuclidianView getEuclidianView() {

			return ev;
		}

		public void onResize() {
		//	ev.setCoordinateSpaceSizeDirectly(100, 100);
		}

		public void deferredOnResize() {
		}

		public void updateNavigationBar() {
			// TODO Auto-generated method stub

		}

	}

	public void setText(String buildGeoGebraString) {
		// TODO Auto-generated method stub

	}

}
