package com.gohealth.issue_tracker.junit.command.command;

import com.gohealth.issue_tracker.command.CreateIssueCommand;
import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.service.IssueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CreateIssueCommandTest {

    @Mock
    private IssueService issueService;

    @InjectMocks
    private CreateIssueCommand createIssueCommand;

    @Test
    void testRunCallsServiceAndPrintsOutput() {
        // Arrange
        setField(createIssueCommand, "description", "Test issue");
        setField(createIssueCommand, "parentId", "PARENT-1");

        Issue fakeIssue = new Issue();
        fakeIssue.setId("AD-123");
        when(issueService.createIssue("Test issue", "PARENT-1")).thenReturn(fakeIssue);

        // Capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Act
        createIssueCommand.run();

        // Assert
        verify(issueService).createIssue("Test issue", "PARENT-1");
        assertTrue(outContent.toString().contains("Created issue: AD-123"));

        // Reset System.out
        System.setOut(originalOut);
    }
}

/*What it does:

Mocks IssueService.

Injects the mock into CreateIssueCommand.

Sets description and parentId like they would be provided by CLI.

Verifies createIssue was called with correct arguments.

Captures System.out and asserts that the correct output is printed.*/