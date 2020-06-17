package org.geogebra.web.full.gui.layout.scientific;

import java.util.Arrays;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.factory.ScientificPropertiesFactory;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentDropDown.DropDownSelectionCallback;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Csilla
 *
 *         Settings view of scientific calculator
 *
 */
public class ScientificSettingsView extends AnimatingPanel implements FastClickHandler {

	private final AppW app;
	private HeaderView headerView;
	private boolean isOpen;
	private Localization localization;
	private ScrollPanel settingsScrollPanel;

	/**
	 * Build and style settings view for sci calc
	 *
	 * @param app
	 *            application
	 */
	public ScientificSettingsView(AppW app) {
		this.addStyleName("scientificSettingsView");
		this.app = app;
		isOpen = true;
		localization = app.getLocalization();
		setAnimator(new SettingsAnimator(app.getAppletFrame(), this));
		createHeader();
		createContent();
	}

	private void createHeader() {
		headerView = new HeaderView();
		headerView.setCaption(localization.getMenu("Settings"));
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(this);

		setHeaderWidget(headerView);
		resizeHeader();
	}

	private void createContent() {
		settingsScrollPanel = new ScrollPanel();
		settingsScrollPanel.addStyleName("settingsPanelScientificNoHeader");

		FlowPanel contentPanel = new FlowPanel();
		PropertiesArray properties =
				new ScientificPropertiesFactory()
						.createGeneralProperties(app, localization, null);

		buildPropertiesPanel(properties, contentPanel);
		settingsScrollPanel.add(contentPanel);
		setContentWidget(settingsScrollPanel);
	}

	private void buildPropertiesPanel(PropertiesArray properties, FlowPanel panel) {
		for (Property property : properties.getProperties()) {
			Widget cell = createPropertyCell(property);
			if (cell != null) {
				panel.add(cell);
			}
		}
	}

	private void updateGUI() {
		updateHeader();
		createContent();
	}

	private void updateHeader() {
		headerView.setCaption(localization.getMenu("Settings"));
	}

	private Widget createPropertyCell(Property property) {
		if (property instanceof EnumerableProperty) {
			final EnumerableProperty enumerableProperty = (EnumerableProperty) property;
			final ComponentDropDown selector = new ComponentDropDown(app);

			selector.setTitleText(enumerableProperty.getName());
			selector.setElements(Arrays.asList(enumerableProperty.getValues()));
			selector.setSelected(enumerableProperty.getIndex());
			selector.setDropDownSelectionCallback(new DropDownSelectionCallback() {
				@Override
				public void onSelectionChanged(int index) {
					enumerableProperty.setIndex(index);
				}
			});
			return selector;

		}

		return null;
	}

	@Override
	public void onClick(Widget source) {
		if (source == headerView.getBackButton()) {
			updateAnimateOutStyle();
			CSSEvents.runOnAnimation(new Runnable() {
				@Override
				public void run() {
					close();
				}
			}, getElement(), getAnimateOutStyle());
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}

	/**
	 * @return true if settings view is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            true if open settings, false otherwise
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Override
	public void resizeTo(int width, int height) {
		resizeHeader();
		resizeContent();
	}

	@Override
	public void onResize() {
		super.onResize();
		resizeHeader();
		resizeContent();
	}

	private void resizeHeader() {
		boolean smallScreen = app.getAppletFrame()
				.hasSmallWindowOrCompactHeader();
		headerView.resizeTo(smallScreen);
	}

	private void resizeContent() {
		boolean isSmallScreen = app.getAppletFrame()
				.hasSmallWindowOrCompactHeader();

		Dom.toggleClass(this, "scientificSettingsViewSmall", isSmallScreen);
		Dom.toggleClass(settingsScrollPanel, "settingsPanelScientificSmallScreen", isSmallScreen);
	}

	@Override
	public void setLabels() {
		updateGUI();
	}
}
