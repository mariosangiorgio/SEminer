SELECT classification, count(*) as total
FROM ARTICLES
JOIN VENUES on venue_venueId = VenueId
WHERE
	name = "SIGSOFT FSE" and
	classification IS NOT null and
	year BETWEEN :firstYear and :lastYear
GROUP BY classification
ORDER BY total DESC
