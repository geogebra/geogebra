package org.geogebra.web.shared.components.infoError;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public class InfoErrorData {
	private String title;
	private String subtext;
	private String actionButtonText;
	private SVGResource img;

	/**
	 * info/error date constructor
	 * @param title - title
	 * @param subtext - error/warning message
	 * @param actionButtonText - action button text
	 */
	public InfoErrorData(String title, String subtext, String actionButtonText, SVGResource img) {
		setTitle(title);
		setSubtext(subtext);
		setActionButtonText(actionButtonText);
		this.img = img;
	}

	/**
	 * info/error date constructor without button
	 * @param title - title
	 * @param subtext - error/warning message
	 */
	public InfoErrorData(String title, String subtext) {
		this(title, subtext, null, MaterialDesignResources.INSTANCE.mow_lightbulb());
	}

	private void setTitle(String title) {
		this.title = title;
	}

	private void setSubtext(String subtext) {
		this.subtext = subtext;
	}

	private void setActionButtonText(String actionButtonText) {
		this.actionButtonText = actionButtonText;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtext() {
		return subtext;
	}

	public String getActionButtonText() {
		return actionButtonText;
	}

	public ResourcePrototype getImage() {
		return img;
	}
}
