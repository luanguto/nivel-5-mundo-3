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
import cadastroserver.model.Pessoajuridica;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author luan_
 */
public class PessoajuridicaJpaController implements Serializable {

    public PessoajuridicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoajuridica pessoajuridica) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa idPessoa = pessoajuridica.getIdPessoa();
            if (idPessoa != null) {
                idPessoa = em.getReference(idPessoa.getClass(), idPessoa.getIdPessoa());
                pessoajuridica.setIdPessoa(idPessoa);
            }
            em.persist(pessoajuridica);
            if (idPessoa != null) {
                idPessoa.getPessoajuridicaCollection().add(pessoajuridica);
                idPessoa = em.merge(idPessoa);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoajuridica(pessoajuridica.getCnpj()) != null) {
                throw new PreexistingEntityException("Pessoajuridica " + pessoajuridica + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoajuridica pessoajuridica) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoajuridica persistentPessoajuridica = em.find(Pessoajuridica.class, pessoajuridica.getCnpj());
            Pessoa idPessoaOld = persistentPessoajuridica.getIdPessoa();
            Pessoa idPessoaNew = pessoajuridica.getIdPessoa();
            if (idPessoaNew != null) {
                idPessoaNew = em.getReference(idPessoaNew.getClass(), idPessoaNew.getIdPessoa());
                pessoajuridica.setIdPessoa(idPessoaNew);
            }
            pessoajuridica = em.merge(pessoajuridica);
            if (idPessoaOld != null && !idPessoaOld.equals(idPessoaNew)) {
                idPessoaOld.getPessoajuridicaCollection().remove(pessoajuridica);
                idPessoaOld = em.merge(idPessoaOld);
            }
            if (idPessoaNew != null && !idPessoaNew.equals(idPessoaOld)) {
                idPessoaNew.getPessoajuridicaCollection().add(pessoajuridica);
                idPessoaNew = em.merge(idPessoaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pessoajuridica.getCnpj();
                if (findPessoajuridica(id) == null) {
                    throw new NonexistentEntityException("The pessoajuridica with id " + id + " no longer exists.");
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
            Pessoajuridica pessoajuridica;
            try {
                pessoajuridica = em.getReference(Pessoajuridica.class, id);
                pessoajuridica.getCnpj();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoajuridica with id " + id + " no longer exists.", enfe);
            }
            Pessoa idPessoa = pessoajuridica.getIdPessoa();
            if (idPessoa != null) {
                idPessoa.getPessoajuridicaCollection().remove(pessoajuridica);
                idPessoa = em.merge(idPessoa);
            }
            em.remove(pessoajuridica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoajuridica> findPessoajuridicaEntities() {
        return findPessoajuridicaEntities(true, -1, -1);
    }

    public List<Pessoajuridica> findPessoajuridicaEntities(int maxResults, int firstResult) {
        return findPessoajuridicaEntities(false, maxResults, firstResult);
    }

    private List<Pessoajuridica> findPessoajuridicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoajuridica.class));
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

    public Pessoajuridica findPessoajuridica(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoajuridica.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoajuridicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoajuridica> rt = cq.from(Pessoajuridica.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
