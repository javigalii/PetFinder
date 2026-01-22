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
                        // 1. RECURSOS ESTÁTICOS (Públicos)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/blog", "/").permitAll()

                        // 2. LOGIN, REGISTRO Y HOME (Públicos)
                        .requestMatchers("/register/**", "/index", "/", "/inicio", "/error").permitAll()
                        .requestMatchers("/file/download/**").permitAll()

                        // 3. ANIMALES PÚBLICOS (Ver catálogo y detalles)
                        // Ojo: "/perfil/usuario/**" es para ver el perfil público de otro usuario
                        .requestMatchers("/detalle/**", "/usuario/**").permitAll()

                        // 4. SOLO ADMINISTRADORES
                        .requestMatchers("/users").hasRole("ADMIN")

                        // 5. ZONA PRIVADA (Rutas que requieren estar logueado)
                        // A. Acciones sobre animales (Likes y favoritos)
                        .requestMatchers("/megusta/**", "/favoritos").authenticated()

                        // B. PERFIL Y GESTIÓN (AQUÍ ESTÁN LOS CAMBIOS)
                        // Como quitaste "/perfil/" del controlador, ahora tienes que listar las rutas sueltas:
                        .requestMatchers(
                                "/perfil",               // Ver mi perfil
                                "/editar",               // Formulario editar usuario
                                "/guardar",              // Guardar usuario
                                "/anadir-animal",        // Formulario añadir animal
                                "/guardar-animal",       // Guardar animal nuevo
                                "/editar-animal/**",     // Formulario editar animal
                                "/actualizar-animal",    // Guardar cambios animal
                                "/eliminar-animal/**"    // Borrar animal
                        ).authenticated()

                        // 6. RUTAS ANTIGUAS (Si alguna te queda)
                        .requestMatchers("/crud/noticias/**", "/noticia/**", "/comentario/insertar", "/megusta/**").authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/", true) // Redirige al inicio (lista de animales)
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