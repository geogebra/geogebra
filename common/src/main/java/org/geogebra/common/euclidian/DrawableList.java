/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian;

//import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * List to store Drawable objects for fast drawing.
 */
public class DrawableList {
	/** first drawable in the list */
	public Link head;
	private Link tail;
	private int size = 0;

	/**
	 * Number of drawables in list
	 * 
	 * @return number of drawables in list
	 */
	public final int size() {
		return size;
	}

	/**
	 * Inserts d at the end of the list.
	 * 
	 * @param d
	 *            Drawable to be inserted
	 */
	public final void add(Drawable d) {
		if (d == null) {
			return;
		}

		if (head == null) {
			head = new Link(d, null);
			tail = head;
		} else {

			// add in the list according to when we want it drawn
			GeoElement priority = d.getGeoElement();
			Link cur = head;
			Link last = head;
			// cur.next test only relevant in concurrent scenarios
			while ((cur.d.getGeoElement().drawBefore(priority, false))
					&& !cur.equals(tail) && cur.next != null) {
				last = cur;
				cur = cur.next;
			}

			if (cur.equals(head)) {
				if (cur.d.getGeoElement().drawBefore(priority, false)) {
					// add at end (list size=1)
					Link temp = new Link(d, null);
					tail.next = temp;
					tail = temp;
				} else { // add at start of list
					Link temp2 = head;
					head = new Link(d, null);
					head.next = temp2;
				}
			} else if (cur.equals(tail)) {
				if ((cur.d.getGeoElement().drawBefore(priority, false))) {
					// add at end
					Link temp = new Link(d, null);
					tail.next = temp;
					tail = temp;
				} else {
					// add one from end
					Link temp = new Link(d, null);
					temp.next = last.next;
					last.next = temp;
				}
			} else { // add in middle
						// Application.debug("middle");
						// Link temp = new Link(d, null);
						// temp.next=cur.next;
						// cur.next = temp;

				Link temp = new Link(d, null);
				temp.next = last.next;
				last.next = temp;
			}

		}
		size++;
	}

	/**
	 * Inserts d at the end of the list only if the list doesn't already contain
	 * d.
	 * 
	 * @param d
	 *            drawable to be added
	 */
	public final void addUnique(Drawable d) {
		if (!contains(d)) {
			add(d);
		}
	}

	/**
	 * Returns true iff d is in this list.
	 * 
	 * @param d
	 *            Drawable to be looked for
	 * @return true iff d is in this list.
	 */
	public final boolean contains(Drawable d) {
		Link cur = head;
		while (cur != null) {
			if (cur.d == d) {
				return true;
			}
			cur = cur.next;
		}
		return false;
	}

	/**
	 * Removes d from list.
	 * 
	 * @param d
	 *            Drawable to be removed
	 */
	public final void remove(Drawable d) {
		Link prev = null;
		Link cur = head;
		while (cur != null) {
			// found algo to remove
			if (cur.d == d) {
				if (prev == null) { // remove from head
					head = cur.next;
					if (head == null) {
						tail = null;
					}
				} else { // standard case
					prev.next = cur.next;
					if (prev.next == null) {
						tail = prev;
					}
				}
				size--;
				return;
			}
			// not yet found
			prev = cur;
			cur = cur.next;
		}
	}

	/**
	 * Draws all drawables in the list.
	 * 
	 * @param g2
	 *            Graphic to be used
	 */
	public final void drawAll(GGraphics2D g2) {
		Link cur = head;
		while (cur != null) {
			// defined check needed in case the GeoList changed its size
			// don't draw GeoList as combos here
			GeoElement geo = cur.d.getGeoElement();
			if (geo.isDefined()
					&& !(geo.isGeoList() && ((GeoList) geo).drawAsComboBox())
					&& !(geo.isGeoInputBox())) {
				if (cur.d.needsUpdate()) {
					cur.d.setNeedsUpdate(false);
					cur.d.update();
				}
				cur.d.draw(g2);
			}
			cur = cur.next;
		}
	}

	/**
	 * Updates all drawables in list
	 */
	public final void updateAll() {
		Link cur = head;
		while (cur != null) {
			cur.d.update();
			cur = cur.next;
		}
	}

	/**
	 * Update all elements when view was updated
	 */
	public final void updateAllForView() {
		Link cur = head;
		while (cur != null) {
			cur.d.updateForView();
			cur = cur.next;
		}
	}

	/**
	 * Updates fot size for all drawables in list
	 */
	public final void updateFontSizeAll() {
		Link cur = head;
		while (cur != null) {
			cur.d.updateFontSize();
			cur = cur.next;
		}
	}

	/**
	 * Empties the list
	 */
	public void clear() {
		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Linked list of drawables
	 */
	public static class Link {
		/** drawable */
		public Drawable d;
		/** next element */
		public Link next;

		/**
		 * @param a
		 *            drawable
		 * @param n
		 *            next
		 */
		Link(Drawable a, Link n) {
			d = a;
			next = n;
		}
	}

	/**
	 * Returns iterator pointing to head of the list
	 * 
	 * @return iterator pointing to head of the list
	 */
	public DrawableIterator getIterator() {
		return new DrawableIterator();
	}

	/**
	 * Allows iteration over the list
	 * 
	 */
	public class DrawableIterator implements Iterator<Drawable> {
		private Link it;

		/**
		 * Creates new drawable iterator
		 */
		DrawableIterator() {
			reset();
		}

		@Override
		final public Drawable next() {
			if (it == null) {
				throw new NoSuchElementException();
			}
			Drawable ret = it.d;
			it = it.next;
			return ret;
		}

		@Override
		final public boolean hasNext() {
			return (it != null);
		}

		/**
		 * Resets the iterator to the head of the list
		 */
		final public void reset() {
			it = head;
		}

		@Override
		final public void remove() {
			// do nothing
		}

	}

}
