package org.geogebra.common.move.ggtapi.models;

import java.util.Date;

import org.geogebra.common.move.ggtapi.models.json.JSONBoolean;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;

/**
 * Material POJO
 * 
 * @author Matthias Meisinger
 * 
 */
public class Material implements Comparable<Material>
{
	

	public enum Provider {TUBE, GOOGLE, ONE};
	public enum MaterialType
	{
		ggb, ggt, link, book, ws, csv;
	}

	private int id;
	private String title;

	private MaterialType type;

	private String description;

	/**
	 * UNIX timestamp of this material's creation time.
	 */
	private long timestamp;

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
	 * URL to the material itself (link to student page for materials of type ggb,
	 * download link for ggt, or external link for link).
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
	private boolean showMenu, showToolbar, showInputbar, showResetIcon, shiftDragZoom;
	private String base64;
	private String googleID;
	private long syncStamp;
	private long modified;
	private String visibility;
	private int localID;
	private boolean deleted;
	private boolean fromAnotherDevice;
	private boolean favorite;
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Material(int id, MaterialType type)
	{
		this.id = id;
		this.type = type;

		this.title = "";
		this.timestamp = -1;
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

	public int getId()
	{
		return this.id;
	}

	public String getTitle()
	{
		return this.title;
	}

	public MaterialType getType()
	{
		return this.type;
	}

	public String getDescription()
	{
		return this.description;
	}

	public String getAuthor()
	{
		return this.author;
	}

	public String getAuthorURL()
	{
		return "//tube.geogebra.org/user/profile/id/" + this.author_id;
	}

	/**
	 * @return the URL to the overview page of the material as a String
	 */
	public String getURL()
	{
		return this.url;
	}

	/**
	 * @return the URL to the material itself (link to student page for materials
	 *         of type ggb, download link for ggt, or external link for link).
	 */
	public String getURLdirect()
	{
		return this.url_direct;
	}

	public String getLanguage()
	{
		return this.language;
	}

	public String getThumbnail()
	{
		return this.thumbnail;
	}

	public boolean isFeatured()
	{
		return this.featured;
	}

	public int getLikes()
	{
		return this.likes;
	}

	public Date getDate()
	{
		// JAVA USES MILLISECONDS, UNIX USES SECONDS
		return new Date(timestamp * 1000);
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setType(MaterialType type)
	{
		this.type = type;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public void setAuthorId(int author_id)
	{
		this.author_id = author_id;
	}

	public void setURL(String url)
	{
		this.url = url;
	}

	public void setURLdirect(String url_direct)
	{
		this.url_direct = url_direct;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public void setThumbnail(String thumbnail)
	{
		this.thumbnail = thumbnail;
	}

	public void setFeatured(boolean featured)
	{
		this.featured = featured;
	}

	public void setLikes(int likes)
	{
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
	
	public int compareTo(Material another)
	{
		if (another == null)
			return 1;
		return this.id - another.id;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("ID: " + this.id + ": (" + this.type + ") ");
		sb.append("Title: " + this.title + " ");
		sb.append("by " + this.author + " (" + this.getAuthorURL() + "), ");
		sb.append("Date: " + this.getDate() + "\n");
		sb.append("Description: " + this.description + "\n");
		sb.append("Language: " + this.language + "\n");
		sb.append("URL: " + this.url + "\n");
		sb.append("URL_DIRECT: " + this.url_direct + "\n");
		sb.append("Thumbnail: " + this.thumbnail + "\n");
		sb.append("Featured: " + this.isFeatured() + " ");
		sb.append("Likes: " + this.likes);
		return sb.toString();
	}
	
	public JSONObject toJson(){
		JSONObject ret = new JSONObject();
		putString(ret,"thumbnail", thumbnail);
	//	putString(ret,"-type", TODO);
		putString(ret, "author_id", author_id + "");
		putString(ret,"language", language);
		putString(ret,"author", author);
		putString(ret,"description", description);
		putString(ret,"url_direct", url_direct);
		putString(ret,"featured", featured+"");
		putString(ret,"timestamp", timestamp+"");
		putString(ret,"url", url);
		putString(ret,"type", type.name());
		putString(ret,"title", title);
		putString(ret,"visibility", visibility);
		putString(ret,"id", id+"");
		putString(ret,"likes", likes+"");
		putString(ret,"ggbBase64", base64);
		putBoolean(ret, "deleted", deleted);
		putBoolean(ret, "favorite", favorite);
		putString(ret,"height", height+"");
		putString(ret,"width", width+"");
		putString(ret,"instructions_pre", this.instructionsPre);
		putString(ret,"instructions_post", this.instructionsPost);
		putString(ret,"syncstamp", syncStamp+"");
		putString(ret, "modified", this.modified + "");
		putBoolean(ret,"toolbar", this.showToolbar);
		putBoolean(ret,"menubar", this.showMenu);
		putBoolean(ret,"inputbar", this.showInputbar);
		putBoolean(ret, "from_another_device", this.fromAnotherDevice);
		return ret;
	}

	private void putBoolean(JSONObject ret, String key, boolean val) {
		if(val){
			ret.put(key, JSONBoolean.getInstance(val));
		}
		
	}

	private void putString(JSONObject ret, String key, String value) {
		if(value!=null){
			ret.put(key, new JSONString(value));
		}
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getInstructionsPre(){
		return instructionsPre;
	}
	
	public String getInstructionsPost(){
		return instructionsPost;
	}

	public void setHeight(int height) {
		if(height > 0){
			this.height = height;
		}
	}

	public void setWidth(int width) {
		if(width > 0){
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
	
	public void setBase64(String base64){
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
}
