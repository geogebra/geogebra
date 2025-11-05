/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree;

import java.util.ArrayList;

import org.geogebra.editor.share.catalog.ArrayDelimiter;
import org.geogebra.editor.share.catalog.ArrayTemplate;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;

/**
 * Mathematical array container supporting matrices, vectors, and arrays.
 */
public class ArrayNode extends InternalNode {

	private ArrayTemplate template;
	private int columns;
	private int rows;

	/**
	 * @param template array template
	 * @param columns number of columns
	 */
	public ArrayNode(ArrayTemplate template, int columns) {
		this(template, columns, 1);
	}

	/**
	 * @param template array template
	 * @param columns number of columns
	 * @param rows number of rows
	 */
	public ArrayNode(ArrayTemplate template, int columns, int rows) {
		super(columns * rows);
		this.template = template;
		this.columns = columns;
		this.rows = rows;
	}

	/**
	 * Adds a new row with empty cells.
	 */
	public void addRow() {
		for (int i = 0; i < columns; i++) {
			super.addChild(new SequenceNode());
		}
		rows += 1;
	}

	@Override
	public SequenceNode getChild(int i) {
		return (SequenceNode) super.getChild(i);
	}

	/**
	 * Sets the argument at the given index.
	 * @param i index
	 * @param argument the argument to set
	 */
	public void setChild(int i, SequenceNode argument) {
		super.setChild(i, argument);
	}

	/**
	 * Adds a new column.
	 */
	public void addChild() {
		SequenceNode argument = new SequenceNode();
		super.addChild(argument);
		columns += 1;
	}

	/**
	 * Adds a column with the given argument.
	 * @param child the component to add
	 */
	@Override
	public void addChild(Node child) {
		super.addChild(child);
		columns += 1;
	}

	/**
	 * Inserts a column at the specified position.
	 * @param i position to insert at
	 * @param child the component to add
	 * @return true if the argument was added successfully
	 */
	@Override
	public boolean addChild(int i, Node child) {
		if (super.addChild(i, child)) {
			columns += 1;
			return true;
		}

		return false;
	}

	/**
	 * Removes the column at the specified index.
	 * @param index index of the column to remove
	 */
	@Override
	public void deleteChild(int index) {
		super.removeChild(index);
		columns -= 1;
	}

	/**
	 * Sets cell.
	 * @param row row
	 * @param column column
	 * @param argument cell value
	 */
	public void setChild(int row, int column, SequenceNode argument) {
		setChild(row * columns + column, argument);
	}

	/**
	 * Returns cell.
	 * @param row row
	 * @param column column
	 * @return matrix cell
	 */
	public SequenceNode getChild(int row, int column) {
		return getChild(row * columns + column);
	}

	/**
	 * @return the opening delimiter.
	 */
	public ArrayDelimiter getOpenDelimiter() {
		return template.getOpenDelimiter();
	}

	/**
	 * @return the closing delimiter.
	 */
	public ArrayDelimiter getCloseDelimiter() {
		return template.getCloseDelimiter();
	}

	/**
	 * @return the field delimiter.
	 */
	public ArrayDelimiter getFieldDelimiter() {
		return template.getFieldDelimiter();
	}

	/**
	 * @return the row delimiter.
	 */
	public ArrayDelimiter getRowDelimiter() {
		return template.getRowDelimiter();
	}

	/**
	 * @return whether this array is one-dimensional.
	 */
	public boolean is1DArray() {
		return getRows() == 1 && template.isArray();
	}

	/**
	 * @return whether this is an array.
	 */
	public boolean isArray() {
		return template.isArray();
	}

	/**
	 * @return whether this is a vector.
	 */
	public boolean isVector() {
		return getRows() == 1 && template.isMatrix();
	}

	/**
	 * @return whether this is a matrix.
	 */
	public boolean isMatrix() {
		return template.isMatrix();
	}

	/**
	 * @return the number of columns.
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @return the number of rows.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Checks whether this is a matrix and updates row/column values accordingly.
	 * @param catalog the catalog to check against
	 */
	public void checkMatrix(TemplateCatalog catalog) {
		int matrixWidth = numberOfColumns(catalog);
		if (matrixWidth >= 0) {
			rows = size();
			flattenMatrix();
			columns = matrixWidth;
			template = catalog.getMatrix();
		}
	}

	// Returns the number of columns or -1 if it's not a matrix
	private int numberOfColumns(TemplateCatalog catalog) {
		int matrixWidth = -1;
		for (int i = 0; i < size(); i++) {
			ArrayNode row = extractRow(i);
			if (row != null) {
				if (row.template != catalog.getArray(Tag.CURLY)) {
					return -1;
				} else if (matrixWidth == -1) {
					matrixWidth = row.size();
				} else if (matrixWidth != row.size()) {
					return -1;
				}
			} else {
				return -1;
			}
		}
		return matrixWidth;
	}

	private ArrayNode extractRow(int row) {
		int idx = 0;
		int size = getChild(row).size();
		while (idx < size && " ".equals(getChild(row).getChild(idx).toString())) {
			idx++;
		}
		int last = size - 1;
		while (last > idx && " ".equals(getChild(row).getChild(last).toString())) {
			last--;
		}
		if (idx == last
				&& getChild(row).getChild(idx) instanceof ArrayNode) {
			return (ArrayNode) getChild(row).getChild(idx);
		}
		return null;
	}

	private void flattenMatrix() {
		ArrayList<Node> entries = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			ArrayNode row = extractRow(i);
			if (row == null) {
				throw new IllegalStateException("Not a matrix");
			}
			for (int j = 0; j < row.size(); j++) {
				Node arg = row.getChild(j);
				entries.add(arg);
			}
		}
		clearChildren();
		for (Node entry : entries) {
			addChild(entry);
		}
	}

	/**
	 * Checks if the cursor should be locked inside the container.
	 * @param node the container to check
	 * @return true if the cursor should be locked
	 */
	public static boolean isLocked(Node node) {
		return node instanceof ArrayNode && node.getParent().isProtected();
	}

	/**
	 * @param other the other array to compare
	 * @return whether the two arrays have the same dimensions.
	 */
	public boolean hasSameDimension(ArrayNode other) {
		if (other == null) {
			return false;
		}

		return rows == other.rows && columns == other.columns;
	}

	@Override
	public boolean hasTag(Tag tag) {
		return template.getTag() == tag;
	}

	@Override
	public ArrayList<Node> replaceChildren(int start, int end, Node array) {
		// works as expected if start = end; otherwise arguments start+1 to end remain unchanged
		SequenceNode argument = getChild(start);
		return argument.replaceChildren(0, argument.size() - 1, array);
	}

	@Override
	public boolean isRenderingOwnPlaceholders() {
		return template.getTag().isRenderingOwnPlaceholders();
	}
}