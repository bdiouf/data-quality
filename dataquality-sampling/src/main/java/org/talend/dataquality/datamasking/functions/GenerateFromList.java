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
package org.talend.dataquality.datamasking.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * created by jgonzalez on 24 juin 2015. This function will modify the input by randomly selecting one of the values
 * given as parameter.
 *
 */
public abstract class GenerateFromList<T2> extends Function<T2> {

    private static final long serialVersionUID = 8936060786451303843L;

    protected List<String> StringTokens = new ArrayList<>();

    private File dataFile = null;

    private final static Logger log = Logger.getLogger(GenerateFromList.class);

    protected void init() {
        StringTokens.clear();
        // input file is exist
        if (parameters.length == 1 && integerParam == 0 && fileExist()) {
            StringTokens = loadDefaultFile();
        } else {
            for (String tmp : parameters) {
                StringTokens.add(tmp.trim());
            }
        }
    }

    /**
     * zshen Check that whether the file is exist
     * 
     * @return
     */
    private boolean fileExist() {
        String filePath = getFileFullPath();
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            dataFile = file;
            return true;
        }
        return false;
    }

    /**
     * zshen Load the file from default one
     * 
     * @return
     */
    private List<String> loadDefaultFile() {
        try {
            String encoding = "UTF-8"; //$NON-NLS-1$
            if (dataFile.isFile() && dataFile.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(dataFile), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    StringTokens.add(lineTxt.trim());
                }
                read.close();
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return StringTokens;
    }

    /**
     * DOC zshen Comment method "getFileFullPath".
     * 
     * @return
     */
    protected String getFileFullPath() {
        return "data/" + parameters[0]; //$NON-NLS-1$
    }

}
