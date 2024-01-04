package galeev.prizeservice.service;

import galeev.prizeservice.message.InputFromAuthServiceMessage;

public interface Processor {
    void processRequest(InputFromAuthServiceMessage inputFromWebhookServiceMessage);
}
