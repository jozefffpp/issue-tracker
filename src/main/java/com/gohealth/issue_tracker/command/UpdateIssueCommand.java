package com.gohealth.issue_tracker.command;

import com.gohealth.issue_tracker.model.Issue;
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

    @Option(names = {"-s", "--status"}, required = true, description = "New status (OPEN, IN_PROGRESS, CLOSED)")
    private String status;

    @Autowired
    private IssueService issueService;

    @Override
    public void run() {
        Issue issue = issueService.updateIssue(id, status);
        if (issue != null) {
            System.out.println("Updated issue: " + id + " to " + status);
        } else {
            System.out.println("Issue not found: " + id);
        }
    }
}
