package by.deniotokiari.core.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
