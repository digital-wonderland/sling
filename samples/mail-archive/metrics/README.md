# Apache Sling mail archive server metrics

Example how to collect & store metrics via [sling-metrics](https://github.com/digital-wonderland/sling-metrics) in [Graphite](http://graphite.wikidot.com/).


## Dependencies

1. apache-mime4j-dom-0.8.0-SNAPSHOT.jar
2. apache-mime4j-core-0.8.0-SNAPSHOT.jar
3. apache-mime4j-mbox-iterator-0.8.0-SNAPSHOT.jar
4. cq-metrics-fragment-0.1.0.jar
5. jackson-annotations-2.2.2.jar
6. jackson-core-2.2.2.jar
7. jackson-databind-2.2.2.jar
8. metrics-core-3.0.1.jar 
9. metrics-graphite-3.0.1.jar
10. metrics-healthchecks-3.0.1.jar
11. metrics-json-3.0.1.jar         
12. metrics-jvm-3.0.1.jar
13. metrics-servlets-3.0.1.jar
14. org.apache.sling.mailarchive.server-0.1.0-SNAPSHOT.jar
15. sling-metrics-bundle-0.1.0.jar

## Configuration

Once all bundles are installed & running, reporting to Graphite must be configured & enabled. 
_Hostname_, _port_ and _prefix_ of your Graphite instance can be configured in the OSGi configuration of ```Sling Metrics :: Reporter :: Graphite```.

## Graphite

Naturally you need a running Graphite instance. The ```nickstenning/graphite``` Docker image is said to be working.

Also keep in mind that all metrics are tracked as [counters](http://metrics.codahale.com/manual/core/#counters). This means to see the difference 
between points in time one has to apply the [nonNegativeDerivative](http://graphite.readthedocs.org/en/latest/functions.html#graphite.render.functions.nonNegativeDerivative) function.

To feed data into Graphite / Carbon it must follow the ```<metric path> <metric value> <metric timestamp>``` [format](http://graphite.readthedocs.org/en/latest/feeding-carbon.html). Then it can be imported i.e. with ```nc```:

```
PORT=2003
SERVER=localhost
echo "sling.metrics.mails.com.example.john.count 3 `date +%s`" | nc ${SERVER} ${PORT};
echo "sling.metrics.mails.com.example.jane.count 5 `date +%s`" | nc ${SERVER} ${PORT};
```

To generate a stacked graph i.e. comparing mails from ```example.com``` vs ```example.org``` use something like the following (assuming Graphite web interface is running at ```localhost:8888```: 
```
http://localhost:8888/render/?width=586&height=308&_salt=1411129768.655&target=nonNegativeDerivative(sling.metrics.*.mails.com.example.count)&target=nonNegativeDerivative(sling.metrics.*.mails.org.example.count)&areaMode=stacked
```
