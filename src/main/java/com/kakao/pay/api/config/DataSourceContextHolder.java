package com.kakao.pay.api.config;

public class DataSourceContextHolder {
    private static ThreadLocal<DatabaseType> CONTEXT_HOLDER = ThreadLocal.withInitial(() -> DatabaseType.MASTER);

    public static void set(DatabaseType databaseType) {
        CONTEXT_HOLDER.set(databaseType);
    }

    public static DatabaseType get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
