package com.seeyon.v3x.plugin.rating.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.seeyon.v3x.plugin.rating.dao.RatingItemDao;
import com.seeyon.v3x.plugin.rating.model.RatingItem;

public class RatingDao extends RatingItemDao {

	/**
	 * 通用的查找数据
	 * 
	 * @param t
	 * @param sql
	 * @return
	 */
	public <T> List<T> fetchDataList(Class<T> t, Object sql) {
		List<T> list = new ArrayList<T>();
		Session session = this.getSessionSelf();
		try {
			list = session.createSQLQuery(String.valueOf(sql)).addEntity(t)
					.list();
		} finally {
			closeSessionIfNewOpen(session);
		}
		return list;
	}

	/**
	 * 构造假数据
	 * 
	 * @param t
	 * @param sql
	 * @return
	 */
	private <T> List<T> mock(Class<T> t, Object sql) {
		if (t == RatingItem.class) {

			return (List<T>) dataMap.get(Long.parseLong(String.valueOf(sql)));

		}
		return new ArrayList<T>();
	}

	public List<RatingItem> saveRatingItem(Long procId, List<RatingItem> retList) {
		// 所有流程都一样 所以procID暂时没有用
		Session session = this.getSessionSelf();
		Transaction trans = session.beginTransaction();
		try {
			trans.begin();
			
			for (RatingItem item : retList) {
				session.save(item);
			}
			session.flush();
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		} finally {
			this.closeSessionIfNewOpen(session);
		}

		// this.savePatchAll(retList);
		return retList;

	}

	public List<RatingItem> deleteRatingItem(long procId, String itemName) {
		// 删除一个名字为xxx的配置
		String sql = "select * from "+this.RATING_ITEM_TABLE_NAME+" where itemName = '"
				+ itemName + "'";
		List<RatingItem> ratingItemList = this.fetchDataList(RatingItem.class,
				sql);
		if (ratingItemList == null || ratingItemList.isEmpty()) {
			return new ArrayList<RatingItem>();
		}
		Session session = this.getSessionSelf();
		Transaction trans = session.beginTransaction();
		try {
			trans.begin();
			for (RatingItem item : ratingItemList) {
				session.delete(item);
			}
			trans.commit();
		} catch (Exception e) {
			trans.rollback();
			e.printStackTrace();
		}finally{
			this.closeSessionIfNewOpen(session);
		}
		return ratingItemList;

	}

	private static final Map<Long, List<RatingItem>> dataMap = new HashMap<Long, List<RatingItem>>();

}
