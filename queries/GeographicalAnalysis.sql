SELECT	Location, count(*)
FROM	ARTICLES
JOIN	Article_Author	ON ArticleId			= ARTICLES_ArticleId
JOIN	AUTHORS			ON authors_AuthorId		= AuthorId
JOIN  	AFFILIATIONS	ON affiliation_title	= `AFFILIATIONS`.title
WHERE Location <> "" and
-- 	venue_venueId = 1 and
	year BETWEEN :firstYear and :lastYear
GROUP BY Location
ORDER BY count(*) DESC
LIMIT 10