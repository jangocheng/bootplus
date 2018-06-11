package io.github.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.github.dao.SysUserRoleDao;
import io.github.entity.SysUserRoleEntity;
import io.github.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户与角色对应关系
 *
 * @author Joey
 * @Email 2434387555@qq.com
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity>
        implements SysUserRoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long userId, List<Long> roleIdList) {
        if (roleIdList.size() == 0) {
            return;
        }
        // 先删除用户与角色关系
        baseMapper.deleteNoMapper(userId);

        // 保存用户与角色关系
        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", userId);
        map.put("roleIdList", roleIdList);
        baseMapper.save(map);
    }

    @Override
    public List<Long> queryRoleIdList(Long userId) {
        return baseMapper.queryRoleIdList(userId);
    }

    @Override
    public void delete(Long userId) {
        baseMapper.deleteNoMapper(userId);
    }

    @Override
    public List<String> queryRoleNames(Long userId) {
        return baseMapper.queryRoleNames(userId);
    }
}
