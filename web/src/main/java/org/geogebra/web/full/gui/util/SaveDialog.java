package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.browser.BrowseResources;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;

public class SaveDialog extends SaveFileDialog {
	private ComponentCheckbox templateCheckbox;
	private Image providerImage;
	private Set<Material.Provider> availableProviders;
	private CompDropDown locationDropDown;
	private FlowPanel locationHolder;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param addTemplateCheckBox whether template checkbox should be visible
	 */
	public SaveDialog(AppW app, DialogData dialogData, boolean addTemplateCheckBox) {
		super(app, dialogData, false);
		buildTemplateCheckbox(addTemplateCheckBox);
		buildLocationDropDown();

		if (!app.isMebis() && app.getGoogleDriveOperation() != null) {
			app.getGoogleDriveOperation().initGoogleDriveApi();
		}

		setOnPositiveAction(() -> {
			if (templateCheckbox.isSelected()) {
				setSaveType(Material.MaterialType.ggsTemplate);
				app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggs);
			} else if (addTemplateCheckBox) {
				setSaveType(Material.MaterialType.ggs);
				app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
			}
			app.getSaveController().saveAs(getInputField().getText(),
					getSaveVisibility(), this);
		});
	}

	private void buildTemplateCheckbox(boolean visible) {
		templateCheckbox = new ComponentCheckbox(app.getLocalization(), false,
				"saveTemplate");
		getContentPanel().add(templateCheckbox);
		templateCheckbox.setVisible(visible);
	}

	private void buildLocationDropDown() {
		fillAvailableProviders();

		List<String> providers = new ArrayList<>();
		for (Material.Provider provider : availableProviders) {
			providers.add(provider.getName());
		}
		locationDropDown = new CompDropDown((AppW) app,
				app.getLocalization().getMenu("Location"), providers, 0);
		locationDropDown.setFullWidth(true);
		locationDropDown.addChangeHandler(() -> {
			Material.Provider provider = getSelectedProvider(locationDropDown.getSelectedText());
			providerImage.setResource(getProviderIcon(provider));
			((AppW) app).getFileManager().setFileProvider(provider);
		});

		FlowPanel providerImageHolder = new FlowPanel();
		providerImageHolder.addStyleName("imageHolder");
		providerImage = new Image(getProviderIcon(
				Material.Provider.getProviderForString(locationDropDown.getSelectedText())));
		providerImageHolder.add(providerImage);

		locationHolder = new FlowPanel();
		locationHolder.addStyleName("locationHolder");
		locationHolder.add(providerImageHolder);
		locationHolder.add(locationDropDown);

		getContentPanel().add(locationHolder);
	}

	private void fillAvailableProviders() {
		availableProviders = new HashSet<>();
		availableProviders.add(Material.Provider.TUBE);

		GeoGebraTubeUser user = null;
		if (app.getLoginOperation() != null) {
			user = app.getLoginOperation().getModel().getLoggedInUser();
		}
		if (user != null && user.hasGoogleDrive()
				&& ((AppW) app).getLAF().supportsGoogleDrive()) {
			availableProviders.add(Material.Provider.GOOGLE);
		}
	}

	@Override
	public void show() {
		super.show();
		Material activeMaterial = app.getActiveMaterial();
		templateCheckbox.setSelected(activeMaterial != null && Material.MaterialType.ggsTemplate
				.equals(activeMaterial.getType()));
		updateProviderUI();
	}

	private void updateProviderUI() {
		if (((AppW) app).isOffline()) {
			locationHolder.setVisible(false);
		} else {
			locationHolder.setVisible(availableProviders.size() > 1);
			int idxOfCurrentProvider = 0;
			Material.Provider currentProvider = ((AppW) app).getFileManager().getFileProvider();
			for (Material.Provider provider : availableProviders) {
				if (provider.equals(currentProvider)) {
					break;
				}
				idxOfCurrentProvider++;
			}
			providerImage.setResource(getProviderIcon(currentProvider));
			locationDropDown.setSelectedIndex(idxOfCurrentProvider);
		}
	}

	private Material.Provider getSelectedProvider(String providerStr) {
		return Material.Provider.getProviderForString(providerStr);
	}

	private ImageResource getProviderIcon(Material.Provider provider) {
		switch (provider) {
		case GOOGLE:
			return BrowseResources.INSTANCE.location_drive();
		case LOCAL:
			return BrowseResources.INSTANCE.location_local();
		default:
		case TUBE:
			return BrowseResources.INSTANCE.location_tube();
		}
	}

	@Override
	protected boolean shouldInputPanelBeVisible() {
		return true;
	}
}