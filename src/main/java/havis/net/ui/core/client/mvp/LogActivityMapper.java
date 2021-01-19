package havis.net.ui.core.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import havis.net.ui.core.client.log.monitor.LogMonitorActivity;
import havis.net.ui.core.client.place.CorePlace;
import havis.net.ui.core.client.place.DialogPlace;

public class LogActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;

	public LogActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		if ((place instanceof CorePlace)
				|| (place instanceof DialogPlace && ((DialogPlace) place).getSection().equals("main"))) {
			return new LogMonitorActivity(clientFactory);
		}
		return null;
	}
}
