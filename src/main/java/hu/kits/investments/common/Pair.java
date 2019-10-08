package hu.kits.investments.common;

import java.util.Objects;

public class Pair<S, T> {

	public final S value1;
	
	public final T value2;

	public Pair(S value1, T value2) {
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public S value1() {
	    return value1;
	}
	
	public T value2() {
        return value2;
    }
	
	@Override
    public String toString() {
        return "(" + value1 + ", " + value2 + ")";
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(other == null || !(other instanceof Pair)) return false;
        Pair<S,T> otherPair = (Pair<S,T>)other;
        return otherPair.value1.equals(value1) && otherPair.value2.equals(value2);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
    }
	
}
