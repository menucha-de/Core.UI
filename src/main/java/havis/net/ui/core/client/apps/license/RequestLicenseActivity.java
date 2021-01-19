package havis.net.ui.core.client.apps.license;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.ui.core.client.app.event.LoadedEvent;
import havis.net.ui.core.client.base.BaseActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.place.DialogPlace;
import havis.util.core.app.AppInfo;
import havis.util.core.license.License;

public class RequestLicenseActivity extends BaseActivity {

	private ClientFactory clientFactory;
	private LicenseRequestDialog dialog;
	private AppInfo appInfo;
	
	public RequestLicenseActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		dialog = clientFactory.getLicenseRequestDialog();
		dialog.setPresenter(this);
		panel.setWidget(dialog.asWidget());
		final DialogPlace place = (DialogPlace) clientFactory.getPlaceController().getWhere();
		LoadedEvent.register(eventBus, new LoadedEvent.Handler() {
			

			@Override
			public void onLoadedEvent(LoadedEvent event) {
				appInfo = event.getAppInfos().get(place.getAppName());
				clientFactory.getAppService().getLicenseRequest(appInfo.getName(), new MethodCallback<License>() {

					@Override
					public void onSuccess(Method method, License response) {
						dialog.show(appInfo, response);
					}
					
					@Override
					public void onFailure(Method method, Throwable exception) {
						//"Failed to get license information!"
					}
				});
			}
		});
		clientFactory.getUtil().loadApps(false);
	}

	public void onBackButton() {
		clientFactory.getPlaceController().goTo(new DialogPlace("activate", appInfo.getName(), "main"));
	}
}
