package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.ui.FlowPanel;

public class LabelValuePanel extends FlowPanel
		implements CloseHandler<GPopupPanel>, SetLabels {
	private final AppW appW;
	private final StringPropertyCollection<?> nameProperty;
	private final List<GeoElement> geos;

	private ComponentInputField tfName;

	/**
	 * Constructor
	 * @param appW - application
	 * @param nameProperty - name property
	 */
	public LabelValuePanel(AppW appW, StringPropertyCollection<?> nameProperty,
			List<GeoElement> geos) {
		super();
		this.appW = appW;
		this.nameProperty = nameProperty;
		this.geos = geos;

		createDialog();
	}

	private void createDialog() {
		tfName = new ComponentInputField(appW, null, nameProperty.getRawName(),
				null, nameProperty.getValue(),
				-1, null, false);
		if (geos.size() == 1) {
			tfName.getTextField().getTextComponent().setAutoComplete(false);
			tfName.getTextField().getTextComponent().enableGGBKeyboard();

			tfName.getTextField().getTextComponent().addBlurHandler(event -> onEnter());
			tfName.getTextField().getTextComponent().addKeyHandler(e -> {
				if (e.isEnterKey()) {
					onEnter();
				}
			});
			add(tfName);
			init();
		}
		setLabels();
	}

	/**
	 * Submit the change
	 */
	protected void onEnter() {
		nameProperty.setValue(tfName.getText());
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		nameProperty.setValue(tfName.getText());
	}

	@Override
	public void setLabels() {
		tfName.setLabels();
	}

	private void init() {
		tfName.setInputText(nameProperty.getValue());
		tfName.focusDeferred();
	}

}
