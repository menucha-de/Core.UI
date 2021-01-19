package havis.net.ui.core.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Messages;

public interface ConstantsResource extends Messages {
	
	public static final ConstantsResource INSTANCE = GWT.create(ConstantsResource.class);

	String install();
	String management();
	String start();
	String stop();
	String delete();
	String info();
	String activate();
	String config();
	String download();
	String upload();
	String reset();
	String backup();
	String restore();
	String logName();
	String logTargetID();
	String logLevel();
	String logClear();
	String drop();
	String clipboard();
	String toClipboard();
	String noProductKey();
	String requestWww();
	String requestEmail();
	String or();
	String contactSalesPerson();
	String product();
	String partNumber();
	String serialMica();
	String sn();
	String emailText(String product, String serial);
	String requestTitle(String product);
	String activateTitle(String product);
	String resetTitle(String product);
	String back();
	String log();
}
