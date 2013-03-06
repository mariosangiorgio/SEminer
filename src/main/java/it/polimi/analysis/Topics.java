package it.polimi.analysis;

import it.polimi.analysis.analyzers.SlidingWindowAnalyzer;
import it.polimi.analysis.output.CSVOutputWriterFactory;
import it.polimi.analysis.output.OutputException;
import it.polimi.analysis.output.OutputWriterFactory;
import it.polimi.analysis.output.producers.CountCSVOutputProducer;
import it.polimi.analysis.queries.HibernateQuery;
import it.polimi.analysis.queries.QueryAggregatingTopics;
import it.polimi.analysis.queries.SEMinerQuery;
import it.polimi.analysis.queries.aggregates.Count;
import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.TopicProfile;
import it.polimi.data.hibernate.entities.Venue;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class Topics {

	public static void main(String[] args) throws OutputException {
		Session session = HibernateSessionManager.getNewSession();
		Query query = session.createQuery("select article.topicProfile from Article article where article.year between :firstYear and :lastYear");
		SEMinerQuery<Count<String>> seMinerQuery = new QueryAggregatingTopics(new HibernateQuery<TopicProfile>(query));

		OutputWriterFactory<Count<String>> outputWriterFactory = new CSVOutputWriterFactory<Count<String>>("output", new CountCSVOutputProducer<String>());
		SlidingWindowAnalyzer<Count<String>> analyzer = new SlidingWindowAnalyzer<Count<String>>("topics", session, seMinerQuery, 1975, 2013, 5, outputWriterFactory);
		analyzer.outputResults();

		@SuppressWarnings("unchecked")
		List<Venue> venues = session.getNamedQuery("findAllVenues").list();
		query = session.createQuery("select article.topicProfile from Article article where article.venue = :venue and article.year between :firstYear and :lastYear");
		seMinerQuery = new QueryAggregatingTopics(new HibernateQuery<TopicProfile>(query));
		for (Venue venue : venues) {
			seMinerQuery.setParameter("venue", venue);
			analyzer = new SlidingWindowAnalyzer<Count<String>>("topics - " + venue.getName(), session, seMinerQuery, 1975, 2013, 5, outputWriterFactory);
			analyzer.outputResults();
		}

	}

}
