package types;


// Helper type to return two values from a method.
public class Pair<T, S> {
	public final T a;
	public final S b;

	public Pair(T a, S b) {
		this.a = a;
		this.b = b;
	}
}
