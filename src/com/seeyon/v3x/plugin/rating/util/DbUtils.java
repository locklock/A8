package com.seeyon.v3x.plugin.rating.util;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.plugin.rating.service.impl.RatingDao;
public class DbUtils {
	   
	    public static void main(String[] args) {  
	          
	       // System.out.println(getBeanFilesList("com.seeyon.v3x.plugin.rating.model.RatingItem"));  
	        System.out.println(genCreateTableSql("com.seeyon.v3x.plugin.rating.model.RatingItem"));  
	        System.out.println(genCreateTableSql("com.seeyon.v3x.plugin.rating.model.RatingUserResult"));
	        System.out.println(genCreateTableSql("com.seeyon.v3x.plugin.rating.model.RatingUserWeight"));
	        //System.out.println(genInsertSql("com.seeyon.v3x.plugin.rating.model.RatingItem"));  
	    }  
	      
	    
	    
	    
	    public static String getBeanName(String bean){  
	        try {  
	            Class clz = Class.forName(bean);  
	            String clzStr = clz.toString();  
	            //得到类名  
	            String beanName = clzStr.substring(clzStr.lastIndexOf(".")+1).toLowerCase();  
	            return beanName;  
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	            return "";  
	        }  
	    }  
	      
	    public static List<String> getBeanPropertyList(String bean){  
	        try {  
	            Class clz = Class.forName(bean);  
	            Field[] strs = clz.getDeclaredFields();  
	            List<String> propertyList = new ArrayList<String>();  
	            for (int i = 0; i < strs.length; i++) {  
	                String protype = strs[i].getType().toString();  
	                propertyList.add(protype.substring(protype.lastIndexOf(".")+1)+"`"+strs[i].getName());  
	            }  
	            return propertyList;  
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	            return null;  
	        }  
	    }  
	      
	    public static String getBeanFilesList(String bean){  
	        try {  
	            Class clz = Class.forName(bean);  
	            Field[] strs = clz.getDeclaredFields();  
	            StringBuffer sb = new StringBuffer();  
	            for (int i = 0; i < strs.length; i++) {  
	                String protype = strs[i].getType().toString();  
	                if (!strs[i].getName().equals("tableName")&&!strs[i].getType().equals("List")) {  
	                   sb.append(strs[i].getName()+",");  
	                }  
	            }  
	            sb.deleteCharAt(sb.toString().lastIndexOf(","));  
	            return sb.toString();  
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	            return null;  
	        }  
	    }  
	      
	    /** 
	     * 生成建表語句 
	     * @param bean 
	     * @return 
	     */  
	    public static String genCreateTableSql(String bean){  
	        List<String> beanPropertyList =  getBeanPropertyList(bean);  
	        StringBuffer sb = new StringBuffer("create table "+RatingDao.TAB_PREFIX+getBeanName(bean)+"(\n");  
	        for (String string : beanPropertyList) {  
	            String[] propertys = string.split("`");  
	            if (!propertys[1].equals("tableName")&&!propertys[1].equals("param")&&!propertys[0].equals("List")) {  
	                if (propertys[1].equals("id")) {  
	                    sb.append("   id bigint primary key auto_increment,\n");  
	                } else {  
	                    if (propertys[0].equals("int")) {  
	                        sb.append("   " + propertys[1] + " int default 0 comment '',\n");  
	                    }else if(propertys[0].equals("long")){ sb.append("   " + propertys[1] + " bigint default 0 comment '',\n");  } else if (propertys[0].equals("String")) {  
	                        sb.append("   " + propertys[1] + " varchar(200) default '' comment '',\n");  
	                    } else if (propertys[0].equals("double")) {  
	                        sb.append("   " + propertys[1] + " double(10,2) default 0.0 comment '',\n");  
	                    } else if (propertys[0].equals("Date")) {  
	                        sb.append("   " + propertys[1] + " datetime comment '',\n");  
	                    }  
	                }  
	            }  
	        }  
	        sb.append(")");  
	        sb.deleteCharAt(sb.lastIndexOf(","));  
	        return sb.toString();  
	    }  
	      
	    /** 
	     * 生成查询语句 
	     * @param bean 
	     * @return 
	     */  
	    public static String genSelectAllSql(String bean){  
	        String filesList =  getBeanFilesList(bean);  
	        return "select \n "+filesList+" \n from \n wnk_pdt_"+getBeanName(bean)+"";  
	    }  
	      
	    /** 
	     * 生成插入语句 
	     * @param bean 
	     * @return 
	     */  
	    public static String genInsertSql(String bean){  
	        String filesList =  getBeanFilesList(bean);  
	        int fl = getLength(filesList);  
	        String wenhao = "";  
	        for (int i = 0; i < fl; i++) {  
	            if(i==fl-1){  
	                wenhao = wenhao+"?";  
	            }else{  
	                wenhao = wenhao+"?,";  
	            }  
	        }  
	        return "insert into "+RatingDao.TAB_PREFIX+getBeanName(bean)+"("+filesList+") values("+wenhao+")";  
	    }  
	    
	    private static int getLength(String str){
	    	
	    	String[] args = str.split(",");
	    	int len =0;
	    	for(String s:args){
	    		
	    		len+=s.length();
	    	}
	    	return len;
	    	
	    }
}
