package com.seeyon.v3x.plugin.rating.service;

import java.util.List;

import com.seeyon.v3x.plugin.rating.model.RatingCountDataSet;
import com.seeyon.v3x.plugin.rating.model.RatingItem;
import com.seeyon.v3x.plugin.rating.model.RatingUserResult;
import com.seeyon.v3x.plugin.rating.model.RatingUserWeight;
import com.seeyon.v3x.plugin.rating.service.impl.RatingDao;

public interface RatingService {
	
	List<RatingItem> getRatingItemList();
	
	List<RatingItem> getRatingItemList(long procId);
	
	void persistRatingResultList(List<RatingUserResult> resultList);
	
	public List<RatingUserResult> getRatingResultList();
	
	Object persistRatingItemList(long procId,List<RatingItem> itemList);
	
	Object deleteRatingItem(long procId,String itemName);
	
	public RatingUserWeight saveRatingUserWeight(RatingUserWeight data);
	
	public RatingCountDataSet getRatingCountDataSet(long procId);
	
	public RatingDao getRatingDao();

}
