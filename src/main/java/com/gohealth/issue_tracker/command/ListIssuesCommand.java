package com.gohealth.issue_tracker.command;

import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.model.IssueStatus;
import com.gohealth.issue_tracker.service.IssueService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Component
@Command(name = "list", description = "List issues by status")
public class ListIssuesCommand implements Runnable {

    @Option(names = {"-s", "--status"}, required = true, description = "Status (OPEN, IN_PROGRESS, CLOSED)")
    private IssueStatus status;

    private final IssueService issueService;

    // Constructor injection
    public ListIssuesCommand(IssueService issueService) {
        this.issueService = issueService;
    }

    @Override
    public void run() {
        List<Issue> issues = issueService.listIssues(status.name());
        if (issues.isEmpty()) {
            System.out.println("No issues found with status: " + status);
        } else {
            // fixed format string for consistent column widths
            String format = "| %-10s | %-30s | %-15s | %-15s | %-20s | %-20s |%n";

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf(format, "ID", "Description", "Parent ID", "Status", "Created At", "Updated At");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");

            issues.forEach(issue -> System.out.printf(
                    format,
                    issue.getId(),
                    issue.getDescription(),
                    issue.getParentId() != null ? issue.getParentId() : "null", // Handle null Parent ID
                    issue.getStatus(),
                    issue.getCreatedAt(),
                    issue.getUpdatedAt()
            ));
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        }
    }
}