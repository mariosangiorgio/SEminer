SELECT externalCitations.name, externalCitations.externalCitations + internalCitations.internalCitations as totalCitations,
-- externalCitations.externalCitations,
internalCitations.internalCitations
FROM
(SELECT name, count(*) as externalCitations
FROM	`ARTICLES`
JOIN	`Article_Author` 		ON `ArticleId` = `Article_Author`.`ARTICLES_ArticleID`
JOIN	`AUTHORS` 				ON `authors_AuthorId` = `AuthorId`
JOIN	`ALL_CITATIONS`			ON `ArticleId` = `ALL_CITATIONS`.`ARTICLES_ArticleID`
WHERE	venue_VenueId = 3
GROUP BY name) as externalCitations

JOIN
(SELECT name, count(*) as internalCitations
FROM	`ARTICLES`
JOIN	`Article_Author` 		ON `ArticleId` = `Article_Author`.`ARTICLES_ArticleID`
JOIN	`AUTHORS` 				ON `authors_AuthorId` = `AuthorId`
JOIN	`INTERNAL_CITATIONS`	ON `ArticleId` = `INTERNAL_CITATIONS`.`ARTICLES_ArticleID`
WHERE	venue_VenueId = 3
GROUP BY name) as internalCitations

ON `internalCitations`.`name` = `externalCitations`.`name`

ORDER BY totalCitations DESC
LIMIT 15