package com.rcggs.enable.data.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtil {

	public static String get(final String resource) {

		CloseableHttpClient client = HttpClients.createMinimal();
		HttpGet httpGet = new HttpGet(resource);
		httpGet.setHeader("Content-type", "application/json");
		String responseString = null;
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				"admin@trifacta.local", "admin");

		try {
			httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet,
					null));
			CloseableHttpResponse response = client.execute(httpGet);
			responseString = EntityUtils
					.toString(response.getEntity(), "UTF-8");
			//System.out.println(responseString);

		} catch (AuthenticationException | ParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return responseString;
	}

	public static String post(final String resource, final String body) {

		CloseableHttpClient client = HttpClients.createMinimal();
		HttpPost httpPost = new HttpPost(resource);
		httpPost.setHeader("Content-type", "application/json");

		try {
			httpPost.setEntity(new StringEntity(body));

			//System.err.println(body);

			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
					"admin@trifacta.local", "admin");
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost,
					null));

			CloseableHttpResponse response = client.execute(httpPost);

//			System.out.println(response.getEntity());
//			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity, "UTF-8");

		} catch (IOException | AuthenticationException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
