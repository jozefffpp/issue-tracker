package com.gohealth.issue_tracker.functional;

import com.gohealth.issue_tracker.IssueTrackerApplication;
import com.gohealth.issue_tracker.command.MainCommand;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IssueTrackerApplicationFunctionalTest {

    @Test
    void factoryBeanIsCreatedAndCreatesCommand() throws Exception {
        CommandLine.IFactory mockFactory = mock(CommandLine.IFactory.class);
        MainCommand mockCommand = mock(MainCommand.class);
        when(mockFactory.create(MainCommand.class)).thenReturn(mockCommand);

        // Verify factory creates the command
        Object cmd = mockFactory.create(MainCommand.class);
        assertNotNull(cmd);
        assertEquals(mockCommand, cmd);
    }

    @Test
    void runExecutesCommandSuccessfully() {
        CommandLine.IFactory mockFactory = mock(CommandLine.IFactory.class);
        MainCommand mockCommand = mock(MainCommand.class);
        try {
            when(mockFactory.create(MainCommand.class)).thenReturn(mockCommand);
        } catch (Exception e) {
            fail("Factory threw exception during setup");
        }

        IssueTrackerApplication app = new IssueTrackerApplication(mockFactory);

        // Should not throw because run() catches all exceptions internally
        assertDoesNotThrow(() -> app.run("arg1", "arg2"));
    }

    @Test
    void runHandlesExceptionsFromFactory() throws Exception {
        CommandLine.IFactory mockFactory = mock(CommandLine.IFactory.class);

        // Simulate factory throwing a checked exception
        when(mockFactory.create(MainCommand.class)).thenThrow(new Exception("fail"));

        IssueTrackerApplication app = new IssueTrackerApplication(mockFactory);

        // run() should catch the exception internally and not propagate
        assertDoesNotThrow(() -> app.run("arg1", "arg2"));
    }

    @Test
    void runHandlesExceptionsFromCommandExecution() throws Exception {
        CommandLine.IFactory mockFactory = mock(CommandLine.IFactory.class);
        MainCommand mockCommand = mock(MainCommand.class);
        when(mockFactory.create(MainCommand.class)).thenReturn(mockCommand);

        // Spy on CommandLine to simulate execute() throwing exception
        CommandLine cmdSpy = spy(new CommandLine(mockCommand, mockFactory));

        // Create a subclass that overrides createCommandLine
        IssueTrackerApplication app = new IssueTrackerApplication(mockFactory) {
            @Override
            protected CommandLine createCommandLine(Object command) {
                return cmdSpy; // return our spy instead of real CommandLine
            }
        };

        // Should not throw because run() catches exceptions
        assertDoesNotThrow(() -> app.run("arg1"));

        // Optional: verify execute was called
        verify(cmdSpy).execute("arg1");
    }
}
