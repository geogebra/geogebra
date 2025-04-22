package org.geogebra.common.kernel.geos;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;

/**
 * Geo for embedded apps
 */
public class GeoEmbed extends GeoWidget {

	private static final double DEFAULT_WIDTH = 800;
	private static final double DEFAULT_HEIGHT = 600;

	public final static int EMBED_SIZE_THRESHOLD = 100;
	private final static int MARGINS = 32; // left + right
	private static final int TOOLBOX_WIDTH = 80;
	public static final String EXTERNAL_PROTOCOL = "external:";

	private double contentWidth = DEFAULT_WIDTH;
	private double contentHeight = DEFAULT_HEIGHT;

	// ONLY USED FOR LOADING OLD MATERIALS
	private Double realWidth;
	private Double realHeight;

	private boolean defined = true;
	private int embedID = -1;

	private boolean background = true;
	private String appName = "graphing";
	private String url;
	private final Map<String, String> settings = new HashMap<>();
	private boolean hasLocation = false;

	/**
	 * @param c
	 *            construction
	 */
	public GeoEmbed(Construction c) {
		super(c);
	}

	@Override
	public double getMinWidth() {
		return EMBED_SIZE_THRESHOLD;
	}

	@Override
	public double getMinHeight() {
		return EMBED_SIZE_THRESHOLD;
	}

	@Override
	public void setSize(double width, double height) {
		if (getWidth() != 0) {
			setContentWidth(contentWidth * width / getWidth());
		}
		if (getHeight() != 0) {
			setContentHeight(contentHeight * height / getHeight());
		}
		super.setSize(width, height);
	}

	@Override
	public void zoomIfNeeded() {
		if (realWidth != null && realHeight != null) {
			EuclidianView ev = app.getActiveEuclidianView();

			setSize(ev.getXscale() * realWidth, ev.getYscale() * realHeight);

			realWidth = null;
			realHeight = null;
		} else {
			super.zoomIfNeeded();
		}
	}

	/**
	 * Center this in a view
	 * 
	 * @param ev
	 *            view
	 */
	public void initPosition(EuclidianViewInterfaceCommon ev) {
		double x = ev.toRealWorldCoordX((ev.getViewWidth() - getWidth() + TOOLBOX_WIDTH) / 2.0);
		double y = ev.toRealWorldCoordY((ev.getViewHeight() - getHeight()) / 2.0);
		startPoint.setLocation(x, y);
	}

	/**
	 * Set the size to the default, and then center it in the view
	 * @param view euclidian view
	 */
	public void initDefaultPosition(EuclidianView view) {
		double width = MyMath.clamp(DEFAULT_WIDTH, EMBED_SIZE_THRESHOLD,
				view.getWidth() - TOOLBOX_WIDTH - MARGINS);
		double height =
				MyMath.clamp(DEFAULT_HEIGHT, EMBED_SIZE_THRESHOLD, view.getHeight() - MARGINS);
		setContentWidth(width);
		setContentHeight(height);
		setSize(width, height);
		initPosition(view);
	}

	public int getContentWidth() {
		return (int) Math.round(contentWidth);
	}

	public void setContentWidth(double contentWidth) {
		this.contentWidth = contentWidth;
	}

	public int getContentHeight() {
		return (int) Math.round(contentHeight);
	}

	public void setContentHeight(double contentHeight) {
		this.contentHeight = contentHeight;
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
	public void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);
		sb.append("\t<embed id=\"");
		sb.append(embedID);
		sb.append("\" app=\"");
		sb.append(appName);
		getEmbedContentSource(sb);
		sb.append("\"/>\n");
		sb.append("\t<contentSize width=\"");
		sb.append(contentWidth);
		sb.append("\" height=\"");
		sb.append(contentHeight);
		sb.append("\"/>\n");
		sb.append("\t<embedSettings");
		for (Map.Entry<String, String> entry : getSettings()) {
			sb.append(' ')
				.append(entry.getKey())
				.append("=\"");
				StringUtil.encodeXML(sb, entry.getValue());
			sb.append('\"');
		}
		sb.append("/>\n");
	}

	/**
	 * Get the source of the embed content, it is the url by default.
	 *
	 * @param sb to append
	 */
	protected void getEmbedContentSource(StringBuilder sb) {
		if (!StringUtil.empty(url)) {
			sb.append("\" url=\"");
			StringUtil.encodeXML(sb, url);
		}
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
	 * @param appName
	 *            app name of the embedded applet
	 */
	public void setAppName(String appName) {
		if (GRAPHING_APPCODE.equals(appName) || CAS_APPCODE.equals(appName)) {
			this.appName = SUITE_APPCODE;
		} else {
			this.appName = appName;
		}
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

	/**
	 *
	 * @return if embed has an external protocol to resolv its content.
	 */
	public boolean hasExternalProtocol() {
		return "external".equals(appName);
	}

	public Set<Map.Entry<String, String>> getSettings() {
		return settings.entrySet();
	}

	/**
	 * Set an attribute (for Calc embeds those are the app parameters).
	 * @param key attribute name
	 * @param value attribute value
	 */
	public void attr(String key, Object value) {
		settings.put(key, String.valueOf(value));
	}

	public void setRealWidth(double width) {
		this.realWidth = width;
	}

	public void setRealHeight(double height) {
		this.realHeight = height;
	}

	@Override
	public boolean isMoveable() {
		return true;
	}

	@Override
	public void setLocation(GPoint2D location) {
		super.setLocation(location);
		hasLocation = true;
	}

	/**
	 * @return whether screen location was initialized
	 */
	public boolean hasLocation() {
		return hasLocation;
	}

	/**
	 * Sets the protocol of the custom, resolvable embed.
	 *
	 * @param type of the custom embed.
	 * @param id of the custom embed.
	 */
	public void setExternalProtocol(String type, String id) {
		setAppName("external");
		setUrl(type + ":" + id);
	}

	/**
	 *
	 * @return external type if embed is a custom one, empty if not.
	 */
	public String getExternalType() {
		if (!hasExternalProtocol()) {
			return "";
		}
		return getProtocol()[0];
	}

	/**
	 *
	 * @return external id if embed is a custom one, empty if not.
	 */
	public String getExternalId() {
		if (!hasExternalProtocol()) {
			return "";
		}
		return getProtocol()[1];
	}

	private String[] getProtocol() {
		return url.split(":", 2);
	}

	/**
	 * @param type external type
	 * @return whether this is an embed of given type
	 */
	public boolean isTypeOf(String type) {
		return getExternalType().equals(type);
	}
}
