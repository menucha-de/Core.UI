package havis.net.ui.core.client.log.monitor;

import havis.net.ui.shared.client.list.WidgetList;
import havis.util.core.log.LogLevel;
import havis.util.core.log.LogTarget;

import java.io.IOException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasConstrainedValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class LogMonitorPanel extends Composite implements LogMonitorView {
	private Presenter presenter;

	private static LogMonitorViewUiBinder uiBinder = GWT.create(LogMonitorViewUiBinder.class);

	@UiField
	ToggleButton observeButton;

	@UiField(provided = true)
	ValueListBox<LogLevel> levelsList = new ValueListBox<LogLevel>(new LevelValueRenderer());

	@UiField(provided = true)
	ValueListBox<LogTarget> targetsList = new ValueListBox<LogTarget>(new TargetValueRenderer());

	@UiField
	ToggleButton expandLog;
	@UiField
	HTMLPanel logControls;
	@UiField
	WidgetList logList;
	@UiField
	Button refreshButton;
	@UiField
	Button exportButton;

	private static class LevelValueRenderer implements Renderer<LogLevel> {
		@Override
		public String render(LogLevel object) {
			return object.name();
		}

		@Override
		public void render(LogLevel object, Appendable appendable) throws IOException {
			appendable.append(object.name());
		}
	}

	private static class TargetValueRenderer implements Renderer<LogTarget> {
		@Override
		public String render(LogTarget object) {
			return object.getLabel();
		}

		@Override
		public void render(LogTarget object, Appendable appendable) throws IOException {
			appendable.append(object.getLabel());
		}
	}

	interface LogMonitorViewUiBinder extends UiBinder<Widget, LogMonitorPanel> {
	}

	public LogMonitorPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("expandLog")
	void onToggleLog(ValueChangeEvent<Boolean> e) {
		presenter.onExpandLog();
	}

	@UiHandler("observeButton")
	void onObserveClick(ClickEvent e) {
		presenter.onObserve();
	}

	@UiHandler("levelsList")
	void onSelectLevel(ValueChangeEvent<LogLevel> e) {
		presenter.onSelectLevel();
	}

	@UiHandler("targetsList")
	void onSelectTarget(ValueChangeEvent<LogTarget> e) {
		presenter.onSelectTarget();
	}

	@UiHandler("refreshButton")
	void onRefreshClick(ClickEvent e) {
		presenter.onRefreshLog();
	}

	@UiHandler("exportButton")
	void onExportClick(ClickEvent e) {
		presenter.onExportLog();
	}

	@UiHandler("logList")
	void onScroll(ScrollEvent e) {
		presenter.onScroll();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasConstrainedValue<LogLevel> getLogLevels() {
		return levelsList;
	}

	@Override
	public HasConstrainedValue<LogTarget> getLogTargets() {
		return targetsList;
	}

	@Override
	public HasValue<Boolean> isObserving() {
		return observeButton;
	}

	@Override
	public HasValue<Boolean> isExpanded() {
		return expandLog;
	}

	@Override
	public HasVisibility getLogControls() {
		return logControls;
	}

	@Override
	public WidgetList getLogList() {
		return logList;
	}

	@Override
	public HasVisibility getRefreshButton() {
		return refreshButton;
	}

	@Override
	public HasVisibility getExportButton() {
		return exportButton;
	}

}
