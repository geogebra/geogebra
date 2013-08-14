package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.WorksheetHeader;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeaderPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

public class DefaultLAF implements LookAndFeel {

	private TabletHeaderPanel hp;
	private final TabletGUI gui;
	protected TouchApp app;

	public DefaultLAF(final TouchApp app) {
		this.app = app;
		this.gui = (TabletGUI) app.getTouchGui();
	}

	@Override
	public void buildHeader(final TouchModel touchModel) {
		this.hp = new TabletHeaderPanel(this.app, touchModel);
		this.gui.setHeaderWidget(this.hp);
		this.gui.addResizeListener(this.hp);
	}

	@Override
	public WorksheetHeader buildWorksheetHeader(final WorksheetGUI worksheetGUI) {
		final WorksheetHeaderPanel header = new WorksheetHeaderPanel(this.app,
				worksheetGUI, this.gui);
		worksheetGUI.setHeaderWidget(header);
		return header;
	}

	@Override
	public int getTabletHeaderHeight() {
		return this.hp.getOffsetHeight();
	}

	@Override
	public int getToolBarHeight() {
		return this.gui.getToolBar().getOffsetHeight();
	}

	@Override
	public int getContentWidgetHeight() {
		return Window.getClientHeight() - getToolBarHeight()
				- getTabletHeaderHeight();
	}

	@Override
	public DefaultResources getIcons() {
		return DefaultResources.INSTANCE;
	}

	@Override
	public int getPaddingLeftOfDialog() {
		return 0;
	}

	@Override
	public TabletHeaderPanel getTabletHeaderPanel() {
		return this.hp;
	}

	@Override
	public boolean isMouseDownIgnored() {
		return false;
	}

	@Override
	public void setTitle(final String title) {
		this.hp.setTitle(title);
	}

	@Override
	public void stateChanged(final boolean b) {
		if (this.getTabletHeaderPanel() != null) {
			this.getTabletHeaderPanel().enableDisableButtons();
		}
	}

	@Override
	public boolean isShareSupported() {
		return false;
	}

	@Override
	public void updateUndoSaveButtons() {
		if (this.getTabletHeaderPanel() != null) {
			this.getTabletHeaderPanel().enableDisableButtons();
		}
	}

	@Override
	public StandardImageButton setStyleBarShowHideHandler(
			final StandardImageButton button, final StyleBar styleBar) {
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
				styleBar.showHide();
			}
		});

		return button;
	}

	@Override
	public StandardImageButton setStyleBarButtonHandler(
			final StandardImageButton button, final StyleBar styleBar,
			final String process) {
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();

				styleBar.onStyleBarButtonEvent(button, process);
				DefaultLAF.this.getApp().setUnsaved();
				TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
			}
		});

		return button;
	}

	@Override
	public StandardImageButton setOptionalButtonHandler(
			final StandardImageButton button, final StyleBar styleBar,
			final OptionType type) {
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {

				event.preventDefault();
				event.stopPropagation();
				styleBar.onOptionalButtonEvent(button, type);
			}
		});

		return button;
	}

	@Override
	public StandardImageButton setAlgebraButtonHandler(
			final StandardImageButton button, final TabletGUI gui) {
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();

				gui.toggleAlgebraView();

				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel()
							.enableDisableButtons();
				}
			}
		});

		return button;
	}

	public TouchApp getApp() {
		return this.app;
	}
}
