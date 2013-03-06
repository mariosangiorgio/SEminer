SELECT name, count(*)
FROM ARTICLES
JOIN Article_Author ON ArticleId = ARTICLES_ArticleId
JOIN AUTHORS ON authors_AuthorId = AuthorId
WHERE venue_VenueID = 1
GROUP BY name
ORDER BY count(*) desc