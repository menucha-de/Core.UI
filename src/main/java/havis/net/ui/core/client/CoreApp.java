package havis.net.ui.core.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.ui.core.client.mvp.AppPlaceHistoryMapper;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.client.mvp.DialogActivityMapper;
import havis.net.ui.core.client.mvp.LogActivityMapper;
import havis.net.ui.core.client.mvp.MainActivityMapper;
import havis.net.ui.core.client.place.CorePlace;

public class CoreApp extends Composite implements EntryPoint {

	@UiField
	SimplePanel dialog;
	
	@UiField
	SimplePanel main;
	
	@UiField
	SimplePanel log;

	private Place defaultPlace = new CorePlace("");
	private static CoreAppUiBinder uiBinder = GWT.create(CoreAppUiBinder.class);

	interface CoreAppUiBinder extends UiBinder<Widget, CoreApp> {
	}

	public CoreApp() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void onModuleLoad() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();

		ActivityMapper mainActivityMapper = new MainActivityMapper(clientFactory);
		ActivityManager mainActivityManager = new ActivityManager(mainActivityMapper, eventBus);
		mainActivityManager.setDisplay(main);

		ActivityMapper logActivityMapper = new LogActivityMapper(clientFactory);
		ActivityManager logActivityManager = new ActivityManager(logActivityMapper, eventBus);
		logActivityManager.setDisplay(log);

		ActivityMapper dialogActivityMapper = new DialogActivityMapper(clientFactory);
		ActivityManager dialogActivityManager = new ActivityManager(dialogActivityMapper, eventBus);
		dialogActivityManager.setDisplay(dialog);

		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);

		RootLayoutPanel.get().add(this);
		historyHandler.handleCurrentHistory();
	}

}
