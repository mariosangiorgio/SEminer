package it.polimi.analysis.queries;

import java.util.List;

public interface SEMinerQuery<R> {
	void setParameter(String parameter, Object value);
	List<R> results();
}
