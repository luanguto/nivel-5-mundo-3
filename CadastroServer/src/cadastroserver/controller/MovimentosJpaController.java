/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver.controller;

import cadastroserver.exceptions.NonexistentEntityException;
import cadastroserver.model.Movimentos;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import cadastroserver.model.Pessoa;
import cadastroserver.model.Produtos;
import cadastroserver.model.Usuarios;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author luan_
 */
public class MovimentosJpaController implements Serializable {

    public MovimentosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimentos movimentos) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa idPessoa = movimentos.getIdPessoa();
            if (idPessoa != null) {
                idPessoa = em.getReference(idPessoa.getClass(), idPessoa.getIdPessoa());
                movimentos.setIdPessoa(idPessoa);
            }
            Produtos idProduto = movimentos.getIdProduto();
            if (idProduto != null) {
                idProduto = em.getReference(idProduto.getClass(), idProduto.getIdProduto());
                movimentos.setIdProduto(idProduto);
            }
            Usuarios idUsuario = movimentos.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                movimentos.setIdUsuario(idUsuario);
            }
            em.persist(movimentos);
            if (idPessoa != null) {
                idPessoa.getMovimentosCollection().add(movimentos);
                idPessoa = em.merge(idPessoa);
            }
            if (idProduto != null) {
                idProduto.getMovimentosCollection().add(movimentos);
                idProduto = em.merge(idProduto);
            }
            if (idUsuario != null) {
                idUsuario.getMovimentosCollection().add(movimentos);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movimentos movimentos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimentos persistentMovimentos = em.find(Movimentos.class, movimentos.getIdMovimento());
            Pessoa idPessoaOld = persistentMovimentos.getIdPessoa();
            Pessoa idPessoaNew = movimentos.getIdPessoa();
            Produtos idProdutoOld = persistentMovimentos.getIdProduto();
            Produtos idProdutoNew = movimentos.getIdProduto();
            Usuarios idUsuarioOld = persistentMovimentos.getIdUsuario();
            Usuarios idUsuarioNew = movimentos.getIdUsuario();
            if (idPessoaNew != null) {
                idPessoaNew = em.getReference(idPessoaNew.getClass(), idPessoaNew.getIdPessoa());
                movimentos.setIdPessoa(idPessoaNew);
            }
            if (idProdutoNew != null) {
                idProdutoNew = em.getReference(idProdutoNew.getClass(), idProdutoNew.getIdProduto());
                movimentos.setIdProduto(idProdutoNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                movimentos.setIdUsuario(idUsuarioNew);
            }
            movimentos = em.merge(movimentos);
            if (idPessoaOld != null && !idPessoaOld.equals(idPessoaNew)) {
                idPessoaOld.getMovimentosCollection().remove(movimentos);
                idPessoaOld = em.merge(idPessoaOld);
            }
            if (idPessoaNew != null && !idPessoaNew.equals(idPessoaOld)) {
                idPessoaNew.getMovimentosCollection().add(movimentos);
                idPessoaNew = em.merge(idPessoaNew);
            }
            if (idProdutoOld != null && !idProdutoOld.equals(idProdutoNew)) {
                idProdutoOld.getMovimentosCollection().remove(movimentos);
                idProdutoOld = em.merge(idProdutoOld);
            }
            if (idProdutoNew != null && !idProdutoNew.equals(idProdutoOld)) {
                idProdutoNew.getMovimentosCollection().add(movimentos);
                idProdutoNew = em.merge(idProdutoNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getMovimentosCollection().remove(movimentos);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getMovimentosCollection().add(movimentos);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movimentos.getIdMovimento();
                if (findMovimentos(id) == null) {
                    throw new NonexistentEntityException("The movimentos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimentos movimentos;
            try {
                movimentos = em.getReference(Movimentos.class, id);
                movimentos.getIdMovimento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimentos with id " + id + " no longer exists.", enfe);
            }
            Pessoa idPessoa = movimentos.getIdPessoa();
            if (idPessoa != null) {
                idPessoa.getMovimentosCollection().remove(movimentos);
                idPessoa = em.merge(idPessoa);
            }
            Produtos idProduto = movimentos.getIdProduto();
            if (idProduto != null) {
                idProduto.getMovimentosCollection().remove(movimentos);
                idProduto = em.merge(idProduto);
            }
            Usuarios idUsuario = movimentos.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getMovimentosCollection().remove(movimentos);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(movimentos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movimentos> findMovimentosEntities() {
        return findMovimentosEntities(true, -1, -1);
    }

    public List<Movimentos> findMovimentosEntities(int maxResults, int firstResult) {
        return findMovimentosEntities(false, maxResults, firstResult);
    }

    private List<Movimentos> findMovimentosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movimentos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Movimentos findMovimentos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimentos.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimentosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movimentos> rt = cq.from(Movimentos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
