package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("t_agent_session")
@Data
public class AgentSessionEntity {
    
    @TableId("s_session_id")
    private String sessionId;

    @TableField("s_user_id")
    private String userId;

    @TableField("t_update_time")
    private LocalDateTime updateTime;

    @TableField("t_create_time")
    private LocalDateTime createTime;
}