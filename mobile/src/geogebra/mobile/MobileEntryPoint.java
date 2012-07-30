package geogebra.mobile;

import geogebra.common.main.App;
import geogebra.mobile.gui.GeoGebraMobileGUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.Composite;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MobileEntryPoint implements EntryPoint
{

	@Override
	public void onModuleLoad()
	{

		/*
		 * the Common.java and the Web.java onlmoduleLoad() are called before this.
		 * So, any script injecting, etc is done there already concerning the <body
		 * data-param-mobile="true"> parameter>
		 */
		loadMobileAsync();
	}

	private void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{

			public void onSuccess()
			{
				new MobileApp();
			}

			public void onFailure(Throwable reason)
			{
				App.debug(reason);
			}
		});
	}
}
