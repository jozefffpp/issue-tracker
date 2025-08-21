package com.gohealth.issue_tracker.command;

import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.model.IssueStatus;
import com.gohealth.issue_tracker.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "update", description = "Update an existing issue")
public class UpdateIssueCommand implements Runnable {

    @Option(names = {"-i", "--id"}, required = true, description = "Issue ID")
    private String id;

    // Change String to IssueStatus
    @Option(names = {"-s", "--status"}, required = true, description = "New status (OPEN, IN_PROGRESS, CLOSED)")
    private IssueStatus status;

    @Autowired
    private IssueService issueService;

    public UpdateIssueCommand(IssueService issueService) {
        this.issueService = issueService;
    }

    // These public setters are required for unit testing the command class.
    // They are not used by the picocli framework, which injects the values
    // directly from the command line arguments.
    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    @Override
    public void run() {
        Issue issue = issueService.updateIssue(id, status.name());
        if (issue != null) {
            System.out.println("Updated issue: " + id + " to " + status);
        } else {
            // Throw an exception to signal an error
            throw new RuntimeException("Issue not found: " + id);
        }
    }
}