package br.com.caelum.gnarus.migration;

import org.hibernate.Session;

public class DatabaseManager {

	private final Session session;

	public DatabaseManager(Session session) {
		this.session = session;
	}

	public void run(String sql) {
		session.createSQLQuery(sql).executeUpdate();
	}

}
