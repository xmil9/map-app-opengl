package types;

//Helper type to return three values from a method.
public class Triple<T, S, R> {
	public final T a;
	public final S b;
	public final R c;

	public Triple(T a, S b, R c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
}
