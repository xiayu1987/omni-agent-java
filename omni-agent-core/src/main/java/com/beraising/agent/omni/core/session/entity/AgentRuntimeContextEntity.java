package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("t_agent_runtime_context")
@Data
public class AgentRuntimeContextEntity {

    @TableId(value = "n_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("s_session_id")
    private String sessionId;

    @TableField("s_context_data")
    private String contextData;

    @TableField("t_update_time")
    private LocalDateTime updateTime;

    @TableField("t_create_time")
    private LocalDateTime createTime;
}
