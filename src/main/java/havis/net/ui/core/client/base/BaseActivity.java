package havis.net.ui.core.client.base;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

public abstract class BaseActivity extends AbstractActivity {

	public abstract void start(AcceptsOneWidget panel, EventBus eventBus);

	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		start(panel, (EventBus) eventBus);
	}

}
