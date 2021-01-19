package havis.net.ui.core.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.core.client.app.event.AppEvent;
import havis.net.ui.core.client.app.event.ConfigEvent;
import havis.net.ui.core.client.base.Permissions;
import havis.net.ui.core.resourcebundle.ConstantsResource;
import havis.net.ui.core.resourcebundle.ResourceBundle;
import havis.net.ui.shared.client.upload.File;
import havis.net.ui.shared.client.upload.FileList;
import havis.net.ui.shared.client.upload.MultipleFileUpload;
import havis.util.core.app.AppInfo;
import havis.util.core.app.AppState;
import havis.util.core.license.LicenseState;

public class ContextPopup extends PopupPanel {
	private ResourceBundle res = ResourceBundle.INSTANCE;
	private ConstantsResource i18n = ConstantsResource.INSTANCE;
	private static ContextPopupUiBinder uiBinder = GWT.create(ContextPopupUiBinder.class);

	interface ContextPopupUiBinder extends UiBinder<Widget, ContextPopup> {
	}

	@UiField
	LIElement title;

	@UiField
	MenuEntry toggle;

	@UiField
	MenuEntry delete;

	@UiField
	MenuEntry activate;

	@UiField
	MenuEntry reset;

	@UiField
	MenuEntry info;

	@UiField
	MenuEntry exportConf;

	@UiField
	MenuEntry importConf;

	@UiField
	MenuEntry resetConf;

	@UiField
	MenuEntry backupConf;

	@UiField
	MenuEntry restoreConf;

	@UiField
	MenuEntry deleteConf;

	@UiField
	MultipleFileUpload upload;

	@UiField
	UListElement contextMenu;

	@UiField
	UListElement configMenu;

	@UiField
	LIElement config;

	@UiField
	LIElement activateItem;
	
	@UiField
	LIElement resetItem;

	@UiField
	LIElement toggleLi;

	@UiField
	LIElement deleteLi;

	@UiField
	LIElement resetConfLi;

	@UiField
	LIElement backupConfLi;

	@UiField
	LIElement restoreConfLi;

	@UiField
	LIElement deleteConfLi;

	@UiField
	HTMLPanel contextMenuPanel;

	private AppInfo appInfo;
	private boolean showConfigMenu;
	private boolean activateRemoved = false;
	private int configMenuHeight;
	private AppButton button;

	public ContextPopup(AppButton button, Permissions permissions) {
		super(true, true);
		add((uiBinder.createAndBindUi(this)));
		this.button = button;
		this.appInfo = button.getAppInfo();
		if (!hasConfig(appInfo)) {
			contextMenu.removeChild(config);
		}
		if (appInfo.getSection().equals("device")) {
			contextMenu.removeChild(toggleLi);
			contextMenu.removeChild(deleteLi);

			configMenu.removeChild(resetConfLi);
			configMenu.removeChild(backupConfLi);
			configMenu.removeChild(restoreConfLi);
			configMenu.removeChild(deleteConfLi);
		} else {
			configMenu.removeChild(resetItem);
		}

		if (appInfo.getSection().equals("driver")) {
			contextMenu.removeChild(toggleLi);
		}

		title.setInnerText(appInfo.getLabel());
		setPermissions(permissions);
	}

	public void update() {
		boolean activateVisible = appInfo.getLicense() != null && appInfo.getLicense() == LicenseState.UNLICENSED;
		if (!activateVisible && !activateRemoved) {
			activateRemoved = true;
			contextMenu.removeChild(activateItem);
		}
	}

	private boolean hasConfig(AppInfo appInfo) {
		String[] config = appInfo.getConfig();
		if (config != null && config.length > 0) {
			if (config.length == 1 && config[0].isEmpty()) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void hide(final boolean autoClosed) {
		contextMenuPanel.setStyleName(res.css().active(), false);
		button.toggleContextButton(false);
		new Timer() {

			@Override
			public void run() {
				ContextPopup.super.hide(autoClosed);
			}
		}.schedule(300);
	}

	private void setPermissions(Permissions permissions) {
		toggle.setAllowed(permissions.state);
		delete.setAllowed(permissions.delete);
		activate.setAllowed(permissions.licenseState);
		info.setAllowed(true);
		if (appInfo.getSection().equals("device")) {
			reset.setAllowed(true);
			exportConf.setAllowed(true);
			importConf.setAllowed(true);
		} else {
			exportConf.setAllowed(permissions.configDownload);
			importConf.setAllowed(permissions.configUpload);
		}
		resetConf.setAllowed(permissions.configReset);
		backupConf.setAllowed(permissions.configBackup);
		restoreConf.setAllowed(permissions.configRestore);
		deleteConf.setAllowed(permissions.configDelete);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		showConfigMenu = false;
		configMenu.getStyle().clearHeight();
		configMenuHeight = configMenu.getOffsetHeight();
		config.replaceClassName(res.css().opened(), res.css().closed());

		configMenu.getStyle().setHeight(0, Unit.PX);
	}

	@UiHandler("delete")
	void onDeleteClick(ClickEvent event) {
		if (delete.isEnabled()) {
			disableMenuEntries(true);
			fireEvent(new AppEvent(AppEvent.Action.DELETE, appInfo));
			hide();
		}
	}

	@UiHandler("toggle")
	void onToggleClick(ClickEvent event) {
		if (toggle.isEnabled()) {
			disableMenuEntries(true);
			if (button.getAppState() == AppState.STARTED) {
				fireEvent(new AppEvent(AppEvent.Action.STOP, appInfo));
			} else {
				fireEvent(new AppEvent(AppEvent.Action.START, appInfo));
			}
			hide();
		}
	}

	@UiHandler("info")
	void onInfoClick(ClickEvent event) {
		if (info.isEnabled()) {
			fireEvent(new AppEvent(AppEvent.Action.INFO, appInfo));
			hide();
		}
	}

	@UiHandler("activate")
	void onActivateClick(ClickEvent event) {
		if (activate.isEnabled()) {
			fireEvent(new AppEvent(AppEvent.Action.ACTIVATE, appInfo));
			hide();
		}
	}

	@UiHandler("reset")
	void onResetClick(ClickEvent event) {
		if (reset.isEnabled()) {
			fireEvent(new AppEvent(AppEvent.Action.ACTIVATE, appInfo));
			hide();
		}
	}

	@UiHandler("exportConf")
	void onExportConfig(ClickEvent event) {
		if (exportConf.isEnabled()) {
			fireEvent(new ConfigEvent(ConfigEvent.Action.EXPORT, appInfo));
			hide();
		}
	}

	@UiHandler("importConf")
	void onImportConfig(ClickEvent event) {
		if (importConf.isEnabled()) {
			upload.reset();
			upload.click();
		}
	}

	@UiHandler("resetConf")
	void onResetConfig(ClickEvent event) {
		if (resetConf.isEnabled()) {
			fireEvent(new ConfigEvent(ConfigEvent.Action.RESET, appInfo));
			hide();
		}
	}

	@UiHandler("backupConf")
	void onBackupConfig(ClickEvent event) {
		if (backupConf.isEnabled()) {
			fireEvent(new ConfigEvent(ConfigEvent.Action.BACKUP, appInfo));
			hide();
		}
	}

	@UiHandler("restoreConf")
	void onRestoreConfig(ClickEvent event) {
		if (restoreConf.isEnabled()) {
			fireEvent(new ConfigEvent(ConfigEvent.Action.RESTORE, appInfo));
			hide();
		}
	}

	@UiHandler("deleteConf")
	void onDeleteConfig(ClickEvent event) {
		if (deleteConf.isEnabled()) {
			fireEvent(new ConfigEvent(ConfigEvent.Action.DELETE, appInfo));
			hide();
		}
	}

	@UiHandler("upload")
	void onChooseFile(ChangeEvent event) {
		FileList fl = upload.getFileList();
		File f = fl.html5Item(0);

		fireEvent(new ConfigEvent(ConfigEvent.Action.IMPORT, appInfo, f));
		hide();
	}

	@UiHandler("configLabel")
	void onShowConfigMenu(ClickEvent event) {
		if (!showConfigMenu) {
			showConfigMenu = true;
			config.replaceClassName(res.css().closed(), res.css().opened());
			configMenu.getStyle().setHeight(configMenuHeight, Unit.PX);
		} else {
			showConfigMenu = false;
			config.replaceClassName(res.css().opened(), res.css().closed());
			configMenu.getStyle().setHeight(0, Unit.PX);
		}
	}

	protected void updateMenu() {
		if (button.getAppState() == AppState.STARTED) {
			toggle.setText(i18n.stop());
			toggle.addStyleName(res.css().started());
			toggle.removeStyleName(res.css().stopped());
		} else if (button.getAppState() == AppState.STOPPED) {
			toggle.setText(i18n.start());
			toggle.addStyleName(res.css().stopped());
			toggle.removeStyleName(res.css().started());
		}
		disableConfigEntries(button.getAppState() != AppState.STOPPED);
	}

	protected void disableMenuEntries(boolean lock) {
		toggle.setLocked(lock);
		delete.setLocked(lock);
		info.setLocked(lock);
		disableConfigEntries(button.getAppState() != AppState.STOPPED);
	}

	private void disableConfigEntries(boolean lock) {
		if (!appInfo.getSection().equals("device")) {
			exportConf.setLocked(lock);
			importConf.setLocked(lock);
			resetConf.setLocked(lock);
			backupConf.setLocked(lock);
			restoreConf.setLocked(lock);
			deleteConf.setLocked(lock);
		}
	}

	public void fadeIn() {
		contextMenuPanel.setStyleName(res.css().active(), true);
	}
}
