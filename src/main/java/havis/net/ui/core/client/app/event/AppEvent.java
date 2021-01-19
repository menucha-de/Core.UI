package havis.net.ui.core.client.app.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import havis.util.core.app.AppInfo;

public class AppEvent extends GwtEvent<AppEvent.Handler> {

	public static enum Action {
		START, STOP, DELETE, INFO, OPEN, ACTIVATE, REQUESTLICENSE
	}

	private Action action;
	private AppInfo appInfo;

	public interface Handler extends EventHandler {
		void onAppEvent(AppEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addAppEventHandler(AppEvent.Handler handler);
	}

	private static final Type<AppEvent.Handler> TYPE = new Type<>();

	public AppEvent(Action action, AppInfo appInfo) {
		this.action = action;
		this.appInfo = appInfo;
	}

	public Action getAction() {
		return action;
	}

	public AppInfo getAppInfo() {
		return appInfo;
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
		handler.onAppEvent(this);
	}

}
