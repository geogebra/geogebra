package org.geogebra.common.move.ggtapi.models;

public class ClientInfo {
	private AuthenticationModel model;
	private String type;
	private String id;
	private int width;
	private int height;
	private String language;
	private String appName;

	public AuthenticationModel getModel() {
		return model;
	}

	public void setModel(AuthenticationModel model) {
		this.model = model;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
