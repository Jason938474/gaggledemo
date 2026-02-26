package com.gaggledemo.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SQLUtilTest {

    @Test
    public void testSqlParsing() throws Exception {
        String testInput = Files.readString(Path.of(getClass().getResource("/parseTest.txt").toURI()));
        List<String> result = SQLUtil.parseSql(testInput);
        List<String> expected = List.of("one", "two", "three", "four");
        Assertions.assertEquals(expected, result);
    }
}
