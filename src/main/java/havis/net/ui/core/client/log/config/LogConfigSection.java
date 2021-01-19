package havis.net.ui.core.client.log.config;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.shared.client.list.WidgetList;
import havis.net.ui.shared.client.widgets.LoadingSpinner;
import havis.net.ui.shared.client.widgets.Util;
import havis.net.ui.shared.resourcebundle.ConstantsResource;

public class LogConfigSection extends Composite implements LogConfigView {

	@UiField
	WidgetList configureList;

	LoadingSpinner spinner = new LoadingSpinner();

	ConstantsResource cons = ConstantsResource.INSTANCE;

	private static LogConfigSectionUiBinder uiBinder = GWT.create(LogConfigSectionUiBinder.class);

	interface LogConfigSectionUiBinder extends UiBinder<Widget, LogConfigSection> {
	}

	@UiConstructor
	public LogConfigSection() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public WidgetList getConfigurationList() {
		return configureList;
	}

	private TakesValue<Boolean> executing = new TakesValue<Boolean>() {

		Timer timer = new Timer() {

			@Override
			public void run() {
				spinner.hide();
			}
		};

		private long time = 0;
		private int value = 0;

		@Override
		public synchronized void setValue(Boolean value) {
			if (timer.isRunning()) {
				timer.cancel();
			}
			if (value) {
				time = new Date().getTime() + 500;
				spinner.getElement().getStyle().setDisplay(Display.BLOCK);
				spinner.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						int scrollTop = Util.getContentScrollTop();
						int innerHeight = Util.getWindowParentInnerHeight();
						int top = scrollTop + innerHeight / 2 - offsetHeight / 2;
						int left = (Window.getClientWidth() - offsetWidth) / 2;
						spinner.setPopupPosition(left, top);
					}
				});
				this.value++;
			} else {
				this.value--;
				if (this.value < 1) {
					this.value = 0;
					long remaining = time - new Date().getTime();
					timer.schedule((int) (remaining > 0 ? remaining : 0));
				}
			}
		}

		@Override
		public Boolean getValue() {
			return value > 0;
		}

	};

	@Override
	public TakesValue<Boolean> getExecuting() {
		return executing;
	}

}
