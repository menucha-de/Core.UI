package havis.net.ui.core.client.apps;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.core.client.app.AppButton;
import havis.net.ui.core.client.app.CoreButton;
import havis.net.ui.core.client.app.event.AppEvent;
import havis.net.ui.core.client.app.event.AppEvent.Action;
import havis.net.ui.core.client.base.AppsView;
import havis.net.ui.shared.client.event.MessageEvent;
import havis.net.ui.shared.client.upload.File;
import havis.net.ui.shared.client.upload.FileList;
import havis.net.ui.shared.client.upload.MultipleFileUpload;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

public class LaunchApps extends Composite implements LaunchAppsView, MessageEvent.HasHandlers {

	@UiField
	FlowPanel container;

	@UiField
	FlowPanel buttonPanel;

	@UiField
	CoreButton install;

	@UiField
	MultipleFileUpload upload;

	private ResourceBundle sharedRes = ResourceBundle.INSTANCE;
	private havis.net.ui.core.resourcebundle.ResourceBundle res = havis.net.ui.core.resourcebundle.ResourceBundle.INSTANCE;
	private LaunchAppsView.Presenter presenter;
	private HashMap<String, AppButton> appsButtons = new HashMap<>();

	private static LaunchAppsUiBinder uiBinder = GWT.create(LaunchAppsUiBinder.class);

	interface LaunchAppsUiBinder extends UiBinder<Widget, LaunchApps> {
	}

	public LaunchApps() {
		initWidget(uiBinder.createAndBindUi(this));
		ensureInjection();
	}

	@Override
	public void setPresenter(AppsView.Presenter presenter) {
		this.presenter = (Presenter) presenter;
	}

	private void ensureInjection() {
		sharedRes.css().ensureInjected();
		res.css().ensureInjected();
	}

	@UiHandler("management")
	void onManagementButtonClick(ClickEvent event) {
		presenter.onShowManagement();
	}

	@UiHandler("upload")
	void onChooseFile(ChangeEvent event) {
		FileList fl = upload.getFileList();
		File f = fl.html5Item(0);

		presenter.uploadApp(f);
		upload.reset();
	}

	@UiHandler("install")
	void onInstallButtonClick(ClickEvent event) {
		if (install.isEnabled()) {
			upload.click();
		}
	}

	@UiHandler("management")
	void onAppInfoEvent(AppEvent event) {
		if (event.getAction() == Action.INFO) {
			presenter.onShowInfo(event.getAppInfo());
		}
	}

	@Override
	public HandlerRegistration addMessageEventHandler(MessageEvent.Handler handler) {
		return addHandler(handler, MessageEvent.getType());
	}

	@Override
	public Map<String, AppButton> getAppButtons() {
		return appsButtons;
	}
	
	@Override
	public void addAppButton(AppButton appButton) {
		int index = buttonPanel.getWidgetCount() - 1;
		buttonPanel.insert(appButton, index);
	}

	@Override
	public void removeAppButton(AppButton appButton) {
		buttonPanel.remove(appButton);
	}

	@Override
	public HasEnabled getInstallButton() {
		return install;
	}

}