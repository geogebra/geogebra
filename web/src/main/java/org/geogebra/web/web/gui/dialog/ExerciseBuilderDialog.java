package org.geogebra.web.web.gui.dialog;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Exercise;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.app.GGWToolBar;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExerciseBuilderDialog extends DialogBoxW implements ClickHandler {

	private AppW app;
	private Exercise exercise;

	private VerticalPanel mainWidget;
	private FlowPanel bottomWidget;
	private Button btApply, btTest;
	private FlexTable assignmentsTable;
	private FlexTable checkAssignmentsTable;

	public ExerciseBuilderDialog(App app) {
		super(false, false, null);

		this.app = (AppW) app;

		exercise = new Exercise(app);
		createGUI();
	}

	private void createGUI() {

		getCaption().setText(app.getMenu("Exercise.CreateNew"));

		setWidget(mainWidget = new VerticalPanel());
		assignmentsTable = new FlexTable();
		FlexCellFormatter cellFormatter = assignmentsTable
		        .getFlexCellFormatter();
		cellFormatter.setColSpan(0, 1, 2);

		assignmentsTable.setWidget(0, 1, new Label(app.getPlain("Tool")));
		assignmentsTable.setWidget(0, 2,
		        new Label(app.getPlain("HintForCorrect")));
		assignmentsTable.setWidget(0, 3, new Label(app.getPlain("Fraction")));

		createAssignmentsTable();
		checkAssignmentsTable = new FlexTable();
		checkAssignmentsTable.setVisible(false);

		mainWidget.add(assignmentsTable);
		mainWidget.add(checkAssignmentsTable);

		mainWidget.add(bottomWidget = new FlowPanel());
		bottomWidget.setStyleName("DialogButtonPanel");

		btApply = new Button(app.getPlain("OK"));
		btApply.addClickHandler(this);
		btApply.getElement().getStyle().setMargin(3, Style.Unit.PX);

		addCancelButton();

		btTest = new Button(app.getPlain("Test"));
		btTest.addClickHandler(this);
		btTest.getElement().getStyle().setMargin(3, Style.Unit.PX);

		bottomWidget.add(btTest);
		bottomWidget.add(btApply);
		// bottomWidget.add(btCancel);

	}

	private void createAssignmentsTable() {
		ArrayList<Assignment> parts = exercise.getParts();
		for (int i = 0; i < parts.size(); i++) {
			final Assignment assignment = parts.get(i);
			int j = 0;
			Image delIcon = getDeleteIcon(assignment);
			assignmentsTable.setWidget(i + 1, j++, delIcon);

			Image icon = new Image();
			icon.setUrl(getIconFile(assignment.getIconFileName()));
			assignmentsTable.setWidget(i + 1, j++, icon);
			assignmentsTable.setWidget(i + 1, j++,
			        new Label(assignment.getToolName()));

			final TextBox textForSolvedAssignment = getHintTextBox(assignment,
			        Result.CORRECT);

			assignmentsTable.setWidget(i + 1, j++, textForSolvedAssignment);

			final ListBox fractions = getFractionsLB(assignment, Result.CORRECT);
			assignmentsTable.setWidget(i + 1, j++, fractions);

			Image editIcon = new Image(GuiResources.INSTANCE.menu_icon_edit());
			editIcon.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					new AssignmentEditDialog(app, assignment,
					        ExerciseBuilderDialog.this).show();
					ExerciseBuilderDialog.this.hide();
				}
			});
			assignmentsTable.setWidget(i + 1, j++, editIcon);
		}

		Image addIcon = new Image(GuiResources.INSTANCE.menu_icon_file_new());
		addIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				DialogBoxW newOrExisting = new DialogBoxW();
				// Todo: Select existing Tool or create new Tool

			}
		});
		assignmentsTable.setWidget(parts.size() + 2, 0, addIcon);

	}

	ListBox getFractionsLB(final Assignment assignment, final Result res) {
		final ListBox fractions = new ListBox();
		fractions.setMultipleSelect(false);
		for (int j = 0; j < Assignment.FRACTIONS.length; j++) {
			fractions.addItem(app.getKernel().format(
			        Assignment.FRACTIONS[j] * 100,
			        StringTemplate.defaultTemplate));
		}

		fractions.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				assignment.setFractionForResult(res,
				        Assignment.FRACTIONS[fractions.getSelectedIndex()]);
			}
		});

		fractions.addAttachHandler(new Handler() {

			public void onAttachOrDetach(AttachEvent event) {
				fractions.setSelectedIndex(Arrays.binarySearch(
				        assignment.FRACTIONS,
				        assignment.getFractionForResult(res)));
			}
		});

		return fractions;
	}

	TextBox getHintTextBox(final Assignment assignment, final Result res) {
		final TextBox textForResult = new TextBox();

		textForResult.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				if (!textForResult.getText().isEmpty()) {
					assignment.setHintForResult(res, textForResult.getText());
				}
			}
		});

		textForResult.addAttachHandler(new Handler() {

			public void onAttachOrDetach(AttachEvent event) {
				textForResult.setText(assignment.getHintForResult(res));
			}
		});

		return textForResult;
	}

	private Image getDeleteIcon(final Assignment assignment) {
		Image delIcon = new Image(GuiResources.INSTANCE.menu_icon_edit_delete());
		delIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				exercise.getParts().remove(assignment);
				assignmentsTable.removeRow(assignmentsTable.getCellForEvent(
				        event).getRowIndex());
			}
		});
		return delIcon;
	}

	SafeUri getIconFile(String fileName) {
		if (!fileName.isEmpty()) {
			String imageURL = app.getImageManager().getExternalImageSrc(
			        fileName);
			if (imageURL != null) {
				return UriUtils.fromString(imageURL);
			}
		}
		return ((ImageResource) GGWToolBar.getMyIconResourceBundle()
		        .mode_tool_32()).getSafeUri();
	}

	public void onClick(ClickEvent e) {
		Element target = e.getNativeEvent().getEventTarget().cast();
		boolean isEditing = assignmentsTable.isVisible();
		if (target == btApply.getElement()) {
			if (isEditing) {
				hide();
				app.getActiveEuclidianView().requestFocusInWindow();

				app.storeUndoInfo();
				app.getKernel().notifyRepaint();
			} else {
				assignmentsTable.setVisible(true);
				checkAssignmentsTable.setVisible(false);
				btTest.setText(app.getPlain("Test"));
				// btApply.setVisible(true);
				hide();
				setGlassEnabled(true);
				center();

			}
		} else if (target == btTest.getElement()) {
			if (isEditing) {
				assignmentsTable.setVisible(false);
				checkAssignmentsTable.setVisible(true);
				check();
				btTest.setText(app.getPlain("Check"));
				btApply.setText(app.getPlain("Back"));
				// btApply.setVisible(false);
				hide();
				setGlassEnabled(false);
				center();
			} else {
				check();
			}
		}
		// }
	}

	private void check() {
		exercise.checkExercise();
		App.debug(String.valueOf(exercise.getResults()));
		App.debug(String.valueOf(exercise.getHints()));
		App.debug(String.valueOf(exercise.getFraction()));

		int k = 1;
		int i = 0; // keep track of the row we're in
		checkAssignmentsTable.setWidget(i, k++, new Label(app.getMenu("Tool")));
		checkAssignmentsTable.setWidget(i, k++,
		        new Label(app.getPlain("Result")));
		checkAssignmentsTable.setWidget(i, k++,
		        new Label(app.getPlain("HintForResult")));
		checkAssignmentsTable.setWidget(i, k++,
		        new Label(app.getPlain("Fraction")));
		i++;

		ArrayList<Assignment> parts = exercise.getParts();
		for (int j = 0; j < parts.size(); j++, i++) {
			final Assignment assignment = parts.get(j);
			Image icon = new Image();
			icon.setUrl(getIconFile(assignment.getIconFileName()));
			k = 0;
			checkAssignmentsTable.setWidget(i, k++, icon);
			checkAssignmentsTable.setWidget(i, k++,
			        new Label(assignment.getToolName()));
			checkAssignmentsTable.setWidget(i, k++, new Label(assignment
			        .getResult().name()));
			checkAssignmentsTable.setWidget(i, k++,
			        new Label(assignment.getHint()));
			checkAssignmentsTable.setWidget(
			        i,
			        k++,
			        new Label(app.getKernel().format(
			                assignment.getFraction() * 100,
			                StringTemplate.defaultTemplate)));
		}

		checkAssignmentsTable.setWidget(i, k = 0,
		        new Label(app.getPlain("FractionTotal")));
		checkAssignmentsTable.setWidget(
		        i,
		        k++,
		        new Label(app.getKernel().format(exercise.getFraction() * 100,
		                StringTemplate.defaultTemplate)));
	}

}
