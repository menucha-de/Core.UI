package havis.net.ui.core.client.apps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.shared.client.event.DialogCloseEvent;

public class InfoDialog extends Composite {

	@UiField
	Frame infoHtmlIframe;

	private InfoActivity presenter;

	private static InfoDialogUiBinder uiBinder = GWT.create(InfoDialogUiBinder.class);

	interface InfoDialogUiBinder extends UiBinder<Widget, InfoDialog> {
	}

	public InfoDialog() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("infoHtmlIframe")
	void onLoadFrame(LoadEvent event) {
		IFrameElement f = IFrameElement.as(infoHtmlIframe.getElement());
		infoHtmlIframe.setHeight(f.getContentDocument().getScrollHeight() + "px");
	}

	public void show(String url) {
		infoHtmlIframe.setUrl(url);
	}
	
	@UiHandler("dialog")
	void onCloseDialog(DialogCloseEvent event) {
		presenter.onBackButton();
	}

	public void setPresenter(InfoActivity presenter) {
		this.presenter = presenter;
	}

}
