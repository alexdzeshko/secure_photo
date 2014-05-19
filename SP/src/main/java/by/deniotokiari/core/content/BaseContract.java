package by.deniotokiari.core.content;

import by.deniotokiari.core.annotations.db.DBAutoincrement;
import by.deniotokiari.core.annotations.db.DBPrimaryKey;
import by.deniotokiari.core.annotations.db.types.DBInteger;

public interface BaseContract {
	
	@DBAutoincrement
	@DBInteger
	@DBPrimaryKey
	public static final String _ID = "_id";
	
}
