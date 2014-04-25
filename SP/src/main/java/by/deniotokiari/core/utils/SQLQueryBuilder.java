package by.deniotokiari.core.utils;

public class SQLQueryBuilder {
	private String sql;

	private static final String templateSelect = "SELECT %s";
	private static final String templateFrom = "FROM %s";
	private static final String templateJoin = "%s JOIN %s ON %s"; // type JOIN
																	// table ON
																	// condition
	private static final String templateWhere = "WHERE %s";
	private static final String templateGroupBy = "GROUP BY %s";
	private static final String templateHaving = "HAVING %s";
	private static final String templateOrderBy = "ORDER BY %s";
	private static final String templateLimit = "LIMIT %s";

	private String select;
	private String from;
	private String join;
	private String where;
	private String groupBy;
	private String having;
	private String orderBy;
	private String limit;

	private String selectTitle;

	public SQLQueryBuilder() {
		sql = "";
	}
	
	public static String and(Object... objects) {
		if (objects == null) {
			return null;
		}
		return getAsString(" AND ", objects);
	}
	
	public static String or(Object... objects) {
		if (objects == null) {
			return null;
		}
		return getAsString(" OR ", objects);
	}
	
	public static String as(String[] columns, String[] names) {
		if (columns.length != names.length) {
			throw new IllegalArgumentException("Columns and names length must be equal!");
		}
		Object[] result = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			result[i] = String.format("%s AS %s", columns[i], names[i]);
		}
		return getAsString(", ", result);
	}

	private void build() {
		sql = "";
		if (select != null) {
			sql += select;
		}
		if (from != null) {
			sql += " " + from;
		}
		if (join != null) {
			sql += " " + join;
		}
		if (where != null) {
			sql += " " + where;
		}
		if (groupBy != null) {
			sql += " " + groupBy;
		}
		if (having != null) {
			sql += " " + having;
		}
		if (orderBy != null) {
			sql += " " + orderBy;
		}
		if (limit != null) {
			sql += " " + limit;
		}
	}

	private static String getAsString(String joiner, Object... objects) {
		String result = "";
        for (Object object : objects) {
            if (object instanceof String) {
                result += object + joiner;
            } else if (object instanceof SQLQueryBuilder) {
                result += "(" + ((SQLQueryBuilder) object);
                if (((SQLQueryBuilder) object).selectTitle != null) {
                    result += ") " + ((SQLQueryBuilder) object).selectTitle + joiner;
                } else {
                    result += joiner;
                }
            }
        }
		result = result.substring(0, result.length() - joiner.length());
		return result;
	}

	public SQLQueryBuilder select(String selectTitle, Object... columns) {
		select = String.format(templateSelect, getAsString(", ", columns));
		this.selectTitle = selectTitle;
		return this;
	}

	public SQLQueryBuilder from(Object... from) {
		this.from = String.format(templateFrom, getAsString(", ", from));
		return this;
	}

	public SQLQueryBuilder join(String type, String table, String condition) {
		join = String.format(templateJoin, type, table, condition);
		return this;
	}

	public SQLQueryBuilder where(Object selections) {
		where = String.format(templateWhere, selections);
		return this;
	}

	public SQLQueryBuilder groupBy(Object... groupBy) {
		if (groupBy == null) {
			return this;
		}
		this.groupBy = String.format(templateGroupBy, getAsString(", ", groupBy));
		return this;
	}

	public SQLQueryBuilder having(Object having) {
		this.having = String.format(templateHaving, having);
		return this;
	}

	public SQLQueryBuilder orderBy(Object... ordersBy) {
		if (ordersBy == null) {
			return this;
		}
		orderBy = String.format(templateOrderBy, getAsString(", ", ordersBy));
		return this;
	}
	
	public SQLQueryBuilder limit(long limit) {
		this.limit = String.format(templateLimit, String.valueOf(limit));
		return this;
	}

	public String getSql() {
		return toString();
	}

	@Override
	public String toString() {
		build();
		return sql;
	}
}
