package org.geogebra.desktop.gui.dialog;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.DynamicCaptionModel;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.main.AppD;

public class DynamicCaptionPanelD extends OptionPanel implements SetLabels, IComboListener {
	private final EnableDynamicCaptionPanel enableDynamicCaption;
	private final ComboPanel captions;

	public DynamicCaptionPanelD(AppD app, AutoCompleteTextFieldD textField, UpdateTabs tabs) {
		captions = new ComboPanel(app, "");
		enableDynamicCaption = new EnableDynamicCaptionPanel(app, textField,
				captions, tabs);
		DynamicCaptionModel dynamicCaptionModel = new DynamicCaptionModel(app);
		captions.setModel(dynamicCaptionModel);
		dynamicCaptionModel.setListener(this);
		add(enableDynamicCaption);
		add(captions);
		setLayout(new SpringLayout());
	}

	@Override
	public void setLabels() {
		enableDynamicCaption.setLabels();
		captions.setLabels();
	}

	@Override
	public void updateFonts() {
		enableDynamicCaption.updateFonts();
		captions.updateFonts();
	}

	@Override
	public void setSelectedIndex(int index) {
		captions.setSelectedIndex(index);
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		JPanel checkboxOrNull = enableDynamicCaption.updatePanel(geos);
		captions.rebuildItems();
		captions.updatePanel(geos);
		updateLayout();
		return checkboxOrNull == null ? null : this;
	}

	private void updateLayout()	{
		int rows = enableDynamicCaption.isSelected() ? 2 : 1;
		SpringUtilities.makeCompactGrid(this, rows, 1,
				0,0,5,5);
		validate();
	}

	@Override
	public void addItem(String plain) {
		captions.addItem(plain);
	}

	@Override
	public void clearItems() {
		captions.clearItems();
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {

	}
}
