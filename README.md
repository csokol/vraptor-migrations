## vraptor-migrations
==================

A VRaptor plugin to allow the use of migrations, just like the Rails guys.

# installing

TODO: maven 

# how to use

Just create a class with name **M(number)AnyNameHere**. The number should always increase,
contain 3 digits (001, 002, ...), and never repeat.

--------   
```java
public class M001TestMigration extends Migration{

	@Override
	public String up() {
		return "your up sql here";
	}
	
	// optional
	public String down() {
		return "your down sql here"
	}

}
```

I suggest you to create a source folder just for your migrations.

# help

send me an email.

# license

Apache License 2.0 (http://www.apache.org/licenses/)
