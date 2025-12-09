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

package org.geogebra.web.full.gui;

import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import jsinterop.base.JsPropertyMap;

public class AssignDialog extends ComponentDialog {
	private final ShareControllerW materialProvider;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public AssignDialog(AppW app,
			DialogData dialogData, ShareControllerW materialProvider) {
		super(app, dialogData, true, true);
		this.materialProvider = materialProvider;
		addAssignButton("assignDialog.lesson", "assignDialog.lesson.description",
				"https://www.geogebra.org/classroom/create?id=%0",
				MaterialDesignResources.INSTANCE.geogebra_color());
		addAssignButton("assignDialog.google", "assignDialog.google.description",
				"https://www.geogebra.org/classroom/embed/google-classroom/share"
				+ "?material=%1&backUrl=https://www.geogebra.org/m/%0",
				MaterialDesignResources.INSTANCE.google_classroom());
	}

	private void addAssignButton(String title, String subtitle, String pattern, SVGResource icon) {
		FlowPanel classroom = new FlowPanel();
		classroom.addStyleName("assignOption");
		Dom.addEventListener(classroom.getElement(), "click",
				click -> openNewTab(pattern));
		Label image = new Label();
		image.setStyleName("icon");
		image.getElement().getStyle().setBackgroundImage("url("
				+ icon.getSafeUri().asString() + ")");
		FlowPanel description = new FlowPanel();
		classroom.add(image);
		classroom.add(description);
		Localization loc = getApplication().getLocalization();
		Label titleLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(loc.getMenu(title),
				"title");
		description.add(titleLabel);
		Label subtitleLabel = BaseWidgetFactory.INSTANCE.newSecondaryText(
				loc.getMenu(subtitle), "subtitle");
		description.add(subtitleLabel);
		addDialogContent(classroom);
	}

	private void openNewTab(String pattern) {
		hide();
		materialProvider.afterSaved((material) -> {
			String url = pattern
					.replace("%0", Global.encodeURIComponent(material.getSharingKey()))
					.replace("%1", Global.encodeURIComponent(toJson(material)));
			DomGlobal.window.open(url);
			materialProvider.setAssign(false);
		});
	}

	private String toJson(Material material) {
		JsPropertyMap<Object> materialProps = JsPropertyMap.of("id", material.getSharingKey(),
				"title", material.getTitle());
		return Global.JSON.stringify(materialProps);
	}
}
