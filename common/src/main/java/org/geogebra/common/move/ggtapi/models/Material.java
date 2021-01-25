package org.geogebra.common.move.ggtapi.models;

import java.io.Serializable;
import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;

/**
 * Material POJO
 * 
 * @author Matthias Meisinger
 * 
 */
@SuppressWarnings("serial")
public class Material implements Comparable<Material>, Serializable {

	public enum Provider {
		TUBE, GOOGLE, LOCAL
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

	private int id;
	private String title;

	private MaterialType type;

	private String description;

	/**
	 * UNIX timestamp of this material's creation time.
	 */
	private long timestamp;

	/**
	 * UNIX timestamp of this material's last autosave time.
	 */
	private long autoSaveTimestamp;

	private String author;

	/**
	 * URL to the author's profile in GeoGebraTube.
	 */
	private int authorID;
	/**
	 * Id of the person who stored material to local device
	 */
	private int viewerID;

	/**
	 * URL to the overview page of the material.
	 */
	private String url;

	/**
	 * URL to the material itself (link to student page for materials of type
	 * ggb, download link for ggt, or external link for link).
	 */
	private String urlDirect;

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

	/**
	 * true if a material is featured, false otherwise.
	 */
	private boolean featured;

	/**
	 * Number of likes for this material
	 */
	private int likes;
	private int width;
	private int height;
	private String instructionsPre;
	private String instructionsPost;
	private boolean showMenu;
	private boolean showToolbar;
	private boolean allowStylebar;
	private boolean showInputbar;
	private boolean showResetIcon;
	private boolean shiftDragZoom;
	private boolean rightClick;
	private boolean labelDrags;
	private String base64;
	private String googleID;
	private long syncStamp;
	private long modified;
	private String visibility;
	private int localID;
	private boolean deleted;
	private boolean fromAnotherDevice;
	private boolean favorite;
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

	/**
	 * @param id
	 *            material id
	 * @param type
	 *            material type
	 */
	public Material(int id, MaterialType type) {
		this.id = id;
		this.type = type;

		this.title = "";
		this.timestamp = -1;
		this.autoSaveTimestamp = -1;
		this.author = "";
		this.authorID = -1;
		this.creator = new UserPublic();
		this.url = "";
		this.urlDirect = "";
		this.language = "";
		this.featured = false;
		this.likes = -1;
		this.description = "";
		this.instructionsPre = "";
		this.instructionsPost = "";
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

	public int getId() {
		return this.id;
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
		return this.author;
	}

	/**
	 * @return the URL to the overview page of the material as a String
	 */
	public String getURL() {
		return this.url;
	}

	public String getEditUrl() {
		return GeoGebraConstants.EDIT_URL_BASE + this.id;
	}

	/**
	 * @return the URL to the material itself (link to student page for
	 *         materials of type ggb, download link for ggt, or external link
	 *         for link).
	 */
	public String getURLdirect() {
		return this.urlDirect;
	}

	public String getLanguage() {
		return this.language;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}

	public boolean isFeatured() {
		return this.featured;
	}

	public int getLikes() {
		return this.likes;
	}

	public Date getDate() {
		return new Date(getTimestampForJava());
	}

	public void setId(int id) {
		this.id = id;
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
	 * Reset timestamp and autosave timestamp.
	 */
	public void resetTimestamp() {
		setTimestamp(0);
		setAutosaveTimestamp(0);
	}

	/**
	 * @param autoSaveTimestamp
	 *            autosave timestamp in s
	 */
	public void setAutosaveTimestamp(long autoSaveTimestamp) {
		this.autoSaveTimestamp = autoSaveTimestamp;
	}

	/**
	 * JAVA USES MILLISECONDS, UNIX USES SECONDS
	 * 
	 * @param autoSaveTimestamp
	 *            autosave timestamp in ms
	 */
	public void setAutosaveTimestampFromJava(long autoSaveTimestamp) {
		setAutosaveTimestamp(autoSaveTimestamp / 1000);
	}

	/**
	 * @return autosave timestamp in seconds
	 */
	public long getAutosaveTimestamp() {
		return autoSaveTimestamp;
	}

	/**
	 * @return autosave timestamp in ms
	 */
	public long getAutosaveTimestampForJava() {
		return autoSaveTimestamp * 1000;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthorId(int author_id) {
		this.authorID = author_id;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setURLdirect(String url_direct) {
		this.urlDirect = url_direct;
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

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public void setLikes(int likes) {
		this.likes = likes;
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

	@Override
	public int compareTo(Material another) {
		if (another == null) {
			return 1;
		}
		return this.id - another.id;
	}

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof Material)) {
			return false;
		}
		return this.id == ((Material) another).id;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: ").append(this.id).append(": (").append(this.type)
				.append(") (local ").append(localID).append(") ");
		sb.append("Title: ");
		sb.append(this.title);
		sb.append(" ");
		sb.append("by ");
		sb.append(this.author);
		sb.append(", ");
		sb.append("Date: ");
		sb.append(this.getDate());
		sb.append("\n");
		sb.append("Description: ");
		sb.append(this.description);
		sb.append("\n");
		sb.append("Language: ");
		sb.append(this.language);
		sb.append("\n");
		sb.append("URL: ");
		sb.append(this.url);
		sb.append("\n");
		sb.append("URL_DIRECT: ");
		sb.append(this.urlDirect);
		sb.append("\n");
		sb.append("preview URL: ");
		sb.append(this.previewUrl);
		sb.append("\n");
		sb.append("Thumbnail: ");
		sb.append(this.thumbnail);
		sb.append("\n");
		sb.append("Featured: ");
		sb.append(this.isFeatured());
		sb.append(" ");
		sb.append("Likes: ");
		sb.append(this.likes);
		return sb.toString();
	}

	public JSONObject toJson() {
		return toJson(false);
	}

	/**
	 * @param storeLocalValues
	 *            whether to include local ID and autosave timestamp
	 * @return JSON representation
	 */
	public JSONObject toJson(boolean storeLocalValues) {
		JSONObject ret = new JSONObject();
		putString(ret, "thumbnail", thumbnail);
		// putString(ret,"-type", TODO);
		putString(ret, "author_id", authorID + "");
		putString(ret, "language", language);
		putString(ret, "author", author);
		putString(ret, "description", description);
		putString(ret, "url_direct", urlDirect);
		putString(ret, "featured", featured + "");
		putString(ret, "timestamp", timestamp + "");
		putString(ret, "url", url);
		putString(ret, "type", type.toString());
		putString(ret, "title", title);
		putString(ret, "visibility", visibility);
		putString(ret, "id", id + "");
		putString(ret, "likes", likes + "");
		putString(ret, "ggbBase64", base64);
		putBoolean(ret, "deleted", deleted);
		putBoolean(ret, "favorite", favorite);
		putString(ret, "height", height + "");
		putString(ret, "width", width + "");
		putString(ret, "instructions_pre", this.instructionsPre);
		putString(ret, "instructions_post", this.instructionsPost);
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
			putString(ret, "autoSaveTimestamp", autoSaveTimestamp + "");

		}
		return ret;
	}

	private static void putBoolean(JSONObject ret, String key, boolean val) {
		if (val) {
			try {
				ret.put(key, Boolean.valueOf(val));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private static void putString(JSONObject ret, String key, String value) {
		if (value != null) {
			try {
				ret.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
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

	public String getInstructionsPre() {
		return instructionsPre;
	}

	public String getInstructionsPost() {
		return instructionsPost;
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

	public void setInstructionsPre(String instructionsPre) {
		this.instructionsPre = instructionsPre;
	}

	public void setInstructionsPost(String instructionsPost) {
		this.instructionsPost = instructionsPost;
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

	public String getGoogleID() {
		return this.googleID;
	}

	public void setGoogleID(String googleID) {
		this.googleID = googleID;
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
		return this.authorID;
	}

	public boolean isFavorite() {
		return this.favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
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
	 * @return sharing key if set; otherwise numeric ID
	 */
	public String getSharingKeyOrId() {
		return sharingKey == null || sharingKey.isEmpty() ? id + ""
				: sharingKey;
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
		setCreatorAsAuthor();
	}

	private void setCreatorAsAuthor() {
		author = creator.getUsername();
		authorID = creator.getId();
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
