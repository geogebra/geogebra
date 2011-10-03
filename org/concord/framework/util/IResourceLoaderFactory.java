package org.concord.framework.util;

import java.net.URL;

public interface IResourceLoaderFactory 
{
	public IResourceLoader getResourceLoader(URL resource, boolean required);
}
