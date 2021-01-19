package havis.net.ui.core.client.log.config;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.rest.core.async.LogServiceAsync;
import havis.net.ui.core.client.base.BaseActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.net.ui.core.resourcebundle.ConstantsResource;
import havis.net.ui.shared.data.HttpMethod;
import havis.util.core.log.LogLevel;
import havis.util.core.log.LogTarget;

public class LogConfigActivity extends BaseActivity implements LogConfigView.Presenter {

	private static final int MAX_RETRIES = 60;

	private ConstantsResource cons = ConstantsResource.INSTANCE;
	private LogServiceAsync service = GWT.create(LogServiceAsync.class);

	private List<LogTarget> targets;
	private List<LogLevel> levels;
	private TreeMap<String, TargetConfig> targetMap = new TreeMap<>();

	private boolean levelsLoaded = false;
	private boolean targetsLoaded = false;

	private OptionsCallback logLevelChange = new OptionsCallback(HttpMethod.PUT);
	private OptionsCallback logDelete = new OptionsCallback(HttpMethod.DELETE);

	private LogConfigView logConfigView;
	private int retries;

	private ClientFactory clientFactory;

	private class OptionsCallback implements MethodCallback<Void> {
		private boolean checked = false;
		private boolean allowed = false;
		private HttpMethod method;

		public OptionsCallback(HttpMethod method) {
			this.method = method;
		}

		public boolean isAllowed() {
			return allowed;
		}

		@Override
		public void onFailure(Method method, Throwable exception) {
			checked = true;
			allowed = false;
		}

		@Override
		public void onSuccess(Method method, Void response) {
			checked = true;
			allowed = this.method.isAllowed(method.getResponse());
			if (logLevelChange.checked && logDelete.checked) {
				loadLogLevels();
				loadLogTargets();
			}
		}

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.logConfigView = clientFactory.getLogConfigView();
		logConfigView.setPresenter(this);
		panel.setWidget(logConfigView.asWidget());
		checkAvailability();
		addListHeader();
	}
	
	private void checkAvailability() {
		++retries;
		service.isServiceAvailable(new TextCallback() {

			@Override
			public void onSuccess(Method method, String response) {
				onReset();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				new Timer() {

					@Override
					public void run() {
						if (retries < MAX_RETRIES) {
							checkAvailability();
						}
					}
				}.schedule(500);
			}
		});
	}

	public LogConfigActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private void addListHeader() {
		logConfigView.getConfigurationList().removeHeader();
		logConfigView.getConfigurationList().addHeaderCell(cons.logName());
		logConfigView.getConfigurationList().addHeaderCell(cons.logTargetID());
		logConfigView.getConfigurationList().addHeaderCell(cons.logLevel());
		logConfigView.getConfigurationList().addHeaderCell(cons.logClear());
	}

	private void addLogTargetConfiguration(TargetConfig targetConfiguration) {
		logConfigView.getConfigurationList().addItem(targetConfiguration.getWidgets());
	}

	@Override
	public void onReset() {
		service.optionsClear(logDelete);
		service.optionsLevel(logLevelChange);
	}

	private void loadLogLevels() {
		service.getLevels(new MethodCallback<List<LogLevel>>() {

			@Override
			public void onSuccess(Method method, List<LogLevel> response) {
				levels = response;
				levels.remove(LogLevel.ALL);
				levelsLoaded = true;
				if (targetsLoaded)
					loadConfigTargets();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				//logConfigView.getErrorPanel().showErrorMessage("Error loading log levels");
			}
		});
	}

	private void loadLogTargets() {
		service.getTargets(new MethodCallback<List<LogTarget>>() {

			@Override
			public void onSuccess(Method method, List<LogTarget> response) {
				if (targets != null) {
					for (LogTarget target : targets) {
						if (!response.contains(target)) {
							targetMap.remove(target.getName());
						}
					}
				}
				targets = response;
				targetsLoaded = true;
				if (levelsLoaded)
					loadConfigTargets();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				//logConfigView.getErrorPanel().showErrorMessage("Error loading log targets");
			}
		});
	}

	private void loadLevelForTarget(final String label, final TargetConfig conf) {
		service.getLevel(label, new MethodCallback<LogLevel>() {

			@Override
			public void onSuccess(Method method, LogLevel response) {
				conf.setLevel(response);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				//logConfigView.getErrorPanel().showErrorMessage("Error loading log level for target " + label);
			}
		});
	}

	private void loadConfigTargets() {
		for (LogTarget target : targets) {
			TargetConfig conf = targetMap.get(target.getName());
			if (conf == null) {
				conf = new TargetConfig(this, target, levels, logLevelChange.isAllowed(), logDelete.isAllowed());
				targetMap.put(target.getName(), conf);
			}
			loadLevelForTarget(target.getName(), conf);
		}

		logConfigView.getConfigurationList().clear();
		for (TargetConfig target : targetMap.values()) {
			addLogTargetConfiguration(target);
		}
	}

	@Override
	public void onSelectLogLevel(final String target, LogLevel level) {
		service.setLevel(target, level, new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				//logConfigView.getErrorPanel().showErrorMessage("Log level for target " + target + " could not be changed.");
			}
		});
	}

	@Override
	public void onClearLogLevel(final String target) {
		logConfigView.getExecuting().setValue(true);
		service.clear(target, new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				logConfigView.getExecuting().setValue(false);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				logConfigView.getExecuting().setValue(false);
				//logConfigView.getErrorPanel().showErrorMessage("Log for target " + target + " could not be deleted.");
			}
		});
	}

	@Override
	public void onRefresh() {
		for (Entry<String, TargetConfig> conf : targetMap.entrySet()) {
			loadLevelForTarget(conf.getKey(), conf.getValue());
		}
	}
}
