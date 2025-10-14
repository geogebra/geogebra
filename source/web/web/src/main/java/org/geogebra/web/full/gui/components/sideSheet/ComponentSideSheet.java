package org.geogebra.web.full.gui.components.sideSheet;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class ComponentSideSheet extends FlowPanel implements SetLabels {
	private final AppW appW;
	private final SideSheetData data;
	private String titleTransKey;
	private Label titleLabel;
	private FlowPanel contentPanel;
	private StandardButton positiveButton;
	private StandardButton negativeButton;
	private IconButton closeButton;
	private final Runnable onClose;

	/**
	 * Side sheet with all optional elements (e.g. back button, buttons)
	 * @param appW {@link AppW}
	 * @param data {@link SideSheetData}
	 * @param addBackButton whether back button should be added in the title panel
	 * @param onClose close button handler
	 */
	public ComponentSideSheet(AppW appW, SideSheetData data, boolean addBackButton,
			Runnable onClose) {
		this.appW = appW;
		this.data = data;
		this.onClose = onClose;
		this.titleTransKey = data.getTitleTransKey();
		addStyleName("sideSheet");
		buildSideSheet(addBackButton);
		setAccessibilityProperties();
	}

	/**
	 * Side sheet without back button in title panel.
	 * @param appW {@link AppW}
	 * @param data {@link SideSheetData}
	 * @param onClose close button handler
	 */
	public ComponentSideSheet(AppW appW, SideSheetData data, Runnable onClose) {
		this(appW, data, false, onClose);
	}

	private void buildSideSheet(boolean addBackButton) {
		buildTitlePanel(addBackButton);
		buildContentPanel();
		buildButtonPanel();
	}

	private void buildTitlePanel(boolean addBackButton) {
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("titlePanel");

		if (addBackButton) {
			titlePanel.addStyleName("withBackBtn");
			IconButton backButton = new IconButton(appW, this::onBack, new ImageIconSpec(
					GuiResourcesSimple.INSTANCE.arrow_back()), "Back");
			backButton.addStyleName("backBtn");
			titlePanel.add(backButton);
		}

		titleLabel = new Label(appW.getLocalization().getMenu(titleTransKey));
		titleLabel.addStyleName("title");
		titlePanel.add(titleLabel);

		closeButton = new IconButton(appW, this::onClose,
				new ImageIconSpec(GuiResourcesSimple.INSTANCE.close()), "Close");
		closeButton.addStyleName("closeBtn");
		closeButton.getElement().setAttribute("tooltip-position", "right");
		closeButton.setTabIndex(0);
		new FocusableWidget(AccessibilityGroup.SETTINGS_CLOSE_BUTTON,
				AccessibilityGroup.ViewControlId.SETTINGS_VIEW, closeButton) {
			@Override
			public void focus(Widget widget) {
				closeButton.addStyleName("keyboardFocus");
				closeButton.getElement().focus();
			}
		}.attachTo(appW);
		titlePanel.add(closeButton);

		add(titlePanel);
	}

	private void buildContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("contentPanel");
		if (hasButtonPanel()) {
			contentPanel.addStyleName("withButtonPanel");
		}
		add(contentPanel);
	}

	private void buildButtonPanel() {
		if (!hasButtonPanel()) {
			return;
		}

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");

		initPositiveButton();
		initNegativeButton();

		buttonPanel.add(positiveButton);
		buttonPanel.add(negativeButton);
		add(buttonPanel);
	}

	private void initPositiveButton() {
		positiveButton = new StandardButton(appW.getLocalization().getMenu(
				data.getPositiveBtnTransKey()));
		positiveButton.setStyleName("dialogContainedButton");
	}

	private void initNegativeButton() {
		negativeButton = new StandardButton(appW.getLocalization().getMenu(
				data.getNegativeBtnTransKey()));
		negativeButton.setStyleName("materialOutlinedButton");
	}

	/**
	 * Adds elements to the content panel
	 * @param widget ui element
	 */
	public void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	/**
	 * Clears the content panel
	 */
	public void clearContent() {
		contentPanel.clear();
	}

	private boolean hasButtonPanel() {
		return data.hasPositiveBtn() && data.hasNegativeBtn();
	}

	/**
	 * side sheet close handler
	 */
	public void onClose() {
		onClose.run();
		appW.getAccessibilityManager().focusAnchor();
	}

	private void onBack() {
		// to fill later, when it's needed
	}

	/**
	 * Attach positive action handler
	 * @param positiveHandler handler
	 */
	public void addPositiveButtonRunnable(Runnable positiveHandler) {
		if (positiveButton != null) {
			positiveButton.addFastClickHandler((source) -> positiveHandler.run());
		}
	}

	/**
	 * Attach negative action handler
	 * @param negativeHandler handler
	 */
	public void addNegativeButtonRunnable(Runnable negativeHandler) {
		if (negativeButton != null) {
			negativeButton.addFastClickHandler(source -> negativeHandler.run());
		}
	}

	/**
	 * @param titleTransKey title translation key
	 */
	public void setTitleTransKey(String titleTransKey) {
		this.titleTransKey = titleTransKey;
		setLabels();
	}

	private void setAccessibilityProperties() {
		AriaHelper.setRole(this, "complementary");
		AriaHelper.setLabel(this, appW.getLocalization().getMenu(titleTransKey));
	}

	/**
	 * focus the close button
	 */
	public void focus() {
		closeButton.getElement().focus();
	}

	@Override
	public void setLabels() {
		titleLabel.setText(appW.getLocalization().getMenu(titleTransKey));
		AriaHelper.setLabel(this, appW.getLocalization().getMenu(titleTransKey));
		if (positiveButton != null) {
			positiveButton.setText(appW.getLocalization().getMenu(data.getPositiveBtnTransKey()));
		}
		if (negativeButton != null) {
			negativeButton.setText(appW.getLocalization().getMenu(data.getNegativeBtnTransKey()));
		}
		closeButton.setLabels();
	}
}
