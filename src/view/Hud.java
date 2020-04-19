//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector4f;

import app.Util;

public class Hud {
	
	///////////////
	
	public static interface UIEventHandler {
		public void onReset();
	}
	
	///////////////

	private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";
    private final List<UIItem> items = new ArrayList<UIItem>();
    private final TextItem seedLabel;
    private final ImageButtonItem resetButton;
    private final UIEventHandler callbacks;
    private Shader shader;
    private MouseState lastMouseState = new MouseState();
    private UIItem lastTouchedItem;

    public Hud(String seedText, UIEventHandler callbacks) throws Exception {
    	this.callbacks = callbacks;
        this.seedLabel = new TextItem(seedText, new FontTexture(FONT, CHARSET));
        this.seedLabel.material().setAmbientColor(new Vector4f(0.8f, 0.8f, 0.8f, 1));
        this.items.add(this.seedLabel);
        
        this.resetButton = new ImageButtonItem(
        		new ImageButtonItem.Textures(
        				new Texture("res/textures/reset_normal.png"),
        				new Texture("res/textures/reset_focused.png"),
        				new Texture("res/textures/reset_pressed.png"),
        				new Texture("res/textures/reset_disabled.png")),
        		() -> this.callbacks.onReset());
        this.items.add(this.resetButton);

        this.shader = makeHudShader();
    }
	
	private static Shader makeHudShader() throws Exception {
	    Shader shader = new Shader();
	    shader.createVertexShader(Util.loadResource("/view/HudVertex.vs"));
	    shader.createFragmentShader(Util.loadResource("/view/HudFragment.fs"));
	    shader.link();
        shader.createUniform("projModelMatrix");
        shader.createUniform("color");
        return shader;
	}

    public void setStatusText(String statusText) {
        this.seedLabel.setText(statusText);
    }

    public List<UIItem> items() {
        return items;
    }
    
    public void resize(Window window) {
        seedLabel.setPosition(10f, 20f, 0);
        resetButton.setPosition(seedLabel.width() + 10f, 15f, 0);
    }
    
    public void render(Matrix4f projMat) {
        shader.bind();
        
        for (RenderedItem item : items) {
            shader.setUniform("projModelMatrix", item.concatTransformation(projMat));
            shader.setUniform("color", item.material().ambientColor());
            item.render();
        }

        shader.unbind();
    }

    public void cleanup() {
		if (shader != null)
			shader.cleanup();
		for (UIItem item : items)
			item.cleanup();
    }
    
    public void processMouse(MouseState curState) {
    	if (curState.equals(lastMouseState))
    		return;
    	
    	boolean posChanged = !curState.pos.equals(lastMouseState.pos);
    	boolean leftButtonChanged =
    			curState.leftButtonDown != lastMouseState.leftButtonDown;
    	boolean rightButtonChanged =
    			curState.rightButtonDown != lastMouseState.rightButtonDown;
    	
    	UIItem curItem = findHitItem(curState.pos);
    	
    	if (curItem != lastTouchedItem) {
        	if (lastTouchedItem != null)
    			lastTouchedItem.onMouseExited(curState);
    		if (curItem != null)
    			curItem.onMouseEntered(curState);
    	}

    	if (curItem != null) {
    		if (posChanged)
    			curItem.onMouseMoved(curState);
    		if (leftButtonChanged) {
    			if (curState.leftButtonDown)
    				curItem.onMouseButtonDown(MouseState.Button.Left, curState);
    			else
    				curItem.onMouseButtonUp(MouseState.Button.Left, curState);
    		}
    		if (rightButtonChanged) {
    			if (curState.rightButtonDown)
    				curItem.onMouseButtonDown(MouseState.Button.Right, curState);
    			else
    				curItem.onMouseButtonUp(MouseState.Button.Right, curState);
    		}
    	}
    	
    	lastMouseState = curState;
    	lastTouchedItem = curItem;
    }
    
    private UIItem findHitItem(Vector2d mousePos) {
    	for (UIItem item : items) {
    		if (item.hitTest(mousePos))
    			return item;
    	}
    	return null;
    }
}