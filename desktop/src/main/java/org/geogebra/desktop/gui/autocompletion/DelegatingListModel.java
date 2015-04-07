package org.geogebra.desktop.gui.autocompletion;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * A simple {@link ListModel} implementation which forwards to a list. This list
 * should be set via the {@link #setDataList(List)} method.
 * 
 * @author Julian Lettner
 */
public class DelegatingListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	private List<?> delegate = Collections.emptyList();

	public int getSize() {
		return delegate.size();
	}

	public Object getElementAt(int index) {
		return delegate.get(index);
	}

	/**
	 * Method for passing in the backing list for this {@link ListModel}
	 * instance. If the list changes after it was passed in one must call
	 * {@link #setDataList(List)} again.
	 * 
	 * @param dataList
	 *            The list
	 */
	public void setDataList(List<?> dataList) {
		// Substitute null with an empty list
		dataList = null != dataList ? dataList : Collections.emptyList();

		// Remember sizes
		int oldSize = delegate.size();
		int newSize = dataList.size();
		int minSize = Math.min(oldSize, newSize);

		// Set delegate
		delegate = dataList;

		// Fire a content changed event for indices which exist in both lists
		if (0 != minSize) {
			fireContentsChanged(this, 0, minSize - 1);
		}

		// Fire interval added event (new list is larger),
		// interval removed event (new list is smaller)
		// or do nothing (same size)
		if (newSize > oldSize) {
			fireIntervalAdded(this, minSize, newSize - 1);
		} else if (newSize < oldSize) {
			fireIntervalRemoved(this, minSize, oldSize - 1);
		} // else (newSize == oldSize) { /* do nothing */ }
	}

}
