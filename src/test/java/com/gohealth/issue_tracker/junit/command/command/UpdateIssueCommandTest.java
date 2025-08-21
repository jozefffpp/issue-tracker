package com.gohealth.issue_tracker.junit.command.command;

import com.gohealth.issue_tracker.command.UpdateIssueCommand;
import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.model.IssueStatus;
import com.gohealth.issue_tracker.service.IssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateIssueCommandTest {

    @Mock
    private IssueService issueService;

    @InjectMocks
    private UpdateIssueCommand updateIssueCommand;

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testRunIssueFound() {
        // Arrange
        String issueId = "AD-1";
        IssueStatus newStatus = IssueStatus.CLOSED;
        Issue updatedIssue = new Issue(issueId, "Sample description", null, newStatus.name());
        when(issueService.updateIssue(issueId, newStatus.name())).thenReturn(updatedIssue);
        updateIssueCommand.setId(issueId);
        updateIssueCommand.setStatus(newStatus);

        // Act
        updateIssueCommand.run();

        // Assert
        verify(issueService).updateIssue(issueId, newStatus.name());
        // You could add assertions on the output, but the primary purpose of this test is to verify the method call.
    }

    @Test
    void testRunIssueNotFound() {
        // Arrange
        String issueId = "AD-999";
        IssueStatus newStatus = IssueStatus.CLOSED;
        when(issueService.updateIssue(issueId, newStatus.name())).thenReturn(null);
        updateIssueCommand.setId(issueId);
        updateIssueCommand.setStatus(newStatus);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> updateIssueCommand.run());
        verify(issueService).updateIssue(issueId, newStatus.name());
    }
}
