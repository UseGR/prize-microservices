package galeev.authservice.service;

import galeev.authservice.message.InputFromWebhookServiceMessage;

public interface Processor {
    void processRequest(InputFromWebhookServiceMessage inputFromWebhookServiceMessage);
}
