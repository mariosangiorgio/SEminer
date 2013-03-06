package it.polimi.analysis.similarity;

public class MostCitedAuthors extends AuthorsAnalyzer {
	private static final String totalPublishedPapersQuery =
		"select count(*) " +
		"from Article as article " +
		"join article.authors author " +
		"join article.citingArticles citingArticle "+
		"where citingArticle.venue = :venue and article.year between :initialYear and :lastYear";
	private static final String publishedPapersByAuthorQuery =
		"select author, count(*) " +
		"from Article as article " +
		"join article.authors author " +
		"join article.citingArticles citingArticle "+
		"where citingArticle.venue = :venue and article.year between :initialYear and :lastYear "+
		"group by author order by count(*) desc";

	public MostCitedAuthors(float ratio) {
		super(ratio, totalPublishedPapersQuery, publishedPapersByAuthorQuery);
	}
}
