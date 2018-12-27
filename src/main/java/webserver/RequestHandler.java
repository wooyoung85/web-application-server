package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	private String HostName;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			if (line == null) {
				return;
			}
			log.debug("request line : {}", line);

			// request Method, url, contentLenth, 로그인 여부 정보 추출
			String[] tokens = line.split(" ");
			String requestMethod = tokens[0].toString();
			String requestUrl = tokens[1].toString();
			int requestContentLength = 0;
			boolean requestCookieLogined = false;

			boolean res302 = false;
			boolean res302WithCookie = false;
			String redirectUrl = "";
			String cookieValue = "";
			
			boolean cssAcceptable = false;

			while (!line.equals("")) {
				line = br.readLine();

				if (line.indexOf("Host:") != -1) {
					HostName = line.substring(line.indexOf(":") + 1).trim();
				}

				if (line.indexOf("Content-Length:") != -1) {
					requestContentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
				}

				if (line.indexOf("Cookie:") != -1) {
					String cookies = line.substring(line.indexOf(":") + 1).trim();
					requestCookieLogined = Boolean.parseBoolean(HttpRequestUtils.parseCookies(cookies).get("logined"));
				}
				
				if (line.indexOf("Accept:") != -1) {
					String accept = line.substring(line.indexOf(":") + 1).trim().split(",")[0];
					if(accept.equals("text/css")) {
						cssAcceptable = true;
					}
				}

				log.debug("header : {}", line);
			}

			// QueryString 추출
			Map<String, String> queryStrings = getQueryString(br, requestMethod, requestUrl, requestContentLength);

			// User 로그인 처리
			if (requestUrl.equals("/user/login") && queryStrings != null) {
				User loginUser = findUser(queryStrings.get("userId"), queryStrings.get("password"));

				// 기존 로그인 정보가 있는 경우
				if (!requestCookieLogined) {
					// 기존 로그인 정보가 없는 경우
					if (loginUser != null) {
						res302WithCookie = true;
						cookieValue = "logined = true";
						redirectUrl = "/index.html";
					} else {
						res302WithCookie = false;
						cookieValue = "logined = false";
						redirectUrl = "/user/login_failed.html";
					}
				}
			}

			// User정보 저장
			if (requestUrl.equals("/user/create") && queryStrings != null) {
				setUser(queryStrings);
				res302 = true;
				redirectUrl = "/index.html";
			}

			DataOutputStream dos = new DataOutputStream(out);

			byte[] body;

			if (res302WithCookie) {
				response302HeaderWithCookie(dos, redirectUrl, cookieValue);
			}

			if (res302) {
				response302Header(dos, redirectUrl);
			} else {
				body = getResponseBodyContents(requestUrl);

				response200Header(dos, body.length, cssAcceptable);
				responseBody(dos, body);
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private Map<String, String> getQueryString(BufferedReader br, String requestMethod, String requestUrl,
			int contentLength) throws IOException {
		Map<String, String> queryStrings = null;

		if (requestMethod.equals("GET")) {
			int questionIndex = requestUrl.indexOf("?");
			if (questionIndex > 0) {
				queryStrings = HttpRequestUtils.parseQueryString(requestUrl.substring(questionIndex + 1));
			}
		} else if (requestMethod.equals("POST")) {
			String postContent = IOUtils.readData(br, contentLength);
			log.info("Post Content : {}", postContent);
			queryStrings = HttpRequestUtils.parseQueryString(postContent);
		}
		return queryStrings;
	}

	private User setUser(Map<String, String> queryStrings) {
		String userId, password, name, email;
		User user = new User("", "", "", "");

		userId = queryStrings.get("userId").toString();
		password = queryStrings.get("password").toString();
		name = queryStrings.get("name").toString();
		email = queryStrings.get("email").toString();
		user = new User(userId, password, name, email);
		log.info(user.toString());
		return user;
	}

	private User findUser(String userId, String password) {
		List<User> userList = User.getUserList();
		User findedUser = null;
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getUserId().equals(userId) && userList.get(i).getPassword().equals(password)) {
				findedUser = userList.get(i);
			}
		}
		return findedUser;
	}

	private byte[] getResponseBodyContents(String requestPage) throws IOException {
		byte[] resultBody = null;
		if (requestPage != "") {
			if(requestPage.equals("/user/list")) {
				List<String> templates = Files.readAllLines((new File("./webapp" + requestPage + ".html").toPath()));
				String template = String.join("", templates);
				
				StringBuilder sb = new StringBuilder();
				List<User> userList = User.getUserList();
				
				for(int i = 0; i < userList.size(); i++) {
					sb.append("<tr>");
					sb.append("<td> </td>");
					sb.append("<td>"+userList.get(i).getUserId()+"</td>");
					sb.append("<td>"+userList.get(i).getName()+"</td>");
					sb.append("<td>"+userList.get(i).getEmail()+"</td>");
					sb.append("</tr>");
				}				
				
				template = template.replace("##tbodyContents##", sb.toString());
				resultBody = template.getBytes();
			}
			else {
				resultBody = Files.readAllBytes(new File("./webapp" + requestPage).toPath());
			}
			
		} else {
			resultBody = "Hello World 자바 프로그래밍!".getBytes();
		}
		return resultBody;
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent, boolean cssAcceptable) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			if(cssAcceptable) {
				dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			}
			else {
				dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			}			
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos, String redirectUrl) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://" + HostName + redirectUrl + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302HeaderWithCookie(DataOutputStream dos, String redirectUrl, String cookieValue) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://" + HostName + redirectUrl + "\r\n");
			dos.writeBytes("Set-Cookie: " + cookieValue + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
