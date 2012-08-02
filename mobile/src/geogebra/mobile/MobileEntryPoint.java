package geogebra.mobile;

import geogebra.mobile.gui.TabletGUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MobileEntryPoint implements EntryPoint
{
	@Override
	public void onModuleLoad()
	{
		loadMobileAsync();
	}

	private void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{

			public void onSuccess()
			{
				new MobileApp(new TabletGUI());
			}

			public void onFailure(Throwable reason)
			{
//				App.debug(reason);
				reason.printStackTrace(); 
			}
		});
	}
}
