package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.gui.images.AppResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/** Helper class to hide default tree images for collapse / expand */
public class TreeImages implements Tree.Resources {

	@Override
	public ImageResource treeClosed() {
		return AppResources.INSTANCE.empty();
	}

	@Override
	public ImageResource treeLeaf() {
		return AppResources.INSTANCE.empty();
	}

	@Override
	public ImageResource treeOpen() {
		return AppResources.INSTANCE.empty();
	}
}