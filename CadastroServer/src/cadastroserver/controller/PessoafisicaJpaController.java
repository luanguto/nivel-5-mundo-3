/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver.controller;

import cadastroserver.exceptions.NonexistentEntityException;
import cadastroserver.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import cadastroserver.model.Pessoa;
import cadastroserver.model.Pessoafisica;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author luan_
 */
public class PessoafisicaJpaController implements Serializable {

    public PessoafisicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoafisica pessoafisica) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa idPessoa = pessoafisica.getIdPessoa();
            if (idPessoa != null) {
                idPessoa = em.getReference(idPessoa.getClass(), idPessoa.getIdPessoa());
                pessoafisica.setIdPessoa(idPessoa);
            }
            em.persist(pessoafisica);
            if (idPessoa != null) {
                idPessoa.getPessoafisicaCollection().add(pessoafisica);
                idPessoa = em.merge(idPessoa);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoafisica(pessoafisica.getCpf()) != null) {
                throw new PreexistingEntityException("Pessoafisica " + pessoafisica + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoafisica pessoafisica) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoafisica persistentPessoafisica = em.find(Pessoafisica.class, pessoafisica.getCpf());
            Pessoa idPessoaOld = persistentPessoafisica.getIdPessoa();
            Pessoa idPessoaNew = pessoafisica.getIdPessoa();
            if (idPessoaNew != null) {
                idPessoaNew = em.getReference(idPessoaNew.getClass(), idPessoaNew.getIdPessoa());
                pessoafisica.setIdPessoa(idPessoaNew);
            }
            pessoafisica = em.merge(pessoafisica);
            if (idPessoaOld != null && !idPessoaOld.equals(idPessoaNew)) {
                idPessoaOld.getPessoafisicaCollection().remove(pessoafisica);
                idPessoaOld = em.merge(idPessoaOld);
            }
            if (idPessoaNew != null && !idPessoaNew.equals(idPessoaOld)) {
                idPessoaNew.getPessoafisicaCollection().add(pessoafisica);
                idPessoaNew = em.merge(idPessoaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pessoafisica.getCpf();
                if (findPessoafisica(id) == null) {
                    throw new NonexistentEntityException("The pessoafisica with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoafisica pessoafisica;
            try {
                pessoafisica = em.getReference(Pessoafisica.class, id);
                pessoafisica.getCpf();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoafisica with id " + id + " no longer exists.", enfe);
            }
            Pessoa idPessoa = pessoafisica.getIdPessoa();
            if (idPessoa != null) {
                idPessoa.getPessoafisicaCollection().remove(pessoafisica);
                idPessoa = em.merge(idPessoa);
            }
            em.remove(pessoafisica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoafisica> findPessoafisicaEntities() {
        return findPessoafisicaEntities(true, -1, -1);
    }

    public List<Pessoafisica> findPessoafisicaEntities(int maxResults, int firstResult) {
        return findPessoafisicaEntities(false, maxResults, firstResult);
    }

    private List<Pessoafisica> findPessoafisicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoafisica.class));
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

    public Pessoafisica findPessoafisica(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoafisica.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoafisicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoafisica> rt = cq.from(Pessoafisica.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
