package org.geogebra.web.full.gui;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.inline.InlineTextController;

public class InlineTextControllerMock implements InlineTextController {
	private String url;

	public InlineTextControllerMock() {
	}

	public InlineTextControllerMock(String url) {
		this.url = url;
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
	public void setAngle(double angle) {

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
	public void draw(GGraphics2D g2, GAffineTransform transform) {

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
	public boolean copySelection() {
		return false;
	}

	@Override
	public void setSelectionText(String text) {

	}
}
