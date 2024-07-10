package com.kahiroshi.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emp {
    private Integer id;
    private String name;
    private String phone;
    private Integer deptId;
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
}
