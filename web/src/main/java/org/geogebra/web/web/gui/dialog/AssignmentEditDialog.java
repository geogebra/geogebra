package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.common.util.GeoAssignment;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog for editing an {@link GeoAssignment}
 * 
 * @author Christoph
 *
 */
public class AssignmentEditDialog extends DialogBoxW implements ClickHandler {

	private AppW app;
	private Assignment assignment;
	private Button btApply;
	private FlexTable hintsAndFractiosforResult;
	private VerticalPanel mainWidget;
	private FlowPanel bottomWidget;
	private ExerciseBuilderDialog exerciseBuilderDialog;
	private Localization loc;

	/**
	 * @param app
	 *            application
	 * @param assignment
	 *            the assignment being edited
	 * @param exerciseBuilderDialog
	 *            the ExercisebuilderDialog opening this dialog //TODO move
	 *            getHintTextBox and getFractionsLB here and use a callback for
	 *            returning
	 */
	public AssignmentEditDialog(App app, Assignment assignment,
			ExerciseBuilderDialog exerciseBuilderDialog) {
		super(false, false, null, ((AppW) app).getPanel());

		this.app = (AppW) app;
		this.loc = app.getLocalization();
		this.assignment = assignment;
		this.exerciseBuilderDialog = exerciseBuilderDialog;
		this.exerciseBuilderDialog.setVisible(false);
		createGUI();
	}

	private void createGUI() {

		getCaption().setText(loc.getMenu("Assignment.Edit"));

		setWidget(mainWidget = new VerticalPanel());

		HorizontalPanel toolNameIconPanel = new HorizontalPanel();
		Image icon = new Image();
		icon.setUrl(exerciseBuilderDialog.getIconFile(assignment
				.getIconFileName()));
		toolNameIconPanel.add(icon);
		toolNameIconPanel.add(new Label(assignment.getDisplayName()));

		mainWidget.add(toolNameIconPanel);

		hintsAndFractiosforResult = new FlexTable();

		hintsAndFractiosforResult.setWidget(0, 0,
				new Label(loc.getMenu("Result.Exercise")));
		hintsAndFractiosforResult.setWidget(0, 1,
				new Label(loc.getMenu("Hint")));
		hintsAndFractiosforResult.setWidget(0, 2,
				new Label(loc.getMenu("Fraction")));

		createHintsAndFractionsTable();

		mainWidget.add(hintsAndFractiosforResult);

		if (assignment instanceof GeoAssignment) {
			mainWidget.add(new Label(app.getLocalization()
					.getCommand("Command")));
			mainWidget.add(getCheckOpLB((GeoAssignment) assignment));
		}
		mainWidget.add(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("DialogButtonPanel");

		btApply = new Button(loc.getMenu("Apply"));
		btApply.addClickHandler(this);
		btApply.getElement().getStyle().setMargin(3, Style.Unit.PX);

		bottomWidget.add(btApply);
	}

	private ListBox getCheckOpLB(final GeoAssignment assignment1) {
		final ListBox checkOperation = new ListBox();
		checkOperation.setMultipleSelect(false);
		for (String op : GeoAssignment.CHECK_OPERATIONS) {
			checkOperation.addItem(app.getLocalization().getCommand(op), op);
		}

		checkOperation.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				assignment1.setCheckOperation(checkOperation
						.getValue(checkOperation.getSelectedIndex()));
			}
		});

		checkOperation.addAttachHandler(new Handler() {

			public void onAttachOrDetach(AttachEvent event) {
				int index = 0;
				while (index < GeoAssignment.CHECK_OPERATIONS.length
						&& GeoAssignment.CHECK_OPERATIONS[index] != assignment1
								.getCheckOperation()) {
					index++;
				}
				checkOperation.setSelectedIndex(index);
			}
		});

		return checkOperation;
	}

	private void createHintsAndFractionsTable() {
		int i = 1;
		int k = 0;
		for (Result res : assignment.possibleResults()) {
			hintsAndFractiosforResult.setWidget(i, k++, new Label(res.name()));
			hintsAndFractiosforResult.setWidget(i, k++,
					exerciseBuilderDialog.getHintTextBox(assignment, res));
			hintsAndFractiosforResult.setWidget(i, k++,
					exerciseBuilderDialog.getFractionsLB(assignment, res));
			i++;
			k = 0;
		}
	}

	public void onClick(ClickEvent e) {
		Element target = e.getNativeEvent().getEventTarget().cast();

		if (target == btApply.getElement()) {
			hide();
			exerciseBuilderDialog.show();
			exerciseBuilderDialog.setVisible(true);
		}

	}

}
