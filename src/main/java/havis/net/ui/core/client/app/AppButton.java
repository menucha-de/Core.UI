package havis.net.ui.core.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.core.client.app.event.AppEvent;
import havis.net.ui.core.client.app.event.ConfigEvent;
import havis.net.ui.core.client.app.event.ConfigEvent.Handler;
import havis.net.ui.core.client.base.Permissions;
import havis.net.ui.core.resourcebundle.ResourceBundle;
import havis.util.core.app.AppInfo;
import havis.util.core.app.AppState;
import havis.util.core.license.LicenseState;

public class AppButton extends Composite implements AppEvent.HasHandlers, ConfigEvent.HasHandlers {

	private static final String IMAGE_FILE = "/app.png";

	@UiField
	ImageElement icon;

	@UiField
	Label label;

	@UiField
	HTML inner;

	@UiField
	FocusPanel outer;

	@UiField
	Label contextButton;

	@UiField
	Label licenseWarningButton;

	private ResourceBundle res = ResourceBundle.INSTANCE;
	private ContextPopup contextPopup;
	private Timer longPressTimer;

	private AppInfo appInfo;
	private AppState appState;
	private boolean hasUi;

	private static AppButtonUiBinder uiBinder = GWT.create(AppButtonUiBinder.class);

	@UiTemplate("Button.ui.xml")
	interface AppButtonUiBinder extends UiBinder<Widget, AppButton> {
	}

	public AppButton(AppInfo appInfo, Permissions permissions) {
		this();
		this.appInfo = appInfo;
		hasUi = !appInfo.getSection().equals("driver");
		icon.setSrc(appInfo.getPath() + IMAGE_FILE);
		setLabel(appInfo.getLabel());
		contextPopup = new ContextPopup(this, permissions);
		update(appInfo);
		longPressTimer = new Timer() {
			@Override
			public void run() {
				showMenu();
			}
		};

		outer.sinkEvents(Event.ONCONTEXTMENU);
		outer.addHandler(new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
				showMenu();
			}
		}, ContextMenuEvent.getType());
		setAppState(appInfo.getState());
		disableMenuEntries(true);
	}

	public AppButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setLabel(String label) {
		this.label.setText(label);
		this.label.setTitle(label);
	}

	public void setIconUrl(String url) {
		icon.setSrc(url);
	}

	public void setAppState(AppState appState) {
		this.appState = appState;
		setButtonStyle(this.appState == AppState.STARTED);
		contextPopup.updateMenu();
	}

	private void setButtonStyle(boolean enabled) {
		outer.setStyleName(res.css().disabled(), !enabled);
		inner.setStyleName(res.css().disabled(), !enabled);
		if (enabled) {
			icon.removeClassName(res.css().disabled());
		} else {
			icon.addClassName(res.css().disabled());
		}
	}

	protected void toggleContextButton(boolean visible) {
		contextButton.setStyleName(res.css().active(), visible);
	}

	@UiHandler("contextButton")
	void onMenuOpen(MouseDownEvent event) {
		showMenu();
	}

	private void showMenu() {
		if (!contextPopup.isShowing()) {
			toggleContextButton(true);
			contextPopup.contextMenuPanel.setStyleName(res.css().active(), false);
			contextPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					contextPopup.setPopupPosition(outer.getAbsoluteLeft(), outer.getAbsoluteTop());
					contextPopup.fadeIn();
				}
			});
		}
	}

	public void hideMenu() {
		contextPopup.hide();
	}

	public void disableMenuEntries(boolean lock) {
		contextPopup.disableMenuEntries(lock);
	}

	@Override
	public HandlerRegistration addAppEventHandler(AppEvent.Handler handler) {
		this.addHandler(handler, AppEvent.getType());
		return contextPopup.addHandler(handler, AppEvent.getType());
	}

	@UiHandler("outer")
	void onAppClick(ClickEvent event) {
		if (appState == AppState.STARTED && hasUi) {
			fireEvent(new AppEvent(AppEvent.Action.OPEN, appInfo));
		}
	}

	@UiHandler("outer")
	void onAppMouseDown(MouseDownEvent event) {
		if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
			longPressTimer.cancel();
			longPressTimer.schedule(500);
		}
	}

	@UiHandler("outer")
	void onAppMouseUp(MouseUpEvent event) {
		longPressTimer.cancel();
	}

	@Override
	public HandlerRegistration addConfigEventHandler(Handler handler) {
		return contextPopup.addHandler(handler, ConfigEvent.getType());
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	protected AppState getAppState() {
		return appState;
	}

	public void update(AppInfo appInfo) {
		this.appInfo = appInfo;
		licenseWarningButton
				.setVisible(appInfo.getLicense() != null && appInfo.getLicense() == LicenseState.UNLICENSED);
		contextPopup.update();
	}
}
