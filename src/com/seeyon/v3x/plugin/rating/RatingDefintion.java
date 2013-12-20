package com.seeyon.v3x.plugin.rating;

import com.seeyon.v3x.plugin.PluginDefintion;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class RatingDefintion extends PluginDefintion{
	
	
	public RatingDefintion()
    {
    }

    public String getId()
    {
        return "ratingPlugin";
    }

    public boolean isAllowStartup(ServletContext arg0)
    {
        log.info("ratingPlugin is loaded!");
        return true;
    }

    private static Log log = LogFactory.getLog(RatingDefintion.class);


}
