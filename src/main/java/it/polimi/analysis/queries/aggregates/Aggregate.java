package it.polimi.analysis.queries.aggregates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Aggregate<T>{
	public Map<T, Double> counter = new HashMap<T, Double>();

	public void addProfile(Profile<T> profile) {
		for (Entry<T, Double> entry : profile.getDistribution().entrySet()) {
			if (counter.containsKey(entry.getKey())) {
				counter.put(entry.getKey(), entry.getValue() + counter.get(entry.getKey()));
			} else {
				counter.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public List<Count<T>> toList() {
		List<Count<T>> result = new ArrayList<Count<T>>();
		for (Entry<T, Double> entry : counter.entrySet()) {
			result.add(new Count<T>(entry.getKey(), entry.getValue()));
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<T, Double> entry : counter.entrySet()) {
			builder.append(String.format("%.2f %s\n", entry.getValue(), entry.getKey()));
		}
		return builder.toString();
	}
}
