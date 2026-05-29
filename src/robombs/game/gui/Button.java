package robombs.game.gui;

import robombs.game.util.*;

import java.awt.*;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

public class Button extends GUIComponent{
       private String label="";
       private int xp = 0;
       private int yp = 0;
       private int xs = 0;
       private int ys = 0;
       private GUIListener bl=null;
       private boolean clicked=false;
       private boolean hideLabel=false;
       private GLFont font=null;

       public Button(int xpos, int ypos, int xdim, int ydim) {
          this.xp=xpos;
          this.yp=ypos;
          this.xs=xdim;
          this.ys=ydim;
      }

      public void setHideLabel(boolean hide) {
          hideLabel=hide;
      }

      public void setLabel(String label) {
          this.label=label;
      }

      public void setListener(GUIListener bl) {
          this.bl=bl;
      }

      public boolean evaluateInput(MouseMapper mouse, KeyMapper keyMapper) {
          boolean has=super.evaluateInput(mouse, keyMapper);
          if (!has && isVisible()) {
              int xpos = getParentX();
              int ypos = getParentY();

              boolean input = false;
              int x = mouse.getMouseX() - xpos;
              int y = mouse.getMouseY() - ypos;
              int scaledX = scaleValue(xp);
              int scaledY = scaleValue(yp);
              int scaledWidth = scaleValue(xs);
              int scaledHeight = scaleValue(ys);
              if (mouse.buttonDown(0)) {
                  if (x >= scaledX && x <= scaledX + scaledWidth && y >= scaledY && y <= scaledY + scaledHeight) {
                      if (!clicked) {
                          if (bl != null) {
                              bl.elementChanged(label, null);
                          }
                      }
                      clicked = true;
                  } 
              } else {
                  clicked = false;
              }
              return input;
          } else {
              return has;
          }
      }

      public void draw(FrameBuffer buffer) {
          if (visible) {
              if (!hideLabel) {
                  if (font == null || font.font.getSize() != Math.max(15, scaleValue(15))) {
                      font = GLFont.getGLFont(new Font("Arial", Font.BOLD, Math.max(15, scaleValue(15))));
                  }
                  int scaledWidth = scaleValue(xs);
                  int xc = scaledWidth / 2 - (TextBlitter.getWidth(font, label) / 2) + scaleValue(xp);
                  TextBlitter.blitText(font, buffer, label, getParentX() + xc, getParentY() + scaleValue(yp), null);
              }
              super.draw(buffer);
          }
      }
   }
