package com.example.batch.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
//import org.hibernate.dialect.identity.IdentitySupport;

public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        super();
    }

    @Override
    public String getTableTypeString() {
        return "";
    }

//    @Override
//    public boolean supportsIdentityColumns() {
//        return true;
//    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new IdentityColumnSupportImpl() {
            @Override
            public boolean supportsIdentityColumns() {
                return true;
            }

//            @Override
//            public String getIdentityColumnString() {
//                return "integer";
//            }

//            @Override
//            public String getIdentitySelectString(String table, String column) {
//                return "select last_insert_rowid()";
//            }

            @Override
            public String getIdentityInsertString() {
                return "";
            }
        };
    }

//    @Override
//    public boolean supportsLimit() {
//        return true;
//    }
//
//    @Override
//    public String getLimitString(String query, int offset, int limit) {
//        return query + " limit " + limit + " offset " + offset;
//    }
}
