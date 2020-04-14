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
import org.joml.Vector4f;

import app.Util;
import ui.Window;

public class Hud {
    private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";
    private final List<RenderedItem> items = new ArrayList<RenderedItem>();
    private final TextItem statusText;
    private Shader shader;

    public Hud(String statusText) throws Exception {
        this.statusText = new TextItem(statusText, new FontTexture(FONT, CHARSET));
        this.statusText.material().setAmbientColor(new Vector4f(0.8f, 0.8f, 0.8f, 1));
        this.items.add(this.statusText);
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
        this.statusText.setText(statusText);
    }

    public List<RenderedItem> items() {
        return items;
    }
    
    public void resize(Window window) {
        statusText.setPosition(10f, 20f, 0);
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
		for (RenderedItem item : items)
			item.cleanup();
   }
}