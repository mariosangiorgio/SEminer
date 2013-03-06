SELECT venue, count(*)

FROM `SEminer`.CitationInformation
JOIN `SEminer`.ALL_CITATIONS ON identifier = allCitations_identifier
JOIN `SEminer`.ARTICLES ON Articles_ArticleId = ArticleId

WHERE venue_venueId = 1

GROUP BY venue
ORDER BY count(*) DESC