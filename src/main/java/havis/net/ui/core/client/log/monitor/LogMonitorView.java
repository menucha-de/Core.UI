package havis.net.ui.core.client.log.monitor;

import com.google.gwt.user.client.ui.HasConstrainedValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

import havis.net.ui.shared.client.list.WidgetList;
import havis.util.core.log.LogLevel;
import havis.util.core.log.LogTarget;

public interface LogMonitorView extends IsWidget {
	void setPresenter(Presenter presenter);

	HasValue<Boolean> isObserving();

	HasValue<Boolean> isExpanded();

	HasConstrainedValue<LogLevel> getLogLevels();

	HasConstrainedValue<LogTarget> getLogTargets();

	HasVisibility getLogControls();

	HasVisibility getRefreshButton();

	HasVisibility getExportButton();

	WidgetList getLogList();

	public interface Presenter {

		void onExpandLog();

		void onObserve();

		void onSelectLevel();

		void onSelectTarget();

		void onClearLog();

		void onExportLog();

		void onRefreshLog();

		void onScroll();

	}
}
