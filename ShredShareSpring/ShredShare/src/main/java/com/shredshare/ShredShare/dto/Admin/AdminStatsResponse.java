package com.shredshare.ShredShare.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsResponse {
    private int totalPending;
    private int totalApproved;
    private int totalOwners;
    private int totalUsers;


}
