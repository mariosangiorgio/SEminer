package it.polimi.crawler;

import it.polimi.data.hibernate.entities.Article;

import java.util.ArrayList;
import java.util.List;

public class TestHandler implements DBLPParserHandler{
	private List<Article> articles = new ArrayList<Article>();

	@Override
	public void handle(Article article) {
		articles.add(article);
	}
	
	public void clear(){
		articles.clear();
	}

	public List<Article> getArticles() {
		return articles;
	}

}
