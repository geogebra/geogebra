package geogebra.touch.gui.elements.header;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ButtonBar for the buttons (undo/redo) on the right side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelRight extends HorizontalPanel
{
	private StandardImageButton undo = new StandardImageButton(TouchEntryPoint.getLookAndFeel().getIcons().undo());
	private StandardImageButton redo = new StandardImageButton(TouchEntryPoint.getLookAndFeel().getIcons().redo());
	TabletHeaderPanel headerPanel;
	TouchApp app;

	/**
	 * Generates the {@link HeaderButton buttons} for the right HeaderPanel.
	 */
	public TabletHeaderPanelRight(final TouchApp app, TabletHeaderPanel headerPanel)
	{
		this.headerPanel = headerPanel;
		this.app = app;

		addUndoButton();
		addRedoButton();
	}

	private void addUndoButton()
	{
		this.undo.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TabletHeaderPanelRight.this.app.getKernel().undo();
				TabletHeaderPanelRight.this.app.resetPen();
				TabletHeaderPanelRight.this.app.setUnsaved();

				TabletHeaderPanelRight.this.enableDisableUndo();
				TabletHeaderPanelRight.this.enableDisableRedo();
				TabletHeaderPanelRight.this.headerPanel.getLeftHeader().enableDisableSave();
			}
		}, ClickEvent.getType());
		this.add(this.undo);
		enableDisableUndo();
	}

	private void addRedoButton()
	{
		this.redo.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TabletHeaderPanelRight.this.app.getKernel().redo();
				TabletHeaderPanelRight.this.app.resetPen();
				TabletHeaderPanelRight.this.app.setUnsaved();

				TabletHeaderPanelRight.this.enableDisableRedo();
				TabletHeaderPanelRight.this.enableDisableUndo();
				TabletHeaderPanelRight.this.headerPanel.getLeftHeader().enableDisableSave();
			}
		}, ClickEvent.getType());
		this.add(this.redo);
		enableDisableRedo();
	}

	public void enableDisableUndo()
	{
		if (this.app.getKernel().undoPossible())
		{
			this.undo.removeStyleName("disabled");
		}
		else
		{
			this.undo.addStyleName("disabled");
		}
	}

	public void enableDisableRedo()
	{
		if (this.app.getKernel().redoPossible())
		{
			this.redo.removeStyleName("disabled");
		}
		else
		{
			this.redo.addStyleName("disabled");
		}
	}
}