package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.resources.ImageResourceConverter;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/** Helper class to hide default tree images for collapse / expand */
public class TreeImages implements Tree.Resources {

	@Override
	public ImageResource treeClosed() {
		return ImageResourceConverter.convertToOldImageResource(AppResources.INSTANCE.empty());
	}

	@Override
	public ImageResource treeLeaf() {
		return ImageResourceConverter.convertToOldImageResource(AppResources.INSTANCE.empty());
	}

	@Override
	public ImageResource treeOpen() {
		return ImageResourceConverter.convertToOldImageResource(AppResources.INSTANCE.empty());
	}
}