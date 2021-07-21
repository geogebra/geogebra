package com.himamis.retex.renderer.android;

import com.himamis.retex.renderer.android.font.FontFactoryAndroid;
import com.himamis.retex.renderer.android.geom.GeomFactoryAndroid;
import com.himamis.retex.renderer.android.graphics.GraphicsFactoryAndroid;
import com.himamis.retex.renderer.android.parser.ParserFactoryAndroid;
import com.himamis.retex.renderer.android.resources.ResourceLoaderFactoryAndroid;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.FontFactory;
import com.himamis.retex.renderer.share.platform.geom.GeomFactory;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.parser.ParserFactory;
import com.himamis.retex.renderer.share.platform.resources.ResourceLoaderFactory;

import android.content.res.AssetManager;

public class FactoryProviderAndroid extends FactoryProvider {
	
	private AssetManager mAssetManager;
	
	public FactoryProviderAndroid(AssetManager assetManager) {
		mAssetManager = assetManager;
	}

	@Override
	protected GeomFactory createGeomFactory() {
		return new GeomFactoryAndroid();
	}

	@Override
	protected FontFactory createFontFactory() {
		return new FontFactoryAndroid(mAssetManager);
	}

	@Override
	protected GraphicsFactory createGraphicsFactory() {
		return new GraphicsFactoryAndroid();
	}

	//@Override
	protected ParserFactory createParserFactory() {
		return new ParserFactoryAndroid();
	}

	//@Override
	protected ResourceLoaderFactory createResourceLoaderFactory() {
		return new ResourceLoaderFactoryAndroid(mAssetManager);
	}
}
