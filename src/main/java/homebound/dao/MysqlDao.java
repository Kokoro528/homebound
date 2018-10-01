package homebound.dao;

import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.*;

import org.hibernate.query.Query;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import homebound.entity.Item;
import homebound.entity.Item.ItemBuilder;
import homebound.hibernate.CategoryId;
import homebound.hibernate.*;


@Repository
public class MysqlDao extends AbstractDaoImplementaion {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public void saveItem(Item item) {
		Session session = null;
		PersistentItem persistentItem = convert(item);

		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.save(persistentItem);
			session.getTransaction().commit();
		} catch (HibernateException ex) {
			if (session != null && session.getTransaction() != null) {
				session.getTransaction().rollback();
			}

			if (ex instanceof ConstraintViolationException) {
				System.out.println("item id " + item.getItemId() + " already exists");
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		System.out.println("insert into db successfully");
	}
	
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {

		Session session = null;

		for (String itemId : itemIds) {
			PersistentHistory persistentHistory = new PersistentHistory();
			persistentHistory.setHistoryId(new HistoryId(itemId, userId));
			try {
				session = sessionFactory.openSession();
				session.beginTransaction();
				session.save(persistentHistory);
				System.out.println("saving favorited");
				session.getTransaction().commit();
			} catch (Exception e) {
				if (session != null && session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				System.out.println("error insert history,  cause = " + e.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {

		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			for (String itemId : itemIds) {
				PersistentHistory persistentHistory = new PersistentHistory();
				persistentHistory.setHistoryId(new HistoryId(itemId, userId));
				session.delete(persistentHistory);
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}


	@Override
	public Set<String> getCategories(String itemId) {

		Set<String> categories = new HashSet<>();
		Session session = null;

		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			
			CriteriaBuilder builder = session.getCriteriaBuilder();
			
			CriteriaQuery<PersistentItem> criteriaQuery = builder.createQuery(PersistentItem.class);
			Root<PersistentItem> piroot = criteriaQuery.from(PersistentItem.class);
			criteriaQuery.select(piroot).where(builder.equal(piroot.get("itemId"), itemId));
			PersistentItem pi = session.createQuery(criteriaQuery).uniqueResult();
			
			CriteriaQuery<PersistentCategories> cq1 = builder.createQuery(PersistentCategories.class);
			Root<PersistentCategories> pcroot= cq1.from(PersistentCategories.class);
			cq1.select(pcroot).where(builder.equal(pcroot.get("persistentItem"), pi));
			List<PersistentCategories> categoriesList = session.createQuery(cq1).getResultList();

			

//			PersistentItem persistentItem = (PersistentItem) session.createCriteria(PersistentItem.class)
//					.add(Restrictions.eq("itemId", itemId)).uniqueResult();
//
//			List<PersistentCategories> categoriesList = session.createCriteria(PersistentCategories.class)
//					.add(Restrictions.eq("persistentItem", persistentItem)).list();
//
			for (PersistentCategories category : categoriesList) {
				categories.add(category.getCategory());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return categories;
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {

		Set<String> favoriteItems = new HashSet<>();

		Session session = null;

		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			
			CriteriaBuilder builder = session.getCriteriaBuilder();
			
			CriteriaQuery<PersistentHistory> criteriaQuery = builder.createQuery(PersistentHistory.class);
//			Root<HistoryId> hid = criteriaQuery.from(HistoryId.class);
//			criteriaQuery.select(hid);
//			criteriaQuery.where(builder.equal(hid.get("userId"), userId));
//			List<HistoryId> persistentHistories = session.createQuery(criteriaQuery).getResultList();
			
			Root<PersistentHistory> phistoryroot = criteriaQuery.from(PersistentHistory.class);
//			Root<PersistentCategories> pcroot= criteriaQuery.from(PersistentCategories.class);
			criteriaQuery.select(phistoryroot);
			criteriaQuery.where(builder.equal(phistoryroot.get("historyId").get("userId"), userId));
			
			Query<PersistentHistory> query=session.createQuery(criteriaQuery);
	         List<PersistentHistory> persistentHistories=query.getResultList();
//	         System.out.println("persistentHistories length: " + persistentHistories.size());
//			
//			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//
//			CriteriaQuery<PersonWrapper> criteria = builder.createQuery( PersonWrapper.class );
//			Root<Person> root = criteria.from( Person.class );
//
//			Path<Long> idPath = root.get( Person_.id );
//			Path<String> nickNamePath = root.get( Person_.nickName);
//
//			criteria.select( builder.construct( PersonWrapper.class, idPath, nickNamePath ) );
//			criteria.where( builder.equal( root.get( Person_.name ), "John Doe" ) );
//
//			List<PersonWrapper> wrappers = entityManager.createQuery( criteria ).getResultList();
	 
			
//			List<PersistentHistory> persistentHistories = session.createCriteria(PersistentHistory.class)
//					.add(Restrictions.eq("historyId.userId", userId)).list();
			

			for (PersistentHistory history : persistentHistories) {
				System.out.println("hahaha");
				favoriteItems.add(history.getHistoryId().getItemId());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return favoriteItems;
	}

	public Set<Item> getFavoriteItems(String userId) {

		System.out.println("enter getFavoriteItems ID");
		Set<String> itemIds = getFavoriteItemIds(userId);
		Set<Item> favoriteItems = new HashSet<>();

		Session session = null;
		try {

			session = sessionFactory.openSession();
			session.beginTransaction();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			for (String itemId : itemIds) {
				CriteriaQuery<PersistentItem> cq = builder.createQuery(PersistentItem.class);
				Root<PersistentItem> piroot = cq.from(PersistentItem.class);
				cq.select(piroot);
				cq.where(builder.equal(piroot.get("itemId"), itemId));
				PersistentItem persistentItem = session.createQuery(cq).getSingleResult();
//				PersistentItem persistentItem = (PersistentItem) session.createCriteria(PersistentItem.class)
//						.add(Restrictions.eq("itemId", itemId)).uniqueResult();
				if (persistentItem != null) {
					favoriteItems.add(convert(persistentItem));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return favoriteItems;
	}
	

	private Item convert(PersistentItem persistentItem) {
		ItemBuilder builder = new ItemBuilder();
		builder.setItemId(persistentItem.getItemId());
		builder.setName(persistentItem.getName());
		builder.setAddress(persistentItem.getAddress());
		builder.setDistance(persistentItem.getDistance());
		builder.setImageUrl(persistentItem.getImageUrl());
		builder.setUrl(persistentItem.getUrl());
		builder.setRating(persistentItem.getRating());
		Set<String> categories = new HashSet<>();

		for (PersistentCategories category : persistentItem.getCategories()) {
			categories.add(category.getCategory());
		}

		builder.setCategories(categories);
		return builder.build();
	}

	private PersistentItem convert(Item item) {
		PersistentItem persistentItem = new PersistentItem();
		persistentItem.setItemId(item.getItemId());
		persistentItem.setName(item.getName());
		persistentItem.setRating(item.getRating());
		persistentItem.setAddress(item.getAddress());
		persistentItem.setImageUrl(item.getImageUrl());
		persistentItem.setUrl(item.getUrl());
		persistentItem.setDistance(item.getDistance());
		persistentItem.setCategories(new HashSet<PersistentCategories>());

		for (String category : item.getCategories()) {
			PersistentCategories persistentCategories = new PersistentCategories();
			persistentCategories.setCategoryId(new CategoryId(item.getItemId(), category));
			persistentCategories.setCategory(category);
			persistentCategories.setPersistentItem(persistentItem);
			persistentItem.getCategories().add(persistentCategories);
		}

		return persistentItem;
	}

}
