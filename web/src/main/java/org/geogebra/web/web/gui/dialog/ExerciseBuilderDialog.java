package org.geogebra.web.web.gui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.BoolAssignment;
import org.geogebra.common.util.Exercise;
import org.geogebra.common.util.GeoAssignment;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog for editing an {@link Exercise}
 * 
 * @author Christoph
 *
 */
public class ExerciseBuilderDialog extends DialogBoxW implements ClickHandler,
		GeoElementSelectionListener, ChangeHandler {

	private AppW app;
	private Exercise exercise;

	private VerticalPanel mainWidget;
	private FlowPanel bottomWidget;
	private Button btApply, btTest;
	private FlexTable assignmentsTable;
	private FlexTable checkAssignmentsTable;

	private ListBox addList;
	private ArrayList<Object> addListMappings; // TODO? Override add, remove,...
												// instead of relying on calling
												// updateAddList each time

	/**
	 * Brings up a new ExerciseBuilderDialog
	 * 
	 * @param app
	 *            application
	 */
	public ExerciseBuilderDialog(App app) {
		super(false, false, null, ((AppW) app).getPanel());

		this.app = (AppW) app;
		exercise = app.getKernel().getExercise();
		addListMappings = new ArrayList<Object>();
		createGUI();
	}

	@Override
	public void setVisible(boolean flag) {
		super.setVisible(flag);
		update();
		if (flag) {
			app.setMoveMode();
			if (!app.getSelectionManager().getSelectionListeners()
					.contains(this)) {
				app.getSelectionManager().addSelectionListener(this);
			}
		} else {
			app.getSelectionManager().removeSelectionListener(this);
		}
	}

	@Override
	public void center() {
		app.setMoveMode();
		if (!app.getSelectionManager().getSelectionListeners().contains(this)) {
			app.getSelectionManager().addSelectionListener(this);
		}
		super.center();
	}

	@Override
	public void hide() {
		app.getSelectionManager().removeSelectionListener(this);
		super.hide();
	}

	private void createGUI() {
		getCaption().setText(app.getMenu("Exercise.CreateNew"));

		setWidget(mainWidget = new VerticalPanel());

		assignmentsTable = new FlexTable();

		createAssignmentsTable();
		checkAssignmentsTable = new FlexTable();
		checkAssignmentsTable.setVisible(false);

		mainWidget.add(assignmentsTable);
		mainWidget.add(checkAssignmentsTable);

		addList = new ListBox();
		addList.addChangeHandler(this);
		addList.setStyleName("submenuContent");

		buildAddListMappings();
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
		if (exercise.getParts().size() == 0) {
			btTest.setEnabled(false);
		}
		bottomWidget.add(btTest);
		bottomWidget.add(btApply);
	}

	private void buildAddListMappings() {
		addListMappings.clear();
		for (int i = 0; i < app.getKernel().getMacroNumber(); i++) {
			if (!exercise.usesMacro(i)) {
				addListMappings.add(app.getKernel().getMacro(i));
			}
		}

		TreeSet<GeoElement> geos = app.getKernel().getConstruction()
				.getGeoSetConstructionOrder();
		for (GeoElement geo : geos) {
			if (geo instanceof GeoBoolean) {
				if (!exercise.usesBoolean((GeoBoolean) geo)) {
					addListMappings.add(geo);
				}
			}
		}
		updateAddList();
	}

	private void createAssignmentsTable() {
		assignmentsTable.removeAllRows();
		assignmentsTable.setWidget(0, 1, new Label(app.getPlain("Tool")));
		assignmentsTable.setWidget(0, 2,
				new Label(app.getPlain("HintForCorrect")));
		assignmentsTable.setWidget(0, 3, new Label(app.getPlain("Fraction")));

		addAssignmentsTableRows();
	}

	private void addAssignmentsTableRows() {
		for (Assignment assignment : exercise.getParts()) {
			appendAssignmentRow(assignment);
		}
	}

	private void appendAssignmentRow(final Assignment assignment) {
		int row = assignmentsTable.getRowCount();
		addAssignmentRow(assignment, row + 1);
	}

	private void addAssignmentRow(final Assignment assignment, int insertrow) {
		int j = 0;
		int row = (insertrow <= assignmentsTable.getRowCount()) ? assignmentsTable
				.insertRow(insertrow) : insertrow;

		Image icon = new Image();
		icon.setUrl(getIconFile(assignment.getIconFileName()));
		assignmentsTable.setWidget(row, j++, icon);
		assignmentsTable.setWidget(row, j++,
				new Label(assignment.getDisplayName()));

		final TextBox textForSolvedAssignment = getHintTextBox(assignment,
				Result.CORRECT);

		assignmentsTable.setWidget(row, j++, textForSolvedAssignment);

		final ListBox fractions = getFractionsLB(assignment, Result.CORRECT);
		assignmentsTable.setWidget(row, j++, fractions);

		Image delIcon = getDeleteIcon(assignment);
		// assignment
		assignmentsTable.setWidget(row, j++, delIcon);

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

	private void update() {
		exercise.notifyUpdate();
		createAssignmentsTable();
		updateAddList();
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
		if (assignment instanceof GeoAssignment) {
			addListMappings.add(((GeoAssignment) assignment).getTool());
			updateAddList();
		} else if (assignment instanceof BoolAssignment) {
			addListMappings.add(((BoolAssignment) assignment).getGeoBoolean());
			updateAddList();
		}
		exercise.remove(assignment);
		if (exercise.getParts().size() == 0) {
			btTest.setEnabled(false);
		}
		assignmentsTable.removeRow(assignmentsTable.getCellForEvent(event)
				.getRowIndex());
		center();
	}

	private void updateAddList() {
		addList.clear();

		addList.addItem(app.getPlain("AddToolOrBoolean"));
		addList.addItem(app.getMenu("Tool.CreateNew"));
		for (Object obj : addListMappings) {
			if (obj instanceof Macro) {
				addList.addItem(((Macro) obj).getToolName());
			} else if (obj instanceof GeoBoolean) {
				addList.addItem(((GeoBoolean) obj).getNameDescription());
			}
		}
	}

	/**
	 * @param fileName
	 *            of user defined tool
	 * @return {@link SafeUri} of Icon with fileName
	 */
	SafeUri getIconFile(String fileName) {
		if (BoolAssignment.class.getSimpleName().equals(fileName)) {
			return ((ImageResource) GGWToolBar.getMyIconResourceBundle()
					.mode_showcheckbox_32()).getSafeUri();
		} else if (!fileName.isEmpty()) {
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
				addList.setVisible(true);
				btTest.setText(app.getPlain("Test"));
				btApply.setText(app.getPlain("OK"));
				hide();
				center();
				if (!app.getSelectionManager().getSelectionListeners()
						.contains(this)) {
					app.getSelectionManager().addSelectionListener(this);
				}
			}
		} else if (target == btTest.getElement()) {
			if (isEditing) {
				assignmentsTable.setVisible(false);
				checkAssignmentsTable.setVisible(true);
				addList.setVisible(false);
				check();
				btTest.setText(app.getPlain("Check"));
				btApply.setText(app.getPlain("Back"));
				hide();
				center();
				app.getSelectionManager().removeSelectionListener(this);
			} else {
				check();
			}
		}
	}

	private void check() {
		if (exercise.getParts().size() > 0) {
			exercise.checkExercise();
			checkAssignmentsTable.removeAllRows();
			int k = 1;
			int i = 0; // keep track of the row we're in
			checkAssignmentsTable.setWidget(i, k++,
					new Label(app.getMenu("Tool")));
			checkAssignmentsTable.setWidget(i, k++,
					new Label(app.getPlain("Result.Exercise")));
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
						new Label(assignment.getDisplayName()));
				checkAssignmentsTable.setWidget(i, k++, new Label(assignment
						.getResult().name()));
				String hint = assignment.getHint();
				if (hint != null && hint.length() > 30) {
					hint = hint.substring(0, hint.indexOf(" ", 25)) + "...";
				}
				checkAssignmentsTable.setWidget(i, k++, new Label(hint));
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
					new Label(app.getKernel().format(
							exercise.getFraction() * 100,
							StringTemplate.defaultTemplate)));
		}
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
			btTest.setEnabled(true);
			center();
		}
	}

	/**
	 * Adds an Assignment to the Exercise
	 * 
	 * @param listBoxIndex
	 *            the index of the Object to check for in addListMappings
	 */
	void addAssignment(int listBoxIndex) {
		Object obj = addListMappings.remove(listBoxIndex);
		if (obj instanceof Macro) {
			addAssignment((Macro) obj);
		} else if (obj instanceof GeoBoolean) {
			addAssignment((GeoBoolean) obj);
		}
		addListMappings.remove(obj);
		updateAddList();
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (geo instanceof GeoBoolean) {
			addAssignment(geo);
			addListMappings.remove(geo);
			updateAddList();
		} else {
			boolean isDependentObject = false;
			for (GeoElement geoP : geo.getAllPredecessors()) {
				isDependentObject |= geoP.isLabelSet();
			}
			if (isDependentObject && isVisible()) {
				setVisible(false);
				newTool();
			}
		}
	}

	private void addAssignment(GeoElement geo) {
		if (geo instanceof GeoBoolean) {
			GeoBoolean check = (GeoBoolean) geo;
			if (!exercise.usesBoolean(check)) {
				Assignment a = exercise.addAssignment(check);
				appendAssignmentRow(a);
				btTest.setEnabled(true);
				center();
			}
		}
	}

	public void onChange(ChangeEvent event) {
		int selectedIndex = addList.getSelectedIndex();
		if (selectedIndex == 1) {
			newTool();
		} else if (selectedIndex > 1) {
			addAssignment(selectedIndex - 2);
		}
		event.stopPropagation();
		addList.setSelectedIndex(0);
	}

	private void newTool() {
		setVisible(false);
		ToolCreationDialog toolCreationDialog = new ToolCreationDialog(app,
				new AsyncOperation() {
					@Override
					public void callback(Object obj) {
						if (obj instanceof Macro) {
							Macro macro = (Macro) obj;
							if (app.getKernel().getMacroID(macro) >= 0) {
								addAssignment(macro);
							}
						}
						ExerciseBuilderDialog.this.setVisible(true);
					}
				});
		toolCreationDialog.center();
	}

}
