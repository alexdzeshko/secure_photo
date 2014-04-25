package by.deniotokiari.core.utils;

import by.deniotokiari.core.annotations.ContractInfo;
import by.deniotokiari.core.annotations.db.DBContract;
import by.deniotokiari.core.annotations.db.DBTableName;
import android.net.Uri;

public class ContractUtils {

	private static final String WRONG_CONTRACT_CLASS = "Wrong contracts class! Contracts class should be marked with DBContract annotation";

	public static Uri getUri(Class<?> contract) {
		ContractInfo info = contract.getAnnotation(ContractInfo.class);
		return Uri.parse(info.uri());
	}

	public static String getType(Class<?> contract) {
		ContractInfo info = contract.getAnnotation(ContractInfo.class);
		return info.type();
	}

	public static String getTableName(Class<?> contract) {
		DBTableName name = contract.getAnnotation(DBTableName.class);
		return name.tableName();
	}

	public static boolean isContractClass(Class<?> cls) {
        return cls.getAnnotation(DBContract.class) != null;
	}

	public static void checkContractClass(Class<?> cls) {
		if (!isContractClass(cls)) {
			throw new IllegalArgumentException(WRONG_CONTRACT_CLASS);
		}
	}

}
