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

import java.util.Set;

public class DQDocument {

    private String id;

    private Set<String> synterm;

    private DQCategory category;

    private String creator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getSynterm() {
        return synterm;
    }

    public void setSynterm(Set<String> synterm) {
        this.synterm = synterm;
    }

    public DQCategory getCategory() {
        return category;
    }

    public void setCategory(DQCategory category) {
        this.category = category;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "DQDocument{" + "id='" + id + '\'' + ", category=" + category.getName() + ", synterm='" + synterm + '\''
                + ", creator='" + creator + '\'' + '}';
    }
}
