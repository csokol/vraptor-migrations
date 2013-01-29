package br.com.caelum.gnarus.migration;

public abstract class Migration {
	public static final String SQL_SPLIT = "#---";

	public abstract String up();
	
	public String down() {
		return ""; 
	}
	
	public boolean hasDown() {
		return !down().equals("");
	}
}
