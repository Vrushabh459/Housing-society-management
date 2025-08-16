package com.app.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardStatsDTO {
    private Long totalResidents;
    private Long totalFlats;
    private Long totalBuildings;
    private Long pendingComplaints;
    private Long pendingAllocations;
    private Long todayVisitors;
    private Long overduePayments;
    private Long activeNotices;
}
