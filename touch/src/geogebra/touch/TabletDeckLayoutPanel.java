package geogebra.touch;

import geogebra.touch.gui.WorksheetGUI;

import java.util.EmptyStackException;
import java.util.Stack;

import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabletDeckLayoutPanel extends DeckLayoutPanel {
	private final Stack<Widget> history;

	public TabletDeckLayoutPanel() {
		this.history = new Stack<Widget>();
	}

	public boolean goBack() {
		try {
			// remove the current shown view
			final Widget current = this.history.pop();

			if (current.equals(TouchEntryPoint.worksheetGUI)) {
				TouchEntryPoint.tabletGUI
						.restoreEuclidian(((WorksheetGUI) current)
								.getContentPanel());
				TouchEntryPoint.tabletGUI.getApp().fileNew();
			} else if (current.equals(TouchEntryPoint.browseGUI)
					&& !TouchEntryPoint.tabletGUI
							.getApp()
							.getFileManager()
							.hasFile(
									TouchEntryPoint.tabletGUI.getApp()
											.getConstructionTitle())) {
				TouchEntryPoint.tabletGUI.getApp().fileNew();
			}

			// go back to the last view
			final Widget next = this.history.pop();
			this.showWidget(next);

			return true;
		} catch (final EmptyStackException e) {
			return false;
		}
	}

	@Override
	public void showWidget(Widget widget) {
		super.showWidget(widget);
		this.history.push(widget);
	}
}
