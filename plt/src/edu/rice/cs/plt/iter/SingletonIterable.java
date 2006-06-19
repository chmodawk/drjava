package edu.rice.cs.plt.iter;

/** An iterable wrapping a single value */
public class SingletonIterable<T> extends AbstractIterable<T> implements SizedIterable<T> {
  
  private final T _element;
  
  public SingletonIterable(T element) { _element = element; }
  public SingletonIterator<T> iterator() { return new SingletonIterator<T>(_element); }
  public int size() { return 1; }
  public boolean isFixed() { return true; }
  
  /** Call the constructor (allows {@code T} to be inferred) */
  public static <T> SingletonIterable<T> make(T element) { 
    return new SingletonIterable<T>(element);
  }
  
}