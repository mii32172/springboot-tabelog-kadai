package com.example.tabelog.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.User;
import com.example.tabelog.entity.VerificationToken;
import com.example.tabelog.repository.VerificationTokenRepository;

@Service

public class VerificationTokenService {
	private final VerificationTokenRepository verificationTokenRepository;
    
    
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {        
        this.verificationTokenRepository = verificationTokenRepository;
    } 
    
    @Transactional
    public void create(User user,String token) {
    	VerificationToken verificationToken = new VerificationToken();
    	
    	verificationToken.setUser(user);
    	verificationToken.setToken(token);
    	
    	verificationTokenRepository.save(verificationToken);
    	
    	
    }
    
    // トークンの文字列で検索した結果を返す
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
    
    //新しく追加
    public String createVerificationToken(User user) {
		// トークンの生成
		VerificationToken token = verificationTokenRepository.findByUser(user);
		if (token == null) {
			token = new VerificationToken();
			token.setUser(user);
		}
		token.setToken(UUID.randomUUID().toString());
		verificationTokenRepository.save(token);

		return token.getToken();
	}

	public VerificationToken findByToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}

	public void deleteToken(VerificationToken token) {
		verificationTokenRepository.delete(token);
	}
}
