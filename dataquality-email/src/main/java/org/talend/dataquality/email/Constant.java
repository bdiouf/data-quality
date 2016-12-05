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
package org.talend.dataquality.email;

import java.util.Arrays;

/**
 * created by talend on 2014年12月29日 Detailled comment
 *
 */
public final class Constant {

    private Constant() {
        // nothing here
    }

    public static final char LEFT_BRACKET = '<'; //$NON-NLS-1$

    public static final char RIGHT_END = '>'; //$NON-NLS-1$

    public static final char DOT = '.'; //$NON-NLS-1$

    public static final char SLASH = '\\'; //$NON-NLS-1$

    public static final char AT = '@';

    // MOD sizhaoliu
    // SLASH must be handled at the first place, because there may be fake slashes in front of the other special chars
    private static final String[] SPECIAL_CHARS = { "\\", "[", "]", "?", "*", "+", "{", "}", "^", "-", "&", "$", "." }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$

    public static String[] getSpecialChars() {
        return Arrays.copyOf(SPECIAL_CHARS, SPECIAL_CHARS.length);
    }
}
