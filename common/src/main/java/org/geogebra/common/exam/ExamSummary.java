package org.geogebra.common.exam;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.event.CheatingEvent;
import org.geogebra.common.main.exam.event.CheatingEvents;
import org.geogebra.common.util.TimeFormatAdapter;

/**
 * Provides information for the exam details UI (during exams) or exam summary UI (after finishing).
 */
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

	/**
	 * Create a new exam summary.
	 *
	 * @param examType The exam type.
	 * @param startDate The exam start date.
	 * @param finishDate The exam finish date. May be null if the exam is still ongoing.
	 * @param cheatingEvents A list of cheating events recorded during the exam.
	 * @param appConfig The app config (needed for the exam display name).
	 * @param timeFormatter A {@link TimeFormatAdapter} for formatting durations.
	 * @param localization A localization.
	 */
	public ExamSummary(@Nonnull ExamRegion examType,
			@Nonnull Date startDate,
			@Nullable Date finishDate,
			@Nonnull CheatingEvents cheatingEvents,
			@Nonnull AppConfig appConfig,
			@Nonnull  TimeFormatAdapter timeFormatter,
			@Nonnull  Localization localization) {
		isExamFinished = finishDate != null;
		cheated = !cheatingEvents.isEmpty();
		examName = examType.getDisplayName(localization, appConfig);
		title = localization.getMenu("exam_menu_entry") + ": " + (cheated
				? localization.getMenu("exam_alert") : localization.getMenu("OK"));
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

	/**
	 * @return true if the exam is finished, false if ongoing.
	 */
	public boolean isExamFinished() {
		return isExamFinished;
	}

	/**
	 * @return true if cheating events have been recorded.
	 */
	public boolean getCheated() {
		return cheated;
	}

	/**
	 * @return The exam name (see {@link ExamRegion#getDisplayName(Localization, AppConfig)}).
	 */
	public String getExamName() {
		return examName;
	}

	/**
	 * @return The dialog title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return The "show this to your teacher.." text.
	 */
	public String getFinishedInfoText() {
		return finishedInfoText;
	}

	/**
	 * @return Hint text for the exam start date label.
	 */
	public String getStartDateHintText() {
		return startDateHintText;
	}

	/**
	 * @return Value for the exam start date label.
	 */
	public String getStartDateLabelText() {
		return startDateLabelText;
	}

	/**
	 * @return Hint text for the exam start time label.
	 */
	public String getStartTimeHintText() {
		return startTimeHintText;
	}

	/**
	 * @return Value for the exam start time label.
	 */
	public String getStartTimeLabelText() {
		return startTimeLabelText;
	}

	/**
	 * @return Hint text for the exam end time label.
	 */
	public String getEndTimeHintText() {
		return endTimeHintText;
	}

	/**
	 * @return Value for the exam end time label.
	 */
	public String getEndTimeLabelText() {
		return endTimeLabelText;
	}

	/**
	 * @return Hint text for the exam duration label.
	 */
	public String getDurationHintText() {
		return durationHintText;
	}

	/**
	 * @return Value for the exam duration label.
	 */
	public String getDurationLabelText() {
		return durationLabelText;
	}

	/**
	 * @return Hint text for the activities (cheating events) label.
	 */
	public String getActivityHintText() {
		return activityHintText;
	}

	/**
	 * @return Value for the activities (cheating events) label.
	 */
	public String getActivityLabelText() {
		return activityLabelText;
	}
}
