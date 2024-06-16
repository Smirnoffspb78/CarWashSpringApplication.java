package com.smirnov.carwashspring.repository;

/**
 * Соедржит запросы репоизториев.
 */
final class Queries {
    private Queries() {
    }

    /**
     * Возращает выруску за заданный диапазон.
     */
    public static final String FIND_PROFIT_BY_RANGE = """
            SELECT SUM(records.cost)
            FROM records
            WHERE start >= :start
            AND finish <= :finish
            AND is_complite = TRUE""";
}
