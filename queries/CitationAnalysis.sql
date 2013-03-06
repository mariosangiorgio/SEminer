SELECT venue, count(*)
FROM CitationInformation
JOIN ALL_CITATIONS ON identifier = allCitations_identifier
JOIN ARTICLES ON Articles_ArticleId = ArticleId
WHERE venue_venueId = 1 and
	  year BETWEEN :firstYear and :lastYear
GROUP BY venue
ORDER BY count(*) DESC
LIMIT 10