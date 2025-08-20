package com.gohealth.issue_tracker.repository;


import com.gohealth.issue_tracker.model.Issue;

import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    Issue save(Issue issue);
    Optional<Issue> findById(String id);
    //List<Issue> findByStatus(Status status);
}
