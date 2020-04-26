package view;

import java.util.Objects;

import org.joml.Vector2d;

public class MouseState {

	public static enum Button {
		Left,
		Right
	}
	
	public final Vector2d pos; 
	public final boolean leftButtonDown;
	public final boolean rightButtonDown;
	
	public MouseState(Vector2d pos, boolean leftDown, boolean rightDown) {
		this.pos = new Vector2d(pos);
		this.leftButtonDown = leftDown;
		this.rightButtonDown = rightDown;
	}
	
	public MouseState() {
		this(new Vector2d(Double.MIN_VALUE, Double.MIN_VALUE), false, true);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		MouseState otherState = (MouseState) other;
		return pos.equals(otherState.pos) &&
				leftButtonDown == otherState.leftButtonDown &&
				rightButtonDown == otherState.rightButtonDown;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos, leftButtonDown, rightButtonDown);
	}
	
}
