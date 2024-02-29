package org.geogebra.common.exam;

import java.util.Date;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.event.CheatingEvent;
import org.geogebra.common.main.exam.event.CheatingEvents;
import org.geogebra.common.util.TimeFormatAdapter;

public final class ExamSummary {

	public final boolean cheated;
	public final String examName;
	public final String title;
	public final String finishedInfoText;
	public final String startDateHintText;
	public final String startDateLabelText;
	public final String startTimeHintText;
	public final String startTimeLabelText;
	public final String endTimeHintText;
	public final String endTimeLabelText;
	public final String durationHintText;
	public final String durationLabelText;
	public final String activityHintText;
	public final String activityLabelText;
	private final TimeFormatAdapter timeFormatter = FormatFactory.getPrototype().getTimeFormat();

	private static String formatDate(Date date, Localization localization) {
		// copied over from ExamEnvironment
		return CmdGetTime.buildLocalizedDate("\\j \\F \\Y", date, localization);
	}

	private static String formatTime(Date date, Localization localization) {
		// copied over from ExamEnvironment
		return CmdGetTime.buildLocalizedDate("\\H:\\i:\\s", date, localization);
	}

	public ExamSummary(ExamRegion examType, Date startDate, Date endDate,
			CheatingEvents cheatingEvents, AppConfig appConfig, Localization localization) {
		this.cheated = !cheatingEvents.isEmpty();
		examName = examType.getDisplayName(localization, appConfig);
		title = localization.getMenu("exam_menu_entry") + (cheated ? localization.getMenu("exam_alert") : localization.getMenu("OK"));
		finishedInfoText = localization.getMenu("exam_log_show_screen_to_teacher");
		durationHintText = localization.getMenu("Duration");
		durationLabelText = timeFormatter.format(localization.getLanguageTag(), endDate.getTime() - startDate.getTime());
		startDateHintText = localization.getMenu("exam_start_date");
		startDateLabelText = formatDate(startDate, localization);
		startTimeHintText = localization.getMenu("exam_start_time");
		startTimeLabelText = formatTime(startDate, localization);
		endTimeHintText = localization.getMenu("exam_end_time");
		endTimeLabelText = formatTime(endDate, localization);
		activityHintText = localization.getMenu("exam_activity");
		activityLabelText = getActivityLog(startDate, endDate, cheatingEvents, localization);
	}

	private String getActivityLog(Date startDate, Date endDate,
			CheatingEvents cheatingEvents, Localization localization) {
		StringBuilder sb = new StringBuilder();
		sb.append(localization.getMenu("exam_start_date") + ": " +
				formatDate(startDate, localization) + "\n");
		sb.append(localization.getMenu("exam_start_time") + ": " +
				formatTime(startDate, localization) + "\n");
		sb.append(localization.getMenu("exam_end_time") + ": " +
				formatTime(endDate, localization) + "\n");

		sb.append(localization.getMenu("exam_activity") + ":\n");
		sb.append("0:00").append(' ')
				.append(localization.getMenu("exam_started") + "\n");
		for (CheatingEvent cheatingEvent : cheatingEvents.getEvents()) {
			sb.append(formatTime(cheatingEvent.getDate(), localization));
			sb.append(' ');
			sb.append(cheatingEvent.getAction().toString(localization));
			sb.append("\n");
		}
		sb.append(formatTime(endDate, localization)).append(' ')
				.append(localization.getMenu("exam_ended")).append("\n");

		return sb.toString();
	}
}
