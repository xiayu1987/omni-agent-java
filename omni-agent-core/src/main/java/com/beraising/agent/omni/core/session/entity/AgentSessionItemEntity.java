package com.beraising.agent.omni.core.session.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Agent会话项实体类
 * 用于映射数据库中t_agent_session_item表的记录
 * 每条记录表示一个代理会话中的具体项目数据
 */
@TableName("t_agent_session_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItemEntity {

    /**
     * 主键ID
     * 使用ASSIGN_ID策略自动生成唯一标识
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话ID
     * 关联到具体的会话记录
     */
    @TableField()
    private String sessionId;

    /**
     * 项目数据
     * 存储会话项的具体内容数据
     */
    @TableField()
    private String itemData;

    /**
     * 更新时间
     * 记录数据最后修改的时间戳
     */
    @TableField()
    private LocalDateTime updateTime;

    /**
     * 创建时间
     * 记录数据创建的时间戳
     */
    @TableField()
    private LocalDateTime createTime;
}

