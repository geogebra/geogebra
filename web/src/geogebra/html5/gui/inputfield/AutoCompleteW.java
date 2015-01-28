package geogebra.html5.gui.inputfield;

import java.util.List;

public interface AutoCompleteW {
	public boolean getAutoComplete();

	public List<String> resetCompletions();

	public List<String> getCompletions();
}
