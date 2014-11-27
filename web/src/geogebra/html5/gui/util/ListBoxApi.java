package geogebra.html5.gui.util;

import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

/**
 * @author gabor
 *
 * some listbox functionalities
 */
public class ListBoxApi {
	
	/**
	 * @param value the value to search
	 * @param lb Listbox for shearching
	 * @return the index if found -1 if not.
	 */
	public static int getIndexOf(String value, ListBox lb) {
		int indexToFind = -1;
		for (int i = 0; i < lb.getItemCount(); i++) {
		    if (lb.getValue(i).equals(value)) {
		        indexToFind = i;
		        break;
		    }
		};
		return indexToFind;
    }

	public static List<String> getSelection(ListBox toolList) {
	    // TODO Auto-generated method stub
	    return null;
    }

	public static List<Integer> getSelectionIndexes(ListBox toolList) {
	    // TODO Auto-generated method stub
	    return null;
    }
}
