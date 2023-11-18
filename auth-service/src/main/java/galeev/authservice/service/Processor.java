package galeev.authservice.service;

import galeev.authservice.message.InputMessage;

public interface Processor {
    void processRequest(InputMessage inputMessage);
}
