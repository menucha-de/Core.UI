package havis.net.ui.core.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import havis.net.ui.core.client.apps.LaunchAppsActivity;
import havis.net.ui.core.client.log.config.LogConfigActivity;
import havis.net.ui.core.client.management.ManagementActivity;
import havis.net.ui.core.client.place.CorePlace;
import havis.net.ui.core.client.place.DialogPlace;
import havis.net.ui.core.client.place.ManagementPlace;

public class MainActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;
	private Activity mainActivity;
	
	public MainActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof CorePlace) {
			mainActivity = new LaunchAppsActivity(clientFactory);
			return mainActivity;
		} else if (place instanceof DialogPlace) {
				if (mainActivity == null) {
					if (((DialogPlace) place).getSection().equals("main")) {
						mainActivity = new LaunchAppsActivity(clientFactory);
					} else if (((DialogPlace) place).getSection().equals("management")) {
						mainActivity = new ManagementActivity(clientFactory);
					}
				}
				return mainActivity;
		} else if (place instanceof ManagementPlace) {
			switch (((ManagementPlace) place).getPage()) {
			case "main":
				mainActivity = new ManagementActivity(clientFactory);
				return mainActivity;
			case "log":
				mainActivity = new LogConfigActivity(clientFactory);
				return mainActivity;
			}
		}
		return null;
	}
}
