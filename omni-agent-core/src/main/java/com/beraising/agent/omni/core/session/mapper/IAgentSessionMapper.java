package com.beraising.agent.omni.core.session.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beraising.agent.omni.core.session.entity.AgentSessionEntity;

/**
 * Agent会话数据访问接口
 * 提供对t_agent_session表的基本CRUD操作
 */
@Mapper
public interface IAgentSessionMapper extends BaseMapper<AgentSessionEntity> {

    /**
     * 根据会话ID查询会话信息
     * @param sessionId 会话ID
     * @return AgentSessionEntity 会话实体对象，如果未找到则返回null
     */
    @Select("SELECT * FROM t_agent_session WHERE session_id = #{sessionId}")
    AgentSessionEntity findById(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID查询该用户的所有会话信息
     * @param userId 用户ID
     * @return List<AgentSessionEntity> 会话实体列表
     */
    @Select("SELECT * FROM t_agent_session WHERE user_id = #{userId}")
    List<AgentSessionEntity> findByUserId(@Param("userId") String userId);

    /**
     * 根据会话ID删除会话记录
     * @param sessionId 会话ID
     * @return int 删除的记录数
     */
    @Delete("DELETE FROM t_agent_session WHERE session_id = #{sessionId}")
    int deleteById(@Param("sessionId") String sessionId);
}
