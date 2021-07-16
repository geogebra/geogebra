package com.himamis.retex.renderer.android.font;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontFactory;
import com.himamis.retex.renderer.share.platform.font.FontLoader;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.font.TextAttributeProvider;
import com.himamis.retex.renderer.share.platform.font.TextLayout;

import android.content.res.AssetManager;

public class FontFactoryAndroid extends FontFactory {
	
	private AssetManager mAssetManager;
	
	public FontFactoryAndroid(AssetManager assetManager) {
		mAssetManager = assetManager;
	}

	@Override
	public Font createFont(String name, int style, int size) {
		return new FontA(name, style, size);
	}

	@Override
	public TextLayout createTextLayout(String string, Font font,
			FontRenderContext fontRenderContext) {
		return new TextLayoutA(string, (FontA) font, (FontRenderContextA) fontRenderContext);
	}

	@Override
	public TextAttributeProvider createTextAttributeProvider() {
		return new TextAttributeProviderA();
	}

	@Override
	public FontLoader createFontLoader() {
		return new FontLoaderA(mAssetManager);
	}

}
