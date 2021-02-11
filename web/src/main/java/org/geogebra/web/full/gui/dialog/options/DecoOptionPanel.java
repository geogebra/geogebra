package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.NumberOptionsModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class DecoOptionPanel extends OptionPanel
		implements IComboListener {
	private Label decoLabel;
	private PopupMenuButtonW decoPopup;
	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public DecoOptionPanel(AppW app) {
		this.app = app;
	}

	@Override
	public void setLabels() {
		decoLabel.setText(app.getLocalization().getMenu("Decoration") + ":");
	}

	/**
	 * @param iconArray
	 *            icons
	 * @param model
	 *            model
	 */
	public void init(ImageOrText[] iconArray,
			final NumberOptionsModel model) {
		FlowPanel mainWidget = new FlowPanel();
		decoLabel = new Label();
		mainWidget.add(decoLabel);

		decoPopup = new PopupMenuButtonW(app, iconArray, -1, 1, SelectionTable.MODE_ICON) {
			@Override
			public void handlePopupActionEvent() {
				super.handlePopupActionEvent();
				int idx = getSelectedIndex();
				model.applyChanges(idx);
			}
		};
		decoPopup.setKeepVisible(false);
		mainWidget.add(decoPopup);
		setWidget(mainWidget);
	}

	@Override
	public void setSelectedIndex(int index) {
		decoPopup.setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
		// do nothing
	}

	@Override
	public void clearItems() {
		// do nothing
	}
}
