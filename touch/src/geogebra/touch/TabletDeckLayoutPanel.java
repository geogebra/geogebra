package geogebra.touch;

import geogebra.touch.gui.WorksheetGUI;

import java.util.EmptyStackException;
import java.util.Stack;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabletDeckLayoutPanel extends DeckLayoutPanel {
	private final Stack<Widget> history;
	private final TouchApp app;

	public TabletDeckLayoutPanel(final TouchApp app) {
		this.app = app;
		this.history = new Stack<Widget>();
	}

	public boolean goBack() {
		try {
			// remove the current shown view
			final Widget current = this.history.pop();

			if (TouchEntryPoint.hasWorksheetGUI()
					&& current.equals(TouchEntryPoint.getWorksheetGUI())) {
				TouchEntryPoint.tabletGUI
						.restoreEuclidian(((WorksheetGUI) current)
								.getContentPanel());
				this.app.fileNew();
			} else if (TouchEntryPoint.hasBrowseGUI()
					&& current.equals(TouchEntryPoint.getBrowseGUI())
					&& !this.app.getFileManager().hasFile(
							this.app.getConstructionTitle())) {
				this.app.fileNew();
			}

			// go back to the last view
			this.showWidget(this.history.pop());
			return true;

		} catch (final EmptyStackException e) {
			return false;
		}
	}

	@Override
	public void showWidget(final Widget widget) {
		super.showWidget(widget);
		this.history.push(widget);
	}

	public void clearHistory() {
		this.history.clear();
	}
}
