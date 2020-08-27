package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Date;

import org.geogebra.common.GeoGebraConstants;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;

public final class GeoGebraElement extends Element implements AttributeProvider {

	/**
	 * @param element
	 *            Assert, that the given {@link Element} is compatible with this
	 *            class and automatically typecast it.
	 * @return cast element
	 */
	public static GeoGebraElement as(Element element) {
		if (element != null) {
			element.setTabIndex(-1);
		}
		return (GeoGebraElement) element;
	}

	/**
	 * @return list of articles on the page that have the proper class (
	 *         {@value GeoGebraConstants#GGM_CLASS_NAME})
	 */
	public static ArrayList<GeoGebraElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<GeoGebraElement> articleNodes = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			GeoGebraElement ae = GeoGebraElement.as(nodes.getItem(i));
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
		AppletParameters params = new AppletParameters(this);
		String paramID = params.getDataParamId();
		if (paramID.equals(getId())) {
			return;
		}
		if (paramID.length() > 0) {
			int suffix = 0;
			while (DOM.getElementById(paramID) != null) {
				paramID = params.getDataParamId() + suffix;
				suffix++;
			}
			setId(paramID);
			return;
		}
		Date creationDate = new Date();
		setId(GeoGebraConstants.GGM_CLASS_NAME + i + creationDate.getTime());
	}

	/**
	 * Create new article element
	 */
	protected GeoGebraElement() {
		// needed for GWT
	}

	public void clear() {
		this.setInnerHTML("");
	}

	/**
	 *
	 * @return that the article element has (inherited) direction attribute
	 */
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

	/**
	 * @return get CSS scale of parent element
	 */
	public double getParentScaleX() {
		return envScale(getParentElement(), "x", false);
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
		if ("".equals(getAttribute("data-scalex"))) {
			setAttribute("data-scalex", String.valueOf(envScale("x")));
		}
		return Double.parseDouble(getAttribute("data-scalex"));
	}

	/**
	 * Read cached scale value or compute it, do not cache it
	 *
	 * @return the CSS scale attached to the article element
	 */
	public double readScaleX() {
		if ("".equals(getAttribute("data-scalex"))) {
			return envScale("x");
		}
		return Double.parseDouble(getAttribute("data-scalex"));
	}

	/**
	 * @return the CSS scale attached to the article element
	 *
	 */
	public double getScaleY() {
		// no instance fields in subclasses of Element, so no way to asign it to
		// a simple field
		if ("".equals(getAttribute("data-scaley"))) {
			setAttribute("data-scaley", String.valueOf(envScale("y")));
		}
		return Double.parseDouble(getAttribute("data-scaley"));
	}

	/**
	 * Remove cached scale values
	 *
	 * @param parentScale
	 *            new scale of scaler element
	 */
	public void resetScale(double parentScale) {
		setAttribute("data-scalex", "" + envScale("x"));
		setAttribute("data-scaley", "" + envScale("y"));
	}

	public GeoGebraElement getElement() {
		return this;
	}
}
