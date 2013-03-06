package it.polimi.analysis;

import it.polimi.analysis.analyzers.Analyzer;
import it.polimi.analysis.output.CSVOutputWriterFactory;
import it.polimi.analysis.output.OutputException;
import it.polimi.analysis.output.OutputWriter;
import it.polimi.analysis.output.OutputWriterFactory;
import it.polimi.analysis.output.producers.CountCSVOutputProducer;
import it.polimi.analysis.queries.HibernateQuery;
import it.polimi.analysis.queries.QueryAggregatingAffiliationProfiles;
import it.polimi.analysis.queries.SEMinerQuery;
import it.polimi.analysis.queries.aggregates.Count;
import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Affiliation;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Venue;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class Affiliations {
	public static void main(String[] args) throws OutputException {
		Session session = HibernateSessionManager.getNewSession();
		Query query = session.createQuery("select article from Article article");
		SEMinerQuery<Count<Affiliation>> seMinerQuery = new QueryAggregatingAffiliationProfiles(new HibernateQuery<Article>(query));

		OutputWriterFactory<Count<Affiliation>> outputWriterFactory = new CSVOutputWriterFactory<Count<Affiliation>>("output", new CountCSVOutputProducer<Affiliation>());
		OutputWriter<Count<Affiliation>> outputWriter = outputWriterFactory.getNewWriter("affiliations");
		Analyzer<Count<Affiliation>> analyzer = new Analyzer<Count<Affiliation>>(session, seMinerQuery, outputWriter);
		analyzer.outputResults();

		@SuppressWarnings("unchecked")
		List<Venue> venues = session.getNamedQuery("findAllVenues").list();
		query = session.createQuery("select article from Article article where venue = :venue");
		seMinerQuery = new QueryAggregatingAffiliationProfiles(new HibernateQuery<Article>(query));
		for (Venue venue : venues) {
			seMinerQuery.setParameter("venue", venue);
			outputWriter = outputWriterFactory.getNewWriter("affiliations - " + venue.getName());
			analyzer = new Analyzer<Count<Affiliation>>(session, seMinerQuery, outputWriter);
			analyzer.outputResults();
		}
	}
}
