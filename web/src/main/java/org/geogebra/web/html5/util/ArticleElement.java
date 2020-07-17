package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TagName;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window.Location;

/**
 * Class for the HTML5 &lt;article&gt; tag
 */
@TagName(ArticleElement.TAG)
public final class ArticleElement extends Element implements ArticleElementInterface {
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
			element.setTabIndex(0);
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#clear()
	 */
	@Override
	public void clear() {
		this.setInnerHTML("");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamId()
	 */
	@Override
	public String getDataParamId() {
		String ret = this.getAttribute("data-param-id");
		if (("".equals(ret)) || !ret.matches("[A-Za-z0-9_]+")) {
			return DEFAULT_APPLET_ID;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamFileName()
	 */
	@Override
	public String getDataParamFileName() {
		return getStringDataParam("filename", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamJSON()
	 */
	@Override
	public String getDataParamJSON() {
		return getStringDataParam("json", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnableLabelDrags()
	 */
	@Override
	public boolean getDataParamEnableLabelDrags() {
		return getBoolDataParam("enableLabelDrags", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnableUndoRedo()
	 */
	@Override
	public boolean getDataParamEnableUndoRedo() {
		return getBoolDataParam("enableUndoRedo", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnableRightClick()
	 */
	@Override
	public boolean getDataParamEnableRightClick() {
		return getBoolDataParam("enableRightClick", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnableCAS(boolean)
	 */
	@Override
	public boolean getDataParamEnableCAS(boolean def) {
		return getBoolDataParam("enableCAS", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnable3D(boolean)
	 */
	@Override
	public boolean getDataParamEnable3D(boolean def) {
		return getBoolDataParam("enable3D", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnableGraphing(boolean)
	 */
	@Override
	public boolean getDataParamEnableGraphing(boolean def) {
		return getBoolDataParam("enableGraphing", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#hasDataParamEnableGraphing()
	 */
	@Override
	public boolean hasDataParamEnableGraphing() {
		return !"".equals(this.getAttribute("data-param-enableGraphing"));
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamRounding()
	 */
	@Override
	public String getDataParamRounding() {
		return this.getAttribute("data-param-rounding");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamBase64String()
	 */
	@Override
	public String getDataParamBase64String() {
		return getStringDataParam("ggbbase64", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowMenuBar(boolean)
	 */
	@Override
	public boolean getDataParamShowMenuBar(boolean def) {
		return getBoolDataParam("showMenuBar", def) || getDataParamApp();
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamAllowStyleBar(boolean)
	 */
	@Override
	public boolean getDataParamAllowStyleBar(boolean def) {
		return getBoolDataParam("allowStyleBar", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowToolBar(boolean)
	 */
	@Override
	public boolean getDataParamShowToolBar(boolean def) {
		if (getDataParamShowMenuBar(false) || getDataParamApp()) {
			return true;
		}
		return getBoolDataParam("showToolBar", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowToolBarHelp(boolean)
	 */
	@Override
	public boolean getDataParamShowToolBarHelp(boolean def) {
		if (!getDataParamShowToolBar(false) && !getDataParamApp()) {
			return false;
		}
		return getBoolDataParam("showToolBarHelp", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamCustomToolBar()
	 */
	@Override
	public String getDataParamCustomToolBar() {
		return getStringDataParam("customToolBar", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowAlgebraInput(boolean)
	 */
	@Override
	public boolean getDataParamShowAlgebraInput(boolean def) {
		return getBoolDataParam("showAlgebraInput", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getAlgebraPosition(org.geogebra.common.main.App.InputPosition)
	 */
	@Override
	public InputPosition getAlgebraPosition(InputPosition def) {
		String pos = getStringDataParam("algebraInputPosition", "")
				.toLowerCase().trim();
		if ("top".equals(pos)) {
			return InputPosition.top;
		}
		if ("bottom".equals(pos)) {
			return InputPosition.bottom;
		}
		if (pos.length() > 0) {
			return InputPosition.algebraView;
		}
		return def;
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowResetIcon()
	 */
	@Override
	public boolean getDataParamShowResetIcon() {
		return getBoolDataParam("showResetIcon", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowAnimationButton()
	 */
	@Override
	public boolean getDataParamShowAnimationButton() {
		return getBoolDataParam("showAnimationButton", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamCapturingThreshold()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamLanguage()
	 */
	@Override
	public String getDataParamLanguage() {
		return this.getAttribute("data-param-language");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamCountry()
	 */
	@Override
	public String getDataParamCountry() {
		return this.getAttribute("data-param-country");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamUseBrowserForJS()
	 */
	@Override
	public boolean getDataParamUseBrowserForJS() {
		return getBoolDataParam("useBrowserForJS", false);
	}

	@Override
	public String[] getDataParamPreloadModules() {
		if (!hasAttribute("data-param-preloadModules")) {
			return null;
		}

		return getAttribute("data-param-preloadModules").split(",");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShiftDragZoomEnabled()
	 */
	@Override
	public boolean getDataParamShiftDragZoomEnabled() {
		return getBoolDataParam("enableShiftDragZoom", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamWidth()
	 */
	@Override
	public int getDataParamWidth() {
		return getIntegerAttribute("width", 0);
	}

	private int getIntegerAttribute(String string, int fallback) {
		String val = this.getAttribute("data-param-" + string);
		if (val == null || val.isEmpty()) {
			return fallback;
		}
		try {
			return (int) Math.round(Double.parseDouble(val));
		} catch (Exception e) {
			Log.warn("Invalid value of " + string + ":" + val);
		}
		return fallback;
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamHeight()
	 */
	@Override
	public int getDataParamHeight() {
		return getIntegerAttribute("height", 0);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamFitToScreen()
	 */
	@Override
	public boolean getDataParamFitToScreen() {
		return getBoolDataParam("fittoscreen", false) || getDataParamApp();
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamBorder()
	 */
	@Override
	public String getDataParamBorder() {
		return this.getAttribute("data-param-borderColor");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowLogging()
	 */
	@Override
	public boolean getDataParamShowLogging() {
		return !"false".equals(getStringDataParam("showLogging", "false"))
				|| (Location.getParameter("GeoGebraDebug") != null);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#isDebugGraphics()
	 */
	@Override
	public boolean isDebugGraphics() {
		return "graphics".equals(getStringDataParam("showLogging", "false"));
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamAllowSymbolTable()
	 */
	@Override
	public boolean getDataParamAllowSymbolTable() {
		return getBoolDataParam("allowSymbolTable", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#isRTL()
	 */
	@Override
	public native boolean isRTL() /*-{
		// https://bugzilla.mozilla.org/show_bug.cgi?id=548397
		if (!$wnd.getComputedStyle) {
			return false;
		}

		var style = $wnd.getComputedStyle(this);
		return style && style.direction === "rtl";
	}-*/;

	private native double envScale(JavaScriptObject current, String type,
			boolean deep) /*-{
		var sx = 1;
		var sy = 1;

		do {
			var matrixRegex = /matrix\((-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+),\s*(-?\d*\.?\d+)\)/;
			var style;
			// https://bugzilla.mozilla.org/show_bug.cgi?id=548397
			if ($wnd.getComputedStyle && current) {
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
				if (style.zoom && current != $doc.body.parentElement) {
					sx *= style.zoom;
					sy *= style.zoom;
				}
			}

			current = current.parentElement;
		} while (deep && current);
		return type === "x" ? sx : sy;
	}-*/;

	private double envScale(String type) {
		return envScale(this, type, true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getParentScaleX()
	 */
	@Override
	public double getParentScaleX() {
		return envScale(this.getParentElement(), "x", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getScaleX()
	 */
	@Override
	public double getScaleX() {
		// no instance fields in subclasses of Element, so no way to assign it
		// to
		// a simple field
		if ("".equals(this.getAttribute("data-scalex"))) {
			this.setAttribute("data-scalex", String.valueOf(envScale("x")));
		}
		return Double.parseDouble(this.getAttribute("data-scalex"));
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#readScaleX()
	 */
	@Override
	public double readScaleX() {
		if ("".equals(this.getAttribute("data-scalex"))) {
			return envScale("x");
		}
		return Double.parseDouble(this.getAttribute("data-scalex"));
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getScaleY()
	 */
	@Override
	public double getScaleY() {
		// no instance fields in subclasses of Element, so no way to asign it to
		// a simple field
		if ("".equals(this.getAttribute("data-scaley"))) {
			this.setAttribute("data-scaley", String.valueOf(envScale("y")));
		}
		return Double.parseDouble(this.getAttribute("data-scaley"));
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamAllowStyleBar()
	 */
	@Override
	public boolean getDataParamAllowStyleBar() {
		return getBoolDataParam("allowStyleBar", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamApp()
	 */
	@Override
	public boolean getDataParamApp() {
		return getBoolDataParam("app", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamScreenshotGenerator()
	 */
	@Override
	public boolean getDataParamScreenshotGenerator() {
		return getBoolDataParam("screenshotGenerator", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamLAF()
	 */
	@Override
	public String getDataParamLAF() {
		return getStringDataParam("laf", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#preventFocus()
	 */
	@Override
	public boolean preventFocus() {
		return getBoolDataParam("preventFocus", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataClientID()
	 */
	@Override
	public String getDataClientID() {
		return getStringDataParam("clientid", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamPerspective()
	 */
	@Override
	public String getDataParamPerspective() {
		return getStringDataParam("perspective", "");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamAppName()
	 */
	@Override
	public String getDataParamAppName() {
		return getStringDataParam("appName", "classic")
				.replace("whiteboard", "notes").toLowerCase(Locale.US);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamScale()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamButtonShadows()
	 */
	@Override
	public boolean getDataParamButtonShadows() {
		return getBoolDataParam("buttonShadows", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamButtonRounding()
	 */
	@Override
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

	@Override
	public String getDataParamButtonBorderColor() {
		return getStringDataParam("buttonBorderColor", null);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#resetScale()
	 */
	@Override
	public void resetScale(double parentScale) {
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamPrerelease()
	 */
	@Override
	public boolean getDataParamPrerelease() {
		return getBoolDataParam("prerelease", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamTubeID()
	 */
	@Override
	public String getDataParamTubeID() {
		return getAttribute("data-param-tubeid");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowStartTooltip(boolean)
	 */
	@Override
	public boolean getDataParamShowStartTooltip(boolean def) {
		return getBoolDataParam("showTutorialLink", def);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamEnableFileFeatures()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#initID(int)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamErrorDialogsActive()
	 */
	@Override
	public boolean getDataParamErrorDialogsActive() {
		return getBoolDataParam("errorDialogsActive", true);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getMaterialsAPIurl()
	 */
	@Override
	public String getMaterialsAPIurl() {
		return this.getAttribute("data-param-materialsApi");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getLoginAPIurl()
	 */
	@Override
	public String getLoginAPIurl() {
		return this.getAttribute("data-param-loginApi");
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowAppsPicker()
	 */
	@Override
	public boolean getDataParamShowAppsPicker() {
		return getBoolDataParam("showAppsPicker", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getBorderThickness()
	 */
	@Override
	public int getBorderThickness() {
		return getDataParamFitToScreen() ? 0 : 2;
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowZoomButtons()
	 */
	@Override
	public boolean getDataParamShowZoomButtons() {
		return getBoolDataParam("showZoomButtons", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowFullscreenButton()
	 */
	@Override
	public boolean getDataParamShowFullscreenButton() {
		return getBoolDataParam("showFullscreenButton", false);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamShowSuggestionButtons()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamMarginTop()
	 */
	@Override
	public int getDataParamMarginTop() {
		return this.getIntegerAttribute("marginTop", 0);
	}

	/* (non-Javadoc)
	 * @see org.geogebra.web.html5.util.ArticleElementInterface#getDataParamFontsCssUrl()
	 */
	@Override
	public String getDataParamFontsCssUrl() {
		return getStringDataParam("fontscssurl", "");
	}

	@Override
	public ArticleElement getElement() {
		return this;
	}

	@Override
	public String getParamScaleContainerClass() {
		return getStringDataParam("scaleContainerClass", "");
	}

	@Override
	public boolean getParamAllowUpscale() {
		return getBoolDataParam("allowUpscale", false);
	}

	@Override
	public boolean getParamAutoHeight() {
		return getBoolDataParam("autoHeight", false);
	}

	@Override
	public boolean getParamDisableAutoScale() {
		return getBoolDataParam("disableAutoScale", false);
	}

	@Override
	public int getParamRandomSeed() {
		return getIntegerAttribute("randomSeed", -1);
	}

	@Override
	public ArticleElementInterface attr(String attributeName, String value) {
		setAttribute("data-param-" + attributeName, value);
		return this;
	}

	@Override
	public String getParamLoginURL() {
		return getStringDataParam("loginURL", "");
	}

	@Override
	public String getParamLogoutURL() {
		return getStringDataParam("logoutURL", "");
	}

	@Override
	public String getParamBackendURL() {
		return getStringDataParam("backendURL", "");
	}

	@Override
	public String getParamFullscreenContainer() {
		return getStringDataParam("fullscreenContainer", "");
	}

	@Override
	public String getParamShareLinkPrefix() {
		return getStringDataParam("shareLinkPrefix", getDataParamAppName());
	}

	@Override
	public String getParamVendor() {
		return getStringDataParam("vendor", "");
	}

	@Override
	public int getParamFontSize(int def) {
		return getIntegerAttribute("fontSize", def);
	}

	@Override
	public String getParamKeyboardType(String def) {
		return getStringDataParam("keyboardType", def);
	}

	@Override
	public boolean getParamTextMode() {
		return getBoolDataParam("textMode", false);
	}
}
