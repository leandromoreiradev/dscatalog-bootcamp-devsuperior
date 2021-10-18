package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//Classe de configuração
@Configuration // Indica que esta é uma classe de configuração
public class AppConfig {

    @Bean // Bean é um componente do spring com assinatura de metodo, o spring intancia e gerencia ele em outros components
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
