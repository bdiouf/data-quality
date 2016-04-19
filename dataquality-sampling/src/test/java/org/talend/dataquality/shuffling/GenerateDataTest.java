package org.talend.dataquality.shuffling;

import java.net.URISyntaxException;

import org.junit.Test;

public class GenerateDataTest {

    @Test
    public void testOneColumnData() throws URISyntaxException {
        System.out.println(new GenerateData().getData("email").size());
    }
}
