package geogebra.common.gui.dialog.options.model;



public abstract class BooleanOptionModel extends OptionsModel {
	public interface IBooleanOptionListener {
		void updateCheckbox(boolean isEqual);
	}

	private IBooleanOptionListener listener;
	
	public BooleanOptionModel(IBooleanOptionListener listener) {
		this.setListener(listener);
	}

	public abstract void applyChanges(boolean value);

	public IBooleanOptionListener getListener() {
		return listener;
	}

	public void setListener(IBooleanOptionListener listener) {
		this.listener = listener;
	}
	
}

