package havis.net.ui.core.client.log.config;

import java.io.IOException;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

import havis.util.core.log.LogLevel;
import havis.util.core.log.LogTarget;

public class TargetConfig extends Composite {

	@UiField
	InlineHTML label;
	@UiField
	InlineHTML name;
	@UiField(provided = true)
	ValueListBox<LogLevel> levels = new ValueListBox<LogLevel>(new Renderer<LogLevel>() {
		@Override
		public String render(LogLevel object) {
			return object.name();
		}

		@Override
		public void render(LogLevel object, Appendable appendable) throws IOException {
			appendable.append(object.name());
		}
	});
	@UiField
	Button clearLogLevel;
	private LogConfigView.Presenter presenter;

	private static TargetConfigurationUiBinder uiBinder = GWT.create(TargetConfigurationUiBinder.class);

	interface TargetConfigurationUiBinder extends UiBinder<Widget, TargetConfig> {
	}

	public TargetConfig(LogConfigView.Presenter presenter, LogTarget target, List<LogLevel> levels,
			boolean levelChangeAllowed, boolean clearAllowed) {
		initWidget(uiBinder.createAndBindUi(this));
		this.label.setText(target.getLabel());
		this.name.setText(target.getName());
		this.levels.setValue(LogLevel.ALL);
		this.levels.setAcceptableValues(levels);
		this.levels.setEnabled(levelChangeAllowed);
		this.clearLogLevel.setEnabled(clearAllowed);
		this.presenter = presenter;
	}

	@UiHandler("levels")
	void onLevelChange(ValueChangeEvent<LogLevel> event) {
		presenter.onSelectLogLevel(name.getText(), levels.getValue());
	}

	@UiHandler("clearLogLevel")
	void onClearClick(ClickEvent event) {
		presenter.onClearLogLevel(name.getText());
	}

	public void setLevel(LogLevel level) {
		this.levels.setValue(level);
	}

	public Widget[] getWidgets() {
		Widget[] result = new Widget[4];
		result[0] = label;
		result[1] = name;
		result[2] = levels;
		result[3] = clearLogLevel;
		return result;
	}
}