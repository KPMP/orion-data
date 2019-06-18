package org.kpmp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

public class JWTHandlerTest {

	private JWTHandler jwtHandler;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		jwtHandler = new JWTHandler(logger);
	}

	@After
	public void tearDown() throws Exception {
		jwtHandler = null;
	}

	@Test
	public void testGetUserIdFromToken() throws Exception {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb3NlbWlAdW1pY2guZWR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQ.PVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q";

		String user = jwtHandler.getUserIdFromToken(token);

		assertEquals("rosemi@umich.edu", user);
	}

	@Test
	public void testGetUserIdFromToken_whenTokenMalformed() throws Exception {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9eyJzdWIiOiJyb3NlbWlAdW1pY2guZWR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQPVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q";

		String user = jwtHandler.getUserIdFromToken(token);

		assertEquals("", user);
		verify(logger).logWarnMessage(JWTHandler.class, null, null, "JWTHandler.getUserIdFromToken",
				"Unable to get UserID from JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9eyJzdWIiOiJyb3NlbWlAdW1pY2guZWR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQPVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q");
	}

	@Test
	public void testGetUserIdFromToken_whenTokenNotBase64() throws Exception {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiJ9.eyJzdWIiOiJyb3NlbWlAdW1pY2guFFFR1IiwiZXhwIjoxNTU5OTQ2MTE5LCJ1c2VyIjoie1wiZmlyc3ROYW1lXCI6XCJNaWNoYWVsXCIsXCJsYXN0TmFtZVwiOlwiUm9zZVwiLFwiZGlzcGxheU5hbWVcIjpcIk1pY2hhZWwgUm9zZVwiLFwiZW1haWxcIjpcInJvc2VtaUB1bWljaC5lZHVcIn0ifQ.PVhgjr36JX9jk7NocOhpchVkr3iImzfhJRsOIhJmO0OoLTTJMPHNI6mWKCUWP9ecDMHboc-U00BVt6YlG26-3Q";

		String user = jwtHandler.getUserIdFromToken(token);

		assertEquals("", user);
		verify(logger).logErrorMessage(JWTHandler.class, null, null, "JWTHandler.getUserIdFromToken",
				"Unable to get UserID from token: Illegal unquoted character ((CTRL-CHAR, code 20)): has to be escaped using backslash to be included in string value\n"
						+ " at [Source: (String)\"{\"sub\":\"rosemi@umich.QQԈ����������������䰉�͕Ȉ��p������9���p��p�5������p��p�����9���p��p�I�͕p��p��������9���p��p�5�������I�͕p��p������p��p�ɽ͕��յ�������p���\"; line: 1, column: 23]");
	}

	@Test
	public void testGetUserIdFromToken_whenTokenIsNull() throws Exception {

		String user = jwtHandler.getUserIdFromToken(null);

		assertEquals("", user);
		verify(logger).logWarnMessage(JWTHandler.class, null, null, "JWTHandler.getUserIdFromToken",
				"Unable to get UserID from JWT null");
	}

	@Test
	public void testGetJWTFromHeader() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");

		String token = jwtHandler.getJWTFromHeader(request);

		assertEquals("token", token);
	}

	@Test
	public void testGetJWTFromHeader_whenAuthHeaderMalformed() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("BEARS token");

		String token = jwtHandler.getJWTFromHeader(request);

		assertEquals(null, token);
		verify(logger).logWarnMessage(JWTHandler.class, null, null, "JWTHandler.getJWTFromHeader",
				"Authorization Header either missing or malformed");
	}

	@Test
	public void testGetJWTFromHeader_whenNoAuthHeader() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);

		String token = jwtHandler.getJWTFromHeader(request);

		assertEquals(null, token);
		verify(logger).logWarnMessage(JWTHandler.class, null, null, "JWTHandler.getJWTFromHeader",
				"Authorization Header either missing or malformed");
	}

}
