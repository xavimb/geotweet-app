package xavi.geo.geotweet;

public class Response {
	public String Count;
	public LocationPoint[] Items;
	public String getCount() {
		return Count;
	}
	public void setCount(String count) {
		Count = count;
	}
	public LocationPoint[] getItems() {
		return Items;
	}
	public void setItems(LocationPoint[] items) {
		Items = items;
	}
}
