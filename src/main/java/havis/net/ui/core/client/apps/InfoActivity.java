package havis.net.ui.core.client.apps;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.ui.core.client.app.event.LoadedEvent;
import havis.net.ui.core.client.base.BaseActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.place.CorePlace;
import havis.net.ui.core.client.place.DialogPlace;
import havis.net.ui.core.client.place.ManagementPlace;
import havis.util.core.app.AppInfo;

public class InfoActivity extends BaseActivity {

	private ClientFactory clientFactory;
	private AppInfo appInfo;
	private DialogPlace place;
	private static final String INFO = "info.html";

	public InfoActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		final InfoDialog dialog = clientFactory.getInfoDialog();
		dialog.setPresenter(this);
		panel.setWidget(dialog.asWidget());
		place = (DialogPlace) clientFactory.getPlaceController().getWhere();

		if (place.getAppName().equals("base")) {
			dialog.show(INFO);
		} else {
			LoadedEvent.register(eventBus, new LoadedEvent.Handler() {

				@Override
				public void onLoadedEvent(LoadedEvent event) {
					String path = "";
					appInfo = event.getAppInfos().get(place.getAppName());
					if (appInfo != null) {
						path = appInfo.getPath();
						if (path != null && !path.trim().isEmpty() && path.charAt(path.length() - 1) != '/') {
							path += '/';
						}
					}
					dialog.show(path + INFO);
				}
			});
			clientFactory.getUtil().loadApps(false);
		}
	}

	public void onBackButton() {
		switch (place.getSection()) {
		case "main":
			clientFactory.getPlaceController().goTo(new CorePlace(""));
			break;
		case "management":
			clientFactory.getPlaceController().goTo(new ManagementPlace("main"));
			break;
		}
	}

}
