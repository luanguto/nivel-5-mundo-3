/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver.controller;

import cadastroserver.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import cadastroserver.model.Pessoafisica;
import java.util.ArrayList;
import java.util.Collection;
import cadastroserver.model.Movimentos;
import cadastroserver.model.Pessoa;
import cadastroserver.model.Pessoajuridica;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author luan_
 */
public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) {
        if (pessoa.getPessoafisicaCollection() == null) {
            pessoa.setPessoafisicaCollection(new ArrayList<Pessoafisica>());
        }
        if (pessoa.getMovimentosCollection() == null) {
            pessoa.setMovimentosCollection(new ArrayList<Movimentos>());
        }
        if (pessoa.getPessoajuridicaCollection() == null) {
            pessoa.setPessoajuridicaCollection(new ArrayList<Pessoajuridica>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Pessoafisica> attachedPessoafisicaCollection = new ArrayList<Pessoafisica>();
            for (Pessoafisica pessoafisicaCollectionPessoafisicaToAttach : pessoa.getPessoafisicaCollection()) {
                pessoafisicaCollectionPessoafisicaToAttach = em.getReference(pessoafisicaCollectionPessoafisicaToAttach.getClass(), pessoafisicaCollectionPessoafisicaToAttach.getCpf());
                attachedPessoafisicaCollection.add(pessoafisicaCollectionPessoafisicaToAttach);
            }
            pessoa.setPessoafisicaCollection(attachedPessoafisicaCollection);
            Collection<Movimentos> attachedMovimentosCollection = new ArrayList<Movimentos>();
            for (Movimentos movimentosCollectionMovimentosToAttach : pessoa.getMovimentosCollection()) {
                movimentosCollectionMovimentosToAttach = em.getReference(movimentosCollectionMovimentosToAttach.getClass(), movimentosCollectionMovimentosToAttach.getIdMovimento());
                attachedMovimentosCollection.add(movimentosCollectionMovimentosToAttach);
            }
            pessoa.setMovimentosCollection(attachedMovimentosCollection);
            Collection<Pessoajuridica> attachedPessoajuridicaCollection = new ArrayList<Pessoajuridica>();
            for (Pessoajuridica pessoajuridicaCollectionPessoajuridicaToAttach : pessoa.getPessoajuridicaCollection()) {
                pessoajuridicaCollectionPessoajuridicaToAttach = em.getReference(pessoajuridicaCollectionPessoajuridicaToAttach.getClass(), pessoajuridicaCollectionPessoajuridicaToAttach.getCnpj());
                attachedPessoajuridicaCollection.add(pessoajuridicaCollectionPessoajuridicaToAttach);
            }
            pessoa.setPessoajuridicaCollection(attachedPessoajuridicaCollection);
            em.persist(pessoa);
            for (Pessoafisica pessoafisicaCollectionPessoafisica : pessoa.getPessoafisicaCollection()) {
                Pessoa oldIdPessoaOfPessoafisicaCollectionPessoafisica = pessoafisicaCollectionPessoafisica.getIdPessoa();
                pessoafisicaCollectionPessoafisica.setIdPessoa(pessoa);
                pessoafisicaCollectionPessoafisica = em.merge(pessoafisicaCollectionPessoafisica);
                if (oldIdPessoaOfPessoafisicaCollectionPessoafisica != null) {
                    oldIdPessoaOfPessoafisicaCollectionPessoafisica.getPessoafisicaCollection().remove(pessoafisicaCollectionPessoafisica);
                    oldIdPessoaOfPessoafisicaCollectionPessoafisica = em.merge(oldIdPessoaOfPessoafisicaCollectionPessoafisica);
                }
            }
            for (Movimentos movimentosCollectionMovimentos : pessoa.getMovimentosCollection()) {
                Pessoa oldIdPessoaOfMovimentosCollectionMovimentos = movimentosCollectionMovimentos.getIdPessoa();
                movimentosCollectionMovimentos.setIdPessoa(pessoa);
                movimentosCollectionMovimentos = em.merge(movimentosCollectionMovimentos);
                if (oldIdPessoaOfMovimentosCollectionMovimentos != null) {
                    oldIdPessoaOfMovimentosCollectionMovimentos.getMovimentosCollection().remove(movimentosCollectionMovimentos);
                    oldIdPessoaOfMovimentosCollectionMovimentos = em.merge(oldIdPessoaOfMovimentosCollectionMovimentos);
                }
            }
            for (Pessoajuridica pessoajuridicaCollectionPessoajuridica : pessoa.getPessoajuridicaCollection()) {
                Pessoa oldIdPessoaOfPessoajuridicaCollectionPessoajuridica = pessoajuridicaCollectionPessoajuridica.getIdPessoa();
                pessoajuridicaCollectionPessoajuridica.setIdPessoa(pessoa);
                pessoajuridicaCollectionPessoajuridica = em.merge(pessoajuridicaCollectionPessoajuridica);
                if (oldIdPessoaOfPessoajuridicaCollectionPessoajuridica != null) {
                    oldIdPessoaOfPessoajuridicaCollectionPessoajuridica.getPessoajuridicaCollection().remove(pessoajuridicaCollectionPessoajuridica);
                    oldIdPessoaOfPessoajuridicaCollectionPessoajuridica = em.merge(oldIdPessoaOfPessoajuridicaCollectionPessoajuridica);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getIdPessoa());
            Collection<Pessoafisica> pessoafisicaCollectionOld = persistentPessoa.getPessoafisicaCollection();
            Collection<Pessoafisica> pessoafisicaCollectionNew = pessoa.getPessoafisicaCollection();
            Collection<Movimentos> movimentosCollectionOld = persistentPessoa.getMovimentosCollection();
            Collection<Movimentos> movimentosCollectionNew = pessoa.getMovimentosCollection();
            Collection<Pessoajuridica> pessoajuridicaCollectionOld = persistentPessoa.getPessoajuridicaCollection();
            Collection<Pessoajuridica> pessoajuridicaCollectionNew = pessoa.getPessoajuridicaCollection();
            Collection<Pessoafisica> attachedPessoafisicaCollectionNew = new ArrayList<Pessoafisica>();
            for (Pessoafisica pessoafisicaCollectionNewPessoafisicaToAttach : pessoafisicaCollectionNew) {
                pessoafisicaCollectionNewPessoafisicaToAttach = em.getReference(pessoafisicaCollectionNewPessoafisicaToAttach.getClass(), pessoafisicaCollectionNewPessoafisicaToAttach.getCpf());
                attachedPessoafisicaCollectionNew.add(pessoafisicaCollectionNewPessoafisicaToAttach);
            }
            pessoafisicaCollectionNew = attachedPessoafisicaCollectionNew;
            pessoa.setPessoafisicaCollection(pessoafisicaCollectionNew);
            Collection<Movimentos> attachedMovimentosCollectionNew = new ArrayList<Movimentos>();
            for (Movimentos movimentosCollectionNewMovimentosToAttach : movimentosCollectionNew) {
                movimentosCollectionNewMovimentosToAttach = em.getReference(movimentosCollectionNewMovimentosToAttach.getClass(), movimentosCollectionNewMovimentosToAttach.getIdMovimento());
                attachedMovimentosCollectionNew.add(movimentosCollectionNewMovimentosToAttach);
            }
            movimentosCollectionNew = attachedMovimentosCollectionNew;
            pessoa.setMovimentosCollection(movimentosCollectionNew);
            Collection<Pessoajuridica> attachedPessoajuridicaCollectionNew = new ArrayList<Pessoajuridica>();
            for (Pessoajuridica pessoajuridicaCollectionNewPessoajuridicaToAttach : pessoajuridicaCollectionNew) {
                pessoajuridicaCollectionNewPessoajuridicaToAttach = em.getReference(pessoajuridicaCollectionNewPessoajuridicaToAttach.getClass(), pessoajuridicaCollectionNewPessoajuridicaToAttach.getCnpj());
                attachedPessoajuridicaCollectionNew.add(pessoajuridicaCollectionNewPessoajuridicaToAttach);
            }
            pessoajuridicaCollectionNew = attachedPessoajuridicaCollectionNew;
            pessoa.setPessoajuridicaCollection(pessoajuridicaCollectionNew);
            pessoa = em.merge(pessoa);
            for (Pessoafisica pessoafisicaCollectionOldPessoafisica : pessoafisicaCollectionOld) {
                if (!pessoafisicaCollectionNew.contains(pessoafisicaCollectionOldPessoafisica)) {
                    pessoafisicaCollectionOldPessoafisica.setIdPessoa(null);
                    pessoafisicaCollectionOldPessoafisica = em.merge(pessoafisicaCollectionOldPessoafisica);
                }
            }
            for (Pessoafisica pessoafisicaCollectionNewPessoafisica : pessoafisicaCollectionNew) {
                if (!pessoafisicaCollectionOld.contains(pessoafisicaCollectionNewPessoafisica)) {
                    Pessoa oldIdPessoaOfPessoafisicaCollectionNewPessoafisica = pessoafisicaCollectionNewPessoafisica.getIdPessoa();
                    pessoafisicaCollectionNewPessoafisica.setIdPessoa(pessoa);
                    pessoafisicaCollectionNewPessoafisica = em.merge(pessoafisicaCollectionNewPessoafisica);
                    if (oldIdPessoaOfPessoafisicaCollectionNewPessoafisica != null && !oldIdPessoaOfPessoafisicaCollectionNewPessoafisica.equals(pessoa)) {
                        oldIdPessoaOfPessoafisicaCollectionNewPessoafisica.getPessoafisicaCollection().remove(pessoafisicaCollectionNewPessoafisica);
                        oldIdPessoaOfPessoafisicaCollectionNewPessoafisica = em.merge(oldIdPessoaOfPessoafisicaCollectionNewPessoafisica);
                    }
                }
            }
            for (Movimentos movimentosCollectionOldMovimentos : movimentosCollectionOld) {
                if (!movimentosCollectionNew.contains(movimentosCollectionOldMovimentos)) {
                    movimentosCollectionOldMovimentos.setIdPessoa(null);
                    movimentosCollectionOldMovimentos = em.merge(movimentosCollectionOldMovimentos);
                }
            }
            for (Movimentos movimentosCollectionNewMovimentos : movimentosCollectionNew) {
                if (!movimentosCollectionOld.contains(movimentosCollectionNewMovimentos)) {
                    Pessoa oldIdPessoaOfMovimentosCollectionNewMovimentos = movimentosCollectionNewMovimentos.getIdPessoa();
                    movimentosCollectionNewMovimentos.setIdPessoa(pessoa);
                    movimentosCollectionNewMovimentos = em.merge(movimentosCollectionNewMovimentos);
                    if (oldIdPessoaOfMovimentosCollectionNewMovimentos != null && !oldIdPessoaOfMovimentosCollectionNewMovimentos.equals(pessoa)) {
                        oldIdPessoaOfMovimentosCollectionNewMovimentos.getMovimentosCollection().remove(movimentosCollectionNewMovimentos);
                        oldIdPessoaOfMovimentosCollectionNewMovimentos = em.merge(oldIdPessoaOfMovimentosCollectionNewMovimentos);
                    }
                }
            }
            for (Pessoajuridica pessoajuridicaCollectionOldPessoajuridica : pessoajuridicaCollectionOld) {
                if (!pessoajuridicaCollectionNew.contains(pessoajuridicaCollectionOldPessoajuridica)) {
                    pessoajuridicaCollectionOldPessoajuridica.setIdPessoa(null);
                    pessoajuridicaCollectionOldPessoajuridica = em.merge(pessoajuridicaCollectionOldPessoajuridica);
                }
            }
            for (Pessoajuridica pessoajuridicaCollectionNewPessoajuridica : pessoajuridicaCollectionNew) {
                if (!pessoajuridicaCollectionOld.contains(pessoajuridicaCollectionNewPessoajuridica)) {
                    Pessoa oldIdPessoaOfPessoajuridicaCollectionNewPessoajuridica = pessoajuridicaCollectionNewPessoajuridica.getIdPessoa();
                    pessoajuridicaCollectionNewPessoajuridica.setIdPessoa(pessoa);
                    pessoajuridicaCollectionNewPessoajuridica = em.merge(pessoajuridicaCollectionNewPessoajuridica);
                    if (oldIdPessoaOfPessoajuridicaCollectionNewPessoajuridica != null && !oldIdPessoaOfPessoajuridicaCollectionNewPessoajuridica.equals(pessoa)) {
                        oldIdPessoaOfPessoajuridicaCollectionNewPessoajuridica.getPessoajuridicaCollection().remove(pessoajuridicaCollectionNewPessoajuridica);
                        oldIdPessoaOfPessoajuridicaCollectionNewPessoajuridica = em.merge(oldIdPessoaOfPessoajuridicaCollectionNewPessoajuridica);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoa.getIdPessoa();
                if (findPessoa(id) == null) {
                    throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.");
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
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getIdPessoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.", enfe);
            }
            Collection<Pessoafisica> pessoafisicaCollection = pessoa.getPessoafisicaCollection();
            for (Pessoafisica pessoafisicaCollectionPessoafisica : pessoafisicaCollection) {
                pessoafisicaCollectionPessoafisica.setIdPessoa(null);
                pessoafisicaCollectionPessoafisica = em.merge(pessoafisicaCollectionPessoafisica);
            }
            Collection<Movimentos> movimentosCollection = pessoa.getMovimentosCollection();
            for (Movimentos movimentosCollectionMovimentos : movimentosCollection) {
                movimentosCollectionMovimentos.setIdPessoa(null);
                movimentosCollectionMovimentos = em.merge(movimentosCollectionMovimentos);
            }
            Collection<Pessoajuridica> pessoajuridicaCollection = pessoa.getPessoajuridicaCollection();
            for (Pessoajuridica pessoajuridicaCollectionPessoajuridica : pessoajuridicaCollection) {
                pessoajuridicaCollectionPessoajuridica.setIdPessoa(null);
                pessoajuridicaCollectionPessoajuridica = em.merge(pessoajuridicaCollectionPessoajuridica);
            }
            em.remove(pessoa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
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

    public Pessoa findPessoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
