package com.gohealth.issue_tracker.junit.command.command;

import com.gohealth.issue_tracker.command.ListIssuesCommand;
import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.model.IssueStatus;
import com.gohealth.issue_tracker.service.IssueService;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListIssuesCommandTest {

    @Mock
    private IssueService issueService;

    @InjectMocks
    private ListIssuesCommand listIssuesCommand;

    // Reflection helper to set private field
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testRunPrintsIssuesTable() throws Exception {
        // Arrange
        setField(listIssuesCommand, "status", IssueStatus.OPEN);

        Issue issue1 = new Issue();
        issue1.setId("AD-1");
        issue1.setDescription("Test issue 1");
        issue1.setParentId("PARENT-1");
        issue1.setStatus("OPEN");
        issue1.setCreatedAt(LocalDateTime.of(2025, 8, 20, 10, 0));
        issue1.setUpdatedAt(LocalDateTime.of(2025, 8, 21, 11, 0));

        Issue issue2 = new Issue();
        issue2.setId("AD-2");
        issue2.setDescription("Test issue 2");
        issue2.setParentId(null);
        issue2.setStatus("OPEN");
        issue2.setCreatedAt(LocalDateTime.of(2025, 8, 19, 9, 30));
        issue2.setUpdatedAt(LocalDateTime.of(2025, 8, 20, 12, 0));

        when(issueService.listIssues(IssueStatus.OPEN.name())).thenReturn(List.of(issue1, issue2));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Act
            listIssuesCommand.run();

            // Assert
            String output = outContent.toString();

            // Define the format string again to build the expected output dynamically
            String format = "| %-10s | %-30s | %-15s | %-15s | %-20s | %-20s |";

            // Build the expected strings with the same formatting logic as the command
            String expectedLine1 = String.format(
                    format,
                    issue1.getId(),
                    issue1.getDescription(),
                    issue1.getParentId(),
                    issue1.getStatus(),
                    issue1.getCreatedAt(),
                    issue1.getUpdatedAt()
            );

            String expectedLine2 = String.format(
                    format,
                    issue2.getId(),
                    issue2.getDescription(),
                    "null", // Special handling for null parentId
                    issue2.getStatus(),
                    issue2.getCreatedAt(),
                    issue2.getUpdatedAt()
            );

            // Now assert that the output contains the exact expected strings
            assertTrue(output.contains(expectedLine1));
            assertTrue(output.contains(expectedLine2));

        } finally {
            // Reset System.out
            System.setOut(originalOut);
        }
    }
}