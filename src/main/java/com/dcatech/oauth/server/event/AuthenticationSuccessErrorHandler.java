package com.dcatech.oauth.server.event;

import com.dcatech.oauth.server.models.Service.IUsuarioService;
import com.dcatech.security.commons.models.entity.Usuario;
import com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

    private Logger log = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);

    @Autowired
    private IUsuarioService usuarioService;

    private final int NUMERO_MAXIMO_INTENTOS = 3;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String message = String.format("Usuario %s autenticado con exito",userDetails.getUsername());
        System.out.println(message);
        log.info(message);

        try {
            Usuario usuario = usuarioService.getUsuarios(authentication.getName());

            if (usuario.getIntentos() != null && usuario.getIntentos() > 0) {
                usuario.setIntentos(0);
                usuarioService.edit(usuario, usuario.getId());
            }
        }catch (FeignException e){
            message = String.format("El usuario %s no esta registrado en el sistema.",authentication.getName());
            log.error(message);
        }

    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        String message = String.format("Error de autenticación: %s",exception.getMessage());
        System.out.println(message);
        log.info(message + " " + authentication.getName());

        try {
            Usuario usuario = usuarioService.getUsuarios(authentication.getName());

            if(usuario.getIntentos() == null){
                usuario.setIntentos(0);
            }

            log.info("Intentos actuales: " + usuario.getIntentos());

            usuario.setIntentos(usuario.getIntentos() + 1);

            log.info("Intentos posteriores: " + usuario.getIntentos());

            if(usuario.getIntentos() >= NUMERO_MAXIMO_INTENTOS){
                log.error(String.format("El usuario %s ha sido deshabilitado por numero máximo de intentos", usuario.getUsername()));
                usuario.setEnabled(false);
            }

            usuarioService.edit(usuario, usuario.getId());

        }catch (FeignException e){
            message = String.format("El usuario %s no esta registrado en el sistema.", authentication.getName());
            log.error(message);
        }

    }
}
