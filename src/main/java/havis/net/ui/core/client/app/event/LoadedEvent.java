package havis.net.ui.core.client.app.event;

import java.util.Map;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import havis.util.core.app.AppInfo;

public class LoadedEvent extends Event<LoadedEvent.Handler> {

	private Map<String, AppInfo> appInfos;

	public interface Handler {
		void onLoadedEvent(LoadedEvent event);
	}

	public static HandlerRegistration register(EventBus eventBus, LoadedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public interface HasHandlers {
		HandlerRegistration addAppEventHandler(LoadedEvent.Handler handler);
	}

	private static final Type<LoadedEvent.Handler> TYPE = new Type<>();

	public LoadedEvent(Map<String, AppInfo> appInfo) {
		this.appInfos = appInfo;
	}

	public Map<String, AppInfo> getAppInfos() {
		return appInfos;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	public static Type<Handler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onLoadedEvent(this);
	}

}
