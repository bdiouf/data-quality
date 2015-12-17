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
package org.talend.dataquality.statistics.datetime;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @author mzhao
 */
public class GroupedSystemDateTimePatternManager {

    private static final Logger LOGGER = Logger.getLogger(GroupedSystemDateTimePatternManager.class);

    private static List<Map<Pattern, String>> PATTERN_GROUP_LIST = new ArrayList<Map<Pattern, String>>();

    static {
        InputStream stream = GroupedSystemDateTimePatternManager.class.getResourceAsStream("DateFormatsGrouped.txt");
        try {
            List<String> lines = IOUtils.readLines(stream);
            Map<Pattern, String> currentGroupMap = null;
            for (String line : lines) {
                if (!"".equals(line.trim())) { // Not empty
                    if (line.startsWith("--")) { // group separator
                        currentGroupMap = new HashMap<Pattern, String>();
                        PATTERN_GROUP_LIST.add(currentGroupMap);
                    } else {
                        String[] lineArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "\t");
                        String format = lineArray[0];
                        Pattern pattern = Pattern.compile(lineArray[1]);
                        currentGroupMap.put(pattern, format);
                    }
                }
            }
            stream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    public static Set<String> datePatternReplace(String value) {
        return dateTimePatternReplace(PATTERN_GROUP_LIST, value);
    }

    /**
     * Replace the value with time pattern string.
     * 
     * @param value
     * @return
     */
    public static Set<String> timePatternReplace(String value) {
        return dateTimePatternReplace(PATTERN_GROUP_LIST, value);
    }

    private static Set<String> dateTimePatternReplace(List<Map<Pattern, String>> patternGroupList, String value) {
        if (StringUtils.isEmpty(value)) {
            return Collections.singleton(StringUtils.EMPTY);
        }
        HashSet<String> resultSet = new HashSet<String>();
        for (Map<Pattern, String> patternMap : patternGroupList) {
            for (Pattern parser : patternMap.keySet()) {
                try {
                    if (parser.matcher(value).find()) {
                        resultSet.add(patternMap.get(parser));
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
            if (resultSet.size() > 0) {
                return resultSet;
            }
        }
        return resultSet;
    }
}
