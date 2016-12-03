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
public class Material implements Comparable<Material>, Serializable {

	public enum Provider {
		TUBE, GOOGLE, ONE, LOCAL
	};

	public enum MaterialType {
		ggb, ggt, link, book, ws, csv, flexiblews;
	}

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
	private int author_id;

	/**
	 * URL to the overview page of the material.
	 */
	private String url;

	/**
	 * URL to the material itself (link to student page for materials of type
	 * ggb, download link for ggt, or external link for link).
	 */
	private String url_direct;

	/**
	 * Two letter language code of the material.
	 */
	private String language;

	/**
	 * URL of the thumbnail picture for the material. It is empty if there is no
	 * thumbnail available for the material.
	 */
	private String thumbnail;

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
	private boolean showMenu, showToolbar, showInputbar, showResetIcon,
			shiftDragZoom;
	private String base64;
	private String googleID;
	private long syncStamp;
	private long modified;
	private String visibility;
	private int localID;
	private boolean deleted;
	private boolean fromAnotherDevice;
	private boolean favorite;
	
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Material(int id, MaterialType type) {
		this.id = id;
		this.type = type;

		this.title = "";
		this.timestamp = -1;
		this.autoSaveTimestamp = -1;
		this.author = "";
		this.author_id = -1;
		this.url = "";
		this.url_direct = "";
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
	}

	public void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
	}

	public void setShowToolbar(boolean showToolbar) {
		this.showToolbar = showToolbar;
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

	public String getAuthorURL() {
		return GeoGebraConstants.PROFILE_URL_BASE + this.author_id;
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
		return this.url_direct;
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

	public void setType(MaterialType type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestampFromJava(long timestamp) {
		setTimestamp(timestamp / 1000); // JAVA USES MILLISECONDS, UNIX USES SECONDS
	}

	public void resetTimestamp() {
		setTimestamp(0);
		setAutosaveTimestamp(0);
	}

	public void setAutosaveTimestamp(long autoSaveTimestamp) {
		this.autoSaveTimestamp = autoSaveTimestamp;
	}

	public void setAutosaveTimestampFromJava(long autoSaveTimestamp) {
		setAutosaveTimestamp(autoSaveTimestamp / 1000); // JAVA USES MILLISECONDS, UNIX USES SECONDS
	}

	public long getAutosaveTimestamp() {
		return autoSaveTimestamp;
	}

	public long getAutosaveTimestampForJava() {
		// JAVA USES MILLISECONDS, UNIX USES SECONDS
		return autoSaveTimestamp * 1000;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthorId(int author_id) {
		this.author_id = author_id;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setURLdirect(String url_direct) {
		this.url_direct = url_direct;
	}

	private String preview_url;

	public void setPreviewURL(String preview_url){
		this.preview_url = preview_url;
	}

	public String getPreviewURL(){
		return preview_url;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	private boolean thumbnailIsBase64 = false;

	public void setThumbnailUrl(String url) {
		this.thumbnail = url;
		setThumbnailIsBase64(false);
	}

	public void setThumbnailBase64(String base64) {
		this.thumbnail = base64;
		setThumbnailIsBase64(true);
	}

	public boolean thumbnailIsBase64(){
		return thumbnailIsBase64;
	}

	public void setThumbnailIsBase64(boolean flag){
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

	public int compareTo(Material another) {
		if (another == null) {
			return 1;
		}
		return this.id - another.id;
	}

	@Override
	public boolean equals(Object another) {
		if (another == null || !(another instanceof Material)) {
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
		sb.append("ID: " + this.id + ": (" + this.type + ") (local " + localID + ") ");
		sb.append("Title: " + this.title + " ");
		sb.append("by " + this.author + " (" + this.getAuthorURL() + "), ");
		sb.append("Date: " + this.getDate() + "\n");
		sb.append("Description: " + this.description + "\n");
		sb.append("Language: " + this.language + "\n");
		sb.append("URL: " + this.url + "\n");
		sb.append("URL_DIRECT: " + this.url_direct + "\n");
		sb.append("preview URL: " + this.preview_url + "\n");
		sb.append("Thumbnail: " + this.thumbnail + "\n");
		sb.append("Featured: " + this.isFeatured() + " ");
		sb.append("Likes: " + this.likes);
		return sb.toString();
	}

	public JSONObject toJson() {
		return toJson(false);
	}

	public JSONObject toJson(boolean storeLocalValues) {
		JSONObject ret = new JSONObject();
		putString(ret, "thumbnail", thumbnail);
		// putString(ret,"-type", TODO);
		putString(ret, "author_id", author_id + "");
		putString(ret, "language", language);
		putString(ret, "author", author);
		putString(ret, "description", description);
		putString(ret, "url_direct", url_direct);
		putString(ret, "featured", featured + "");
		putString(ret, "timestamp", timestamp + "");
		putString(ret, "url", url);
		putString(ret, "type", type.name());
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
		if (storeLocalValues) {
			putString(ret, "localID", localID + "");
			putString(ret, "autoSaveTimestamp", autoSaveTimestamp + "");
		}
		return ret;
	}

	private void putBoolean(JSONObject ret, String key, boolean val) {
		if (val) {
			try {
				ret.put(key, Boolean.valueOf(val));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private void putString(JSONObject ret, String key, String value) {
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

	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
		}
	}

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
		return this.author_id;
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
}
