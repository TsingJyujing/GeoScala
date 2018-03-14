# Cluster Manual

## Difference between common algorithms
There're many implementation of DBScan, K-Means and other cluster algorithms, but most of them are used in Euclid space.
In this library, some algorithms are reimplemented for Geographical using.

There's an obvious difference between Euclid space and 2-sphere, the measurement of distance are not same.
In 