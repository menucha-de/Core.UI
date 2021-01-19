package havis.net.ui.core.client.management;

import havis.net.ui.core.client.base.AppsView;

public interface ManagementView extends AppsView {

	interface Presenter extends AppsView.Presenter {
		void onConfigureLog();
	}
}
