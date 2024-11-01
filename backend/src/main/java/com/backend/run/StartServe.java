package com.backend.run;

import com.backend.netty.SocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartServe implements CommandLineRunner {

    private final SocketServer socketServer;

    @Override
    public void run(String... args) throws Exception {
        socketServer.start();
    }
}
