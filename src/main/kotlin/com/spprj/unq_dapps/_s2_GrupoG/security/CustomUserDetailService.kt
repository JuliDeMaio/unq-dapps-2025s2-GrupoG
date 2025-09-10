package com.spprj.unq_dapps._s2_GrupoG.security
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Usuario no encontrado: $username")

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            listOf(org.springframework.security.core.authority.SimpleGrantedAuthority(user.role.name))
        )
    }
}