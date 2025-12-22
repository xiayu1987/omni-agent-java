package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent运行时上下文实体类
 * 用于存储和管理Agent在运行过程中的上下文信息
 * 对应数据库表t_agent_runtime_context
 */
@TableName("t_agent_runtime_context")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentRuntimeContextEntity {

    /**
     * 主键ID
     */
    @TableId()
    private String id;

    /**
     * 会话ID，用于关联同一会话的上下文数据
     */
    @TableField()
    private String sessionId;

    /**
     * 上下文数据，存储Agent运行时的具体上下文信息
     */
    @TableField()
    private String contextData;

    /**
     * 更新时间，记录数据最后更新的时间戳
     */
    @TableField()
    private LocalDateTime updateTime;

    /**
     * 创建时间，记录数据首次创建的时间戳
     */
    @TableField()
    private LocalDateTime createTime;
}
