package org.talend.dataquality.statistics.text;

import java.io.Serializable;

/**
 * Text length bean with computation algorithm described at <a
 * href="https://help.talend.com/pages/viewpage.action?pageId=261412880&thc_login=done">Text statistics</a> <br>
 * If the length is not available (for example minTextLengthIgnoreBlank vs "all blank records"), <keyword>null</keyword>
 * will be returned.
 * 
 * @author zhao
 *
 */
public class TextLengthStatistics implements Serializable {

    private static final long serialVersionUID = 2599916536904438319L;

    // min
    private Integer minTextLength = null;

    private Integer minTextLengthIgnoreBlank = null;

    // max
    private Integer maxTextLength = null;

    private Integer maxTextLengthIgnoreBlank = null;

    // sum text length
    private Integer sumTextLength = null;

    private Integer sumTextLengthIgnoreBlank = null;

    // count
    private Integer count = 0;

    // count ignore blank
    private Integer countIgnoreBlank = 0;

    public void add(String value) {
        if (value == null) {
            return;
        }

        // Min
        if (minTextLength == null) {
            minTextLength = value.length();
        } else if (minTextLength > value.length()) {
            minTextLength = value.length();
        }
        // Min ignore blank
        if (value.trim().length() != 0) {
            if (minTextLengthIgnoreBlank == null) {
                minTextLengthIgnoreBlank = value.length();
            } else if (minTextLengthIgnoreBlank > value.length()) {
                minTextLengthIgnoreBlank = value.length();
            }
        }

        // Max
        if (maxTextLength == null) {
            maxTextLength = value.length();
        } else if (maxTextLength < value.length()) {
            maxTextLength = value.length();
        }
        // Max ignore blank
        if (value.trim().length() != 0) {
            if (maxTextLengthIgnoreBlank == null) {
                maxTextLengthIgnoreBlank = value.length();
            } else if (maxTextLengthIgnoreBlank < value.length()) {
                maxTextLengthIgnoreBlank = value.length();
            }
        }

        // sum
        if (sumTextLength == null) {
            sumTextLength = value.length();
        } else {
            sumTextLength += value.length();
        }

        // sum ignore blank
        if (value.trim().length() != 0) {
            if (sumTextLengthIgnoreBlank == null) {
                sumTextLengthIgnoreBlank = value.length();
            } else {
                sumTextLengthIgnoreBlank += value.length();
            }
        }

        // count
        count++;
        if (value.trim().length() != 0) {
            countIgnoreBlank++;
        }

    }

    public Integer getMinTextLength() {
        return minTextLength == null ? 0 : minTextLength;
    }

    public Integer getMinTextLengthIgnoreBlank() {
        return minTextLengthIgnoreBlank == null ? 0 : minTextLengthIgnoreBlank;
    }

    public Integer getMaxTextLength() {
        return maxTextLength == null ? 0 : maxTextLength;
    }

    public Integer getMaxTextLengthIgnoreBlank() {
        return maxTextLengthIgnoreBlank == null ? 0 : maxTextLengthIgnoreBlank;
    }

    public Double getAvgTextLength() {
        return count == 0 ? null : sumTextLength * 100.00 / (count * 100);
    }

    public Double getAvgTextLengthIgnoreBlank() {
        return countIgnoreBlank == 0 ? 0 : sumTextLengthIgnoreBlank * 100.00 / (countIgnoreBlank * 100);
    }

    public void setMinTextLength(Integer minTextLength) {
        this.minTextLength = minTextLength;
    }

    public void setMinTextLengthIgnoreBlank(Integer minTextLengthIgnoreBlank) {
        this.minTextLengthIgnoreBlank = minTextLengthIgnoreBlank;
    }

    public void setMaxTextLength(Integer maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    public void setMaxTextLengthIgnoreBlank(Integer maxTextLengthIgnoreBlank) {
        this.maxTextLengthIgnoreBlank = maxTextLengthIgnoreBlank;
    }

    public void setSumTextLength(Integer sumTextLength) {
        this.sumTextLength = sumTextLength;
    }

    public void setSumTextLengthIgnoreBlank(Integer sumTextLengthIgnoreBlank) {
        this.sumTextLengthIgnoreBlank = sumTextLengthIgnoreBlank;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setCountIgnoreBlank(Integer countIgnoreBlank) {
        this.countIgnoreBlank = countIgnoreBlank;
    }

    public Integer getSumTextLength() {
        return sumTextLength;
    }

    public Integer getSumTextLengthIgnoreBlank() {
        return sumTextLengthIgnoreBlank;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getCountIgnoreBlank() {
        return countIgnoreBlank;
    }

}
