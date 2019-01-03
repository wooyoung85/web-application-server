package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import http.HttpMethod;
import http.HttpRequest;
import org.junit.Test;

public class HttpRequestTest {

	private String testDirectory = "./src/test/resources/";
	
	@Test
	public void request_GET() throws FileNotFoundException {
		InputStream is = new FileInputStream(new File(testDirectory +"Http_GET.txt"));
		HttpRequest request = new HttpRequest(is);
		
		assertEquals(HttpMethod.GET, request.getMethod());
		assertEquals("/user/create", request.getUrl());
		assertEquals("keep-alive", request.getHeaderField("Connection"));
		assertEquals("wooyoung85", request.getParameter("userId"));
	}
	
	@Test
	public void request_POST() throws FileNotFoundException {
		InputStream is = new FileInputStream(new File(testDirectory +"Http_POST.txt"));
		HttpRequest request = new HttpRequest(is);
		
		assertEquals(HttpMethod.POST, request.getMethod());
		assertEquals("/user/create", request.getUrl());
		assertEquals("keep-alive", request.getHeaderField("Connection"));
		assertEquals("wooyoung85", request.getParameter("userId"));
		assertEquals("123", request.getParameter("password"));
		assertEquals("wooyoung85@test.com", request.getParameter("email"));
	}
}
