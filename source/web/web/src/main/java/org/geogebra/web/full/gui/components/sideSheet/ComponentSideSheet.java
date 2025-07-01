package org.geogebra.web.full.gui.components.sideSheet;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class ComponentSideSheet extends FlowPanel implements SetLabels {
	private final AppW appW;
	private final SideSheetData data;
	private Label titleLabel;
	private FlowPanel contentPanel;
	private StandardButton positiveButton;
	private StandardButton negativeButton;
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
		addStyleName("sideSheet");
		buildSideSheet(addBackButton);
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

		titleLabel = new Label(appW.getLocalization().getMenu(data.getTitleTransKey()));
		titleLabel.addStyleName("title");
		titlePanel.add(titleLabel);

		IconButton closeButton = new IconButton(appW, this::onClose,
				new ImageIconSpec(GuiResourcesSimple.INSTANCE.close()), "Close");
		closeButton.addStyleName("closeBtn");
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

	private boolean hasButtonPanel() {
		return data.hasPositiveBtn() && data.hasNegativeBtn();
	}

	private void onClose() {
		onClose.run();
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

	@Override
	public void setLabels() {
		titleLabel.setText(appW.getLocalization().getMenu(data.getTitleTransKey()));
		if (positiveButton != null) {
			positiveButton.setText(appW.getLocalization().getMenu(data.getPositiveBtnTransKey()));
		}
		if (negativeButton != null) {
			negativeButton.setText(appW.getLocalization().getMenu(data.getNegativeBtnTransKey()));
		}
	}
}
