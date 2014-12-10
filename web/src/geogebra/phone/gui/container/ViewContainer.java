package geogebra.phone.gui.container;

import geogebra.html5.gui.ResizeListener;
import geogebra.phone.gui.view.View;

import com.google.gwt.user.client.ui.IsWidget;

public interface ViewContainer  extends ResizeListener, IsWidget {

	void addView(View view);

	void removeView(View view);

	void showView(View view);

}
