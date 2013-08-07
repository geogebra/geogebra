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

public class DefaultLAF implements LookAndFeel {

	private TabletHeaderPanel hp;
	protected TouchApp app;

	public DefaultLAF(TouchApp app) {
		this.app = app;
	}

	@Override
	public void buildHeader(TabletGUI gui, TouchModel touchModel) {
		this.hp = new TabletHeaderPanel(gui, this.app, touchModel);
		gui.setHeaderWidget(this.hp);
		gui.addResizeListener(this.hp);
	}

	public TouchApp getApp() {
		return this.app;
	}

	@Override
	public int getAppBarHeight() {
		return 62;
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
	public int getPanelsHeight() {
		return 122;
	}

	@Override
	public TabletHeaderPanel getTabletHeaderPanel() {
		return this.hp;
	}

	@Override
	public int getToolBarHeight() {
		return 75;
	}

	@Override
	public boolean isMouseDownIgnored() {
		return false;
	}

	@Override
	public void setTitle(String title) {
		this.hp.setTitle(title);
	}

	@Override
	public void stateChanged(boolean b) {
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
	public StandardImageButton setStyleBarShowHideHandler(StandardImageButton button, final StyleBar styleBar) {
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
	public StandardImageButton setStyleBarButtonHandler(final StandardImageButton button, final StyleBar styleBar, final String process) {
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
	public StandardImageButton setOptionalButtonHandler(final StandardImageButton button, final StyleBar styleBar, final OptionType type) {
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
	public StandardImageButton setAlgebraButtonHandler(StandardImageButton button, final TabletGUI gui) {
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();

				gui.toggleAlgebraView();

				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel().enableDisableButtons();
				}
			}
		});

		return button;
	}

	@Override
	public WorksheetHeader buildWorksheetHeader(WorksheetGUI worksheetGUI, TabletGUI tabletGUI) {
		WorksheetHeaderPanel header = new WorksheetHeaderPanel(this.app, worksheetGUI, tabletGUI);
		worksheetGUI.setHeaderWidget(header);
		return header;
	}
}
