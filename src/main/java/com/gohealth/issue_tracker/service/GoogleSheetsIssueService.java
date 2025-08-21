package com.gohealth.issue_tracker.service;

import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.model.IssueStatus;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class GoogleSheetsIssueService implements IssueService {

    private final Sheets sheetsService;
    private final String SHEET_NAME = "Sheet1";
    private final String SPREADSHEET_ID = "1tcWPle1hz2XBf7iZv2lcruIj4O7FRo6w5mUHUiu8FYU";
    private final String RANGE = "Sheet1!A:F"; // A: ID, B: Description, C: Parent ID, D: Status, E: Created at, F: Updated at

    public GoogleSheetsIssueService() throws GeneralSecurityException, IOException {
        sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                getCredentials()
        ).setApplicationName("Issue Tracker CLI").build();
    }

    private Credential getCredentials() throws IOException {
        InputStream in = GoogleSheetsIssueService.class.getResourceAsStream("/credentials.json");
        return GoogleCredential.fromStream(in).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    }

    // NEW: Method to check if an issue exists
    private boolean issueExists(String issueId) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
        List<List<Object>> values = response.getValues();
        if (values != null && values.size() > 1) {
            for (int i = 1; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (row.get(0).toString().equals(issueId)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Method to get the next issue ID from a dedicated counter cell
    private String getNextIdFromCounter() throws IOException {
        String counterRange = "Counters!A1";
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, counterRange).execute();
        List<List<Object>> values = response.getValues();
        int counter = 1;
        if (values != null && !values.isEmpty()) {
            counter = Integer.parseInt(values.get(0).get(0).toString());
        }

        ValueRange nextValue = new ValueRange().setValues(List.of(List.of(counter + 1)));
        sheetsService.spreadsheets().values().update(SPREADSHEET_ID, counterRange, nextValue)
                .setValueInputOption("RAW").execute();

        return "AD-" + counter;
    }

    @Override
    public Issue createIssue(String description, String parentId) {
        try {
            // Validate parent ID
            if (parentId != null && !issueExists(parentId)) {
                throw new RuntimeException("Parent issue not found with ID: " + parentId);
            }

            String newId = getNextIdFromCounter();

            ValueRange body = new ValueRange()
                    .setValues(List.of(List.of(
                            newId,
                            description,
                            parentId,
                            IssueStatus.OPEN.name(),
                            LocalDateTime.now().toString(),
                            LocalDateTime.now().toString()
                    )));
            sheetsService.spreadsheets().values().append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            return new Issue(newId, description, parentId, IssueStatus.OPEN.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Issue updateIssue(String id, String status) {
        try {
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
            List<List<Object>> values = response.getValues();
            if (values != null && values.size() > 1) {
                for (int i = 1; i < values.size(); i++) {
                    List<Object> row = values.get(i);
                    if (row.get(0).equals(id)) {
                        String rangeToUpdate = SHEET_NAME + "!D" + (i + 1) + ":F" + (i + 1);

                        ValueRange updatedValues = new ValueRange()
                                .setValues(List.of(List.of(status, LocalDateTime.now().toString())));

                        sheetsService.spreadsheets().values().update(SPREADSHEET_ID, rangeToUpdate, updatedValues)
                                .setValueInputOption("RAW")
                                .execute();

                        return new Issue(id, (String) row.get(1), (String) row.get(2), status);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Issue> listIssues(String status) {
        List<Issue> issues = new ArrayList<>();
        try {
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
            List<List<Object>> values = response.getValues();
            if (values != null && values.size() > 1) {
                for (int i = 1; i < values.size(); i++) { // skip header
                    List<Object> row = values.get(i);
                    if (row.get(3).toString().equalsIgnoreCase(status)) {
                        Issue issue = new Issue();
                        issue.setId((String) row.get(0));
                        issue.setDescription((String) row.get(1));
                        issue.setParentId((String) row.get(2));
                        issue.setStatus((String) row.get(3));

                        try {
                            issue.setCreatedAt(LocalDateTime.parse((String) row.get(4)));
                        } catch (DateTimeParseException | NullPointerException e) {
                            System.err.println("Warning: Could not parse Created At date for issue " + issue.getId());
                        }

                        try {
                            issue.setUpdatedAt(LocalDateTime.parse((String) row.get(5)));
                        } catch (DateTimeParseException | NullPointerException e) {
                            System.err.println("Warning: Could not parse Updated At date for issue " + issue.getId());
                        }

                        issues.add(issue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return issues;
    }
}