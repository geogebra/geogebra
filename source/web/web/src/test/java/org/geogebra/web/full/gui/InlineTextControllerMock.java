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

package org.geogebra.web.full.gui;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;

public class InlineTextControllerMock implements InlineTextController {
	private String url;

	public InlineTextControllerMock() {
	}

	public InlineTextControllerMock(String url) {
		this.url = url;
	}

	@Override
	public GeoInline getInline() {
		return null;
	}

	@Override
	public boolean updateFontSize() {
		return false;
	}

	@Override
	public void create() {

	}

	@Override
	public void discard() {

	}

	@Override
	public void setLocation(int x, int y) {

	}

	@Override
	public void setWidth(int width) {

	}

	@Override
	public void setHeight(int height) {

	}

	@Override
	public void toForeground(int x, int y) {

	}

	@Override
	public void toBackground() {

	}

	@Override
	public void format(String key, Object val) {

	}

	@Override
	public void updateContent() {

	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return null;
	}

	@Override
	public String getHyperLinkURL() {
		return url;
	}

	@Override
	public String getHyperlinkRangeText() {
		return null;
	}

	@Override
	public void draw(GGraphics2D g2) {

	}

	@Override
	public void insertHyperlink(String url, String text) {

	}

	@Override
	public void setHyperlinkUrl(String url) {
		this.url = url;
	}

	@Override
	public void switchListTo(String listType) {

	}

	@Override
	public String getListStyle() {
		return null;
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return null;
	}

	@Override
	public void updateContentIfChanged() {

	}

	@Override
	public void saveContent() {

	}

	@Override
	public boolean copySelection() {
		return false;
	}

	@Override
	public void setSelectionText(String text) {

	}

	@Override
	public void setTransform(double angle, double sx, double sy) {

	}

	@Override
	public boolean isEditing() {
		return false;
	}

	public VerticalAlignment getVerticalAlignment() {
		return VerticalAlignment.TOP;
	}

	@Override
	public void setVerticalAlignment(VerticalAlignment alignment) {

	}

	@Override
	public HorizontalAlignment getHorizontalAlignment() {
		return HorizontalAlignment.LEFT;
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignment alignment) {

	}

}
