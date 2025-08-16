package com.app.dto;

import java.time.LocalDateTime;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatMemberDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String relationship;
    private boolean isOwner;
    private Long flatId;
    private String flatNumber;
    private Long userId;
    private boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
    


