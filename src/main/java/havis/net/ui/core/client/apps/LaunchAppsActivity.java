package havis.net.ui.core.client.apps;

import java.util.Collection;

import havis.net.ui.core.client.apps.LaunchAppsView.Presenter;
import havis.net.ui.core.client.base.AppsActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.place.DialogPlace;
import havis.net.ui.core.client.place.ManagementPlace;
import havis.util.core.app.AppInfo;

public class LaunchAppsActivity extends AppsActivity implements Presenter {
	public LaunchAppsActivity(ClientFactory clientFactory) {
		super(clientFactory);
		setView(clientFactory.getLaunchAppsView());
		setPage("main");
	}

	protected void initializeUI() {
		super.initializeUI();
		((LaunchAppsView) getView()).getInstallButton().setEnabled(getPermissions().install);
	}

	@Override
	protected Collection<AppInfo> getApps() {
		return getClientFactory().getUtil().getAppsForSection(null);
	}

	@Override
	public void onShowInfo(AppInfo appInfo) {
		getClientFactory().getPlaceController().goTo(new DialogPlace("info", appInfo.getName(), getPage()));
	}

	@Override
	public void onShowManagement() {
		getClientFactory().getPlaceController().goTo(new ManagementPlace("main"));
	}

}
