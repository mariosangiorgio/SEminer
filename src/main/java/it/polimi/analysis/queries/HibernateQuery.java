package it.polimi.analysis.queries;

import java.util.List;

import org.hibernate.Query;

public class HibernateQuery<R> implements SEMinerQuery<R> {
	private final Query hibernateQuery;

	public HibernateQuery(Query query) {
		this.hibernateQuery = query;
	}

	@Override
	public void setParameter(String parameter, Object value) {
		hibernateQuery.setParameter(parameter, value);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<R> results() {
		return hibernateQuery.list();
	}

}
