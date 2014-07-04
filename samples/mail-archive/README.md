Apache Sling mail archive server sample app
==========================================

This is a work in progress sample application for Sling, initially
contributed by Igor Bogomolov who wrote it as part of an internship,
and meant to create a useful mail archive server that also serves
as a more complex Sling sample.

To run this, and install the bundles provided by this module in a 
Sling Launchpad (trunk) instance.

Then, start at http://localhost:8080/mailarchiveserver/import.mbox.html
to import a few mbox files, you can find some at 
server/src/test/resources/test-files/mbox/

http://localhost:8080/mailarchiveserver.html is the server's homepage.

To import live mail from a mail server you need a Connector - the SLING-3297
contribution does include one for Exchange, but we haven't included it
in this sample so far.

Stats (even more work in progress) are available at 
http://localhost:8080/content/mailarchiveserver/stats.html



## Sling Metrics integration

1. Start the VM with ```vagrant up``` (the VM is based on [this](https://github.com/digital-wonderland/packer-templates/tree/master/CentOS-6-x86_64) template but any CentOS 6 with EPEL enabled should do fine)
2. Verify [Graphite](http://graphite.wikidot.com/) is available at [localhost:8888](http://localhost:8888/) (if you get a 500 ssh into the VM with ```vagrant ssh``` and restart httpd: ```sudo service httpd restart``` - happens sometimes due to a race condition in the httpd / graphite integration)
3. Install ```sling-metrics-bundle``` and its dependencies
4. Enable the Graphite reporter in the OSGi configuration
5. Enjoy

Please keep in mind that everything is tracked as [Counter](http://metrics.codahale.com/manual/core/#counters). 
This means to see the difference between points in time one has to apply the [nonNegativeDerivative](http://graphite.readthedocs.org/en/latest/functions.html#graphite.render.functions.nonNegativeDerivative) function.