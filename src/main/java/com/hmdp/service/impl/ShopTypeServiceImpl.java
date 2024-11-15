package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOPTRPELIST_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {

        String shopTypeListJson = stringRedisTemplate.opsForValue().get(CACHE_SHOPTRPELIST_KEY);

        if(StrUtil.isNotBlank(shopTypeListJson)) {
            List<ShopType> shopType = JSONUtil.toList(shopTypeListJson, ShopType.class);
            return Result.ok(shopType);
        }

        List<ShopType> list = list();

        if(list == null || list.size() == 0) {
            return Result.fail("店铺不存在！");
        }

        stringRedisTemplate.opsForValue().set(CACHE_SHOPTRPELIST_KEY, JSONUtil.toJsonStr(list));

        return Result.ok(list);

    }
}
