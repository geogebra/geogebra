package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;

import java.util.Date;

/**
 * Material POJO
 * 
 * @author Matthias Meisinger
 * 
 */
public class Material implements Comparable<Material>
{
	public enum MaterialType
	{
		ggb, ggt, link;
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
	private String author_url;

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

	public Material(int id, MaterialType type)
	{
		this.id = id;
		this.type = type;

		this.title = "";
		this.timestamp = -1;
		this.author = "";
		this.author_url = "";
		this.url = "";
		this.url_direct = "";
		this.language = "";
		this.featured = false;
		this.likes = -1;
		this.description = "";
		this.instructionsPre = "";
		this.instructionsPost = "";
		this.width = 800;
		this.height = 600;
		this.thumbnail = "";
		showMenu = false;
		showToolbar= false;
		showInputbar= false;
		showResetIcon = false; 
		shiftDragZoom = true;
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
		return this.author_url;
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

	public void setAuthorURL(String author_url)
	{
		this.author_url = author_url;
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
		sb.append("by " + this.author + " (" + this.author_url + "), ");
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
		putString(ret,"author_url", author_url);
		putString(ret,"language", language);
		putString(ret,"author", author);
		putString(ret,"description", description);
		putString(ret,"url_direct", url_direct);
		putString(ret,"featured", featured+"");
		putString(ret,"timestamp", timestamp+"");
		putString(ret,"url", url);
		putString(ret,"type", type.name());
		putString(ret,"title", title);
		putString(ret,"id", id+"");
		putString(ret,"likes", likes+"");
		return ret;
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

	public String getShiftDragZoom() {
		return this.shiftDragZoom+"";
	}

	public String getShowMenu() {
		return this.showMenu+"";
	}

	public String getShowToolbar() {
		return this.showToolbar+"";
	}
	
	public String getShowInputbar() {
		return this.showInputbar+"";
	}

	public String getShowResetIcon() {
		return this.showResetIcon+"";
	}
}
