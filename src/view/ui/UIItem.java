package view.ui;

import org.joml.Vector2d;

import view.MouseState;
import view.RenderedItem;


public abstract class UIItem extends RenderedItem {
	
	private boolean enabled = true;
	
	public abstract float width();
	public abstract float height();
	
	public void enable(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean hitTest(Vector2d pos) {
		boolean horzHit = position().x <= pos.x &&
				pos.x <= position().x + width() * scale();
		boolean vertHit = position().y <= pos.y &&
				pos.y <= position().y + height() * scale();
		return horzHit && vertHit;
	}
	
	public void onMouseEntered(MouseState curState)	{
	}
	
	public void onMouseExited(MouseState curState)	{
	}
	
	public void onMouseMoved(MouseState curState)	{
	}
	
	public void onMouseButtonDown(MouseState.Button button, MouseState curState) {
	}
	
	public void onMouseButtonUp(MouseState.Button button, MouseState curState) {
	}
}
