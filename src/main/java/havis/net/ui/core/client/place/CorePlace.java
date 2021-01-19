package havis.net.ui.core.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CorePlace extends Place {
	private String page;
	
	public CorePlace(String token) {
		this.page = token;
	}
	
	public String getPage() {
		return page;
	}
	
	@Prefix("core")
	public static class Tokenizer implements PlaceTokenizer<CorePlace> {
		@Override
		public CorePlace getPlace(String token) {
			return new CorePlace(token);
		}
		
		@Override
		public String getToken(CorePlace place) {
			return place.getPage();
		}
	}
}
