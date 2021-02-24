package com.dcatech.oauth.server.models.Service;

import com.dcatech.security.commons.models.entity.Usuario;


public interface IUsuarioService {

    Usuario getUsuarios(String username);
    Usuario edit(Usuario usuario,Long id);
}
