package xavi.geo.geotweet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.stream.JsonReader;


public class APIRequest {

	public static String httpresponse;
	private static Boolean finished = false;

	public static void connect(String url) {

		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object
		HttpGet httpget = new HttpGet(url);

		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Examine the response status
			Log.i("Praeda", response.getStatusLine().toString());

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				httpresponse = convertStreamToString(instream);
				// now you have the string representation of the HTML request
				instream.close();
			}

		} catch (Exception e) {
		}

	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static ArrayList<LocationPoint> getAll() throws IOException {
		new Thread() {
			public void run() {

				connect("http://ec2-107-22-139-130.compute-1.amazonaws.com/all?callback=?");

				synchronized (finished) {
					finished = true;
				}

			}
		}.start();
		while (!finished);
		
		String parsed = httpresponse.substring(4).replace('(', ' ').replace(')', ' ');
		System.out.print(parsed);
		
		//InputStream is = new ByteArrayInputStream(parsed.getBytes("UTF-8"));
		//ArrayList res = (ArrayList) readJsonStream(is);
		ArrayList<LocationPoint> result = new ArrayList<LocationPoint>();
		
		try {
		      JSONObject jsonObject = new JSONObject(parsed);
		      Log.i(APIRequest.class.getName(),
		          "Number of entries " + jsonObject.getString("Count"));
		      JSONArray jsonArray = jsonObject.getJSONArray("Items");
		      for (int i = 0; i < jsonArray.length(); i++) {
		        JSONObject jsonPoint = jsonArray.getJSONObject(i);
		        Log.i(APIRequest.class.getName(), jsonPoint.getString("text"));
		        
		        String _id = jsonPoint.getJSONObject("id").getString("S");
		        String _text = jsonPoint.getJSONObject("text").getString("S");
		        String _x = jsonPoint.getJSONObject("x").getString("S");
		        String _y = jsonPoint.getJSONObject("y").getString("S");
		        String _date = jsonPoint.getJSONObject("date").getString("S");
		        
		        LocationPoint lp = new LocationPoint(_id, _x, _y, _text, _date);
		        result.add(lp);
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		/*for (int i=0; i<res.size(); i++) {
			System.out.println(res.get(i));
		}*/
		
		return result;
	}

	public static List readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readMessagesArray(reader);
		} finally {
			reader.close();
		}
	}

	public static List readMessagesArray(JsonReader reader) throws IOException {
		List messages = new ArrayList();

		reader.beginArray();
		while (reader.hasNext()) {
			messages.add(readMessage(reader));
		}
		reader.endArray();
		return messages;
	}

	public static List readMessage(JsonReader reader) throws IOException {
		long id = -1;
		double x,y;
		String text = null;
		String user = null;
		List geo = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextLong();
			} else if (name.equals("text")) {
				text = reader.nextString();
			} else if (name.equals("geo")) {
				geo = readDoublesArray(reader);
			} else if (name.equals("x")) {
				x = reader.nextDouble();
			} else if (name.equals("y")) {
				y = reader.nextDouble();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		ArrayList<LocationPoint> lp = new ArrayList<LocationPoint>();
		lp.add(new LocationPoint("", "", "", "", ""));
		return lp;
	}

	public static List readDoublesArray(JsonReader reader) throws IOException {
		List doubles = new ArrayList();

		reader.beginArray();
		while (reader.hasNext()) {
			doubles.add(reader.nextDouble());
		}
		reader.endArray();
		return doubles;
	}

}
