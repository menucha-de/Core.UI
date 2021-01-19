package havis.net.ui.core.client.apps.license;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.core.client.app.PasteTextBox;
import havis.net.ui.core.client.app.event.AppEvent;
import havis.net.ui.core.client.app.event.AppEvent.Action;
import havis.net.ui.core.client.app.event.PasteEvent;
import havis.net.ui.core.resourcebundle.ConstantsResource;
import havis.net.ui.shared.client.event.DialogCloseEvent;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.client.widgets.IconButton;
import havis.util.core.app.AppInfo;

public class LicenseActivateDialog extends Composite {

	@UiField
	CommonEditorDialog dialog;

	@UiField
	IconButton requestEmail;

	@UiField
	Label titleLabel;

	@UiField
	PasteTextBox license;

	private AppInfo appInfo;

	private ActivateLicenseActivity presenter;

	private static LicenseActivateDialogUiBinder uiBinder = GWT.create(LicenseActivateDialogUiBinder.class);

	interface LicenseActivateDialogUiBinder extends UiBinder<Widget, LicenseActivateDialog> {
	}

	public LicenseActivateDialog() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPresenter(ActivateLicenseActivity presenter) {
		this.presenter = presenter;
	}

	public void show(AppInfo appInfo) {
		this.appInfo = appInfo;
		if (appInfo.getSection().equals("device")) {
			titleLabel.setText(ConstantsResource.INSTANCE.resetTitle(appInfo.getLabel()));
			dialog.setButtonCaption(ConstantsResource.INSTANCE.reset());
		} else {
			titleLabel.setText(ConstantsResource.INSTANCE.activateTitle(appInfo.getLabel()));
		}
	}

	public String getLicenseString() {
		return license.getText();
	}

	@UiHandler("dialog")
	void onCloseDialog(DialogCloseEvent event) {
		if (event.isAccept()) {
			presenter.onChange();
		} else {
			presenter.onClose();
		}
	}

	@UiHandler("dialog")
	void onAccept(DialogCloseEvent event) {

	}

	@UiHandler("requestEmail")
	void onShowRequestDialog(ClickEvent event) {
		presenter.onRequest();
		fireEvent(new AppEvent(Action.REQUESTLICENSE, appInfo));
	}

	@UiHandler("license")
	void onPaste(PasteEvent event) {
		license.setText(event.getText());
		dialog.setButtonEnabled(true);
	}

	@UiHandler("license")
	void onKeyPress(KeyUpEvent event) {
		if (license.getText().isEmpty()) {
			dialog.setButtonEnabled(true);
		}
	}
}
