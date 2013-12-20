package com.seeyon.v3x.plugin.rating.dao;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.plugin.rating.model.RatingItem;

public class RatingItemDao extends BaseHibernateDao<RatingItem>{
	
	public static boolean isInit = false;
	
	public static SessionFactory FACTORY;
	
	public RatingItemDao(){
		try{
		synchronized(RatingItemDao.class){
			if(!isInit){
				isInit = true;
				init();
				
			}
		}
		}catch(Exception e){
			isInit = false;
			e.printStackTrace();
		}
		
	}
	public static final String TAB_PREFIX = "rat_";
	public static final String RATING_ITEM_TABLE_NAME =TAB_PREFIX+ "RatingItem";
	public static final String RATING_USER_RESULT_TABLE_NAME =TAB_PREFIX+ "RatingUserResult";
	public static final String RATING_USER_WEIGHT_TABLE_NAME =TAB_PREFIX+ "RatingUserWeight";
	public List<RatingItem> findAll(){
		SQLQuery query = this.getSessionSelf().createSQLQuery("select {item.*} from "+RATING_ITEM_TABLE_NAME+" item");
		query.addEntity("item",this.getEntityClass());
		return query.list();
	}
	
	public Long saveRatingItem(RatingItem item){
		 this.save(item);
		return item.getId();
	}
	
	public void deleteRatingItem(long id){
	
		this.delete(id);
		
	}
	public static Map<Session,Long> sessionMap = new HashMap<Session,Long>();
	public Session getSessionSelf(){
		
		Session session  = null;
		try{
			session = this.getSession();
		}catch(Exception e){
			//e.printStackTrace();
		}
		if(session == null){
			session =  FACTORY.openSession();
			sessionMap.put(session,System.currentTimeMillis());
		}
		
		return session;
		
	}
	public void closeSessionIfNewOpen(Session session){
		if(session!=null&&session.isOpen()){
			Long sessionId = sessionMap.get(session);
			if(sessionId!=null){
				try{
					if(session.isOpen()){
						session.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					sessionMap.remove(session);
				}
			}
		}
		
	}
	public static void init(){
		
		Configuration cfg=new Configuration();

	    try {
	    	URL url = RatingItemDao.class.getResource("self.xml");
	    	FACTORY = cfg.configure(url).addURL(RatingItemDao.class.getResource("rating.hbm.xml")).addURL(RatingItemDao.class.getResource("templete.hbm.xml")).buildSessionFactory();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}
	
	
	

}
