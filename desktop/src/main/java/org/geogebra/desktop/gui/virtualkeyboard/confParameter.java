package org.geogebra.desktop.gui.virtualkeyboard;

public class confParameter {
	private String Name = null;
	private String Param = null;

	/**
	 * This method returns confParameter.Name
	 *
	 * @return java.lang.String
	 */

	public String getName() {
		return this.Name;
	}

	/**
	 * This method sets confParameter.Name
	 */
	public void setName(String Name) {
		this.Name = Name;
	}

	/**
	 * This method returns confParameter.Parameter
	 *
	 * @return java.lang.String
	 */
	public String getParam() {
		return this.Param;
	}

	/**
	 * This method sets confParameter.Parameter
	 */
	public void setParam(String Param) {
		this.Param = Param;
	}
}
