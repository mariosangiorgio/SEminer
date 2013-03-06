package it.polimi.analysis.queries;

import it.polimi.analysis.analyzers.AffiliationProfile;
import it.polimi.analysis.queries.aggregates.AggregateAffiliation;
import it.polimi.analysis.queries.aggregates.Count;
import it.polimi.data.hibernate.entities.Affiliation;
import it.polimi.data.hibernate.entities.Article;

import java.util.List;

public class QueryAggregatingAffiliationProfiles implements SEMinerQuery<Count<Affiliation>>{

	private final SEMinerQuery<Article> basicQuery;

	public QueryAggregatingAffiliationProfiles(SEMinerQuery<Article> basicQuery) {
		this.basicQuery = basicQuery;
	}

	@Override
	public void setParameter(String parameter, Object value) {
		basicQuery.setParameter(parameter, value);
	}

	@Override
	public List<Count<Affiliation>> results() {
		AggregateAffiliation aggregateAffiliation = new AggregateAffiliation();
		for (Article article : basicQuery.results()) {
			aggregateAffiliation.addProfile(AffiliationProfile.fromArticle(article));
		}
		return aggregateAffiliation.toList();
	}
}
