SELECT externalCitations.affiliation_title, externalCitations.externalCitations + internalCitations.internalCitations as totalCitations
FROM
(SELECT affiliation_title, count(*) as externalCitations
FROM	`ARTICLES`
JOIN	`Article_Author` 		ON `ArticleId` = `Article_Author`.`ARTICLES_ArticleID`
JOIN	`AUTHORS` 				ON `authors_AuthorId` = `AuthorId`
JOIN	`ALL_CITATIONS`			ON `ArticleId` = `ALL_CITATIONS`.`ARTICLES_ArticleID`
JOIN	`CitationInformation`	ON `allCitations_identifier` = `identifier`
-- JOIN	`INTERNAL_CITATIONS`	ON `ArticleId` = `INTERNAL_CITATIONS`.`ARTICLES_ArticleID`
WHERE	`affiliation_title` != ''
GROUP BY affiliation_title) as externalCitations

JOIN
(SELECT affiliation_title, count(*) as internalCitations
FROM	`ARTICLES`
JOIN	`Article_Author` 		ON `ArticleId` = `Article_Author`.`ARTICLES_ArticleID`
JOIN	`AUTHORS` 				ON `authors_AuthorId` = `AuthorId`
JOIN	`ALL_CITATIONS`			ON `ArticleId` = `ALL_CITATIONS`.`ARTICLES_ArticleID`
JOIN	`INTERNAL_CITATIONS`	ON `ArticleId` = `INTERNAL_CITATIONS`.`ARTICLES_ArticleID`
WHERE	`affiliation_title` != ''
GROUP BY affiliation_title) as internalCitations

ON `internalCitations`.`affiliation_title` = `externalCitations`.`affiliation_title`

ORDER BY totalCitations DESC