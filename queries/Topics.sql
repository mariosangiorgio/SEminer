SELECT classification, count(*) as total
FROM ARTICLES
WHERE
	-- venue_venueId = 1 and
	classification IS NOT null and
	year BETWEEN :firstYear and :lastYear
GROUP BY classification
ORDER BY total DESC