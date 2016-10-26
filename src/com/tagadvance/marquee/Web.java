package com.tagadvance.marquee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

public class Web {

	/**
	 * 8 kB default buffer size
	 */
	public static int DEFAULT_BUFFER_SIZE = 8192;

	public static String cURL(String spec) throws IOException {
		System.out.println("Connecting to: " + spec);
		URL url = new URL(spec);
		URLConnection con = url.openConnection();
		Reader in = new InputStreamReader(con.getInputStream());
		try {
			return readAll(in).toString();
		} finally {
			in.close();
		}
	}

	public static StringBuilder readAll(Reader in) throws IOException {
		return readAll(in, DEFAULT_BUFFER_SIZE);
	}

	public static StringBuilder readAll(Reader in, int size) throws IOException {
		StringBuilder sb = new StringBuilder();
		int read;
		char[] buf = new char[size];
		while ((read = in.read(buf)) != -1) {
			sb.append(buf, 0, read);
		}
		return sb;
	}

}
