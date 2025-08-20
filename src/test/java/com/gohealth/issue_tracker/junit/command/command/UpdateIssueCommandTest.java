package com.gohealth.issue_tracker.junit.command.command;

import com.gohealth.issue_tracker.command.UpdateIssueCommand;
import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.service.IssueService;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateIssueCommandTest {

    @Mock
    private IssueService issueService;

    @InjectMocks
    private UpdateIssueCommand updateIssueCommand;

    @Test
    void testRunUpdatesExistingIssue() throws Exception {
        // Arrange
        setField(updateIssueCommand, "id", "AD-123");
        setField(updateIssueCommand, "status", "CLOSED");

        Issue fakeIssue = new Issue();
        fakeIssue.setId("AD-123");
        fakeIssue.setStatus("CLOSED");

        when(issueService.updateIssue("AD-123", "CLOSED")).thenReturn(fakeIssue);

        // Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        updateIssueCommand.run();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Updated issue: AD-123 to CLOSED"));

        // Reset System.out
        System.setOut(originalOut);
    }

    @Test
    void testRunIssueNotFound() throws Exception {
        // Arrange
        setField(updateIssueCommand, "id", "AD-999");
        setField(updateIssueCommand, "status", "OPEN");

        when(issueService.updateIssue("AD-999", "OPEN")).thenReturn(null);

        // Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        updateIssueCommand.run();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Issue not found: AD-999"));

        // Reset System.out
        System.setOut(originalOut);
    }

    // Reflection helper to set private fields
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

/* What this test covers:

Updates an existing issue successfully → prints Updated issue: ....

Handles the case when the issue is not found → prints Issue not found: ....

Uses reflection to set id and status since Picocli normally injects them.

Mocks the IssueService to simulate behavior.*/