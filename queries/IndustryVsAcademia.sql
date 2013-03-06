SELECT	isIndustry, count(*)
FROM	ARTICLES
JOIN	Article_Author	ON ArticleId			= ARTICLES_ArticleId
JOIN	AUTHORS			ON authors_AuthorId		= AuthorId
JOIN  	AFFILIATIONS	ON affiliation_title	= `AFFILIATIONS`.title
WHERE -- venue_venueId = 2 and
	  year BETWEEN :firstYear and :lastYear
GROUP BY isIndustry
ORDER BY count(*) DESC