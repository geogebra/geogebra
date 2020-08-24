package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.dialog.options.model.CommonOptionsModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.util.FormLabel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxPanel extends OptionPanel implements IComboListener {

	private FormLabel label;
	private ListBox listBox;
	private String title;
	private Localization loc;

	/**
	 * @param loc
	 *            localization
	 * @param title
	 *            title
	 */
	public ListBoxPanel(Localization loc, final String title) {
		this.loc = loc;
		this.title = title;
		listBox = new ListBox();
		label = new FormLabel().setFor(listBox);
		FlowPanel mainWidget = new FlowPanel();
		mainWidget.setStyleName("listBoxPanel");

		mainWidget.add(getLabel());
		mainWidget.add(getListBox());

		getListBox().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				onListBoxChange();
			}
		});
		setWidget(mainWidget);
	}

	MultipleOptionsModel getMultipleModel() {
		return (MultipleOptionsModel) getModel();
	}

	protected void onListBoxChange() {
		if (isCommonOptionsModel()) {
			((CommonOptionsModel) getModel()).applyChanges(
					getListBox().getSelectedValue());
		} else {
			getMultipleModel().applyChanges(getListBox().getSelectedIndex());
		}
	}

	protected boolean isCommonOptionsModel() {
		return getModel() instanceof CommonOptionsModel;
	}

	@Override
	public void setLabels() {
		if (StringUtil.empty(getTitle())) {
			getLabel().setText("");
		} else {
			getLabel().setText(getTitle() + ":");
		}

		int idx = getListBox().getSelectedIndex();
		getListBox().clear();
		if (isCommonOptionsModel()) {
			((CommonOptionsModel) getModel()).fillModes(loc);
		} else {
			getMultipleModel().fillModes(loc);
		}
		getListBox().setSelectedIndex(idx);
	}

	@Override
	public void setSelectedIndex(int index) {
		getListBox().setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
		getListBox().addItem(item);
	}

	/**
	 * @return localized title
	 */
	public String getTitle() {
		String ret = loc.getMenu(title);
		if (ret.equals(title)) {
			// needed for eg Tooltip
			ret = loc.getMenu(title);
		}
		return ret;
	}

	/**
	 * @param title
	 *            title
	 */
	public void setTitle(String title) {
		this.title = title;
		getLabel().setText(title);
	}

	public ListBox getListBox() {
		return listBox;
	}

	public FormLabel getLabel() {
		return label;
	}

	@Override
	public void clearItems() {
		getListBox().clear();
	}
}
