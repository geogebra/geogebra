package org.geogebra.web.full.gui.properties.ui.panel;

import static org.geogebra.common.properties.PropertyView.*;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class DimensionRatioPanel extends FlowPanel {
	private final AppW appW;
	private final PropertiesPanelAdapter propertiesPanelAdapter;

	/**
	 * Create a dimension ratio editor panel
	 * @param appW application
	 * @param propertiesPanelAdapter {@link PropertiesPanelAdapter}
	 * @param dimensionRatioEditor {@link DimensionRatioEditor}
	 */
	public DimensionRatioPanel(AppW appW, PropertiesPanelAdapter propertiesPanelAdapter,
			DimensionRatioEditor dimensionRatioEditor) {
		this.appW = appW;
		this.propertiesPanelAdapter = propertiesPanelAdapter;
		buildGUI(dimensionRatioEditor);
	}

	private void buildGUI(DimensionRatioEditor dimensionRatioEditor) {
		IconButton lockRatio = createLockRatio(dimensionRatioEditor);

		add(new Label(appW.getLocalization().getMenu(dimensionRatioEditor.getLabel())));
		FlowPanel ratioPanel = new FlowPanel();
		ratioPanel.addStyleName("ratioPanel");
		ratioPanel.add(propertiesPanelAdapter.getWidget(
				dimensionRatioEditor.getLeadingTextField()));
		ratioPanel.add(new Label(":"));
		ratioPanel.add(propertiesPanelAdapter.getWidget(
				dimensionRatioEditor.getTrailingTextField()));
		ratioPanel.add(lockRatio);

		add(ratioPanel);
	}

	private IconButton createLockRatio(DimensionRatioEditor dimensionRatioEditor) {
		SVGResource icon = dimensionRatioEditor.isLocked()
				? MaterialDesignResources.INSTANCE.lock_black()
				: MaterialDesignResources.INSTANCE.lock_open_black();
		IconButton lockRatio = new IconButton(appW, null, new ImageIconSpec(icon),
				getLockedUnlockedKey(dimensionRatioEditor));
		lockRatio.addFastClickHandler(source -> {
			dimensionRatioEditor.setLocked(!dimensionRatioEditor.isLocked());
			lockRatio.setIcon(dimensionRatioEditor.isLocked()
					? MaterialDesignResources.INSTANCE.lock_black()
					: MaterialDesignResources.INSTANCE.lock_open_black());
			lockRatio.setTitle(appW.getLocalization().getMenu(
					getLockedUnlockedKey(dimensionRatioEditor)));
		});
		lockRatio.getElement().setAttribute("tooltip-position", "right");
		return lockRatio;
	}

	private String getLockedUnlockedKey(DimensionRatioEditor dimensionRatioEditor) {
		return dimensionRatioEditor.isLocked() ? "UnlockRatio" : "LockRatio";
	}
}
