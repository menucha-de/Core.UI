package havis.net.ui.core.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ManagementPlace extends Place {
	private String page;
	
	public ManagementPlace(String token) {
		this.page = token;
	}
	
	public String getPage() {
		return page;
	}
	
	@Prefix("management")
	public static class Tokenizer implements PlaceTokenizer<ManagementPlace> {
		@Override
		public ManagementPlace getPlace(String token) {
			return new ManagementPlace(token);
		}
		
		@Override
		public String getToken(ManagementPlace place) {
			return place.getPage();
		}
	}
}
