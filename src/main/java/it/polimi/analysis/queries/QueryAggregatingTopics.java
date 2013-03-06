package it.polimi.analysis.queries;

import it.polimi.analysis.queries.aggregates.AggregateTopic;
import it.polimi.analysis.queries.aggregates.Count;
import it.polimi.data.hibernate.entities.TopicProfile;

import java.util.List;

public class QueryAggregatingTopics implements SEMinerQuery<Count<String>> {
	private final SEMinerQuery<TopicProfile> basicQuery;

	public QueryAggregatingTopics(SEMinerQuery<TopicProfile> basicQuery) {
		this.basicQuery = basicQuery;
	}

	@Override
	public void setParameter(String parameter, Object value) {
		basicQuery.setParameter(parameter, value);
	}

	@Override
	public List<Count<String>> results() {
		List<TopicProfile> basicQueryResult = basicQuery.results();
		AggregateTopic aggregateTopic = new AggregateTopic();
		for (TopicProfile result : basicQueryResult) {
			aggregateTopic.addProfile(result);
		}
		return aggregateTopic.toList();
	}

}
