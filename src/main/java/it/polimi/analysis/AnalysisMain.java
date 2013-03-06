package it.polimi.analysis;

import it.polimi.analysis.analyzers.IAnalyzer;
import it.polimi.analysis.analyzers.SlidingWindowAnalyzer;
import it.polimi.analysis.output.CSVOutputWriterFactory;
import it.polimi.analysis.output.OutputException;
import it.polimi.analysis.output.OutputWriterFactory;
import it.polimi.analysis.output.producers.ObjectArrayCSVOutputProducer;
import it.polimi.analysis.queries.HibernateQuery;
import it.polimi.analysis.queries.SEMinerQuery;
import it.polimi.data.hibernate.HibernateSessionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.hibernate.Query;
import org.hibernate.Session;

public class AnalysisMain {
	public static void main(String[] args) throws OutputException, IOException {
		String fileName = args[0];
		int initialYear = Integer.parseInt(args[1]);
		int finalYear = Integer.parseInt(args[2]);
		int step = Integer.parseInt(args[3]);
		String query = readFileContent(fileName);

		fileName = new File(fileName).getName();

		System.out.println("Performing query " + query);

		IAnalyzer<Object[]> analyzer;
		OutputWriterFactory<Object[]> outputWriterFactory = new CSVOutputWriterFactory<Object[]>("output", new ObjectArrayCSVOutputProducer());
		Session session = HibernateSessionManager.getNewSession();
		Query sqlQuery = session.createSQLQuery(query);
		SEMinerQuery<Object[]> seMinerQuery = new HibernateQuery<Object[]>(sqlQuery);
		analyzer = new SlidingWindowAnalyzer<Object[]>(fileName, session, seMinerQuery, initialYear, finalYear, step, outputWriterFactory);
		analyzer.outputResults();
	}

	public static String readFileContent(String fileName) throws IOException {
		FileReader reader = new FileReader(fileName);
		BufferedReader in = new BufferedReader(reader);
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			builder.append(line);
			builder.append('\n');
		}
		in.close();
		reader.close();
		return builder.toString();
	}
}
