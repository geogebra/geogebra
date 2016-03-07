package com.himamis.retex.renderer.android.resources;

import java.io.IOException;
import java.io.InputStream;

import com.himamis.retex.renderer.android.BaseObjectHelper;
import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.platform.resources.ResourceLoader;

import android.content.res.AssetManager;

public class ResourceLoaderA implements ResourceLoader {
	
	private AssetManager mAssetManager;
	
	public ResourceLoaderA(AssetManager assetManager) {
		mAssetManager = assetManager;
	}

	public InputStream loadResource(Object base, String path) throws ResourceParseException {
		try {
			return mAssetManager.open(BaseObjectHelper.getPath(base, path));
		} catch (IOException e) {
			throw new ResourceParseException("Could not load resource.", e);
		}
	}

}
