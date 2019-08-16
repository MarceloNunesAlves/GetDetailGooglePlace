package br.com.marcelonunesalves.GetDetailGooglePlace;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		HttpClient httpClient = new DefaultHttpClient();
		try {
			// this twitter call returns json results.
			// see this page for more info: https://dev.twitter.com/docs/using-search
			// http://search.twitter.com/search.json?q=%40apple

			// Example URL 1: this yahoo weather call returns results as an rss (xml) feed
			// HttpGet httpGetRequest = new
			// HttpGet("http://weather.yahooapis.com/forecastrss?p=80020&u=f");

			// Example URL 2: this twitter api call returns results in a JSON format
			HttpGet httpGetRequest = new HttpGet("https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJ7yWJXc5ZzpQRxLROjU3O7cs&fields=name,formatted_address,international_phone_number,website,rating,review&key=");

			// Execute HTTP request
			HttpResponse httpResponse = httpClient.execute(httpGetRequest);

			System.out.println("----------------------------------------");
			System.out.println(httpResponse.getStatusLine());
			System.out.println("----------------------------------------");

			// Get hold of the response entity
			HttpEntity entity = httpResponse.getEntity();

			// If the response does not enclose an entity, there is no need
			// to bother about connection release
			byte[] buffer = new byte[1024];
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				try {
					int bytesRead = 0;
					BufferedInputStream bis = new BufferedInputStream(inputStream);
					while ((bytesRead = bis.read(buffer)) != -1) {
						String chunk = new String(buffer, 0, bytesRead);
						
						JsonFactory factory = new JsonFactory();
		                //factory.enable(JsonParser.Feature.IGNORE_UNDEFINED);
		                //factory.enable(JsonParser.Feature.ALLOW_MISSING_VALUES);
		                ObjectMapper objectMapper = new ObjectMapper(factory);
		                
						JsonNode json = objectMapper.readTree(chunk);
						System.out.println(json.get("result"));
					}
				} catch (IOException ioException) {
					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					ioException.printStackTrace();
				} catch (RuntimeException runtimeException) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection immediately.
					httpGetRequest.abort();
					runtimeException.printStackTrace();
				} finally {
					// Closing the input stream will trigger connection release
					try {
						inputStream.close();
					} catch (Exception ignore) {
					}
				}
			}
		} catch (ClientProtocolException e) {
			// thrown by httpClient.execute(httpGetRequest)
			e.printStackTrace();
		} catch (IOException e) {
			// thrown by entity.getContent();
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpClient.getConnectionManager().shutdown();
		}
	}
}
