package com.hmdp.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;


import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{
    private String name;

    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.name = name;
    }

    public static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID().toString() + "-";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        String key = KEY_PREFIX + name;
        Boolean success = stringRedisTemplate
                .opsForValue()
                .setIfAbsent(key, threadId, timeoutSec, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlock() {
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + name),
                ID_PREFIX + Thread.currentThread().getId()
        );

    }

//    @Override
//    public void unlock() {
//        String threadId = ID_PREFIX + Thread.currentThread().getId();
//        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
//
//        if(id.equals(threadId)){
//            stringRedisTemplate.delete(KEY_PREFIX + name);
//        }
//
//    }
}
