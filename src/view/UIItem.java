package view;

import org.joml.Vector2d;

public abstract class UIItem extends RenderedItem {
	
	public abstract float width();
	public abstract float height();
	
	public boolean hitTest(Vector2d pos) {
		boolean horzHit = position().x <= pos.x &&
				pos.x <= position().x + width() * scale();
		boolean vertHit = position().y <= pos.y &&
				pos.y <= position().y + height() * scale();
		return horzHit && vertHit;
	}
	
	public void onMouseDown(Vector2d pos)
	{
		// Do nothing by default.
	}
}
