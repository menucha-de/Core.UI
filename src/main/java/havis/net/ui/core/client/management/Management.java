package havis.net.ui.core.client.management;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.core.client.app.AppButton;
import havis.net.ui.core.client.base.AppsView;

public class Management extends Composite implements ManagementView {

	@UiField
	FlowPanel deviceAppsPanel;

	@UiField
	FlowPanel driverAppsPanel;

	private Presenter presenter;
	private HashMap<String, AppButton> appsButtons = new HashMap<>();

	private static ManagementUiBinder uiBinder = GWT.create(ManagementUiBinder.class);

	interface ManagementUiBinder extends UiBinder<Widget, Management> {
	}

	public Management() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(AppsView.Presenter presenter) {
		this.presenter = (Presenter) presenter;
	}

	@UiHandler("logButton")
	void onLogButtonClick(ClickEvent event) {
		presenter.onConfigureLog();
	}

	private FlowPanel getPanel(Area area) {
		switch (area) {
		case DEVICE:
			return deviceAppsPanel;
		case DRIVER:
			return driverAppsPanel;
		}
		return null;
	}

	@Override
	public Map<String, AppButton> getAppButtons() {
		return appsButtons;
	}

	@Override
	public void addAppButton(AppButton appButton) {
		String section = appButton.getAppInfo().getSection();
		if (section != null) {
			Area area = Area.valueOf(appButton.getAppInfo().getSection().toUpperCase());
			if (area == Area.DEVICE) {
				int index = getPanel(area).getWidgetCount() - 1;
				getPanel(area).insert(appButton, index);
			} else {
				getPanel(area).add(appButton);
			}
		}
	}

	@Override
	public void removeAppButton(AppButton appButton) {
		appButton.removeFromParent();
	}

}
