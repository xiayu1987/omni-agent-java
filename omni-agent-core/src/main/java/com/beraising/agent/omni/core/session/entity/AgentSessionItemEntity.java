package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("t_agent_session_item")
@Data
public class AgentSessionItemEntity {

    @TableId(value = "n_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("s_session_id")
    private String sessionId;

    @TableField("s_item_data")
    private String itemData;

    @TableField("t_update_time")
    private LocalDateTime updateTime;

    @TableField("t_create_time")
    private LocalDateTime createTime;
}
