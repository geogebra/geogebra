package geogebra.common.kernel;
/**
 * Types of path / region parameter behaviours
 * @author kondr
 *
 */
public enum PathRegionHandling {
	/** use closest point when path / region changed*/
	OFF("false"),
	/** use same parameter when path / region changed*/
	ON("true"),
	/** try to be consistent with old behavior (3.2)*/
	AUTO("auto");
	private String xml;
	private PathRegionHandling(String xml){
		this.xml = xml;
	}
	/**
	 * @return value for xml
	 */
	public String getXML(){
		return xml;
	}
	
	public static PathRegionHandling parse(String s){
		if("false".equals(s))
			return OFF;
		if("true".equals(s))
			return ON;
		return AUTO;
	}
}
