package com.himamis.retex.renderer.share.platform.graphics.stubs;

import java.util.Map;

import com.himamis.retex.renderer.share.CharFont;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.font.TextAttribute;
import com.himamis.retex.renderer.share.platform.geom.Shape;

public class FontStub implements Font {

	@Override
	public Font deriveFont(Map<TextAttribute, Object> map) {
		return this;
	}

	@Override
	public Font deriveFont(int type) {
		return this;
	}

	@Override
	public boolean isEqual(Font f) {
		return false;
	}

	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public Shape getGlyphOutline(FontRenderContext frc, CharFont cf) {
		return null;
	}

	@Override
	public boolean canDisplay(char ch) {
		return true;
	}

	@Override
	public boolean canDisplay(int c) {
		return true;
	}

}
