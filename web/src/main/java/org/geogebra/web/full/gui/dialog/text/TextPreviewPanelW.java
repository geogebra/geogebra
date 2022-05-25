package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.dialog.TextPreviewer;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

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

	private MyEuclidianViewPanelForTextPreviewPanelW evPanel;

	/**
	 * @param kernel
	 *            kernel
	 */
	public TextPreviewPanelW(Kernel kernel) {
		super(kernel);
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
	 *            preview content
	 */
	@Override
	protected void updateViewportSize(GeoText previewGeo) {
		
		int padding = 5; // account for inset
		
		boolean isLatex = previewGeo.isLaTeX();
		boolean serif = previewGeo.isSerifFont();
		
		int size = (int) (previewGeo.getFontSizeMultiplier() * getApp()
		        .getFontSize());
		GFont textFont = getApp().getFontCommon(serif, previewGeo.getFontStyle(),
		        size);

		GRectangle rect = AwtFactory.getPrototype().newRectangle();
		if (isLatex) {
			EuclidianStatic.drawMultilineLaTeX(getApp(),
					ev.getTempGraphics2D(textFont), previewGeo,
					ev.getTempGraphics2D(textFont), textFont, GColor.BLACK,
					GColor.WHITE,
					previewGeo.getTextString(), 0, 0, serif, null, rect);
			
		} else {
			EuclidianStatic.drawMultiLineText(getApp(),
					previewGeo.getTextString(), 0, 0,
					((EuclidianViewW) ev).getG2P(),
					serif, textFont, rect, null, DrawText.DEFAULT_MARGIN);
		}
		int w = (int) rect.getWidth() + padding;
		int h = (int) rect.getHeight() + padding;
		((EuclidianViewW) ev).setPreferredSize(w, h);
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
		if (ev == null) {
			ev = new MyEuclidianView(getEVPanel(), new EuclidianControllerW(
					kernel), EuclidianView.EVNO_GENERAL, null);
		}
		return (EuclidianViewW) ev;
	}

	private MyEuclidianViewPanelForTextPreviewPanelW getEVPanel() {
		if (evPanel == null) {
			evPanel = new MyEuclidianViewPanelForTextPreviewPanelW();
		}
		return evPanel;
	}

	/**
	 * Update UI size
	 */
	public void onResize() {
		getEVPanel().onResize();
	}

	/****************************************************************************
	 * Extension of EuclidianViewD for displaying preview text strings
	 * 
	 */
	private static class MyEuclidianView extends EuclidianViewW {

		public MyEuclidianView(MyEuclidianViewPanelForTextPreviewPanelW panel,
				EuclidianController ec, 
		        int evno, EuclidianSettings settings) {
			super(panel, ec, evno, settings);

			// the show axis and show grid parameters currently do nothing, so
			// we do it here
			this.setShowAxes(false, false);
			this.showGrid(false);
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

		@Override
		public AbsolutePanel getAbsolutePanel() {
			return this;
		}

		@Override
		public Panel getEuclidianPanel() {
			return this;
		}

		@Override
		public Canvas getCanvas() {
			return canvas;
		}

		@Override
		public EuclidianView getEuclidianView() {
			return ev;
		}

		@Override
		public void onResize() {
			// nothing to do on resize
		}

		@Override
		public void deferredOnResize() {
			// nothing to do on resize
		}

		@Override
		public void updateNavigationBar() {
			// not needed for text preview
		}

		@Override
		public void reset() {
			// not needed
		}

	}

}
