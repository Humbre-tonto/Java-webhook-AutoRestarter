package com.internaldev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebhookListenerTest {

    @Mock
    private RemoteCommandExecutor commandExecutor;

    @InjectMocks
    private WebhookListener webhookListener;

    @Test
    void whenJBossIsDown_thenJBossScriptIsExecuted() throws Exception {
        webhookListener.handleDownEvent("JBoss Server");
        verify(commandExecutor).executeCommand("/app/scripts/startJBoss.sh");
    }

    @Test
    void whenMuleIsDown_thenMuleScriptIsExecuted() throws Exception {
        webhookListener.handleDownEvent("Mule Server");
        verify(commandExecutor).executeCommand("/app/scripts/startMule.sh");
    }

    @Test
    void whenTomcatIsDown_thenTomcatScriptsAreExecuted() throws Exception {
        webhookListener.handleDownEvent("Tomcat Server");
        verify(commandExecutor).executeCommand("/app/scripts/startTomcat.sh && /app/scripts/startSecondaryTomcat.sh");
    }

    @Test
    void whenUnknownServiceIsDown_thenNoScriptIsExecuted() throws Exception {
        webhookListener.handleDownEvent("Unknown Service");
        verify(commandExecutor, never()).executeCommand(anyString());
    }
}
