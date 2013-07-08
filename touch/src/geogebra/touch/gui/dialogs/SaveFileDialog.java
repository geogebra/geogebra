package geogebra.touch.gui.dialogs;

import geogebra.touch.TouchApp;

public class SaveFileDialog extends FileDialog
{

	public SaveFileDialog(TouchApp app)
	{
		super(app);
		//FIXME the glass pane has z-index 20, we must go higher
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
			super.fm.saveFile(super.textBox.getText(), super.app);
			super.app.setConstructionTitle(super.textBox.getText());
		}
	}

	@Override
	public void show()
	{
		super.app.getGgbApi().getBase64(true);
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
