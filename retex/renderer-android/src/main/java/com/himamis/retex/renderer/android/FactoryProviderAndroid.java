package com.himamis.retex.renderer.android;

import android.content.res.AssetManager;

import com.himamis.retex.renderer.android.font.FontFactoryAndroid;
import com.himamis.retex.renderer.android.geom.GeomFactoryAndroid;
import com.himamis.retex.renderer.android.graphics.GraphicsFactoryAndroid;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.FontFactory;
import com.himamis.retex.renderer.share.platform.geom.GeomFactory;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;

public class FactoryProviderAndroid extends FactoryProvider {
	
	private final AssetManager mAssetManager;
	
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
}
