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
import cadastroserver.model.Movimentos;
import cadastroserver.model.Produtos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author luan_
 */
public class ProdutosJpaController implements Serializable {

    public ProdutosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produtos produtos) {
        if (produtos.getMovimentosCollection() == null) {
            produtos.setMovimentosCollection(new ArrayList<Movimentos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Movimentos> attachedMovimentosCollection = new ArrayList<Movimentos>();
            for (Movimentos movimentosCollectionMovimentosToAttach : produtos.getMovimentosCollection()) {
                movimentosCollectionMovimentosToAttach = em.getReference(movimentosCollectionMovimentosToAttach.getClass(), movimentosCollectionMovimentosToAttach.getIdMovimento());
                attachedMovimentosCollection.add(movimentosCollectionMovimentosToAttach);
            }
            produtos.setMovimentosCollection(attachedMovimentosCollection);
            em.persist(produtos);
            for (Movimentos movimentosCollectionMovimentos : produtos.getMovimentosCollection()) {
                Produtos oldIdProdutoOfMovimentosCollectionMovimentos = movimentosCollectionMovimentos.getIdProduto();
                movimentosCollectionMovimentos.setIdProduto(produtos);
                movimentosCollectionMovimentos = em.merge(movimentosCollectionMovimentos);
                if (oldIdProdutoOfMovimentosCollectionMovimentos != null) {
                    oldIdProdutoOfMovimentosCollectionMovimentos.getMovimentosCollection().remove(movimentosCollectionMovimentos);
                    oldIdProdutoOfMovimentosCollectionMovimentos = em.merge(oldIdProdutoOfMovimentosCollectionMovimentos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produtos produtos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produtos persistentProdutos = em.find(Produtos.class, produtos.getIdProduto());
            Collection<Movimentos> movimentosCollectionOld = persistentProdutos.getMovimentosCollection();
            Collection<Movimentos> movimentosCollectionNew = produtos.getMovimentosCollection();
            Collection<Movimentos> attachedMovimentosCollectionNew = new ArrayList<Movimentos>();
            for (Movimentos movimentosCollectionNewMovimentosToAttach : movimentosCollectionNew) {
                movimentosCollectionNewMovimentosToAttach = em.getReference(movimentosCollectionNewMovimentosToAttach.getClass(), movimentosCollectionNewMovimentosToAttach.getIdMovimento());
                attachedMovimentosCollectionNew.add(movimentosCollectionNewMovimentosToAttach);
            }
            movimentosCollectionNew = attachedMovimentosCollectionNew;
            produtos.setMovimentosCollection(movimentosCollectionNew);
            produtos = em.merge(produtos);
            for (Movimentos movimentosCollectionOldMovimentos : movimentosCollectionOld) {
                if (!movimentosCollectionNew.contains(movimentosCollectionOldMovimentos)) {
                    movimentosCollectionOldMovimentos.setIdProduto(null);
                    movimentosCollectionOldMovimentos = em.merge(movimentosCollectionOldMovimentos);
                }
            }
            for (Movimentos movimentosCollectionNewMovimentos : movimentosCollectionNew) {
                if (!movimentosCollectionOld.contains(movimentosCollectionNewMovimentos)) {
                    Produtos oldIdProdutoOfMovimentosCollectionNewMovimentos = movimentosCollectionNewMovimentos.getIdProduto();
                    movimentosCollectionNewMovimentos.setIdProduto(produtos);
                    movimentosCollectionNewMovimentos = em.merge(movimentosCollectionNewMovimentos);
                    if (oldIdProdutoOfMovimentosCollectionNewMovimentos != null && !oldIdProdutoOfMovimentosCollectionNewMovimentos.equals(produtos)) {
                        oldIdProdutoOfMovimentosCollectionNewMovimentos.getMovimentosCollection().remove(movimentosCollectionNewMovimentos);
                        oldIdProdutoOfMovimentosCollectionNewMovimentos = em.merge(oldIdProdutoOfMovimentosCollectionNewMovimentos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produtos.getIdProduto();
                if (findProdutos(id) == null) {
                    throw new NonexistentEntityException("The produtos with id " + id + " no longer exists.");
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
            Produtos produtos;
            try {
                produtos = em.getReference(Produtos.class, id);
                produtos.getIdProduto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produtos with id " + id + " no longer exists.", enfe);
            }
            Collection<Movimentos> movimentosCollection = produtos.getMovimentosCollection();
            for (Movimentos movimentosCollectionMovimentos : movimentosCollection) {
                movimentosCollectionMovimentos.setIdProduto(null);
                movimentosCollectionMovimentos = em.merge(movimentosCollectionMovimentos);
            }
            em.remove(produtos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produtos> findProdutosEntities() {
        return findProdutosEntities(true, -1, -1);
    }

    public List<Produtos> findProdutosEntities(int maxResults, int firstResult) {
        return findProdutosEntities(false, maxResults, firstResult);
    }

    private List<Produtos> findProdutosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produtos.class));
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

    public Produtos findProdutos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produtos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produtos> rt = cq.from(Produtos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
