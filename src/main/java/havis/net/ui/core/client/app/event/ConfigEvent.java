package havis.net.ui.core.client.app.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import havis.net.ui.shared.client.upload.File;
import havis.util.core.app.AppInfo;

public class ConfigEvent extends GwtEvent<ConfigEvent.Handler> {

	public static enum Action {
		IMPORT, EXPORT, RESET, BACKUP, RESTORE, DELETE
	}

	private Action action;
	private AppInfo appInfo;
	private File file;

	public interface Handler extends EventHandler {
		void onConfigEvent(ConfigEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addConfigEventHandler(ConfigEvent.Handler handler);
	}

	private static final Type<ConfigEvent.Handler> TYPE = new Type<>();

	public ConfigEvent(Action action, AppInfo appInfo) {
		this.action = action;
		this.appInfo = appInfo;
	}

	public ConfigEvent(Action action, AppInfo appInfo, File file) {
		this(action, appInfo);
		this.file = file;
	}

	public Action getAction() {
		return action;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public File getFile() {
		return file;
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
		handler.onConfigEvent(this);
	}
}
