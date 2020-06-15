package org.geogebra.web.full.gui;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.inline.InlineTableController;

public class InlineTableControllerMock implements InlineTableController {

	private final boolean editMode;

	public InlineTableControllerMock(boolean editMode) {
		this.editMode = editMode;
	}

	@Override
	public void setLocation(int x, int y) {

	}

	@Override
	public void setWidth(double width) {

	}

	@Override
	public void setHeight(double height) {

	}

	@Override
	public void setAngle(double angle) {

	}

	@Override
	public void removeFromDom() {

	}

	@Override
	public void update() {

	}

	@Override
	public boolean isInEditMode() {
		return editMode;
	}

	@Override
	public void draw(GGraphics2D g2, GAffineTransform transform) {

	}

	@Override
	public void toForeground(int x, int y) {

	}

	@Override
	public void toBackground() {

	}

	@Override
	public void updateContent() {

	}

	@Override
	public void setBackgroundColor(GColor bgColor) {

	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return null;
	}

	@Override
	public void insertRowAbove() {

	}

	@Override
	public void insertRowBelow() {

	}

	@Override
	public void insertColumnLeft() {

	}

	@Override
	public void insertColumnRight() {

	}

	@Override
	public void removeRow() {

	}

	@Override
	public void removeColumn() {

	}

	@Override
	public void format(String key, Object val) {

	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return null;
	}

	@Override
	public String getHyperLinkURL() {
		return null;
	}

	@Override
	public void setHyperlinkUrl(String url) {

	}

	@Override
	public String getHyperlinkRangeText() {
		return null;
	}

	@Override
	public void insertHyperlink(String url, String text) {

	}

	@Override
	public String getListStyle() {
		return null;
	}

	@Override
	public void switchListTo(String listType) {

	}
}
