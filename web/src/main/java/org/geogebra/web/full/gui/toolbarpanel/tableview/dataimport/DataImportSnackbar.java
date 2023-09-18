package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

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
	}

	private void buildGui(String title) {
		FlowPanel titleHolder = new FlowPanel();
		titleHolder.addStyleName("titleHolder");

		Image dataImg = new Image(MaterialDesignResources.INSTANCE.upload_file().getSafeUri());
		Label titleLbl = new Label(title);
		StandardButton xButton = new StandardButton(MaterialDesignResources.INSTANCE.clear(),
				24);

		titleHolder.add(dataImg);
		titleHolder.add(titleLbl);
		titleHolder.add(xButton);

		ComponentProgressBar progressBar = new ComponentProgressBar(true, false);
		add(titleHolder);
		add(progressBar);
	}
}
