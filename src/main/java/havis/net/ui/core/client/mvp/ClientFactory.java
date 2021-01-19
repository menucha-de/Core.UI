package havis.net.ui.core.client.mvp;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.rest.core.async.AppServiceAsync;
import havis.net.ui.core.client.apps.InfoDialog;
import havis.net.ui.core.client.apps.LaunchAppsView;
import havis.net.ui.core.client.apps.license.LicenseActivateDialog;
import havis.net.ui.core.client.apps.license.LicenseRequestDialog;
import havis.net.ui.core.client.base.Util;
import havis.net.ui.core.client.log.config.LogConfigView;
import havis.net.ui.core.client.log.monitor.LogMonitorView;
import havis.net.ui.core.client.management.ManagementView;

public interface ClientFactory {
	EventBus getEventBus();
	PlaceController getPlaceController();
	LaunchAppsView getLaunchAppsView();
	LogMonitorView getLogMonitorView();
	ManagementView getManagementView();
	LogConfigView getLogConfigView();
	LicenseRequestDialog getLicenseRequestDialog();
	LicenseActivateDialog getLicenseActivateDialog();
	Util getUtil();
	AppServiceAsync getAppService();
	InfoDialog getInfoDialog();
}
