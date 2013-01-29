package br.com.caelum.gnarus.migration;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class MigrationExecutor {

	private final SessionFactory sf;
	private int currentMigration = -1;
	private Session session;

	public MigrationExecutor(SessionFactory sf) {
		this.sf = sf;
	}

	public void begin() {
		session = sf.openSession();
		session.beginTransaction();
	}

	public void end() {
		session.getTransaction().commit();
	}

	public void rollback() {
		session.getTransaction().rollback();
	}
	public void rollback(Migration m) {
		if(m.hasDown()) {
			runSqls(m.down());
		}
		rollback();
	}

	public void updateCurrentMigration(int lastMigration) {
		session.createSQLQuery("update gnarusmigration set last = " + lastMigration).executeUpdate();
	}

	public int currentMigration() {
		if(currentMigration == -1) {
			currentMigration = (int) session.createSQLQuery("select last from gnarusmigration").uniqueResult();
		}
		return currentMigration;
	}

	private void runSqls(String fullSql) {
		String[] sqls = fullSql.split(Migration.SQL_SPLIT);
		for(String sql : sqls) {
			session.createSQLQuery(sql).executeUpdate();
		}
	}

	public void run(Migration m) {
		runSqls(m.up());
	}

	public void prepareTables() {
		session.createSQLQuery("create table if not exists gnarusmigration (last int(20))").executeUpdate();
	}

	public void close() {
		session.close();
	}

}
