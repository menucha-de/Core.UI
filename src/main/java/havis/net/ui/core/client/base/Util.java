package havis.net.ui.core.client.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.rest.core.async.AppServiceAsync;
import havis.net.ui.core.client.app.event.LoadedEvent;
import havis.util.core.app.AppInfo;

public class Util {

	private EventBus eventBus;
	private AppServiceAsync appService;
	private Map<String, AppInfo> appInfoMap;
	private static final int OVERLAY_HEIGHT = 500;

	public Util(EventBus eventBus, AppServiceAsync appService) {
		this.eventBus = eventBus;
		this.appService = appService;
	}

	public void loadApps(boolean force) {
		if (appInfoMap == null || force) {
			appInfoMap = new LinkedHashMap<String, AppInfo>();
			appService.get(new MethodCallback<Collection<AppInfo>>() {

				@Override
				public void onSuccess(Method method, Collection<AppInfo> response) {
					for (AppInfo ai : response) {
						appInfoMap.put(ai.getName(), ai);
					}
					eventBus.fireEvent(new LoadedEvent(appInfoMap));
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					// TODO Auto-generated method stub

				}
			});
		} else {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					// fire event
					eventBus.fireEvent(new LoadedEvent(appInfoMap));
				}
			});

		}
	}

	public List<AppInfo> getAppsForSection(String section) {
		List<AppInfo> result = new ArrayList<AppInfo>();
		for (AppInfo ai : appInfoMap.values()) {
			if (section != null) {
				if (ai.getSection().equals(section))
					result.add(ai);
			} else {
				if (ai.getSection() == null)
					result.add(ai);
			}
		}
		return result;
	}

	public Map<String, AppInfo> getApps() {
		return appInfoMap;
	}

	public PopupPanel.PositionCallback getPosition(final PopupPanel panel) {
		return new PopupPanel.PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int top = (OVERLAY_HEIGHT - offsetHeight) / 2;
				int left = (Window.getClientWidth() - offsetWidth) / 2;
				panel.setPopupPosition(left, top);
			}
		};
	}
}
