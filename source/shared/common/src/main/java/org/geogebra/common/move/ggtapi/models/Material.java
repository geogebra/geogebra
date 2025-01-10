package org.geogebra.common.move.ggtapi.models;

import java.io.Serializable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Material POJO
 * 
 * @author Matthias Meisinger
 * 
 */
@SuppressWarnings("serial")
public class Material implements Serializable {

    public enum Provider {
		TUBE("GeoGebra"),
		GOOGLE("Google Drive"),
		LOCAL("Local");

		private String name;

		Provider(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		/**
		 * @param providerStr - provider string
		 * @return provider
		 */
		public static Provider getProviderForString(String providerStr) {
			if (providerStr.equals(TUBE.getName())) {
				return TUBE;
			}
			if (providerStr.equals(GOOGLE.getName())) {
				return GOOGLE;
			}
			if (providerStr.equals(LOCAL.getName())) {
				return LOCAL;
			}
			return TUBE;
		}
	}

	public enum MaterialType {
		ggb, ggt, ggs, link, book, ws, csv, flexiblews, ggsTemplate;

		@Override
		public String toString() {
			if (this == MaterialType.ggsTemplate) {
				return "ggs-template";
			}
			return this.name();
		}
	}

	public static final int MAX_TITLE_LENGTH = 255;

	private String title;

	private MaterialType type;

	private String description;

	/**
	 * UNIX timestamp of this material's creation time.
	 */
	private long timestamp;

	/**
	 * Id of the person who stored material to local device
	 */
	private int viewerID;

	/**
	 * URL to the overview page of the material.
	 */
	private String url;

	/**
	 * Two letter language code of the material.
	 */
	private String language;

	/**
	 * URL of the thumbnail picture for the material. It is empty if there is no
	 * thumbnail available for the material.
	 */
	private String thumbnail;
	private boolean thumbnailIsBase64 = false;
	private String previewUrl;
	private int width;
	private int height;
	private boolean showMenu;
	private boolean showToolbar;
	private boolean allowStylebar;
	private boolean showInputbar;
	private boolean showResetIcon;
	private boolean shiftDragZoom;
	private boolean rightClick;
	private boolean labelDrags;
	private String base64;
	private long syncStamp;
	private long modified;
	private String visibility;
	private int localID;
	private boolean deleted;
	private boolean fromAnotherDevice;
	private boolean undoRedo;
	private boolean showZoomButtons;

	private boolean is3d;
	private boolean spreadsheet;
	private boolean cas;
	private boolean graphics2;
	private boolean constprot;
	private boolean propcalc;
	private boolean dataanalysis;
	private boolean funcinsp;
	private boolean macro;
	private String sharingKey;
	private int elemcntApplet;
	private String fileName;
	private String appName;

	private long dateCreated;
	private UserPublic creator;
	private Object fileContent;
	private boolean multiuser;
	private boolean sharedWithGroup;

	/**
	 * @param type
	 *            material type
	 */
	public Material(MaterialType type) {
		this.type = type;

		this.title = "";
		this.timestamp = -1;
		this.creator = new UserPublic();
		this.url = "";
		this.language = "";
		this.description = "";
		this.visibility = "P";
		this.width = 800;
		this.height = 600;
		this.thumbnail = "";
		this.syncStamp = -1;
		this.modified = -1;
		this.localID = -1;
		this.showMenu = false;
		this.showToolbar = false;
		this.showInputbar = false;
		this.showResetIcon = false;
		this.shiftDragZoom = true;
		this.rightClick = true;
		this.labelDrags = true;
		this.appName = "";
	}

	/**
	 * Copy constructor
	 * @param material material to be copied
	 */
	public Material(Material material) {
		title = material.title;
		type = material.type;
		description = material.description;
		timestamp = material.timestamp;
		viewerID = material.viewerID;
		url = material.url;
		language = material.language;
		thumbnail = material.thumbnail;
		thumbnailIsBase64 = material.thumbnailIsBase64;
		previewUrl = material.previewUrl;
		width = material.width;
		height = material.height;
		showMenu = material.showMenu;
		showToolbar = material.showToolbar;
		allowStylebar = material.allowStylebar;
		showInputbar = material.showInputbar;
		showResetIcon = material.showResetIcon;
		shiftDragZoom = material.shiftDragZoom;
		rightClick = material.rightClick;
		labelDrags = material.labelDrags;
		base64 = material.base64;
		syncStamp = material.syncStamp;
		modified = material.modified;
		visibility = material.visibility;
		localID = material.localID;
		deleted = material.deleted;
		fromAnotherDevice = material.fromAnotherDevice;
		undoRedo = material.undoRedo;
		showZoomButtons = material.showZoomButtons;
		is3d = material.is3d;
		spreadsheet = material.spreadsheet;
		cas = material.cas;
		graphics2 = material.graphics2;
		constprot = material.constprot;
		propcalc = material.propcalc;
		dataanalysis = material.dataanalysis;
		funcinsp = material.funcinsp;
		macro = material.macro;
		sharingKey = material.sharingKey;
		elemcntApplet = material.elemcntApplet;
		fileName = material.fileName;
		appName = material.appName;
		dateCreated = material.dateCreated;
		creator = material.creator;
		fileContent = material.fileContent;
		sharedWithGroup = material.sharedWithGroup;
		multiuser = material.multiuser;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
	}

	public void setShowToolbar(boolean showToolbar) {
		this.showToolbar = showToolbar;
	}

	public void setAllowStylebar(boolean allowStylebar) {
		this.allowStylebar = allowStylebar;
	}

	public void setShowInputbar(boolean showInputbar) {
		this.showInputbar = showInputbar;
	}

	public void setShowResetIcon(boolean showResetIcon) {
		this.showResetIcon = showResetIcon;
	}

	public void setShiftDragZoom(boolean shiftDragZoom) {
		this.shiftDragZoom = shiftDragZoom;
	}

	public String getTitle() {
		return this.title;
	}

	public MaterialType getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}

	public String getAuthor() {
		return creator == null ? "" : this.creator.getDisplayName();
	}

	/**
	 * @return the URL to the overview page of the material as a String
	 */
	public String getURL() {
		return this.url;
	}

	public String getEditUrl() {
		return GeoGebraConstants.EDIT_URL_BASE + getSharingKeySafe();
	}

	public String getLanguage() {
		return this.language;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}

	public void setType(MaterialType type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param timestamp
	 *            timestamp in s
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @param timestamp
	 *            timestamp in ms
	 */
	public void setTimestampFromJava(long timestamp) {
		setTimestamp(timestamp / 1000); // JAVA USES MILLISECONDS, UNIX USES
										// SECONDS
	}

	/**
	 * Reset timestamp.
	 */
	public void resetTimestamp() {
		setTimestamp(0);
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setPreviewURL(String preview_url) {
		this.previewUrl = preview_url;
	}

	public String getPreviewURL() {
		return previewUrl;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @param url
	 *            thumbnail URL
	 */
	public void setThumbnailUrl(String url) {
		this.thumbnail = url;
		setThumbnailIsBase64(false);
	}

	/**
	 * @param base64
	 *            thumbnail as base64
	 */
	public void setThumbnailBase64(String base64) {
		this.thumbnail = base64;
		setThumbnailIsBase64(true);
	}

	/**
	 * @return whether thumbnail is stored as base64
	 */
	public boolean thumbnailIsBase64() {
		return thumbnailIsBase64;
	}

	/**
	 * @param flag
	 *            whether thumbnail is stored as base64
	 */
	public void setThumbnailIsBase64(boolean flag) {
		thumbnailIsBase64 = flag;
	}

	public void setSyncStamp(long stamp) {
		this.syncStamp = stamp;
	}

	public long getSyncStamp() {
		return this.syncStamp;
	}

	public void setVisibility(String v) {
		this.visibility = v;
	}

	public String getVisibility() {
		return this.visibility;
	}

	public void setMultiuser(boolean multiuser) {
		this.multiuser = multiuser;
	}

	public boolean isMultiuser() {
		return multiuser;
	}

	public void setSharedWithGroup(boolean sharedWithGroup) {
		this.sharedWithGroup = sharedWithGroup;
	}

	public boolean isSharedWithGroup() {
		return sharedWithGroup;
	}

	@Override
	public String toString() {
		return "ID: " + getSharingKeySafe() + ": (" + this.type
				+ ") (local " + localID + ") "
				+ "Title: "
				+ this.title
				+ " by " + getAuthor()
				+ ", "
				+ "Date: "
				+ this.timestamp
				+ "\n"
				+ "Description: "
				+ this.description
				+ "\n"
				+ "Language: "
				+ this.language
				+ "\n"
				+ "URL: "
				+ this.url
				+ "\n"
				+ "File: "
				+ this.fileName
				+ "\n"
				+ "Preview: "
				+ this.previewUrl
				+ "\n"
				+ "Thumbnail: "
				+ this.thumbnail;
	}

	public JSONObject toJson() {
		return toJson(false);
	}

	/**
	 * @param storeLocalValues
	 *            whether to include local ID
	 * @return JSON representation
	 */
	public JSONObject toJson(boolean storeLocalValues) {
		JSONObject ret = new JSONObject();
		putString(ret, "thumbnail", thumbnail);
		// putString(ret,"-type", TODO);
		putString(ret, "author_id", getAuthorID() + "");
		putString(ret, "language", language);
		putString(ret, "author", getAuthor());
		putString(ret, "description", description);
		putString(ret, "timestamp", timestamp + "");
		putString(ret, "url", url);
		putString(ret, "type", type.toString());
		putString(ret, "title", title);
		putString(ret, "visibility", visibility);
		putString(ret, "ggbBase64", base64);
		putBoolean(ret, "deleted", deleted);
		putString(ret, "height", height + "");
		putString(ret, "width", width + "");
		putString(ret, "syncstamp", syncStamp + "");
		putString(ret, "modified", this.modified + "");
		putBoolean(ret, "toolbar", this.showToolbar);
		putBoolean(ret, "menubar", this.showMenu);
		putBoolean(ret, "inputbar", this.showInputbar);
		putBoolean(ret, "from_another_device", this.fromAnotherDevice);
		putString(ret, "is3d", this.is3d ? "1" : "0");
		putString(ret, "viewerID", viewerID + "");
		putString(ret, "appnname", appName);
		if (storeLocalValues) {
			putString(ret, "localID", localID + "");

		}
		return ret;
	}

	private static void putBoolean(JSONObject ret, String key, boolean val) {
		if (val) {
			try {
				ret.put(key, Boolean.valueOf(val));
			} catch (JSONException e) {
				Log.debug(e);
			}
		}

	}

	private static void putString(JSONObject ret, String key, String value) {
		if (value != null) {
			try {
				ret.put(key, value);
			} catch (JSONException e) {
				Log.debug(e);
			}
		}
	}

	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return timestamp in ms
	 */
	public long getTimestampForJava() {
		// JAVA USES MILLISECONDS, UNIX USES SECONDS
		return timestamp * 1000;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            applet height in px
	 */
	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
		}
	}

	/**
	 * @param width
	 *            applet width in px
	 */
	public void setWidth(int width) {
		if (width > 0) {
			this.width = width;
		}
	}

	public boolean getShiftDragZoom() {
		return this.shiftDragZoom;
	}

	public boolean getRightClick() {
		return this.rightClick;
	}

	public boolean getLabelDrags() {
		return this.labelDrags;
	}

	public boolean getShowMenu() {
		return this.showMenu;
	}

	public boolean getShowToolbar() {
		return this.showToolbar;
	}

	public boolean getShowInputbar() {
		return this.showInputbar;
	}

	public boolean getShowResetIcon() {
		return this.showResetIcon;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public String getBase64() {
		return this.base64;
	}

	public void setModified(long parseLong) {
		this.modified = parseLong;
	}

	public long getModified() {
		return this.modified;
	}

	/**
	 * @param localID
	 *            local ID
	 */
	public void setLocalID(int localID) {
		this.localID = localID;
	}

	public int getLocalID() {
		return localID;
	}

	public boolean isFromAnotherDevice() {
		return fromAnotherDevice;
	}

	public void setFromAnotherDevice(boolean fromAnotherDevice) {
		this.fromAnotherDevice = fromAnotherDevice;
	}

	public int getAuthorID() {
		return creator == null ? -1 : creator.getId();
	}

	public boolean has3d() {
		return is3d;
	}

	public void setIs3d(boolean is3d) {
		this.is3d = is3d;
	}

	public boolean hasSpreadsheet() {
		return spreadsheet;
	}

	public void setSpreadsheet(boolean spreadsheet) {
		this.spreadsheet = spreadsheet;
	}

	public boolean hasCas() {
		return cas;
	}

	public void setCas(boolean cas) {
		this.cas = cas;
	}

	public boolean hasGraphics2() {
		return graphics2;
	}

	public void setGraphics2(boolean graphics2) {
		this.graphics2 = graphics2;
	}

	public boolean hasConstprot() {
		return constprot;
	}

	public void setConstprot(boolean constprot) {
		this.constprot = constprot;
	}

	public boolean hasPropcalc() {
		return propcalc;
	}

	public void setPropcalc(boolean propcalc) {
		this.propcalc = propcalc;
	}

	public boolean hasDataanalysis() {
		return dataanalysis;
	}

	public void setDataanalysis(boolean dataanalysis) {
		this.dataanalysis = dataanalysis;
	}

	public boolean hasFuncinsp() {
		return funcinsp;
	}

	public void setFuncinsp(boolean funcinsp) {
		this.funcinsp = funcinsp;
	}

	public boolean hasMacro() {
		return macro;
	}

	public void setMacro(boolean macro) {
		this.macro = macro;
	}

	public void setSharingKey(String sharingKey) {
		this.sharingKey = sharingKey;
	}

	public String getSharingKey() {
		return sharingKey;
	}

	/**
	 * @return sharing key if set, otherwise empty string
	 */
	public String getSharingKeySafe() {
		return sharingKey == null ? "" : sharingKey;
	}

	public int getElemcntApplet() {
		return elemcntApplet;
	}

	public void setElemcntApplet(int elemcntApplet) {
		this.elemcntApplet = elemcntApplet;
	}

	public int getViewerID() {
		return viewerID;
	}

	public void setViewerID(int int1) {
		this.viewerID = int1;
	}

	public void setRightClick(boolean rightClick) {
		this.rightClick = rightClick;
	}

	public void setLabelDrags(boolean labelDrags) {
		this.labelDrags = labelDrags;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fn) {
		fileName = fn;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public UserPublic getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            material creator
	 */
	public void setCreator(UserPublic creator) {
		this.creator = creator;
	}

	/**
	 * @param userId user ID
	 * @deprecated use setCreator instead; this method will be removed
	 * when upload works with new API
	 */
	@Deprecated
	public void setAuthorId(int userId) {
		setCreator(new UserPublic(userId, getAuthor()));
	}

	public boolean getAllowStylebar() {
		return allowStylebar;
	}

	public boolean getUndoRedo() {
		return undoRedo;
	}

	public void setUndoRedo(boolean undoRedo) {
		this.undoRedo = undoRedo;
	}

	public boolean getShowZoomButtons() {
		return showZoomButtons;
	}

	public void setShowZoomButtons(boolean showZoomButtons) {
		this.showZoomButtons = showZoomButtons;
	}

	public void setContent(Object fileContent) {
		this.fileContent = fileContent;
	}

	public Object getContent() {
		return this.fileContent;
	}
}
