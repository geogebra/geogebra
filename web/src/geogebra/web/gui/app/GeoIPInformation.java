package geogebra.web.gui.app;

import java.io.Serializable;

/**
 * @author Rana
 *
 */
public class GeoIPInformation implements Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String country;
	private String language;
	
	/**
	 * Default constructor: required for IsSerializable.
	 */
	public GeoIPInformation() {
		
	}
	/**
	 * @return name of country AT, US, etc.
	 */
	public String getCountry() {
    	return country;
    }
	/**
	 * @param country represents a name of a country e.g. AT, US, etc.
	 */
	public void setCountry(String country) {
    	this.country = country;
    }
	/**
	 * @return language code e.g. en-US, de-AT, etc.
	 */
	public String getLanguage() {
    	return language;
    }
	/**
	 * @param language : e.g. en-US, de-AT, etc. 
	 */
	public void setLanguage(String language) {
    	this.language = language;
    }	

}
