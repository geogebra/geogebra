package org.geogebra.web.html5.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TagName;
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
		// assert element.getTagName().equalsIgnoreCase(TAG);
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

	/**
	 * @return the data-param-id article attribute as String if set else
	 *         AppWeb.DEFAULT_APPLET_ID
	 */
	public String getDataParamId() {
		String ret = this.getAttribute("data-param-id");
		if ((ret == "") || !ret.matches("[A-Za-z0-9_]+"))
			return AppW.DEFAULT_APPLET_ID;
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
	 * Determines if the "data-param-guiOff" article attribute is set to true If
	 * it is set to true, the GuiManager should never be created, and only a
	 * single Graphics View should show in web applets, regardless of what is
	 * there in the ggb file's construction. This is for speedup, but its
	 * drawback is that it should be decided before the AppW is created.
	 * 
	 * @return the data-param-guiOff (default: false)
	 */
	public boolean getDataParamGuiOff() {
		return ("true".equals(this.getAttribute("data-param-guiOff")));
	}

	/**
	 * Determines if the "data-param-enableLabelDrags" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableLabelDrags (default: true)
	 */
	public boolean getDataParamEnableLabelDrags() {
		return (!"false".equals(this
		        .getAttribute("data-param-enableLabelDrags")));
	}

	/**
	 * Determines if the "data-param-enableRightClick" article attribute is set
	 * to true
	 * 
	 * @return the data-param-enableRightClick (default: true)
	 */
	public boolean getDataParamEnableRightClick() {
		return (!"false".equals(this
		        .getAttribute("data-param-enableRightClick")));
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
		return getBoolParam("data-param-showMenuBar", def);
	}

	public boolean getDataParamAllowStyleBar(boolean def) {
		return getBoolParam("data-param-allowStyleBar", def);
	}

	/**
	 * @return the data-param-showToolBar (default: false)
	 */
	public boolean getDataParamShowToolBar(boolean def) {
		if (getDataParamShowMenuBar(false)) {
			return true;
		}
		return getBoolParam("data-param-showToolBar", def);
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
		return getBoolParam("data-param-showAlgebraInput", def);
	}
	
	public InputPositon getAlgebraPosition(InputPositon def) {
		String pos = this.getAttribute("data-param-algebraInputPosition").toLowerCase().trim();
		if("top".equals(pos)){
			return InputPositon.top;
		}
		if("bottom".equals(pos)){
			return InputPositon.bottom;
		}
		if(pos.length() > 0){
			return InputPositon.algebraView;
		}
		return def;
	}

	private boolean getBoolParam(String attr, boolean def) {
		return (def && !"false".equals(this.getAttribute(attr)))
		        || "true".equals(this.getAttribute(attr));
	}

	/**
	 * @return the data-param-showResetIcon (default: false)
	 */
	public boolean getDataParamShowResetIcon() {
		return ("true".equals(this.getAttribute("data-param-showResetIcon")));
	}

	/**
	 * @return the data-param-showAnimationButton (default: true)
	 */
	public boolean getDataParamShowAnimationButton() {
		return (!"false".equals(this
		        .getAttribute("data-param-showAnimationButton")));
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
		return (!"false"
		        .equals(this.getAttribute("data-param-useBrowserForJS")));
	}

	/**
	 * @return the data-param-enableShiftDragZoom (default: true)
	 */
	public boolean getDataParamShiftDragZoomEnabled() {
		return (!"false".equals(this
		        .getAttribute("data-param-enableShiftDragZoom")));
	}

	/**
	 * @return integer value of the data-param-width, 0 if not present
	 */
	public int getDataParamWidth() {
		String width = this.getAttribute("data-param-width");
		return (width != null && !width.equals("")) ? Integer.parseInt(width,
		        10) : 0;
	}

	/**
	 * @return integer value of the data-param-height, 0 if not present
	 */
	public int getDataParamHeight() {
		String height = this.getAttribute("data-param-height");
		return (height != null && !height.equals("")) ? Integer.parseInt(
		        height, 10) : 0;
	}

	/**
	 * @return integer value of the data-param-minwidth, 0 if not present
	 */
	public int getDataParamMinWidth() {
		String width = this.getAttribute("data-param-minwidth");
		return (width != null && !width.equals("")) ? Integer.parseInt(width,
		        10) : 0;
	}

	/**
	 * @return integer value of the data-param-minheight, 0 if not present
	 */
	public int getDataParamMinHeight() {
		String height = this.getAttribute("data-param-minheight");
		return (height != null && !height.equals("")) ? Integer.parseInt(
		        height, 10) : 0;
	}

	/**
	 * @return integer value of the data-param-maxwidth, 0 if not present
	 */
	public int getDataParamMaxWidth() {
		String width = this.getAttribute("data-param-maxwidth");
		return (width != null && !width.equals("")) ? Integer.parseInt(width,
		        10) : 0;
	}

	/**
	 * @return the array containing the minwidth and minheight as integers
	 */
	public int[] getDataParamMinDimensions() {
		String minDimensions = this.getAttribute("data-param-mindimensions");
		int[] result = null;
		if (minDimensions != null && !"".equals(minDimensions)) {
			result = new int[2];
			result[0] = Integer.parseInt(minDimensions.split(",")[0]);
			result[1] = Integer.parseInt(minDimensions.split(",")[1]);
		}
		return result;
	}

	/**
	 * @return the array containing the maxwidth and maxheight as integers
	 */
	public int[] getDataParamMaxDimensions() {
		String maxDimensions = this.getAttribute("data-param-maxdimensions");
		int[] result = null;
		if (maxDimensions != null && !"".equals(maxDimensions)) {
			result = new int[2];
			result[0] = Integer.parseInt(maxDimensions.split(",")[0]);
			result[1] = Integer.parseInt(maxDimensions.split(",")[1]);
		}
		return result;
	}

	/**
	 * @return wheter the applet should fit to screen
	 */
	public boolean getDataParamFitToScreen() {
		return "true".equals(this.getAttribute("data-param-fittoscreen"));
	}

	/**
	 * @return integer value of the data-param-maxheight, 0 if not present
	 */
	public int getDataParamMaxHeight() {
		String height = this.getAttribute("data-param-maxheight");
		return (height != null && !height.equals("")) ? Integer.parseInt(
		        height, 10) : 0;
	}

	public String getDataParamBorder() {
		return this.getAttribute("data-param-borderColor");
	}

	/**
	 * @return the data-param-showLogging (default: false)
	 */
	public boolean getDataParamShowLogging() {
		return ("true".equals(this.getAttribute("data-param-showLogging")));
	}

	/**
	 * @return the data-param-allowSymbolTable (default: true)
	 */
	public boolean getDataParamAllowSymbolTable() {
		return (!"false".equals(this
		        .getAttribute("data-param-allowSymbolTable")));
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

	private float getEnvScaleX() {
		return envScale("x");
	}

	private float getEnvScaleY() {
		return envScale("y");
	}

	private native float envScale(String type) /*-{
		var matrixRegex = /matrix\((-?\d*\.?\d+),\s*0,\s*0,\s*(-?\d*\.?\d+),\s*0,\s*0\)/, style = $wnd
				.getComputedStyle(this), transform, matches;
		if (style) {
					transform = @org.geogebra.web.html5.util.ArticleElement::getTransform(Lcom/google/gwt/core/client/JavaScriptObject;)(style),
					matches = transform.match(matrixRegex);
			if (matches && matches.length) {
				if (type === "x") {
					return $wnd.parseFloat(matches[1]);
				} else {
					return $wnd.parseFloat(matches[2]);
				}
			} else if (transform.indexOf("scale") === 0) {
				return $wnd.parseFloat(transform
						.substr(transform.indexOf("(") + 1));
			}

		}
		return 1;
	}-*/;

	/**
	 * @return the CSS scale attached to the article element
	 */
	public float getScaleX() {
		// no instance fields in subclasses of Element, so no way to asign it to
		// a simple field
		if ("".equals(this.getAttribute("data-scalex"))) {
			this.setAttribute("data-scalex", String.valueOf(getEnvScaleX()));
		}
		return Float.parseFloat(this.getAttribute("data-scalex"));
	}

	/**
	 * @return the CSS scale attached to the article element
	 * 
	 */
	public float getScaleY() {
		// no instance fields in subclasses of Element, so no way to asign it to
		// a simple field
		if ("".equals(this.getAttribute("data-scaley"))) {
			this.setAttribute("data-scaley", String.valueOf(getEnvScaleY()));
		}
		return Float.parseFloat(this.getAttribute("data-scaley"));
	}

	/**
	 * @return the data-param-heightcrop attribute, that will be cropped from
	 *         the applet height
	 */
	public int getDataParamHeightCrop() {
		String crop = this.getAttribute("data-param-heightcrop");
		return (crop != null && !crop.equals("")) ? Integer.parseInt(crop, 10)
		        : 0;
	}

	/**
	 * @return the data-param-widthcrop attribute, taht will be cropped from the
	 *         applet width
	 */
	public int getDataParamWidthCrop() {
		String crop = this.getAttribute("data-param-widthcrop");
		return (crop != null && !crop.equals("")) ? Integer.parseInt(crop, 10)
		        : 0;
	}

	/**
	 * @return default false
	 */
	public boolean getDataParamAllowStyleBar() {
		return "true".equals(getAttribute("data-param-allowStyleBar"));
	}

	public boolean getDataParamApp() {
		return "true".equals(this.getAttribute("data-param-app"));
	}

	public boolean getDataParamScreenshotGenerator() {
		return "true".equals(this
		        .getAttribute("data-param-screenshotGenerator"));
	}

	public String getDataParamLAF() {
		return this.getAttribute("data-param-laf");
	}

	/**
	 * @return wheter focus prevented (use in multiple applets)
	 */
	public boolean preventFocus() {
		return this.getAttribute("data-param-preventFocus") != null;
	}

	public String getDataClientID() {
		return this.getAttribute("data-param-clientid");
	}

	public String getDataParamPerspective() {
		String ret = this.getAttribute("data-param-perspective");
		return ret == null ? "" : ret;
	}

	public double getDataParamScale() {
		String scale = this.getAttribute("data-param-scale");
		double ret = 1;
		try {
			ret = Double.valueOf(scale);
		} catch (Throwable t) {
			Log.warn("Invalid scale");
		}
		return ret;

	}

	public void adjustScale() {
		if (getDataParamApp()
		        || (getAttribute("data-scalex") != null && !""
		                .equals(getAttribute("data-scalex")))) {
			return;
		}
		double externalScale = getDataParamScale();
		setAttribute("data-scalex", "" + (externalScale * envScale("x")));
		setAttribute("data-scaley", "" + (externalScale * envScale("y")));

		Element parent = this.getParentElement();
		if (parent.getParentElement() != null
		        && "applet_container".equals(parent.getParentElement().getId())) {
			parent = parent.getParentElement();
		}
		Browser.scale(parent, externalScale, 0, 0);
	}

	public boolean getDataParamPrerelease() {
		return "true".equals(getAttribute("data-param-prerelease"));
	}

	public String getDataParamTubeID() {
		return getAttribute("data-param-tubeid");
	}

	public boolean getDataParamNo3D() {
		return "true".equals(getAttribute("data-param-no3d"));
	}

	public boolean getDataParamNoCAS() {
		return "true".equals(getAttribute("data-param-nocas"));
	}

	public boolean getDataParamShowStartTooltip() {
		return "true".equals(getAttribute("data-param-showTutorialLink"));
	}

	public boolean getDataParamEnableFileFeatures() {
		return !"false".equals(getAttribute("data-param-enableFileFeatures"));
	}

}
