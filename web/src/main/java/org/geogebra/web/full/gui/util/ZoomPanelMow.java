package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.web.html5.util.TestHarness;

/**
 * @author csilla
 *
 */
public class ZoomPanelMow extends FlowPanel
		implements SetLabels, CoordSystemListener {
	private AppW appW;
	private StandardButton dragPadBtn;
	private StandardButton zoomInBtn;
	private StandardButton zoomOutBtn;
	private StandardButton homeBtn;
	private ZoomController zoomController;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ZoomPanelMow(AppW app) {
		this.appW = app;
		zoomController = new ZoomController(appW, app.getActiveEuclidianView());
		if (app.getActiveEuclidianView() != null) {
			app.getActiveEuclidianView().getEuclidianController()
					.addZoomerListener(this);
		}
		buildGui();
	}

	private void buildGui() {
		addStyleName("mowZoomPanel");
		addDragPadButton();
		addZoomButtons();
	}

	/**
	 * @return zoom controller
	 */
	public ZoomController getZoomController() {
		return zoomController;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return appW;
	}

	/**
	 * @return drag pad button
	 */
	public StandardButton getDragPadBtn() {
		return dragPadBtn;
	}

	/**
	 * remove selected style
	 */
	public void deselectDragBtn() {
		getDragPadBtn().removeStyleName("selected");
		getAppW().setMode(EuclidianConstants.MODE_SELECT_MOW);
	}

	private void addDragPadButton() {
		dragPadBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.move_canvas(), null, 24, appW);
		dragPadBtn.setStyleName("zoomPanelBtn");
		TestHarness.setAttr(dragPadBtn, "panViewTool");

		FastClickHandler handlerHome = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getAppW().setMode(EuclidianConstants.MODE_TRANSLATEVIEW);
				getDragPadBtn().addStyleName("selected");
				if (getAppW().isMenuShowing()) {
					getAppW().toggleMenu();
				}
			}
		};
		dragPadBtn.addFastClickHandler(handlerHome);
		add(dragPadBtn);
		// click handler
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// to stopPropagation and preventDefault.
			}
		});
	}

	/**
	 * Add zoom in/out buttons to GUI
	 */
	private void addZoomButtons() {
		if (!Browser.isMobile()) {
			addZoomInButton();
			addZoomOutButton();
		}
		homeBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.home_zoom_black18(), null, 20,
				appW);
		homeBtn.setStyleName("zoomPanelBtn");
		homeBtn.addStyleName("zoomPanelBtnSmall");
		getZoomController().hideHomeButton(homeBtn);
		FastClickHandler handlerHome = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getZoomController().onHomePressed();
				deselectDragBtn();
			}
		};
		homeBtn.addFastClickHandler(handlerHome);
		add(homeBtn);
		// click handler
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// to stopPropagation and preventDefault.
			}
		});
	}

	private void addZoomOutButton() {
		zoomOutBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.zoomout_black24(), null, 24, appW);
		zoomOutBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerZoomOut = new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				getZoomController().onZoomOutPressed();
				deselectDragBtn();
			}
		};
		zoomOutBtn.addFastClickHandler(handlerZoomOut);
		add(zoomOutBtn);
	}

	private void addZoomInButton() {
		zoomInBtn = new StandardButton(
				ZoomPanelResources.INSTANCE.zoomin_black24(), null, 24, appW);
		zoomInBtn.setStyleName("zoomPanelBtn");

		FastClickHandler handlerZoomIn = new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				getZoomController().onZoomInPressed();
				deselectDragBtn();
			}
		};
		zoomInBtn.addFastClickHandler(handlerZoomIn);
		add(zoomInBtn);
	}

	/**
	 * Sets translated titles of the buttons.
	 */
	@Override
	public void setLabels() {
		setButtonTitleAndAltText(dragPadBtn, "PanView");
		setButtonTitleAndAltText(homeBtn, "StandardView");
		setButtonTitleAndAltText(zoomOutBtn, "ZoomOut.Tool");
		setButtonTitleAndAltText(zoomInBtn, "ZoomIn.Tool");
	}

	private void setButtonTitleAndAltText(StandardButton btn, String string) {
		if (btn != null) {
			btn.setTitle(appW.getLocalization().getMenu(string));
			btn.setAltText(appW.getLocalization().getMenu(string));
		}
	}

	@Override
	public void onCoordSystemChanged() {
		getZoomController().updateHomeButton(homeBtn);
	}
}
