package robombs.game.util;

import com.threed.jpct.*;
import java.awt.*;

/**
 * A class to blit text. Based on class written by CyberKilla.
 */
public class TextBlitter {

	private static GLFont font=null;
	
	static {
		Font f=new Font("Arial", Font.BOLD, 15);
		font=GLFont.getGLFont(f);
	}
	
	public static GLFont create(int size) {
		Font f=new Font("Arial", Font.BOLD, size);
		return GLFont.getGLFont(f);
	}

    public static int getWidth(String s) {
    	return font.getStringBounds(s).width;
    }
    
    public static void blitText(FrameBuffer buffer, String line, int x, int y) {
    	blitText(font, buffer, line, x, y, null);
    }
    
    public static void blitText(FrameBuffer buffer, String line, int x, int y, int maxX, int maxY) {
    	int clipWidth = maxX - x;
    	int clipHeight = maxY - y;
    	if (clipWidth <= 0 || clipHeight <= 0) {
    		return;
    	}

    	blitText(font, buffer, fitToWidth(font, line, clipWidth), x, y, null);
    }
    
    public static int getWidth(GLFont font, String s) {
    	return font.getStringBounds(s).width;
    }
    
    public static void blitText(GLFont font, FrameBuffer buffer, String line, int x, int y, Color col) {
    	if (line == null || line.isEmpty()) {
    		return;
    	}
		font.blitString(buffer, line, x, y + baselineOffset(font), -1, col != null ? col : Color.WHITE);
    }

    private static String fitToWidth(GLFont font, String line, int maxWidth) {
		if (line == null || line.isEmpty() || getWidth(font, line) <= maxWidth) {
			return line;
		}

		StringBuilder fitted = new StringBuilder(line.length());
		for (int i = 0; i < line.length(); i++) {
			fitted.append(line.charAt(i));
			if (getWidth(font, fitted.toString()) > maxWidth) {
				fitted.deleteCharAt(fitted.length() - 1);
				break;
			}
		}
		return fitted.toString();
    }

	private static int baselineOffset(GLFont font) {
		return (font.fontHeight * 2) / 3;
	}
}
