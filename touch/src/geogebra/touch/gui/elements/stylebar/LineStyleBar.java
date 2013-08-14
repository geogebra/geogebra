package geogebra.touch.gui.elements.stylebar;

import geogebra.html5.gui.util.Slider;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class LineStyleBar extends FlowPanel {
	public static final int SLIDER_MIN = 1;
	public static final int SLIDER_MAX = 12;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private static StandardImageButton[] lineStyle = {
			new StandardImageButton(LafIcons.line_solid()),
			new StandardImageButton(LafIcons.line_dashed_long()),
			new StandardImageButton(LafIcons.line_dashed_short()),
			new StandardImageButton(LafIcons.line_dotted()),
			new StandardImageButton(LafIcons.line_dash_dot()) };

	private FlowPanel buttonPanel;

	public LineStyleBar(final TouchModel touchModel) {
		this.addStyleName("lineStyleBar");

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < lineStyle.length; i++) {
			final int index = i;

			lineStyle[i].addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					StyleBarStatic.applyLineStyle(touchModel.getSelectedGeos(),
							index);
					touchModel.getGuiModel().setLineStyle(index);
					touchModel.storeOnClose();

					if (touchModel.getCommand().equals(ToolBarCommand.Pen)
							|| touchModel.getCommand().equals(
									ToolBarCommand.FreehandShape)) {
						touchModel.getKernel().getApplication().getEuclidianView1()
						.getEuclidianController().getPen().setPenLineStyle(index);
					}
				}
			}, ClickEvent.getType());
			this.buttonPanel.add(lineStyle[i]);
		}
		
		this.add(this.buttonPanel);

		final Slider slider = new Slider();

		slider.setMinimum(SLIDER_MIN);
		slider.setMaximum(SLIDER_MAX);

		if (touchModel.lastSelected() != null) {
			slider.setValue(Integer.valueOf(touchModel.lastSelected()
					.getLineThickness()));
		} else if (touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| touchModel.getCommand().equals(ToolBarCommand.FreehandShape)) {
			slider.setValue(new Integer(touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen().getPenSize()));
		}

		slider.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				StyleBarStatic.applyLineSize(touchModel.getSelectedGeos(),
						event.getValue().intValue());
				touchModel.getGuiModel().setLineSize(
						event.getValue().intValue());
				touchModel.storeOnClose();

				if (touchModel.getCommand().equals(ToolBarCommand.Pen)
						|| touchModel.getCommand().equals(
								ToolBarCommand.FreehandShape)) {
					touchModel.getKernel().getApplication().getEuclidianView1()
					.getEuclidianController().getPen()
							.setPenSize(event.getValue().intValue());
				}
			}
		});
		this.add(slider);
	}

}
