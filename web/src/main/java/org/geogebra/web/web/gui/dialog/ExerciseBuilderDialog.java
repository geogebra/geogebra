package org.geogebra.web.web.gui.dialog;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Exercise;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.toolbar.ToolbarSubemuW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog for editing an {@link Exercise}
 * 
 * @author Christoph
 *
 */
public class ExerciseBuilderDialog extends DialogBoxW implements ClickHandler,
		MouseDownHandler, MouseUpHandler, TouchStartHandler, TouchEndHandler {

	private AppW app;
	private Exercise exercise;

	private VerticalPanel mainWidget;
	private FlowPanel bottomWidget;
	private Button btApply, btTest;
	private FlexTable assignmentsTable;
	private FlexTable checkAssignmentsTable;

	// private ExerciseBuilderDOMHandler exerciseBuilderHandler;
	private UnorderedList addList;
	private ToolbarSubemuW userAddModes;

	public void onTouchEnd(TouchEndEvent event) {
		onEnd(event);
	}

	public void onTouchStart(TouchStartEvent event) {
		// TODO Auto-generated method stub

	}

	public void onMouseUp(MouseUpEvent event) {
		onEnd(event);
	}

	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub

	}

	private void onEnd(DomEvent<?> event) {

		Element relativeElement = event.getRelativeElement();
		String modeS = relativeElement.getAttribute("mode");
		// Element target = event.getNativeEvent().getEventTarget().cast();
		if (addList.getElement().isOrHasChild(relativeElement)) {
			if (modeS.isEmpty()) {
				handleAddClick();
			} else {
				int mode = Integer.parseInt(modeS);
				addAssignment(mode);
				relativeElement.removeFromParent();
				userAddModes.setVisible(false);
			}
			event.stopPropagation();
		} else {
			userAddModes.setVisible(false);
			event.stopPropagation();
		}
	}

	/**
	 * If add symbol is clicked, <br />
	 * a ToolCreationDialog will be created, if there are no (more) Macros which
	 * can be used for the Exercise <br />
	 * or a SubMenu like chooser of the tools which can be used for the Exercise
	 * will be shown.
	 */
	void handleAddClick() {
		if (app.getKernel().getMacroNumber() == 0
				|| app.getKernel().getMacroNumber() <= exercise.getParts()
						.size()) {
			newTool();
		} else {
			userAddModes.setVisible(true);
		}
	}

	private void newTool() {
		this.setVisible(false);
		ToolCreationDialog toolCreationDialog = new ToolCreationDialog(app,
				new AsyncOperation() {
					@Override
					public void callback(Object obj) {
						if (obj != null) {
							Macro macro = (Macro) obj;
							addAssignment(macro);
						}
						ExerciseBuilderDialog.this.setVisible(true);
					}
				});
		toolCreationDialog.center();

	}

	/**
	 * Brings up a new ExerciseBuilderDialog
	 * 
	 * @param app
	 *            application
	 */
	public ExerciseBuilderDialog(App app) {
		super(false, false, null, ((AppW) app).getPanel());

		this.app = (AppW) app;
		// exerciseBuilderHandler = new ExerciseBuilderDOMHandler();
		exercise = app.getKernel().getExercise();
		if (exercise.isEmpty()) {
			exercise.initStandardExercise();
		}
		createGUI();
	}

	private void createGUI() {

		getCaption().setText(app.getMenu("Exercise.CreateNew"));

		setWidget(mainWidget = new VerticalPanel());
		addDomHandlers(mainWidget);
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

		addList = new UnorderedList();
		addDomHandlers(addList);
		// addIcon = new ListItem();
		Image addIcon = new Image(GuiResources.INSTANCE.menu_icon_file_new());
		ListItem addListItem = new ListItem();
		addListItem.addStyleName("toolbar_item");
		addListItem.add(addIcon);
		addList.add(addListItem);

		userAddModes = new ToolbarSubemuW(app, 1);
		userAddModes.addStyleName("toolbar_item");
		userAddModes.setVisible(false);
		for (int i = 0; i < app.getKernel().getMacroNumber(); i++) {
			if (!exercise.usesMacro(i)) {
				ListItem item = userAddModes.addItem(i
						+ EuclidianConstants.MACRO_MODE_ID_OFFSET);
				addDomHandlers(item);
			}
		}
		addList.add(userAddModes);
		mainWidget.add(addList);

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
		for (Assignment assignment : exercise.getParts()) {
			appendAssignmentRow(assignment);
		}
	}

	private void addDomHandlers(Widget w) {
		w.addDomHandler(this, MouseDownEvent.getType());
		w.addDomHandler(this, MouseUpEvent.getType());
		w.addDomHandler(this, TouchStartEvent.getType());
		w.addDomHandler(this, TouchEndEvent.getType());
	}

	private void appendAssignmentRow(final Assignment assignment) {
		int row = assignmentsTable.getRowCount();
		addAssignmentRow(assignment, row + 1);
	}

	private void addAssignmentRow(final Assignment assignment, int insertrow) {
		int j = 0;
		int row = (insertrow <= assignmentsTable.getRowCount()) ? assignmentsTable
				.insertRow(insertrow) : insertrow;

		Image delIcon = getDeleteIcon(assignment);
		// assignment
		assignmentsTable.setWidget(row, j++, delIcon);

		Image icon = new Image();
		icon.setUrl(getIconFile(assignment.getIconFileName()));
		assignmentsTable.setWidget(row, j++, icon);
		assignmentsTable.setWidget(row, j++,
				new Label(assignment.getToolName()));

		final TextBox textForSolvedAssignment = getHintTextBox(assignment,
				Result.CORRECT);

		assignmentsTable.setWidget(row, j++, textForSolvedAssignment);

		final ListBox fractions = getFractionsLB(assignment, Result.CORRECT);
		assignmentsTable.setWidget(row, j++, fractions);

		Image editIcon = new Image(GuiResources.INSTANCE.menu_icon_edit());
		editIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				handleEditClick(assignment);
			}
		});
		assignmentsTable.setWidget(row, j++, editIcon);
	}

	/**
	 * Brings up a new AssignmentEditDialog to edit the assignment and hides
	 * this ExerciseBuilderDialog
	 * 
	 * @param assignment
	 *            The Assignment to be edited.
	 */
	void handleEditClick(Assignment assignment) {
		new AssignmentEditDialog(app, assignment, ExerciseBuilderDialog.this)
				.center();
		hide();
	}

	/**
	 * @param assignment
	 *            the assignment for which the Listbox should set the fraction
	 * @param res
	 *            the result of the assignment for which the Listbox should set
	 *            the fraction
	 * @return a single select ListBox containing all possible fractions as
	 *         defined in {@link Assignment#FRACTIONS} setting the fraction for
	 *         a result in this assignment when they are changed
	 */
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
						Assignment.FRACTIONS,
						assignment.getFractionForResult(res)));
			}
		});

		return fractions;
	}

	/**
	 * @param assignment
	 *            the assignment for which the TextBox should set the hint
	 * @param res
	 *            the result of the assignment for which the TextBox should set
	 *            the hint
	 * @return a TextBox setting the hint for a result in this assignment it is
	 *         changed
	 */
	TextBox getHintTextBox(final Assignment assignment, final Result res) {
		final TextBox textForResult = new TextBox();

		textForResult.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				assignment.setHintForResult(res, textForResult.getText());
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
				handleAssignmentDeleteClick(event, assignment);
			}
		});
		return delIcon;
	}

	/**
	 * Handles the remove of an Assignment from the Exercise
	 * 
	 * @param event
	 *            the original event to determine which row should be removed
	 *            from Table
	 * @param assignment
	 *            the assignment to remove from the Exercise
	 */
	void handleAssignmentDeleteClick(ClickEvent event, Assignment assignment) {
		ListItem item = userAddModes.addItem(app.getKernel().getMacroID(
				assignment.getTool())
				+ EuclidianConstants.MACRO_MODE_ID_OFFSET);
		addDomHandlers(item);
		exercise.remove(assignment);
		assignmentsTable.removeRow(assignmentsTable.getCellForEvent(event)
				.getRowIndex());
	}

	/**
	 * @param fileName
	 *            of user defined tool
	 * @return {@link SafeUri} of Icon with fileName
	 */
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
				btApply.setText(app.getPlain("OK"));
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
				hide();
				setGlassEnabled(false);
				center();
			} else {
				check();
			}
		}
	}

	private void check() {
		exercise.checkExercise();

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

	/**
	 * Adds a user defined tool to the exercise as well as the view
	 * 
	 * @param macro
	 *            the user defined tool which should be added to the exercise as
	 *            well as the view
	 */
	void addAssignment(Macro macro) {
		if (!exercise.usesMacro(macro)) {
			Assignment a = exercise.addAssignment(macro);
			appendAssignmentRow(a);
		}
		userAddModes.setVisible(false);
	}

	/**
	 * Adds a user defined tool to the exercise as well as the view
	 * 
	 * @param mode
	 *            the ID of the user defined tool which should be added to the
	 *            exercise as well as the view
	 */
	void addAssignment(int mode) {
		addAssignment(app.getKernel().getMacro(
				mode - EuclidianConstants.MACRO_MODE_ID_OFFSET));
	}

}
