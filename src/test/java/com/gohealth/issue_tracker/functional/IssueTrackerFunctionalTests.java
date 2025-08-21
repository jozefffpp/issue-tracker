package com.gohealth.issue_tracker.functional;


import com.gohealth.issue_tracker.command.CreateIssueCommand;
import com.gohealth.issue_tracker.command.ListIssuesCommand;
import com.gohealth.issue_tracker.command.MainCommand;
import com.gohealth.issue_tracker.command.UpdateIssueCommand;
import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.service.GoogleSheetsIssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueTrackerFunctionalTests {

    @Mock
    private GoogleSheetsIssueService sheetsService;

    private CommandLine cmd;

    @BeforeEach
    void setup() {

        MainCommand main = new MainCommand(); // annotation-based

        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            public <T> T create(Class<T> cls) throws Exception {
                if (cls == CreateIssueCommand.class) return cls.cast(new CreateIssueCommand(sheetsService));
                if (cls == UpdateIssueCommand.class) return cls.cast(new UpdateIssueCommand(sheetsService));
                if (cls == ListIssuesCommand.class) return cls.cast(new ListIssuesCommand(sheetsService));
                return cls.getDeclaredConstructor().newInstance();
            }
        };

        cmd = new CommandLine(main, factory); // this is enough
    }

    // ---------- CREATE ISSUE ----------

    @Test
    void createIssueWithDescription() {
        Issue mockIssue = new Issue("AD-1", "Job is failing", null, "OPEN");
        when(sheetsService.createIssue(anyString(), isNull())).thenReturn(mockIssue);

        int exitCode = cmd.execute("create", "--description", "Job is failing");
        assertEquals(0, exitCode);
        verify(sheetsService).createIssue("Job is failing", null);
    }

    @Test
    void createIssueWithParentId() {
        Issue mockIssue = new Issue("AD-1", "Dependent job", "AD-1", "OPEN");
        when(sheetsService.createIssue(anyString(), anyString())).thenReturn(mockIssue);

        int exitCode = cmd.execute("create",
                "--description", "Dependent job",
                "--parentId", "AD-1");

        assertEquals(0, exitCode);
        verify(sheetsService).createIssue("Dependent job", "AD-1");
    }

    @Test
    void createIssueFailsWithoutDescription() {
        int exitCode = cmd.execute("create");

        assertEquals(2, exitCode);
        verifyNoInteractions(sheetsService);
    }

    // NEW: Verify that creating an issue with an invalid parentId fails
    @Test
    void createIssueFailsOnNonexistentParentId() {
        // Arrange
        when(sheetsService.createIssue(anyString(), anyString())).thenReturn(null);

        // Act
        int exitCode = cmd.execute("create",
                "--description", "Dependent job",
                "--parentId", "INVALID-ID");

        // Assert
        assertEquals(1, exitCode);
    }

    // ---------- UPDATE ISSUE ----------

    @Test
    void updateIssueStatus() {
        Issue updatedIssue = new Issue("AD-1", "Job is failing", null, "CLOSED");
        when(sheetsService.updateIssue(anyString(), anyString())).thenReturn(updatedIssue);

        int exitCode = cmd.execute("update",
                "--id", "AD-1",
                "--status", "CLOSED");

        assertEquals(0, exitCode);
        verify(sheetsService).updateIssue("AD-1", "CLOSED");
    }

    @Test
    void updateIssueFailsWithInvalidStatus() {
        int exitCode = cmd.execute("update",
                "--id", "AD-1",
                "--status", "INVALID");

        assertEquals(2, exitCode);
        verifyNoInteractions(sheetsService);
    }

    // NEW: Verify that updating a nonexistent issue fails
    @Test
    void updateIssueFailsWithNonexistentId() {
        // Arrange
        when(sheetsService.updateIssue(anyString(), anyString())).thenReturn(null);

        // Act
        int exitCode = cmd.execute("update",
                "--id", "AD-999",
                "--status", "OPEN");

        // Assert
        assertEquals(1, exitCode);
    }

    // ---------- LIST ISSUES ----------

    @Test
    void listOpenIssues() {
        when(sheetsService.listIssues("OPEN")).thenReturn(List.of(
                new Issue("AD-1", "Issue 1", null, "OPEN"),
                new Issue("AD-2", "Issue 2", null, "OPEN")
        ));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            int exitCode = cmd.execute("list", "--status", "OPEN");
            assertEquals(0, exitCode);
            verify(sheetsService).listIssues("OPEN");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void listIssuesFailsWithInvalidStatus() {
        int exitCode = cmd.execute("list", "--status", "INVALID");

        assertEquals(2, exitCode);
        verifyNoInteractions(sheetsService);
    }

    // Verify that listing issues for a valid status returns a message when no issues are found
    @Test
    void listIssuesSucceedsWithNoIssuesFound() {
        // Arrange
        when(sheetsService.listIssues("CLOSED")).thenReturn(List.of());

        // Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Act
            int exitCode = cmd.execute("list", "--status", "CLOSED");

            // Assert
            assertEquals(0, exitCode);
            verify(sheetsService).listIssues("CLOSED");

            String output = outContent.toString();
            assertEquals("No issues found with status: CLOSED\n", output);

        } finally {
            // Reset System.out
            System.setOut(originalOut);
        }
    }
}

