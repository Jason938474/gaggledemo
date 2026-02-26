package com.gaggledemo.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SQLUtilTest {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void testSqlParsing() throws Exception {
        logger.info("Starting Sql parsing test");
        String testInput = Files.readString(Path.of(getClass().getResource("/parseTest.txt").toURI()));
        List<String> result = SQLUtil.parseSql(testInput);
        List<String> expected = List.of("one", "two", "three", "four");
        Assertions.assertEquals(expected, result);
    }
}
