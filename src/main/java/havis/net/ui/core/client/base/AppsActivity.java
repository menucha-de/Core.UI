package havis.net.ui.core.client.base;

import havis.net.rest.core.async.AppServiceAsync;
import havis.net.ui.core.client.app.AppButton;
import havis.net.ui.core.client.app.event.AppEvent;
import havis.net.ui.core.client.app.event.ConfigEvent;
import havis.net.ui.core.client.app.event.LoadedEvent;
import havis.net.ui.core.client.base.AppsView.Presenter;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.place.DialogPlace;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.upload.File;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.LoadingSpinner;
import havis.net.ui.shared.data.HttpMethod;
import havis.util.core.app.AppInfo;
import havis.util.core.app.AppState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.xml.XMLHttpRequest;

public abstract class AppsActivity extends BaseActivity {
	private class Callback<T> implements MethodCallback<T> {

		private String errorMessage;

		public Callback(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void onFailure(Method method, Throwable exception) {
			updateViewOnError(errorMessage);
		}

		@Override
		public void onSuccess(Method method, T response) {
		}
	}

	private static final int MAX_RESPONSE = 5;
	private static final int MAX_RETRIES = 60;
	private static final int OVERLAY_HEIGHT = 500;
	private AppsView view;
	private LoadingSpinner spinner = new LoadingSpinner();
	private CustomMessageWidget message = new CustomMessageWidget();
	private Util util;
	private AppServiceAsync service;
	private int retries = 0;
	private Permissions permissions;
	private ClientFactory clientFactory;
	private EventBus eventBus;
	private int responseCount;
	private String page;

	private AppEvent.Handler appHandler = new AppEvent.Handler() {

		@Override
		public void onAppEvent(AppEvent event) {
			switch (event.getAction()) {
			case START:
				toggleAppState(event.getAppInfo(), AppState.START);
				break;
			case STOP:
				toggleAppState(event.getAppInfo(), AppState.STOP);
				break;
			case DELETE:
				deleteApp(event.getAppInfo());
				break;
			case INFO:
				clientFactory.getPlaceController().goTo(new DialogPlace("info", event.getAppInfo().getName(), page));
				break;
			case OPEN:
				onOpenURL(event.getAppInfo().getPath() + Window.Location.getQueryString());
				break;
			case ACTIVATE:
				clientFactory.getPlaceController()
						.goTo(new DialogPlace("activate", event.getAppInfo().getName(), page));
				break;
			case REQUESTLICENSE:
				clientFactory.getPlaceController().goTo(new DialogPlace("request", event.getAppInfo().getName(), page));
				break;
			default:
				break;
			}
		}
	};

	private ConfigEvent.Handler configHandler = new ConfigEvent.Handler() {

		@Override
		public void onConfigEvent(ConfigEvent event) {
			switch (event.getAction()) {
			case EXPORT:
				downloadConfig(event.getAppInfo());
				break;
			case IMPORT:
				uploadConfig(event.getAppInfo(), event.getFile());
				break;
			case RESET:
				service.resetConfig(event.getAppInfo().getName(), new Callback<Void>("Failed to reset configuration!"));
				break;
			case BACKUP:
				service.storeBackup(event.getAppInfo().getName(), "default",
						new Callback<Void>("Failed to backup configuration!"));
				break;
			case RESTORE:
				service.restoreBackup(event.getAppInfo().getName(), "default",
						new Callback<Void>("Failed to restore configuration!"));
				break;
			case DELETE:
				service.dropBackup(event.getAppInfo().getName(), "default",
						new Callback<Void>("Failed to drop configuration backup!"));
				break;
			default:
				break;
			}
		}
	};

	private PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback() {

		@Override
		public void setPosition(int offsetWidth, int offsetHeight) {
			int top = (OVERLAY_HEIGHT - offsetHeight) / 2;
			int left = (Window.getClientWidth() - offsetWidth) / 2;
			spinner.setPopupPosition(left, top);
		}
	};

	public AppsActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private void callback(Permissions.Callback callback, Permissions permissions) {
		++responseCount;
		if (responseCount == MAX_RESPONSE) {
			callback.getPermissions(permissions);
		}
	}

	private void checkAvailability() {
		++retries;
		service.get(new MethodCallback<Collection<AppInfo>>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				new Timer() {

					@Override
					public void run() {
						if (retries < MAX_RETRIES) {
							checkAvailability();
						}
					}
				}.schedule(500);
			}

			@Override
			public void onSuccess(Method method, Collection<AppInfo> response) {
				retries = 0;
				checkPermissions(new Permissions.Callback() {

					@Override
					public void getPermissions(Permissions permissions) {
						AppsActivity.this.permissions = permissions;
						initializeUI();
					}
				});
			}
		});
	}

	private void checkPermissions(final Permissions.Callback callback) {
		responseCount = 0;
		final Permissions permissions = new Permissions();
		service.optionsInstall(new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				permissions.failed = true;
				callback(callback, permissions);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				permissions.install = HttpMethod.POST.isAllowed(method.getResponse());
				permissions.delete = HttpMethod.DELETE.isAllowed(method.getResponse());
				callback(callback, permissions);
			}
		});
		service.optionsState(new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				permissions.failed = true;
				callback(callback, permissions);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				permissions.state = HttpMethod.PUT.isAllowed(method.getResponse());
				callback(callback, permissions);
			}
		});

		service.optionsConfig(new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				permissions.failed = true;
				callback(callback, permissions);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				permissions.configDownload = HttpMethod.GET.isAllowed(method.getResponse());
				permissions.configUpload = HttpMethod.POST.isAllowed(method.getResponse());
				permissions.configReset = HttpMethod.DELETE.isAllowed(method.getResponse());
				callback(callback, permissions);
			}
		});

		service.optionsBackup(new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				permissions.failed = true;
				callback(callback, permissions);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				permissions.configBackup = HttpMethod.PUT.isAllowed(method.getResponse());
				permissions.configRestore = HttpMethod.GET.isAllowed(method.getResponse());
				permissions.configDelete = HttpMethod.DELETE.isAllowed(method.getResponse());
				callback(callback, permissions);
			}
		});

		service.optionsLicenseRequest(new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				permissions.failed = true;
				callback(callback, permissions);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				permissions.licenseState = HttpMethod.GET.isAllowed(method.getResponse());
				permissions.licenseRequest = HttpMethod.GET.isAllowed(method.getResponse());
				permissions.licenseResponse = HttpMethod.GET.isAllowed(method.getResponse());
				callback(callback, permissions);
			}
		});
	}

	private void deleteApp(final AppInfo appInfo) {
		service.remove(appInfo.getName(), new Callback<Void>("Failed to delete app!") {

			@Override
			public void onSuccess(Method method, Void response) {
				Map<String, AppButton> appsMap = view.getAppButtons();
				AppButton ab = appsMap.get(appInfo.getName());
				if (ab != null) {
					ab.hideMenu();
					view.removeAppButton(ab);
					appsMap.remove(appInfo.getName());
					clientFactory.getUtil().getApps().remove(appInfo.getName());
				}
			}
		});
	}

	public void getInstalledApps() {
		spinner.setPopupPositionAndShow(posCallback);
		LoadedEvent.register(eventBus, new LoadedEvent.Handler() {

			@Override
			public void onLoadedEvent(LoadedEvent event) {
				Map<String, AppButton> appsMap = view.getAppButtons();
				ArrayList<String> previousAppNames = new ArrayList<>(appsMap.keySet());
				Collection<AppInfo> response = getApps();
				final int size = response.size();
				if (size == 0) {
					for (AppButton appButton : appsMap.values()) {
						view.removeAppButton(appButton);
					}
					appsMap.clear();
					spinner.hide();
				} else {
					for (final AppInfo app : response) {
						AppButton ab = appsMap.get(app.getName());
						if (ab == null) {
							ab = new AppButton(app, permissions);
							AppState state = app.getState();
							if (state == null || state == AppState.STARTING || state == AppState.STOPPING) {
								updateAppState(app.getName());
							} else {
								ab.disableMenuEntries(false);
							}
							ab.addAppEventHandler(appHandler);
							ab.addConfigEventHandler(configHandler);
							view.addAppButton(ab);
							appsMap.put(app.getName(), ab);
						} else {
							ab.update(app);
							previousAppNames.remove(app.getName());
						}
					}

					// remove deleted apps
					for (String name : previousAppNames) {
						view.removeAppButton(appsMap.get(name));
						appsMap.remove(name);
					}

					spinner.hide();
				}
			}
		});
		util.loadApps(true);
	}

	protected abstract Collection<AppInfo> getApps();

	protected ClientFactory getClientFactory() {
		return clientFactory;
	}

	protected String getPage() {
		return page;
	}

	protected Permissions getPermissions() {
		return permissions;
	}

	protected AppsView getView() {
		return view;
	}

	protected void initializeUI() {
		getInstalledApps();
	}

	protected void setPage(String page) {
		this.page = page;
	}

	protected void setView(AppsView view) {
		this.view = view;
	}

	private void onOpenURL(final String url) {
		try {
			new RequestBuilder(RequestBuilder.HEAD, url).sendRequest(null, new RequestCallback() {

				@Override
				public void onError(Request request, Throwable exception) {
					updateViewOnError("The service is not available");
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 200) {
						Window.Location.assign(url);
					} else {
						updateViewOnError("The service is not available");
					}
				}
			});
		} catch (RequestException e) {
			updateViewOnError("The service is not available");
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.eventBus = eventBus;
		service = clientFactory.getAppService();
		util = clientFactory.getUtil();
		view.setPresenter((Presenter) this);
		panel.setWidget(view.asWidget());
		checkAvailability();
	}

	private void toggleAppState(final AppInfo appInfo, final AppState state) {
		service.setState(appInfo.getName(), state, new Callback<Void>("Failed set state: " + state.name() + "!") {

			@Override
			public void onSuccess(Method method, Void response) {
				updateAppState(appInfo.getName());
			}
		});
	}

	private void updateAppState(final String name) {
		service.getState(name, new Callback<AppState>("Failed to get app state!") {

			@Override
			public void onSuccess(Method method, AppState response) {
				AppState state = response;
				if (state == null || state == AppState.STARTING || state == AppState.STOPPING) {
					new Timer() {

						@Override
						public void run() {
							updateAppState(name);
						}
					}.schedule(1000);
				} else {
					AppButton ab = view.getAppButtons().get(name);
					ab.setAppState(state);
					ab.disableMenuEntries(false);
				}
			}
		});
	}

	protected void updateViewOnError(String errorMessage) {
		CustomMessageWidget.show(errorMessage, MessageType.ERROR);
		getInstalledApps();
	}

	public void uploadApp(File file) {
		uploadFile(file, GWT.getHostPageBaseURL() + "rest/apps/" + file.getName(), "Failed to install app!", ".app");
	}

	private void downloadConfig(AppInfo appInfo) {
		String url = getConfigUrl(appInfo);
		Window.open(url, "_blank", "");
	}

	public void uploadConfig(AppInfo appInfo, File file) {
		String url = getConfigUrl(appInfo);
		uploadFile(file, url, "Failed to import Configuration!", ".config");
	}

	private String getConfigUrl(AppInfo appInfo) {
		String url = GWT.getHostPageBaseURL();
		if (appInfo.getSection().equals("device")) {
			String[] config = appInfo.getConfig();
			if (config != null && config.length > 0) {
				url += "rest/" + config[0];
			}
		} else {
			url += "rest/apps/" + appInfo.getName() + "/config";
		}
		return url;
	}

	private void uploadFile(File file, String url, final String errorMessage, String fileType) {
		if (file.getName().endsWith(fileType)) {

			spinner.show();
			final XMLHttpRequest xhr = Browser.getWindow().newXMLHttpRequest();
			xhr.open("POST", url);
			xhr.setRequestHeader("Content-Type", "application/octet-stream");
			xhr.setRequestHeader("Authorization", "Basic " + Browser.getWindow().btoa("admin:"));

			xhr.addEventListener("load", new EventListener() {

				@Override
				public void handleEvent(Event evt) {
					int status = xhr.getStatus();
					if (status == 204) {
						getInstalledApps();
					}
					if ((status == 500) || (status == 400)) {
						spinner.hide();
						message.showMessage(xhr.getResponseText(), MessageType.ERROR);
					}
				}
			}, false);

			xhr.addEventListener("error", new EventListener() {

				@Override
				public void handleEvent(Event evt) {
					spinner.hide();
					message.showMessage(errorMessage, MessageType.ERROR);
				}
			}, false);

			xhr.addEventListener("abort", new EventListener() {

				@Override
				public void handleEvent(Event evt) {
					spinner.hide();
					message.showMessage("Aborted by user!", MessageType.INFO);
				}
			}, false);

			xhr.send(file);
		} else {
			spinner.hide();
			message.showMessage("File must be of type *" + fileType, MessageType.ERROR);
		}
	}

}
