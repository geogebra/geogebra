package org.geogebra.web.full.gui.toolbarpanel.spreadsheet.stylebar;

import org.geogebra.common.spreadsheet.core.SpreadsheetStyleBarModel;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.user.client.ui.FlowPanel;

public class HorizontalAlignmentPopup extends GPopupPanel implements CloseHandler<GPopupPanel> {
	private final IconButton horizontalAlignmentButton;
	private final SpreadsheetStyleBarModel styleBarModel;
	private IconButton lastSelectedButton;
	private IconButton leftAlignButton;
	private IconButton centerAlignButton;
	private IconButton rightAlignButton;

	/**
	 * Horizontal alignment popup with left/center/right alignment buttons.
	 * @param appW {@link AppW}
	 * @param horizontalAlignmentButton anchor button of popup
	 * @param styleBarModel {@link SpreadsheetStyleBarModel}
	 */
	public HorizontalAlignmentPopup(AppW appW, IconButton horizontalAlignmentButton,
			SpreadsheetStyleBarModel styleBarModel) {
		super(true, appW.getAppletFrame(), appW);
		this.horizontalAlignmentButton = horizontalAlignmentButton;
		this.styleBarModel = styleBarModel;
		addStyleName("quickStyleBarPopup");
		buildGui();
		addCloseHandler(this);
	}

	private void buildGui() {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;

		leftAlignButton = buildIconButton(res.horizontal_align_left(),
				SpreadsheetStyling.TextAlignment.LEFT);
		centerAlignButton = buildIconButton(res.horizontal_align_center(),
				SpreadsheetStyling.TextAlignment.CENTERED);
		rightAlignButton = buildIconButton(res.horizontal_align_right(),
				SpreadsheetStyling.TextAlignment.RIGHT);

		FlowPanel buttonList = new FlowPanel();
		buttonList.add(leftAlignButton);
		buttonList.add(centerAlignButton);
		buttonList.add(rightAlignButton);

		lastSelectedButton = leftAlignButton;
		add(buttonList);
		updateState();
	}

	private IconButton buildIconButton(SVGResource svg,
			SpreadsheetStyling.TextAlignment alignment) {
		IconButton button = new IconButton((AppW) getApplication(), new ImageIconSpec(svg),
				"", () -> {});
		button.addStyleName("small");
		button.addFastClickHandler(source -> {
			updateSelection(button);
			styleBarModel.setTextAlignment(alignment);
			hide();
		});
		return button;
	}

	/**
	 * Update ui based on {@link SpreadsheetStyleBarModel#getState()}.
	 */
	public void updateState() {
		lastSelectedButton.setActive(false);
		switch (styleBarModel.getState().textAlignment) {
		case RIGHT:
			updateSelection(rightAlignButton);
			break;
		case CENTERED:
			updateSelection(centerAlignButton);
			break;
		case LEFT:
			updateSelection(leftAlignButton);
			break;
		default:
		}
	}

	private void updateSelection(IconButton button) {
		lastSelectedButton.setActive(false);
		horizontalAlignmentButton.setIcon(button.getIcon());
		button.setActive(true);
		lastSelectedButton = button;
	}

	@Override
	public void onClose(CloseEvent event) {
		horizontalAlignmentButton.setActive(false);
	}
}
