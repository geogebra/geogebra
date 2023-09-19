package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

import org.geogebra.common.gui.view.table.importer.DataImporterError;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentProgressBar;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

public class DataImportSnackbar extends FlowPanel {

	public DataImportSnackbar(AppW appW, String title) {
		addStyleName("dataImporter");
		buildGui(title);
		appW.getAppletFrame().add(this);
		setWidth(appW.getActiveEuclidianView().getWidth() + "px");
	}

	public DataImportSnackbar(AppW appW, String title, DataImporterError error) {
		addStyleName("dataImporter");
		addStyleName("error");
		buildErrorGui(title, error);
		appW.getAppletFrame().add(this);
		setWidth(appW.getActiveEuclidianView().getWidth() + "px");
	}

	private void buildGui(String title) {
		addTitleHolder(title);

		ComponentProgressBar progressBar = new ComponentProgressBar(true, false);
		add(progressBar);
	}

	private void addTitleHolder(String title) {
		FlowPanel titleHolder = new FlowPanel();
		titleHolder.addStyleName("titleHolder");

		Image dataImg = new Image(MaterialDesignResources.INSTANCE.upload_file().withFill(
				"#E6E6EB").getSafeUri());
		Label titleLbl = new Label(title);
		StandardButton xButton = new StandardButton(MaterialDesignResources.INSTANCE.clear()
				.withFill("#E6E6EB"), 24);

		titleHolder.add(dataImg);
		titleHolder.add(titleLbl);
		titleHolder.add(xButton);
		add(titleHolder);
	}

	private void buildErrorGui(String title, DataImporterError error) {
		addTitleHolder(title);

		FlowPanel errorHolder = new FlowPanel();
		errorHolder.addStyleName("errorHolder");
		Label errorLbl = new Label("erroooooorrrr");
		errorLbl.addStyleName("errorMsg");
		StandardButton tryAgain = new StandardButton("TRY AGAIN");
		errorHolder.add(errorLbl);
		errorHolder.add(tryAgain);

		add(errorHolder);
	}
}
