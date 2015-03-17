package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolViewW;

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
		        geogebra.common.gui.util.SelectionTable.MODE_TEXT, true, false,
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
