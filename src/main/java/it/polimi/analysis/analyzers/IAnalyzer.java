package it.polimi.analysis.analyzers;

import it.polimi.analysis.output.OutputException;

public interface IAnalyzer<R> {
	void outputResults() throws OutputException;
}