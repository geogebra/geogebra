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
	MobileApp app;

	@Override
	public void onModuleLoad()
	{
		 this.app = new MobileApp(new TabletGUI());

//		this.app = new MobileApp(new IconTestGUI());
		loadMobileAsync();
	}

	private void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{

			@Override
			public void onSuccess()
			{
				MobileEntryPoint.this.app.start();
			}

			@Override
			public void onFailure(Throwable reason)
			{
				// App.debug(reason);
				reason.printStackTrace();
			}
		});
	}
}
