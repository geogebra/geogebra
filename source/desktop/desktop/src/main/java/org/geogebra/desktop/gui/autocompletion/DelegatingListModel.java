/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	@Override
	public int getSize() {
		return delegate.size();
	}

	@Override
	public Object getElementAt(int index) {
		return delegate.get(index);
	}

	/**
	 * Method for passing in the backing list for this {@link ListModel}
	 * instance. If the list changes after it was passed in one must call
	 * {@link #setDataList(List)} again.
	 * 
	 * @param dataList0
	 *            The list
	 */
	public void setDataList(List<?> dataList0) {
		// Substitute null with an empty list
		List<?> dataList = null != dataList0 ? dataList0 : Collections
				.emptyList();

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
