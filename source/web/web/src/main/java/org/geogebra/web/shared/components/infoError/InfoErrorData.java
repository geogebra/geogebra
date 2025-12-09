/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
