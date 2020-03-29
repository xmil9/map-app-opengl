package view;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import ui.Window;

public class Hud {
    private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";
    private final List<RenderedItem> items = new ArrayList<RenderedItem>();
    private final TextItem statusText;

    public Hud(String statusText) throws Exception {
        this.statusText = new TextItem(statusText, new FontTexture(FONT, CHARSET));
        this.statusText.material().setAmbientColor(new Vector4f(0, 0, 0, 1));
        items.add(this.statusText);
    }

    public void setStatusText(String statusText) {
        this.statusText.setText(statusText);
    }

    public List<RenderedItem> items() {
        return items;
    }
    
    public void resize(Window window) {
        statusText.setPosition(10f, window.height() - 50f, 0);
    }
    
    public void render(Shader shader, Matrix4f projMat) {
        shader.bind();
        
        for (RenderedItem item : items) {
            shader.setUniform("projModelMatrix", item.concatTransformation(projMat));
            shader.setUniform("color", item.material().ambientColor());
            item.render();
        }

        shader.unbind();
    }

    public void cleanup() {
		for (RenderedItem item : items)
			item.cleanup();
   }
}