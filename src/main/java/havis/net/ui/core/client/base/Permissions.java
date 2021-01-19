package havis.net.ui.core.client.base;

public class Permissions {

	public interface Callback {
		void getPermissions(Permissions permissions);
	}

	public boolean install;
	public boolean delete;
	public boolean state;
	public boolean configDownload;
	public boolean configUpload;
	public boolean configReset;
	public boolean configBackup;
	public boolean configRestore;
	public boolean configDelete;
	public boolean failed;
	public boolean licenseState;
	public boolean licenseRequest;
	public boolean licenseResponse;
}
