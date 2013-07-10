package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;
import geogebra.touch.model.GuiModel;

public class SaveFileDialog extends FileDialog
{

	public SaveFileDialog(TouchApp app, GuiModel guiModel)
	{
		super(app, guiModel);
		// FIXME the glass pane has z-index 20, we must go higher
		this.getElement().getStyle().setZIndex(42);
	}

	@Override
	protected void onOK()
	{
		save();
		setDownloadName();
		hide();
	}

	@Override
	protected void onCancel()
	{
		hide();
	}

	/**
	 * Saves current file as xml String to the local storage.
	 * 
	 */
	private void save()
	{
		if (!super.textBox.getText().isEmpty())
		{
			super.app.setConstructionTitle(super.textBox.getText());
			super.fm.saveFile(super.textBox.getText(), super.app);
		}
	}

	@Override
	public void show()
	{
		super.textBox.setText(super.app.getConstructionTitle());
		super.show();
	}

	private void setDownloadName()
	{
		super.okButton.getElement().setAttribute("download", super.textBox.getText() + ".ggb");
	}

	@Override
	public void setLabels()
	{
		// TODO Auto-generated method stub

	}
}
