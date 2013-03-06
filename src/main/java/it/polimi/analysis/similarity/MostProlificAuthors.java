package it.polimi.analysis.similarity;

public class MostProlificAuthors extends AuthorsAnalyzer {
	private static final String totalPublishedPapersQuery =
		"select count(*) " +
		"from Article as article " +
		"join article.authors author " +
		"where article.venue = :venue and article.year between :initialYear and :finalYear";
	private static final String publishedPapersByAuthorQuery =
		"select author, count(*) " +
		"from Article as article join article.authors author " +
		"where article.venue = :venue and article.year between :initialYear and :finalYear"+
		"group by author order by count(*) desc";

	public MostProlificAuthors(float ratio) {
		super(ratio, totalPublishedPapersQuery, publishedPapersByAuthorQuery);
	}
}
