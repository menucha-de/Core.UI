package havis.net.ui.core.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import havis.net.ui.core.client.place.CorePlace;
import havis.net.ui.core.client.place.DialogPlace;
import havis.net.ui.core.client.place.ManagementPlace;

@WithTokenizers({
	CorePlace.Tokenizer.class,
	ManagementPlace.Tokenizer.class,
	DialogPlace.Tokenizer.class
})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
