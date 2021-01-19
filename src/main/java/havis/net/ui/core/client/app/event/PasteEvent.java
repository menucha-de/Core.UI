package havis.net.ui.core.client.app.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class PasteEvent extends GwtEvent<PasteEvent.Handler> {
	public interface Handler extends EventHandler {
		void onPasteEvent(PasteEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addPasteEventHandler(PasteEvent.Handler handler);
	}

	private static final Type<PasteEvent.Handler> TYPE = new Type<>();

	private String text;
	
	public PasteEvent(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	public static Type<Handler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onPasteEvent(this);
	}

}
