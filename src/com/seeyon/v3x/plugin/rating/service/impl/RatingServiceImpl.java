package com.seeyon.v3x.plugin.rating.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.plugin.rating.model.RatingCountDataSet;
import com.seeyon.v3x.plugin.rating.model.RatingItem;
import com.seeyon.v3x.plugin.rating.model.RatingTableCountResult;
import com.seeyon.v3x.plugin.rating.model.RatingUserResult;
import com.seeyon.v3x.plugin.rating.model.RatingUserWeight;
import com.seeyon.v3x.plugin.rating.model.TemplateInfo;
import com.seeyon.v3x.plugin.rating.service.RatingService;
import com.seeyon.v3x.plugin.rating.util.UIUtils;

public class RatingServiceImpl implements RatingService {

	public static final boolean DEBUG = true;
	private RatingDao dao = new RatingDao();

	@Override
	public List<RatingItem> getRatingItemList() {
		// TODO Auto-generated method stub
		String sql = "select * from " + RatingDao.RATING_ITEM_TABLE_NAME;
		return dao.fetchDataList(RatingItem.class, sql);
	}

	@Override
	public List<RatingItem> getRatingItemList(long procId) {
		// TODO Auto-generated method stub
		String sql = "select * from " + RatingDao.RATING_ITEM_TABLE_NAME
				+ " where procId=" + procId;
		return dao.fetchDataList(RatingItem.class, sql);
	}

	@Override
	public void persistRatingResultList(List<RatingUserResult> resultList) {
		// TODO Auto-generated method stub
		if (DEBUG) {
			Session session = dao.getSessionSelf();
			Transaction trans = session.beginTransaction();
			try {

				trans.begin();
				String name = null;
				Integer weight = null;
				for (RatingUserResult result : resultList) {
					if (name == null) {
						name = String.valueOf(session
								.createSQLQuery(
										" select name from v3x_org_member where id="
												+ result.getUserId()).list()
								.get(0));
						String sql = "select wei.weight from "
								+ this.getRatingDao().RATING_USER_WEIGHT_TABLE_NAME
								+ " wei left join v3x_affair aff on aff.templete_id = wei.procId and aff.id="
								+ result.getInstanceId();
						List lsit = session.createSQLQuery(sql).list();
						if (lsit == null || lsit.isEmpty()) {
							weight = 50;
						} else {
							try {
								weight = Integer.parseInt(String.valueOf(lsit
										.get(0)));
							} catch (Exception e) {
								weight = 50;
							}
						}
					}
					result.setWeight(weight);
					result.setUserName(name);
					session.saveOrUpdate(result);
				}
				trans.commit();
			} catch (Exception e) {
				trans.rollback();
				e.printStackTrace();
			} finally {
				dao.closeSessionIfNewOpen(session);
			}
		} else {
			dao.savePatchAll(resultList);
		}
	}

	// 短暂时间的缓存表数据 缓存5秒钟
	private static final Map<Long, List<RatingUserResult>> INST_RET_CACHE = new HashMap<Long, List<RatingUserResult>>();

	@Override
	public List<RatingUserResult> getRatingResultList() {
		// TODO Auto-generated method stub
		List<RatingUserResult> retList = new ArrayList<RatingUserResult>();
		if (INST_RET_CACHE.size() > 0) {
			retList = INST_RET_CACHE.values().iterator().next();
		} else {
			String sql = "select * from "
					+ RatingDao.RATING_USER_RESULT_TABLE_NAME;
			retList = dao.fetchDataList(RatingUserResult.class, sql);
			INST_RET_CACHE.put(new Date().getTime(), retList);
			// 5秒后清除数据
			new Thread() {
				public void run() {

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					INST_RET_CACHE.clear();

				}
			}.start();
		}
		return retList;
	}

	@Override
	public Object persistRatingItemList(long procId, List<RatingItem> itemList) {
		// TODO Auto-generated method stub
		return dao.saveRatingItem(procId, itemList);
	}

	@Override
	public Object deleteRatingItem(long procId, String itemName) {
		// TODO Auto-generated method stub
		return dao.deleteRatingItem(procId, itemName);
	}


	private static Map<String, Object> COMMON_CACHE = new HashMap<String, Object>();

	public Map<String, TemplateInfo> getTemplateInfoList() {

		Map<String, TemplateInfo> retMap = new HashMap<String, TemplateInfo>();

		String sql = "select f.templete_id ,f.object_id,tpl.subject,f.node_policy from v3x_affair f left join v3x_templete tpl on f.templete_id=tpl.id where  f.id in(select distinct instanceId from rat_ratinguserresult) and f.templete_id is not null";
		final String key = sql;
		Object dd = COMMON_CACHE.get(sql);
		if (dd != null) {
			return (Map<String, TemplateInfo>) dd;
		}
		Session session = dao.getSessionSelf();
		try {
			List<Object[]> list = session.createSQLQuery(sql).list();
			// Map<Long,Long> map = new HashMap<Long,Long>();
			if (list.isEmpty()) {
				return retMap;
			}
			StringBuilder inStatement = new StringBuilder("(");
			int tag = 1, len = list.size();
			for (Object[] obj : list) {
				if (!"打分".equals("" + obj[3])) {
					continue;
				}
				// map.put(Long.parseLong(""+obj[1]),
				// Long.parseLong(""+obj[0]));
				TemplateInfo info = new TemplateInfo();

				retMap.put("" + obj[0], info);
				info.setTemplateName("" + obj[2]);
				info.setTemplateId("" + obj[0]);
				if (tag < len) {
					inStatement.append(Long.parseLong("" + obj[0])).append(",");
				} else {
					inStatement.append(Long.parseLong("" + obj[0]));
				}
				tag++;
			}
			inStatement.append(")");
			sql = "select distinct c.templete_id from col_summary c where c.templete_id in "
					+ inStatement;
			List<Object> list1 = session.createSQLQuery(sql).list();

			for (Object obj : list1) {
				TemplateInfo info = retMap.get("" + obj);
				if (info != null) {
					info.setType(1);
				}

			}
			sql = "select distinct e.templete_id from edoc_summary e where e.templete_id in "
					+ inStatement;
			List<Object> list2 = session.createSQLQuery(sql).list();

			for (Object obj : list2) {
				TemplateInfo info = retMap.get("" + obj);
				if (info != null) {
					info.setType(2);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			dao.closeSessionIfNewOpen(session);
		}
		COMMON_CACHE.put(key, retMap);

		new Thread() {
			public void run() {
				try {
					System.out.println("---------cache------------");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				COMMON_CACHE.remove(key);

			}
		}.start();
		return retMap;
	}
	
	public List<RatingTableCountResult> getRatingTableCountResult(
			long templateId, int month, int type) {

		List<RatingTableCountResult> retList = new ArrayList<RatingTableCountResult>();
		Map<Long, List<RatingUserResult>>objResult =  getProcessResultList(templateId,month,type);
			// ok 计算分数
			for (Map.Entry<Long, List<RatingUserResult>> entry : objResult.entrySet()) {
				RatingTableCountResult rdt = new RatingTableCountResult();
				rdt.setInstanceId(entry.getKey() + "");
				List<RatingUserResult> valList = entry.getValue();
				double total = 0d;
				double weight = 0d;
				for (RatingUserResult rst : valList) {
					rdt.setInstanceName(rst.getInstanceName());
					total += rst.getScore()*rst.getWeight() ;
					weight += rst.getWeight();
				}
				rdt.setScore(NUMBER_FORMAT.format(total/weight));
				retList.add(rdt);
			}

		
		return retList;

	}

	public Map<Long, List<RatingUserResult>> getProcessResultList(long templateId,
			int month, int type) {
		Map<Long, List<RatingUserResult>> objResult = new HashMap<Long, List<RatingUserResult>>();
		String key = "" + templateId + "_" + month + "_" + type;
		Session session = dao.getSessionSelf();
		try {
			Date[] dates = UIUtils.flatDatePairByMonth(month);
			// id 是模版，根据type进行选择
			Map<String, TemplateInfo> infoMap = this.getTemplateInfoList();
			TemplateInfo info = infoMap.get("" + templateId);
			String sql = "select id,object_id from v3x_affair f where templete_id="
					+ templateId;

			List<Object[]> idList = session.createSQLQuery(sql).list();
			Map<Long, Long> instance2porc = new HashMap<Long, Long>();
			for (Object[] ids : idList) {
				instance2porc.put(Long.parseLong(String.valueOf(ids[0])),
						Long.parseLong(String.valueOf(ids[1])));
			}
			List<RatingUserResult> result = session
					.createCriteria(RatingUserResult.class)
					.add(Restrictions.between("time", dates[0], dates[1]))
					.add(Restrictions.in("instanceId", instance2porc.keySet()))
					.list();
			for (RatingUserResult rst : result) {
				// step1 找出属于哪一个流程
				Long objId = instance2porc.get(rst.getInstanceId());
				List<RatingUserResult> list = objResult.get(objId);
				if (list == null) {
					list = new ArrayList<RatingUserResult>();
					objResult.put(objId, list);
				}
				list.add(rst);
			}
			return objResult;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.closeSessionIfNewOpen(session);
		}
		return objResult;
	}
	public RatingCountDataSet getRatingCountDataSet(long procId) {

		String sql = "select * from " + RatingDao.RATING_USER_RESULT_TABLE_NAME
				+ "  where procId=" + procId;
		List<RatingUserResult> retList = dao.fetchDataList(
				RatingUserResult.class, sql);

		RatingCountDataSet dataSet = new RatingCountDataSet();

		Map<Long, List<RatingUserResult>> instRetMap = new HashMap<Long, List<RatingUserResult>>();
		Map<Long, String> mapString = new HashMap<Long, String>();
		// step1 先按照实例分出来
		for (RatingUserResult ret : retList) {
			long instId = ret.getInstanceId();
			List<RatingUserResult> ilist = instRetMap.get(instId);
			if (ilist == null) {
				ilist = new ArrayList<RatingUserResult>();
				instRetMap.put(instId, ilist);
			}
			mapString.put(instId, ret.getInstanceName());
			ilist.add(ret);
		}
		// 每个实例的分数
		Map<Long, Double> retScoreMap = new HashMap<Long, Double>();
		// 计算每一个流程的情况
		for (Map.Entry<Long, List<RatingUserResult>> entry : instRetMap
				.entrySet()) {

			List<RatingUserResult> userRetList = entry.getValue();
			// step2 先计算出每个人的分数是多少
			Map<Long, List<RatingUserResult>> scoreMap = new HashMap<Long, List<RatingUserResult>>();
			// 权重表
			Map<Long, Integer> userweight = new HashMap<Long, Integer>();

			for (RatingUserResult ret : userRetList) {
				long userID = ret.getUserId();
				List<RatingUserResult> ilist = scoreMap.get(userID);
				if (ilist == null) {
					ilist = new ArrayList<RatingUserResult>();
					scoreMap.put(userID, ilist);
				}
				ilist.add(ret);
				// override again and again
				userweight.put(userID,
						ret.getWeight() == 0 ? 50 : ret.getWeight());
			}
			Map<Long, Double> userScoreMap = new HashMap<Long, Double>();
			for (Map.Entry<Long, List<RatingUserResult>> entry2 : scoreMap
					.entrySet()) {

				double total = 0;
				int count = 0;
				for (RatingUserResult ret : entry2.getValue()) {

					total += ret.getScore();
					count++;
				}

				userScoreMap.put(entry2.getKey(), total / count);
			}
			double totalScore = 0d;
			int count = 0;
			for (Map.Entry<Long, Double> entry3 : userScoreMap.entrySet()) {

				Integer weight = userweight.get(entry3.getKey());
				if (weight == null) {
					weight = 50;
				}
				count += weight;
			}
			for (Map.Entry<Long, Double> entry3 : userScoreMap.entrySet()) {

				Integer weight = userweight.get(entry3.getKey());
				if (weight == null) {
					weight = 50;
				}
				totalScore += (weight * entry3.getValue() / count);

			}

			// 流程实例为ID,后边是分数
			retScoreMap.put(entry.getKey(), totalScore);
			// 组装数据
			// 1.pie chart
			// RatingPieChartDataSet pie = new RatingPieChartDataSet();
			for (Map.Entry<Long, Double> entry4 : retScoreMap.entrySet()) {
				String score = NUMBER_FORMAT.format(entry4.getValue());
				List<Object> list = dataSet.getPieChartDataSet().get(score);
				if (list == null) {
					list = new ArrayList<Object>();
					dataSet.getPieChartDataSet().put(score, list);
				}
				list.add(entry4.getKey());

			}
			dataSet.getBarChartDataSet().put(entry.getKey(), entry.getValue());

		}
		Map<String, List<Object>> map = dataSet.getPieChartDataSet();
		List<RatingTableCountResult> tableDataSet = new ArrayList<RatingTableCountResult>();
		for (Map.Entry<String, List<Object>> entry : map.entrySet()) {

			for (Object obj : entry.getValue()) {
				RatingTableCountResult result = new RatingTableCountResult();
				result.setScore(entry.getKey());
				result.setInstanceId(String.valueOf(obj));
				String name = mapString.get(obj);
				result.setInstanceName(name);
				tableDataSet.add(result);
			}

		}
		dataSet.setTableDataSet(tableDataSet);
		return dataSet;

	}

	private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.#");

	@Override
	public RatingUserWeight saveRatingUserWeight(RatingUserWeight data) {
		Session session = dao.getSessionSelf();
		Transaction trans = session.beginTransaction();
		trans.begin();
		try {
			session.saveOrUpdate(data);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		} finally {
			dao.closeSessionIfNewOpen(session);
		}
		return data;
	}

	public static void main(String[] args) {

		NumberFormat number = new DecimalFormat("0.#");
		double f = 123.43232325;
		System.out.println(number.format(f));
	}

	@Override
	public RatingDao getRatingDao() {
		// TODO Auto-generated method stub
		return dao;
	}

}
