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
    	Graphics graphics = buffer.getGraphics();
    	if (graphics == null) {
    		return;
    	}

    	try {
    		int clipWidth = maxX - x;
    		int clipHeight = maxY - y;
    		if (clipWidth <= 0 || clipHeight <= 0) {
    			return;
    		}

    		Shape oldClip = graphics.getClip();
    		graphics.setClip(x, y, clipWidth, clipHeight);
    		blitText(font, graphics, line, x, y, null);
    		graphics.setClip(oldClip);
		} finally {
    		graphics.dispose();
    	}
    }
    
    public static int getWidth(GLFont font, String s) {
    	return font.getStringBounds(s).width;
    }
    
    public static void blitText(GLFont font, FrameBuffer buffer, String line, int x, int y, Color col) {
    	Graphics graphics = buffer.getGraphics();
    	if (graphics == null) {
    		return;
    	}
		try {
			blitText(font, graphics, line, x, y, col);
		} finally {
			graphics.dispose();
		}
    }

    private static void blitText(GLFont font, Graphics graphics, String line, int x, int y, Color col) {
		Color oldColor = graphics.getColor();
		Font oldFont = graphics.getFont();
		try {
			graphics.setFont(font.font);
			graphics.setColor(col != null ? col : Color.WHITE);
			int offset = baselineOffset(font);
			graphics.drawString(line, x, y + offset);
		} finally {
			graphics.setColor(oldColor);
			graphics.setFont(oldFont);
		}
    }

	private static int baselineOffset(GLFont font) {
		return (font.fontHeight * 2) / 3;
	}
}
