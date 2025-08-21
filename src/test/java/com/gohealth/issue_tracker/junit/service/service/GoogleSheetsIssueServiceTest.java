package com.gohealth.issue_tracker.junit.service.service;

import com.gohealth.issue_tracker.model.Issue;
import com.gohealth.issue_tracker.service.GoogleSheetsIssueService;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleSheetsIssueServiceTest {

    private GoogleSheetsIssueService service;
    private Sheets sheetsMock;
    private Sheets.Spreadsheets spreadsheetsMock;
    private Sheets.Spreadsheets.Values valuesMock;

    @BeforeEach
    void setUp() throws Exception {
        sheetsMock = mock(Sheets.class);
        spreadsheetsMock = mock(Sheets.Spreadsheets.class);
        valuesMock = mock(Sheets.Spreadsheets.Values.class);

        when(sheetsMock.spreadsheets()).thenReturn(spreadsheetsMock);
        when(spreadsheetsMock.values()).thenReturn(valuesMock);

        // Use a constructor or reflection to inject the mock Sheets service
        service = new GoogleSheetsIssueService();
    }


    //Value "2" has to be set in google "Counter" sheet at "A1" position. Otherwise the test will not pass.
    /*@Test
    void testCreateIssue() throws IOException {
        ValueRange existingIds = new ValueRange().setValues(Arrays.asList(
                Arrays.asList("ID"),   // header
                Arrays.asList("AD-1")  // existing ID
        ));

        // Mock Get
        Sheets.Spreadsheets.Values.Get getMock = mock(Sheets.Spreadsheets.Values.Get.class);
        when(valuesMock.get(anyString(), anyString())).thenReturn(getMock);
        when(getMock.execute()).thenReturn(existingIds);

        // Mock Append
        Sheets.Spreadsheets.Values.Append appendMock = mock(Sheets.Spreadsheets.Values.Append.class);
        when(valuesMock.append(anyString(), anyString(), any(ValueRange.class))).thenReturn(appendMock);
        when(appendMock.setValueInputOption(anyString())).thenReturn(appendMock);
        AppendValuesResponse appendResponse = new AppendValuesResponse();
        when(appendMock.execute()).thenReturn(appendResponse);

        Issue issue = service.createIssue("Test description", "AD-1");

        assertNotNull(issue);
        assertEquals("Test description", issue.getDescription());
        assertEquals("OPEN", issue.getStatus());
        assertEquals("AD-2", issue.getId());
    }
*/
    @Test
    void testUpdateIssue() throws IOException {
        List<Object> row = Arrays.asList("AD-1", "Old desc", "AD-0", "OPEN", LocalDateTime.now().toString(), LocalDateTime.now().toString());
        ValueRange response = new ValueRange().setValues(Arrays.asList(
                Arrays.asList("ID","Desc","Parent","Status","Created","Updated"), // header
                row
        ));

        // Mock Get
        Sheets.Spreadsheets.Values.Get getMock = mock(Sheets.Spreadsheets.Values.Get.class);
        when(valuesMock.get(anyString(), anyString())).thenReturn(getMock);
        when(getMock.execute()).thenReturn(response);

        // Mock Update
        Sheets.Spreadsheets.Values.Update updateMock = mock(Sheets.Spreadsheets.Values.Update.class);
        when(valuesMock.update(anyString(), anyString(), any(ValueRange.class))).thenReturn(updateMock);
        when(updateMock.setValueInputOption(anyString())).thenReturn(updateMock);
        UpdateValuesResponse updateResponse = new UpdateValuesResponse();
        when(updateMock.execute()).thenReturn(updateResponse);

        Issue updated = service.updateIssue("AD-1", "IN_PROGRESS");

        assertNotNull(updated);
        assertEquals("IN_PROGRESS", updated.getStatus());
    }

    //The google sheet has to be cleaned up for this test to test properly
    /*@Test
    void testListIssues() throws IOException {
        List<Object> row1 = Arrays.asList("AD-1", "Desc1", "AD-0", "OPEN", LocalDateTime.now().toString(), LocalDateTime.now().toString());
        List<Object> row2 = Arrays.asList("AD-2", "Desc2", "AD-0", "CLOSED", LocalDateTime.now().toString(), LocalDateTime.now().toString());

        ValueRange response = new ValueRange().setValues(Arrays.asList(
                Arrays.asList("ID","Desc","Parent","Status","Created","Updated"),
                row1,
                row2
        ));

        // Mock Get
        Sheets.Spreadsheets.Values.Get getMock = mock(Sheets.Spreadsheets.Values.Get.class);
        when(valuesMock.get(anyString(), anyString())).thenReturn(getMock);
        when(getMock.execute()).thenReturn(response);

        List<Issue> openIssues = service.listIssues("OPEN");
        assertEquals(1, openIssues.size());
        assertEquals("AD-1", openIssues.get(0).getId());

        List<Issue> closedIssues = service.listIssues("CLOSED");
        assertEquals(1, closedIssues.size());
        assertEquals("AD-2", closedIssues.get(0).getId());
    }*/
}