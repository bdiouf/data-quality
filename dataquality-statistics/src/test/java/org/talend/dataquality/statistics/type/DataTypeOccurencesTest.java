package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertEquals;
import static org.talend.dataquality.statistics.type.DataTypeEnum.*;

import org.junit.Test;

public class DataTypeOccurencesTest {

    @Test
    public void shouldReturnStringWhenOnlyEmptyType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);

        // then
        assertEquals(STRING, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnBooleanWhenOnlyEmptyAndBooleanType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(BOOLEAN);

        // then
        assertEquals(BOOLEAN, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnBooleanWhenBooleanExceedsDefaultThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(BOOLEAN);
        typeOccurrences.increment(BOOLEAN);
        typeOccurrences.increment(STRING);

        // then
        assertEquals(BOOLEAN, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnTimeWhenOnlyEmptyAndTimerType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(TIME);

        // then
        assertEquals(TIME, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnTimeWhenTimeExceedsDefaultThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(TIME);
        typeOccurrences.increment(BOOLEAN);
        typeOccurrences.increment(TIME);

        // then
        assertEquals(TIME, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnDateWhenOnlyEmptyAndDateType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DATE);

        // then
        assertEquals(DATE, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnDateExceedsDefaultThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DATE);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(DATE);

        // then
        assertEquals(DATE, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnIntegerWhenOnlyEmptyAndIntegerType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(INTEGER);

        // then
        assertEquals(INTEGER, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnIntegerWhenIntegerExceedsDefaultThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(STRING);

        // then
        assertEquals(INTEGER, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnDoubleWhenOnlyEmptyAndDoubleType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DOUBLE);

        // then
        assertEquals(DOUBLE, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnDoubleWhenDoubleExceedsDefaultThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(DOUBLE);

        // then
        assertEquals(DOUBLE, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnDoubleWhenTheIntegerRatioEqualsToThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(DOUBLE);

        // then
        assertEquals(DOUBLE, typeOccurrences.getSuggestedType(0.6));
    }

    @Test
    public void shouldReturnIntegerWhenTheIntegerRatioExceedsThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(DOUBLE);

        // then
        assertEquals(INTEGER, typeOccurrences.getSuggestedType(0.55));
    }

    @Test
    public void shouldReturnStringWhenEachTypeAppearsOnce() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(BOOLEAN);
        typeOccurrences.increment(TIME);
        typeOccurrences.increment(DATE);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(STRING);

        // then
        assertEquals(STRING, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnStringWhenEachTypeAppearsOnce2() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(BOOLEAN);
        typeOccurrences.increment(TIME);
        typeOccurrences.increment(DATE);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(STRING);

        // then
        assertEquals(STRING, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnIntegerWhenDoubleAndIntegerExceedsDefaultThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(STRING);

        // then
        assertEquals(INTEGER, typeOccurrences.getSuggestedType());
    }

    @Test
    public void shouldReturnIntegerWhenIntegerExceedsGivenThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(STRING);
        DataTypeEnum suggestedType = typeOccurrences.getSuggestedType(0.1, 0.5);

        // then
        assertEquals(INTEGER, suggestedType);
    }

    @Test
    public void shouldReturnDoubleWhenIntegerDoesNotExceedGivenThreshold() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(STRING);
        DataTypeEnum suggestedType = typeOccurrences.getSuggestedType(0.1, 0.9);

        // then
        assertEquals(DOUBLE, suggestedType);
    }

    @Test
    public void shouldReturnStringWhenStringIsDominantType() {
        // given
        DataTypeOccurences typeOccurrences = new DataTypeOccurences();

        // when
        typeOccurrences.increment(EMPTY);
        typeOccurrences.increment(DOUBLE);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(INTEGER);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(STRING);
        typeOccurrences.increment(STRING);
        DataTypeEnum suggestedType = typeOccurrences.getSuggestedType(0.1, 0.9);

        // then
        assertEquals(STRING, suggestedType);
    }
}
