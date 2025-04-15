package com.pfe.sytemedeconge.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import Model.Utilisateur;
import Repository.UtilisateurRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        // Convertir l'utilisateur en UserDetails pour Spring Security
        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .roles(utilisateur.getRole().getName())
                // Ajoute le rôle de l'utilisateur
                .build();

                /*String authority = "ROLE_" + utilisateur.getRole().getName(); // Ensure this becomes ROLE_EMPLOYE
        return new User(utilisateur.getEmail(), utilisateur.getMotDePasse(),
                Collections.singleton(new SimpleGrantedAuthority(authority)));*/
    }

    public void uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        Optional<Utilisateur> userOptional = utilisateurRepository.findById(userId);
        if (userOptional.isPresent()) {
            Utilisateur user = userOptional.get();
            user.setProfilePicture(file.getBytes());
            utilisateurRepository.save(user);
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }

    public byte[] getProfilePicture(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return user.getProfilePicture();
    }
}

