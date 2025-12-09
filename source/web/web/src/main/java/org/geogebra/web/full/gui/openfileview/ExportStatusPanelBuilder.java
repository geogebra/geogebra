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

package org.geogebra.web.full.gui.openfileview;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

public class ExportStatusPanelBuilder {

	/**
	 * @return panel showing information for status {@link ExportStatus#PENDING}
	 * and {@link ExportStatus#IN_PROGRESS}
	 */
	public static FlowPanel getPendingInProgressPanel() {
		FlowPanel statusPanel = new FlowPanel();
		statusPanel.addStyleName("exportStatusPanel");

		FlowPanel messagePanel = getStatusMessagePanel(ExportStatus.PENDING);
		statusPanel.add(getStatusImage(ExportStatus.PENDING));
		statusPanel.add(messagePanel);

		return statusPanel;
	}

	/**
	 * @param downloadFiles starts downloading the prepared zip package
	 * @return panel showing information for status {@link ExportStatus#AVAILABLE}
	 */
	public static FlowPanel getAvailablePanel(Runnable downloadFiles) {
		FlowPanel statusPanel = new FlowPanel();
		statusPanel.addStyleName("exportStatusPanel");

		FlowPanel messagePanel = getStatusMessagePanel(ExportStatus.AVAILABLE);
		StandardButton downloadButton = new StandardButton("Jetzt herunterladen");
		downloadButton.addStyleName("dialogContainedButton");
		downloadButton.addFastClickHandler(source -> downloadFiles.run());

		statusPanel.add(getStatusImage(ExportStatus.AVAILABLE));
		statusPanel.add(messagePanel);
		statusPanel.add(downloadButton);

		return statusPanel;

	}

	/**
	 * @return panel showing information for status {@link ExportStatus#ERROR}
	 */
	public static FlowPanel getErrorPanel() {
		FlowPanel statusPanel = new FlowPanel();
		statusPanel.addStyleName("exportStatusPanel error");

		FlowPanel messagePanel = getStatusMessagePanel(ExportStatus.ERROR);
		statusPanel.add(getStatusImage(ExportStatus.ERROR));
		statusPanel.add(messagePanel);

		return statusPanel;
	}

	/**
	 * Build image for status
	 * @param status {@link ExportStatus}
	 * @return colored image
	 */
	private static Image getStatusImage(ExportStatus status) {
		SVGResource statusIcon = ExportStatus.getStatusIcon(status);
		if (statusIcon == null) {
			return null;
		}
		Image statusImage = new NoDragImage(statusIcon.getSafeUri().asString());
		statusImage.addStyleName("statusImage");

		return statusImage;
	}

	/**
	 * Build message panel holding main message and help
	 * @param status {@link ExportStatus}
	 * @return message panel
	 */
	private static FlowPanel getStatusMessagePanel(ExportStatus status) {
		FlowPanel messagePanel = new FlowPanel();
		messagePanel.addStyleName("messagePanel");

		String messageTxt = ExportStatus.getStatusMessage(status);
		String helpTxt = ExportStatus.getStatusHelp(status);
		Label message = BaseWidgetFactory.INSTANCE.newPrimaryText(messageTxt, "message");
		Label help = BaseWidgetFactory.INSTANCE.newSecondaryText(helpTxt, "help");

		messagePanel.add(message);
		messagePanel.add(help);
		return messagePanel;
	}
}
