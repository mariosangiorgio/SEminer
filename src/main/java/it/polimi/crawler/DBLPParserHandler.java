package it.polimi.crawler;

import it.polimi.data.hibernate.entities.Article;

public interface DBLPParserHandler {
	void handle(Article article);
}
