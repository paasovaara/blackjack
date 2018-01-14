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


        m_lastTickTimestamp = System.currentTimeMillis();
        m_root.initialize(m_context);

        Thread readerThread = new Thread() {
            public void run() {
                System.out.println("Starting the game, press any key to quit...");
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AIGameExecutor.this.stop();
            }
        };
        readerThread.setDaemon(true);
        readerThread.start();

        Thread t = new Thread() {
            public void run() {
                while (m_running) {
                    tickNode();
                }
            }
        };
        t.start();
    }

    @Override
    public void stop() {
        m_running = false;
    }
}
