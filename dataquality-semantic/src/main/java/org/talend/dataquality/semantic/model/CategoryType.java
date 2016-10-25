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
package org.talend.dataquality.semantic.model;

public enum CategoryType {
    RE,
    DD,
    KW,
    OT;

    public static CategoryType from(String recognizerType) {
        switch (recognizerType) {
        case "REGEX":
            return RE;
        case "OPEN_INDEX":
            return DD;
        case "CLOSED_INDEX":
            return DD;
        case "KEYWORD":
            return KW;
        case "OTHER":
            return OT;
        default:
            break;
        }
        return OT;
    }
}
