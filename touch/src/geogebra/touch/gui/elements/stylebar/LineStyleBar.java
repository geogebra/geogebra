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
	private Slider slider = new Slider();
	TouchModel touchModel;

	public LineStyleBar(final TouchModel model) {
		this.addStyleName("lineStyleBar");

		this.touchModel = model;

		this.buttonPanel = new FlowPanel();
		this.buttonPanel.setStyleName("styleBarButtonPanel");

		for (int i = 0; i < lineStyle.length; i++) {
			final int index = i;

			lineStyle[i].addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					StyleBarStatic.applyLineStyle(
							LineStyleBar.this.touchModel.getSelectedGeos(),
							index);

					LineStyleBar.this.touchModel.getGuiModel().setLineStyle(
							index);
					LineStyleBar.this.touchModel.storeOnClose();

					if (LineStyleBar.this.touchModel.getCommand().equals(
							ToolBarCommand.Pen)
							|| LineStyleBar.this.touchModel.getCommand()
									.equals(ToolBarCommand.FreehandShape)) {
						LineStyleBar.this.touchModel.getKernel()
								.getApplication().getEuclidianView1()
								.getEuclidianController().getPen()
								.setPenLineStyle(index);
						if (LineStyleBar.this.touchModel.getCommand().equals(
								ToolBarCommand.Pen)
								|| LineStyleBar.this.touchModel.getCommand()
										.equals(ToolBarCommand.FreehandShape)) {
							LineStyleBar.this.touchModel.getKernel()
									.getApplication().getEuclidianView1()
									.getEuclidianController().getPen()
									.setPenLineStyle(index);
						}
					}
				}
			});
			this.buttonPanel.add(lineStyle[i]);
		}

		this.add(this.buttonPanel);

		this.slider.setMinimum(SLIDER_MIN);
		this.slider.setMaximum(SLIDER_MAX);

		update();

		if (this.touchModel.lastSelected() != null) {
			this.slider.setValue(Integer.valueOf(this.touchModel.lastSelected()
					.getLineThickness()));
		} else if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.slider.setValue(new Integer(this.touchModel.getKernel()
					.getApplication().getEuclidianView1()
					.getEuclidianController().getPen().getPenSize()));
		}

		this.slider.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				StyleBarStatic.applyLineSize(LineStyleBar.this.touchModel
						.getSelectedGeos(), event.getValue().intValue());
				LineStyleBar.this.touchModel.getGuiModel().setLineSize(
						event.getValue().intValue());
				LineStyleBar.this.touchModel.storeOnClose();

				if (LineStyleBar.this.touchModel.getCommand().equals(
						ToolBarCommand.Pen)
						|| LineStyleBar.this.touchModel.getCommand().equals(
								ToolBarCommand.FreehandShape)) {
					LineStyleBar.this.touchModel.getKernel().getApplication()
							.getEuclidianView1().getEuclidianController()
							.getPen().setPenSize(event.getValue().intValue());
					LineStyleBar.this.touchModel.getKernel().getApplication()
							.getEuclidianView1().getEuclidianController()
							.getPen().setPenSize(event.getValue().intValue());
				}
			}
		});
		this.add(this.slider);
	}

	public void update() {
		if (this.touchModel.lastSelected() != null) {
			this.slider.setValue(Integer.valueOf(this.touchModel.lastSelected()
					.getLineThickness()));
		} else if (this.touchModel.getCommand().equals(ToolBarCommand.Pen)
				|| this.touchModel.getCommand().equals(
						ToolBarCommand.FreehandShape)) {
			this.slider.setValue(new Integer(this.touchModel.getKernel()
					.getApplication().getEuclidianView1()
					.getEuclidianController().getPen().getPenSize()));
		}
	}
}
