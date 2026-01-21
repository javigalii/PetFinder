package edu.vedoque.seguridadbase.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        // 1. RECURSOS ESTÁTICOS (Siempre públicos)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/blog").permitAll()

                        // 2. PÁGINAS PÚBLICAS (Login, Registro, Inicio)
                        .requestMatchers("/register/**", "/index", "/", "/inicio").permitAll()
                        .requestMatchers("/file/download/**").permitAll()

                        // 3. ANIMALES (El catálogo y los detalles son públicos para que la gente se anime a adoptar)
                        .requestMatchers("/animales/lista", "/animales/detalle/**", "/perfil/usuario/**").permitAll()

                        // 4. SOLO ADMINISTRADORES
                        .requestMatchers("/users").hasRole("ADMIN")

                        // 5. ZONA PRIVADA (Requiere estar logueado)
                        // A. Acciones sobre animales
                        .requestMatchers("/animales/megusta/**", "/animales/favoritos", "/animales/detalle/**").authenticated()

                        // B. PERFIL DE USUARIO (Ver, Editar, Guardar)
                        // El ** incluye /perfil/, /perfil/editar y /perfil/guardar
                        .requestMatchers("/perfil/**").authenticated()

                        // 6. RUTAS ANTIGUAS (Noticias)
                        .requestMatchers("/crud/noticias/**", "/noticia/**", "/comentario/insertar", "/megusta/**", "/crud/noticias/insertar").authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                // Al entrar correctamente, te lleva a la lista de animales
                                .defaultSuccessUrl("/animales/lista")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}