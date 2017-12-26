package blackjack.engine;

import behave.execution.Executor;

import java.io.IOException;

public class AIGameExecutor extends Executor {
    private boolean m_running = false;

    @Override
    public void start(long period, long delay) {
        if (m_running) {
            throw new RuntimeException("Already running");
        }
        m_running = true;
        System.out.println("Starting the game, press any key to quit...");

        m_lastTickTimestamp = System.currentTimeMillis();
        m_root.initialize(m_context);

        Thread t = new Thread() {
            public void run() {
                while (m_running) {
                    tickNode();
                }
            }
        };
        t.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stop();
    }

    @Override
    public void stop() {
        m_running = false;
    }
}
