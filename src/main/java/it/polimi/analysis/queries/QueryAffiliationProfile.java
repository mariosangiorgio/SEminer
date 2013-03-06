package it.polimi.analysis.queries;

import it.polimi.analysis.analyzers.AffiliationProfile;
import it.polimi.data.hibernate.entities.Article;

import java.util.ArrayList;
import java.util.List;

public class QueryAffiliationProfile implements SEMinerQuery<AffiliationProfile> {
	private final HibernateQuery<Article> articleQuery;

	public QueryAffiliationProfile(HibernateQuery<Article> articleQuery) {
		this.articleQuery = articleQuery;
	}

	@Override
	public void setParameter(String parameter, Object value) {
		this.articleQuery.setParameter(parameter, value);
	}

	@Override
	public List<AffiliationProfile> results() {
		List<AffiliationProfile> affiliationProfiles = new ArrayList<AffiliationProfile>();
		for (Article article : articleQuery.results()) {
			affiliationProfiles.add(AffiliationProfile.fromArticle(article));
		}
		return affiliationProfiles;
	}
}
