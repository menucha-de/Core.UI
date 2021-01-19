package havis.net.ui.core.client.app;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

import havis.net.ui.core.client.app.event.PasteEvent;
import havis.net.ui.core.client.app.event.PasteEvent.Handler;

public class PasteTextBox extends TextBox implements PasteEvent.HasHandlers {

	public PasteTextBox() {
		super();
		sinkEvents(Event.ONPASTE);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONPASTE:
			event.stopPropagation();
			event.preventDefault();
			elemental.events.Event ev = (elemental.events.Event) event;
			String pastedText = ev.getClipboardData().getData("text/plain");
			fireEvent(new PasteEvent(pastedText));
			break;
		}
	}

	@Override
	public HandlerRegistration addPasteEventHandler(Handler handler) {
		return addHandler(handler, PasteEvent.getType());
	}
}
