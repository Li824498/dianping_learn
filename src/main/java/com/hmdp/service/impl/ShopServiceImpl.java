package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    private int a = 0;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;


    @Override
    public Result queryById(Long id) {
//        Shop shop = queryWithPassThrough(id);

//        Shop shop = queryWithMutex(id);

//        Shop shop = queryWithLogicalExpire(id);
//

//        Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, this::getById, 20L, TimeUnit.SECONDS);



        if(shop == null) {
            return Result.fail("店铺不存在！");
        }

        return Result.ok(shop);
    }

//    public static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

//    public Shop queryWithLogicalExpire(Long id) {
//        String key = CACHE_SHOP_KEY + id;
//
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//
//        if(StrUtil.isBlank(shopJson)) {
//            return null;
//        }
//
//        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
//        JSONObject data = (JSONObject) redisData.getData();
//        Shop shop = JSONUtil.toBean(data, Shop.class);
//        LocalDateTime expireTime = redisData.getExpireTime();
//
//        if(expireTime.isAfter(LocalDateTime.now())) {
//            return shop;
//        }
//
//        String lockKey = LOCK_SHOP_KEY + id;
//        boolean isLock = tryLock(lockKey);
//
//        if(isLock) {
//            CACHE_REBUILD_EXECUTOR.submit(() -> {
//                try {
//                    saveShop2Redis(id, 20L);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    unlock(lockKey);
//                }
//            });
//        }
//
//
//        return shop;
//    }

//    private Shop queryWithMutex(Long id) {
//        String key = CACHE_SHOP_KEY + id;
//
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//
//        if(StrUtil.isNotBlank(shopJson)) {
//            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
//            return shop;
//        }
//
//        if (shopJson != null) {
//            return null;
//        }
//
//        String lockKey = LOCK_SHOP_KEY + id;
//        Shop shop = null;
//        try {
//            if(!tryLock(lockKey)) {
//                Thread.sleep(50);
//                return queryWithMutex(id);
//            }
//
//            shop = getById(id);
//
//            Thread.sleep(200);
//
//            if(shop == null) {
//                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//                return null;
//            }
//
//            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            unlock(lockKey);
//        }
//
//        return shop;
//    }

//    public Shop queryWithPassThrough(Long id) {
//        String key = CACHE_SHOP_KEY + id;
//
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//
//        if(StrUtil.isNotBlank(shopJson)) {
//            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
//            return shop;
//        }
//
//        if (shopJson != null) {
//            return null;
//        }
//
//        Shop shop = getById(id);
//
//        if(shop == null) {
//            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//            return null;
//        }
//
//        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
//
//        return shop;
//    }

//    public boolean tryLock(String key){
//        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", LOCK_SHOP_TTL, TimeUnit.SECONDS);
//        return BooleanUtil.isTrue(flag);
//    }
//
//    public void unlock(String key) {
//        stringRedisTemplate.delete(key);
//    }


//    public void saveShop2Redis(Long id, Long expireSeconds) throws InterruptedException {
//        Shop shop = getById(id);
//        Thread.sleep(200);
//
//        RedisData redisData = new RedisData();
//        redisData.setData(shop);
//        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
//
//        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
//    }


    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();

        if(id == null) {
            return Result.fail(("店铺id不能为空"));
        }

        updateById(shop);
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);

        return Result.ok();
    }
}
