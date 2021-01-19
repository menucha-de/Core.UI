package havis.net.ui.core.client.log.monitor;

import havis.util.core.log.LogEntry;
import havis.util.core.log.LogLevel;
import havis.util.core.log.LogTarget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.i18n.client.DateTimeFormat;

public class LogMonitor {
	private static final LogLevel DEFAULT_LEVEL = LogLevel.ALL;
	private static final LogTarget DEFAULT_TARGET = new LogTarget("ALL", "ALL");
	private static final int DEFAULT_LIMIT = 30;
	private static final int DEFAULT_PART = 9;

	private static final DateTimeFormat DATE = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);
	private static final DateTimeFormat TIME = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_MEDIUM);

	private static final String[] FIELD_LABELS = new String[] { "Level", "Date", "Time", "Service", "Message" };

	private List<LogLevel> levels = new ArrayList<>();
	private List<LogTarget> targets = new ArrayList<LogTarget>();
	private int limit = DEFAULT_LIMIT;
	private boolean endReached = false;

	private LogLevel level = DEFAULT_LEVEL;
	private LogTarget target = DEFAULT_TARGET;

	private int offset;
	private int cursor;

	private ArrayList<String[]> logEntryList = new ArrayList<String[]>();

	public LogMonitor() {
		reset();
	}

	public List<LogLevel> getLevels() {
		return levels;
	}

	public void setLevels(List<LogLevel> levels) {
		this.levels.clear();
		for (LogLevel level : levels) {
			switch (level) {
			case OFF:
				break;
			default:
				this.levels.add(level);
			}
		}
	}

	public void setTargets(List<LogTarget> targets) {
		this.targets.clear();
		this.targets.add(DEFAULT_TARGET);
		for (LogTarget logTarget : targets) {
			this.targets.add(logTarget);
		}
	}

	public List<LogTarget> getTargets() {
		return targets;
	}

	public void setLogEntries(List<LogEntry> logEntries) {
		for (ListIterator<LogEntry> iterator = logEntries.listIterator(logEntries.size()); iterator.hasPrevious();) {
			LogEntry logEntry = iterator.previous();
			logEntryList.add(new String[] { logEntry.getLevel().name(), DATE.format(new Date(logEntry.getTime())),
					TIME.format(new Date(logEntry.getTime())), logEntry.getTargetName(), logEntry.getMessage() });
		}

		if (offset == 0) {
			endReached = true;
			logEntryList.add(new String[] { "", "", "", "", "" });
		}

	}

	public List<String[]> getLogEntries() {
		return logEntryList;
	}

	public String[] getPreviousEntry() {
		if (cursor < logEntryList.size()) {
			return logEntryList.get(cursor++);
		}
		return null;
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		if (!level.equals(this.level)) {
			clearLogEntries();
		}
		this.level = level;
	}

	public LogTarget getTarget() {
		return target;
	}

	public void setTarget(LogTarget target) {
		if (!target.equals(this.target)) {
			clearLogEntries();
		}
		this.target = target;
	}

	public static int getDefaultPart() {
		return DEFAULT_PART;
	}

	public static String[] getFieldLabels() {
		return FIELD_LABELS;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int currentOffset) {
		if (currentOffset < 0) {
			this.limit = currentOffset + DEFAULT_LIMIT;
			this.offset = 0;
		} else {
			this.limit = DEFAULT_LIMIT;
			this.offset = currentOffset;
		}
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	private void clearLogEntries() {
		logEntryList.clear();
	}

	public int getLimit() {
		return limit;
	}

	public boolean isEndReached() {
		return endReached;
	}

	public int getInitialCount() {
		return logEntryList.size() < DEFAULT_PART ? logEntryList.size() : DEFAULT_PART;
	}

	public void reset() {
		this.limit = DEFAULT_LIMIT;
		this.logEntryList.clear();
		this.cursor = 0;
		// this.offset = -1;
		this.endReached = false;
	}
}
