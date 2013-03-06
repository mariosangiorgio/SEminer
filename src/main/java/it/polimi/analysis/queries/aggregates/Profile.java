package it.polimi.analysis.queries.aggregates;

import java.util.Map;

public interface Profile<T> {
	Map<T, Double> getDistribution();
}
