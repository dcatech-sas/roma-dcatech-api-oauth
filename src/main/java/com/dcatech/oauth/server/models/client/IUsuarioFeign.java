package com.dcatech.oauth.server.models.client;

import com.dcatech.security.commons.models.entity.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dcatech-seguridad-services")
public interface IUsuarioFeign {


    @GetMapping("/usuarios/find-username/{username}")
    Usuario getUsuarios(@PathVariable String username);

    @PutMapping("/update/{id}")
    Usuario edit(@RequestBody Usuario usuario, @PathVariable Long id);
}
