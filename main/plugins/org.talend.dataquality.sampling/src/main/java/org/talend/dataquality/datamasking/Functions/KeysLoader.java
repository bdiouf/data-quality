// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.Functions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Load keys from file
 * 
 * @since 2.1.1
 * @author mzhao
 */
public class KeysLoader {

    /**
     * 
     * @param filePath the file path where keys to be loaded.
     * @param keyDelimiter key delimiter
     * @return keys array
     * @throws FileNotFoundException
     * @throws NullPointerException
     */
    public static List<String> loadKeys(String filePath, String keyDelimiter) throws FileNotFoundException, NullPointerException {
        List<String> keys = new ArrayList<>();
        Scanner in;
        try {
            in = new Scanner(new FileReader(filePath)).useDelimiter(keyDelimiter);
            while (in.hasNext()) {
                keys.add(in.next().trim());
            }
            in.close();
        } catch (FileNotFoundException | NullPointerException e) {
            throw (e);
        }
        return keys;
    }
}
