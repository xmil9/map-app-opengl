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
import ui.Window;

public class Hud {
	
	///////////////
	
	public static interface EventHandler {
		public void onReset();
	}
	
	///////////////

	private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";
    private final List<UIItem> items = new ArrayList<UIItem>();
    private final TextItem seedLabel;
    private final ImageButtonItem resetButton;
    private final EventHandler callbacks;
    private Shader shader;

    public Hud(String seedText, EventHandler callbacks) throws Exception {
    	this.callbacks = callbacks;
        this.seedLabel = new TextItem(seedText, new FontTexture(FONT, CHARSET));
        this.seedLabel.material().setAmbientColor(new Vector4f(0.8f, 0.8f, 0.8f, 1));
        this.items.add(this.seedLabel);
        
        this.resetButton = new ImageButtonItem(
        		new Texture("res/textures/reset.png"),
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
    
    public void onMouseDown(Vector2d mousePos) {
    	for (UIItem item : items) {
    		if (item.hitTest(mousePos)) {
    			item.onMouseDown(mousePos);
    			break;
    		}
    	}
    }
}