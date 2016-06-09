// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.classifier.custom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class UDCategorySerDeser {

    private static final String BUNDLE_NAME = "org.talend.dataquality.semantic"; //$NON-NLS-1$

    private static final String FILE_NAME = "categorizer.json"; //$NON-NLS-1$

    private static UserDefinedClassifier udc;

    public static UserDefinedClassifier getRegexClassifier() {
        if (udc == null) {
            udc = readJsonFile();
        }
        return udc;
    }

    /**
     * 
     * Read json file and get the Object UserDefinedClassifier.
     * 
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    static UserDefinedClassifier readJsonFile() {
        UserDefinedClassifier udc = null;
        InputStream inputStream = UDCategorySerDeser.class
                .getResourceAsStream("/org/talend/dataquality/semantic/recognizer/" + FILE_NAME);
        try {
            udc = readJsonFile(inputStream);
        } catch (IOException e) {
            URL url;
            try {
                url = new URL("platform:/plugin/" + BUNDLE_NAME //$NON-NLS-1$
                        + "/org/talend/dataquality/semantic/recognizer/" + FILE_NAME);
                inputStream = url.openConnection().getInputStream();
                udc = readJsonFile(inputStream);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return udc;
    }

    static UserDefinedClassifier readJsonFile(InputStream inputStream) throws IOException {
        return new ObjectMapper().readValue(inputStream, UserDefinedClassifier.class);
    }

    /**
     * 
     * Write the content to json file.
     * 
     * @param userDefinedClassifier the classifiers to persist in a json file
     * @param outputStream the stream to write in
     * @return
     */
    public boolean writeToJsonFile(UserDefinedClassifier userDefinedClassifier, OutputStream outputStream) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, userDefinedClassifier);
            outputStream.close();
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return false;
        }
        return true;
    }

}
