package havis.net.ui.core.client.mvp;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import havis.net.rest.core.async.AppServiceAsync;
import havis.net.ui.core.client.apps.InfoDialog;
import havis.net.ui.core.client.apps.LaunchApps;
import havis.net.ui.core.client.apps.LaunchAppsView;
import havis.net.ui.core.client.apps.license.LicenseActivateDialog;
import havis.net.ui.core.client.apps.license.LicenseRequestDialog;
import havis.net.ui.core.client.base.Util;
import havis.net.ui.core.client.log.config.LogConfigSection;
import havis.net.ui.core.client.log.config.LogConfigView;
import havis.net.ui.core.client.log.monitor.LogMonitorPanel;
import havis.net.ui.core.client.log.monitor.LogMonitorView;
import havis.net.ui.core.client.management.Management;
import havis.net.ui.core.client.management.ManagementView;

public class ClientFactoryDefault implements ClientFactory {

	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private final AppServiceAsync appService = GWT.create(AppServiceAsync.class);
	private final Util util = new Util(eventBus, appService);
	private final LaunchAppsView launchAppsView = new LaunchApps();
	private final LogMonitorView logMonitorView = new LogMonitorPanel();
	private final ManagementView managementView = new Management();
	private final LogConfigView logConfigView = new LogConfigSection();
	private final LicenseRequestDialog licenseRequestDialog = new LicenseRequestDialog();
	private final LicenseActivateDialog licenseActivateDialog = new LicenseActivateDialog();
	private final InfoDialog infoDialog = new InfoDialog();

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public LaunchAppsView getLaunchAppsView() {
		return launchAppsView;
	}

	@Override
	public LogMonitorView getLogMonitorView() {
		return logMonitorView;
	}

	@Override
	public ManagementView getManagementView() {
		return managementView;
	}

	@Override
	public LogConfigView getLogConfigView() {
		return logConfigView;
	}

	@Override
	public LicenseRequestDialog getLicenseRequestDialog() {
		return licenseRequestDialog;
	}

	@Override
	public LicenseActivateDialog getLicenseActivateDialog() {
		return licenseActivateDialog;
	}

	@Override
	public Util getUtil() {
		return util;
	}

	@Override
	public AppServiceAsync getAppService() {
		return appService;
	}

	@Override
	public InfoDialog getInfoDialog() {
		return infoDialog;
	}
}
