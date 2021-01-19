package havis.net.ui.core.client.apps;

import com.google.gwt.user.client.ui.HasEnabled;

import havis.net.ui.core.client.base.AppsView;
import havis.net.ui.shared.client.upload.File;
import havis.util.core.app.AppInfo;

public interface LaunchAppsView extends AppsView {
	HasEnabled getInstallButton();

	public interface Presenter extends AppsView.Presenter {
		void onShowInfo(AppInfo appInfo);

		void getInstalledApps();

		void uploadApp(File f);

		void onShowManagement();
	}

}
