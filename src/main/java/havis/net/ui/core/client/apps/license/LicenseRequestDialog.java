package havis.net.ui.core.client.apps.license;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import elemental.client.Browser;
import havis.net.ui.core.resourcebundle.ConstantsResource;
import havis.net.ui.shared.client.event.DialogCloseEvent;
import havis.util.core.app.AppInfo;
import havis.util.core.license.License;

public class LicenseRequestDialog extends Composite {

	@UiField
	TextArea emailText;
	
	@UiField
	Label titleLabel;
	
	@UiField
	TableCellElement product;
	
	@UiField
	TableCellElement sn;

	private AppInfo appInfo;
	private License license;
	private String productText;

	private RequestLicenseActivity presenter;
	
	private static LicenseRequestDialogUiBinder uiBinder = GWT.create(LicenseRequestDialogUiBinder.class);

	interface LicenseRequestDialogUiBinder extends UiBinder<Widget, LicenseRequestDialog> {
	}

	public LicenseRequestDialog() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void setValues() {
		titleLabel.setText(ConstantsResource.INSTANCE.requestTitle(appInfo.getLabel()));
		product.setInnerText(productText);
		sn.setInnerText(license.getSerial());
	}
	
	public void show(AppInfo appInfo, License license) {
		this.appInfo = appInfo;
		this.license = license;
		productText = appInfo.getSection().equals("device") ? appInfo.getReset() : license.getProduct();
		setValues();
	}
	
	@UiHandler("dialog")
	void onCloseDialog(DialogCloseEvent event) {
		presenter.onBackButton();
	}
	
	@UiHandler("toClipboard")
	void onCopyToClipboard(ClickEvent event) {
		emailText.setText(ConstantsResource.INSTANCE.emailText(productText, license.getSerial()));
		emailText.setFocus(true);
		emailText.selectAll();
		Browser.getWindow().getDocument().execCommand("copy", false, "");
	}

	public void setPresenter(RequestLicenseActivity presenter) {
		this.presenter = presenter;
	}
}
