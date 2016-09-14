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
package org.talend.dataquality.email.api;

/**
 * the parameters used by email verify.
 */
public class EmailVerifyParams {

    private boolean isUseRegularRegex;// = true;

    private String localPartRegexExpress;

    // private String localPartRegex = EmailValidUtils.replaceLocalpart(localPartRegexExpress);

    private boolean isLocalPartCaseSensitive;// = false; // the domain part is never case sensitive

    private boolean isBlackListDomains;

    private boolean isCallbackMailServer;// = false;

    // By default, it is checked
    private boolean isValidTLDs;// = true;

    /**
     * Getter for isUseRegularRegex.talend
     * 
     * @return the isUseRegularRegex
     */
    public boolean isUseRegularRegex() {
        return this.isUseRegularRegex;
    }

    /**
     * Sets the isUseRegularRegex.
     * 
     * @param isUseRegularRegex the isUseRegularRegex to set
     */
    public void setUseRegularRegex(boolean isUseRegularRegex) {
        this.isUseRegularRegex = isUseRegularRegex;
    }

    /**
     * Getter for localPartRegexExpress.
     * 
     * @return the localPartRegexExpress
     */
    public String getLocalPartRegexExpress() {
        return this.localPartRegexExpress;
    }

    /**
     * Sets the localPartRegexExpress.
     * 
     * @param localPartRegexExpress the localPartRegexExpress to set
     */
    public void setLocalPartRegexExpress(String localPartRegexExpress) {
        this.localPartRegexExpress = localPartRegexExpress;
    }

    /**
     * Getter for isLocalPartCaseSensitive.
     * 
     * @return the isLocalPartCaseSensitive
     */
    public boolean isLocalPartCaseSensitive() {
        return this.isLocalPartCaseSensitive;
    }

    /**
     * Sets the isLocalPartCaseSensitive.
     * 
     * @param isLocalPartCaseSensitive the isLocalPartCaseSensitive to set
     */
    public void setLocalPartCaseSensitive(boolean isLocalPartCaseSensitive) {
        this.isLocalPartCaseSensitive = isLocalPartCaseSensitive;
    }

    /**
     * Getter for isCallbackMailServer.
     * 
     * @return the isCallbackMailServer
     */
    public boolean isCallbackMailServer() {
        return this.isCallbackMailServer;
    }

    /**
     * Sets the isCallbackMailServer.
     * 
     * @param isCallbackMailServer the isCallbackMailServer to set
     */
    public void setCallbackMailServer(boolean isCallbackMailServer) {
        this.isCallbackMailServer = isCallbackMailServer;
    }

    /**
     * Getter for isValidTLDs.
     * 
     * @return the isValidTLDs
     */
    public boolean isValidTLDs() {
        return this.isValidTLDs;
    }

    /**
     * Sets the isValidTLDs.
     * 
     * @param isValidTLDs the isValidTLDs to set
     */
    public void setValidTLDs(boolean isValidTLDs) {
        this.isValidTLDs = isValidTLDs;
    }

    /**
     * Getter for isBlackListDomains.
     * 
     * @return the isBlackListDomains
     */
    public boolean isBlackListDomains() {
        return isBlackListDomains;
    }

    /**
     * Sets the isBlackListDomains.
     * 
     * @param isBlackListDomains the isBlackListDomains to set
     */
    public void setBlackListDomains(boolean isBlackListDomains) {
        this.isBlackListDomains = isBlackListDomains;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LocalPartRegexExpress -> ").append(getLocalPartRegexExpress()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("isBlackListDomains -> ").append(isBlackListDomains()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$    
        sb.append("isUseRegularRegex -> ").append(isUseRegularRegex()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("isLocalPartCaseSensitive -> ").append(isLocalPartCaseSensitive()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("isCallbackMailServer -> ").append(isCallbackMailServer()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$    
        sb.append("isValidTLDs -> ").append(isValidTLDs()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        return sb.toString();
    }
}
