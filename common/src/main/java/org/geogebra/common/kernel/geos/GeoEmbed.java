package org.geogebra.common.kernel.geos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * Geo for embedded apps
 */
public class GeoEmbed extends GeoWidget implements Translateable {

	private boolean defined = true;
	private int embedID = -1;
	private double contentWidth = 800;
	private double contentHeight = 600;
	private boolean background = true;
	private String appName = "graphing";
	private String url;
	private Map<String, String> settings = new HashMap<>();

	/**
	 * @param c
	 *            construction
	 */
	public GeoEmbed(Construction c) {
		super(c);
	}

	/**
	 * Center this in a view
	 * 
	 * @param ev
	 *            view
	 */
	public void initPosition(EuclidianViewInterfaceCommon ev) {
		double x = ev.toRealWorldCoordX(ev.getViewWidth() / 2.0)
				- contentWidth / ev.getXscale() / 2;
		double y = ev.toRealWorldCoordY(ev.getViewHeight() / 2.0)
				- contentHeight / ev.getYscale() / 2;
		startPoint = new GeoPoint(cons);
		startPoint.setCoords(x, y, 1);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.EMBED;
	}

	@Override
	public GeoElement copy() {
		GeoEmbed ret = new GeoEmbed(cons);
		ret.set(this);
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoEmbed) {
			this.appName = ((GeoEmbed) geo).appName;
		}
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	public void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<embed id=\"");
		sb.append(embedID);
		sb.append("\" app=\"");
		sb.append(appName);
		if (!StringUtil.empty(url)) {
			sb.append("\" url=\"");
			sb.append(StringUtil.encodeXML(url));
		}
		sb.append("\"/>\n");
		sb.append("<embedSettings");
		for (Map.Entry<String, String> entry: getSettings()) {
			sb.append(' ')
				.append(entry.getKey())
				.append("=\"")
				.append(StringUtil.encodeXML(entry.getValue()))
				.append('\"');

		}
		sb.append("/>");
		XMLBuilder.dimension(sb, Double.toString(contentWidth), Double.toString(contentHeight));
	}

	/**
	 * @return appName param of the applet
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @return embed ID: needs to be unique in construction
	 */
	public int getEmbedID() {
		return embedID;
	}

	/**
	 * @param embedID
	 *            embed ID: needs to be unique in construction
	 */
	public void setEmbedId(int embedID) {
		this.embedID = embedID;
	}

	/**
	 * @return whether the applet is currently inactive (can be moved)
	 */
	public boolean isBackground() {
		return background;
	}

	/**
	 * @param background
	 *            whether the applet is currently inactive (can be moved)
	 */
	public void setBackground(boolean background) {
		this.background = background;
	}

	/**
	 * @return width parameter of the embedded applet
	 */
	public double getContentWidth() {
		return contentWidth;
	}

	/**
	 * @return height parameter of the embedded applet
	 */
	public double getContentHeight() {
		return contentHeight;
	}

	/**
	 * @param newWidth
	 *            width parameter of the embedded applet
	 */
	public void setContentWidth(double newWidth) {
		this.contentWidth = newWidth;
	}

	/**
	 * @param newHeight
	 *            height parameter of the embedded applet
	 */
	public void setContentHeight(double newHeight) {
		this.contentHeight = newHeight;
	}

	/**
	 * @param appName
	 *            app name of the embedded applet
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return URL for external embeds
	 */
	public String getURL() {
		return url;
	}

	/**
	 * @param url
	 *            url for external embed
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isGraspableMath() {
		return url != null && url.contains("graspablemath.com");
	}

	public Set<Map.Entry<String, String>> getSettings() {
		return settings.entrySet();
	}

	public void attr(String key, Object value) {
		settings.put(key, String.valueOf(value));
	}
}
