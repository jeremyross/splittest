package org.jeremyross;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class SplitTest extends CamelTestSupport {

    @Test
    public void test() throws InterruptedException {
        final MockEndpoint mock = context.getEndpoint("mock:parentComplete", MockEndpoint.class);
        mock.expectedMessageCount(1);

        // give it time to see if too many messages are sent
        Thread.sleep(5000);

        mock.assertIsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("scheduler:testBug?initialDelay=1000&useFixedDelay=true&delay=60000&greedy=true")
                    .multicast().parallelProcessing()
                        .log("test")
                    .end()

                    // this should result in the scheduler waiting its delay period before sending
                    // another exchange
                    .setProperty(Exchange.SCHEDULER_POLLED_MESSAGES, constant(false))
                    .to("mock:parentComplete");
            }
        };
    }
}
