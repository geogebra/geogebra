/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.toolbarpanel;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValuesStatisticsViewModel;
import org.geogebra.common.gui.view.table.TableValuesStatisticsViewModel.Content;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.states.State;
import org.geogebra.common.util.AttributedString;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentToast;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.Widget;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class StatsSideSheetTV {
	private final AppW app;
	private FlowPanel statPanel;
	private final ComponentSideSheet sideSheet;
	private State.Subscription dataUpdateRegistration;

	/**
	 * @param app application
	 * @param data side sheet data
	 */
	public StatsSideSheetTV(AppW app, SideSheetData data) {
		this.app = app;
		sideSheet = new ComponentSideSheet(app, data);
		sideSheet.addStyleName("statistics");
		sideSheet.addAttachHandler(evt -> {
			if (!evt.isAttached()) {
				cancelRegistration();
			}
		});
	}

	private void buildSideSheet(String subTitle) {
		Label subTitleLabel = BaseWidgetFactory.INSTANCE.newPrimaryText("", "subTitle");
		subTitleLabel.getElement().setInnerHTML(subTitle);
		sideSheet.addToContent(subTitleLabel);
	}

	/**
	 * @param rowData row data
	 */
	public void setRowsAndShow(State<List<StatisticGroup>> rowData) {
		cancelRegistration();
		setRows(rowData.get());
		this.dataUpdateRegistration = rowData.subscribe(this::setRows);
		sideSheet.show();
	}

	private void cancelRegistration() {
		if (dataUpdateRegistration != null) {
			dataUpdateRegistration.cancel();
		}
	}

	private void setRows(List<StatisticGroup> statistics) {
		if (statPanel != null) {
			statPanel.removeFromParent();
		}
		this.statPanel = new FlowPanel();
		renderGroups(statistics, app, statPanel);
		sideSheet.addToContent(statPanel);
	}

	/**
	 * Convert statistics data into UI elements and add them to a panel.
	 * @param statistics statistics result
	 * @param app app
	 * @param parent parent panel
	 */
	public static void renderGroups(List<StatisticGroup> statistics, AppW app, Panel parent) {
		for (StatisticGroup statisticGroup : statistics) {
			FlowPanel group = new FlowPanel();
			group.addStyleName("group");

			addHeadingIfExists(statisticGroup.heading(), parent);

			for (StatisticGroup.Row row : statisticGroup.rows()) {
				FlowPanel rowPanel = new FlowPanel();
				rowPanel.addStyleName("row");
				rowPanel.add(BaseWidgetFactory.INSTANCE.newPrimaryText(row.label(), "label"));
				if (row.isLaTeX()) {
					addLatexCanvasBasedRow(app, row, rowPanel);
				} else {
					addValueRow(app, row, rowPanel);
				}

				addCopyButton(app, row, rowPanel, app.getLocalization().getMenu("General.CopyValue"));
				group.add(rowPanel);
			}
			parent.add(group);
		}
	}

	/**
	 * Creates and adds heading label to parent panel.
	 * @param statHeading {@link AttributedString}
	 * @param parent panel
	 */
	private static void addHeadingIfExists(AttributedString statHeading, Panel parent) {
		if (statHeading != null) {
			Label heading = BaseWidgetFactory.INSTANCE.newPrimaryText("", "heading");
			heading.getElement().setInnerHTML(TableUtil.toHtml(statHeading));
			parent.add(heading);
		}
	}

	/**
	 * Creates and adds a scrollable canvas row to the parent, showing the latex content.
	 * @param app {@link AppW}
	 * @param row {@link StatisticGroup.Row}
	 * @param rowPanel row panel
	 */
	private static void addLatexCanvasBasedRow(AppW app, StatisticGroup.Row row, FlowPanel rowPanel) {
		FlowPanel canvasWrapper = new FlowPanel();
		canvasWrapper.addStyleName("canvasWrapper");
		Canvas canvas = Canvas.createIfSupported();
		((DrawEquationW) app.getDrawEquation())
				.paintOnCleanCanvas(row.value(), canvas, 16, GColor.newColor(0, 0, 0, 0.87), false);
		canvasWrapper.add(canvas);
		rowPanel.add(canvasWrapper);
	}

	/**
	 * Creates and adds a label to the row. If the value is too long, will be shown with
	 * ellipsis and on hover a tooltip is visible with the whole value.
	 * @param app {@link AppW}
	 * @param row {@link StatisticGroup.Row}
	 * @param rowPanel row panel
	 */
	private static void addValueRow(AppW app, StatisticGroup.Row row, FlowPanel rowPanel) {
		Label valueLbl = BaseWidgetFactory.INSTANCE.newPrimaryText(row.value(), "value");
		rowPanel.add(valueLbl);
		ComponentToast toast = new ComponentToast(app, row.value());
		valueLbl.addMouseOverHandler(event -> showTooltipIfNecessary(valueLbl, toast, app));
		valueLbl.addMouseOutHandler(event -> toast.hide());
	}

	/**
	 * Tooltip only shown if value label longer than available space.
	 * @param widget value label
	 * @param toast {@link ComponentToast}
	 * @param app {@link AppW}
	 */
	private static void showTooltipIfNecessary(Widget widget, ComponentToast toast, AppW app) {
		if (widget.getOffsetWidth() < widget.getElement().getScrollWidth()) {
			app.getAppletFrame().add(toast);
			toast.setPopupPosition(widget.getAbsoluteLeft(), widget.getAbsoluteTop() - 32);
			Scheduler.get().scheduleDeferred(() -> toast.addStyleName("fadeIn"));
		} else {
			toast.hide();
		}
	}

	/**
	 * Creates and adds a copy button, which is visible on row hover. It copies the
	 * value of the row to the clipboard.
	 * @param app {@link AppW}
	 * @param row {@link StatisticGroup.Row}
	 * @param parent parent panel
	 * @param buttonText localizes text of the button
	 */
	private static void addCopyButton(
			AppW app, StatisticGroup.Row row, FlowPanel parent, String buttonText) {
		if (row.clipboardValue() == null) {
			return;
		}

		StandardButton copyButton = new StandardButton(
				MaterialDesignResources.INSTANCE
						.copy_black()
						.withFill(GeoGebraColorConstants.NEUTRAL_700.toString()),
				16);
		Dom.addEventListener(
				copyButton.getElement(),
				"mouseover",
				event -> copyButton.setIcon(MaterialDesignResources.INSTANCE
						.copy_black()
						.withFill(GeoGebraColorConstants.NEUTRAL_900.toString())));
		Dom.addEventListener(
				copyButton.getElement(),
				"mouseout",
				event -> copyButton.setIcon(MaterialDesignResources.INSTANCE
						.copy_black()
						.withFill(GeoGebraColorConstants.NEUTRAL_700.toString())));
		copyButton.addStyleName("valueCopyButton");
		copyButton.setTooltipPositionRight();
		copyButton.setTitle(buttonText);

		Dom.addEventListener(parent.getElement(), "mouseover", event -> {
			copyButton.removeStyleName("transitionOut");
			copyButton.addStyleName("transitionIn");
		});
		Dom.addEventListener(parent.getElement(), "mouseout", event -> {
			copyButton.removeStyleName("transitionIn");
			copyButton.addStyleName("transitionOut");
		});

		copyButton.addFastClickHandler(source -> {
			CopyPasteW.writeToExternalClipboardWithFallback(row.clipboardValue(), null);
			app.getToolTipManager()
					.showBottomMessage(app.getLocalization().getMenu("CopiedToClipboard"), app);
		});

		parent.add(copyButton);
	}

	/**
	 * Add regression UI and show
	 * @param plotActionHandler callback to plot the selected regression curve
	 */
	public void addRegressionChooser(
			TableValuesStatisticsViewModel model,
			@NonNull State<@NonNull List<StatisticGroup>> groups,
			@NonNull State<@NonNull List<String>> models,
			@Nullable Runnable plotActionHandler) {
		List<String> items = models.get();

		ComponentDropDown regressionChooser =
				new ComponentDropDown(app, app.getLocalization().getMenu("RegressionModel"), items, 0);
		regressionChooser.setFullWidth(true);
		regressionChooser.addChangeHandler(() -> {
			model.selectedRegressionIndexChanged(regressionChooser.getSelectedIndex());
			setRows(groups.get());
		});

		sideSheet.addToContent(regressionChooser);

		if (plotActionHandler != null) {
			sideSheet.addPositiveButtonRunnable(plotActionHandler);
		}
		setRowsAndShow(groups);
	}

	/**
	 * Add error panel and show.
	 * @param errorMessage error message to show
	 */
	public void showError(String errorMessage) {
		cancelRegistration();
		sideSheet.addStyleName("error");
		InfoErrorData errorData = new InfoErrorData(
				app.getLocalization().getMenu("StatsDialog.NoData"),
				errorMessage,
				null,
				MaterialDesignResources.INSTANCE.bar_chart_black());
		ComponentInfoErrorPanel infoPanel =
				new ComponentInfoErrorPanel(app.getLocalization(), errorData, null);
		sideSheet.addToContent(infoPanel);
		sideSheet.show();
	}

	/**
	 * @param content view content; if null, view will be closed
	 * @param model table stats model
	 */
	public void update(Content content, TableValuesStatisticsViewModel model) {
		if (content instanceof Content.Statistics stats) {
			showStatisticsDialog(content.title(), content.header(), stats.groups());
		} else if (content instanceof Content.Regression regression) {
			showRegressionDialog(
					content.title(),
					content.header(),
					model,
					regression.groups(),
					regression.regressionModels(),
					regression.plotAction());
		} else if (content instanceof Content.Error err) {
			showErrorDialog(err.title(), err.header(), err.message());
		} else { // null or invalid
			sideSheet.close();
		}
	}

	private void showStatisticsDialog(
			@NonNull String title,
			@NonNull AttributedString header,
			@NonNull State<List<StatisticGroup>> statisticGroups) {
		SideSheetData sideSheetData = new SideSheetData(title, null, null);
		sideSheet.update(sideSheetData);
		buildSideSheet(TableUtil.toHtml(header));
		setRowsAndShow(statisticGroups);
	}

	private void showRegressionDialog(
			@NonNull String title,
			@NonNull AttributedString header,
			TableValuesStatisticsViewModel model,
			@NonNull State<@NonNull List<StatisticGroup>> groups,
			@NonNull State<@NonNull List<String>> models,
			@Nullable Runnable plotActionHandler) {
		SideSheetData sideSheetData =
				new SideSheetData(title, null, plotActionHandler != null ? "Plot" : null);
		sideSheet.update(sideSheetData);
		buildSideSheet(TableUtil.toHtml(header));
		addRegressionChooser(model, groups, models, plotActionHandler);
	}

	private void showErrorDialog(
			@NonNull String title, @NonNull AttributedString header, @NonNull String errorMessage) {
		SideSheetData sideSheetData = new SideSheetData(title, null, null);
		sideSheet.update(sideSheetData);
		buildSideSheet(TableUtil.toHtml(header));
		showError(errorMessage);
	}
}
