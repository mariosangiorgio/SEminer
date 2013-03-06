package it.polimi.analysis.queries.aggregates;

public class Count<T> implements Comparable<Count<T>> {
	private final T content;
	private final Double count;

	public Count(T content, Double count) {
		this.content = content;
		this.count = count;
	}

	public T getContent() {
		return content;
	}

	public Double getCount() {
		return count;
	}

	@Override
	public int compareTo(Count<T> o) {
		return o.count.compareTo(this.count);
	}

	@Override
	public String toString() {
		return String.format("%s, %.2f", content, count);
	}
}
