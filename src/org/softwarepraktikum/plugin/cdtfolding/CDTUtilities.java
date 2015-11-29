package org.softwarepraktikum.plugin.cdtfolding;

import org.eclipse.swt.graphics.RGB;

public class CDTUtilities {
	
	static RGB restoreRGB (String rgbEncoding) {
		String[] values = rgbEncoding.split(" ");
		
		if (values.length < 3 || values[0].equals("") || values[1].equals("") || values[2].equals("")) {
			return defaultRGB();
		}
		
		return new RGB(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
	}
	
	private static RGB defaultRGB() {
		return new RGB(255, 255, 255);
	}
}
