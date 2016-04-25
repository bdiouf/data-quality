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

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceLastChars.
 *
 */
public class ReplaceLastCharsLong extends ReplaceLastChars<Long> {

    private static final long serialVersionUID = -9172743551534233769L;

    private int parameter = 0;

    @Override
    protected Long doGenerateMaskedField(Long l) {
        if (l != null && integerParam > 0) {
            if (l == 0L) {
                return (long) rnd.nextInt(9);
            } else {
                parameter = (int) Math.log10(l) + 1 <= integerParam ? (int) Math.log10(l) + 1 : integerParam;
                StringBuilder sbui = new StringBuilder(l.toString());
                StringBuilder rempl = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < parameter; ++i) {
                    rempl.append(rnd.nextInt(9));
                }
                sbui.replace(sbui.length() - parameter, sbui.length(), rempl.toString());
                return Long.parseLong(sbui.toString());
            }
        } else {
            return 0L;
        }
    }
}
