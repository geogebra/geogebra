package geogebra.web.gui.applet;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.View;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main frame containing every view / menu bar / ....
 * This Panel (Frame is resize able)
 */
public class GeoGebraFrame extends VerticalPanel {

	private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
	private static GeoGebraFrame activeInstance;

	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();

	/** The application */
	protected AppW app;

	private boolean resize = false;
	private boolean move = false;

	/** Creates new GeoGebraFrame */
	public GeoGebraFrame() {
		super();
		instances.add(this);
		activeInstance = this;
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER);
	}

	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * @param geoGebraMobileTags
	 *          list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {
		init(geoGebraMobileTags);
	}

	public static void useDataParamBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder == null || dpBorder.length() != 7 ||
			(dpBorder.length() > 0 && dpBorder.charAt(0) != '#')) {
			dpBorder = "#000000";
		}
		ae.getStyle().setBorderWidth(1, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gf.getStyleElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		gf.getStyleElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gf.getStyleElement().getStyle().setBorderColor(dpBorder);
	}

	public static void useFocusedBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = "#9999ff";

		ae.getStyle().setBorderWidth(1, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gf.getStyleElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		gf.getStyleElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gf.getStyleElement().getStyle().setBorderColor(dpBorder);
	}

	private static void init(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (ArticleElement articleElement : geoGebraMobileTags) {
			GeoGebraFrame inst = new GeoGebraFrame();
			inst.app = inst.createApplication(articleElement, inst);
			useDataParamBorder(articleElement, inst);
		    inst.add(inst.app.buildApplicationPanel());
		    RootPanel.get(articleElement.getId()).add(inst);
			
		}
	}

	public static void finishAsyncLoading(ArticleElement articleElement,
            GeoGebraFrame inst, AppW app) {
	    handleLoadFile(articleElement, app);
    }
	
	public static void renderArticleElemnt(ArticleElement element) {
		Date creationDate = new Date();
		element.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		GeoGebraFrame inst = new GeoGebraFrame();
		inst.app = inst.createApplication(element, inst);
		useDataParamBorder(element, inst);
	    inst.add(inst.app.buildApplicationPanel());
	    RootPanel.get(element.getId()).add(inst);
	}

	

	private static void handleLoadFile(ArticleElement articleElement,
			AppW app) {
		View view = new View(articleElement, app);
		fileLoader.setView(view);
		fileLoader.onPageLoad();
	}

	/**
	 * @return the application
	 */
	public App getApplication() {
		return app;
	}

	/**
	 * Sets the Application of the GeoGebraFrame
	 * @param app
	 *          the application
	 */
	public void setApplication(AppW app) {
		this.app = app;
	}

	/**
	 * @param useFullGui
	 *          if false only one euclidianView will be available (without
	 *          menus / ...)
	 * @return the newly created instance of Application
	 */
	protected AppW createApplication(ArticleElement ae, GeoGebraFrame gf) {
		return new AppW(ae, gf);
	}

	/**
	 * @return list of instances of GeogebraFrame
	 */
	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if(!app.getUseFullGui()){
			return;
		}
		final int eventType = DOM.eventGetType(event);

		switch (eventType) {
		case Event.ONMOUSEOVER:
			if (isCursorAtResizePosition(event)) {
				DOM.setStyleAttribute(getElement(), "cursor", "se-resize");
			} else {
				DOM.setStyleAttribute(getElement(), "cursor", "default");
			}
			break;
		case Event.ONMOUSEDOWN:
			if (isCursorAtResizePosition(event)) {
				if (resize == false) {
					resize = true;
					DOM.setCapture(getElement());
				}
			}
			break;
		case Event.ONMOUSEMOVE:
			if (resize == true) {
				int width = DOM.eventGetClientX(event);
				int height = DOM.eventGetClientY(event);

				setPixelSize(width, height);
				app.getGuiManager().resize(width, height);
			} else if (move == true) {
				RootPanel.get().setWidgetPosition(this, DOM.eventGetClientX(event),
						DOM.eventGetClientY(event));
			}
			break;
		case Event.ONMOUSEUP:
			if (move == true) {
				move = false;
				DOM.releaseCapture(getElement());
			}
			if (resize == true) {
				resize = false;
				DOM.releaseCapture(getElement());
			}
			break;
		}
	}

	/**
	 * Determines if the mouse cursor is at the bottom right position of the Frame
	 * @param event
	 *          the cursor event
	 * @return true if cursor is within a 10x10 frame on the bottom right position
	 */
	protected boolean isCursorAtResizePosition(Event event) {
		int cursorY = DOM.eventGetClientY(event);
		int initialY = getAbsoluteTop();
		int height = getOffsetHeight();

		int cursorX = DOM.eventGetClientX(event);
		int initialX = getAbsoluteLeft();
		int width = getOffsetWidth();

		if (((initialX + width - 10) < cursorX && cursorX <= (initialX + width))
				&& ((initialY + height - 10) < cursorY && cursorY <= (initialY + height))) {
			return true;
		}
		return false;
	}
	
}
