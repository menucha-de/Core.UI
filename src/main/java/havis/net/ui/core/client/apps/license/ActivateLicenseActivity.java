package havis.net.ui.core.client.apps.license;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.xml.XMLHttpRequest;
import havis.net.ui.core.client.app.event.LoadedEvent;
import havis.net.ui.core.client.base.BaseActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.place.CorePlace;
import havis.net.ui.core.client.place.DialogPlace;
import havis.net.ui.core.client.place.ManagementPlace;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.LoadingSpinner;
import havis.util.core.app.AppInfo;

public class ActivateLicenseActivity extends BaseActivity {

	private ClientFactory clientFactory;
	private LicenseActivateDialog dialog;
	private AppInfo appInfo;
	private boolean isDeviceApp;

	public ActivateLicenseActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		dialog = clientFactory.getLicenseActivateDialog();
		dialog.setPresenter(this);
		panel.setWidget(dialog.asWidget());
		final DialogPlace place = (DialogPlace) clientFactory.getPlaceController().getWhere();

		LoadedEvent.register(eventBus, new LoadedEvent.Handler() {

			@Override
			public void onLoadedEvent(LoadedEvent event) {
				appInfo = event.getAppInfos().get(place.getAppName());
				isDeviceApp = appInfo.getSection().equals("device");
				dialog.show(appInfo);
			}
		});
		clientFactory.getUtil().loadApps(false);
	}

	public void onChange() {
		if (isDeviceApp) {
			String[] config = appInfo.getConfig();
			if (config.length > 1) {
				uploadLicense(dialog.getLicenseString(), "text/plain", "rest/" + config[1] + "?product=" + appInfo.getReset(),
						"Reset key not accepted!");
			}
			clientFactory.getPlaceController().goTo(new ManagementPlace("main"));
		} else {
			uploadLicense(dialog.getLicenseString(), "application/octet-stream", "rest/apps/" + appInfo.getName() + "/license",
					"License not accepted!");
			clientFactory.getPlaceController().goTo(new CorePlace(""));
		}
	}

	public void onClose() {
		Place nextPlace = isDeviceApp ? new ManagementPlace("main") : new CorePlace("");
		clientFactory.getPlaceController().goTo(nextPlace);
	}

	public void onRequest() {
		clientFactory.getPlaceController().goTo(new DialogPlace("request", appInfo.getName(), "main"));
	}

	private void uploadLicense(String license, String type, String url, final String errorMessage) {
		final LoadingSpinner spinner = new LoadingSpinner();
		final CustomMessageWidget message = new CustomMessageWidget();
		spinner.setPopupPositionAndShow(clientFactory.getUtil().getPosition(spinner));
		final XMLHttpRequest xhr = Browser.getWindow().newXMLHttpRequest();
		xhr.open("PUT", url);
		xhr.setRequestHeader("Content-Type", type);
		xhr.setRequestHeader("Authorization", "Basic " + Browser.getWindow().btoa("admin:"));

		xhr.addEventListener("load", new EventListener() {

			@Override
			public void handleEvent(Event evt) {
				int status = xhr.getStatus();
				if (status == 204) {
					spinner.hide();
				}
				if (status == 500) {
					spinner.hide();
					message.showMessage(errorMessage, MessageType.ERROR);
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

		xhr.send(license);
	}

}
