package org.miktmc.filters;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

public class MockHttpUrlStreamHandler extends URLStreamHandler {

	private Map<URL, URLConnection> connections = new HashMap<>();

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return connections.get(url);
	}

	public void resetConnections() {
		connections = new HashMap<>();
	}

	public MockHttpUrlStreamHandler addConnection(URL url, URLConnection urlConnection) {
		connections.put(url, urlConnection);
		return this;
	}

}
