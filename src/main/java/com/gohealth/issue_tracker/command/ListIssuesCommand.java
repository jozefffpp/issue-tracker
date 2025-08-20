package com.gohealth.issue_tracker.command;

import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.service.IssueService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Component
@Command(name = "list", description = "List issues by status")
public class ListIssuesCommand implements Runnable {

    @Option(names = {"-s", "--status"}, required = true, description = "Status (OPEN, IN_PROGRESS, CLOSED)")
    private String status;

    private final IssueService issueService;

    // Constructor injection
    public ListIssuesCommand(IssueService issueService) {
        this.issueService = issueService;
    }

    @Override
    public void run() {
        List<Issue> issues = issueService.listIssues(status);
        if (issues.isEmpty()) {
            System.out.println("No issues found with status: " + status);
        } else {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("| ID | Description | Parent ID | Status | Created At | Updated At |");
            System.out.println("-------------------------------------------------------------------");
            issues.forEach(issue -> System.out.println(
                    issue.getId() + " | " +
                            issue.getDescription() + " | " +
                            issue.getParentId() + " | " +
                            issue.getStatus() + " | " +
                            issue.getCreatedAt() + " | " +
                            issue.getUpdatedAt()
            ));
        }
    }
}