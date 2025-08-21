package com.gohealth.issue_tracker;
import com.gohealth.issue_tracker.command.MainCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IssueTrackerApplicationTests {

    @Autowired
    private ApplicationContext context;
    @Autowired
    @Qualifier("picocliSpringFactory")
    private CommandLine.IFactory factory;

    /**
     * Test that Spring context loads correctly
     */
    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    /**
     * Test that the picocliSpringFactory bean exists and works
     */
    @Test
    void factoryBeanIsCreated() throws Exception {
        assertNotNull(factory);

        // Create a command using the factory
        Object cmd = factory.create(MainCommand.class);
        assertNotNull(cmd);
    }

    /**
     * Unit test run() method: calls execute() with args
     */
    @Test
    void runExecutesCommandLine() throws Exception {
        CommandLine.IFactory mockFactory = mock(CommandLine.IFactory.class);
        MainCommand mainCommand = mock(MainCommand.class);
        when(mockFactory.create(MainCommand.class)).thenReturn(mainCommand);

        IssueTrackerApplication app = new IssueTrackerApplication(mockFactory);

        app.run("arg1", "arg2");

        verify(mockFactory).create(MainCommand.class);
    }

    /**
     * Unit test run() handles exceptions gracefully
     */
    @Test
    void runHandlesExceptions() throws Exception {
        CommandLine.IFactory mockFactory = mock(CommandLine.IFactory.class);
        when(mockFactory.create(MainCommand.class)).thenThrow(new Exception("fail"));

        IssueTrackerApplication app = new IssueTrackerApplication(mockFactory);

        assertDoesNotThrow(() -> app.run("arg1", "arg2"));
    }
}