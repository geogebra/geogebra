package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

/**
 * Class for the HTML5 &lt;article&gt; tag
 */
@TagName(ArticleElement.TAG)
public final class ArticleElement extends Element {
	/** tag name */
	static final String TAG = "article";

	/**
	 * @param element
	 *            Assert, that the given {@link Element} is compatible with this
	 *            class and automatically typecast it.
	 * @return cast element
	 */
	public static ArticleElement as(Element element) {
		if (element != null) {
			element.setTabIndex(10);
		}
		// assert element.getTagName().equalsIgnoreCase(TAG);
		//addNativeHandlers(element);
		return (ArticleElement) element;
	}

	/**
	 * Create new article element
	 */
	protected ArticleElement() {
		// needed for GWT
	}

	/**
	 * Clear the content of this element
	 */
	public void clear() {
		this.setInnerHTML("");
	}

	/**
	 * @param el
	 *            element
	 * @param app
	 *            app listening to focus (see {@link AppW#addFocusToApp()})
	 */
	public static native void addNativeFocusHandler(Element el, AppW app)/*-{
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
		return getStringDataParam("filename", "");
	}

	/**
	 * @return data-param-json (string encoded ZIP file stucture)
	 */
	public String getDataParamJSON() {
		return getStringDataParam("json", "");
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
	
	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enableCAS: whether CAS is enabled
	 */
	public boolean getDataParamEnableCAS(boolean def) {
		return getBoolDataParam("enableCAS", def);
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enable3D: whether 3D is enabled
	 */
	public boolean getDataParamEnable3D(boolean def) {
		return getBoolDataParam("enable3D", def);
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return data-param-enableGraphing: whether graphing, commands and vectors
	 *         are enabled
	 */
	public boolean getDataParamEnableGraphing(boolean def) {
		return getBoolDataParam("enableGraphing", def);
	}

	/**
	 * @return true, if there is data-param-enableGraphing attribute, and it is
	 *         not an empty string
	 */
	public boolean hasDataParamEnableGraphing() {
		return !"".equals(this.getAttribute("data-param-enableGraphing"));
	}

	/**
	 * @return rounding; consists of integer and suffix that determines whether
	 *         significant figures are used (s) and whether fractions are
	 *         prefered (r)
	 */
	public String getDataParamRounding() {
		return this.getAttribute("data-param-rounding");
	}

	/**
	 * @return the data-param-ggbbase64 article attribute as String if set else
	 *         empty String
	 */
	public String getDataParamBase64String() {
		return getStringDataParam("ggbbase64", "");
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showMenuBar (default: false)
	 */
	public boolean getDataParamShowMenuBar(boolean def) {
		return getBoolDataParam("showMenuBar", def) || getDataParamApp();
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return data-param-allowStylebar: whether to have stylebar; no effect
	 *         when menu is present
	 */
	public boolean getDataParamAllowStyleBar(boolean def) {
		return getBoolDataParam("allowStyleBar", def);
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showToolBar (default: false)
	 */
	public boolean getDataParamShowToolBar(boolean def) {
		if (getDataParamShowMenuBar(false) || getDataParamApp()) {
			return true;
		}
		return getBoolDataParam("showToolBar", def);
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return whether to show toolbar help (tooltips)
	 */
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
		return getStringDataParam("customToolBar", "");
	}

	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return the data-param-showAlgebraInput (default: true)
	 */
	public boolean getDataParamShowAlgebraInput(boolean def) {
		return getBoolDataParam("showAlgebraInput", def);
	}
	
	/**
	 * @param def
	 *            fallback if parameter is not set
	 * @return input position (top / bottom / AV)
	 */
	public InputPosition getAlgebraPosition(InputPosition def) {
		String pos = getStringDataParam("algebraInputPosition", "")
				.toLowerCase().trim();
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

	/**
	 * @return pixel distance from point that counts as hit, defaults to
	 *         {@link App#DEFAULT_THRESHOLD}
	 */
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
	 * @return the data-param-useBrowserForJS (default: false)
	 */
	public boolean getDataParamUseBrowserForJS() {
		return getBoolDataParam("useBrowserForJS", false);
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
		return getIntegerAttribute("width", 0);

	}

	private int getIntegerAttribute(String string, int fallback) {
		String val = this.getAttribute("data-param-" + string);
		if(val == null || val.isEmpty()){
			return fallback;
		}
		try{
			return (int) Math.round(Double.parseDouble(val));
		} catch (Exception e) {
			Log.warn("Invalid value of " + string + ":" + val);
		}
		return fallback;
	}

	/**
	 * @return integer value of the data-param-height, 0 if not present
	 */
	public int getDataParamHeight() {
		return getIntegerAttribute("height", 0);
	}

	/**
	 * @return wheter the applet should fit to screen
	 */
	public boolean getDataParamFitToScreen() {
		return getBoolDataParam("fittoscreen", false) || getDataParamApp();
	}

	/**
	 * @return border color (valid CSS color)
	 */
	public String getDataParamBorder() {
		return this.getAttribute("data-param-borderColor");
	}

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	public boolean getDataParamShowLogging() {
		return !"false".equals(getStringDataParam("showLogging", "false"))
				|| (Location.getParameter("GeoGebraDebug") != null);
	}

	public boolean isDebugGraphics() {
		return "graphics".equals(getStringDataParam("showLogging", "false"));
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
		var style = $wnd.getComputedStyle(this);
		return style && style.direction === "rtl";
	}-*/;

	private static native String getTransform(JavaScriptObject style) /*-{
		return style.transform || style.webkitTransform || style.MozTransform
				|| style.msTransform || style.oTransform || "";
	}-*/;



	private native double envScale(JavaScriptObject current, String type,
			boolean deep) /*-{
		var sx = 1;
		var sy = 1;

		do {
			var matrixRegex = /matrix\((-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+)\)/;
			var style;
			// https://bugzilla.mozilla.org/show_bug.cgi?id=548397
			if ($wnd.getComputedStyle) {
				style = $wnd.getComputedStyle(current);
			}
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
		} while (deep && current);
		return type === "x" ? sx : sy;
	}-*/;

	private double envScale(String type) {
		return envScale(this, type, true);
	}

	/**
	 * @return get CSS scale of parent element
	 */
	public double getParentScaleX() {
		return envScale(this.getParentElement(), "x", false);
	}

	/**
	 * Read scale value and cache it
	 * 
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

	/**
	 * Read cached scale value or compute it, do not cache it
	 * 
	 * @return the CSS scale attached to the article element
	 */
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

	/**
	 * @return whwether to use app mode (forces fit to screen and most UIs
	 *         visible)
	 */
	public boolean getDataParamApp() {
		return getBoolDataParam("app", false);
	}

	/**
	 * Running in screenshot generator mode allows some optimizations
	 * 
	 * @return whether we are running the applet as screenshot generator
	 */
	public boolean getDataParamScreenshotGenerator() {
		return getBoolDataParam("screenshotGenerator", false);
	}

	/**
	 * @return look and feel
	 */
	public String getDataParamLAF() {
		return getStringDataParam("laf", "");
	}

	/**
	 * @return whether focus prevented (use in multiple applets)
	 */
	public boolean preventFocus() {
		return getBoolDataParam("preventFocus", false);
	}

	/**
	 * @return client ID for API
	 */
	public String getDataClientID() {
		return getStringDataParam("clientid", "");
	}

	/**
	 * @return perspective
	 */
	public String getDataParamPerspective() {
		return getStringDataParam("perspective", "");
	}

	/**
	 * @return graphing, geometry or classic; defaults to classic
	 */
	public String getDataParamAppName() {
		return getStringDataParam("appName", "classic").toLowerCase(Locale.US);
	}

	/**
	 * @return data-param-scale: the parameter for CSS scaling
	 */
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
	 * @return whether focus prevented (use in multiple applets)
	 */
	public boolean getDataParamButtonShadows() {
		return getBoolDataParam("buttonShadows", false);
	}

	/**
	 * @return data-param-buttonRounding: the parameter for how rounded buttons
	 *         are (0-1)
	 */
	public double getDataParamButtonRounding() {
		String rounding = this.getAttribute("data-param-buttonRounding");
		if (rounding.length() < 1) {
			return 0.2;
		}
		double ret = 0.2;
		try {
			ret = Double.parseDouble(rounding);
		} catch (Throwable t) {
			Log.warn("Invalid buttonRounding");
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

	/**
	 * Remove cached scale values
	 */
	public void resetScale() {
		setAttribute("data-scalex", "" + envScale("x"));
		setAttribute("data-scaley", "" + envScale("y"));
	}

	/**
	 * @return data-param-prerelease: whether to use some beta features
	 * @param el element
	 */
	public static boolean getDataParamFitToScreen(Element el) {
		return "true"
				.equals(el.getAttribute("data-param-app").trim().toLowerCase())
				|| "true".equals(el.getAttribute("data-param-fittoscreen")
						.trim().toLowerCase());
	}

	/**
	 * @return data-param-prerelease: whether to use some beta features
	 */
	public String getDataParamPrerelease() {
		return getAttribute("data-param-prerelease").trim().toLowerCase();
	}

	/**
	 * @return material ID (or sharing code) to open on startup
	 */
	public String getDataParamTubeID() {
		return getAttribute("data-param-tubeid");
	}

	/**
	 * @param def
	 *            fallback if parameter not set
	 * @return whether to show "welcome" tooltip
	 */
	public boolean getDataParamShowStartTooltip(boolean def) {
		return getBoolDataParam("showTutorialLink", def);
	}

	/**
	 * @return whether to enable file menu
	 */
	public boolean getDataParamEnableFileFeatures() {
		return getBoolDataParam("enableFileFeatures", true);
	}

	/**
	 * @return list of articles on the page that have the proper class (
	 *         {@value GeoGebraConstants#GGM_CLASS_NAME})
	 */
	public static ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			ArticleElement ae = ArticleElement.as(nodes.getItem(i));
			ae.initID(i);
			articleNodes.add(ae);
		}
		return articleNodes;
	}

	/**
	 * Set the ID of this article to something unique; prefer getDataParamId,
	 * append number in case of conflicts. If not set, use a string that
	 * contains i
	 * 
	 * @param i
	 *            number for id if fdataParamId not set
	 */
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

	/**
	 * @return whether error dialogs should be active, defaults to true
	 */
	public boolean getDataParamErrorDialogsActive() {
		return getBoolDataParam("errorDialogsActive", true);
	}

	/**
	 * @return whether startups stats are enabled
	 */
	public static boolean isEnableUsageStats() {
		return ((CASFactory) GWT.create(CASFactory.class)).isEnabled();
	}

	/**
	 * @return URL of materials plaftform API (empty string if not set)
	 */
	public String getMaterialsAPIurl() {
		return this.getAttribute("data-param-materialsApi");
	}

	/**
	 * @return URL of materials plaftform API (empty string if not set)
	 */
	public String getLoginAPIurl() {
		return this.getAttribute("data-param-loginApi");
	}

	/**
	 * @return whether to allow apps picker (for classic)
	 */
	public boolean getDataParamShowAppsPicker() {
		return getBoolDataParam("showAppsPicker", false);
	}

	/**
	 * @return total thickness of borders (left + right = top + bottom)
	 */
	public int getBorderThickness() {
		return getDataParamFitToScreen() ? 0 : 2;
	}

	/**
	 * @return whether to show zoom buttons, defaults to false
	 */
	public boolean getDataParamShowZoomButtons() {
		return getBoolDataParam("showZoomButtons", false);
	}

	/**
	 * @return whether to show fullscreen button, defaults to false
	 */
	public boolean getDataParamShowFullscreenButton() {
		return getBoolDataParam("showFullscreenButton", false);
	}

	/**
	 * @return whether suggestions buttons should be shown; default true if not
	 *         set
	 */
	public boolean getDataParamShowSuggestionButtons() {
		return getBoolDataParam("showSuggestionButtons", true);
	}

	private boolean getBoolDataParam(String string, boolean def) {
		String attr = "data-param-" + string;
		return (def && !"false".equals(this.getAttribute(attr)))
				|| "true".equals(this.getAttribute(attr));
	}

	private String getStringDataParam(String string, String def) {
		String attr = "data-param-" + string;
		return "".equals(this.getAttribute(attr)) ? def
				: this.getAttribute(attr);
	}

	/**
	 * @return how much space should be left above the applet in fit-to-screen
	 *         mode
	 */
	public int getDataParamMarginTop() {
		return this.getIntegerAttribute("marginTop", 0);
	}

	/**
	 * @return height based on height and fitToScreen parameters
	 */
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

	public String getDataParamFontsCssUrl() {
		return getStringDataParam("fontscssurl", "");
	}

}
