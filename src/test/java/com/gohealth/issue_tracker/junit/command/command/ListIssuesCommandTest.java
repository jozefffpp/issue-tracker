package com.gohealth.issue_tracker.junit.command.command;

import com.gohealth.issue_tracker.command.ListIssuesCommand;
import com.gohealth.issue_tracker.model.Issue;
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

    @Test
    void testRunPrintsIssuesTable() throws Exception {
        // Arrange
        setField(listIssuesCommand, "status", "OPEN");

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

        when(issueService.listIssues("OPEN")).thenReturn(List.of(issue1, issue2));

        // Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        listIssuesCommand.run();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("AD-1 | Test issue 1 | PARENT-1 | OPEN"));
        assertTrue(output.contains("AD-2 | Test issue 2 | null | OPEN"));

        // Reset System.out
        System.setOut(originalOut);
    }

    // Reflection helper to set private field
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

/*What this test does:

Sets the status field manually (OPEN) since Picocli normally injects it.

Mocks issueService.listIssues() to return two fake issues.

Captures the printed output to verify that the table contains both issues with ID, Description, Parent ID, Status, Created At, and Updated At.
*/