package xavi.geo.geotweet;


public class LocationPoint {
	public String id, x, y, message, date;

	public LocationPoint(String id, String x, String y, String message,
			String date) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.message = message;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
}
