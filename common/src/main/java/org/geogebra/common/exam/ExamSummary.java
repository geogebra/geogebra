package org.geogebra.common.exam;

import java.util.Date;

import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.event.CheatingEvent;
import org.geogebra.common.main.exam.event.CheatingEvents;
import org.geogebra.common.util.TimeFormatAdapter;

public final class ExamSummary {

	private boolean isExamFinished;
	private boolean cheated;
	private String examName;
	private String title;
	private String finishedInfoText;
	private String startDateHintText;
	private String startDateLabelText;
	private String startTimeHintText;
	private String startTimeLabelText;
	private String endTimeHintText;
	private String endTimeLabelText = "";
	private String durationHintText;
	private String durationLabelText = "";
	private String activityHintText;
	public String activityLabelText;

	private static String formatDate(Date date, Localization localization) {
		// copied over from ExamEnvironment
		return CmdGetTime.buildLocalizedDate("\\j \\F \\Y", date, localization);
	}

	private static String formatTime(Date date, Localization localization) {
		// copied over from ExamEnvironment
		return CmdGetTime.buildLocalizedDate("\\H:\\i:\\s", date, localization);
	}

	public ExamSummary(ExamRegion examType, Date startDate, Date finishDate,
			CheatingEvents cheatingEvents, AppConfig appConfig, TimeFormatAdapter timeFormatter,
			Localization localization) {
		isExamFinished = finishDate != null;
		cheated = !cheatingEvents.isEmpty();
		examName = examType.getDisplayName(localization, appConfig);
		title = localization.getMenu("exam_menu_entry") + ": " + (cheated ? localization.getMenu("exam_alert") : localization.getMenu("OK"));
		finishedInfoText = localization.getMenu("exam_log_show_screen_to_teacher");
		durationHintText = localization.getMenu("Duration");
		if (finishDate != null) {
			durationLabelText = timeFormatter.format(localization.getLanguageTag(),
					finishDate.getTime() - startDate.getTime());
			endTimeLabelText = formatTime(finishDate, localization);
		}
		startDateHintText = localization.getMenu("exam_start_date");
		startDateLabelText = formatDate(startDate, localization);
		startTimeHintText = localization.getMenu("exam_start_time");
		startTimeLabelText = formatTime(startDate, localization);
		endTimeHintText = localization.getMenu("exam_end_time");
		activityHintText = localization.getMenu("exam_activity");
		activityLabelText = getActivityLog(startDate, finishDate, cheatingEvents, localization);
	}

	private String getActivityLog(Date startDate, Date finishDate,
			CheatingEvents cheatingEvents, Localization localization) {
		StringBuilder sb = new StringBuilder();
		sb.append(localization.getMenu("exam_start_date")).append(": ")
				.append(formatDate(startDate, localization)).append("\n");
		sb.append(localization.getMenu("exam_start_time")).append(": ")
				.append(formatTime(startDate, localization)).append("\n");
		if (finishDate != null) {
			sb.append(localization.getMenu("exam_end_time")).append(": ")
					.append(formatTime(finishDate, localization)).append("\n");
		}

		sb.append(localization.getMenu("exam_activity")).append(":\n");
		sb.append("0:00").append(' ')
				.append(localization.getMenu("exam_started")).append("\n");
		for (CheatingEvent cheatingEvent : cheatingEvents.getEvents()) {
			sb.append(formatTime(cheatingEvent.getDate(), localization));
			sb.append(' ');
			sb.append(cheatingEvent.getAction().toString(localization));
			sb.append("\n");
		}
		if (finishDate != null) {
			sb.append(formatTime(finishDate, localization)).append(' ')
					.append(localization.getMenu("exam_ended")).append("\n");
		}
		return sb.toString();
	}

	public boolean isExamFinished() {
		return isExamFinished;
	}

	public boolean getCheated() {
		return cheated;
	}

	public String getExamName() {
		return examName;
	}

	public String getTitle() {
		return title;
	}

	public String getFinishedInfoText() {
		return finishedInfoText;
	}

	public String getStartDateHintText() {
		return startDateHintText;
	}

	public String getStartDateLabelText() {
		return startDateLabelText;
	}

	public String getStartTimeHintText() {
		return startTimeHintText;
	}

	public String getStartTimeLabelText() {
		return startTimeLabelText;
	}

	public String getEndTimeHintText() {
		return endTimeHintText;
	}

	public String getEndTimeLabelText() {
		return endTimeLabelText;
	}

	public String getDurationHintText() {
		return durationHintText;
	}

	public String getDurationLabelText() {
		return durationLabelText;
	}

	public String getActivityHintText() {
		return activityHintText;
	}

	public String getActivityLabelText() {
		return activityLabelText;
	}
}
