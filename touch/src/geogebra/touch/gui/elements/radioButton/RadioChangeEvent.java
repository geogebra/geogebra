package geogebra.touch.gui.elements.radioButton;

public class RadioChangeEvent {
	private final int index;

	RadioChangeEvent(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}
}
