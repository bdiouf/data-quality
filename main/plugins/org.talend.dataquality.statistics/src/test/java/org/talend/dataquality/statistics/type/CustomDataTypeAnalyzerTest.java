package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.DataTypeEnum;

public class CustomDataTypeAnalyzerTest {

    @Test
    public void testCustomDataPattern() {

        CustomDataTypeAnalyzer analyzer = new CustomDataTypeAnalyzer();

        String[] testColumn = new String[] { "1", "2", "2015?08?20", "2012?02?12", "12/2/99" };

        // Before set Custom Data Pattern: yyyy?mm?dd
        // the type of testColumn is INTEGER, since "2015?08?20" & "2012?02?12" can't be recognised as date
        for (String record : testColumn) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataType> resultBeforeSetCustomP = analyzer.getResult();
        assertEquals(DataTypeEnum.INTEGER, resultBeforeSetCustomP.get(0).getSuggestedType());

        // After set Custom Data Pattern: yyyy?mm?dd, "2015?08?20" & "2012?02?12" can be recognised as date
        // the type of testColumn is DATE
        analyzer.addCustomDateTimePattern("yyyy?mm?dd");
        analyzer.init();
        for (String record : testColumn) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataType> resultAfterSetCustomP = analyzer.getResult();
        assertEquals(DataTypeEnum.DATE, resultAfterSetCustomP.get(0).getSuggestedType());

    }
}
