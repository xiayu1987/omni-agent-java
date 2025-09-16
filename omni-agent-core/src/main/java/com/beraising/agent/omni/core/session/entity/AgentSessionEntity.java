package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("t_agent_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionEntity {

    @TableId()
    private String sessionId;

    @TableField()
    private String userId;

    @TableField()
    private LocalDateTime updateTime;

    @TableField()
    private LocalDateTime createTime;

}