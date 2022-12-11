package org.geogebra.web.html5.gui;

import org.geogebra.common.main.App;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.DecoratorPanel;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.UIObject;
import org.gwtproject.user.client.ui.Widget;

/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * <p>
 * A {@link SimplePanel} that wraps its contents in stylized boxes, which can be
 * used to add rounded corners to a {@link Widget}.
 * </p>
 * <p>
 * This widget will <em>only</em> work in quirks mode in most cases.
 * Specifically, setting the height or width of the DecoratorPanel will result
 * in rendering issues.
 * </p>
 * <p>
 * Wrapping a {@link Widget} in a "9-box" allows users to specify images in each
 * of the corners and along the four borders. This method allows the content
 * within the {@link DecoratorPanel} to resize without disrupting the look of
 * the border. In addition, rounded corners can generally be combined into a
 * single image file, which reduces the number of downloaded files at startup.
 * This class also simplifies the process of using AlphaImageLoaders to support
 * 8-bit transparencies (anti-aliasing and shadows) in ie6, which does not
 * support them normally.
 * </p>
 * <h3>Setting the Size:</h3>
 * <p>
 * If you set the width or height of the {@link DecoratorPanel}, you need to set
 * the height and width of the middleCenter cell to 100% so that the
 * middleCenter cell takes up all of the available space. If you do not set the
 * width and height of the {@link DecoratorPanel}, it will wrap its contents
 * tightly.
 * </p>
 * 
 * <pre>
 * .gwt-DecoratorPanel .middleCenter {
 *   height: 100%;
 *   width: 100%;
 * }
 * </pre>
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-DecoratorPanel { the panel }</li>
 * <li>.gwt-DecoratorPanel .top { the top row }</li>
 * <li>.gwt-DecoratorPanel .topLeft { the top left cell }</li>
 * <li>.gwt-DecoratorPanel .topLeftInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .topCenter { the top center cell }</li>
 * <li>.gwt-DecoratorPanel .topCenterInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .topRight { the top right cell }</li>
 * <li>.gwt-DecoratorPanel .topRightInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .middle { the middle row }</li>
 * <li>.gwt-DecoratorPanel .middleLeft { the middle left cell }</li>
 * <li>.gwt-DecoratorPanel .middleLeftInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .middleCenter { the middle center cell }</li>
 * <li>.gwt-DecoratorPanel .middleCenterInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .middleRight { the middle right cell }</li>
 * <li>.gwt-DecoratorPanel .middleRightInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .bottom { the bottom row }</li>
 * <li>.gwt-DecoratorPanel .bottomLeft { the bottom left cell }</li>
 * <li>.gwt-DecoratorPanel .bottomLeftInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .bottomCenter { the bottom center cell }</li>
 * <li>.gwt-DecoratorPanel .bottomCenterInner { the inner element of the cell }</li>
 * <li>.gwt-DecoratorPanel .bottomRight { the bottom right cell }</li>
 * <li>.gwt-DecoratorPanel .bottomRightInner { the inner element of the cell }</li>
 * </ul>
 */
public class GDecoratorPanel extends SimplePanel {
	/**
	 * The default style name.
	 */
	private static final String DEFAULT_STYLENAME = "gwt-DecoratorPanel";

	/**
	 * The default styles applied to each row.
	 */
	private static final String[] DEFAULT_ROW_STYLENAMES = { "top", "middle",
			"bottom" };
	/**
	 * The container element at the center of the panel.
	 */
	private Element containerElem;

	/**
	 * The table body element.
	 */
	private Element tbody;

	/**
	 * Create a new row with a specific style name. The row will contain three
	 * cells (Left, Center, and Right), each prefixed with the specified style
	 * name.
	 * 
	 * This method allows Widgets to reuse the code on a DOM level, without
	 * creating a DecoratorPanel Widget.
	 * 
	 * @param styleName
	 *            the style name
	 * @return the new row {@link Element}
	 */
	static Element createTR(String styleName, boolean rtl) {
		Element trElem = DOM.createTR();
		UIObject.setStyleName(trElem, styleName);
		if (rtl) {
			DOM.appendChild(trElem, createTD(styleName + "Right"));
			DOM.appendChild(trElem, createTD(styleName + "Center"));
			DOM.appendChild(trElem, createTD(styleName + "Left"));
		} else {
			DOM.appendChild(trElem, createTD(styleName + "Left"));
			DOM.appendChild(trElem, createTD(styleName + "Center"));
			DOM.appendChild(trElem, createTD(styleName + "Right"));
		}
		return trElem;
	}

	/**
	 * Create a new table cell with a specific style name.
	 * 
	 * @param styleName
	 *            the style name
	 * @return the new cell {@link Element}
	 */
	private static Element createTD(String styleName) {
		Element tdElem = DOM.createTD();
		Element inner = DOM.createDiv();
		DOM.appendChild(tdElem, inner);
		UIObject.setStyleName(tdElem, styleName);
		UIObject.setStyleName(inner, styleName + "Inner");
		return tdElem;
	}

	/**
	 * Create a new {@link DecoratorPanel}.
	 */
	public GDecoratorPanel(App app) {
		this(DEFAULT_ROW_STYLENAMES, 1, app);
	}

	/**
	 * Creates a new panel using the specified style names to apply to each row.
	 * Each row will contain three cells (Left, Center, and Right). The Center
	 * cell in the containerIndex row will contain the {@link Widget}.
	 * 
	 * @param rowStyles
	 *            an array of style names to apply to each row
	 * @param containerIndex
	 *            the index of the container row
	 */
	GDecoratorPanel(String[] rowStyles, int containerIndex, App app) {
		super(DOM.createTable());

		// Add a tbody
		Element table = getElement();
		tbody = DOM.createTBody();
		DOM.appendChild(table, tbody);
		table.setPropertyInt("cellSpacing", 0);
		table.setPropertyInt("cellPadding", 0);

		// Add each row
		for (int i = 0; i < rowStyles.length; i++) {
			Element row = createTR(rowStyles[i],
					app.getLocalization().isRightToLeftReadingOrder());
			DOM.appendChild(tbody, row);
			if (i == containerIndex) {
				containerElem = DOM.getFirstChild(DOM.getChild(row, 1));
			}
		}

		// Set the overall style name
		setStyleName(DEFAULT_STYLENAME);
	}

	/**
	 * Get a specific Element from the panel.
	 * 
	 * @param row
	 *            the row index
	 * @param cell
	 *            the cell index
	 * @return the Element at the given row and cell
	 */
	protected Element getCellElement(int row,
			int cell) {
		Element tr = DOM.getChild(tbody, row);
		Element td = DOM.getChild(tr, cell);
		return DOM.getFirstChild(td);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Element getContainerElement() {
		return containerElem;
	}

	@Override
	public void onAttach() {
		super.onAttach();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}

