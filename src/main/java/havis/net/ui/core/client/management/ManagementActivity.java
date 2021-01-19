package havis.net.ui.core.client.management;

import java.util.Collection;

import havis.net.ui.core.client.base.AppsActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.place.ManagementPlace;
import havis.util.core.app.AppInfo;

public class ManagementActivity extends AppsActivity implements ManagementView.Presenter {

	public ManagementActivity(ClientFactory clientFactory) {
		super(clientFactory);
		setView(clientFactory.getManagementView());
		setPage("management");
	}

	@Override
	public void onConfigureLog() {
		getClientFactory().getPlaceController().goTo(new ManagementPlace("log"));
	}

	@Override
	protected Collection<AppInfo> getApps() {
		return getClientFactory().getUtil().getApps().values();
	}
}
