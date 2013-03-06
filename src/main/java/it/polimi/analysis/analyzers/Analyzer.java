package it.polimi.analysis.analyzers;

import it.polimi.analysis.output.OutputException;
import it.polimi.analysis.output.OutputWriter;
import it.polimi.analysis.queries.SEMinerQuery;

import java.util.List;

import org.hibernate.Session;

public class Analyzer<R> implements IAnalyzer<R> {
	private final Session session;
	private final SEMinerQuery<R> query;
	private final OutputWriter<R> outputWriter;

	public Analyzer(Session session, SEMinerQuery<R> query, OutputWriter<R> outputWriter) {
		this.session = session;
		this.query = query;
		this.outputWriter = outputWriter;
	}

	protected Session getSession() {
		return session;
	}
	
	public void bindValue(String parameter, Object value) {
		query.setParameter(parameter, value);
	}

	public void outputResults() throws OutputException {
		List<R> result = query.results();
		for (R item : result) {
			outputWriter.writeLine(item);
		}
		outputWriter.close();
	}
}
