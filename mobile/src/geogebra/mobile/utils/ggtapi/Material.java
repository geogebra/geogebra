package geogebra.mobile.utils.ggtapi;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * Material POJO
 * 
 * @author Matthias Meisinger
 * 
 */
public class Material implements Comparable<Material>
{
	enum MaterialType
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
		this.thumbnail = "";
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

	@SuppressWarnings("deprecation")
	public Date getDate()
	{
		// JAVA USES MILLISECONDS, UNIX USES SECONDS
		return new Date(this.timestamp * 1000);
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

	@Override
	public int compareTo(Material another)
	{
		if (another == null)
			return 1;
		return this.id - another.id;
	}
}
