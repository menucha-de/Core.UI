package havis.net.ui.core.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface ResourceBundle extends ClientBundle {

	public static final ResourceBundle INSTANCE = GWT.create(ResourceBundle.class);

	@Source("resources/CssResources.css")
	CssResources css();

	@Source("resources/context_opened.png")
	DataResource contextOpened();

	@Source("resources/context_closed.png")
	DataResource contextClosed();

	@Source("resources/editor_close.png")
	DataResource editorClose();

	@Source("resources/app_info.png")
	DataResource appInfo();

	@Source("resources/app_delete.png")
	DataResource appDelete();

	@Source("resources/app_started.png")
	DataResource appStarted();

	@Source("resources/app_stopped.png")
	DataResource appStopped();

	@Source("resources/app_license.png")
	DataResource appLicense();

	@Source("resources/app_license_big.png")
	DataResource appLicenseBig();

	@Source("resources/app_license_email.png")
	DataResource appLicenseEmail();

	@Source("resources/app_license_copy.png")
	DataResource appLicenseCopy();

	@Source("resources/Icon_Install.png")
	DataResource iconInstall();

	@Source("resources/Icon_Log.png")
	DataResource iconLog();

	@Source("resources/Icon_Drivers.png")
	DataResource iconDrivers();
	
	@Source("resources/Icon_Management.png")
	DataResource iconManagement();

	@Source("resources/config_closed.png")
	DataResource configClosed();

	@Source("resources/config_opened.png")
	DataResource configOpened();

	@Source("resources/config_download.png")
	DataResource configDownload();

	@Source("resources/config_upload.png")
	DataResource configUpload();

	@Source("resources/config_reset.png")
	DataResource configReset();

	@Source("resources/config_backup.png")
	DataResource configBackup();

	@Source("resources/config_restore.png")
	DataResource configRestore();

	@Source("resources/config_delete.png")
	DataResource configDelete();

	@Source("resources/config_download_disabled.png")
	DataResource configDownloadDisabled();

	@Source("resources/config_upload_disabled.png")
	DataResource configUploadDisabled();

	@Source("resources/config_reset_disabled.png")
	DataResource configResetDisabled();

	@Source("resources/config_backup_disabled.png")
	DataResource configBackupDisabled();

	@Source("resources/config_restore_disabled.png")
	DataResource configRestoreDisabled();

	@Source("resources/config_delete_disabled.png")
	DataResource configDeleteDisabled();

}
