SELECT affiliation_title, count(affiliation_title) as total
FROM `ARTICLES`
JOIN `Article_Author`	ON `ArticleID` = `Articles_ArticleID`
JOIN `AUTHORS` 			ON `Authors_AuthorID` = `AuthorID`
WHERE venue_venueId = 1
GROUP BY affiliation_title
ORDER BY total DESC


SELECT affiliation_title, sum(citations) as citations
FROM (
SELECT affiliation_title, count(*) as citations
FROM	`ARTICLES`
JOIN	`Article_Author` 		ON `ArticleId` = `Article_Author`.`ARTICLES_ArticleID`
JOIN	`AUTHORS` 				ON `authors_AuthorId` = `AuthorId`
JOIN	`INTERNAL_CITATIONS`	ON `ArticleId` = `INTERNAL_CITATIONS`.`ARTICLES_ArticleID`
JOIN	`ARTICLES` as `CITING_ARTICLES` ON `INTERNAL_CITATIONS`.`citingArticles_ArticleId` = `CITING_ARTICLES`.ArticleID
WHERE	`affiliation_title` != ''
GROUP BY affiliation_title

UNION

SELECT affiliation_title, count(*) as citations
FROM	`ARTICLES`
JOIN	`Article_Author` 		ON `ArticleId` = `Article_Author`.`ARTICLES_ArticleID`
JOIN	`AUTHORS` 				ON `authors_AuthorId` = `AuthorId`
JOIN	`ALL_CITATIONS`	ON `ArticleId` = `ALL_CITATIONS`.`ARTICLES_ArticleID`
WHERE	`affiliation_title` != ''
GROUP BY affiliation_title
) as CITATIONS
GROUP BY affiliation_title
ORDER BY citations DESC
LIMIT 15