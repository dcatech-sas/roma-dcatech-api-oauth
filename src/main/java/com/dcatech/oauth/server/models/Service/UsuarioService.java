package com.dcatech.oauth.server.models.Service;

import com.dcatech.oauth.server.models.client.IUsuarioFeign;
import com.dcatech.security.commons.models.entity.Usuario;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private IUsuarioFeign usuarioFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Usuario " + username);

        try {
            Usuario usuarios = usuarioFeign.getUsuarios(username);

            List<GrantedAuthority> authorities = new ArrayList<>();

            System.out.println("Roles encontrados " + usuarios.getRoles().size());
            try {
                authorities = usuarios.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                        .peek(authority -> log.info(authority.getAuthority()))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.out.println("Error en los roles " + authorities);
            }

            log.info("Usuario autenticado: " + username);

            return new User(usuarios.getUsername(), usuarios.getPassword(), usuarios.getEnabled(),
                    true, true, true, authorities);
        }catch (FeignException e){
            String error = "Error en el login, no existe el usuario " + username + " registrado en e sistema.";
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
    }

    @Override
    public Usuario getUsuarios(String username) {
            return usuarioFeign.getUsuarios(username);
    }

    @Override
    public Usuario edit(Usuario usuario, Long id) {
        return usuarioFeign.edit(usuario,id);
    }
}
