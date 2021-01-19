package havis.net.ui.core.client.app;

import com.google.gwt.user.client.ui.Label;

import havis.net.ui.core.resourcebundle.ResourceBundle;

public class MenuEntry extends Label {

	private ResourceBundle res = ResourceBundle.INSTANCE;
	private boolean locked;
	private boolean allowed;

	public MenuEntry() {
	}

	public boolean isEnabled() {
		return !locked && allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
		updateStyle();
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		updateStyle();
	}

	private void updateStyle() {
		this.setStyleName(res.css().disabled(), locked || !allowed);
	}
}
