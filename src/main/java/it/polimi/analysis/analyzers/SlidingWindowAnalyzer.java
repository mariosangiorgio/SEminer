package it.polimi.analysis.analyzers;

import it.polimi.analysis.output.OutputException;
import it.polimi.analysis.output.OutputWriter;
import it.polimi.analysis.output.OutputWriterFactory;
import it.polimi.analysis.queries.SEMinerQuery;

import org.hibernate.Session;

public class SlidingWindowAnalyzer<R> implements IAnalyzer<R> {
	private final SEMinerQuery<R> baseQuery;
	private final int firstYear;
	private final int lastYear;
	private final int step;
	private final String analysisName;
	private final OutputWriterFactory<R> outputWriterFactory;
	private final Session session;

	public SlidingWindowAnalyzer(String analysisName, Session session, SEMinerQuery<R> baseQuery, int firstYear, int lastYear, int step, OutputWriterFactory<R> outputWriterFactory) {
		this.session = session;
		this.baseQuery = baseQuery;
		this.firstYear = firstYear;
		this.lastYear = lastYear;
		this.step = step;
		this.analysisName = analysisName;
		this.outputWriterFactory = outputWriterFactory;
	}

	public void outputResults() throws OutputException {
		for (int i = firstYear; i + step <= lastYear; i++) {
			OutputWriter<R> outputWriter = outputWriterFactory.getNewWriter(analysisName + "-" + i + "to" + (i + step));
			Analyzer<R> analyzer = new Analyzer<R>(session, baseQuery, outputWriter);
			analyzer.bindValue("firstYear", i);
			analyzer.bindValue("lastYear", i + step);
			analyzer.outputResults();
		}
	}
}
