package com.odk.connect.service;

import org.springframework.stereotype.Service;
import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LogginAttemptService {
	private static final int MAXIMUN_NUMBER_OF_ATTEMPTS = 5;
	public static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Integer> loginAttempCache;

	public LogginAttemptService() {
		super();
		loginAttempCache = CacheBuilder.newBuilder().expireAfterWrite(15,MINUTES).maximumSize(100).build(new CacheLoader<String, Integer>(){
			public Integer load(String key) {
				return 0;
			}
		});
	}
	public void evictUserFromLoginAttemptCache(String username){
		loginAttempCache.invalidate(username);
    }
	public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttempCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttempCache.put(username,attempts);
    }
	 public boolean hasExceededMaxAttempts(String username) {
	        try {
	            return loginAttempCache.get(username) >= MAXIMUN_NUMBER_OF_ATTEMPTS ;
	        } catch (ExecutionException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }

}
