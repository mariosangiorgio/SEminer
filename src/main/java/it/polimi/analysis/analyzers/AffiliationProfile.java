package it.polimi.analysis.analyzers;

import it.polimi.analysis.queries.aggregates.Profile;
import it.polimi.data.hibernate.entities.Affiliation;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AffiliationProfile implements Profile<Affiliation> {
	private final Map<Affiliation, Double> affiliationProfile = new HashMap<Affiliation, Double>();

	private AffiliationProfile(Map<Affiliation, Integer> affiliationCounts, int authors) {
		for (Entry<Affiliation, Integer> entry : affiliationCounts.entrySet()) {
			affiliationProfile.put(entry.getKey(), entry.getValue() / (double) authors);
		}
	}

	public static AffiliationProfile fromArticle(Article article) {
		Map<Affiliation, Integer> affiliationCounts = new HashMap<Affiliation, Integer>();
		for (Author author : article.getAuthors()) {
			if (author.getAffiliation() != null) {
				Affiliation affiliation = author.getAffiliation();
				if (affiliationCounts.containsKey(affiliation)) {
					affiliationCounts.put(affiliation, affiliationCounts.get(affiliation) + 1);
				} else {
					affiliationCounts.put(affiliation, 1);
				}
			}
		}
		return new AffiliationProfile(affiliationCounts, article.numberOfAuthors());
	}

	public Map<Affiliation, Double> getDistribution() {
		return affiliationProfile;
	}
}