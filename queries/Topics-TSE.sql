SELECT classification, count(*) as total
FROM ARTICLES
JOIN VENUES on venue_venueId = VenueId
WHERE
	name = "IEEE Trans. Software Eng." and
	classification IS NOT null and
	year BETWEEN :firstYear and :lastYear
GROUP BY classification
ORDER BY total DESC
