package org.geogebra.web.full.gui.properties.ui.panel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.impl.graphics.DimensionRatioProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class DimensionPanel extends ComponentExpandableList implements SetLabels {
	private final AppW appW;
	private final PropertiesPanelAdapter propertiesPanelAdapter;
	private Label dimensionLabel;
	private ComponentInputField xRatio;
	private ComponentInputField yRatio;
	private IconButton lockRatio;
	private Label ratioLabel;
	private String ratioLabelKey;

	/**
	 * Create a dimension panel based on property
	 * @param appW application
	 * @param propertiesPanelAdapter {@link PropertiesPanelAdapter}
	 * @param property
	 * {@link org.geogebra.common.properties.impl.graphics.DimensionPropertiesCollection}
	 */
	public DimensionPanel(AppW appW, PropertiesPanelAdapter propertiesPanelAdapter,
			PropertyCollection<?> property) {
		super(appW, null, property.getRawName());
		this.appW = appW;
		this.propertiesPanelAdapter = propertiesPanelAdapter;
		buildGUI(property);
	}

	private void buildGUI(PropertyCollection<?> property) {
		addStyleName("dimensionPanel");

		FlowPanel minMaxPanel = new FlowPanel();
		minMaxPanel.addStyleName("minMaxPanel");
		for (Property prop : property.getProperties()) {
			if (prop instanceof DimensionRatioProperty) {
				addToContent(buildRatioPanel((DimensionRatioProperty) prop));
				addToContent(dimensionLabel = new Label(appW.getLocalization()
						.getMenu("Dimensions")));
			} else {
				Widget minMaxItem = propertiesPanelAdapter.getWidget(prop);
				minMaxItem.addStyleName("minMaxItem");
				minMaxPanel.add(minMaxItem);
			}
		}
		addToContent(minMaxPanel);
	}

	private FlowPanel buildRatioPanel(DimensionRatioProperty property) {
		xRatio = new ComponentInputField(appW, "", "xAxis", "", property.getXRatio());
		xRatio.getTextField().getTextComponent().addBlurHandler(
				evt -> property.setXRatio(xRatio.getText()));
		xRatio.getTextField().getTextComponent().addKeyHandler(e -> {
			if (e.isEnterKey()) {
				property.setXRatio(xRatio.getText());
			}
		});
		xRatio.setDisabled(!property.isRatioEnabled());

		yRatio = new ComponentInputField(appW, "", "yAxis", "", property.getYRatio());
		yRatio.getTextField().getTextComponent().addBlurHandler(
				evt -> property.setYRatio(yRatio.getText()));
		yRatio.getTextField().getTextComponent().addKeyHandler(e -> {
			if (e.isEnterKey()) {
				property.setYRatio(yRatio.getText());
			}
		});
		yRatio.setDisabled(!property.isRatioEnabled());

		SVGResource icon = property.isRatioLocked()
				? MaterialDesignResources.INSTANCE.lock_black()
				: MaterialDesignResources.INSTANCE.lock_open_black();
		lockRatio = new IconButton(appW, null, new ImageIconSpec(icon),
				getLockedUnlockedKey(property));
		lockRatio.addFastClickHandler(source -> {
			property.setRatioLocked(!property.isRatioLocked());
			boolean locked = property.isRatioLocked();
			lockRatio.setIcon(locked
					? MaterialDesignResources.INSTANCE.lock_black()
					: MaterialDesignResources.INSTANCE.lock_open_black());
			lockRatio.setTitle(appW.getLocalization().getMenu(getLockedUnlockedKey(property)));
			xRatio.setDisabled(locked);
			yRatio.setDisabled(locked);
		});

		FlowPanel panel = new FlowPanel();
		ratioLabelKey = property.getRawName();
		panel.add(ratioLabel = new Label(appW.getLocalization().getMenu(ratioLabelKey)));
		FlowPanel ratioPanel = new FlowPanel();
		ratioPanel.addStyleName("ratioPanel");
		ratioPanel.add(xRatio);
		ratioPanel.add(new Label(":"));
		ratioPanel.add(yRatio);
		ratioPanel.add(lockRatio);

		panel.add(ratioPanel);
		return panel;
	}

	private String getLockedUnlockedKey(DimensionRatioProperty property) {
		return property.isRatioLocked() ? "UnlockRatio" : "LockRatio";
	}

	@Override
	public void setLabels() {
		super.setLabels();
		dimensionLabel.setText(appW.getLocalization().getMenu("Dimensions"));
		xRatio.setLabels();
		yRatio.setLabels();
		lockRatio.setLabels();
		ratioLabel.setText(appW.getLocalization().getMenu(ratioLabelKey));
	}
}
