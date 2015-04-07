package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.AppResourcesConverter;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.StyleBarW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolViewW;

import com.google.gwt.resources.client.ImageResource;

public class ConstructionProtocolStyleBarW extends StyleBarW {

	private ConstructionProtocolViewW cpView;

	public ConstructionProtocolStyleBarW(ConstructionProtocolViewW cpw, AppW app) {
		super(app, App.VIEW_CONSTRUCTION_PROTOCOL);
		cpView = cpw;
		// addButtons();
		addViewButton();
	}

	protected void addButtons() {
		String[] tableColumns = {};
		boolean[] show = {};
		for (int k = 0; k < cpView.getData().getColumnCount(); k++) {
			tableColumns[k] = cpView.getData().getColumns()[k]
			        .getTranslatedTitle();
			show[k] = cpView.getData().getColumns()[k].getInitShow();
		}
		PopupMenuButton btnColumns = new PopupMenuButton(app,
		        ImageOrText.convert(tableColumns), -1, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT, true, false,
		        show);
		for (int k = 0; k < cpView.getData().getColumnCount(); k++) {
			if (cpView.getData().getColumns()[k].getInitShow()) {
				btnColumns.changeMultiSelection(k, true);
			}
		}

		ImageResource ic = AppResources.INSTANCE.header_column();
		AppResourcesConverter.setIcon(ic, btnColumns);
		// btnColumns.addPopupHandler(this);
		btnColumns.setKeepVisible(false);
		add(btnColumns);
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}
}
