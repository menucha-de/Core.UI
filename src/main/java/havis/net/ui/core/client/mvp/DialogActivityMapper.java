package havis.net.ui.core.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import havis.net.ui.core.client.apps.InfoActivity;
import havis.net.ui.core.client.apps.license.ActivateLicenseActivity;
import havis.net.ui.core.client.apps.license.RequestLicenseActivity;
import havis.net.ui.core.client.place.DialogPlace;

public class DialogActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;

	public DialogActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof DialogPlace) {
			switch (((DialogPlace) place).getType()) {
			case "request":
				return new RequestLicenseActivity(clientFactory);
			case "activate":
				return new ActivateLicenseActivity(clientFactory);
			case "info":
				return new InfoActivity(clientFactory);
			}
		}
		return null;
	}
}
