package geogebra.touch.gui.elements.header;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ButtonBar for the buttons (undo/redo) on the right side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
class TabletHeaderUndoRedo extends HorizontalPanel {
	private FastButton undo;
	private FastButton redo;

	TouchApp app;
	TouchModel model;

	/**
	 * Generates the {@link HeaderButton buttons} for the right HeaderPanel.
	 */
	public TabletHeaderUndoRedo(final TouchApp app, TouchModel model) {
		this.app = app;
		this.model = model;

			this.undo = new StandardButton(TouchEntryPoint.getLookAndFeel()
					.getIcons().undo());
			this.redo = new StandardButton(TouchEntryPoint.getLookAndFeel()
					.getIcons().redo());

		this.addUndoButton();
		this.addRedoButton();
	}

	private void addRedoButton() {

		this.redo.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				TabletHeaderUndoRedo.this.model.resetSelection();
				TabletHeaderUndoRedo.this.app.getKernel().redo();
				TabletHeaderUndoRedo.this.app.resetPen();
				TabletHeaderUndoRedo.this.app.setUnsaved();
			}
		});

		this.add(this.redo);
		this.enableDisableRedo();
	}

	private void addUndoButton() {
		this.undo.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				TabletHeaderUndoRedo.this.model.removePreviewObject();
				TabletHeaderUndoRedo.this.model.resetSelection();
				TabletHeaderUndoRedo.this.app.getKernel().undo();
				TabletHeaderUndoRedo.this.app.resetPen();
				TabletHeaderUndoRedo.this.app.setUnsaved();
			}
		});

		this.add(this.undo);
		this.enableDisableUndo();
	}

	protected void enableDisableRedo() {
		if (this.app.getKernel().redoPossible()) {
			this.redo.removeStyleName("disabled");
			this.redo.setEnabled(true);
		} else {
			this.redo.addStyleName("disabled");
			this.redo.setEnabled(false);
		}
	}

	protected void enableDisableUndo() {
		if (this.app.getKernel().undoPossible()) {
			this.undo.removeStyleName("disabled");
			this.undo.setEnabled(true);
		} else {
			this.undo.addStyleName("disabled");
			this.undo.setEnabled(false);
		}
	}
}