package geogebra.touch;

import java.util.EmptyStackException;
import java.util.Stack;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class TabletDeckLayoutPanel extends DeckLayoutPanel {
	private final Stack<Widget> history;
	private final TouchApp app;

	TabletDeckLayoutPanel(final TouchApp app) {
		this.app = app;
		this.history = new Stack<Widget>();
	}

	boolean goBack() {
		try {
			// remove the current shown view
			final Widget current = this.history.pop();
			System.out.println("goBack");
			if (TouchEntryPoint.hasWorksheetGUI() && current.equals(TouchEntryPoint.getWorksheetGUI())) {
//				TouchEntryPoint.touchGUI.restoreEuclidian(((WorksheetGUI) current).getContentPanel());
				this.app.fileNew();
			} else if (TouchEntryPoint.hasBrowseGUI() && current.equals(TouchEntryPoint.getBrowseGUI())) {
				this.app.getFileManager().hasFile(this.app.getConstructionTitle(), new Callback<Boolean, Boolean>() {

							@Override
							public void onSuccess(final Boolean hasFile) {
								if (!hasFile) {
									TabletDeckLayoutPanel.this.app.fileNew();
								}
							}

							@Override
							public void onFailure(final Boolean result) {
								// TODO Auto-generated method stub

							}
						});

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

	void clearHistory() {
		this.history.clear();
	}
}
