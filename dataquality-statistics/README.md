Data Quality - Statistics API
===================
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This module contains API for statistical analysis. 
This API can be built with [Apache Maven](http://maven.apache.org/): (JDK 8 is required)

    mvn clean install

## Packages Information

| Package Name         | Function Description     |
|----------------------|--------------------------|
| org.talend.dataquality.statistics.quality | Value quality statistics: count the number of type Valid/Invalid and Empty values|
| org.talend.dataquality.statistics.numeric | Statistics for numerical values: Summary statistics(include: Min/Max/Mean/Variance/Sum), Quantile, Histogram | 
| org.talend.dataquality.statistics.frequency | Statistics for Value Frequency and Pattern Frequency|
| org.talend.dataquality.statistics.cardinality | Count the number of Distinct and Duplicate values|


## Dependencies
This project depends on another Data Quality project: [org.talend.datascience.common](https://github.com/Talend/tdq-studio-ee/tree/master/main/plugins/org.talend.datascience.common).

The *summary statistics* in this API depends on [Apache Commons Math3](http://commons.apache.org/proper/commons-math/userguide/stat.html) statistics package, using the class [SummaryStatistics](http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/descriptive/SummaryStatistics.html) which does not store the input data values in memory, so the statistics can be computed in one pass through the data.

This API uses the Java library [stream-lib](https://github.com/addthis/stream-lib) for computing: the *cardinality estimation* (algorithm: [HyperLogLog](https://en.wikipedia.org/wiki/HyperLogLog)), the *approximate frequency counts* (based on [Count–min sketch](https://en.wikipedia.org/wiki/Count%E2%80%93min_sketch) or [Space-Saving Algorithm](https://icmi.cs.ucsb.edu/research/tech_reports/reports/2005-23.pdf)) and the *quantiles estimation* (algorithm: [Dunning's T-Digest](https://github.com/tdunning/t-digest/blob/master/docs/t-digest-paper/histo.pdf)).

## License
Copyright (c) 2006-2015 Talend