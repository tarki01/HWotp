package com.otptgbot.SecurityUtilities;

import com.otptgbot.DAO.AccountRepo;
import com.otptgbot.Entities.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrincipalResolver implements UserDetailsService {

    private final AccountRepo accountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account foundAccount = accountRepo.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new org.springframework.security.core.userdetails.User(
                foundAccount.getLogin(),
                foundAccount.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + foundAccount.getAccessLvl().name()))
        );
    }
}