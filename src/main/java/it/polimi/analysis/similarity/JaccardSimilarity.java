package it.polimi.analysis.similarity;

import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.Venue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

public class JaccardSimilarity<T> {
	public double computeSimilarity(Set<T> a, Set<T> b) {
		Set<T> difference = new HashSet<T>(a);
		difference.removeAll(b);
		Set<T> intersection = new HashSet<T>(a);
		intersection.removeAll(difference);
		Set<T> union = new HashSet<T>(a);
		union.addAll(b);
		return ((double) intersection.size()) / union.size();
	}

	public static void main(String args[]) throws IOException {
		JaccardSimilarity<Author> similarity = new JaccardSimilarity<Author>();
		AuthorsAnalyzer authors = new MostCitedAuthors(.1f);

		Session session = HibernateSessionManager.getNewSession();
		@SuppressWarnings("unchecked")
		List<Venue> venues = (List<Venue>) session.getNamedQuery("findAllVenues").list();

		int step = 41;
		for (int year = 1970; year + step <= 2012; year++) {
			int initialYear = year, finalYear = year + step;
			StringBuilder builder = new StringBuilder();

			List<Venue> venuesToConsider = new ArrayList<Venue>();
			for (Venue v : venues) {
				if (!authors.getAuthors(v, initialYear, finalYear).isEmpty()) {
					venuesToConsider.add(v);
				}
			}
			if (venuesToConsider.size() < 2) {
				continue;
			}

			String filename = "./data/author_similarity-" + initialYear + "-" + finalYear;
			filename = filename + (venuesToConsider.size() == 2 ? ".csv" : "-mds.csv");
			FileWriter writer = new FileWriter(filename);
			for (int i = 0; i < venuesToConsider.size() - 1; i++) {
				builder.append(venuesToConsider.get(i) + ",");
			}
			builder.append(venuesToConsider.get(venuesToConsider.size() - 1) + "\n");

			for (Venue venue : venuesToConsider) {
				for (int i = 0; i < venuesToConsider.size() - 1; i++) {
					builder.append(similarity.computeSimilarity(authors.getAuthors(venue, initialYear, finalYear), authors.getAuthors(venuesToConsider.get(i), initialYear, finalYear)) + ",");
				}
				builder.append(similarity.computeSimilarity(authors.getAuthors(venue, initialYear, finalYear),
						authors.getAuthors(venuesToConsider.get(venuesToConsider.size() - 1), initialYear, finalYear))
						+ "\n");
			}

			writer.write(builder.toString());
			writer.close();
		}
	}
}
