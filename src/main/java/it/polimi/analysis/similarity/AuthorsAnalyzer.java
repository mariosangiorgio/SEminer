package it.polimi.analysis.similarity;

import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.Venue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

public class AuthorsAnalyzer {
	private final Map<MapKey, Set<Author>> cache = new HashMap<MapKey, Set<Author>>();

	private final Session session = HibernateSessionManager.getNewSession();
	private final String totalPublishedPapersQuery;
	private final String publishedPapersByAuthorQuery;
	private final float ratio;

	public AuthorsAnalyzer(float ratio, String totalPublishedPapersQuery, String publishedPapersByAuthorQuery) {
		this.ratio = ratio;
		this.totalPublishedPapersQuery = totalPublishedPapersQuery;
		this.publishedPapersByAuthorQuery = publishedPapersByAuthorQuery;
	}

	public Set<Author> getAuthors(Venue venue, int initialYear, int finalYear) {
		MapKey key = new MapKey(venue, initialYear, finalYear);
		if (!cache.containsKey(key)) {
			Set<Author> authors = new HashSet<Author>();

			long totalPublishedPapers = (Long) session.createQuery(totalPublishedPapersQuery).setParameter("venue", venue).setParameter("initialYear", initialYear).setParameter("lastYear", finalYear)
					.uniqueResult();
			long processedPublishedPapers = 0;
			long threshold = (long) (totalPublishedPapers * ratio);

			@SuppressWarnings("unchecked")
			List<Object[]> result = (List<Object[]>) session.createQuery(publishedPapersByAuthorQuery).setParameter("initialYear", initialYear).setParameter("lastYear", finalYear)
					.setParameter("venue", venue).list();

			int index = 0;
			while (processedPublishedPapers < threshold && index < result.size()) {
				Object[] element = result.get(index++);
				Author author = (Author) element[0];
				long publishedPapers = (Long) element[1];
				processedPublishedPapers += publishedPapers;
				authors.add(author);
			}
			cache.put(key, authors);
		}
		return cache.get(key);
	}
}

class MapKey {
	private Venue venue;
	private int initialYear, finalYear;

	MapKey(Venue venue, int initialYear, int finalYear) {
		this.venue = venue;
		this.initialYear = initialYear;
		this.finalYear = finalYear;
	}

	@Override
	public int hashCode() {
		return venue.hashCode() + initialYear + finalYear;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MapKey) {
			MapKey other = (MapKey) o;
			return venue.equals(other.venue) && initialYear == other.initialYear && finalYear == other.finalYear;
		}
		return false;
	}

}
