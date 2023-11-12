package galeev.authservice.service;

import galeev.authservice.message.InputMessage;
import lombok.SneakyThrows;

public interface Processor {
    void processRequest(InputMessage inputMessage);
}
