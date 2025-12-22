package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * AgentSessionEntity实体类
 * 用于映射数据库表t_agent_session，表示代理会话信息
 */
@TableName("t_agent_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionEntity {

    /**
     * 会话ID，作为表的主键
     */
    @TableId()
    private String sessionId;

    /**
     * 父会话ID，用于关联父级会话
     */
    @TableField()
    private String parentSessionId;

    /**
     * 用户ID，标识会话所属的用户
     */
    @TableField()
    private String userId;

    /**
     * 用户类型，表示用户的分类或角色
     */
    @TableField()
    private int userType;

    /**
     * 更新时间，记录会话信息最后更新的时间
     */
    @TableField()
    private LocalDateTime updateTime;

    /**
     * 创建时间，记录会话的创建时间
     */
    @TableField()
    private LocalDateTime createTime;

}
