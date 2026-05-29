package robombs.game.gui;

import robombs.game.util.*;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

/**
 * A window component. A window can contain other components as a kind of master container and it can be dragged around with the mouse.
 * As all components in this "toolkit", the window doesn't render itself in a generic way. Its visuals are defined by a texture.
 */
public class Window extends GUIComponent {

    private Texture backDrop=null;
    private List<KeyState> keyEvents=new ArrayList<KeyState>();

    private int xDrag=-999;
    private int yDrag=-999;
    private String lowerRight=null;
    private GLFont font=TextBlitter.create(12);

    /**
     * Creates a new window.
     * @param backDrop the texture that defines how the window looks like.
     * @param x the x-position of the upper left corner.
     * @param y the y-position of the upper left corner.
     * @throws Exception
     */
    public Window(Texture backDrop, int x, int y) throws Exception {
        setX(x);
        setY(y);
        this.backDrop=backDrop;
    }
    
    public void setWindowTexture(Texture backDrop) {
    	this.backDrop=backDrop;
    }
    
    public void setText(String txt) {
    	lowerRight=txt;
    }

    public boolean evaluateInput(MouseMapper mouse, KeyMapper keyMapper) {
        if (visible) {
            boolean has = super.evaluateInput(mouse, keyMapper);
            if (!has) {
                boolean input = false;
                int xpos = getX();
                int ypos = getY();
                int y = mouse.getMouseY();
                int x = mouse.getMouseX();
                int scaledWidth = scaleValue(backDrop.getWidth());
                int titleBarHeight = scaleValue(15);

                if (x >= xpos && x <= scaledWidth + xpos && y >= ypos && y <= titleBarHeight + ypos || xDrag != -999) {
                    // title bar:
                    if (mouse.buttonDown(0)) {
                        if (xDrag == -999) {
                            xDrag = x;
                            yDrag = y;
                        }
                        xpos += x - xDrag;
                        ypos += y - yDrag;
                        setX(xpos);
                        setY(ypos);
                        xDrag = x;
                        yDrag = y;
                        input = true;
                    } else {
                        xDrag = -999;
                        yDrag = -999;
                    }
                } else {
                    xDrag = -999;
                    yDrag = -999;
                }
                if (!input) {
                	keyEvents.clear();
                	KeyState ks=null;
                    while ((ks=keyMapper.poll())!=KeyState.NONE) {
                        // consume all keyboard events, because nothing else happened!
                    	if (ks.getState()) {
                    		keyEvents.add(ks);
                    	}
                    }
                }
                return input;
            } else {
                return has;
            }
        }
        return false;
    }

    public boolean hasKeyEvent(int ke) {
    	for (KeyState ks:keyEvents) {
    		if (ks.getKeyCode()==ke) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public void draw(FrameBuffer buffer) {
        if (visible) {
        	int scaledWidth = scaleValue(backDrop.getWidth());
        	int scaledHeight = scaleValue(backDrop.getHeight());
            buffer.blit(backDrop, 0, 0, getX(), getY(), backDrop.getWidth(), backDrop.getHeight(), scaledWidth, scaledHeight, -1f, false, null);
            if (lowerRight!=null) {
            	int scaledFontSize = Math.max(12, scaleValue(12));
            	if (font.font.getSize() != scaledFontSize) {
            		font = GLFont.getGLFont(new Font("Arial", Font.BOLD, scaledFontSize));
            	}
            	int w=TextBlitter.getWidth(font, lowerRight);
            	TextBlitter.blitText(font, buffer, lowerRight, getX()+scaledWidth-w-scaleValue(15), getY()+scaledHeight-scaleValue(15), java.awt.Color.WHITE);
            }
            super.draw(buffer);
        }
    }
}
