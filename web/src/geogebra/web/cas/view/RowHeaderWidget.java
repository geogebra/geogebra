package geogebra.web.cas.view;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RowHeaderWidget extends VerticalPanel {

	public RowHeaderWidget(int n) {
		Label label = new Label();
		label.setText(n+"");
	    add(label);
    }

}
