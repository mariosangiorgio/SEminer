SELECT affiliation_title, count(affiliation_title) as total
FROM `ARTICLES`
JOIN `Article_Author`	ON `ArticleID` = `Articles_ArticleID`
JOIN `AUTHORS` 			ON `Authors_AuthorID` = `AuthorID`
WHERE venue_venueId = 1
GROUP BY affiliation_title
ORDER BY total DESC