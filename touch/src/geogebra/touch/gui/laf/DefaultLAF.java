package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.touch.gui.elements.header.WorksheetHeaderPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;

import com.google.gwt.user.client.Window;

public class DefaultLAF implements LookAndFeel {

	private TabletHeaderPanel hp;
	private BrowseHeaderPanel bhp;
	private final TabletGUI gui;
	protected TouchApp app;

	public DefaultLAF(final TouchApp app) {
		this.app = app;
		this.gui = (TabletGUI) app.getTouchGui();
	}

	@Override
	public void buildTabletHeader(final TouchModel touchModel) {
		this.hp = new TabletHeaderPanel(this.app, touchModel);
		this.gui.setHeaderWidget(this.hp);
		this.gui.addResizeListener(this.hp);
	}

	@Override
	public WorksheetHeader buildWorksheetHeader(final WorksheetGUI worksheetGUI) {
		final WorksheetHeaderPanel header = new WorksheetHeaderPanel(this.app,
				this.gui);
		worksheetGUI.setHeaderWidget(header);
		return header;
	}

	@Override
	public BrowseHeaderPanel buildBrowseHeader(BrowseGUI browseGUI) {
		this.bhp = new BrowseHeaderPanel(this.app.getLocalization(), browseGUI,
				this.app.getOfflineOperation());
		browseGUI.setHeaderWidget(this.bhp);
		browseGUI.addResizeListener(this.bhp);
		return this.bhp;
	}

	@Override
	public int getTabletHeaderHeight() {
		return this.hp.getOffsetHeight();
	}

	@Override
	public int getBrowseHeaderHeight() {
		return this.bhp.getOffsetHeight();
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

	public TouchApp getApp() {
		return this.app;
	}

	@Override
	public FastButton setStyleBarShowHideHandler(final FastButton button,
			final StyleBar styleBar) {
		button.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				styleBar.showHide();
			}
		});

		return button;
	}

	@Override
	public FastButton setStyleBarButtonHandler(final FastButton button,
			final StyleBar styleBar, final String process) {
		button.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				styleBar.onStyleBarButtonEvent(button, process);
				DefaultLAF.this.getApp().setUnsaved();
				TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
			}
		});

		return button;
	}

	@Override
	public FastButton setOptionalButtonHandler(final FastButton button,
			final StyleBar styleBar, final OptionType type) {
		button.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				styleBar.onOptionalButtonEvent(button, type);
			}
		});

		return button;
	}

	@Override
	public FastButton setAlgebraButtonHandler(final FastButton button,
			final TabletGUI gui) {
		button.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {

				gui.toggleAlgebraView();

				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null) {
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel()
							.enableDisableButtons();
				}
			}
		});

		return button;
	}
}
