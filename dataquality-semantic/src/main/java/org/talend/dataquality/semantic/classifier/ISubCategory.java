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
package org.talend.dataquality.semantic.classifier;

import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.filter.ISemanticFilter;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by sizhaoliu on 16.03.15.
 */
@JsonDeserialize(as = UserDefinedCategory.class)
public interface ISubCategory {

    public String getId();

    public String getName();

    public String getLabel();

    public ISemanticFilter getFilter();

    public ISemanticValidator getValidator();

}
