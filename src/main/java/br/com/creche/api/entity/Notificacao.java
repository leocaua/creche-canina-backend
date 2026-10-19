package br.com.creche.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String texto;
    private Boolean lida;

    @Column(name = "lida_admin")
    private Boolean lidaAdmin;

    @Column(name = "excluida_admin")
    private Boolean excluidaAdmin;

    @Column(name = "excluida_cliente")
    private Boolean excluidaCliente;

    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();

        if (this.lida == null) {
            this.lida = false;
        }
        if (this.lidaAdmin == null) {
            this.lidaAdmin = false;
        }
        if (this.excluidaAdmin == null) {
            this.excluidaAdmin = false;
        }
        if (this.excluidaCliente == null) {
            this.excluidaCliente = false;
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Boolean getLida() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null &&
                auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return Boolean.TRUE.equals(this.lidaAdmin);
        }
        return Boolean.TRUE.equals(this.lida);
    }
    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    @JsonIgnore
    public Boolean getLidaAdmin() {
        return lidaAdmin;
    }
    public void setLidaAdmin(Boolean lidaAdmin) {
        this.lidaAdmin = lidaAdmin;
    }

    @JsonIgnore
    public Boolean getExcluidaAdmin() {
        return excluidaAdmin;
    }
    public void setExcluidaAdmin(Boolean excluidaAdmin) {
        this.excluidaAdmin = excluidaAdmin;
    }

    @JsonIgnore
    public Boolean getExcluidaCliente() {
        return excluidaCliente;
    }
    public void setExcluidaCliente(Boolean excluidaCliente) {
        this.excluidaCliente = excluidaCliente;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}