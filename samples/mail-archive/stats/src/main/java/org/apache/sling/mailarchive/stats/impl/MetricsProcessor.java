package org.apache.sling.mailarchive.stats.impl;

import com.codahale.metrics.Counter;
import com.github.digital_wonderland.sling_metrics.service.MetricService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.sling.mailarchiveserver.api.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
public class MetricsProcessor implements MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsProcessor.class);

    @Reference
    private MetricService metricService;

    @Override
    public void processMessage(final Message msg) {
        LOG.debug("Processing message [{}]", msg.getMessageId());

        for(Mailbox mailbox : msg.getFrom()) {
            final String from = mailbox.getAddress();

            final String user = getUser(from);
            final String reverseOrg = getReversedOrganization(from);
            final String tld = getTld(from);

            getTldCounter(tld).inc();
            getOrganizationCounter(tld, reverseOrg).inc();
            getUserCounter(tld, reverseOrg, user).inc();

            LOG.debug("FROM: [{}] User: [{}] Org: [{}] Tld: [{}]", new Object[] {from, user, reverseOrg, tld});
        }
    }

    private String getUser(final String sender) {
        return sender.split("@")[0];
    }

    private Counter getUserCounter(final String tld, final String org, final String user) {
        final String name = metricService.name(tld, org, user);
        return metricService.counter(name);
    }

    private String getTld(final String sender) {
        return sender.substring(sender.lastIndexOf('.') + 1);
    }

    private Counter getTldCounter(final String tld) {
        final String name = metricService.name(tld);
        return metricService.counter(name);
    }

    private String getReversedOrganization(final String sender) {
        String s = sender.split("@")[1];
        s = s.substring(0, s.lastIndexOf('.'));
        String[] org = s.split("\\.");
        ArrayUtils.reverse(org);
        return StringUtils.join(org, '.');
    }

    private Counter getOrganizationCounter(final String tld, final String org) {
        final String name = metricService.name(tld, org);
        return metricService.counter(name);
    }

}
