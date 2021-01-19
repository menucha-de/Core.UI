package havis.net.ui.core.client.log.config;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;

import havis.net.ui.shared.client.list.WidgetList;
import havis.util.core.log.LogLevel;

public interface LogConfigView extends IsWidget {
	void setPresenter(Presenter presenter);

	WidgetList getConfigurationList();

	TakesValue<Boolean> getExecuting();

	interface Presenter {

		void onReset();

		void onSelectLogLevel(String target, LogLevel level);

		void onClearLogLevel(String target);

		void onRefresh();

	}
}
