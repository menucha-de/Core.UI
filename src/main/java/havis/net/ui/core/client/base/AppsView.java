package havis.net.ui.core.client.base;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

import havis.net.ui.core.client.app.AppButton;

public interface AppsView extends IsWidget {
	void addAppButton(AppButton appButton);

	void removeAppButton(AppButton appButton);

	Map<String, AppButton> getAppButtons();

	void setPresenter(Presenter presenter);
	
	public interface Presenter {
	}
}
