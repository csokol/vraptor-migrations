package br.com.caelum.gnarus.migration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.PatternFilenameFilter;

public class UglyClasspathMigrationFinder {

	public List<Migration> all() {
		String base = "br.com.caelum.gnarus.migration.all";
		List<Migration> migrations = new ArrayList<>();
		File baseDir = new File("src/main/webapp/WEB-INF/classes/"
				+ base.replace(".", "/"));
		FilenameFilter filterClasses = new PatternFilenameFilter(".*\\.class");
		for (String file : baseDir.list(filterClasses)) {
			String typeName = base + "." + file.replace(".class", "");
			createMigrationIfMatching(migrations, typeName);
		}
		return migrations;
	}

	@SuppressWarnings("unchecked")
	private void createMigrationIfMatching(List<Migration> migrations,
			String typeName) {
		try {
			Class<Migration> type = (Class<Migration>) Class.forName(typeName);
			if (Migration.class.isAssignableFrom(type)) {
				Migration migration = (Migration) type.newInstance();
				migrations.add(migration);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to startup migration "
					+ typeName, e);
		}

	}

}
