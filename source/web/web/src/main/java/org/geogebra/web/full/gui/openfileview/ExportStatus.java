package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;

public enum ExportStatus {
	NOT_STARTED("not_started"),
	PENDING("pending"),
	IN_PROGRESS("in_progress"),
	AVAILABLE("available"),
	ERROR("error");

	public static final String DOWNLOAD_ALL = "Alle dateien herunterladen";
	private final String status;
	private final static String MESSAGE_PENDING_IN_PROGRESS = "Download wird vorbereitet...";
	private final static String MESSAGE_AVAILABLE = "Download bereit";
	private final static String MESSAGE_ERROR = "Download fehlgeschlagen";
	private final static String HELP_PENDING_IN_PROGRESS = "Der Download aller Tafelbilder"
			+ " wird vorbereitet und startet in Kürze automatisch. Die Dauer variiert abhängig"
			+ " von Gesamtdateigröße und Verbindungsgeschwindigkeit. Bitte verlassen Sie diese"
			+ " Seite nicht, bis der Download im Browser abgeschlossen ist.";
	private final static String HELP_AVAILABLE = "Die ZIP-Datei mit allen "
			+ "Tafelbildern ist jetzt verfügbar. Klicken Sie auf die Schaltfläche, um den"
			+ " Download zu starten.";
	private final static String HELP_ERROR = "Beim Erstellen des Downloads"
			+ " ist ein Fehler aufgetreten. Bitte versuchen Sie es erneut.";

	ExportStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * Retrieve ExportStatus from string
	 * @param status string representing status
	 * @return {@link ExportStatus}
	 */
	public static ExportStatus getStatus(String status) {
		switch (status) {
		case "not_started":
			return NOT_STARTED;
		case "pending":
			return PENDING;
		case "in_progress":
			return IN_PROGRESS;
		case "available":
			return AVAILABLE;
		case "error":
		default:
			return ERROR;
		}
	}

	/**
	 * Retrieve colored icon based on {@link ExportStatus}
	 * @param status {@link ExportStatus}
	 * @return purple info icon for {@link ExportStatus#PENDING} and
	 * {@link ExportStatus#IN_PROGRESS}, red error icon fon {@link ExportStatus#ERROR},
	 * null otherwise
	 */
	public static SVGResource getStatusIcon(ExportStatus status) {
		switch (status) {
		case PENDING:
		case IN_PROGRESS:
		case AVAILABLE:
			return MaterialDesignResources.INSTANCE.info_black().withFill(
					GeoGebraColorConstants.MEBIS_PURPLE_A400.toString());
		case ERROR:
			return MaterialDesignResources.INSTANCE.error().withFill(GColor.ERROR.toString());
		case NOT_STARTED:
		default:
			return null;
		}
	}

	/**
	 * Retrieve main message of status to inform user
	 * @param status {@link ExportStatus}
	 * @return message of status
	 */
	public static String getStatusMessage(ExportStatus status) {
		switch (status) {
		case PENDING:
		case IN_PROGRESS:
			return MESSAGE_PENDING_IN_PROGRESS;
		case AVAILABLE:
			return MESSAGE_AVAILABLE;
		case ERROR:
			return MESSAGE_ERROR;
		default:
			return null;
		}
	}

	/**
	 * Retrieve help message (more detail than {@link ExportStatus#getStatusMessage(ExportStatus)})
	 * @param status {@link ExportStatus}
	 * @return help message
	 */
	public static String getStatusHelp(ExportStatus status) {
		switch (status) {
		case PENDING:
		case IN_PROGRESS:
			return HELP_PENDING_IN_PROGRESS;
		case AVAILABLE:
			return HELP_AVAILABLE;
		case ERROR:
			return HELP_ERROR;
		default:
			return null;
		}
	}
}
