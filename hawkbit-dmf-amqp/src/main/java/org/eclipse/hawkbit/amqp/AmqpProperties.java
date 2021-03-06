/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.amqp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bean which holds the necessary properties for configuring the AMQP
 * connection.
 * 
 *
 *
 *
 */
@ConfigurationProperties("hawkbit.dmf.rabbitmq")
public class AmqpProperties {

    private String deadLetterQueue = "dmf_connector_deadletter";
    private String deadLetterExchange = "dmf.connector.deadletter";
    private String receiverQueue = "dmf_receiver";

    /**
     * Returns the dead letter exchange.
     * 
     * @return dead letter exchange
     */
    public String getDeadLetterExchange() {
        return deadLetterExchange;
    }

    /**
     * Sets the dead letter exchange.
     * 
     * @param deadLetterExchange
     *            the deadLetterExchange to be set
     */
    public void setDeadLetterExchange(final String deadLetterExchange) {
        this.deadLetterExchange = deadLetterExchange;
    }

    /**
     * Returns the dead letter queue.
     * 
     * @return the dead letter queue
     */
    public String getDeadLetterQueue() {
        return deadLetterQueue;
    }

    /**
     * Sets the dead letter queue.
     * 
     * @param deadLetterQueue
     *            the deadLetterQueue ro be set
     */
    public void setDeadLetterQueue(final String deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }

    public String getReceiverQueue() {
        return receiverQueue;
    }

    public void setReceiverQueue(final String receiverQueue) {
        this.receiverQueue = receiverQueue;
    }
}
