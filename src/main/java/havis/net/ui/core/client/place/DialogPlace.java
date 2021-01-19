package havis.net.ui.core.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class DialogPlace extends Place {
	
//	public enum Dialog {
//		REQUEST_LICENSE, ACTIVATE_LICENSE, INFO
//	}
//	
	private String type;
	private String appName;
	private String section;
	
	public DialogPlace(String type, String appName, String section) {
		this.type = type;
		this.appName = appName;
		this.section = section;
	}
	
	public String getType() {
		return type;
	}
	
	public String getAppName() {
		return appName;
	}

	public String getSection() {
		return section;
	}

	@Prefix("dialog")
	public static class Tokenizer implements PlaceTokenizer<DialogPlace> {
		@Override
		public DialogPlace getPlace(String token) {
			String[] arr = token.split(":");
			return new DialogPlace(arr[0], arr[1], arr[2]);
		}
		
		@Override
		public String getToken(DialogPlace place) {
			return place.getType() + ":" + place.getAppName() + ":" + place.getSection();
		}
	}

}
