package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TagName;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Widget;

@TagName(ArticleElement.TAG)
public final class ArticleElement extends Element {

	static final String TAG = "article";

	/**
	 * @param element
	 *            Assert, that the given {@link Element} is compatible with this
	 *            class and automatically typecast it.
	 * @return
	 */
	public static ArticleElement as(Element element) {
		if (element != null) {
			element.setTabIndex(10);
		}
		// assert element.getTagName().equalsIgnoreCase(TAG);
		//addNativeHandlers(element);
		return (ArticleElement) element;
	}

	protected ArticleElement() {
	}

	public void add(Widget w) {
		this.appendChild(w.getElement());
	}

	public void clear() {
		this.setInnerHTML("");
	}

	public boolean remove(Widget w) {
		for (int i = 0; i < this.getChildCount(); i++) {
			if (this.getChild(i).equals(w.getElement())) {
				this.removeChild(this.getChild(i));
				return true;
			}
		}
		return false;
	}

	public static native void addNativeHandlers(Element el, AppW app)/*-{
		el.onfocus = function(event) {
			app.@org.geogebra.web.html5.main.AppW::addFocusToApp()();
		}
	}-*/;



	/**
	 * @return the data-param-id article attribute as String if set else
	 *         AppWeb.DEFAULT_APPLET_ID
	 */
	public String getDataParamId() {
		String ret = this.getAttribute("data-param-id");
		if (("".equals(ret)) || !ret.matches("[A-Za-z0-9_]+")) {
			return AppW.DEFAULT_APPLET_ID;
		}
		return ret;
	}

	/**
	 * @return the data-param-filename article attribute as String if set else
	 *         empty String
	 */
	public String getDataParamFileName() {
		String ret = this.getAttribute("data-param-filename");
		return (ret != null) ? ret : "";
	}

	public String getDataParamJSON() {
		String ret = this.getAttribute("data-param-json");
		return (ret != null) ? ret : "";
	}

	/**
	 * Determines if the "data-param-enableLabelDrags" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableLabelDrags (default: true)
	 */
	public boolean getDataParamEnableLabelDrags() {
		return getBoolDataParam("enableLabelDrags", true);
	}

	/**
	 * Determines if the "data-param-enableUndoRedo" article attribute is set to
	 * true
	 * 
	 * @return the data-param-enableUndoRedo (default: true)
	 */
	public boolean getDataParamEnableUndoRedo() {
		return getBoolDataParam("enableUndoRedo", true);
	}

	/**
	 * Determines if the "data-param-enableRightClick" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableRightClick (default: true)
	 */
	public boolean getDataParamEnableRightClick() {
		return getBoolDataParam("enableRightClick", true);
	}
	
	public boolean getDataParamEnableCAS(boolean def) {
		return getBoolDataParam("enableCAS", def);
	}

	public boolean getDataParamEnable3D(boolean def) {
		return getBoolDataParam("enable3D", def);
	}

	public boolean getDataParamEnableGraphing(boolean def) {
		return getBoolDataParam("enableGraphing", def);
	}

	/*
	 * Returns true, if there is data-param-enableGraphing attribute, and it is
	 * not an empty string
	 */
	public boolean hasDataParamEnableGraphing() {
		return !"".equals(this.getAttribute("data-param-enableGraphing"));
	}

	public String getDataParamRounding() {
		return this.getAttribute("data-param-rounding");
	}

	/**
	 * @return the data-param-ggbbase64 article attribute as String if set else
	 *         empty String
	 */
	public String getDataParamBase64String() {
		String ret = this.getAttribute("data-param-ggbbase64");
		return (ret != null) ? ret : "";
	}

	/**
	 * @return the data-param-showMenuBar (default: false)
	 */
	public boolean getDataParamShowMenuBar(boolean def) {
		return getBoolDataParam("showMenuBar", def) || getDataParamApp();
	}

	public boolean getDataParamShowMenuBar2(boolean def) {
		return getBoolDataParam("showMenuBar", def);
	}
	public boolean getDataParamAllowStyleBar(boolean def) {
		return getBoolDataParam("allowStyleBar", def);
	}

	/**
	 * @return the data-param-showToolBar (default: false)
	 */
	public boolean getDataParamShowToolBar(boolean def) {
		if (getDataParamShowMenuBar(false) || getDataParamApp()) {
			return true;
		}
		return getBoolDataParam("showToolBar", def);
	}

	public boolean getDataParamShowToolBarHelp(boolean def) {
		if (!getDataParamShowToolBar(false) && !getDataParamApp()) {
			return false;
		}
		return getBoolDataParam("showToolBarHelp", def);
	}

	/**
	 * 
	 * @return the data-param-customToolBar (default: null)
	 */
	public String getDataParamCustomToolBar() {
		return this.getAttribute("data-param-customToolBar");
	}

	/**
	 * @return the data-param-showAlgebraInput (default: true)
	 */
	public boolean getDataParamShowAlgebraInput(boolean def) {
		return getBoolDataParam("showAlgebraInput", def);
	}
	
	public InputPosition getAlgebraPosition(InputPosition def) {
		String pos = this.getAttribute("data-param-algebraInputPosition").toLowerCase().trim();
		if("top".equals(pos)){
			return InputPosition.top;
		}
		if("bottom".equals(pos)){
			return InputPosition.bottom;
		}
		if(pos.length() > 0){
			return InputPosition.algebraView;
		}
		return def;
	}

	/**
	 * @return the data-param-showResetIcon (default: false)
	 */
	public boolean getDataParamShowResetIcon() {
		return getBoolDataParam("showResetIcon", false);
	}

	/**
	 * @return the data-param-showAnimationButton (default: true)
	 */
	public boolean getDataParamShowAnimationButton() {
		return getBoolDataParam("showAnimationButton", true);
	}

	public int getDataParamCapturingThreshold() {
		int threshold = App.DEFAULT_THRESHOLD;
		if ("".equals(this.getAttribute("data-param-capturingThreshold"))) {
			return threshold;
		}
		try {
			threshold = Integer.parseInt(this
			        .getAttribute("data-param-capturingThreshold"));
		} catch (Throwable t) {
			Log.error("Invalid capturing threshold: "
			        + this.getAttribute("data-param-capturingThreshold"));
		}
		return threshold;
	}

	/**
	 * eg "de"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	public String getDataParamLanguage() {
		return this.getAttribute("data-param-language");
	}

	/**
	 * 
	 * eg "AT"
	 * 
	 * @return the data-param-showResetIcon (default: null)
	 */
	public String getDataParamCountry() {
		return this.getAttribute("data-param-country");
	}

	/**
	 * 
	 * @return the data-param-allowJSscripting (default: true)
	 */
	public boolean getDataParamUseBrowserForJS() {
		return getBoolDataParam("useBrowserForJS", true);
	}

	/**
	 * @return the data-param-enableShiftDragZoom (default: true)
	 */
	public boolean getDataParamShiftDragZoomEnabled() {
		return getBoolDataParam("enableShiftDragZoom", true);
	}

	/**
	 * @return integer value of the data-param-width, 0 if not present
	 */
	public int getDataParamWidth() {
		return getIntegerAttribute("data-param-width", 0);

	}

	private int getIntegerAttribute(String string, int fallback) {
		String val = this.getAttribute(string);
		if(val == null || val.isEmpty()){
			return fallback;
		}
		try{
			return Integer.parseInt(val, 10);
		} catch (Exception e) {
			Log.warn("Invalid value of " + string + ":" + val);
		}
		return fallback;
	}

	/**
	 * @return integer value of the data-param-height, 0 if not present
	 */
	public int getDataParamHeight() {
		return getIntegerAttribute("data-param-height", 0);
	}

	/**
	 * @return wheter the applet should fit to screen
	 */
	public boolean getDataParamFitToScreen() {
		return getBoolDataParam("fittoscreen", false) || getDataParamApp();
	}

	public String getDataParamBorder() {
		return this.getAttribute("data-param-borderColor");
	}

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	public boolean getDataParamShowLogging() {
		return getBoolDataParam("showLogging", false)
				|| (Location.getParameter("GeoGebraDebug") != null);
	}

	/**
	 * @return the data-param-allowSymbolTable (default: true)
	 */
	public boolean getDataParamAllowSymbolTable() {
		return getBoolDataParam("allowSymbolTable", true);
	}

	/**
	 * 
	 * @return that the article element has (inherited) direction attribute
	 */
	public native boolean isRTL() /*-{
		var style;
		if ($wnd.getComputedStyle) {
			style = $wnd.getComputedStyle(this);
			return style.direction === "rtl";
		} else if (this.currentStyle) {
			return this.currentStyle.direction === "rtl";
		}
		return false;
	}-*/;

	private static native String getTransform(JavaScriptObject style) /*-{
		return style.transform || style.webkitTransform || style.MozTransform
				|| style.msTransform || style.oTransform || "";
	}-*/;



	private native double envScale(String type) /*-{
		var current = this;
		var sx = 1;
		var sy = 1;

		do {
			var matrixRegex = /matrix\((-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+)\)/, style = $wnd
					.getComputedStyle(current);
			if (style) {
				var transform = style.transform || style.webkitTransform
						|| style.MozTransform || style.msTransform
						|| style.oTransform || "";
				var matches = transform.match(matrixRegex);
				if (matches && matches.length) {

					sx *= $wnd.parseFloat(matches[1]);
					sy *= $wnd.parseFloat(matches[4]);
				} else if (transform.indexOf("scale") === 0) {
					var mul = $wnd.parseFloat(transform.substr(transform
							.indexOf("(") + 1));
					sx *= mul;
					sy *= mul;
				}
			}

			current = current.parentElement;
		} while (current);
		return type === "x" ? sx : sy;
	}-*/;

	/**
	 * @return the CSS scale attached to the article element
	 */
	public double getScaleX() {
		// no instance fields in subclasses of Element, so no way to assign it
		// to
		// a simple field
		if ("".equals(this.getAttribute("data-scalex"))) {
			this.setAttribute("data-scalex", String.valueOf(envScale("x")));
		}
		return Double.parseDouble(this.getAttribute("data-scalex"));
	}

	public double readScaleX() {
		if ("".equals(this.getAttribute("data-scalex"))) {
			return envScale("x");
		}
		return Double.parseDouble(this.getAttribute("data-scalex"));
	}

	/**
	 * @return the CSS scale attached to the article element
	 * 
	 */
	public double getScaleY() {
		// no instance fields in subclasses of Element, so no way to asign it to
		// a simple field
		if ("".equals(this.getAttribute("data-scaley"))) {
			this.setAttribute("data-scaley", String.valueOf(envScale("y")));
		}
		return Double.parseDouble(this.getAttribute("data-scaley"));
	}



	/**
	 * @return default false
	 */
	public boolean getDataParamAllowStyleBar() {
		return getBoolDataParam("allowStyleBar", false);
	}

	public boolean getDataParamApp() {
		return getBoolDataParam("app", false);
	}

	public boolean getDataParamScreenshotGenerator() {
		return getBoolDataParam("screenshotGenerator", false);
	}

	public String getDataParamLAF() {
		return this.getAttribute("data-param-laf");
	}

	/**
	 * @return wheter focus prevented (use in multiple applets)
	 */
	public boolean preventFocus() {
		return getBoolDataParam("preventFocus", false);
	}

	public String getDataClientID() {
		return this.getAttribute("data-param-clientid");
	}

	public String getDataParamPerspective() {
		String ret = this.getAttribute("data-param-perspective");
		return ret == null ? "" : ret;
	}

	public String getDataParamAppName() {
		String ret = this.getAttribute("data-param-appname");
		return ret == null || ret.length() < 1 ? "classic" : ret;
	}

	public double getDataParamScale() {
		String scale = this.getAttribute("data-param-scale");
		if (scale.length() < 1) {
			return 1;
		}
		double ret = 1;
		try {
			ret = Double.parseDouble(scale);
		} catch (Throwable t) {
			Log.warn("Invalid scale");
		}
		return ret;

	}

	/**
	 * Sync data-scale params with external environment
	 * 
	 */
	public void adjustScale() {
		if (getDataParamApp()
		        || (getAttribute("data-scalex") != null && !""
		                .equals(getAttribute("data-scalex")))) {
			return;
		}
		double externalScale = getDataParamScale();
		Element parent = this.getParentElement();
		if (parent.getParentElement() != null
		        && "applet_container".equals(parent.getParentElement().getId())) {
			parent = parent.getParentElement();
		}
		Browser.scale(parent, externalScale, 0, 0);
		resetScale();

	}

	public void resetScale() {
		setAttribute("data-scalex", "" + envScale("x"));
		setAttribute("data-scaley", "" + envScale("y"));
	}

	public String getDataParamPrerelease() {
		return getAttribute("data-param-prerelease").trim().toLowerCase();
	}

	public String getDataParamTubeID() {
		return getAttribute("data-param-tubeid");
	}
	public boolean getDataParamShowStartTooltip(boolean def) {
		return getBoolDataParam("showTutorialLink", def);
	}

	/**
	 * @return whether to enable file menu
	 */
	public boolean getDataParamEnableFileFeatures() {
		return getBoolDataParam("enableFileFeatures", true);
	}

	public static ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			ArticleElement ae = ArticleElement.as(nodes.getItem(i));
			ae.initID(i);
			articleNodes.add(ae);
		}
		return articleNodes;
	}

	public void initID(int i) {
		String paramID = getDataParamId();
		if (paramID.equals(getId())) {
			return;
		}
		if (paramID.length() > 0) {
			int suffix = 0;
			while (DOM.getElementById(paramID) != null) {
				paramID = getDataParamId() + suffix;
				suffix++;
			}
			setId(paramID);
			return;
		}
		Date creationDate = new Date();
		setId(GeoGebraConstants.GGM_CLASS_NAME + i + creationDate.getTime());

	}

	public boolean getDataParamErrorDialogsActive() {
		return getBoolDataParam("errorDialogsActive", true);
	}

	public static boolean isEnableUsageStats() {
		return ((CASFactory) GWT.create(CASFactory.class)).isEnabled();
	}

	public String getMaterialsAPIurl() {
		return this.getAttribute("data-param-materialsApi");
	}

	public String getLoginAPIurl() {
		return this.getAttribute("data-param-loginApi");
	}

	public boolean getDataParamShowAppsPicker() {
		return getBoolDataParam("showAppsPicker", false);
	}

	public int getBorderThickness() {
		return getDataParamFitToScreen() ? 0 : 2;
	}

	public boolean getDataParamShowZoomButtons() {
		return getBoolDataParam("showZoomButtons", false);
	}

	public boolean getDataParamShowFullscreenButton() {
		return getBoolDataParam("showFullscreenButton", false);
	}

	private boolean getBoolDataParam(String string, boolean def) {
		String attr = "data-param-" + string;
		return (def && !"false".equals(this.getAttribute(attr)))
				|| "true".equals(this.getAttribute(attr));
	}

	public int getDataParamMarginTop() {
		return this.getIntegerAttribute("data-param-marginTop", 0);
	}

	public int computeHeight() {
		// do we have data-param-height?
		int height = getDataParamHeight() - getBorderThickness();

		// do we have fit to screen?

		if (getDataParamFitToScreen()) {
			int margin = AppW.smallScreen() ? 0 : getDataParamMarginTop();
			height = Window.getClientHeight() - margin;
		}

		if (height > 0) {
			return height;
		}

		return height;
	}

}
