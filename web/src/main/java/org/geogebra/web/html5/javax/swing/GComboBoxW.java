package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.event.ActionListener;
import org.geogebra.common.javax.swing.AbstractJComboBox;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class GComboBoxW extends org.geogebra.common.javax.swing.AbstractJComboBox {

	private ListBox impl = null;

	/**
	 * Creates new wrapper Box
	 */
	public GComboBoxW(final App app, final int view) {
		this.impl = new ListBox();
		impl.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {

				EuclidianViewW ev = (EuclidianViewW) (view == App.VIEW_EUCLIDIAN2 ? app
				        .getEuclidianView2(1) : app.getEuclidianView1());

				ev.getEuclidianController().setComboboxFocused(true);
			}
		});
		impl.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {

				EuclidianViewW ev = (EuclidianViewW) (view == App.VIEW_EUCLIDIAN2 ? app
				        .getEuclidianView2(1) : app.getEuclidianView1());

				ev.getEuclidianController().setComboboxFocused(false);
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		impl.setVisible(b);

	}

	@Override
	public Object getItemAt(int i) {
		return impl.getItemText(i);
	}

	@Override
	public void setFont(GFont font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForeground(GColor objectColor) {
		impl.getElement().getStyle()
		        .setColor(GColor.getColorString(objectColor));
	}

	@Override
	public void setBackground(GColor color) {
		impl.getElement().getStyle()
		        .setBackgroundColor(GColor.getColorString(color));
	}

	@Override
	public void setFocusable(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEditable(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addItem(String string) {
		impl.addItem(string);

	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		impl.setSelectedIndex(selectedIndex);
	}

	@Override
	public int getSelectedIndex() {
		return impl.getSelectedIndex();
	}

	@Override
	public void addActionListener(ActionListener newActionListener) {
		impl.addChangeHandler((ChangeHandler) newActionListener);

	}

	public static Widget getImpl(AbstractJComboBox comboBox) {
		if (!(comboBox instanceof GComboBoxW))
			return null;
		return ((GComboBoxW) comboBox).impl;
	}

	@Override
	public void removeAllItems() {
		impl.clear();

	}

	@Override
	public int getItemCount() {
		return impl.getItemCount();
	}

}
