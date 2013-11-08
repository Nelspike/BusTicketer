package bus.ticketer.connection;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.message.*;
import org.json.*;

public class ConnectionRunnable implements Runnable {

	private String link, resultString, method;
	private ArrayList<NameValuePair> payload;
	private int readTimeout = 10000, connectionTimeout = 15000;
	private JSONObject resultObject;

	public ConnectionRunnable(String link, String method,
			ArrayList<NameValuePair> payload) {
		this.link = link;
		this.method = method;
		this.payload = payload;
	}

	@Override
	public void run() {
		connect();
	}

	private void connect() {
		HttpURLConnection con = null;
		String line = "";
		StringBuffer sb = new StringBuffer();
		try {

			URL url = new URL(link);
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(readTimeout);
			con.setConnectTimeout(connectionTimeout);
			con.setRequestMethod(method);
			con.setDoInput(true);

			if (payload != null) {
				con.setDoOutput(true);

				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

				for (NameValuePair pair : payload)
					params.add(new BasicNameValuePair(pair.getName(), pair
							.getValue()));

				OutputStream os = con.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();
			}

			// Start the connection
			con.connect();

			// Read results from the query
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));

			while ((line = reader.readLine()) != null)
				sb.append(line);

			resultString = sb.toString();

			try {
				setResultObject(new JSONObject(resultString));
			} catch (JSONException e) {
				//Can't parse to JSONObject
			}

			reader.close();
		} catch (IOException e) {
			//Can't connect to the server
		} finally {
			if (con != null)
				con.disconnect();
		}
	}

	private String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	public JSONObject getResultObject() {
		return resultObject;
	}

	public void setResultObject(JSONObject resultObject) {
		this.resultObject = resultObject;
	}

}
