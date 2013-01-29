package br.com.caelum.gnarus.migration;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class MigrationRunner {

	private final List<Migration> migrations;
	private final NumberExtractor extractNumber;
	private final static Logger LOGGER = LoggerFactory
			.getLogger(MigrationRunner.class);
	private final MigrationExecutor executor;

	public MigrationRunner(List<Migration> migrations, NumberExtractor number,
			MigrationExecutor executor) {
		this.extractNumber = number;
		this.executor = executor;
		this.migrations = sort(migrations);
	}

	private List<Migration> sort(List<Migration> unsortedMigrations) {
		Collections
				.sort(unsortedMigrations, new MigrationSorter(extractNumber));
		return unsortedMigrations;
	}

	@PostConstruct
	public void execute() {

		prepareTables();

		for (Migration m : migrations) {
			executeMigration(m);
		}

		executor.close();
	}

	private void prepareTables() {
		try {
			executor.begin();
			executor.prepareTables();
			executor.end();
		} catch (Exception e) {
			executor.rollback();
			throw new RuntimeException("Unable to execute migration process", e);
		}
	}

	private void executeMigration(Migration m) {
		int number = extractNumber.from(m);
		if (number > executor.currentMigration()) {
			try {
				executor.begin();
				LOGGER.debug("Running migration " + number);
				executor.run(m);
				LOGGER.info("Migration " + number + " executed!");
				executor.updateCurrentMigration(number);
				executor.end();
			} catch (Exception e) {
				executor.rollback(m);
				throw new RuntimeException("Unable to execute migration "
						+ number, e);
			}
		}
	}

}
