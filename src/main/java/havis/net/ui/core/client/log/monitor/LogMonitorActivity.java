package havis.net.ui.core.client.log.monitor;

import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import havis.net.rest.core.async.LogServiceAsync;
import havis.net.ui.core.client.base.BaseActivity;
import havis.net.ui.core.client.mvp.ClientFactory;
import havis.util.core.log.LogEntry;
import havis.util.core.log.LogLevel;
import havis.util.core.log.LogTarget;

public class LogMonitorActivity extends BaseActivity implements LogMonitorView.Presenter {

	private LogServiceAsync logging = GWT.create(LogServiceAsync.class);

	private LogMonitor model;
	private LogMonitorView view;
	private Timer timer;
	private boolean firstExpand = true;
	private boolean locked;
	private static final String DOWNLOAD_LINK_BASE = GWT.getHostPageBaseURL() + "rest/log/";
	private ClientFactory clientFactory;

	public LogMonitorActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view = clientFactory.getLogMonitorView();
		view.setPresenter(this);
		this.model = new LogMonitor();
		panel.setWidget(view);
	}

	private void initialize() {
		loadLogLevels();
		loadLogTargets();
		setListHeader();
	}

	private void refreshTargets() {
		loadLogTargets();
	}

	private void setDisabled(Element listBox, boolean disabled) {
		if (disabled) {
			listBox.setPropertyBoolean("disabled", disabled);
		} else {
			listBox.removeAttribute("disabled");
		}
	}

	private void setLocked(boolean locked) {
		this.locked = locked;

		setDisabled(((UIObject) view.getLogLevels()).getElement(), locked);
		setDisabled(((UIObject) view.getLogTargets()).getElement(), locked);
	}

	private void setButtonsVisible(boolean visible) {
		view.getRefreshButton().setVisible(visible);
		view.getExportButton().setVisible(visible);
		String overflow = locked ? "hidden" : "auto";
		view.getLogList().getItemsContainter().getElement().getStyle().setProperty("overflow", overflow);
	}

	private void loadLogLevels() {
		logging.getLevels(new MethodCallback<List<LogLevel>>() {

			@Override
			public void onSuccess(Method method, List<LogLevel> response) {
				model.setLevels(response);

				if (!model.getLevels().isEmpty()) {
					view.getLogLevels().setValue(model.getLevel());
					view.getLogLevels().setAcceptableValues(model.getLevels());
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
			}
		});
	}

	private void loadLogTargets() {
		logging.getTargets(new MethodCallback<List<LogTarget>>() {

			@Override
			public void onSuccess(Method method, List<LogTarget> response) {
				model.setTargets(response);
				if (!model.getTargets().isEmpty()) {
					view.getLogTargets().setValue(model.getTarget());
					view.getLogTargets().setAcceptableValues(model.getTargets());
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
			}
		});
	}

	private void setListHeader() {
		for (String item : LogMonitor.getFieldLabels()) {
			view.getLogList().addHeaderCell(item);
		}
	}

	private Widget[] createWidgetRow(String[] row) {
		Widget[] widgetRow = new Widget[row.length];
		SimplePanel level = new SimplePanel();
		level.addStyleName("level " + row[0]);
		level.setTitle(row[0]);
		widgetRow[0] = level;
		for (int i = 1; i < row.length; i++) {
			InlineHTML ih = new InlineHTML(row[i]);
			ih.setTitle(row[i]);
			widgetRow[i] = ih;
		}
		return widgetRow;
	}

	private void prependLogEntry(String[] row) {
		Widget[] w = createWidgetRow(row);
		view.getLogList().insertRow(w);
	}

	private void loadLogEntries(final boolean refresh) {
		logging.get(model.getTarget().getName(), model.getLevel(), model.getLimit(), model.getOffset(), null,
				new MethodCallback<List<LogEntry>>() {

					@Override
					public void onSuccess(Method method, List<LogEntry> response) {
						if (refresh) {
							model.reset();
							view.getLogList().clear();
						}
						model.setLogEntries(response);
						if (refresh) {
							int count = model.getInitialCount();
							int rows = 10;
							int i;
							for (i = 0; i < rows - count; ++i) {
								prependLogEntry(new String[] { "", "", "", "", "" });
							}
							for (; i < rows; ++i) {
								prependLogEntry(model.getPreviousEntry());
							}
							view.getLogList().getItemsContainter().setVerticalScrollPosition(34);
						}
						if (!view.isObserving().getValue()) {
							setLocked(false);
						}
					}

					@Override
					public void onFailure(Method method, Throwable exception) {
						if (!view.isObserving().getValue()) {
							setLocked(false);
						}
					}
				});
	}

	private void refresh() {
		if (!view.isObserving().getValue()) {
			setLocked(true);
		}
		logging.size(model.getTarget().getName(), model.getLevel(), new MethodCallback<Integer>() {

			@Override
			public void onSuccess(Method method, Integer response) {
				if (response > 0) {
					model.setOffset(response - model.getLimit());
					loadLogEntries(true);
				} else {
					view.getLogList().clear();
					if (!view.isObserving().getValue()) {
						setLocked(false);
					}
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				setLocked(false);
			}
		});
	}

	@Override
	public void onExpandLog() {
		if (firstExpand) {
			initialize();
			firstExpand = false;
		} else {
			refreshTargets();
		}
		if (view.isExpanded().getValue()) {
			refresh();
			view.getLogList().setHeight("307px");
		} else {
			view.getLogList().setHeight("0");
		}
		view.getLogControls().setVisible(view.isExpanded().getValue());
	}

	@Override
	public void onObserve() {
		refresh();
		if (view.isObserving().getValue()) {
			setLocked(true);
			setButtonsVisible(false);
			timer = new Timer() {

				@Override
				public void run() {
					refresh();
				}
			};
			timer.scheduleRepeating(1000);
		} else {
			timer.cancel();
			setLocked(false);
			setButtonsVisible(true);
		}
	}

	@Override
	public void onSelectLevel() {
		model.setLevel(view.getLogLevels().getValue());

		refresh();
	}

	@Override
	public void onSelectTarget() {
		model.setTarget(view.getLogTargets().getValue());
		refresh();
	}

	@Override
	public void onClearLog() {
		view.getLogList().clear();
	}

	@Override
	public void onExportLog() {
		String link = DOWNLOAD_LINK_BASE + model.getTarget().getName() + "/" + model.getLevel() + "/export";
		Window.Location.assign(link);
	}

	private void scrollUp() {
		if (model.getCursor() == model.getLogEntries().size() - 5) {
			model.setOffset(model.getOffset() - model.getLimit());
			if (!model.isEndReached()) {
				loadLogEntries(false);
			}
		}
		String[] entry = model.getPreviousEntry();
		if (entry != null) {
			prependLogEntry(entry);
		}
	}

	private void scrollDown() {
		if (!locked) {
			refresh();
		}
	}

	@Override
	public void onRefreshLog() {
		refresh();
	}

	@Override
	public void onScroll() {
		ScrollPanel itemsContainer = view.getLogList().getItemsContainter();
		int maxPos = itemsContainer.getMaximumVerticalScrollPosition();
		int scrollPos = itemsContainer.getVerticalScrollPosition();
		if (scrollPos == 0) {
			scrollUp();
			maxPos = itemsContainer.getMaximumVerticalScrollPosition();
			itemsContainer.setVerticalScrollPosition(34);
		}

		if (scrollPos == maxPos) {
			scrollDown();
			maxPos = itemsContainer.getMaximumVerticalScrollPosition();
			itemsContainer.setVerticalScrollPosition(34);
		}
	}

}
