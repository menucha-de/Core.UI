package havis.net.ui.core.client.app;

import havis.net.ui.core.client.app.event.AppEvent;
import havis.net.ui.core.client.app.event.AppEvent.Action;
import havis.net.ui.core.client.app.event.AppEvent.Handler;
import havis.net.ui.core.resourcebundle.ConstantsResource;
import havis.net.ui.core.resourcebundle.ResourceBundle;
import havis.util.core.app.AppInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CoreButton extends Composite implements HasEnabled, HasClickHandlers, AppEvent.HasHandlers {

	@UiField
	FocusPanel buttonContainer;

	@UiField
	FlowPanel button;

	@UiField
	FocusPanel outer;

	@UiField
	HTML inner;

	@UiField
	Label label;

	@UiField
	ImageElement icon;

	@UiField
	Label contextButton;
	
	@UiField
	Label licenseWarningButton;

	private ResourceBundle res = ResourceBundle.INSTANCE;
	private Timer longPressTimer;

	private boolean enabled;

	private static CoreButtonUiBinder uiBinder = GWT.create(CoreButtonUiBinder.class);

	@UiTemplate("Button.ui.xml")
	interface CoreButtonUiBinder extends UiBinder<Widget, CoreButton> {
	}

	public static enum Type {
		INSTALL, MANAGEMENT, LOG
	}

	@UiConstructor
	public CoreButton(Type type) {
		initWidget(uiBinder.createAndBindUi(this));
		
		button.remove(licenseWarningButton);
		
		switch (type) {
		case INSTALL:
			icon.setSrc(res.iconInstall().getSafeUri().asString());

			setText(ConstantsResource.INSTANCE.install());

			button.remove(contextButton);
			break;
		case MANAGEMENT:
			icon.setSrc(res.iconManagement().getSafeUri().asString());

			setText(ConstantsResource.INSTANCE.management());

			contextButton.addStyleName(res.css().info());
			break;
		case LOG:
			icon.setSrc(res.iconLog().getSafeUri().asString());

			setText(ConstantsResource.INSTANCE.log());

			button.remove(contextButton);
			break;			
		}

		longPressTimer = new Timer() {
			@Override
			public void run() {
				showInfo();
			}
		};

		outer.sinkEvents(Event.ONCONTEXTMENU);
		outer.addHandler(new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
				showInfo();
			}
		}, ContextMenuEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return outer.addClickHandler(handler);
	}

	private void setText(String text) {
		label.setTitle(text);
		label.setText(text);
	}
	
	private void showInfo() {
		AppInfo info = new AppInfo("base", "", "", "", new String[] {}, "", "");
		fireEvent(new AppEvent(Action.INFO, info));
	}

	@UiHandler("contextButton")
	void onShowInfoClick(ClickEvent event) {
		showInfo();
	}

	@Override
	public HandlerRegistration addAppEventHandler(Handler handler) {
		return addHandler(handler, AppEvent.getType());
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
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		outer.setStyleName(res.css().disabled(), !enabled);
		inner.setStyleName(res.css().disabled(), !enabled);
		if (enabled) {
			icon.removeClassName(res.css().disabled());
		} else {
			icon.addClassName(res.css().disabled());
		}
	}

}
