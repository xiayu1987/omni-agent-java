package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("t_agent_session_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItemEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField()
    private String sessionId;

    @TableField()
    private String itemData;

    @TableField()
    private LocalDateTime updateTime;

    @TableField()
    private LocalDateTime createTime;
}
