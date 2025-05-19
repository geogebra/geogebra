package org.geogebra.web.full.gui.exam;

import org.geogebra.common.util.lang.Language;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ExamSEBDialog extends ComponentDialog {
	private static final String downloadDE = "https://safeexambrowser.org/download_de.html";
	private static final String downloadEN = "https://safeexambrowser.org/download_en.html";

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public ExamSEBDialog(AppW app, DialogData dialogData) {
		super(app, dialogData, true, true);

		addStyleName("examSEBDialog");
		buildContent(app.getVendorSettings());
	}

	private void buildContent(VendorSettings vendorSettings) {
		Label helpText = BaseWidgetFactory.INSTANCE.newPrimaryText(app.getLocalization()
				.getMenu("ExamSEBDialog.LaunchSEBHelp"));
		addDialogContent(helpText);

		FlowPanel downloadSEB = buildDownloadSEBButton(vendorSettings);
		downloadSEB.addDomHandler(event -> {
			Language lang = app.getLocalization().getLanguage();
			String link = lang.equals(Language.German) ? downloadDE : downloadEN;
			Browser.openWindow(link);
		}, ClickEvent.getType());
		addDialogContent(downloadSEB);
	}

	private FlowPanel buildDownloadSEBButton(VendorSettings vendorSettings) {
		FlowPanel downloadSEB = new FlowPanel();
		downloadSEB.addStyleName("downloadSEBLink");

		Label buttonText = new Label(app.getLocalization().getMenu("ExamSEBDialog.DownloadSEB"));
		buttonText.addStyleName("buttonText");
		downloadSEB.add(buttonText);

		NoDragImage buttonImage = new NoDragImage(
				MaterialDesignResources.INSTANCE.open_in_new_tab().withFill(
						vendorSettings.getPrimaryColor().toString()), 16);
		buttonImage.addStyleName("buttonImage");
		downloadSEB.add(buttonImage);

		return downloadSEB;
	}
}
